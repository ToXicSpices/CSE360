package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import jUnitTestCodes.ThreadValidator;

/**
 * <p>Title: ThreadValidatorTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for testing the {@link ThreadValidator} class.
 * Covers input validation for thread names, including empty names, invalid characters,
 * maximum length boundaries, and valid names.
 * </p>
 *
 * <p>Tests include boundary value analysis and cyclomatic path coverage.</p>
 */
public class ThreadValidatorTests {

    /** Valid thread name for testing. */
    private static final String VALID_NAME = "Thread123";

    /** Maximum allowed length for thread names. */
    private static final int MAX_LENGTH = 20;

    /**
     * <p>Method: testEmptyThreadName()</p>
     * <p>Description: Tests an empty thread name. Should return the "Thread name cannot be empty." error.</p>
     */
    @Test
    void testEmptyThreadName() {
        assertEquals("Thread name cannot be empty.", ThreadValidator.validateThreads(""));
    }

    /**
     * <p>Method: testInvalidCharacters()</p>
     * <p>Description: Tests thread names with invalid characters (special characters or spaces). 
     * Should return the "Thread may only contain alphabets or numbers." error.</p>
     */
    @Test
    void testInvalidCharacters() {
        assertEquals("Thread may only contain alphabets or numbers.", 
                     ThreadValidator.validateThreads("Thread!@#"));
        assertEquals("Thread may only contain alphabets or numbers.", 
                     ThreadValidator.validateThreads("Thread Name")); // space invalid
    }

    /**
     * <p>Method: testMaxLengthBoundary()</p>
     * <p>Description: Tests thread names at the maximum allowed length (20 characters) 
     * and exceeding it (21 characters). Ensures correct boundary validation.</p>
     */
    @Test
    void testMaxLengthBoundary() {
        String validBoundary = "A".repeat(MAX_LENGTH); // 20 chars
        String invalidBoundary = "B".repeat(MAX_LENGTH + 1); // 21 chars

        // Valid max length
        assertEquals("", ThreadValidator.validateThreads(validBoundary));

        // Exceeds max length
        assertEquals("Thread name cannot be longer than 20 characters.", 
                     ThreadValidator.validateThreads(invalidBoundary));
    }

    /**
     * <p>Method: testValidNames()</p>
     * <p>Description: Tests valid thread names. All should return an empty string (no errors).</p>
     */
    @Test
    void testValidNames() {
        assertEquals("", ThreadValidator.validateThreads(VALID_NAME));
        assertEquals("", ThreadValidator.validateThreads("abcXYZ123"));
        assertEquals("", ThreadValidator.validateThreads("A1B2C3D4E5F6G7H8I9J")); // exactly 20 chars
    }

    /**
     * <p>Method: testCyclomaticPaths()</p>
     * <p>Description: Tests multiple cyclomatic paths of the validator:
     * empty name, invalid characters, exceeding max length, and valid name.</p>
     */
    @Test
    void testCyclomaticPaths() {
        // Empty name
        assertEquals("Thread name cannot be empty.", ThreadValidator.validateThreads(""));

        // Invalid chars
        assertEquals("Thread may only contain alphabets or numbers.", 
                     ThreadValidator.validateThreads("name!"));

        // Exceed max length
        assertEquals("Thread name cannot be longer than 20 characters.", 
                     ThreadValidator.validateThreads("A".repeat(21)));

        // Valid name
        assertEquals("", ThreadValidator.validateThreads("ValidThread1"));
    }
}
