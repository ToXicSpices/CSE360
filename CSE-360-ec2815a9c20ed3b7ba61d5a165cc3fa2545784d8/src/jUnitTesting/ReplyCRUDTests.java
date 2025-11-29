package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;

import entityClasses.Post;
import entityClasses.Reply;

/**
 * <p>Test class focused on Reply CRUD (Create, Read, Update, Delete) operations.</p>
 *
 * <p>This class validates that all reply-related database operations work correctly,
 * including creation, retrieval, updating, and deletion of replies. These tests
 * ensure that the student user stories for reply management are properly implemented.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared
 * afterwards to maintain consistency across test executions.</p>
 *
 * @version 2.0 (JUnit 5 Update)
 */
public class ReplyCRUDTests {


    /**
     * <p>Default constructor.</p>
     */
    public ReplyCRUDTests() {
        // No setup needed here, @BeforeEach handles database prep
    }

    /** Helper object used to interact with the application's database. */
    private Database dbHelper = applicationMain.FoundationsMain.database;

    /** Username used when creating test replies. */
    private final String TEST_USERNAME = "username1";

    /** ID of the post to which replies will be associated during tests. */
    private int test_postId = 0;

    /**
     * <p>Setup method executed before each test.</p>
     *
     * <p>Creates a fresh database instance, connects to it, adds the necessary
     * thread, and inserts a test post to which replies can be linked.</p>
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
     * <p>Teardown method executed after each test.</p>
     *
     * <p>Erases all stored database information so every test starts fresh.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @AfterEach
    public void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    /**
     * <p>Tests the creation of a reply.</p>
     *
     * <p>Verifies that a reply can be stored in the database correctly and that
     * its content, owner, and associated post can be retrieved accurately.</p>
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
     * <p>Tests retrieval of replies based on post ID.</p>
     *
     * <p>Ensures that multiple replies tied to a post can be retrieved and that
     * each expected reply is present in the returned list.</p>
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
     * <p>Tests updating a reply.</p>
     *
     * <p>Validates that edited reply content is correctly applied and retrieved.</p>
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
     * <p>Tests deletion of a reply.</p>
     *
     * <p>Confirms that the reply is removed from the database and cannot be
     * retrieved afterward.</p>
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
