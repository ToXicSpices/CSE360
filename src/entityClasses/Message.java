package entityClasses;

/**
 * <p>Title: Message Class</p>
 *
 * <p>Description:
 * Represents a message sent between users in the educational platform.
 * Each {@code Message} contains sender and receiver information, a subject line,
 * message content, a unique identifier, and a read/unread status.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores metadata and content for messages</li>
 *     <li>Tracks whether a message has been read</li>
 *     <li>Provides getters and setters for all fields to support database operations</li>
 * </ul>
 *
 * <p>Note: Validation and permission checking should be handled by higher-level services or controllers.</p>
 * 
 * @author
 *     Educational Platform Development Team
 * @version 1.0
 * @since 2024-01-01
 */
public class Message {

    /** <p>Description: Unique identifier of the message, typically assigned by the database.</p> */
    private int id;

    /** <p>Description: Username of the sender.</p> */
    private String sender;

    /** <p>Description: Username of the receiver.</p> */
    private String receiver;

    /** <p>Description: Subject line of the message.</p> */
    private String subject;

    /** <p>Description: Main body content of the message.</p> */
    private String content;

    /** <p>Description: Flag indicating whether the message has been read.</p> */
    private boolean isRead;

    /**
     * <p>Method: Message(int id, String sender, String receiver, String subject, String content, boolean isRead)</p>
     *
     * <p>Description:
     * Constructs a {@code Message} object with all fields initialized.
     * </p>
     *
     * @param id unique identifier of the message
     * @param sender username of the sender
     * @param receiver username of the receiver
     * @param subject subject line of the message
     * @param content body content of the message
     * @param isRead read status of the message
     */
    public Message(int id, String sender, String receiver, String subject, String content, boolean isRead) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.isRead = isRead;
    }

    /**
     * <p>Method: Message(String sender, String receiver, String subject, String content)</p>
     *
     * <p>Description:
     * Constructs a {@code Message} object with default ID (-1) and unread status (false).
     * </p>
     *
     * @param sender username of the sender
     * @param receiver username of the receiver
     * @param subject subject line of the message
     * @param content body content of the message
     */
    public Message(String sender, String receiver, String subject, String content) {
        this(-1, sender, receiver, subject, content, false);
    }

    // ------------------- GETTERS -------------------

    /**
     * <p>Method: getId()</p>
     *
     * <p>Description:
     * Retrieves the unique identifier of the message.
     * </p>
     *
     * @return message ID
     */
    public int getId() { return id; }

    /**
     * <p>Method: getSender()</p>
     *
     * <p>Description:
     * Retrieves the username of the sender.
     * </p>
     *
     * @return sender username
     */
    public String getSender() { return sender; }

    /**
     * <p>Method: getReceiver()</p>
     *
     * <p>Description:
     * Retrieves the username of the receiver.
     * </p>
     *
     * @return receiver username
     */
    public String getReceiver() { return receiver; }

    /**
     * <p>Method: getSubject()</p>
     *
     * <p>Description:
     * Retrieves the subject line of the message.
     * </p>
     *
     * @return subject text
     */
    public String getSubject() { return subject; }

    /**
     * <p>Method: getContent()</p>
     *
     * <p>Description:
     * Retrieves the body content of the message.
     * </p>
     *
     * @return message content
     */
    public String getContent() { return content; }

    /**
     * <p>Method: isRead()</p>
     *
     * <p>Description:
     * Checks whether the message has been read.
     * </p>
     *
     * @return true if the message is read, false otherwise
     */
    public boolean isRead() { return isRead; }

    // ------------------- SETTERS -------------------

    /**
     * <p>Method: setId(int id)</p>
     *
     * <p>Description:
     * Sets the unique identifier of the message.
     * </p>
     *
     * @param id the message ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * <p>Method: setRead(boolean read)</p>
     *
     * <p>Description:
     * Sets the read/unread status of the message.
     * </p>
     *
     * @param read new read status
     */
    public void setRead(boolean read) { this.isRead = read; }
}
