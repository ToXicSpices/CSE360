package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import jUnitTestCodes.UserValidator;

/**
 * <p>Title: UserValidatorTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for validating the methods in {@link UserValidator}.
 * This class tests username, password, and email validation logic to ensure
 * all rules are correctly enforced and appropriate error messages are returned.</p>
 *
 * <p>Each test is isolated and focuses on a single validation scenario.</p>
 *
 * @version 1.0
 */
public class UserValidatorTests {

    /**
     * <p>Test Method: testEmptyUsername()</p>
     * <p>Description: Verifies that {@link UserValidator#checkValidUsername(String)}
     * returns an error message when the input username is empty.</p>
     */
    @Test
    public void testEmptyUsername() {
        String result = UserValidator.checkValidUsername("");
        assertEquals("Username is empty", result);
    }

    /**
     * <p>Test Method: testUsernameTooShort()</p>
     * <p>Description: Ensures that a username shorter than 4 characters is rejected
     * and returns the proper error message.</p>
     */
    @Test
    public void testUsernameTooShort() {
        String result = UserValidator.checkValidUsername("abc");
        assertEquals("A Username must have at least 4 characters.\n", result);
    }

    /**
     * <p>Test Method: testUsernameTooLong()</p>
     * <p>Description: Ensures that a username longer than 16 characters is rejected
     * and returns the proper error message.</p>
     */
    @Test
    public void testUsernameTooLong() {
        String longUsername = "abcdefghijklmnopq"; // 17 chars
        String result = UserValidator.checkValidUsername(longUsername);
        assertEquals("A Username must have no more than 16 characters.", result);
    }

    /**
     * <p>Test Method: testUsernameInvalidStart()</p>
     * <p>Description: Checks that a username starting with a non-alphabet character
     * is rejected and the correct error message is returned.</p>
     */
    @Test
    public void testUsernameInvalidStart() {
        String result = UserValidator.checkValidUsername("1username");
        assertEquals("A Username must start with alphabet!(A-Z or a-z).", result);
    }

    /**
     * <p>Test Method: testValidUsername()</p>
     * <p>Description: Ensures a properly formatted username passes validation
     * and returns an empty error string.</p>
     */
    @Test
    public void testValidUsername() {
        String result = UserValidator.checkValidUsername("user.name_01");
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testEmptyPassword()</p>
     * <p>Description: Verifies that {@link UserValidator#checkValidPassword(String)}
     * returns an error when the password input is empty.</p>
     */
    @Test
    public void testEmptyPassword() {
        String result = UserValidator.checkValidPassword("");
        assertEquals("Password is empty", result);
    }

    /**
     * <p>Test Method: testPasswordMissingUppercase()</p>
     * <p>Description: Ensures that a password missing an uppercase letter
     * is rejected with the proper error message.</p>
     */
    @Test
    public void testPasswordMissingUppercase() {
        String result = UserValidator.checkValidPassword("lowercase1!");
        assertEquals("Needs a upper case alphabet for password", result);
    }

    /**
     * <p>Test Method: testPasswordMissingLowercase()</p>
     * <p>Description: Ensures that a password missing a lowercase letter
     * is rejected with the proper error message.</p>
     */
    @Test
    public void testPasswordMissingLowercase() {
        String result = UserValidator.checkValidPassword("UPPERCASE1!");
        assertEquals("Needs a lower case alphabet for password", result);
    }

    /**
     * <p>Test Method: testPasswordMissingNumber()</p>
     * <p>Description: Ensures that a password missing a numeric digit
     * is rejected with the proper error message.</p>
     */
    @Test
    public void testPasswordMissingNumber() {
        String result = UserValidator.checkValidPassword("Password!");
        assertEquals("Needs a number for password", result);
    }

    /**
     * <p>Test Method: testPasswordMissingSpecialChar()</p>
     * <p>Description: Ensures that a password missing a special character
     * is rejected with the proper error message.</p>
     */
    @Test
    public void testPasswordMissingSpecialChar() {
        String result = UserValidator.checkValidPassword("Password1");
        assertEquals("Needs a special for password", result);
    }

    /**
     * <p>Test Method: testPasswordValid()</p>
     * <p>Description: Ensures a valid password that meets all rules passes
     * and returns an empty string.</p>
     */
    @Test
    public void testPasswordValid() {
        String result = UserValidator.checkValidPassword("Passw0rd!");
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testEmptyEmail()</p>
     * <p>Description: Verifies that {@link UserValidator#checkValidEmail(String)}
     * returns an error when the email input is empty.</p>
     */
    @Test
    public void testEmptyEmail() {
        String result = UserValidator.checkValidEmail("");
        assertEquals("Email is empty", result);
    }

    /**
     * <p>Test Method: testEmailMissingAt()</p>
     * <p>Description: Ensures an email without '@' is rejected with a proper error message.</p>
     */
    @Test
    public void testEmailMissingAt() {
        String result = UserValidator.checkValidEmail("user.domain.com");
        assertEquals("Needs \"@\" for email address", result);
    }

    /**
     * <p>Test Method: testEmailValid()</p>
     * <p>Description: Ensures a properly formatted email passes validation
     * and returns an empty string.</p>
     */
    @Test
    public void testEmailValid() {
        String result = UserValidator.checkValidEmail("user@example.com");
        assertEquals("", result);
    }

    /**
     * <p>Test Method: testEmailInvalidLocalPart()</p>
     * <p>Description: Ensures that an email starting with a dot in the local part
     * is rejected with the correct error message.</p>
     */
    @Test
    public void testEmailInvalidLocalPart() {
        String result = UserValidator.checkValidEmail(".user@example.com");
        assertEquals("Needs quotations(\") for special characters like space and \"(),:;<>@[\\]\"", result);
    }
}
