package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.StudentStatus;
import entityClasses.User;

/**
 * <p>Title: StatusCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD (Create, Read, Update, Delete) operations 
 * on the StudentStatus table in the database. Ensures that student status records
 * can be inserted, updated, retrieved, and deleted correctly.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleaned afterwards.</p>
 *
 * @version 1.0
 */
public class StatusCRUDTests {

    /**
     * <p>Field: dbHelper</p>
     * <p>Description: Helper object used to interact with the database during tests.
     * A fresh instance is initialized in {@link #setup()} before each test execution.</p>
     */
    private Database dbHelper = FoundationsMain.database;

    /**
     * <p>Field: TEST_USERNAME</p>
     * <p>Description: The username used for the test student. Ensures foreign key
     * constraints in StudentStatus table are satisfied.</p>
     */
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setup() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();

        // Ensure test user exists
        if (!dbHelper.doesUserExist(TEST_USERNAME)) {
            dbHelper.register(new User(
                    TEST_USERNAME,
                    "password123",
                    "Test",
                    "T",
                    "User",
                    "Tester",
                    "testuser@example.com",
                    false,
                    true,
                    false
            ));
        }

        // Clean StudentStatus before each test
        if (dbHelper.getStudentStatus(TEST_USERNAME) != null) {
            dbHelper.deleteStudentStatus(TEST_USERNAME);
        }
        dbHelper.insertStudentStatus(TEST_USERNAME);
    }

    @AfterEach
    void teardown() throws SQLException {
        // Clean up to ensure no leftover data
        dbHelper.deleteStudentStatus(TEST_USERNAME);
    }

    @Test
    void testInsertStudentStatus() throws SQLException {
        // Insert new status
        dbHelper.insertStudentStatus(TEST_USERNAME);

        StudentStatus status = dbHelper.getStudentStatus(TEST_USERNAME);
        assertNotNull(status);
        assertEquals(TEST_USERNAME, status.getUserName());
        assertEquals(0, status.getPostNumber());
        assertEquals(0, status.getReplyNumber());
        assertEquals(0, status.getViewReceived());
        assertEquals(0, status.getReplyReceived());
        assertEquals(0, status.getUpvoteReceived());
        assertEquals(0, status.getPromotion());
        assertEquals(0, status.getViolation());
    }

    @Test
    void testUpdateStudentStatus() throws SQLException {
        StudentStatus status = dbHelper.getStudentStatus(TEST_USERNAME);
        status.setPostNumber(5);
        status.setReplyNumber(3);
        status.setViewReceived(10);
        status.setReplyReceived(2);
        status.setUpvoteReceived(7);
        status.setPromotion(1);
        status.setViolation(0);

        dbHelper.updateStudentStatus(status);

        StudentStatus updated = dbHelper.getStudentStatus(TEST_USERNAME);
        assertEquals(5, updated.getPostNumber());
        assertEquals(3, updated.getReplyNumber());
        assertEquals(10, updated.getViewReceived());
        assertEquals(2, updated.getReplyReceived());
        assertEquals(7, updated.getUpvoteReceived());
        assertEquals(1, updated.getPromotion());
        assertEquals(0, updated.getViolation());
    }

    @Test
    void testGetStudentStatusCreatesDefault() throws SQLException {
        // Delete status manually if exists
        dbHelper.updateStudentStatus(new StudentStatus(TEST_USERNAME, 0,0,0,0,0,0,0));

        StudentStatus status = dbHelper.getStudentStatus(TEST_USERNAME);
        assertNotNull(status);
        assertEquals(TEST_USERNAME, status.getUserName());
        assertEquals(0, status.getPostNumber());
        assertEquals(0, status.getReplyNumber());
        assertEquals(0, status.getViewReceived());
    }
}
