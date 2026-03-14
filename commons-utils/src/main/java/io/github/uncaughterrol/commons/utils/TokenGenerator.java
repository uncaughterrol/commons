package io.github.uncaughterrol.commons.utils;

import java.security.SecureRandom;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating random tokens and composite keys.
 *
 * <p>Exposes two variants for all token methods:</p>
 * <ul>
 *   <li><b>Secure</b> — backed by {@link SecureRandom}; cryptographically strong.
 *       Use for OTPs, session tokens, password reset links, or any
 *       security-sensitive context.</li>
 *   <li><b>Fast</b> — backed by {@link ThreadLocalRandom}; statistically random
 *       but not cryptographically secure. Use for test data, display codes,
 *       idempotency keys, or any non-security context.</li>
 * </ul>
 *
 * <p>This class is not instantiable.</p>
 */
public final class TokenGenerator {

    private TokenGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // -------------------------------------------------------------------------
    // RNG Strategy
    // -------------------------------------------------------------------------

    /**
     * Internal abstraction over a random integer source.
     *
     * <p>Allows {@link #randomString} to remain agnostic of the underlying RNG,
     * making it trivial to swap between secure and fast implementations.</p>
     */
    @FunctionalInterface
    private interface RandomSource {
        /**
         * Returns a random {@code int} in the range {@code [0, bound)}.
         *
         * @param bound the exclusive upper bound; must be positive
         * @return a non-negative random int less than {@code bound}
         */
        int nextInt(int bound);
    }

    /**
     * Cryptographically strong RNG, shared across all threads.
     *
     * <p>{@link SecureRandom} is thread-safe and seeded from OS entropy.
     * A single instance is reused to avoid repeated, expensive seeding.</p>
     */
    private static final SecureRandom SECURE_RNG = new SecureRandom();

    /**
     * Fast statistical RNG delegating to {@link ThreadLocalRandom}.
     *
     * <p>Not cryptographically secure, but contention-free and significantly
     * faster under concurrent load. Each thread maintains its own instance.</p>
     */
    private static final RandomSource FAST_RNG = bound -> ThreadLocalRandom.current().nextInt(bound);

    // -------------------------------------------------------------------------
    // Alphabets
    // -------------------------------------------------------------------------

    /**
     * Alphabet used for purely alphabetic tokens (A–Z, a–z).
     */
    private static final char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Base-62 alphabet used for alphanumeric tokens (0–9, A–Z, a–z).
     */
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Alphabet used for numeric tokens (0–9).
     */
    private static final char[] NUMERIC = "0123456789".toCharArray();

    // -------------------------------------------------------------------------
    // Secure tokens  (use for OTPs, session IDs, password resets, etc.)
    // -------------------------------------------------------------------------

    /**
     * Generates a cryptographically secure alphanumeric token of the default length (6).
     *
     * <p>Example:</p>
     * <pre>
     * secureAlphanumericToken() → "aB3kQ7"
     * </pre>
     *
     * @return a 6-character secure alphanumeric token
     */
    public static String secureAlphanumericToken() {
        return secureAlphanumericToken(6);
    }

    /**
     * Generates a cryptographically secure numeric token of the default length (6).
     *
     * <p>Suitable for use as a one-time password (OTP).</p>
     *
     * <p>Example:</p>
     * <pre>
     * secureNumericToken() → "048273"
     * </pre>
     *
     * @return a 6-digit secure numeric token
     */
    public static String secureNumericToken() {
        return secureNumericToken(6);
    }

    /**
     * Generates a cryptographically secure alphanumeric token of the specified length.
     *
     * <p>Example:</p>
     * <pre>
     * secureAlphanumericToken(8) → "x3Kp9mAQ"
     * secureAlphanumericToken(5) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 6
     * @return a secure alphanumeric token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 6
     */
    public static String secureAlphanumericToken(int size) {
        if (size < 6) throw new IllegalArgumentException("Size must be at least 6");
        return randomString(BASE62, size, SECURE_RNG::nextInt);
    }

    /**
     * Generates a cryptographically secure alphabetic token of the specified length.
     *
     * <p>Example:</p>
     * <pre>
     * secureAlphaToken(4) → "kRmZ"
     * secureAlphaToken(0) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 1
     * @return a secure alphabetic token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 1
     */
    public static String secureAlphaToken(int size) {
        if (size < 1) throw new IllegalArgumentException("Size must be at least 1");
        return randomString(ALPHA, size, SECURE_RNG::nextInt);
    }

    /**
     * Generates a cryptographically secure numeric token of the specified length.
     *
     * <p>Suitable for use as a one-time password (OTP).</p>
     *
     * <p>Example:</p>
     * <pre>
     * secureNumericToken(4) → "7392"
     * secureNumericToken(0) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 1
     * @return a secure numeric token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 1
     */
    public static String secureNumericToken(int size) {
        if (size < 1) throw new IllegalArgumentException("Size must be at least 1");
        return randomString(NUMERIC, size, SECURE_RNG::nextInt);
    }

    // -------------------------------------------------------------------------
    // Fast tokens  (use for test data, display codes, idempotency keys, etc.)
    // -------------------------------------------------------------------------

    /**
     * Generates a fast (non-secure) alphanumeric token of the default length (6).
     *
     * <p>Not cryptographically secure. Do <b>not</b> use for authentication,
     * session management, or any security-sensitive context.</p>
     *
     * <p>Example:</p>
     * <pre>
     * fastAlphanumericToken() → "aB3kQ7"
     * </pre>
     *
     * @return a 6-character fast alphanumeric token
     */
    public static String fastAlphanumericToken() {
        return fastAlphanumericToken(6);
    }

    /**
     * Generates a fast (non-secure) numeric token of the default length (6).
     *
     * <p>Not cryptographically secure. Do <b>not</b> use for OTPs or
     * any security-sensitive context.</p>
     *
     * <p>Example:</p>
     * <pre>
     * fastNumericToken() → "048273"
     * </pre>
     *
     * @return a 6-digit fast numeric token
     */
    public static String fastNumericToken() {
        return fastNumericToken(6);
    }

    /**
     * Generates a fast (non-secure) alphanumeric token of the specified length.
     *
     * <p>Not cryptographically secure. Do <b>not</b> use for authentication,
     * session management, or any security-sensitive context.</p>
     *
     * <p>Example:</p>
     * <pre>
     * fastAlphanumericToken(8) → "x3Kp9mAQ"
     * fastAlphanumericToken(5) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 6
     * @return a fast alphanumeric token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 6
     */
    public static String fastAlphanumericToken(int size) {
        if (size < 6) throw new IllegalArgumentException("Size must be at least 6");
        return randomString(BASE62, size, FAST_RNG);
    }

    /**
     * Generates a fast (non-secure) alphabetic token of the specified length.
     *
     * <p>Not cryptographically secure. Do <b>not</b> use for authentication,
     * session management, or any security-sensitive context.</p>
     *
     * <p>Example:</p>
     * <pre>
     * fastAlphaToken(4) → "kRmZ"
     * fastAlphaToken(0) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 1
     * @return a fast alphabetic token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 1
     */
    public static String fastAlphaToken(int size) {
        if (size < 1) throw new IllegalArgumentException("Size must be at least 1");
        return randomString(ALPHA, size, FAST_RNG);
    }

    /**
     * Generates a fast (non-secure) numeric token of the specified length.
     *
     * <p>Not cryptographically secure. Do <b>not</b> use for OTPs or
     * any security-sensitive context.</p>
     *
     * <p>Example:</p>
     * <pre>
     * fastNumericToken(4) → "7392"
     * fastNumericToken(0) → throws IllegalArgumentException
     * </pre>
     *
     * @param size the desired token length; must be at least 1
     * @return a fast numeric token of the given length
     * @throws IllegalArgumentException if {@code size} is less than 1
     */
    public static String fastNumericToken(int size) {
        if (size < 1) throw new IllegalArgumentException("Size must be at least 1");
        return randomString(NUMERIC, size, FAST_RNG);
    }

    // -------------------------------------------------------------------------
    // Composite key  (RNG-agnostic)
    // -------------------------------------------------------------------------

    /**
     * Builds a hyphen-delimited composite key from the given arguments.
     *
     * <p>Each argument must be either a {@link String} or a {@link Number};
     * any other type causes an {@link IllegalArgumentException} to be thrown.
     * Arguments are joined in order, separated by {@code -}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * compositeKey("user", 42, "session")  → "user-42-session"
     * compositeKey("order", 99)            → "order-99"
     * compositeKey("x", new Object())      → throws IllegalArgumentException
     * </pre>
     *
     * @param args the values to join; each must be a {@code String} or {@code Number}
     * @return a hyphen-separated composite key
     * @throws IllegalArgumentException if any argument is not a {@code String} or {@code Number}
     */
    public static String compositeKey(Object... args) {
        StringJoiner joiner = new StringJoiner("-");
        for (Object arg : args) {
            if (arg instanceof String || arg instanceof Number) {
                joiner.add(arg.toString());
            } else {
                throw new IllegalArgumentException("Invalid argument: " + arg);
            }
        }
        return joiner.toString();
    }

    // -------------------------------------------------------------------------
    // Core
    // -------------------------------------------------------------------------

    /**
     * Generates a random string of the given length by sampling from the
     * provided alphabet using the given {@link RandomSource}.
     *
     * <p>The caller controls which RNG backs this call — either
     * {@link #SECURE_RNG} for cryptographic strength or {@link #FAST_RNG}
     * for maximum throughput.</p>
     *
     * @param alphabet the character pool to sample from; must not be empty
     * @param size     the number of characters to generate
     * @param rng      the random source to use
     * @return a randomly assembled string of the specified length
     */
    private static String randomString(char[] alphabet, int size, RandomSource rng) {
        final int len = alphabet.length;
        final char[] buf = new char[size];
        for (int i = 0; i < size; i++) {
            buf[i] = alphabet[rng.nextInt(len)];
        }
        return new String(buf);
    }
}