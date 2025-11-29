package entityClasses;

/**
 * <p>Title: Reply Class</p>
 *
 * <p>Description:
 * Represents a reply to a forum post within the educational platform.
 * Each {@code Reply} contains metadata including content, the author, and the post it belongs to.
 * This class serves as a simple data model for CRUD operations and reporting.
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

    /** 
     * <p>Description: Main body content of the reply, required.</p>
     */
    private String content = null;

    /** 
     * <p>Description: Username of the reply creator.</p>
     */
    private String owner = null;

    /** 
     * <p>Description: Identifier of the post this reply belongs to.</p>
     */
    private int postId = -1;

    /** 
     * <p>Description: Unique identifier for the reply, assigned by database/system.</p>
     */
    private int replyId = -1;

    /**
     * <p>Method: Reply()</p>
     *
     * <p>Description:
     * Default constructor. Intended for ORM or serialization use. Produces an incomplete {@code Reply} object.
     * </p>
     */
    public Reply() {
        // Default constructor for framework use
    }

    /**
     * <p>Method: Reply(String c, String o, int p)</p>
     *
     * <p>Description:
     * Constructs a {@code Reply} object with all required fields populated.
     * </p>
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
     * <p>Method: getContent()</p>
     *
     * <p>Description:
     * Retrieves the content of the reply.
     * </p>
     *
     * @return the content text of the reply
     */
    public String getContent() { return this.content; }

    /**
     * <p>Method: getOwner()</p>
     *
     * <p>Description:
     * Retrieves the username of the reply owner.
     * </p>
     *
     * @return the owner username
     */
    public String getOwner() { return this.owner; }

    /**
     * <p>Method: getPostId()</p>
     *
     * <p>Description:
     * Retrieves the post ID associated with this reply.
     * </p>
     *
     * @return the identifier of the parent post
     */
    public int getPostId() { return this.postId; }

    /**
     * <p>Method: getReplyId()</p>
     *
     * <p>Description:
     * Retrieves the unique identifier of this reply.
     * </p>
     *
     * @return this reply's identifier
     */
    public int getReplyId() { return this.replyId; }

    /**
     * <p>Method: setReplyId(int r)</p>
     *
     * <p>Description:
     * Sets the unique reply ID, typically assigned upon database insertion.
     * </p>
     *
     * @param r reply identifier value
     */
    public void setReplyId(int r) { this.replyId = r; }

    /**
     * <p>Method: updateContent(String c)</p>
     *
     * <p>Description:
     * Updates the content of this reply.
     * </p>
     *
     * @param c new content value
     */
    public void updateContent(String c) { this.content = c; }

    /**
     * <p>Method: setContent(String content)</p>
     *
     * <p>Description:
     * Sets the content of the reply. Alias for {@link #updateContent(String)}.
     * </p>
     *
     * @param content new content text
     */
    public void setContent(String content) { this.content = content; }
}
