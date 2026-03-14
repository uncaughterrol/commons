package io.github.uncaughterrol.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SmartStringUtilsTests {

    @Test
    void capFirst_shouldCapitalizedFirstCharacter() {
        Assertions.assertEquals("SmartString", SmartStringUtils.capFirst("smartString"));
    }

    @Test
    void uncapFirst_shouldLowercaseFirstCharacter() {
        Assertions.assertEquals("smartString", SmartStringUtils.uncapFirst("SmartString"));
    }

    @Test
    void toCamelCase_shouldJoinWordsWithFirstCharacterLowerCase() {
        Assertions.assertEquals("smartStringUtils", SmartStringUtils.toCamelCase("Smart String Utils"));
    }

    @Test
    void toPascalCase_shouldJoinWordsWithEachWordCapitalized() {
        Assertions.assertEquals("SmartStringUtils", SmartStringUtils.toPascalCase("smart string utils"));
    }

    @Test
    void toDotCase_shouldJoinWordsWithDot() {
        Assertions.assertEquals("smart.string.utils", SmartStringUtils.toDotCase("smartStringUtils"));
    }

    @Test
    void toKebabCase_shouldJoinWordsWithHyphen() {
        Assertions.assertEquals("smart-string-utils", SmartStringUtils.toKebabCase("smartStringUtils"));
    }

    @Test
    void toSnakeCase_shouldJoinWordsWithUnderscore() {
        Assertions.assertEquals("smart_string_utils", SmartStringUtils.toSnakeCase("smartStringUtils"));
    }

    @Test
    void toUpperSnakeCase_shouldJoinWordsWithUnderscoreUppercased() {
        Assertions.assertEquals("SMART_STRING_UTILS", SmartStringUtils.toUpperSnakeCase("smartStringUtils"));
    }

    @Test
    void toLowerSentenceCase_shouldJoinWordsWithSpaceLowercased() {
        Assertions.assertEquals("smart string utils", SmartStringUtils.toLowerSentenceCase("Smart String Utils"));
    }

    @Test
    void toSentenceCase_shouldCapitalizeFirstWordOnly() {
        Assertions.assertEquals("Smart string utils", SmartStringUtils.toSentenceCase("smart string utils"));
    }
    @Test
    void toPlural_shouldAppendSForRegularWords() {
        Assertions.assertEquals("cats", SmartStringUtils.toPlural("cat"));
    }

    @Test
    void toPlural_shouldAppendEsWhenWordEndsWithSOrXOrZ() {
        Assertions.assertEquals("boxes", SmartStringUtils.toPlural("box"));
    }

    @Test
    void toPlural_shouldReplaceYWithIesWhenWordEndsWithConsonantY() {
        Assertions.assertEquals("categories", SmartStringUtils.toPlural("category"));
    }

    @Test
    void toSingular_shouldRemoveTrailingS() {
        Assertions.assertEquals("cat", SmartStringUtils.toSingular("cats"));
    }

    @Test
    void toSingular_shouldRemoveEsForSuffixedWords() {
        Assertions.assertEquals("box", SmartStringUtils.toSingular("boxes"));
    }

    @Test
    void toSingular_shouldRemoveIesAndAppendY() {
        Assertions.assertEquals("category", SmartStringUtils.toSingular("categories"));
    }

    // --- Irregulars ---

    @Test
    void irregulars_toPlural() {
        Assertions.assertEquals("mice", SmartStringUtils.toPlural("mouse"));
        Assertions.assertEquals("children", SmartStringUtils.toPlural("child"));
        Assertions.assertEquals("people", SmartStringUtils.toPlural("person"));
    }

    @Test
    void irregulars_toSingular() {
        Assertions.assertEquals("mouse", SmartStringUtils.toSingular("mice"));
        Assertions.assertEquals("child", SmartStringUtils.toSingular("children"));
        Assertions.assertEquals("person", SmartStringUtils.toSingular("people"));
    }

    @Test
    void irregulars_toPlural_casing() {
        Assertions.assertEquals("Mice", SmartStringUtils.toPlural("Mouse"));
        Assertions.assertEquals("CHILDREN", SmartStringUtils.toPlural("CHILD"));
    }

    @Test
    void irregulars_toSingular_casing() {
        Assertions.assertEquals("Mouse", SmartStringUtils.toSingular("Mice"));
        Assertions.assertEquals("CHILD", SmartStringUtils.toSingular("CHILDREN"));
    }
}