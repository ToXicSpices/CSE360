package entityClasses;

/**
 * <p>Title: User Class</p>
 *
 * <p>Description:
 * Represents a system user entity. Contains personal details such as username, 
 * first name, middle name, last name, preferred first name, email address, and roles (Admin, Student, Staff).
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Stores personal information and credentials of the user</li>
 *     <li>Tracks assigned roles within the system</li>
 *     <li>Provides getters and setters for all fields to support database operations</li>
 * </ul>
 *
 * <p>Note: Validation, authentication, and permission enforcement should be handled by higher-level services.</p>
 * 
 * @author
 *     Lynn Robert Carter
 * @version
 *     1.0
 * @since
 *     2025-01-01
 */
public class User {

    /** <p>Description: The account username of the user.</p> */
    private String userName;

    /** <p>Description: The account password of the user.</p> */
    private String password;

    /** <p>Description: User's first name.</p> */
    private String firstName;

    /** <p>Description: User's middle name.</p> */
    private String middleName;

    /** <p>Description: User's last name.</p> */
    private String lastName;

    /** <p>Description: User's preferred first name.</p> */
    private String preferredFirstName;

    /** <p>Description: User's email address.</p> */
    private String emailAddress;

    /** <p>Description: Whether the user has the Admin role.</p> */
    private boolean adminRole;

    /** <p>Description: Whether the user has the Student role.</p> */
    private boolean studentRole;

    /** <p>Description: Whether the user has the Staff role.</p> */
    private boolean staffRole;

    /**
     * <p>Method: User()</p>
     *
     * <p>Description:
     * Default constructor. Provided for completeness, not typically used in this system.
     * </p>
     */
    public User() { }

    /**
     * <p>Method: User(String userName, String password, String fn, String mn, String ln, String pfn, String ea, boolean r1, boolean r2, boolean r3)</p>
     *
     * <p>Description:
     * Constructs a fully populated {@code User} object with personal details and assigned roles.
     * </p>
     *
     * @param userName account username
     * @param password account password
     * @param fn first name
     * @param mn middle name
     * @param ln last name
     * @param pfn preferred first name
     * @param ea email address
     * @param r1 true if Admin role, false otherwise
     * @param r2 true if Student role, false otherwise
     * @param r3 true if Staff role, false otherwise
     */
    public User(String userName, String password, String fn, String mn, String ln, String pfn,
                String ea, boolean r1, boolean r2, boolean r3) {
        this.userName = userName;
        this.password = password;
        this.firstName = fn;
        this.middleName = mn;
        this.lastName = ln;
        this.preferredFirstName = pfn;
        this.emailAddress = ea;
        this.adminRole = r1;
        this.studentRole = r2;
        this.staffRole = r3;
    }

    // ------------------- SETTERS -------------------

    /**
     * <p>Method: setUserName(String userName)</p>
     *
     * <p>Description:
     * Sets the username for the user.
     * </p>
     *
     * @param userName the username to set
     */
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * <p>Method: setPassword(String password)</p>
     *
     * <p>Description:
     * Sets the account password for the user.
     * </p>
     *
     * @param password the password to set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * <p>Method: setFirstName(String firstName)</p>
     *
     * <p>Description:
     * Sets the first name of the user.
     * </p>
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * <p>Method: setMiddleName(String middleName)</p>
     *
     * <p>Description:
     * Sets the middle name of the user.
     * </p>
     *
     * @param middleName the middle name to set
     */
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    /**
     * <p>Method: setLastName(String lastName)</p>
     *
     * <p>Description:
     * Sets the last name of the user.
     * </p>
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * <p>Method: setPreferredFirstName(String preferredFirstName)</p>
     *
     * <p>Description:
     * Sets the preferred first name of the user.
     * </p>
     *
     * @param preferredFirstName the preferred first name to set
     */
    public void setPreferredFirstName(String preferredFirstName) { this.preferredFirstName = preferredFirstName; }

    /**
     * <p>Method: setEmailAddress(String emailAddress)</p>
     *
     * <p>Description:
     * Sets the email address of the user.
     * </p>
     *
     * @param emailAddress the email address to set
     */
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    /**
     * <p>Method: setAdminRole(boolean role)</p>
     *
     * <p>Description:
     * Assigns or removes the Admin role for the user.
     * </p>
     *
     * @param role true to assign Admin role, false to remove
     */
    public void setAdminRole(boolean role) { this.adminRole = role; }

    /**
     * <p>Method: setStudentRole(boolean role)</p>
     *
     * <p>Description:
     * Assigns or removes the Student role for the user.
     * </p>
     *
     * @param role true to assign Student role, false to remove
     */
    public void setStudentRole(boolean role) { this.studentRole = role; }

    /**
     * <p>Method: setStaffRole(boolean role)</p>
     *
     * <p>Description:
     * Assigns or removes the Staff role for the user.
     * </p>
     *
     * @param role true to assign Staff role, false to remove
     */
    public void setStaffRole(boolean role) { this.staffRole = role; }

    // ------------------- GETTERS -------------------

    /**
     * <p>Method: getUserName()</p>
     *
     * <p>Description:
     * Retrieves the username of the user.
     * </p>
     *
     * @return the username
     */
    public String getUserName() { return userName; }

    /**
     * <p>Method: getPassword()</p>
     *
     * <p>Description:
     * Retrieves the password of the user.
     * </p>
     *
     * @return the password
     */
    public String getPassword() { return password; }

    /**
     * <p>Method: getFirstName()</p>
     *
     * <p>Description:
     * Retrieves the first name of the user.
     * </p>
     *
     * @return the first name
     */
    public String getFirstName() { return firstName; }

    /**
     * <p>Method: getMiddleName()</p>
     *
     * <p>Description:
     * Retrieves the middle name of the user.
     * </p>
     *
     * @return the middle name
     */
    public String getMiddleName() { return middleName; }

    /**
     * <p>Method: getLastName()</p>
     *
     * <p>Description:
     * Retrieves the last name of the user.
     * </p>
     *
     * @return the last name
     */
    public String getLastName() { return lastName; }

    /**
     * <p>Method: getPreferredFirstName()</p>
     *
     * <p>Description:
     * Retrieves the preferred first name of the user.
     * </p>
     *
     * @return the preferred first name
     */
    public String getPreferredFirstName() { return preferredFirstName; }

    /**
     * <p>Method: getEmailAddress()</p>
     *
     * <p>Description:
     * Retrieves the email address of the user.
     * </p>
     *
     * @return the email address
     */
    public String getEmailAddress() { return emailAddress; }

    /**
     * <p>Method: getAdminRole()</p>
     *
     * <p>Description:
     * Checks if the user has Admin role.
     * </p>
     *
     * @return true if Admin, false otherwise
     */
    public boolean getAdminRole() { return adminRole; }

    /**
     * <p>Method: getStudentRole()</p>
     *
     * <p>Description:
     * Checks if the user has Student role.
     * </p>
     *
     * @return true if Student, false otherwise
     */
    public boolean getStudentRole() { return studentRole; }

    /**
     * <p>Method: getStaffRole()</p>
     *
     * <p>Description:
     * Checks if the user has Staff role.
     * </p>
     *
     * @return true if Staff, false otherwise
     */
    public boolean getStaffRole() { return staffRole; }

    /**
     * <p>Method: getNumRoles()</p>
     *
     * <p>Description:
     * Returns the total number of roles assigned to the user (0–3).
     * </p>
     *
     * @return number of roles
     */
    public int getNumRoles() {
        int numRoles = 0;
        if (adminRole) numRoles++;
        if (studentRole) numRoles++;
        if (staffRole) numRoles++;
        return numRoles;
    }
}
