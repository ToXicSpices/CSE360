package entityClasses;

import java.util.ArrayList;

/**
 * <p>Title: Post Class</p>
 *
 * <p>Description:
 * Represents a forum post entity within an educational platform that supports interactions from both
 * students and staff. The {@code Post} class models various types of posts (e.g., discussions,
 * announcements, assignments) and provides properties necessary for CRUD operations and validation.
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
 * validation enforcement or permission checking. These must be handled at a higher application layer.</p>
 *
 * @author
 *     Educational Platform Development Team
 * @version
 *     1.0
 * @since
 *     2024-01-01
 */
public class Post {

    /** Title of the post, required for identification */
    private String title = null;

    /** Optional subtitle providing additional context */
    private String subtitle = null;

    /** Main body content of the post, required */
    private String content = null;

    /** Username of the post owner/creator */
    private String owner = null;

    /** Category or discussion thread where the post belongs */
    private String thread = null;

    /** List of tags used for categorization and searching */
    private ArrayList<String> tags = new ArrayList<>();

    /** Unique identifier for the post, assigned by database or system */
    private int postId = -1;

    /** Optional grade assigned by staff (e.g., points or letter) */
    private String grade = null;

    /** Optional feedback text provided by staff */
    private String feedback = null;

    /** Username of staff member who graded the post */
    private String gradedBy = null;

    /** TRUE once staff releases grades to students */
    private boolean gradeReleased = false;

    /**
     * Default constructor.
     * <p>
     * Primarily intended for frameworks or deserialization that require an empty object before filling fields.
     * Not recommended for typical object creation, as it produces an incomplete post.
     * </p>
     */
    public Post() {
        // Default constructor for ORM or serialization
    }

    /**
     * Constructs a complete {@code Post} object with required and optional metadata.
     *
     * @param ti the title of the post, must not be null or empty
     * @param st the subtitle of the post, may be null or empty
     * @param c the main content body, must not be null or empty
     * @param o the username of the owner/creator, required
     * @param ta list of tags to categorize the post (may be empty)
     * @param th the thread or section the post belongs to, required
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
     * Retrieves the title of the post.
     * @return the post title
     */
    public String getTitle() { return title; }

    /**
     * Retrieves the subtitle of the post.
     * @return the post subtitle, may be null
     */
    public String getSubtitle() { return subtitle; }

    /**
     * Retrieves the main content of the post.
     * @return the post content text
     */
    public String getContent() { return content; }

    /**
     * Retrieves the username of the owner.
     * @return owner username
     */
    public String getOwner() { return owner; }

    /**
     * Retrieves the thread/category of the post.
     * @return thread name
     */
    public String getThread() { return thread; }

    /**
     * Returns the list of tags combined into a comma-separated string.
     * Useful for display and some database formats.
     *
     * @return a single string of tags separated by commas
     */
    public String getTagsString() {
        String tagString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagString += tags.get(i);
            if (i < tags.size() - 1) {
                tagString += ",";
            }
        }
        return tagString;
    }

    /**
     * Retrieves the unique ID assigned to this post.
     * @return post ID value
     */
    public int getPostId() { return this.postId; }

    /**
     * Retrieves the assigned grade (may be null if not graded).
     * @return grade string or null
     */
    public String getGrade() { return grade; }

    /**
     * Retrieves the staff feedback (may be null if not graded).
     * @return feedback string or null
     */
    public String getFeedback() { return feedback; }

    /**
     * Retrieves the username of the staff member who graded the post.
     * @return gradedBy username or null
     */
    public String getGradedBy() { return gradedBy; }

    /**
     * Indicates whether the grade has been released to students.
     * @return true if released
     */
    public boolean isGradeReleased() { return gradeReleased; }

    /**
     * Updates the post title.
     * @param ti new title value
     */
    public void updateTitle(String ti) { this.title = ti; }

    /**
     * Updates the post subtitle.
     * @param st new subtitle value
     */
    public void updateSubtitle(String st) { this.subtitle = st; }

    /**
     * Updates the post content body.
     * @param c new content value
     */
    public void updateContent(String c) { this.content = c; }

    /**
     * Updates the thread the post belongs to.
     * @param th new thread/category name
     */
    public void updateThread(String th) { this.thread = th; }

    /**
     * Adds a tag to the post’s categorization list.
     * @param ta the tag to add
     */
    public void addTag(String ta) { this.tags.add(ta); }

    /**
     * Removes a tag from the post’s tag list, if present.
     * @param ta the tag to remove
     */
    public void removeTag(String ta) { this.tags.remove(ta); }

    /**
     * Sets the unique post ID, typically assigned by the database layer.
     * @param id identifier value
     */
    public void setPostId(int id) { this.postId = id; }

    /**
     * Sets/updates the grade.
     * @param g grade value
     */
    public void setGrade(String g) { this.grade = g; }

    /**
     * Sets/updates feedback.
     * @param f feedback text
     */
    public void setFeedback(String f) { this.feedback = f; }

    /**
     * Sets the staff username who graded.
     * @param by staff username
     */
    public void setGradedBy(String by) { this.gradedBy = by; }

    /**
     * Sets release flag.
     * @param released boolean flag
     */
    public void setGradeReleased(boolean released) { this.gradeReleased = released; }
}
