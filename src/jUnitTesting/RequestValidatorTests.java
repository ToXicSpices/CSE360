package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import jUnitTestCodes.RequestValidator;

/**
 * <p>Title: RequestValidatorTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for {@link RequestValidator}. Tests the {@code validateRequests} method
 * using boundary values and cyclomatic paths. Ensures proper error messages for empty fields,
 * maximum length limits, and valid inputs.</p>
 *
 * @version 1.0
 */
public class RequestValidatorTests {

	/** 
	 * <p>Description: A valid request title used for testing purposes.
	 * Ensures that validation logic passes for a correctly formatted title.</p>
	 */
	private static final String VALID_TITLE = "Valid Request Title";

	/** 
	 * <p>Description: A valid request content used for testing purposes.
	 * Ensures that validation logic passes for correctly formatted content.</p>
	 */
	private static final String VALID_CONTENT = "This is valid request content.";

    /**
     * <p>Method: testEmptyTitle()</p>
     * <p>Description: Tests empty title field. Should return the appropriate error message.</p>
     */
    @Test
    void testEmptyTitle() {
        assertEquals("Title cannot be empty.", RequestValidator.validateRequests("", VALID_CONTENT));
    }

    /**
     * <p>Method: testTitleLengthBoundary()</p>
     * <p>Description: Tests the title field at boundary conditions.
     * - 79 characters is valid
     * - 80 characters is invalid</p>
     */
    @Test
    void testTitleLengthBoundary() {
        String maxTitle = "T".repeat(79);
        String tooLongTitle = "T".repeat(80);

        assertEquals("", RequestValidator.validateRequests(maxTitle, VALID_CONTENT));
        assertEquals("Title cannot be longer than 80 characters", RequestValidator.validateRequests(tooLongTitle, VALID_CONTENT));
    }

    /**
     * <p>Method: testEmptyContent()</p>
     * <p>Description: Tests empty content field. Should return the appropriate error message.</p>
     */
    @Test
    void testEmptyContent() {
        assertEquals("Content cannot be empty.", RequestValidator.validateRequests(VALID_TITLE, ""));
    }

    /**
     * <p>Method: testContentLengthBoundary()</p>
     * <p>Description: Tests the content field at boundary conditions.
     * - 2200 characters is valid
     * - 2201 characters is invalid</p>
     */
    @Test
    void testContentLengthBoundary() {
        String maxContent = "C".repeat(2199);
        String tooLongContent = "C".repeat(2200);

        assertEquals("", RequestValidator.validateRequests(VALID_TITLE, maxContent));
        assertEquals("Content cannot be longer than 2200 characters", RequestValidator.validateRequests(VALID_TITLE, tooLongContent));
    }

    /**
     * <p>Method: testValidRequest()</p>
     * <p>Description: Tests a fully valid request. No error should be returned.</p>
     */
    @Test
    void testValidRequest() {
        assertEquals("", RequestValidator.validateRequests(VALID_TITLE, VALID_CONTENT));
    }

    /**
     * <p>Method: testMultipleErrors()</p>
     * <p>Description: Tests multiple invalid fields. The first encountered error
     * should be returned according to the logic in {@code validateRequests}.</p>
     */
    @Test
    void testMultipleErrors() {
        // Empty title takes precedence
        assertEquals("Title cannot be empty.", RequestValidator.validateRequests("", ""));

        // Too long title takes precedence if not empty
        assertEquals("Title cannot be longer than 80 characters",
                RequestValidator.validateRequests("T".repeat(80), ""));

        // Empty content takes precedence if title is valid
        assertEquals("Content cannot be empty.", RequestValidator.validateRequests(VALID_TITLE, ""));
    }
}
