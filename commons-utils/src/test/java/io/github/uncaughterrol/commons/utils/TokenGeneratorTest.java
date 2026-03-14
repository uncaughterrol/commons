package io.github.uncaughterrol.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenGeneratorTest {

    private static final String BASE62_PATTERN = "[0-9A-Za-z]+";
    private static final String ALPHA_PATTERN = "[A-Za-z]+";
    private static final String NUMERIC_PATTERN = "[0-9]+";
    private static final String COMPOSITE_PATTERN = "[0-9A-Za-z\\-]+";

    @Test
    void secureAlphanumericToken_shouldReturnSixCharsByDefault() {
        Assertions.assertEquals(6, TokenGenerator.secureAlphanumericToken().length());
    }

    @Test
    void secureAlphanumericToken_shouldContainOnlyBase62Chars() {
        Assertions.assertTrue(TokenGenerator.secureAlphanumericToken().matches(BASE62_PATTERN));
    }

    @Test
    void secureAlphanumericToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(12, TokenGenerator.secureAlphanumericToken(12).length());
    }

    @Test
    void secureAlphanumericToken_shouldThrowIfSizeLessThanSix() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.secureAlphanumericToken(5));
    }

    @Test
    void secureAlphaToken_shouldContainOnlyAlphaChars() {
        Assertions.assertTrue(TokenGenerator.secureAlphaToken(8).matches(ALPHA_PATTERN));
    }

    @Test
    void secureAlphaToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(8, TokenGenerator.secureAlphaToken(8).length());
    }

    @Test
    void secureAlphaToken_shouldThrowIfSizeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.secureAlphaToken(0));
    }

    @Test
    void secureNumericToken_shouldReturnSixCharsByDefault() {
        Assertions.assertEquals(6, TokenGenerator.secureNumericToken().length());
    }

    @Test
    void secureNumericToken_shouldContainOnlyDigits() {
        Assertions.assertTrue(TokenGenerator.secureNumericToken().matches(NUMERIC_PATTERN));
    }

    @Test
    void secureNumericToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(8, TokenGenerator.secureNumericToken(8).length());
    }

    @Test
    void secureNumericToken_shouldThrowIfSizeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.secureNumericToken(0));
    }

    @Test
    void fastAlphanumericToken_shouldReturnSixCharsByDefault() {
        Assertions.assertEquals(6, TokenGenerator.fastAlphanumericToken().length());
    }

    @Test
    void fastAlphanumericToken_shouldContainOnlyBase62Chars() {
        Assertions.assertTrue(TokenGenerator.fastAlphanumericToken().matches(BASE62_PATTERN));
    }

    @Test
    void fastAlphanumericToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(12, TokenGenerator.fastAlphanumericToken(12).length());
    }

    @Test
    void fastAlphanumericToken_shouldThrowIfSizeLessThanSix() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.fastAlphanumericToken(5));
    }

    @Test
    void fastAlphaToken_shouldContainOnlyAlphaChars() {
        Assertions.assertTrue(TokenGenerator.fastAlphaToken(8).matches(ALPHA_PATTERN));
    }

    @Test
    void fastAlphaToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(8, TokenGenerator.fastAlphaToken(8).length());
    }

    @Test
    void fastAlphaToken_shouldThrowIfSizeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.fastAlphaToken(0));
    }

    @Test
    void fastNumericToken_shouldReturnSixCharsByDefault() {
        Assertions.assertEquals(6, TokenGenerator.fastNumericToken().length());
    }

    @Test
    void fastNumericToken_shouldContainOnlyDigits() {
        Assertions.assertTrue(TokenGenerator.fastNumericToken().matches(NUMERIC_PATTERN));
    }

    @Test
    void fastNumericToken_shouldReturnCorrectLength() {
        Assertions.assertEquals(8, TokenGenerator.fastNumericToken(8).length());
    }

    @Test
    void fastNumericToken_shouldThrowIfSizeLessThanOne() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.fastNumericToken(0));
    }

    @Test
    void compositeKey_shouldJoinStringsWithHyphen() {
        Assertions.assertEquals("user-session-42", TokenGenerator.compositeKey("user", "session", 42));
    }

    @Test
    void compositeKey_shouldHandleSingleArg() {
        Assertions.assertEquals("order", TokenGenerator.compositeKey("order"));
    }

    @Test
    void compositeKey_shouldHandleNumberArgs() {
        Assertions.assertEquals("1-2-3", TokenGenerator.compositeKey(1, 2, 3));
    }

    @Test
    void compositeKey_shouldThrowOnInvalidArgType() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> TokenGenerator.compositeKey("x", new Object()));
    }

    @Test
    void compositeKey_shouldMatchExpectedPattern() {
        Assertions.assertTrue(TokenGenerator.compositeKey("abc", 99).matches(COMPOSITE_PATTERN));
    }
}