package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;

import database.Database;

/**
 * <p>Title: ThreadCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD operations on the Threads table.
 * Ensures that threads can be added, retrieved, updated, and deleted correctly.
 * Each test ensures a clean database state by preparing/deleting test threads as needed.
 * </p>
 *
 * @version 1.0
 */
public class ThreadCRUDTests {

    private Database dbHelper;
    private static final String TEST_THREAD = "TestThread";

    /**
     * <p>Method: cleanupBefore()</p>
     * <p>Description: Prepares the database before each test. Ensures a clean state by
     * connecting to the database and deleting any thread with the test name.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void cleanupBefore() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();

        // Delete any existing test thread
        int threadId = dbHelper.getThreadId(TEST_THREAD);
        if (threadId != -1) {
            dbHelper.deleteThread(threadId);
        }
    }

    /**
     * <p>Method: testAddThread()</p>
     * <p>Description: Tests adding a thread to the database using {@code addThread()}.
     * Asserts that the thread exists after insertion.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testAddThread() throws SQLException {
        dbHelper.addThread(TEST_THREAD);
        assertTrue(dbHelper.existsThreadName(TEST_THREAD), "Thread should exist after insertion");
    }

    /**
     * <p>Method: testGetAllThreads()</p>
     * <p>Description: Tests retrieving all threads using {@code getAllThreads()}.
     * Asserts that the list contains the test thread after insertion.</p>
     */
    @Test
    void testGetAllThreads() {
        dbHelper.addThread(TEST_THREAD);
        ArrayList<String> threads = dbHelper.getAllThreads();
        assertTrue(threads.contains(TEST_THREAD), "All threads should contain the test thread");
    }

    /**
     * <p>Method: testGetThreadIdAndName()</p>
     * <p>Description: Tests retrieving a thread ID by name and retrieving a thread name by ID.
     * Asserts that the ID and name match the inserted thread.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testGetThreadIdAndName() throws SQLException {
        dbHelper.addThread(TEST_THREAD);
        int id = dbHelper.getThreadId(TEST_THREAD);
        assertTrue(id > 0, "Thread ID should be greater than zero");

        String name = dbHelper.getThreadName(id);
        assertEquals(TEST_THREAD, name, "Thread name retrieved by ID should match inserted name");
    }

    /**
     * <p>Method: testUpdateThreadName()</p>
     * <p>Description: Tests updating a thread name using {@code updateThreadName()}.
     * Asserts that the new name exists and the old name no longer exists.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testUpdateThreadName() throws SQLException {
        dbHelper.addThread(TEST_THREAD);
        int id = dbHelper.getThreadId(TEST_THREAD);

        String newName = TEST_THREAD + "_Updated";
        dbHelper.updateThreadName(id, newName);

        assertTrue(dbHelper.existsThreadName(newName), "Updated thread name should exist");
        assertFalse(dbHelper.existsThreadName(TEST_THREAD), "Old thread name should no longer exist");
    }

    /**
     * <p>Method: testDeleteThread()</p>
     * <p>Description: Tests deleting a thread using {@code deleteThread()}.
     * Asserts that the thread no longer exists after deletion.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testDeleteThread() throws SQLException {
        dbHelper.addThread(TEST_THREAD);
        int id = dbHelper.getThreadId(TEST_THREAD);

        dbHelper.deleteThread(id);
        assertFalse(dbHelper.existsThreadName(TEST_THREAD), "Thread should not exist after deletion");
    }
}
