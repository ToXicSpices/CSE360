package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.Post;
import entityClasses.StudentStatus;
import entityClasses.User;
import jUnitTestCodes.StatusRetriever;

public class StatusRetrieverTests {

    /** Test username for student */
    private static final String TEST_USER = "testStudent";

    /** Database helper instance */
    private static Database dbHelper;
    
    /** Status Retriever instance */

    /** Valid posts for testing */
    private Post post1, post2;

    /** Unique titles to prevent unique constraint violations */
    private String title1, title2;

    /** 
     * <p>Method: setupDatabase()</p>
     * <p>Description: Connects to the database, ensures test user exists,
     * inserts a test thread, cleans old posts/status, and inserts test posts.</p>
     * 
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setupDatabase() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();
        @SuppressWarnings("unused")
		StatusRetriever sr = new StatusRetriever(dbHelper);

        // Ensure test user exists
        if (!dbHelper.doesUserExist(TEST_USER)) {
            dbHelper.register(new User(
                TEST_USER,
                "password123",
                "Test",
                "T",
                "User",
                "Tester",
                "test@example.com",
                false,
                true,
                false
            ));
        }

        // Ensure a test thread exists (for foreign key)
        if (!dbHelper.existsThreadName("General")) {
            dbHelper.addThread("General");
        }

        // Clean previous student status
        dbHelper.deleteStudentStatus(TEST_USER);

        // Delete previous posts by this user
        ArrayList<Post> oldPosts = dbHelper.getPostsFromUserNewestFirst(TEST_USER);
        for (Post p : oldPosts) {
            dbHelper.deletePost(p.getPostId());
        }

        // Unique titles
        long timestamp = System.currentTimeMillis();
        title1 = "Post1_" + timestamp;
        title2 = "Post2_" + timestamp;

        // Insert test posts
        ArrayList<String> tags1 = new ArrayList<>();
        tags1.add("Java");
        ArrayList<String> tags2 = new ArrayList<>();
        tags2.add("JUnit");

        post1 = new Post(title1, "Subtitle1", "Content1", TEST_USER, tags1, "General");
        post2 = new Post(title2, "Subtitle2", "Content2", TEST_USER, tags2, "General");

        dbHelper.makePost(post1);
        dbHelper.makePost(post2);
    }

    /** 
     * <p>Method: cleanupDatabase()</p>
     * <p>Description: Cleans all test posts and student status after each test.</p>
     * 
     * @throws SQLException if a database error occurs
     */
    @AfterEach
    void cleanupDatabase() throws SQLException {
        dbHelper.deleteStudentStatus(TEST_USER);

        // Delete test posts
        ArrayList<Post> oldPosts = dbHelper.getPostsFromUserNewestFirst(TEST_USER);
        for (Post p : oldPosts) {
            dbHelper.deletePost(p.getPostId());
        }
    }

    /** 
     * <p>Method: testTotalPosts()</p>
     * <p>Description: Verifies getStudentTotalPosts returns correct post count.</p>
     */
    @Test
    void testTotalPosts() {
        assertEquals(2, StatusRetriever.getStudentTotalPosts(TEST_USER));
    }

    /** 
     * <p>Method: testTotalRepliesZero()</p>
     * <p>Description: Tests getStudentTotalReplies returns 0 for new posts.</p>
     */
    @Test
    void testTotalRepliesZero() {
        assertEquals(0, StatusRetriever.getStudentTotalReplies(TEST_USER));
    }

    /** 
     * <p>Method: testTotalRepliesGotZero()</p>
     * <p>Description: Tests getStudentTotalRepliesGot returns 0 if no replies exist.</p>
     */
    @Test
    void testTotalRepliesGotZero() {
        assertEquals(0, StatusRetriever.getStudentTotalRepliesGot(TEST_USER));
    }

    /** 
     * <p>Method: testTotalUpvotesGotZero()</p>
     * <p>Description: Tests getStudentTotalUpvotesGot returns 0 if no upvotes exist.</p>
     */
    @Test
    void testTotalUpvotesGotZero() {
        assertEquals(0, StatusRetriever.getStudentTotalUpvotesGot(TEST_USER));
    }

    /** 
     * <p>Method: testTotalViewsGotZero()</p>
     * <p>Description: Tests getStudentTotalViewsGot returns 0 if no views exist.</p>
     */
    @Test
    void testTotalViewsGotZero() {
        assertEquals(0, StatusRetriever.getStudentTotalViewsGot(TEST_USER));
    }

    /** 
     * <p>Method: testParticipationMaxLimits()</p>
     * <p>Description: Tests getParticipation max caps at 5 posts and 5 replies, weighted calculation.</p>
     */
    @Test
    void testParticipationMaxLimits() {
        StudentStatus s = new StudentStatus(TEST_USER, 10, 8, 0, 0, 0, 0, 0);
        double participation = StatusRetriever.getParticipation(s);
        double expected = (5.0/5*0.6 + 5.0/5*0.4)*100;
        assertEquals(expected, participation, 0.01);
    }

    /** 
     * <p>Method: testParticipationNormal()</p>
     * <p>Description: Tests getParticipation with normal values.</p>
     */
    @Test
    void testParticipationNormal() {
        StudentStatus s = new StudentStatus(TEST_USER, 3, 4, 0, 0, 0, 0, 0);
        double participation = StatusRetriever.getParticipation(s);
        double expected = (3.0/5*0.6 + 4.0/5*0.4)*100;
        assertEquals(expected, participation, 0.01);
    }

    /** 
     * <p>Method: testPerformanceZeroPosts()</p>
     * <p>Description: Tests getPerformance handles 0 posts (avoid division by zero).</p>
     */
    @Test
    void testPerformanceZeroPosts() {
        StudentStatus s = new StudentStatus(TEST_USER, 0, 0, 0, 0, 0, 0, 0);
        double performance = StatusRetriever.getPerformance(s);
        assertEquals(0, performance, 0.01);
    }

    /** 
     * <p>Method: testPerformanceExcessRepliesViewsUpvotes()</p>
     * <p>Description: Tests getPerformance handles ratios above 1 correctly (extra credit adjustments).</p>
     */
    @Test
    void testPerformanceExcessRepliesViewsUpvotes() {
        StudentStatus s = new StudentStatus(TEST_USER, 2, 0, 4, 6, 5, 2, 1);
        double performance = StatusRetriever.getPerformance(s);
        assertTrue(performance > 108.9 && performance < 109.1);
    }

    /** 
     * <p>Method: testTotalGradeCalculation()</p>
     * <p>Description: Verifies getTotalGrade calculation for combination of participation and performance.</p>
     */
    @Test
    void testTotalGradeCalculation() {
        double grade = StatusRetriever.getTotalGrade(80, 90);
        double expected = 80*0.8 + 90*0.2;
        assertEquals(expected, grade, 0.01);
    }

    /** 
     * <p>Method: testGradeMarkBoundaries()</p>
     * <p>Description: Tests getGradeMark boundary values for all letter grades.</p>
     */
    @Test
    void testGradeMarkBoundaries() {
        assertEquals("A+", StatusRetriever.getGradeMark(97));
        assertEquals("A", StatusRetriever.getGradeMark(93));
        assertEquals("A-", StatusRetriever.getGradeMark(90));
        assertEquals("B+", StatusRetriever.getGradeMark(87));
        assertEquals("B", StatusRetriever.getGradeMark(83));
        assertEquals("B-", StatusRetriever.getGradeMark(80));
        assertEquals("C+", StatusRetriever.getGradeMark(77));
        assertEquals("C", StatusRetriever.getGradeMark(73));
        assertEquals("C-", StatusRetriever.getGradeMark(70));
        assertEquals("D", StatusRetriever.getGradeMark(60));
        assertEquals("F", StatusRetriever.getGradeMark(50));
    }

    /** 
     * <p>Method: testPerformRefreshStatus()</p>
     * <p>Description: Verifies that performRefreshStatus updates the StudentStatus correctly.</p>
     */
    @Test
    void testPerformRefreshStatus() {
        StudentStatus s = new StudentStatus(TEST_USER, 0, 0, 0, 0, 0, 0, 0);
        StatusRetriever.performRefreshStatus(s);
        assertEquals(2, s.getPostNumber());
        assertEquals(0, s.getReplyNumber());
        assertEquals(0, s.getViewReceived());
        assertEquals(0, s.getReplyReceived());
        assertEquals(0, s.getUpvoteReceived());
    }
}
