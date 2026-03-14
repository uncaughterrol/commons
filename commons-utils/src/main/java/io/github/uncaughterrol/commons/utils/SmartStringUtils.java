package io.github.uncaughterrol.commons.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SmartStringUtils {

    private static final Logger logger = Logger.getLogger(SmartStringUtils.class.getName());

    private static final Map<String, String> singularToPlural = new HashMap<>(64);
    private static final Map<String, String> irregulars = new HashMap<>(64);

    enum CaseType {
        CAMEL,
        DOT,
        KEBAB,
        PASCAL,
        SENTENCE_LOWER,
        SNAKE,
        UPPER_SNAKE,
    }

    static {
        try (InputStream in = SmartStringUtils.class.getClassLoader()
                .getResourceAsStream("irregular-mapping.properties")) {

            if (in != null) {
                Properties props = new Properties();
                props.load(in);

                for (String plural : props.stringPropertyNames()) {
                    String singular = props.getProperty(plural);
                    irregulars.put(plural, singular);
                    singularToPlural.put(singular, plural);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading irregular-mapping.properties", e);
        }
    }

    public SmartStringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts the given string to <b>camelCase</b>.
     *
     * <p>Word boundaries are detected from separators such as
     * underscore ({@code _}), dash ({@code -}), space, dot ({@code .}),
     * or transitions between lower and upper case characters.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toCamelCase("hello_world")      → "helloWorld"
     * toCamelCase("Hello World")      → "helloWorld"
     * toCamelCase("hello-world")      → "helloWorld"
     * </pre>
     *
     * @param input the input string
     * @return the camelCase representation, or the original string if null or empty
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.CAMEL);
    }

    /**
     * Converts the given string to <b>PascalCase</b>.
     *
     * <p>Word boundaries are detected from separators or case transitions.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toPascalCase("hello_world") → "HelloWorld"
     * toPascalCase("user-profile") → "UserProfile"
     * </pre>
     *
     * @param input the input string
     * @return the PascalCase representation, or the original string if null or empty
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.PASCAL);
    }

    /**
     * Converts the given string to <b>snake_case</b>.
     *
     * <p>All characters are converted to lowercase and words are separated
     * using underscores.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toSnakeCase("HelloWorld") → "hello_world"
     * toSnakeCase("userProfile") → "user_profile"
     * </pre>
     *
     * @param input the input string
     * @return the snake_case representation, or the original string if null or empty
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.SNAKE);
    }

    /**
     * Converts the given string to <b>UPPER_SNAKE_CASE</b>.
     *
     * <p>All characters are converted to uppercase and words are separated
     * using underscores.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toUpperSnakeCase("HelloWorld") → "HELLO_WORLD"
     * </pre>
     *
     * @param input the input string
     * @return the UPPER_SNAKE_CASE representation, or the original string if null or empty
     */
    public static String toUpperSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.UPPER_SNAKE);
    }

    /**
     * Converts the given string to <b>kebab-case</b>.
     *
     * <p>All characters are converted to lowercase and words are separated
     * using hyphens.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toKebabCase("HelloWorld") → "hello-world"
     * </pre>
     *
     * @param input the input string
     * @return the kebab-case representation, or the original string if null or empty
     */
    public static String toKebabCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.KEBAB);
    }

    /**
     * Converts the given string to <b>dot.case</b>.
     *
     * <p>All characters are converted to lowercase and words are separated
     * using dots.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toDotCase("HelloWorld") → "hello.world"
     * </pre>
     *
     * @param input the input string
     * @return the dot.case representation, or the original string if null or empty
     */
    public static String toDotCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.DOT);
    }

    /**
     * Converts the given string to a lowercase sentence format.
     *
     * <p>Words are separated by spaces and all characters are lowercase.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toLowerSentenceCase("HelloWorldExample") → "hello world example"
     * </pre>
     *
     * @param input the input string
     * @return the lowercase sentence representation, or the original string if null or empty
     */
    public static String toLowerSentenceCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return transform(input, CaseType.SENTENCE_LOWER);
    }

    /**
     * Capitalizes the first character of the given string.
     *
     * <p>Example:</p>
     * <pre>
     * capFirst("hello") → "Hello"
     * </pre>
     *
     * @param input the input string
     * @return the string with its first character capitalized,
     *         or the original string if null or empty
     */
    public static String capFirst(String input) {
        if (input == null || input.isEmpty()) return input;

        char first = input.charAt(0);
        char upper = toUpperAscii(first);

        if (first == upper) return input;

        char[] chars = input.toCharArray();
        chars[0] = upper;
        return new String(chars);
    }

    /**
     * Converts the first character of the given string to lowercase.
     *
     * <p>Example:</p>
     * <pre>
     * uncapFirst("Hello") → "hello"
     * </pre>
     *
     * @param input the input string
     * @return the string with its first character lowercased,
     *         or the original string if null or empty
     */
    public static String uncapFirst(String input) {
        if (input == null || input.isEmpty()) return input;

        char first = input.charAt(0);
        char lower = toLowerAscii(first);

        if (first == lower) return input;

        char[] chars = input.toCharArray();
        chars[0] = lower;
        return new String(chars);
    }

    /**
     * Converts the given string to sentence case.
     *
     * <p>The first character is capitalized while the rest of the
     * sentence remains lowercase.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toSentenceCase("helloWorldExample") → "Hello world example"
     * </pre>
     *
     * @param input the input string
     * @return the sentence case representation, or the original string if null or empty
     */
    public static String toSentenceCase(String input) {
        if (input == null || input.isEmpty()) return input;

        String result = transform(input, CaseType.SENTENCE_LOWER);

        char first = result.charAt(0);
        char upper = toUpperAscii(first);

        if (first == upper) return result;

        char[] chars = result.toCharArray();
        chars[0] = upper;
        return new String(chars);
    }

    /**
     * Converts a singular English word to its plural form.
     *
     * <p>This method handles common pluralization rules such as:</p>
     * <ul>
     * <li>{@code city → cities}</li>
     * <li>{@code box → boxes}</li>
     * <li>{@code dog → dogs}</li>
     * </ul>
     *
     * <p>Irregular words are resolved using the
     * {@code irregular-mapping.properties} file if available.</p>
     *
     * @param input the singular word
     * @return the plural form, or the original string if null or empty
     */
    public static String toPlural(String input) {
        if (input == null || input.isEmpty()) return input;

        String lower = input.toLowerCase(Locale.ROOT);

        String irregular = singularToPlural.get(lower);
        if (irregular != null) {
            return matchCase(input, irregular);
        }

        if (irregulars.containsKey(lower)) {
            return input;
        }

        int len = lower.length();
        char last = lower.charAt(len - 1);

        if (last == 's' && (len < 2 || lower.charAt(len - 2) != 's')) {
            return input;
        }

        if (last == 'y' && len > 1) {
            char before = lower.charAt(len - 2);

            if (before != 'a' && before != 'e' && before != 'i' && before != 'o' && before != 'u') {
                char[] chars = new char[len + 2];
                input.getChars(0, len - 1, chars, 0);
                chars[len - 1] = 'i';
                chars[len] = 'e';
                chars[len + 1] = 's';
                return matchCase(input, new String(chars));
            }
        }

        if (last == 's' || last == 'x' || last == 'z' ||
                (last == 'h' && len > 1 && (lower.charAt(len - 2) == 's' || lower.charAt(len - 2) == 'c'))) {

            return input + "es";
        }

        return input + "s";
    }

    /**
     * Converts a plural English word to its singular form.
     *
     * <p>This method handles common singularization rules and
     * supports irregular word mappings loaded from
     * {@code irregular-mapping.properties}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * toSingular("cities") → "city"
     * toSingular("boxes") → "box"
     * </pre>
     *
     * @param word the plural word
     * @return the singular form, or the original string if null or empty
     */
    public static String toSingular(String word) {
        if (word == null || word.isEmpty()) return word;

        String lower = word.toLowerCase(Locale.ROOT);
        int len = lower.length();

        String irregular = irregulars.get(lower);
        if (irregular != null) {
            return matchCase(word, irregular);
        }

        if (len >= 4 &&
                lower.charAt(len - 3) == 'i' &&
                lower.charAt(len - 2) == 'e' &&
                lower.charAt(len - 1) == 's') {

            char[] chars = new char[len - 2];
            word.getChars(0, len - 3, chars, 0);
            chars[len - 3] = 'y';
            return matchCase(word, new String(chars));
        }

        if (len >= 3) {
            char c2 = lower.charAt(len - 2);
            char c3 = lower.charAt(len - 1);

            if (c3 == 's' && c2 == 'e') {
                char c1 = lower.charAt(len - 3);
                if (c1 == 's' || c1 == 'x' || c1 == 'z' ||
                        (c1 == 'h' && len > 3 && (lower.charAt(len - 4) == 's' || lower.charAt(len - 4) == 'c'))) {

                    return word.substring(0, len - 2);
                }
            }
        }

        if (len > 1 && lower.charAt(len - 1) == 's') {
            return word.substring(0, len - 1);
        }

        return word;
    }

    private static String transform(String input, CaseType type) {
        int len = input.length();
        StringBuilder sb = new StringBuilder(len + 8);

        boolean upperNext = type == CaseType.PASCAL;
        boolean firstWord = true;

        char sep = switch (type) {
            case SNAKE, UPPER_SNAKE -> '_';
            case KEBAB -> '-';
            case DOT -> '.';
            case SENTENCE_LOWER -> ' ';
            default -> 0;
        };

        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);

            if (isSeparator(c)) {
                upperNext = true;

                if (sep != 0 && !sb.isEmpty() && sb.charAt(sb.length() - 1) != sep) {
                    sb.append(sep);
                }
                continue;
            }

            if (i > 0) {
                char prev = input.charAt(i - 1);
                char next = (i + 1 < len) ? input.charAt(i + 1) : 0;

                if (isBoundary(prev, c, next)) {
                    upperNext = true;

                    if (sep != 0) {
                        sb.append(sep);
                    }
                }
            }

            if (type == CaseType.UPPER_SNAKE) {
                sb.append(toUpperAscii(c));
                firstWord = false;
                continue;
            }

            if (type == CaseType.SNAKE || type == CaseType.KEBAB ||
                    type == CaseType.DOT || type == CaseType.SENTENCE_LOWER) {

                sb.append(toLowerAscii(c));
                firstWord = false;
                continue;
            }

            if (firstWord && type == CaseType.CAMEL) {
                sb.append(toLowerAscii(c));
                firstWord = false;
                continue;
            }

            if (upperNext) {
                sb.append(toUpperAscii(c));
                upperNext = false;
                firstWord = false;
            } else {
                sb.append(toLowerAscii(c));
                firstWord = false;
            }
        }

        return sb.toString();
    }

    private static boolean isBoundary(char prev, char curr, char next) {
        if (isLowerAscii(prev) && isUpperAscii(curr)) return true;
        return isUpperAscii(prev) && isUpperAscii(curr) && isLowerAscii(next);
    }

    private static boolean isSeparator(char c) {
        return c == '_' || c == '-' || c == ' ' || c == '.';
    }

    private static boolean isUpperAscii(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isLowerAscii(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static char toLowerAscii(char c) {
        return (c >= 'A' && c <= 'Z') ? (char) (c + 32) : c;
    }

    private static char toUpperAscii(char c) {
        return (c >= 'a' && c <= 'z') ? (char) (c - 32) : c;
    }

    private static String matchCase(String original, String result) {
        int len = result.length();

        boolean allUpper = true;
        for (int i = 0; i < original.length(); i++) {
            if (isLowerAscii(original.charAt(i))) {
                allUpper = false;
                break;
            }
        }
        if (allUpper) {
            char[] upper = new char[len];
            for (int i = 0; i < len; i++) {
                upper[i] = toUpperAscii(result.charAt(i));
            }
            return new String(upper);
        }

        if (isUpperAscii(original.charAt(0))) {
            char[] chars = new char[len];
            chars[0] = toUpperAscii(result.charAt(0));
            for (int i = 1; i < len; i++) {
                chars[i] = toLowerAscii(result.charAt(i));
            }
            return new String(chars);
        }

        char[] lower = new char[len];
        for (int i = 0; i < len; i++) {
            lower[i] = toLowerAscii(result.charAt(i));
        }
        return new String(lower);
    }
}