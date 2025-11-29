package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import jUnitTestCodes.MessageValidator;

/**
 * <p>Title: MessageValidatorTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for {@link MessageValidator}. Tests the {@code validateMessage} method
 * using boundary values and cyclomatic paths. Ensures proper error messages for empty fields,
 * length limits, and valid inputs.</p>
 *
 * @version 1.0
 */
public class MessageValidatorTests {

    private static final String VALID_RECEIVER = "testuser";
    private static final String VALID_SUBJECT = "Hello";
    private static final String VALID_CONTENT = "This is a valid message.";

    /**
     * <p>Method: testEmptyFields()</p>
     * <p>Description: Tests empty field cases. All combinations of empty receiver,
     * subject, and content should return an error message.</p>
     */
    @Test
    void testEmptyFields() {
        assertEquals("All fields are required.", MessageValidator.validateMessage("", VALID_SUBJECT, VALID_CONTENT));
        assertEquals("All fields are required.", MessageValidator.validateMessage(VALID_RECEIVER, "", VALID_CONTENT));
        assertEquals("All fields are required.", MessageValidator.validateMessage(VALID_RECEIVER, VALID_SUBJECT, ""));
        assertEquals("All fields are required.", MessageValidator.validateMessage("", "", ""));
    }

    /**
     * <p>Method: testSubjectLengthBoundary()</p>
     * <p>Description: Tests the subject field at boundary conditions.
     * - 100 characters is valid
     * - 101 characters is invalid</p>
     */
    @Test
    void testSubjectLengthBoundary() {
        String maxSubject = "S".repeat(100);
        String tooLongSubject = "S".repeat(101);

        assertEquals("", MessageValidator.validateMessage(VALID_RECEIVER, maxSubject, VALID_CONTENT));
        assertEquals("Subject cannot exceed 100 characters.", MessageValidator.validateMessage(VALID_RECEIVER, tooLongSubject, VALID_CONTENT));
    }

    /**
     * <p>Method: testContentLengthBoundary()</p>
     * <p>Description: Tests the content field at boundary conditions.
     * - 2200 characters is valid
     * - 2201 characters is invalid</p>
     */
    @Test
    void testContentLengthBoundary() {
        String maxContent = "C".repeat(2200);
        String tooLongContent = "C".repeat(2201);

        assertEquals("", MessageValidator.validateMessage(VALID_RECEIVER, VALID_SUBJECT, maxContent));
        assertEquals("Content cannot exceed 2200 characters.", MessageValidator.validateMessage(VALID_RECEIVER, VALID_SUBJECT, tooLongContent));
    }

    /**
     * <p>Method: testValidMessage()</p>
     * <p>Description: Tests a fully valid message. No error should be returned.</p>
     */
    @Test
    void testValidMessage() {
        assertEquals("", MessageValidator.validateMessage(VALID_RECEIVER, VALID_SUBJECT, VALID_CONTENT));
    }

    /**
     * <p>Method: testMultipleErrors()</p>
     * <p>Description: Even if multiple fields are invalid, the method should return
     * the first encountered error message based on the logic in {@code validateMessage}.</p>
     */
    @Test
    void testMultipleErrors() {
        // Empty receiver takes precedence
        assertEquals("All fields are required.", MessageValidator.validateMessage("", "", "C".repeat(2201)));

        // Empty subject takes precedence if receiver is valid
        assertEquals("All fields are required.", MessageValidator.validateMessage(VALID_RECEIVER, "", "C".repeat(2201)));

        // Empty content takes precedence if receiver and subject are valid
        assertEquals("All fields are required.", MessageValidator.validateMessage(VALID_RECEIVER, VALID_SUBJECT, ""));
    }
}
