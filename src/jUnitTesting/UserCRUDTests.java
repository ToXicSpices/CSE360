package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;

/**
 * <p>Title: UserCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD (Create, Read, Update, Delete) operations 
 * on users in the database. This class ensures that user-related database operations
 * work correctly and satisfy the student user stories for user management.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared afterwards.</p>
 *
 * @version 1.0
 */
class UserCRUDTests {

    /**
     * <p>Field: dbHelper</p>
     * <p>Description: Helper object used to interact with the database during tests.
     * A fresh instance is initialized and connected before each test execution.</p>
     */
    private Database dbHelper = FoundationsMain.database;

    /**
     * <p>Field: TEST_USERNAME</p>
     * <p>Description: The username used for all test users created in this test class.
     * Ensures consistency when verifying user-specific queries.</p>
     */
    private static final String TEST_USERNAME = "testuser";

    /**
     * <p>Field: TEST_EMAIL</p>
     * <p>Description: The email address used for all test users created in this test class.</p>
     */
    private static final String TEST_EMAIL = "testuser@example.com";

    /**
     * <p>Method: cleanupBefore()</p>
     * <p>Description: Prepares the database before each test. Ensures a clean state by
     * connecting to the database and removing any existing test user with the test username.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void cleanupBefore() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();
        if (dbHelper.doesUserExist(TEST_USERNAME)) {
            dbHelper.deleteUser(TEST_USERNAME);
        }
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
     * <p>Test Method: testCreateUser()</p>
     * <p>Description: Tests creating a user in the database. Verifies that the user
     * exists after registration by checking the username.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testCreateUser() throws SQLException {
        User user = new User(TEST_USERNAME, "password123", "Test", "T", "User",
                "Tester", TEST_EMAIL, true, true, false);

        dbHelper.register(user);

        assertTrue(dbHelper.doesUserExist(TEST_USERNAME));
    }

    /**
     * <p>Test Method: testReadUser()</p>
     * <p>Description: Tests retrieving users from the database. Verifies that the
     * test user appears in both the full list of User objects and the list of usernames.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testReadUser() throws SQLException {
        User user = new User(TEST_USERNAME, "password123", "Test", "T", "User",
                "Tester", TEST_EMAIL, true, true, false);
        dbHelper.register(user);

        List<User> allUsers = dbHelper.getAllUsers();
        assertTrue(allUsers.stream().anyMatch(u -> u.getUserName().equals(TEST_USERNAME)));

        List<String> usernames = dbHelper.getUserList();
        assertTrue(usernames.contains(TEST_USERNAME));
    }

    /**
     * <p>Test Method: testUpdateUserPassword()</p>
     * <p>Description: Tests updating a user's password. Verifies that the update
     * operation returns true indicating success.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testUpdateUserPassword() throws SQLException {
        User user = new User(TEST_USERNAME, "oldPass", "Test", "T", "User",
                "Tester", TEST_EMAIL, false, true, false);
        dbHelper.register(user);

        boolean updated = dbHelper.updatePassword(TEST_USERNAME, "newPass");
        assertTrue(updated);
    }

    /**
     * <p>Test Method: testDeleteUser()</p>
     * <p>Description: Tests deleting a user from the database. Verifies that
     * the user exists before deletion and does not exist afterwards.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDeleteUser() throws SQLException {
        User user = new User(TEST_USERNAME, "password123", "Test", "T", "User",
                "Tester", TEST_EMAIL, false, true, false);
        dbHelper.register(user);

        assertTrue(dbHelper.doesUserExist(TEST_USERNAME));

        dbHelper.deleteUser(TEST_USERNAME);

        assertFalse(dbHelper.doesUserExist(TEST_USERNAME));
    }
}
