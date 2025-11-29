package entityClasses;

/**
 * <p>Title: StudentStatus Class</p>
 *
 * <p>Description:
 * Represents the status and activity metrics of a student within the educational platform.
 * Tracks contributions, received interactions, and disciplinary or reward metrics.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores student username and activity metrics</li>
 *     <li>Tracks engagement statistics such as views, replies received, and upvotes</li>
 *     <li>Tracks student recognition and violations</li>
 *     <li>Provides getters and setters for all fields for database or UI use</li>
 * </ul>
 *
 * <p>Note: Validation, business rules, and permission enforcement should be handled by higher-level components or services.</p>
 * 
 * @author
 *     Educational Platform Development Team
 * @version
 *     1.0
 * @since
 *     2024-01-01
 */
public class StudentStatus {

    /** <p>Description: Username of the student.</p> */
    private String userName;

    /** <p>Description: Number of posts created by the student.</p> */
    private int postNumber;

    /** <p>Description: Number of replies created by the student.</p> */
    private int replyNumber;

    /** <p>Description: Total number of views received on the student's posts.</p> */
    private int viewReceived;

    /** <p>Description: Total number of replies received on the student's posts.</p> */
    private int replyReceived;

    /** <p>Description: Total number of upvotes received on the student's posts/replies.</p> */
    private int upvoteReceived;

    /** <p>Description: Number of promotions earned by the student.</p> */
    private int promotion;

    /** <p>Description: Number of violations recorded for the student.</p> */
    private int violation;

    /**
     * <p>Method: StudentStatus()</p>
     *
     * <p>Description:
     * Default constructor. Creates a {@code StudentStatus} object with all numeric metrics initialized to zero.
     * </p>
     */
    public StudentStatus() { }

    /**
     * <p>Method: StudentStatus(String userName, int postNumber, int replyNumber, int viewReceived, int replyReceived, int upvoteReceived, int promotion, int violation)</p>
     *
     * <p>Description:
     * Constructs a {@code StudentStatus} object with all fields initialized.
     * </p>
     *
     * @param userName the student's username
     * @param postNumber number of posts created
     * @param replyNumber number of replies created
     * @param viewReceived total views received
     * @param replyReceived total replies received
     * @param upvoteReceived total upvotes received
     * @param promotion number of promotions
     * @param violation number of violations
     */
    public StudentStatus(String userName, int postNumber, int replyNumber,
                         int viewReceived, int replyReceived, int upvoteReceived,
                         int promotion, int violation) {
        this.userName = userName;
        this.postNumber = postNumber;
        this.replyNumber = replyNumber;
        this.viewReceived = viewReceived;
        this.replyReceived = replyReceived;
        this.upvoteReceived = upvoteReceived;
        this.promotion = promotion;
        this.violation = violation;
    }

    /**
     * <p>Method: StudentStatus(String userName)</p>
     *
     * <p>Description:
     * Convenience constructor for new students. All numeric metrics are initialized to zero.
     * </p>
     *
     * @param userName the student's username
     */
    public StudentStatus(String userName) {
        this(userName, 0, 0, 0, 0, 0, 0, 0);
    }

    // -------------------- GETTERS --------------------

    /**
     * <p>Method: getUserName()</p>
     *
     * <p>Description:
     * Retrieves the student's username.
     * </p>
     *
     * @return the student's username
     */
    public String getUserName() { return userName; }

    /**
     * <p>Method: getPostNumber()</p>
     *
     * <p>Description:
     * Retrieves the number of posts created by the student.
     * </p>
     *
     * @return number of posts created
     */
    public int getPostNumber() { return postNumber; }

    /**
     * <p>Method: getReplyNumber()</p>
     *
     * <p>Description:
     * Retrieves the number of replies created by the student.
     * </p>
     *
     * @return number of replies created
     */
    public int getReplyNumber() { return replyNumber; }

    /**
     * <p>Method: getViewReceived()</p>
     *
     * <p>Description:
     * Retrieves the total number of views received.
     * </p>
     *
     * @return total views received
     */
    public int getViewReceived() { return viewReceived; }

    /**
     * <p>Method: getReplyReceived()</p>
     *
     * <p>Description:
     * Retrieves the total number of replies received.
     * </p>
     *
     * @return total replies received
     */
    public int getReplyReceived() { return replyReceived; }

    /**
     * <p>Method: getUpvoteReceived()</p>
     *
     * <p>Description:
     * Retrieves the total number of upvotes received.
     * </p>
     *
     * @return total upvotes received
     */
    public int getUpvoteReceived() { return upvoteReceived; }

    /**
     * <p>Method: getPromotion()</p>
     *
     * <p>Description:
     * Retrieves the number of promotions earned by the student.
     * </p>
     *
     * @return number of promotions
     */
    public int getPromotion() { return promotion; }

    /**
     * <p>Method: getViolation()</p>
     *
     * <p>Description:
     * Retrieves the number of violations recorded for the student.
     * </p>
     *
     * @return number of violations
     */
    public int getViolation() { return violation; }

    // -------------------- SETTERS --------------------

    /**
     * <p>Method: setPostNumber(int postNumber)</p>
     *
     * <p>Description:
     * Updates the number of posts created by the student.
     * </p>
     *
     * @param postNumber new post count
     */
    public void setPostNumber(int postNumber) { this.postNumber = postNumber; }

    /**
     * <p>Method: setReplyNumber(int replyNumber)</p>
     *
     * <p>Description:
     * Updates the number of replies created by the student.
     * </p>
     *
     * @param replyNumber new reply count
     */
    public void setReplyNumber(int replyNumber) { this.replyNumber = replyNumber; }

    /**
     * <p>Method: setViewReceived(int viewReceived)</p>
     *
     * <p>Description:
     * Updates the total views received on the student's posts.
     * </p>
     *
     * @param viewReceived new view count
     */
    public void setViewReceived(int viewReceived) { this.viewReceived = viewReceived; }

    /**
     * <p>Method: setReplyReceived(int replyReceived)</p>
     *
     * <p>Description:
     * Updates the total replies received on the student's posts.
     * </p>
     *
     * @param replyReceived new reply count
     */
    public void setReplyReceived(int replyReceived) { this.replyReceived = replyReceived; }

    /**
     * <p>Method: setUpvoteReceived(int upvoteReceived)</p>
     *
     * <p>Description:
     * Updates the total upvotes received on the student's posts/replies.
     * </p>
     *
     * @param upvoteReceived new upvote count
     */
    public void setUpvoteReceived(int upvoteReceived) { this.upvoteReceived = upvoteReceived; }

    /**
     * <p>Method: setPromotion(int promotion)</p>
     *
     * <p>Description:
     * Updates the number of promotions earned by the student.
     * </p>
     *
     * @param promotion new promotion count
     */
    public void setPromotion(int promotion) { this.promotion = promotion; }

    /**
     * <p>Method: setViolation(int violation)</p>
     *
     * <p>Description:
     * Updates the number of violations recorded for the student.
     * </p>
     *
     * @param violation new violation count
     */
    public void setViolation(int violation) { this.violation = violation; }

    /**
     * <p>Method: toString()</p>
     *
     * <p>Description:
     * Returns a string representation of the {@code StudentStatus} object including all fields.
     * </p>
     *
     * @return string containing all fields and their values
     */
    @Override
    public String toString() {
        return "StudentStatus{" +
                "userName='" + userName + '\'' +
                ", postNumber=" + postNumber +
                ", replyNumber=" + replyNumber +
                ", viewReceived=" + viewReceived +
                ", replyReceived=" + replyReceived +
                ", upvoteReceived=" + upvoteReceived +
                ", promotion=" + promotion +
                ", violation=" + violation +
                '}';
    }
}
