package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import guiMakePost.ModelMakePost;

/**
 * <p>Test class focused on input validation for post creation.</p>
 *
 * <p>This class validates the correctness of the validation methods in
 * {@link guiMakePost.ModelMakePost}, including title, subtitle, content,
 * and tag checks. These tests ensure that invalid inputs are correctly
 * rejected and valid inputs are accepted.</p>
 *
 * <p>All tests are headless and independent, with no UI or database
 * dependencies.</p>
 *
 * @version 1.0
 */
public class PostValidationTests {

	 /**
     * Default constructor for PostValidationTests.
     * JUnit creates a new instance of this test class for each test method.
     */
    public PostValidationTests() {
        // no-op
    }
    
    /**
     * ✅ Tests validation of post titles.
     *
     * <p>Checks for:</p>
     * <ul>
     *     <li>Too short titles (less than 5 characters)</li>
     *     <li>Too long titles (more than 120 characters)</li>
     *     <li>Valid titles with allowed characters</li>
     * </ul>
     */
    @Test
    public void testCheckValidTitle() {
        assertEquals("Too short! At least 5 characters.",
                     ModelMakePost.checkValidTitle("abc"));

        assertEquals("Too long! No more than 120 characters.",
                     ModelMakePost.checkValidTitle("a".repeat(121)));

        assertEquals("", ModelMakePost.checkValidTitle("Valid Title 123!"));
    }

    /**
     * ✅ Tests validation of post subtitles.
     *
     * <p>Checks for:</p>
     * <ul>
     *     <li>Subtitles exceeding 80 characters</li>
     *     <li>Subtitles with multiple lines</li>
     *     <li>Valid single-line subtitles</li>
     * </ul>
     */
    @Test
    public void testCheckValidSubtitle() {
        assertEquals("Too long! No more than 80 characters",
                     ModelMakePost.checkValidSubtitle("a".repeat(81)));

        assertEquals("No more than 1 line!",
                     ModelMakePost.checkValidSubtitle("Line1\nLine2"));

        assertEquals("", ModelMakePost.checkValidSubtitle("Valid Subtitle"));
    }

    /**
     * ✅ Tests validation of post content.
     *
     * <p>Checks for:</p>
     * <ul>
     *     <li>Too short content (less than 10 characters)</li>
     *     <li>Too long content (more than 2200 characters)</li>
     *     <li>Valid content of acceptable length</li>
     * </ul>
     */
    @Test
    public void testCheckValidContent() {
        assertEquals("Too short! At least 10 characters.",
                     ModelMakePost.checkValidContent("short"));

        assertEquals("Too long! No more than 2200 characters.",
                     ModelMakePost.checkValidContent("a".repeat(2201)));

        assertEquals("", ModelMakePost.checkValidContent("This is valid content."));
    }

    /**
     * ✅ Tests validation of post tags.
     *
     * <p>Checks for:</p>
     * <ul>
     *     <li>Too short tags (less than 2 characters)</li>
     *     <li>Too long tags (more than 15 characters)</li>
     *     <li>Invalid characters in tags</li>
     *     <li>Valid tags with only letters</li>
     * </ul>
     */
    @Test
    public void testCheckValidTag() {
        assertEquals("Too short! At least 2 characters.",
                     ModelMakePost.checkValidTag("a"));

        assertEquals("Too long! No more than 15 characters",
                     ModelMakePost.checkValidTag("a".repeat(16)));

        assertEquals("Invalid Input",
                     ModelMakePost.checkValidTag("tag123")); // numbers are not allowed

        assertEquals("", ModelMakePost.checkValidTag("ValidTag"));
    }
}
