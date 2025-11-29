package entityClasses;

import java.util.ArrayList;

/**
 * <p>Title: Post Class</p>
 *
 * <p>Description:
 * Represents a forum post entity within an educational platform. Supports interactions
 * from both students and staff. Models various types of posts and provides properties
 * necessary for CRUD operations and validation.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores and manages core post information</li>
 *     <li>Supports tagging and thread categorization</li>
 *     <li>Maintains authorship and ownership metadata</li>
 * </ul>
 *
 * <p>Note: This class is a simple entity model and does not include business logic such as
 * validation enforcement or permission checking.</p>
 *
 * @author
 *     Educational Platform Development Team
 * @version
 *     1.0
 * @since
 *     2024-01-01
 */
public class Post {

    /** <p>Description: Title of the post, required for identification.</p> */
    private String title = null;

    /** <p>Description: Optional subtitle providing additional context.</p> */
    private String subtitle = null;

    /** <p>Description: Main body content of the post, required.</p> */
    private String content = null;

    /** <p>Description: Username of the post owner/creator.</p> */
    private String owner = null;

    /** <p>Description: Category or discussion thread where the post belongs.</p> */
    private String thread = null;

    /** <p>Description: List of tags used for categorization and searching.</p> */
    private ArrayList<String> tags = new ArrayList<>();

    /** <p>Description: Unique identifier for the post, assigned by database or system.</p> */
    private int postId = -1;

    /**
     * <p>Method: Post()</p>
     *
     * <p>Description:
     * Default constructor. Primarily intended for frameworks or deserialization
     * that require an empty object before filling fields.
     * </p>
     */
    public Post() { }

    /**
     * <p>Method: Post(String ti, String st, String c, String o, ArrayList ta, String th)</p>
     *
     * <p>Description:
     * Constructs a fully populated {@code Post} object containing required core data
     * and optional metadata such as subtitle and tags.
     * </p>
     *
     * @param ti the title of the post (required)
     * @param st the subtitle of the post (optional)
     * @param c the main content body (required)
     * @param o the username of the post owner/creator (required)
     * @param ta list of tags applied to the post (optional, may be empty)
     * @param th the thread or category the post belongs to (required)
     * @throws IllegalArgumentException if required fields are null or invalid
     */
    public Post(String ti, String st, String c, String o, ArrayList<String> ta, String th) {
        this.title = ti;
        this.subtitle = st;
        this.content = c;
        this.owner = o;
        this.tags = ta;
        this.thread = th;
    }

    /**
     * <p>Method: getTitle()</p>
     *
     * <p>Description:
     * Retrieves the title of the post.
     * </p>
     *
     * @return the post title
     */
    public String getTitle() { return title; }

    /**
     * <p>Method: getSubtitle()</p>
     *
     * <p>Description:
     * Retrieves the subtitle of the post.
     * </p>
     *
     * @return the post subtitle, may be null
     */
    public String getSubtitle() { return subtitle; }

    /**
     * <p>Method: getContent()</p>
     *
     * <p>Description:
     * Retrieves the main content of the post.
     * </p>
     *
     * @return the post content text
     */
    public String getContent() { return content; }

    /**
     * <p>Method: getOwner()</p>
     *
     * <p>Description:
     * Retrieves the username of the post owner.
     * </p>
     *
     * @return owner username
     */
    public String getOwner() { return owner; }

    /**
     * <p>Method: getThread()</p>
     *
     * <p>Description:
     * Retrieves the thread/category of the post.
     * </p>
     *
     * @return thread name
     */
    public String getThread() { return thread; }

    /**
     * <p>Method: getTagsString()</p>
     *
     * <p>Description:
     * Returns the list of tags combined into a comma-separated string.
     * Useful for display and some database formats.
     * </p>
     *
     * @return a single string of tags separated by commas
     */
    public String getTagsString() {
        String tagString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagString += tags.get(i);
            if (i < tags.size() - 1) tagString += ",";
        }
        return tagString;
    }

    /**
     * <p>Method: getPostId()</p>
     *
     * <p>Description:
     * Retrieves the unique identifier assigned to this post.
     * </p>
     *
     * @return post ID value
     */
    public int getPostId() { return this.postId; }

    /**
     * <p>Method: updateTitle(String ti)</p>
     *
     * <p>Description:
     * Updates the post title.
     * </p>
     *
     * @param ti new title value
     */
    public void updateTitle(String ti) { this.title = ti; }

    /**
     * <p>Method: updateSubtitle(String st)</p>
     *
     * <p>Description:
     * Updates the post subtitle.
     * </p>
     *
     * @param st new subtitle value
     */
    public void updateSubtitle(String st) { this.subtitle = st; }

    /**
     * <p>Method: updateContent(String c)</p>
     *
     * <p>Description:
     * Updates the post content body.
     * </p>
     *
     * @param c new content value
     */
    public void updateContent(String c) { this.content = c; }

    /**
     * <p>Method: updateThread(String th)</p>
     *
     * <p>Description:
     * Updates the thread/category the post belongs to.
     * </p>
     *
     * @param th new thread/category name
     */
    public void updateThread(String th) { this.thread = th; }

    /**
     * <p>Method: addTag(String ta)</p>
     *
     * <p>Description:
     * Adds a tag to the post’s categorization list.
     * </p>
     *
     * @param ta the tag to add
     */
    public void addTag(String ta) { this.tags.add(ta); }

    /**
     * <p>Method: removeTag(String ta)</p>
     *
     * <p>Description:
     * Removes a tag from the post’s tag list, if present.
     * </p>
     *
     * @param ta the tag to remove
     */
    public void removeTag(String ta) { this.tags.remove(ta); }

    /**
     * <p>Method: setPostId(int id)</p>
     *
     * <p>Description:
     * Sets the unique post ID, typically assigned by the database layer.
     * </p>
     *
     * @param id identifier value
     */
    public void setPostId(int id) { this.postId = id; }
}
