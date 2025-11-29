package entityClasses;

/**
 * <p>Title: Reply Class</p>
 *
 * <p>Description:
 * Represents a reply to a forum post within the educational platform. Each {@code Reply}
 * contains basic metadata including the content, creator, and reference to the parent post.
 * This class serves as a simple data model for CRUD operations handled elsewhere in the application.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores reply content and ownership details</li>
 *     <li>Associates the reply with a specific post</li>
 *     <li>Provides identifiers for database persistence</li>
 * </ul>
 *
 * <p>Note: Validation and permission checking must be enforced by higher layers.</p>
 *
 * @author
 *     Educational Platform Development Team
 * @version
 *     1.0
 * @since
 *     2024-01-01
 */
public class Reply {

    /** Main body content of the reply, required */
    private String content = null;

    /** Username of the reply creator */
    private String owner = null;

    /** Identifier of the post this reply belongs to */
    private int postId = -1;

    /** Unique identifier for the reply, assigned by database/system */
    private int replyId = -1;

    /**
     * Default constructor.
     * <p>
     * Intended for ORM or serialization use. Produces an incomplete {@code Reply} object,
     * so direct use in typical application logic is discouraged.
     * </p>
     */
    public Reply() {
        // Default constructor for framework use
    }

    /**
     * Constructs a {@code Reply} object with all required fields populated.
     *
     * @param c the content of the reply (must not be null or empty)
     * @param o the username of the reply owner/author
     * @param p the ID of the post that this reply is associated with
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Reply(String c, String o, int p) {
        this.content = c;
        this.owner = o;
        this.postId = p;
    }

    /**
     * Retrieves the reply content.
     * @return the content text of the reply
     */
    public String getContent() { return this.content; }

    /**
     * Retrieves the username of the reply owner.
     * @return owner username
     */
    public String getOwner() { return this.owner; }

    /**
     * Retrieves the post ID associated with this reply.
     * @return identifier of the parent post
     */
    public int getPostId() { return this.postId; }

    /**
     * Retrieves the unique reply ID.
     * @return this reply's identifier
     */
    public int getReplyId() { return this.replyId; }

    /**
     * Sets the unique reply ID, typically assigned upon database insertion.
     * @param r reply identifier value
     */
    public void setReplyId(int r) { this.replyId = r; }

    /**
     * Updates the reply content.
     * @param c new content value
     */
    public void updateContent(String c) { this.content = c; }

    /**
     * Sets the reply content.
     * <p>Alias for {@link #updateContent(String)} provided for compatibility.</p>
     *
     * @param content new content text
     */
    public void setContent(String content) { this.content = content; }

}
