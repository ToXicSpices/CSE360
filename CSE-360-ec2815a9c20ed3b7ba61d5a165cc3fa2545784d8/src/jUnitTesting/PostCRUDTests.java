package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import entityClasses.Post;

/**
 * Test class focused on Post CRUD (Create, Read, Update, Delete) operations.
 *
 * <p>This class validates that all post-related database operations work correctly,
 * including creation, retrieval, updating, and deletion of posts. These tests
 * ensure the Student User Stories for post management are properly implemented.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared afterwards.</p>
 *
 * @version 1.0
 */
public class PostCRUDTests {

    /**
     * Helper object to interact with the database during tests.
     * A fresh instance is initialized in {@link #setup()}.
     */
    private Database dbHelper = applicationMain.FoundationsMain.database;

    /**
     * Username used for test posts created in this class.
     */
    private final String TEST_USERNAME = "username1";

    /**
     * Default constructor for PostTests.
     * JUnit uses this constructor to create a new test instance for each test execution.
     */
    public PostCRUDTests() {}

    /**
     * Sets up a fresh database state before each test.
     *
     * @throws SQLException if the database setup fails
     */
    @BeforeEach
    public void setup() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();
        dbHelper.addThread("General");
        dbHelper.addThread("Project");
    }

    /**
     * Cleans the database after each test to prevent cross-test interference.
     *
     * @throws SQLException if clearing the database fails
     */
    @AfterEach
    public void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    /**
     * ✅ Tests creating and retrieving a post from the database.
     *
     * <p>Ensures that:</p>
     * <ul>
     *     <li>The post receives a valid ID</li>
     *     <li>Inserted values (title, content, tags, owner) match retrieved values</li>
     * </ul>
     *
     * @throws SQLException if database access fails
     */
    @Test
    public void testCreatePost() throws SQLException {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("java");
        tags.add("programming");

        Post testPost = new Post(
            "Test Post Title",
            "Test Subtitle",
            "This is test content for the post",
            TEST_USERNAME,
            tags,
            "General"
        );

        int postId = dbHelper.makePost(testPost);

        assertTrue(postId > 0);
        assertTrue(dbHelper.doesPostExistByTitle("Test Post Title"));

        assertEquals("Test Post Title", dbHelper.getPostTitle(postId));
        assertEquals("Test Subtitle", dbHelper.getPostSubtitle(postId));
        assertEquals("This is test content for the post", dbHelper.getPostContent(postId));
        assertEquals(TEST_USERNAME, dbHelper.getPostOwnerUsername(postId));
        assertEquals("General", dbHelper.getPostThread(postId));

        ArrayList<String> retrievedTags = dbHelper.getPostTags(postId);
        assertTrue(retrievedTags.contains("java"));
        assertTrue(retrievedTags.contains("programming"));
    }

    /**
     * ✅ Tests retrieval of multiple posts from different queries.
     *
     * <p>Checks:</p>
     * <ul>
     *     <li>Fetching posts newest-first</li>
     *     <li>Fetching posts created by a specific user</li>
     * </ul>
     *
     * @throws SQLException if database access fails
     */
    @Test
    public void testReadPosts() throws SQLException {
        Post post1 = new Post("First Post", "Subtitle 1", "Content 1",
                              TEST_USERNAME, new ArrayList<>(), "General");
        Post post2 = new Post("Second Post", "Subtitle 2", "Content 2",
                              TEST_USERNAME, new ArrayList<>(), "General");

        dbHelper.makePost(post1);
        dbHelper.makePost(post2);

        ArrayList<Post> allPosts = dbHelper.getAllPostsNewestFirst();
        ArrayList<Post> userPosts = dbHelper.getPostsFromUserNewestFirst(TEST_USERNAME);

        assertFalse(allPosts.isEmpty());
        assertFalse(userPosts.isEmpty());

        boolean foundFirst = false, foundSecond = false;
        for (Post post : allPosts) {
            if (post.getTitle().equals("Second Post")) {
                assertFalse(foundFirst);
                foundSecond = true;
            } else if (post.getTitle().equals("First Post")) {
                foundFirst = true;
            }
        }
        assertTrue(foundSecond);
    }

    /**
     * ✅ Tests updating post fields (title, subtitle, content, and tags).
     *
     * @throws SQLException if an update query fails
     */
    @Test
    public void testUpdatePost() throws SQLException {
        Post originalPost = new Post("Original Title", "Original Subtitle",
                                     "Original Content", TEST_USERNAME,
                                     new ArrayList<>(), "General");
        int postId = dbHelper.makePost(originalPost);

        dbHelper.updatePostTitle(postId, "Updated Title");
        dbHelper.updatePostSubitle(postId, "Updated Subtitle");
        dbHelper.updatePostContent(postId, "Updated Content");
        dbHelper.addPostTag(postId, "newtag");

        assertEquals("Updated Title", dbHelper.getPostTitle(postId));
        assertEquals("Updated Subtitle", dbHelper.getPostSubtitle(postId));
        assertEquals("Updated Content", dbHelper.getPostContent(postId));

        ArrayList<String> tags = dbHelper.getPostTags(postId);
        assertTrue(tags.contains("newtag"));

        dbHelper.removePostTag(postId, "newtag");
        tags = dbHelper.getPostTags(postId);
        assertFalse(tags.contains("newtag"));
    }

    /**
     * ✅ Tests deleting a post and validating it no longer exists in the database.
     *
     * @throws SQLException if deletion fails
     */
    @Test
    public void testDeletePost() throws SQLException {
        Post testPost = new Post("Post to Delete", "Subtitle", "Content",
                                 TEST_USERNAME, new ArrayList<>(), "General");
        int postId = dbHelper.makePost(testPost);

        assertTrue(dbHelper.doesPostExistByTitle("Post to Delete"));

        dbHelper.deletePost(postId);

        assertFalse(dbHelper.doesPostExistByTitle("Post to Delete"));
        assertNull(dbHelper.getPostTitle(postId));
    }
}
