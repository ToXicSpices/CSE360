package jUnitTesting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import jUnitTestCodes.ReplyValidator;

/**
 * <p>Title: ReplyValidatorTests Class</p>
 *
 * <p>Description:
 * This class contains JUnit 5 test cases for the {@link ReplyValidator} class.
 * It tests the {@code validateReply} method using boundary value analysis and
 * cyclomatic path coverage. The tests verify that replies which are empty,
 * exceed the maximum allowed length, or are valid, return the correct messages.
 * </p>
 */
public class ReplyValidatorTests {

    /**
     * <p>Test Method: testEmptyReply()</p>
     * <p>Description: Tests the behavior of {@link ReplyValidator#validateReply(String)}
     * when the reply is empty (0 characters). The method should return the error message
     * indicating that the user must type something.</p>
     */
    @Test
    void testEmptyReply() {
        String content = "";
        String result = ReplyValidator.validateReply(content);
        assertEquals("Please type something before sending.", result);
    }

    /**
     * <p>Test Method: testSingleCharacterReply()</p>
     * <p>Description: Tests the behavior of {@link ReplyValidator#validateReply(String)}
     * when the reply has a single character. This is the minimum non-empty valid input
     * and should return an empty string, indicating the reply is valid.</p>
     */
    @Test
    void testSingleCharacterReply() {
        String content = "a";
        String result = ReplyValidator.validateReply(content);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testReplyExactly1000Chars()</p>
     * <p>Description: Tests the behavior of {@link ReplyValidator#validateReply(String)}
     * when the reply contains exactly 1000 characters, which is the maximum valid length.
     * The method should return an empty string.</p>
     */
    @Test
    void testReplyExactly1000Chars() {
        String content = "a".repeat(1000);
        String result = ReplyValidator.validateReply(content);
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testReplyJustAbove1000Chars()</p>
     * <p>Description: Tests the behavior of {@link ReplyValidator#validateReply(String)}
     * when the reply exceeds 1000 characters (1001 characters). The method should return
     * the error message indicating the reply is too long.</p>
     */
    @Test
    void testReplyJustAbove1000Chars() {
        String content = "a".repeat(1001);
        String result = ReplyValidator.validateReply(content);
        assertEquals("Replies cannot exceed 1000 characters.", result);
    }

    /**
     * <p>Test Method: testNormalReply()</p>
     * <p>Description: Tests the behavior of {@link ReplyValidator#validateReply(String)}
     * for a typical valid reply of normal length. The method should return an empty
     * string, indicating the reply is valid.</p>
     */
    @Test
    void testNormalReply() {
        String content = "This is a normal reply.";
        String result = ReplyValidator.validateReply(content);
        assertEquals("", result);
    }
}
