package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Message;
import entityClasses.User;

/**
 * <p>Title: MessageCRUDTests</p>
 *
 * <p>Description:
 * JUnit 5 test class for performing CRUD operations on the Message table.
 * Verifies that messages can be inserted, retrieved, marked as read, and deleted correctly.
 * Each test ensures a clean database state by creating/deleting test users and messages as needed.
 * </p>
 *
 * @version 1.0
 */
public class MessageCRUDTests {

    private Database dbHelper = FoundationsMain.database;
    private static final String SENDER = "senderUser";
    private static final String RECEIVER = "receiverUser";

    /**
     * <p>Method: setupBeforeEach()</p>
     * <p>Description: Prepares the database before each test. Ensures test sender and receiver
     * users exist and deletes any pre-existing messages for the receiver.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setupBeforeEach() throws SQLException {
        dbHelper.connectToDatabase();

        // Ensure sender exists
        if (!dbHelper.doesUserExist(SENDER)) {
            dbHelper.register(new User(SENDER, "pass123", "Sender", "S", "User", "Test", "sender@example.com", false, true, false));
        }

        // Ensure receiver exists
        if (!dbHelper.doesUserExist(RECEIVER)) {
            dbHelper.register(new User(RECEIVER, "pass123", "Receiver", "R", "User", "Test", "receiver@example.com", false, true, false));
        }

        // Clean up existing messages
        List<Message> existing = dbHelper.getAllMessages(RECEIVER);
        for (Message msg : existing) {
            dbHelper.deleteMessage(msg);
        }
    }

    /**
     * <p>Method: teardownAfterEach()</p>
     * <p>Description: Cleans up the database after each test by deleting any messages
     * created for the receiver during the test.</p>
     *
     * @throws SQLException if a database access error occurs
     */
    @AfterEach
    void teardownAfterEach() throws SQLException {
        List<Message> msgs = dbHelper.getAllMessages(RECEIVER);
        for (Message msg : msgs) {
            dbHelper.deleteMessage(msg);
        }
    }

    /**
     * <p>Method: testMakeMessageAndGetMessage()</p>
     * <p>Description: Tests inserting a message using {@code makeMessage()} and retrieving
     * it by ID using {@code getMessage()}. Asserts that all fields match the inserted values.</p>
     */
    @Test
    void testMakeMessageAndGetMessage() {
        Message msg = new Message(SENDER, RECEIVER, "Test Subject", "This is a test message.");
        int msgId = dbHelper.makeMessage(msg);
        assertTrue(msgId > 0, "Message ID should be generated");

        Message fetched = dbHelper.getMessage(msgId);
        assertNotNull(fetched);
        assertEquals(SENDER, fetched.getSender());
        assertEquals(RECEIVER, fetched.getReceiver());
        assertEquals("Test Subject", fetched.getSubject());
        assertEquals("This is a test message.", fetched.getContent());
        assertFalse(fetched.isRead());
    }

    /**
     * <p>Method: testGetAllMessagesAndUnread()</p>
     * <p>Description: Tests retrieving all messages and all unread messages for a receiver.
     * Inserts two messages and asserts that {@code getAllMessages()} and
     * {@code getAllUnreadMessages()} return the correct counts.</p>
     */
    @Test
    void testGetAllMessagesAndUnread() {
        Message msg1 = new Message(SENDER, RECEIVER, "Hello 1", "Content 1");
        Message msg2 = new Message(SENDER, RECEIVER, "Hello 2", "Content 2");
        dbHelper.makeMessage(msg1);
        dbHelper.makeMessage(msg2);

        List<Message> all = dbHelper.getAllMessages(RECEIVER);
        assertEquals(2, all.size());

        List<Message> unread = dbHelper.getAllUnreadMessages(RECEIVER);
        assertEquals(2, unread.size());
    }

    /**
     * <p>Method: testMarkMessageAsRead()</p>
     * <p>Description: Tests marking a message as read. Inserts a new message, asserts that
     * it is initially unread, calls {@code markMessageAsRead()}, and verifies that
     * {@code isRead} is updated in the database.</p>
     */
    @Test
    void testMarkMessageAsRead() {
        Message msg = new Message(SENDER, RECEIVER, "Read Test", "Marking this message read");
        int msgId = dbHelper.makeMessage(msg);
        Message fetched = dbHelper.getMessage(msgId);

        assertFalse(fetched.isRead());
        dbHelper.markMessageAsRead(fetched);

        Message updated = dbHelper.getMessage(msgId);
        assertTrue(updated.isRead(), "Message should be marked as read");
    }

    /**
     * <p>Method: testDeleteMessage()</p>
     * <p>Description: Tests deleting a message from the database. Inserts a message,
     * verifies it exists, deletes it using {@code deleteMessage()}, and asserts
     * that it no longer exists.</p>
     */
    @Test
    void testDeleteMessage() {
        Message msg = new Message(SENDER, RECEIVER, "Delete Test", "This message will be deleted");
        int msgId = dbHelper.makeMessage(msg);

        Message fetched = dbHelper.getMessage(msgId);
        assertNotNull(fetched);

        dbHelper.deleteMessage(fetched);
        Message deleted = dbHelper.getMessage(msgId);
        assertNull(deleted, "Deleted message should not exist in database");
    }
}
