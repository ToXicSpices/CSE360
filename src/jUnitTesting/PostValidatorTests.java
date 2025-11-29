package jUnitTesting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import jUnitTestCodes.PostValidator;

/**
 * <p>Title: PostValidatorTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for {@link PostValidator}. (from guiMakePost.ModelMakePost)
 * Tests boundary values and cyclomatic paths for title, subtitle, content, and tag validations.
 * Each test method verifies the expected error messages or successful validation.
 * </p>
 */
public class PostValidatorTests {

    // ======== Title Tests ========

    /**
     * <p>Test Method: testTitleTooShort()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} when the title
     * is shorter than 5 characters. Should return an error indicating the title is too short.</p>
     */
    @Test
    void testTitleTooShort() {
        String title = "abcd";
        String result = PostValidator.checkValidTitle(title);
        assertEquals("Too short! At least 5 characters.", result);
    }

    /**
     * <p>Test Method: testTitleMinLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} with a title
     * exactly 5 characters long. Should return an empty string indicating valid input.</p>
     */
    @Test
    void testTitleMinLength() {
        String title = "abcde";
        String result = PostValidator.checkValidTitle(title);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testTitleMaxLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} with a title
     * exactly 120 characters long. Should return an empty string indicating valid input.</p>
     */
    @Test
    void testTitleMaxLength() {
        String title = "a".repeat(120);
        String result = PostValidator.checkValidTitle(title);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testTitleTooLong()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} when the title
     * exceeds 120 characters. Should return an error indicating the title is too long.</p>
     */
    @Test
    void testTitleTooLong() {
        String title = "a".repeat(121);
        String result = PostValidator.checkValidTitle(title);
        assertEquals("Too long! No more than 120 characters.", result);
    }

    /**
     * <p>Test Method: testTitleInvalidCharacter()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} when the title
     * contains invalid characters (e.g., emoji). Should return an error indicating invalid input.</p>
     */
    @Test
    void testTitleInvalidCharacter() {
        String title = "Title😀";
        String result = PostValidator.checkValidTitle(title);
        assertEquals("Invalid Input", result);
    }

    /**
     * <p>Test Method: testTitleNormalValid()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTitle(String)} with a normal
     * valid title containing letters, numbers, and allowed special characters. Should return empty string.</p>
     */
    @Test
    void testTitleNormalValid() {
        String title = "Hello World! 123";
        String result = PostValidator.checkValidTitle(title);
        assertEquals("", result);
    }

    // ======== Subtitle Tests ========

    /**
     * <p>Test Method: testSubtitleTooLong()</p>
     * <p>Description: Tests {@link PostValidator#checkValidSubtitle(String)} when the subtitle
     * exceeds 80 characters. Should return an error indicating the subtitle is too long.</p>
     */
    @Test
    void testSubtitleTooLong() {
        String subtitle = "a".repeat(81);
        String result = PostValidator.checkValidSubtitle(subtitle);
        assertEquals("Too long! No more than 80 characters", result);
    }

    /**
     * <p>Test Method: testSubtitleWithNewline()</p>
     * <p>Description: Tests {@link PostValidator#checkValidSubtitle(String)} when the subtitle
     * contains a newline character. Should return an error indicating only one line is allowed.</p>
     */
    @Test
    void testSubtitleWithNewline() {
        String subtitle = "Line 1\nLine 2";
        String result = PostValidator.checkValidSubtitle(subtitle);
        assertEquals("No more than 1 line!", result);
    }

    /**
     * <p>Test Method: testSubtitleValid()</p>
     * <p>Description: Tests {@link PostValidator#checkValidSubtitle(String)} with a valid
     * subtitle. Should return an empty string indicating valid input.</p>
     */
    @Test
    void testSubtitleValid() {
        String subtitle = "This is a valid subtitle.";
        String result = PostValidator.checkValidSubtitle(subtitle);
        assertEquals("", result);
    }

    // ======== Content Tests ========

    /**
     * <p>Test Method: testContentTooShort()</p>
     * <p>Description: Tests {@link PostValidator#checkValidContent(String)} when content
     * is shorter than 10 characters. Should return an error indicating the content is too short.</p>
     */
    @Test
    void testContentTooShort() {
        String content = "Too short";
        String result = PostValidator.checkValidContent(content);
        assertEquals("Too short! At least 10 characters.", result);
    }

    /**
     * <p>Test Method: testContentMinLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidContent(String)} with content
     * exactly 10 characters long. Should return an empty string indicating valid input.</p>
     */
    @Test
    void testContentMinLength() {
        String content = "1234567890";
        String result = PostValidator.checkValidContent(content);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testContentMaxLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidContent(String)} with content
     * exactly 2200 characters long. Should return an empty string indicating valid input.</p>
     */
    @Test
    void testContentMaxLength() {
        String content = "a".repeat(2200);
        String result = PostValidator.checkValidContent(content);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testContentTooLong()</p>
     * <p>Description: Tests {@link PostValidator#checkValidContent(String)} when content
     * exceeds 2200 characters. Should return an error indicating the content is too long.</p>
     */
    @Test
    void testContentTooLong() {
        String content = "a".repeat(2201);
        String result = PostValidator.checkValidContent(content);
        assertEquals("Too long! No more than 2200 characters.", result);
    }

    // ======== Tag Tests ========

    /**
     * <p>Test Method: testTagTooShort()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} when tag is shorter
     * than 2 characters. Should return an error indicating tag is too short.</p>
     */
    @Test
    void testTagTooShort() {
        String tag = "A";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("Too short! At least 2 characters.", result);
    }

    /**
     * <p>Test Method: testTagMinLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} with tag exactly
     * 2 characters long. Should return empty string indicating valid input.</p>
     */
    @Test
    void testTagMinLength() {
        String tag = "AB";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testTagMaxLength()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} with tag exactly
     * 15 characters long. Should return empty string indicating valid input.</p>
     */
    @Test
    void testTagMaxLength() {
        String tag = "abcdefghijklmno";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testTagTooLong()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} when tag exceeds
     * 15 characters. Should return an error indicating tag is too long.</p>
     */
    @Test
    void testTagTooLong() {
        String tag = "abcdefghijklmnop";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("Too long! No more than 15 characters", result);
    }

    /**
     * <p>Test Method: testTagInvalidCharacter()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} when tag contains
     * invalid characters (non-alphabetic). Should return an error indicating invalid input.</p>
     */
    @Test
    void testTagInvalidCharacter() {
        String tag = "Tag1";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("Invalid Input", result);
    }

    /**
     * <p>Test Method: testTagNormalValid()</p>
     * <p>Description: Tests {@link PostValidator#checkValidTag(String)} with a normal valid
     * tag containing only alphabetic characters. Should return empty string indicating valid input.</p>
     */
    @Test
    void testTagNormalValid() {
        String tag = "ValidTag";
        String result = PostValidator.checkValidTag(tag);
        assertEquals("", result);
    }
}
