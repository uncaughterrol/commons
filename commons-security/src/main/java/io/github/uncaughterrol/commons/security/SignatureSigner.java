package io.github.uncaughterrol.commons.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for signing and verifying request payloads using HMAC-SHA256.
 *
 * <p>Produces a canonical string from the payload by sorting keys lexicographically,
 * then signs it with the provided secret via HMAC-SHA256, returning a Base64-encoded signature.
 *
 * <p><b>Thread safety:</b> All methods are thread-safe. {@link Mac} instances are pooled
 * per-thread via {@link ThreadLocal} to avoid the cost of repeated JCA lookups.
 *
 * <p><b>Performance notes:</b>
 * <ul>
 *   <li>{@link Mac} instances are reused per thread — avoids expensive {@code Mac.getInstance()}
 *       on every call.</li>
 *   <li>{@link SecretKeySpec} objects are cached by secret string — avoids redundant allocations
 *       when the same secret is reused across calls.</li>
 * </ul>
 */
public final class SignatureSigner {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Per-thread {@link Mac} pool. Eliminates repeated algorithm lookups through the JCA provider
     * chain, which are expensive under high concurrency.
     */
    private static final ThreadLocal<Mac> MAC_POOL = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(HMAC_SHA256);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Cache of {@link SecretKeySpec} instances keyed by their raw secret string.
     *
     * <p>Bounded to {@value #MAX_CACHED_KEYS} entries to prevent unbounded growth when secrets
     * are dynamic or user-supplied. Eviction is coarse (full clear) — replace with an LRU
     * structure if retention of hot keys matters.
     */
    private static final ConcurrentHashMap<String, SecretKeySpec> KEY_CACHE = new ConcurrentHashMap<>();

    private static final int MAX_CACHED_KEYS = 256;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SignatureSigner() {
    }

    /**
     * Returns a cached {@link SecretKeySpec} for the given secret, creating one if absent.
     *
     * @param secret the HMAC secret key material
     * @return a {@link SecretKeySpec} initialized with the secret bytes
     */
    private static SecretKeySpec getKey(String secret) {
        if (KEY_CACHE.size() >= MAX_CACHED_KEYS) KEY_CACHE.clear();
        return KEY_CACHE.computeIfAbsent(
                secret,
                s -> new SecretKeySpec(s.getBytes(StandardCharsets.UTF_8), HMAC_SHA256)
        );
    }

    /**
     * Signs a payload map using HMAC-SHA256 and returns a Base64-encoded signature.
     *
     * <p>The canonical string is constructed by sorting all payload entries by key
     * lexicographically, then concatenating them as {@code key=value&} pairs. For example:
     * <pre>{@code
     *   { "b" -> "2", "a" -> "1" }  =>  "a=1&b=2&"
     * }</pre>
     *
     * @param payload the key-value pairs to sign; must not be {@code null} or empty
     * @param secret  the HMAC secret; must not be {@code null}
     * @return a Base64-encoded HMAC-SHA256 signature of the canonical payload string
     * @throws RuntimeException if the signing operation fails unexpectedly
     */
    public static String sign(Map<String, String> payload, String secret) {
        try {
            Mac mac = MAC_POOL.get();
            mac.init(getKey(secret));

            StringBuilder sb = new StringBuilder(payload.size() * 32);
            new TreeMap<>(payload).forEach((k, v) ->
                    sb.append(k).append('=').append(v).append('&')
            );

            byte[] raw = mac.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(raw);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies that a payload matches the given signature using HMAC-SHA256.
     *
     * <p>Comparison is performed in <b>constant time</b> via {@link MessageDigest#isEqual} to
     * prevent timing side-channel attacks, where a naive {@code String.equals()} could leak
     * signature bytes by short-circuiting on the first mismatched character.
     *
     * @param payload   the key-value pairs that were originally signed
     * @param signature the Base64-encoded signature to verify against
     * @param secret    the HMAC secret used during signing
     * @return {@code true} if the recomputed signature matches {@code signature}; {@code false}
     * otherwise
     * @throws RuntimeException if the signing operation fails unexpectedly
     */
    public static boolean verify(Map<String, String> payload, String signature, String secret) {
        byte[] expected = Base64.getDecoder().decode(sign(payload, secret));
        byte[] actual = Base64.getDecoder().decode(signature);
        return MessageDigest.isEqual(expected, actual);
    }
}