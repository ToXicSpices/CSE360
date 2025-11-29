package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.ArrayList;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Request;
import entityClasses.User;

/**
 * <p>Title: RequestCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD (Create, Read, Update, Delete) operations
 * on system requests in the database. This class ensures that request-related database
 * operations work correctly and satisfy platform requirements.</p>
 *
 * <p>Each test is isolated: the database is set up before each test and cleared afterwards.</p>
 *
 * @version 1.0
 */
public class RequestCRUDTests {

    /**
     * <p>Field: dbHelper</p>
     * <p>Description: Helper object used to interact with the database during tests.
     * A fresh instance is initialized before each test execution.</p>
     */
    private Database dbHelper = FoundationsMain.database;

    /**
     * <p>Field: TEST_USER</p>
     * <p>Description: Username used as the requester for test requests.</p>
     */
    private static final String TEST_USER = "testuser";

    /**
     * <p>Field: TEST_TITLE</p>
     * <p>Description: Title used for test requests.</p>
     */
    private static final String TEST_TITLE = "Test Request";

    /**
     * <p>Method: cleanupBefore()</p>
     * <p>Description: Prepares the database before each test. Ensures a clean state by
     * connecting to the database and deleting any request with the test title.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void cleanupBefore() throws SQLException {
    	dbHelper = new Database();
        dbHelper.connectToDatabase();

        // Ensure test user exists for foreign key
        if (!dbHelper.doesUserExist(TEST_USER)) {
            dbHelper.register(new User(
                TEST_USER,
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

        // Clean up any existing test request
        Request existing = dbHelper.getRequestByTitle(TEST_TITLE);
        if (existing != null) {
            dbHelper.deleteRequest(existing);
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
    void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    /**
     * <p>Test Method: testCreateRequest()</p>
     * <p>Description: Tests creating a new request. Verifies that the request is inserted
     * into the database and receives a valid ID.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testCreateRequest() throws SQLException {
        Request request = new Request(TEST_TITLE, "This is a test request", TEST_USER, false);
        int id = dbHelper.makeRequest(request);

        assertTrue(id > 0);
        Request fetched = dbHelper.getRequestByTitle(TEST_TITLE);
        assertNotNull(fetched);
        assertEquals(TEST_TITLE, fetched.getTitle());
        assertEquals(TEST_USER, fetched.getRequester());
    }

    /**
     * <p>Test Method: testReadAllRequests()</p>
     * <p>Description: Tests retrieving all requests from the database. Verifies that
     * a newly created request appears in the list.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testReadAllRequests() throws SQLException {
        Request request = new Request(TEST_TITLE, "Content for reading", TEST_USER, false);
        dbHelper.makeRequest(request);

        ArrayList<Request> requests = dbHelper.getAllRequests();
        assertFalse(requests.isEmpty());
        assertTrue(requests.stream().anyMatch(r -> r.getTitle().equals(TEST_TITLE)));
    }

    /**
     * <p>Test Method: testUpdateRequestTitle()</p>
     * <p>Description: Tests updating the title of an existing request. Verifies
     * that the title change is persisted in the database.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testUpdateRequestTitle() throws SQLException {
        Request request = new Request(TEST_TITLE, "Content", TEST_USER, false);
        dbHelper.makeRequest(request);

        String newTitle = "Updated Title";
        dbHelper.updateRequestTitle(request, newTitle);

        Request updated = dbHelper.getRequestByTitle(newTitle);
        assertNotNull(updated);
        assertEquals(newTitle, updated.getTitle());
    }

    /**
     * <p>Test Method: testUpdateRequestContent()</p>
     * <p>Description: Tests updating the content of an existing request. Verifies
     * that the content change is persisted in the database.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testUpdateRequestContent() throws SQLException {
        Request request = new Request(TEST_TITLE, "Old Content", TEST_USER, false);
        dbHelper.makeRequest(request);

        String newContent = "Updated Content";
        dbHelper.updateRequestContent(request, newContent);

        Request updated = dbHelper.getRequestByTitle(TEST_TITLE);
        assertEquals(newContent, updated.getContent());
    }

    /**
     * <p>Test Method: testUpdateRequestChecked()</p>
     * <p>Description: Tests updating the checked status of an existing request. Verifies
     * that the status change is persisted in the database.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testUpdateRequestChecked() throws SQLException {
        Request request = new Request(TEST_TITLE, "Content", TEST_USER, false);
        dbHelper.makeRequest(request);

        dbHelper.updateRequestChecked(request, true);

        Request updated = dbHelper.getRequestByTitle(TEST_TITLE);
        assertTrue(updated.isChecked());
    }

    /**
     * <p>Test Method: testDeleteRequest()</p>
     * <p>Description: Tests deleting a request from the database. Verifies that
     * the request no longer exists after deletion.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDeleteRequest() throws SQLException {
        Request request = new Request(TEST_TITLE, "Content", TEST_USER, false);
        dbHelper.makeRequest(request);

        dbHelper.deleteRequest(request);

        Request deleted = dbHelper.getRequestByTitle(TEST_TITLE);
        assertNull(deleted);
    }
}
