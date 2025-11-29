package entityClasses;

/**
 * <p>Title: Request Class</p>
 *
 * <p>Description:
 * Represents a request submitted by a user within the educational platform. 
 * Each {@code Request} contains a title, content, the username of the requester, 
 * a unique identifier, and a status indicating whether the request has been checked or processed.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores request details including title, content, and requester</li>
 *     <li>Tracks whether the request has been reviewed or processed</li>
 *     <li>Provides getters and setters for all fields to support database operations</li>
 * </ul>
 *
 * <p>Note: Validation, permission checking, and request processing logic should be handled by 
 * higher-level services or controllers.</p>
 * 
 * @author
 *     Educational Platform Development Team
 * @version
 *     1.0
 * @since
 *     2024-01-01
 */
public class Request {

    /** <p>Description: Title of the request.</p> */
    private String title;

    /** <p>Description: Main content or body of the request.</p> */
    private String content;

    /** <p>Description: Username of the user who submitted the request.</p> */
    private String requester;

    /** <p>Description: Unique identifier for the request, typically assigned by the database.</p> */
    private int requestId;

    /** <p>Description: Flag indicating whether the request has been checked/processed.</p> */
    private boolean checked;

    /**
     * <p>Method: Request()</p>
     *
     * <p>Description:
     * Default constructor. Creates an empty {@code Request} object, useful for serialization frameworks or default object creation.
     * </p>
     */
    public Request() { }

    /**
     * <p>Method: Request(String t, String c, String r, boolean checked)</p>
     *
     * <p>Description:
     * Constructs a {@code Request} object with all fields initialized except requestId.
     * </p>
     *
     * @param t the title of the request
     * @param c the content of the request
     * @param r the username of the requester
     * @param checked initial checked status
     */
    public Request(String t, String c, String r, boolean checked) {
        this.title = t;
        this.content = c;
        this.requester = r;
        this.checked = checked;
    }

    // ------------------- GETTERS -------------------

    /**
     * <p>Method: getTitle()</p>
     *
     * <p>Description:
     * Retrieves the request's title.
     * </p>
     *
     * @return the request title
     */
    public String getTitle() { return this.title; }

    /**
     * <p>Method: getContent()</p>
     *
     * <p>Description:
     * Retrieves the main content of the request.
     * </p>
     *
     * @return the request content
     */
    public String getContent() { return this.content; }

    /**
     * <p>Method: getRequester()</p>
     *
     * <p>Description:
     * Retrieves the username of the requester.
     * </p>
     *
     * @return requester username
     */
    public String getRequester() { return this.requester; }

    /**
     * <p>Method: getRequestId()</p>
     *
     * <p>Description:
     * Retrieves the unique identifier of the request.
     * </p>
     *
     * @return request ID
     */
    public int getRequestId() { return this.requestId; }

    /**
     * <p>Method: isChecked()</p>
     *
     * <p>Description:
     * Checks whether the request has been checked/processed.
     * </p>
     *
     * @return true if the request is checked, false otherwise
     */
    public boolean isChecked() { return this.checked; }

    // ------------------- UPDATE / SETTERS -------------------

    /**
     * <p>Method: updateTitle(String t)</p>
     *
     * <p>Description:
     * Updates the title of the request.
     * </p>
     *
     * @param t new title value
     */
    public void updateTitle(String t) { this.title = t; }

    /**
     * <p>Method: updateContent(String c)</p>
     *
     * <p>Description:
     * Updates the content of the request.
     * </p>
     *
     * @param c new content value
     */
    public void updateContent(String c) { this.content = c; }

    /**
     * <p>Method: setRequestId(int id)</p>
     *
     * <p>Description:
     * Sets the unique request ID, typically assigned by the database.
     * </p>
     *
     * @param id new request ID
     */
    public void setRequestId(int id) { this.requestId = id; }

    /**
     * <p>Method: updateChecked(boolean c)</p>
     *
     * <p>Description:
     * Updates the checked status of the request.
     * </p>
     *
     * @param c new checked value
     */
    public void updateChecked(boolean c) { this.checked = c; }

    /**
     * <p>Method: setChecked(boolean c)</p>
     *
     * <p>Description:
     * Directly sets the checked status, useful for database load operations.
     * </p>
     *
     * @param c checked value to set
     */
    public void setChecked(boolean c) { this.checked = c; }
}
