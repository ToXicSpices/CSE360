package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;

import entityClasses.Post;
import entityClasses.Reply;

/**
 * <p>Title: ReplyCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD (Create, Read, Update, Delete) operations 
 * on replies in the database. This class ensures that reply-related database operations
 * work correctly and satisfy the student user stories for reply management.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared afterwards.</p>
 *
 * @version 2.0 (JUnit 5 Update)
 */
public class ReplyCRUDTests {

    /**
     * <p>Field: dbHelper</p>
     * <p>Description: Helper object used to interact with the database during tests.
     * A fresh instance is initialized in {@link #setup()} before each test execution.</p>
     */
    private Database dbHelper = applicationMain.FoundationsMain.database;

    /**
     * <p>Field: TEST_USERNAME</p>
     * <p>Description: The username used for all test replies created in this test class.
     * Ensures consistency when verifying user-specific queries.</p>
     */
    private final String TEST_USERNAME = "username1";

    /**
     * <p>Field: test_postId</p>
     * <p>Description: The ID of the post to which replies will be associated during tests.</p>
     */
    private int test_postId = 0;

    /**
     * <p>Constructor: ReplyCRUDTests()</p>
     * <p>Description: Default constructor used by JUnit to create a new test instance
     * for each test execution.</p>
     */
    public ReplyCRUDTests() {
        // No setup needed here, @BeforeEach handles database prep
    }

    /**
     * <p>Method: setup()</p>
     * <p>Description: Initializes a fresh database state before each test. Connects to
     * the database, adds a thread, and inserts a test post to which replies can be linked.
     * Ensures tests do not interfere with each other.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    public void setup() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();
        dbHelper.addThread("General");

        Post test_post = new Post("Post", "Subtitle", "Content",
               "Post Owner", new ArrayList<>(), "General");
        test_postId = dbHelper.makePost(test_post);
    }

    /**
     * <p>Method: teardown()</p>
     * <p>Description: Cleans up the database after each test to prevent cross-test interference.
     * Ensures that each test starts with a clean state.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @AfterEach
    public void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    /**
     * <p>Test Method: testCreateReply()</p>
     * <p>Description: Tests creating a reply in the database. Verifies that the reply receives a valid ID
     * and that all fields (content, owner, and associated post ID) match the inserted values.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testCreateReply() throws SQLException {
        Reply testReply = new Reply(
            "This is test content for the reply",
            TEST_USERNAME,
            test_postId
        );

        int replyId = dbHelper.makeReply(testReply);

        assertTrue(replyId > 0);
        assertEquals("This is test content for the reply", dbHelper.getReplyContent(replyId));
        assertEquals(TEST_USERNAME, dbHelper.getReplyOwnerUsername(replyId));
        assertEquals(test_postId, dbHelper.getReplyPostId(replyId));
    }

    /**
     * <p>Test Method: testReadReplies()</p>
     * <p>Description: Tests retrieval of replies associated with a specific post.
     * Ensures that multiple replies can be fetched and all expected replies are present.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testReadReplies() throws SQLException {
        Reply reply1 = new Reply("Content 1", TEST_USERNAME, test_postId);
        Reply reply2 = new Reply("Content 2", TEST_USERNAME, test_postId);

        dbHelper.makeReply(reply1);
        dbHelper.makeReply(reply2);

        ArrayList<Reply> allReplies = dbHelper.getRepliesByPostId(test_postId);

        assertFalse(allReplies.isEmpty());

        boolean foundFirst = false, foundSecond = false;
        for (Reply reply : allReplies) {
            if (reply.getContent().equals("Content 1")) foundFirst = true;
            if (reply.getContent().equals("Content 2")) foundSecond = true;
        }

        assertTrue(foundFirst);
        assertTrue(foundSecond);
    }

    /**
     * <p>Test Method: testUpdateReply()</p>
     * <p>Description: Tests updating the content of a reply. Verifies that changes
     * are persisted in the database and can be retrieved correctly.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testUpdateReply() throws SQLException {
        Reply originalReply = new Reply("Original Content", TEST_USERNAME, test_postId);
        int replyId = dbHelper.makeReply(originalReply);

        dbHelper.updateReplyContent(replyId, "Updated Content");

        assertEquals("Updated Content", dbHelper.getReplyContent(replyId));
    }

    /**
     * <p>Test Method: testDeleteReply()</p>
     * <p>Description: Tests deleting a reply from the database. Confirms that
     * the reply no longer exists and cannot be retrieved after deletion.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testDeleteReply() throws SQLException {
        Reply testReply = new Reply("Reply to Delete", TEST_USERNAME, test_postId);
        int replyId = dbHelper.makeReply(testReply);

        assertTrue(dbHelper.doesReplyExistByContent("Reply to Delete"));

        dbHelper.deleteReply(replyId);

        assertFalse(dbHelper.doesReplyExistByContent("Reply to Delete"));
        assertNull(dbHelper.getReplyContent(replyId));
    }
}
