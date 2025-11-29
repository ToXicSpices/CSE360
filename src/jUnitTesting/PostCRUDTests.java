package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import database.Database;
import java.sql.SQLException;
import java.util.ArrayList;
import entityClasses.Post;

/**
 * <p>Title: PostCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD operations (Create, Read, Update, Delete) 
 * on posts in the database. This class ensures that post-related database operations
 * are functioning according to the application requirements.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared afterwards.</p>
 */
public class PostCRUDTests {

    /**
     * <p>Field: dbHelper</p>
     * <p>Description: Helper object used to interact with the database during tests.
     * A fresh instance is initialized in {@link #setup()} before each test execution.</p>
     */
    private Database dbHelper = applicationMain.FoundationsMain.database;

    /**
     * <p>Field: TEST_USERNAME</p>
     * <p>Description: The username used for all test posts created in this test class.
     * Ensures consistency when verifying user-specific queries.</p>
     */
    private final String TEST_USERNAME = "username1";

    /**
     * <p>Constructor: PostCRUDTests()</p>
     * <p>Description: Default constructor used by JUnit to create a new test instance
     * for each test execution.</p>
     */
    public PostCRUDTests() {}

    /**
     * <p>Method: setup()</p>
     * <p>Description: Initializes a fresh database state before each test, including
     * connecting to the database and creating required threads (e.g., "General", "Project").
     * Ensures tests do not interfere with each other.</p>
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
     * <p>Method: teardown()</p>
     * <p>Description: Cleans up the database after each test to prevent cross-test interference.
     * Ensures that each test starts with a clean state.</p>
     *
     * @throws SQLException if clearing the database fails
     */
    @AfterEach
    public void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    /**
     * <p>Test Method: testCreatePost()</p>
     * <p>Description: Tests creating a post in the database and retrieving it. 
     * Verifies that the post receives a valid ID and that all fields (title, subtitle,
     * content, owner, thread, and tags) match the inserted values.</p>
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
     * <p>Test Method: testReadPosts()</p>
     * <p>Description: Tests retrieval of multiple posts. Verifies that posts are correctly
     * fetched newest-first and by a specific user, ensuring the query results are accurate
     * and ordered as expected.</p>
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
                foundSecond = true;
            } else if (post.getTitle().equals("First Post")) {
                foundFirst = true;
            }
        }
        assertTrue(foundFirst && foundSecond);
    }

    /**
     * <p>Test Method: testUpdatePost()</p>
     * <p>Description: Tests updating a post's fields (title, subtitle, content, and tags)
     * and verifies that changes are reflected in the database. Also checks that added tags
     * can be removed successfully.</p>
     *
     * @throws SQLException if database access fails
     */
    @Test
    public void testUpdatePost() throws SQLException {
        Post originalPost = new Post("Original Title", "Original Subtitle",
                                     "Original Content", TEST_USERNAME,
                                     new ArrayList<>(), "General");
        int postId = dbHelper.makePost(originalPost);

        dbHelper.updatePostTitle(postId, "Updated Title");
        dbHelper.updatePostSubtitle(postId, "Updated Subtitle");
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
     * <p>Test Method: testDeletePost()</p>
     * <p>Description: Tests deleting a post and verifies that it no longer exists in the
     * database. Ensures that queries on the deleted post return null or indicate absence.</p>
     *
     * @throws SQLException if database access fails
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
