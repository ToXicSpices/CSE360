package database;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.security.SecureRandom;

import entityClasses.User;
import entityClasses.Message;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.Request;
import entityClasses.StudentStatus;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	/**JDBC driver name**/
	static final String JDBC_DRIVER = "org.h2.Driver";   
	/**database URL**/
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	/**Database credentials: USER**/
	static final String USER = "sa"; 
	/**Database credentials: PASS**/
	static final String PASS = ""; 

	//  Shared variables used within this class
	/**Singleton to access the database**/
	private Connection connection = null;
	/**The H2 Statement is used to construct queries**/
	private Statement statement = null;
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	/**user name for current user**/
	private String currentUsername;
	/**password for current user**/
	private String currentPassword;
	/**first name for current user**/
	private String currentFirstName;
	/**middle name for current user**/
	private String currentMiddleName;
	/**last name for current user**/
	private String currentLastName;
	/**preferred first name for current user**/
	private String currentPreferredFirstName;
	/**email address for current user**/
	private String currentEmailAddress;
	/**admin role for current user**/
	private boolean currentAdminRole;
	/**student role for current user**/
	private boolean currentStudentRole;
	/**staff role for current user**/
	private boolean currentStaffRole;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}

/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			 statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 * @throws SQLException if issue occurred
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "studentRole BOOL DEFAULT FALSE, "
				+ "staffRole BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
			    + "code VARCHAR(10) PRIMARY KEY, "
			    + "emailAddress VARCHAR(255), "
			    + "role VARCHAR(10), "
			    + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";			// Mark generated time, mark archived and delete after 24 hours
	    statement.execute(invitationCodesTable);
	    
	    // Create the One-time Passwords table
	    String oneTimePasscodesTable = "CREATE TABLE IF NOT EXISTS OneTimePasscodes ("
	            + "passcode VARCHAR(10) PRIMARY KEY, "
	            + "emailAddress VARCHAR(255), "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";			// Mark generated time, mark archived and delete after 24 hours
	    statement.execute(oneTimePasscodesTable);
	   
	    // Create Threads table
	    String threadTable = "CREATE TABLE IF NOT EXISTS Threads ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "threadName VARCHAR(255) UNIQUE)";
	    statement.execute(threadTable);
	    
	    // Create Posts table
	    String postTable = "CREATE TABLE IF NOT EXISTS Posts ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "title VARCHAR(255) NOT NULL UNIQUE, "
	            + "subtitle VARCHAR(255), "
	            + "content VARCHAR(2200), "
	            + "owner VARCHAR(255), "
	            + "thread INT DEFAULT 1, "
	            + "tags VARCHAR(255), "
	            + "FOREIGN KEY (thread) REFERENCES Threads(id) ON DELETE SET DEFAULT)";
	    statement.execute(postTable);

	    // Create Replies table
	    String replyTable = "CREATE TABLE IF NOT EXISTS Replies ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "content VARCHAR(1000), "
	            + "owner VARCHAR(255), "
	            + "postId INT, "
	            + "FOREIGN KEY (postId) REFERENCES Posts(id) ON DELETE SET NULL)";
	    statement.execute(replyTable);
	    
	    // Create Post Read Status table
	    String postReadStatusTable = "CREATE TABLE IF NOT EXISTS PostReadStatus ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255), "
	            + "postId INT, "
	            + "upvotes INT, "
	            + "isRead BOOL DEFAULT FALSE, "
	            + "FOREIGN KEY (postId) REFERENCES Posts(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (userName) REFERENCES userDB(userName) ON DELETE CASCADE, "
	            + "UNIQUE (userName, postId))";
	    statement.execute(postReadStatusTable);
	    
	    // Create Reply Read Status table
	    String replyReadStatusTable = "CREATE TABLE IF NOT EXISTS ReplyReadStatus ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255), "
	            + "replyId INT, "
	            + "isRead BOOL DEFAULT FALSE, "
	            + "FOREIGN KEY (replyId) REFERENCES Replies(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (userName) REFERENCES userDB(userName) ON DELETE CASCADE, "
	            + "UNIQUE (userName, replyId))";
	    statement.execute(replyReadStatusTable);
	    
	    // Create Student Status table
	    String studentStatusTable = "CREATE TABLE IF NOT EXISTS StudentStatus ("
	    	    + "userName VARCHAR(255) PRIMARY KEY, "
	    	    + "postNumber INT DEFAULT 0, "
	    	    + "replyNumber INT DEFAULT 0, "
	    	    + "viewReceived INT DEFAULT 0, "
	    	    + "replyReceived INT DEFAULT 0, "
	    	    + "upvoteReceived INT DEFAULT 0, "
	    	    + "promotion INT DEFAULT 0, "
	    	    + "violation INT DEFAULT 0, "
	    	    + "FOREIGN KEY (userName) REFERENCES userDB(userName) ON DELETE CASCADE)";
	    statement.execute(studentStatusTable);
	    
	    // Create System Requests table
	    String systemRequestsTable = "CREATE TABLE IF NOT EXISTS systemRequests ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "requester VARCHAR(255) NULL, "
	            + "title VARCHAR(255) NOT NULL UNIQUE, "
	            + "content VARCHAR(2200), "
	            + "checked BOOLEAN DEFAULT FALSE, "
	            + "FOREIGN KEY (requester) REFERENCES userDB(userName) ON DELETE SET NULL)";
	    statement.execute(systemRequestsTable);
	    
	    // Create Message table
	    String messageTable ="CREATE TABLE IF NOT EXISTS Message ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "sender VARCHAR(255) NULL, "
	    		+ "receiver VARCHAR(255) NULL, "
	    		+ "subject VARCHAR(255), "
	    		+ "content VARCHAR(2200), "
	    		+ "isRead BOOLEAN DEFAULT FALSE, "
	    		+ "FOREIGN KEY (sender) REFERENCES userDB(userName) ON DELETE SET NULL, "
	    		+ "FOREIGN KEY (receiver) REFERENCES userDB(userName) ON DELETE SET NULL)";
	    statement.execute(messageTable);

	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, studentRole, staffRole) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentStudentRole = user.getStudentRole();
			pstmt.setBoolean(9, currentStudentRole);
			
			currentStaffRole = user.getStaffRole();
			pstmt.setBoolean(10, currentStaffRole);
			
			pstmt.executeUpdate();
		}
		
		if (user.getStudentRole()) {
	        insertStudentStatus(user.getUserName());
	    }
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "Select User" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

	/*******
	 * <p> Method: getAllUsers() </p>
	 * <P> Description: Generate an List of Strings includes all data from all users. </p>
	 *  
	 *  @return a list of all users data.
	 */
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String query = "SELECT * FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
	            User user = new User();
	            user.setUserName(rs.getString("userName"));
	            user.setPassword(rs.getString("password"));
	            user.setFirstName(rs.getString("firstName"));
	            user.setMiddleName(rs.getString("middleName"));
	            user.setLastName(rs.getString("lastName"));
	            user.setPreferredFirstName(rs.getString("preferredFirstName"));
	            user.setEmailAddress(rs.getString("emailAddress"));
	            user.setAdminRole(rs.getBoolean("adminRole"));
	            user.setStudentRole(rs.getBoolean("studentRole"));
	            user.setStaffRole(rs.getBoolean("staffRole"));
	            users.add(user);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}

	/**
	 * <p> Method: getAllStudents() </p>
	 * <P> Description: Retrieves all users who have the student role from the userDB table. </p>
	 *
	 * @return a {@code List} of {@code User} objects representing all students;
	 *         returns an empty list if no students exist or if a database error occurs
	 */
	public List<User> getAllStudents() {
	    List<User> students = new ArrayList<>();
	    String query = "SELECT * FROM userDB WHERE studentRole = TRUE";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            User user = new User();
	            user.setUserName(rs.getString("userName"));
	            user.setPassword(rs.getString("password"));
	            user.setFirstName(rs.getString("firstName"));
	            user.setMiddleName(rs.getString("middleName"));
	            user.setLastName(rs.getString("lastName"));
	            user.setPreferredFirstName(rs.getString("preferredFirstName"));
	            user.setEmailAddress(rs.getString("emailAddress"));
	            user.setAdminRole(rs.getBoolean("adminRole"));
	            user.setStudentRole(rs.getBoolean("studentRole"));
	            user.setStaffRole(rs.getBoolean("staffRole"));

	            students.add(user);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return students;
	}
	
	/*******
	 * <p> Method: deleteUser() </p>
	 * <P> Description: delete a user by username from the database. </p>
	 *  
	 *  @param userName input from dialog
	 */
	public void deleteUser(String userName) {
	    String query = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginStudent(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginStudent(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginStaff(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginStaff(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	/*******
	 * <p> Method: updatePassword(String username, String newPassword) </p>
	 * 
	 * <p> Description: Update the password by username in the database. </p>
	 * 
	 * @param username specifies the specific user that we want to change password if it is in the table.
	 * 
	 * @param newPassword specifies the new password to replace the old password.
	 * 
	 * @return true if the password is successfully updated
	 * 
	 */
	public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE userDB SET password = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getStudentRole()) numberOfRoles++;
		if (user.getStaffRole()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: getAllInvitationEmails() </p>
	 * 
	 * <p> Description: Return all emails from the invitation table to help to show the 
	 * invitation table in admin home.</p>
	 * 
	 * @return the list of emails that received and not yet used the invitation code.
	 * 
	 */
	public ArrayList<String> getAllInvitationEmails() {
        ArrayList<String> emails = new ArrayList<>();
        String query = "SELECT emailAddress FROM InvitationCodes";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                emails.add(rs.getString("emailAddress"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }
	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	/**
	 * <p> Method: getInvitationEmailCodeRole() </p>
	 * <P> Description: Retrieves all invitation codes along with associated email addresses and roles. </p>
	 *
	 * @return an {@code ArrayList<String[]>} containing all invitation entries;
	 *         returns an empty list if no entries exist or a database error occurs
	 */
	public ArrayList<String[]> getInvitationEmailCodeRole() {
	    ArrayList<String[]> list = new ArrayList<>();
	    String query = "SELECT emailAddress, code, role FROM InvitationCodes";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String email = rs.getString("emailAddress");
	            String code = rs.getString("code");
	            String role = rs.getString("role");
	            list.add(new String[]{ email, code, role });
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	/*******
	 * <p> Method: void removeInvitationByEmail(String email) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used by email address.</p>
	 * 
	 * @param email is the email address been sent the invitation code
	 *  
	 */
	public void removeInvitationByEmail(String email) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	       pstmt.setString(1, email);
	       ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
	       			query = "DELETE FROM InvitationCodes WHERE emailAddress = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, email);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	/*******
	 * <p> Method: registerOneTimePasscodeRequest(String emailAddress) </p>
	 * 
	 * <p> Description: create a new entry in the one-time password table by only giving
	 * the email with empty password.</p>
	 * 
	 * @param emailAddress is the email address been sent the one-time password
	 *  
	 */
	// store the request to the table
	public void registerOneTimePasscodeRequest(String emailAddress) {
	    String query = "INSERT INTO OneTimePasscodes (passcode, emailAddress) VALUES (?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, "");
	        pstmt.setString(2, emailAddress);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return;
	}
	
	/*******
	 * <p> Method: generateAndUpdatePasscode(String emailAddress) </p>
	 * 
	 * <p> Description: generate and update the one-time password if the admin 
	 * send it to the specific email.</p>
	 * 
	 * @param emailAddress is the email address needs to receive the one-time password
	 *  
	 * @return the string of one-time password made of uppercase letters and numbers in 10-digit
	 */
	public String generateAndUpdatePasscode(String emailAddress) {
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Uppercase only
	    SecureRandom secureRandom = new SecureRandom();
	    StringBuilder code = new StringBuilder();

	    for (int i = 0; i < 10; i++) {
	        int index = secureRandom.nextInt(chars.length());
	        code.append(chars.charAt(index));
	    }

	    String passcode = code.toString();

	    String query = 
	            "UPDATE OneTimePasscodes " +
	            "SET passcode = ?, " +
	            "    createdAt = CURRENT_TIMESTAMP " +   // Update time stamp after generated
	            "WHERE emailAddress = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, passcode);
	        pstmt.setString(2, emailAddress);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return passcode;
	}
	
	/**
	 * <p> Method: getAllOneTimePasscodeEntries() </p>
	 * <P> Description: Retrieves all one-time passcode entries from the OneTimePasscodes table. </p>
	 *
	 * @return a {@code Map} where the keys are email addresses and the values are the associated passcodes;
	 *         returns an empty map if no entries exist or if a database error occurs
	 */
	public Map<String, String> getAllOneTimePasscodeEntries() {
	    Map<String, String> map = new HashMap<>();
	    String query = "SELECT emailAddress, passcode FROM OneTimePasscodes";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            map.put(rs.getString("emailAddress"), rs.getString("passcode"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return map;
	}
	
	/*******
	 * <p> Method: getEmailsWithoutPasscode() </p>
	 * 
	 * <p> Description: provide the list of emails that needs one-time password.</p>
	 *  
	 * @return the list of emails that doesn't receive the one-time password yet.
	 */
	// list to notify the admin which request has not been sent passcode
	public ArrayList<String> getEmailsWithoutPasscode() {
		ArrayList<String> emails = new ArrayList<>();
	    String query = "SELECT emailAddress FROM OneTimePasscodes WHERE passcode = ''";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String email = rs.getString("emailAddress");
	            if (email != null && !email.isEmpty()) {
	                emails.add(email);
	            }
	        }

	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return emails;
	}
	
	/*******
	 * <p> Method: getPasscodeByEmail(String email) </p>
	 *
	 * <p> Description: retrieve the passcode associated with the given email
	 * from the OneTimePasscodes table. Returns "" if none exists. </p>
	 *
	 * @param email the email whose passcode should be retrieved
	 *
	 * @return the corresponding passcode, or "" if none exists
	 */
	public String getPasscodeByEmail(String email) {
	    String query = "SELECT passcode FROM OneTimePasscodes WHERE emailAddress = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            String passcode = rs.getString("passcode");
	            return passcode != null ? passcode : "";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return "";
	}
	
	/**
	 * <p> Method: removePasscodeRowByEmail(String email) </p>
	 * <P> Description: Deletes a one-time passcode entry for a specific email address. </p>
	 *
	 * @param email the email address whose passcode entry should be removed
	 */
	public void removePasscodeRowByEmail(String email) {
	    String query = "DELETE FROM OneTimePasscodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Method: emailaddressHasBeenSentPasscode(String emailAddress) </p>
	 * 
	 * <p> Description: check if the email address has been sent a one-time password, so it can
	 * be removed from the one-time password request table in admin home.</p>
	 *  
	 * @param emailAddress the email that needs to be checked with corresponding non-empty passcode
	 * 
	 * @return true is the email has a corresponding non-empty string of one-time password on the table
	 */
	public boolean emailaddressHasBeenSentPasscode(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM OneTimePasscodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {  // move cursor to the first row
	            int count = rs.getInt("count");
	            return count > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/*******
	 * <p> Method: removeExpiredEntries() </p>
	 *
	 * <p> Description: Removes all invitation codes and one-time passcodes that are older
	 * than 24 hours. This cleanup ensures that expired authentication data does not remain
	 * in the database and prevents outdated credentials from being used.</p>
	 *
	 */
	public void removeExpiredEntries() {

	    String deleteInvitations =
	        "DELETE FROM InvitationCodes WHERE createdAt <= ?";
	    String deletePasscodes =
	        "DELETE FROM OneTimePasscodes WHERE createdAt <= ?";

	    // Compute the cutoff time (24 hours ago)
	    Timestamp cutoff = Timestamp.from(Instant.now().minus(24, ChronoUnit.HOURS));

	    try (PreparedStatement ps1 = connection.prepareStatement(deleteInvitations);
	         PreparedStatement ps2 = connection.prepareStatement(deletePasscodes)) {

	        ps1.setTimestamp(1, cutoff);
	        ps2.setTimestamp(1, cutoff);

	        ps1.executeUpdate();
	        ps2.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentStudentRole = rs.getBoolean(10);
	    	currentStaffRole = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	/*******
	 * <p> Method: String getUsernameByEmail(String email) </p>
	 * 
	 * <p> Description: get the username by email address.</p>
	 * 
	 * @param email is the email address that needs to check from the table
	 * 
	 * @return the corresponding username if the email is found on the table
	 *  
	 */
	// see if any user has the email
	public String getUsernameByEmail(String email) {
		String query = "SELECT * FROM userDB WHERE emailAddress = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
		       ResultSet rs = pstmt.executeQuery();			
			rs.next();
			return rs.getString(2);
	    } catch (SQLException e) {
			return "";
		}
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET studentRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStudentRole = true;
				else
					currentStudentRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET staffRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStaffRole = true;
				else
					currentStaffRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentStudentRole() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentStudentRole() { return currentStudentRole;};
	
	
	/*******
	 * <p> Method: boolean getCurrentStaffRole() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentStaffRole() { return currentStaffRole;};
	 
	
	/*******
	 * <p> Method: makePost(Post post) </p>
	 * 
	 * <p> Description: make post use attributes of the object post </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param post specifies a post object to be added to the database.
	 * 
	 * @return the generated id for each post
	 *
	 */
	public int makePost(Post post) throws SQLException {
	    String insertPost = "INSERT INTO Posts (title, subtitle, content, owner, thread, tags) "
	                      + "VALUES (?, ?, ?, ?, ?, ?)";
	    int generatedId = -1;

	    try (PreparedStatement pstmt = connection.prepareStatement(insertPost, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, post.getTitle());
	        pstmt.setString(2, post.getSubtitle());
	        pstmt.setString(3, post.getContent());
	        pstmt.setString(4, post.getOwner());
	        pstmt.setInt(5, getThreadId(post.getThread()));
	        pstmt.setString(6, post.getTagsString());
	        pstmt.executeUpdate();

	        // Fetch the auto-generated ID
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	            }
	        }
	    }
	    return generatedId;
	}

	/***
	 * <p> Method: String getPostTitle(int postId) </p>
	 * 
	 * <p> Description: return the post title by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the String of post title
	 * 
	 */
	// Database getters for Posts
	public String getPostTitle(int postId) {
	    String query = "SELECT title FROM Posts WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("title");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: String getPostSubtitle(int postId) </p>
	 * 
	 * <p> Description: return the post subtitle by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the String of post subtitle
	 * 
	 */
	public String getPostSubtitle(int postId) {
	    String query = "SELECT subtitle FROM Posts WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("subtitle");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: String getPostContent(int postId) </p>
	 * 
	 * <p> Description: return the post content by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the String of post content
	 * 
	 */
	public String getPostContent(int postId) {
	    String query = "SELECT content FROM Posts WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("content");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: String getPostOwnerUsername(int postId) </p>
	 * 
	 * <p> Description: return the post owner username by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the String of post owner username
	 * 
	 */
	public String getPostOwnerUsername(int postId) {
	    String query = "SELECT owner FROM Posts WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("owner");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: String getPostThread(int postId) </p>
	 * 
	 * <p> Description: return the post thread by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the String of post thread
	 * 
	 */
	public String getPostThread(int postId) {
	    String query = "SELECT thread FROM Posts WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return getThreadName(rs.getInt("thread"));
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
    /***
	 * <p> Method: ArrayList getPostTags(int postId) </p>
	 * 
	 * <p> Description: return the post tags by post id from post table </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the ArrayList of String of post tags
	 * 
	 */
    public ArrayList<String> getPostTags(int postId) {
        ArrayList<String> tagsList = new ArrayList<>();
        String query = "SELECT tags FROM Posts WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String tags = rs.getString("tags");
                if (tags != null && !tags.isEmpty()) {
                    String[] tagArray = tags.split(",");
                    for (String tag : tagArray) {
                        tagsList.add(tag);
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagsList;
    }
    
    /***
	 * <p> Method: ArrayList getPostReplyList(int postId) </p>
	 * 
	 * <p> Description: return the reply list of particular post by looking through 
	 * the reply table with specific post id </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the ArrayList of Integer of reply ids
	 * 
	 */
	public ArrayList<Integer> getPostReplyList(int postId) {
	    ArrayList<Integer> replyIds = new ArrayList<>();
	    String query = "SELECT id FROM Replies WHERE postId = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            replyIds.add(rs.getInt("id"));
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return replyIds;
	}
	
	/***
	 * <p> Method: int getPostNumberOfReplies(int postId) </p>
	 * 
	 * <p> Description: return the reply number of particular post by looking through 
	 * the reply table with specific post id </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @return the number of replies
	 * 
	 */
	public int getPostNumberOfReplies(int postId) {
	    int count = 0;
	    String query = "SELECT COUNT(*) AS replyCount FROM Replies WHERE postId = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);

	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            count = rs.getInt("replyCount");
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return count;
	}
	
	/***
	 * <p> Method: ArrayList getAllPostsNewestFirst() </p>
	 * 
	 * <p> Description: return the post list consists of all posts stored in the post table </p>
	 * 
	 * @return the ArrayList of all Post
	 * 
	 */
	public ArrayList<Post> getAllPostsNewestFirst() {
	    ArrayList<Post> posts = new ArrayList<>();
	    String query = "SELECT * FROM Posts ORDER BY id DESC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	int id = rs.getInt("id");
	            Post post = new Post(
	                rs.getString("title"),
	                rs.getString("subtitle"),
	                rs.getString("content"),
	                rs.getString("owner"),
	                getPostTags(id),
	                getThreadName(rs.getInt("thread"))
	            );
	            post.setPostId(id);
	            posts.add(post);
	        }

	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
	}
	
	/***
	 * <p> Method: ArrayList getPostsFromUserNewestFirst(String owner) </p>
	 * 
	 * <p> Description: return the post list consists of all posts of specific owner/user </p>
	 * 
	 * @param owner is the post owner username
	 * 
	 * @return the ArrayList of Post
	 * 
	 */
	public ArrayList<Post> getPostsFromUserNewestFirst(String owner) {
	    ArrayList<Post> posts = new ArrayList<>();
	    String query = "SELECT * FROM Posts WHERE owner = ? ORDER BY id DESC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, owner);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            int id = rs.getInt("id");
	            Post post = new Post(
	                rs.getString("title"),
	                rs.getString("subtitle"),
	                rs.getString("content"),
	                rs.getString("owner"),
	                getPostTags(id),
	                getThreadName(rs.getInt("thread"))
	            );
	            post.setPostId(id);
	            posts.add(post);
	        }

	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
	}
	
	/**
	 * <p> Method: getNumberOfPosts(String userName) </p>
	 * 
	 * <P> Description:Retrieves the total number of posts created by a specific user. </p>
	 *
	 * @param userName the username of the post owner
	 * @return the total number of posts created by the specified user;
	 *         returns 0 if no posts exist or if a database error occurs
	 */
	public int getNumberOfPosts(String userName) {
	    String sql = "SELECT COUNT(*) AS postCount " +
	                 "FROM Posts " +
	                 "WHERE owner = ?";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, userName);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("postCount");
	            } else {
	                return 0;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
	
	/***
	 * <p> Method: boolean doesPostExistByTitle(String title) </p>
	 * 
	 * <p> Description: determine whether the post title to be created already existed </p>
	 * 
	 * @param title is the post title to be checked
	 * 
	 * @return true if the title already existed
	 * 
	 */
	public boolean doesPostExistByTitle(String title) {
	    String sql = "SELECT 1 FROM Posts WHERE title = ? LIMIT 1";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, title);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
     * <p> Method: ArrayList getUnreadPosts(String userName) </p>
	 * 
	 * <p> Description: Check if a specific post is unread by a given user. </p>
     *
     * @param username The username of the user.
     * 
     * @param postId The ID of the post.
     * 
     * @return A list of Post objects that are unread by the user.
     * 
     */
	public boolean isPostUnread(String username, int postId) {
	    String sql = """
	        SELECT prs.isRead
	        FROM PostReadStatus prs
	        WHERE prs.userName = ? AND prs.postId = ?
	    """;

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, username);
	        ps.setInt(2, postId);

	        try (ResultSet rs = ps.executeQuery()) {
	            // If no record exists → the post has never been read → unread
	            if (!rs.next()) return true;

	            // If record exists → return NOT isRead
	            return !rs.getBoolean("isRead");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return true;
	}
	
	/**
	 * <p> Method: Post getPostById(int postId) </p>
	 * 
	 * <p> Description: Retrieve a Post object by its ID. </p>
	 *
	 * @param postId The ID of the post to retrieve.
	 * 
	 * @return The Post object if found, otherwise null.
	 * 
	 */
    public Post getPostById(int postId) {
        ArrayList<Post> allPosts = getAllPostsNewestFirst();
        for (Post post : allPosts) {
            if (post.getPostId() == postId) {
                return post;
            }
        }
        return null; // Post not found
    }

	// Database setters/updaters for Posts
	/***
	 * <p> Method: void updatePostTitle(int postId, String title) </p>
	 * 
	 * <p> Description: update/edit title of a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param title is the new updated title
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void updatePostTitle(int postId, String title) throws SQLException {
	    String query = "UPDATE Posts SET title = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, title);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    }
	}
	
	/***
	 * <p> Method: void updatePostSubitle(int postId, String subtitle) </p>
	 * 
	 * <p> Description: update/edit subtitle of a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param subtitle is the new updated subtitle
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void updatePostSubtitle(int postId, String subtitle) throws SQLException {
	    String query = "UPDATE Posts SET subtitle = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, subtitle);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    }
	}
	
	/***
	 * <p> Method: void updatePostContent(int postId, String content) </p>
	 * 
	 * <p> Description: update/edit content of a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param content is the new updated content
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void updatePostContent(int postId, String content) throws SQLException {
	    String query = "UPDATE Posts SET content = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, content);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    }
	}
	
	/***
	 * <p> Method: void updatePostOwnerUsername(int postId, String owner) </p>
	 * 
	 * <p> Description: update/edit owner username of a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param owner is the new updated owner username
	 * 
	 */
	public void updatePostOwnerUsername(int postId, String owner) {
	    String query = "UPDATE Posts SET owner = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, owner);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/***
	 * <p> Method: void updatePostThread(int postId, String thread) </p>
	 * 
	 * <p> Description: update/edit thread of a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param thread is the new updated thread
	 * 
	 */
	public void updatePostThread(int postId, String thread) {
	    String query = "UPDATE Posts SET thread = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, thread);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/***
	 * <p> Method: void addPostTag(int postId, String tag) </p>
	 * 
	 * <p> Description: add a tag to a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param tag is the new tag to be added
	 * 
	 */
	public void addPostTag(int postId, String tag) {
	    ArrayList<String> tagsList = getPostTags(postId);

	    // Avoid duplicates
	    if (!tagsList.contains(tag)) {
	        tagsList.add(tag);
	    }

	    String newTags = String.join(",", tagsList);

	    String updateQuery = "UPDATE Posts SET tags = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newTags);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/***
	 * <p> Method: void removePostTag(int postId, String tag) </p>
	 * 
	 * <p> Description: remove a tag from a particular post </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param tag is the tag of a post to be romoved
	 * 
	 */
	public void removePostTag(int postId, String tag) {
	    ArrayList<String> tagsList = getPostTags(postId);

	    // Remove matching tag (case-insensitive)
	    tagsList.removeIf(t -> t.equalsIgnoreCase(tag));

	    String newTags = String.join(",", tagsList);

	    String updateQuery = "UPDATE Posts SET tags = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newTags);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
    
	/***
	 * <p> Method: void addPostReply(int postId, int replyId) </p>
	 * 
	 * <p> Description: add a reply to a particular post by linking a reply from reply table to post table</p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param replyId is the unique post id for each reply
	 * 
	 */
	public void addPostReply(int postId, int replyId) {
	    String query = "UPDATE Replies SET postId = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        pstmt.setInt(2, replyId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/***
	 * <p> Method: void removePostReply(int postId, int replyId) </p>
	 * 
	 * <p> Description: remove a reply to a particular post by remove link between 
	 * a reply from reply table to post table</p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 * @param replyId is the unique post id for each reply
	 * 
	 */
	public void removePostReply(int postId, int replyId) {
	    String query = "UPDATE Replies SET postId = NULL WHERE id = ? AND postId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/***
	 * <p> Method: void deletePost(int postId) </p>
	 * 
	 * <p> Description: delete a post object from post table without deleting its replies </p>
	 * 
	 * @param postId is the unique post id for each post
	 * 
	 */
	public void deletePost(int postId) {
	    try {
	        // Delete from PostReadStatus table first
	        String deletePostReadStatusQuery = "DELETE FROM PostReadStatus WHERE postId = ?";
	        try (PreparedStatement pstmt1 = connection.prepareStatement(deletePostReadStatusQuery)) {
	            pstmt1.setInt(1, postId);
	            pstmt1.executeUpdate();
	        }
	        
	        // Delete the post itself
	        String deletePostQuery = "DELETE FROM Posts WHERE id = ?";
	        try (PreparedStatement pstmt4 = connection.prepareStatement(deletePostQuery)) {
	            pstmt4.setInt(1, postId);
	            pstmt4.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/*******
	 * <p> Method: makeReply(Post post) </p>
	 * 
	 * <p> Description: make replies use attributes of the object reply </p>
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 * @param reply specifies a reply object to be added to the database.
	 * 
	 * @return the generated id for each reply
	 *
	 */
	public int makeReply(Reply reply) throws SQLException {
	    String insertReply = "INSERT INTO Replies (content, owner, postId) VALUES (?, ?, ?)";
	    int generatedId = -1;

	    try (PreparedStatement pstmt = connection.prepareStatement(insertReply, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, reply.getContent());
	        
	        pstmt.setString(2, reply.getOwner());
	        
	        pstmt.setInt(3, reply.getPostId());

	        pstmt.executeUpdate();

	        // Get the auto-generated reply ID
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	            }
	        }
	    }

	    return generatedId;
	}
	
    // Database getters for Replies
	/***
	 * <p> Method: String getReplyContent(int replyId) </p>
	 * 
	 * <P> Description: return the reply content. </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 * @return the reply content
	 */
	public String getReplyContent(int replyId) {
	    String query = "SELECT content FROM Replies WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("content");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: String getReplyOwnerUsername(int replyId) </p>
	 * 
	 * <p> Description: return the reply owner usrename </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 * @return the reply owner username
	 * 
	 */
	public String getReplyOwnerUsername(int replyId) {
	    String query = "SELECT owner FROM Replies WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("owner");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
	/***
	 * <p> Method: int getReplyPostId(int replyId) </p>
	 * 
	 * <p> Description: return the post of a particular reply </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 * @return the post id of the post of the particular reply
	 * 
	 */
	public int getReplyPostId(int replyId) {
	    String query = "SELECT postId FROM Replies WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, replyId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("postId");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
    
	/***
	 * <p> Method: ArrayList getRepliesByPostId(int postId) </p>
	 * 
	 * <p> Description: return the reply list of a particular reply </p>
	 * 
	 * @param postId is the unique id for each post
	 * 
	 * @return the ArrayList of Reply
	 * 
	 */
	public ArrayList<Reply> getRepliesByPostId(int postId) {
	    ArrayList<Reply> replies = new ArrayList<>();
	    String query = "SELECT * FROM Replies WHERE postId = ? ORDER BY id ASC"; // ASC = oldest to newest

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Reply reply = new Reply(
	                rs.getString("content"),
	                rs.getString("owner"),
	                rs.getInt("postId")
	            );
	            reply.setReplyId(rs.getInt("id"));
	            replies.add(reply);
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return replies;
	}
	
	/**
	 * <p> Method: int getNumberOfReplies(String userName) </p>
	 * 
	 * <P> Description: Retrieves the total number of replies created by a specific user. </p>
	 *
	 * @param userName the username of the reply owner
	 * @return the total number of replies created by the specified user;
	 *         returns 0 if no replies exist or if a database error occurs
	 */
	public int getNumberOfReplies(String userName) {
	    String sql = "SELECT COUNT(*) AS replyCount " +
	                 "FROM Replies " +
	                 "WHERE owner = ?";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, userName);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("replyCount");
	            } else {
	                return 0;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
	
	/**
     * <p> Method: ArrayList getUnreadReplies(String userName) </p>
	 * 
	 * <p> Description: Retrieve all unread replies for a given user. </p>
     *
     * @param userName The username of the user.
     * 
     * @param postId is the unique id for each post
     * 
     * @return A list of Reply objects that are unread by the user.
     */
	public ArrayList<Reply> getUnreadReplies(String userName, int postId) {
        ArrayList<Reply> unreadReplies = new ArrayList<>();

        String sql = """
            SELECT r.*
            FROM Replies r
            LEFT JOIN ReplyReadStatus rrs 
                ON r.id = rrs.replyId AND rrs.userName = ?
            WHERE (rrs.isRead = FALSE OR rrs.isRead IS NULL)
              AND r.postId = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, postId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reply reply = new Reply(
                        rs.getString("content"),
                        rs.getString("owner"),
                        rs.getInt("postId")   // use postId column from DB
                    );
                    reply.setReplyId(rs.getInt("id"));
                    unreadReplies.add(reply);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unreadReplies;
    }
	
	// Database setters/updaters for Replies
	/***
	 * <p> Method: void updateReplyContent(int replyId, String content) </p>
	 * 
	 * <p> Description: update the reply content </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 * @param content is the new/updated reply content
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void updateReplyContent(int replyId, String content) throws SQLException {
	    String query = "UPDATE Replies SET content = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, content);
	        pstmt.setInt(2, replyId);
	        pstmt.executeUpdate();
	    }
	}
	
	/***
	 * <p> Method: void updateReplyOwnerUsername(int replyId, String owner) </p>
	 * 
	 * <p> Description: update the reply owner username </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 * @param owner is the new/updated reply owner username
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void updateReplyOwnerUsername(int replyId, String owner) throws SQLException {
	    String query = "UPDATE Replies SET owner = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, owner);
	        pstmt.setInt(2, replyId);
	        pstmt.executeUpdate();
	    }
	}
	
	/***
	 * <p> Method: void deleteReply(int replyId) </p>
	 * 
	 * <p> Description: delete a specific reply from reply table </p>
	 * 
	 * @param replyId is the unique id for each reply
	 * 
	 */
	public void deleteReply(int replyId) {
	    try {
	        // First delete from ReplyReadStatus table
	        String deleteReadStatusQuery = "DELETE FROM ReplyReadStatus WHERE replyId = ?";
	        try (PreparedStatement pstmt1 = connection.prepareStatement(deleteReadStatusQuery)) {
	            pstmt1.setInt(1, replyId);
	            pstmt1.executeUpdate();
	        }
	        
	        // Then delete the reply itself
	        String deleteReplyQuery = "DELETE FROM Replies WHERE id = ?";
	        try (PreparedStatement pstmt2 = connection.prepareStatement(deleteReplyQuery)) {
	            pstmt2.setInt(1, replyId);
	            pstmt2.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	/**
	 * <p> Method: void markPostAsRead(String userName, int postId) </p>
	 * 
	 * <p> Description: Mark a post as read by a specific user.</p>
	 * 
	 * @param userName The username of the user reading the post.
	 * 
	 * @param postId   The ID of the post to mark as read.
	 * 
	 */
	public void markPostAsRead(String userName, int postId) {
	    String sql = "MERGE INTO PostReadStatus (userName, postId, isRead, upvotes) "
	                 + "KEY(userName, postId) "
	                 + "VALUES (?, ?, TRUE, COALESCE((SELECT upvotes FROM PostReadStatus "
	                 + "WHERE userName = ? AND postId = ?), 0))";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, userName);
	        ps.setInt(2, postId);
	        ps.setString(3, userName);
	        ps.setInt(4, postId);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * <p> Method: void makePostUpvote(String userName, int postId) </p>
	 * 
	 * <P> Description: Records an upvote for a specific post by a given user. </p>
	 *
	 * @param userName the username of the user giving the upvote
	 * @param postId the ID of the post being upvoted
	 */
	public void makePostUpvote(String userName, int postId) {
	    String sql = 
	        "MERGE INTO PostReadStatus (userName, postId, upvotes, isRead) " +
	        "KEY(userName, postId) " +
	        "VALUES (?, ?, COALESCE((SELECT upvotes FROM PostReadStatus WHERE userName = ? AND postId = ?), 0) + 1, TRUE)";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, userName);
	        ps.setInt(2, postId);
	        ps.setString(3, userName);
	        ps.setInt(4, postId);

	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * <p> Method: int getPostViews(int postId) </p>
	 * 
	 * <P> Description: Retrieves the total number of views for a specific post. </p>
	 *
	 * @param postId the ID of the post to count views for
	 * @return the total number of views for the specified post, or 0 if no views exist or a database error occurs
	 */
	public int getPostViews(int postId) {
	    String sql = "SELECT COUNT(*) AS totalViews "
	               + "FROM PostReadStatus "
	               + "WHERE postId = ? AND isRead = TRUE";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, postId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getInt("totalViews");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0; // default if no rows or error
	}

	/**
	 * <p> Method: int getPostUpvotes(int postId) </p>
	 * 
	 * <P> Description: Retrieves the total number of upvotes for a specific post. </p>
	 *
	 * @param postId the ID of the post to count upvotes for
	 * @return the total number of upvotes for the specified post;
	 *         returns 0 if no upvotes exist or if a database error occurs
	 */
	public int getPostUpvotes(int postId) {
	    String sql = "SELECT COALESCE(SUM(upvotes), 0) AS totalUpvotes "
	               + "FROM PostReadStatus "
	               + "WHERE postId = ?";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, postId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            return rs.getInt("totalUpvotes");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
	}
	
	/**
	 * <p> Method:boolean isUpvoted(String username, int postId) </p>
	 * 
	 * <P> Description: Checks whether a specific user has upvoted a particular post. </p>
	 *
	 * @param username the username of the user
	 * @param postId the ID of the post
	 * @return {@code true} if the user has upvoted the post (upvotes > 0);
	 *         {@code false} if the user has not upvoted, the record does not exist, or a database error occurs
	 */
	public boolean isUpvoted(String username, int postId) {
	    String sql = "SELECT upvotes FROM PostReadStatus WHERE userName = ? AND postId = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, username);
	        ps.setInt(2, postId);

	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("upvotes") > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
    
    /**
     * <p> Method: String hasUserReadPost(String userName, int postId) </p>
	 * 
	 * <p> Description: Check whether a user has read a specific post. </p>
     *
     * @param postId The ID of the post to check.
     * 
     * @param userName The username of the user.
     * 
     * @return true if the post is marked as read by the user, false otherwise.
     * 
     */
    public String hasUserReadPost(String userName, int postId) {
        String sql = "SELECT isRead FROM PostReadStatus WHERE postId = ? AND userName = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	ps.setInt(1, postId);
            ps.setString(2, userName);
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		return rs.getBoolean("isRead") ? "Viewed" : "Unread";
            	} 
            	else {
            		return "Unread";
            	}
            } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
        } catch (SQLException e) {
	        e.printStackTrace();
	    }
        
        return "Unread";
        
    }

    /**
     * <p> Method: void markRepliesAsRead(String userName, int postId) </p>
	 * 
	 * <p> Description: Mark all replies belonging to a post as read by a specific user. </p>
     *
     * @param userName The username of the user.
     * 
     * @param postId   The ID of the post whose replies should be marked as read.
     */
    public void markRepliesAsRead(String userName, int postId) {
        String selectReplies = "SELECT id FROM Replies WHERE postId = ?";
        try (PreparedStatement psSelect = connection.prepareStatement(selectReplies)) {
            psSelect.setInt(1, postId);
            try (ResultSet rs = psSelect.executeQuery()) {
                while (rs.next()) {
                    int replyId = rs.getInt("id");
                    String sql = "MERGE INTO ReplyReadStatus (userName, replyId, isRead) " +
                                 "KEY(userName, replyId) " +
                                 "VALUES (?, ?, TRUE)";
                    try (PreparedStatement psMerge = connection.prepareStatement(sql)) {
                        psMerge.setString(1, userName);
                        psMerge.setInt(2, replyId);
                        psMerge.executeUpdate();
                    } catch (SQLException e) {
            	        e.printStackTrace();
            	    }
                }
            } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
        } catch (SQLException e) {
	        e.printStackTrace();
	    }
    }
    
    /**
     * <p> Method: int getNumberOfUnreadReplies(int postId, String userName) </p>
	 * 
	 * <p> Description: Get the number of unread replies for a specific post and user. </p>
     *
     * @param postId   The ID of the post.
     * 
     * @param userName The username of the user.
     * 
     * @return The number of unread replies.
     * 
     */
    public int getNumberOfUnreadReplies(int postId, String userName) {
        String sql = "SELECT COUNT(*) AS unreadCount " +
                     "FROM Replies r " +
                     "LEFT JOIN ReplyReadStatus rs " +
                     "ON r.id = rs.replyId AND rs.userName = ? " +
                     "WHERE r.postId = ? AND (rs.isRead IS NULL OR rs.isRead = FALSE)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, postId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("unreadCount");
                } else {
                    return 0;
                }
            } catch (SQLException e) {
    	        e.printStackTrace();
    	    }
        } catch (SQLException e) {
	        e.printStackTrace();
	    }
        return 0;
    }

    
    /*******
     * <p> Method: searchPostsByTitle(String searchTitle) </p>
     * 
     * <p> Description: Search for posts that contain the specified title text (case-insensitive). </p>
     * 
     * @param searchTitle the text to search for in post titles
     * 
     * @return ArrayList of Post objects that match the search criteria, ordered by newest first
     * 
     */
    public ArrayList<Post> searchPostsByTitle(String searchTitle) {
        ArrayList<Post> matchingPosts = new ArrayList<>();
        
        // Return empty list if search term is null or empty
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return matchingPosts;
        }
        
        // SQL query to search for posts containing the search term in title (case-insensitive)
        String query = "SELECT * FROM Posts WHERE LOWER(title) LIKE LOWER(?) ORDER BY id DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Add wildcards for partial matching
            pstmt.setString(1, "%" + searchTitle.trim() + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                Post post = new Post(
                    rs.getString("title"),
                    rs.getString("subtitle"), 
                    rs.getString("content"),
                    rs.getString("owner"),
                    getPostTags(id),
                    getThreadName(rs.getInt("thread"))
                );
                post.setPostId(id);
                matchingPosts.add(post);
            }
            
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return matchingPosts;
    }
    
    /*******
     * <p> Method: addThread(String threadName) </p>
     * 
     * <p> Description: This method adds a new thread to the Threads table if it does not already exist. </p>
     * 
     * @param threadName The name of the thread to be added to the database.
     * 
     */
    public void addThread(String threadName) {
        String query = "MERGE INTO Threads (threadName) KEY(threadName) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, threadName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <p> Method: boolean existsThreadName(String name) </p>
	 * 
	 * <p> Checks whether a thread with the specified name exists in the Threads table. </p>
     *
     * @param name the name of the thread to check for existence
     * @return {@code true} if a thread with the given name exists,
     *         {@code false} if no thread matches or if the result set is empty
     * @throws SQLException if a database access error occurs
     */
    public boolean existsThreadName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Threads WHERE threadName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /*******
     * <p> Method: getAllThreads() </p>
     * 
     * <p> Description: This method retrieves all thread names from the Threads table 
     * and returns them as an ArrayList of Strings. </p>
     * 
     * @return An ArrayList containing all thread names.
     * 
     */
    public ArrayList<String> getAllThreads() {
        ArrayList<String> threads = new ArrayList<>();
        String query = "SELECT threadName FROM Threads";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                threads.add(rs.getString("threadName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threads;
    }
    
    /*******
     * <p> Method: getThreadId(String threadName) </p>
     * 
     * <p> Description: This method retrieves the unique ID of a thread based on its name. 
     * If the thread does not exist, it returns -1. </p>
     * 
     * @param threadName The name of the thread to search for.
     * 
     * @return The thread ID if found, or -1 if no matching thread exists.
     * 
     */
    public int getThreadId(String threadName) {
        String query = "SELECT id FROM Threads WHERE threadName = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, threadName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*******
     * <p> Method: getThreadName(int threadId) </p>
     * 
     * <p> Description: This method retrieves the name of a thread based on its unique ID. 
     * If the thread ID does not exist in the database, the method returns null. </p>
     * 
     * @param threadId The unique ID of the thread.
     * 
     * @return The thread name as a String, or null if the thread is not found.
     * 
     */
    public String getThreadName(int threadId) {
        String query = "SELECT threadName FROM Threads WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, threadId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("threadName");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * <p> Method: void updateThreadName(int threadId, String newName) </p>
	 * 
	 * <p> Description: Updates the name of a thread in the Threads table. </p>
     * 
     * @param threadId the unique identifier of the thread to update
     * @param newName the new name to set for the thread
     * @throws SQLException if a database access error occurs
     */
    public void updateThreadName(int threadId, String newName) throws SQLException {
        String sql = "UPDATE Threads SET threadName = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, threadId);
            ps.executeUpdate();
        }
    }


    /**
     * <p> Method: void deleteThread(int threadId) </p>
	 * 
	 * <p> Description: Deletes a thread from the Threads table. </p>
     *
     * @param threadId the unique identifier of the thread to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteThread(int threadId) throws SQLException {
        String sql = "DELETE FROM Threads WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, threadId);
            ps.executeUpdate();
        }
    }
    
    /**
     * <p> Method: boolean doesUserOwnReply(String username, int replyId) </p>
     * 
     * <p> Description: Check if a user owns a specific reply.</p>
     * 
     * @param username the username to check ownership for
     * @param replyId the ID of the reply to check
     * 
     * @return true if the user owns the reply, false otherwise
     */
    public boolean doesUserOwnReply(String username, int replyId) {
        String query = "SELECT COUNT(*) FROM Replies WHERE id = ? AND owner = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, replyId);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    
	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	
	/**
	 * <p> Method: boolean doesReplyExistByContent(String content) </p>
	 * <p> Description:
	 * Checks whether a reply with the specified content exists in the Replies table. </p>
	 *
	 * @param content the textual content to search for within reply records
	 * @return {@code true} if a reply with the given content exists,
	 *         {@code false} if no match is found or if a database error occurs
	 */
	public boolean doesReplyExistByContent(String content) {
	    String sql = "SELECT 1 FROM Replies WHERE content = ? LIMIT 1";

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, content);
	        ResultSet rs = stmt.executeQuery();
	        return rs.next(); // true if at least one record is found
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	/**
	 * <p> Method: int makeRequest(Request request) </p>
	 * 
	 * <p> Description: Inserts a new request into the systemRequests table. </p>
	 *
	 * @param request the {@code Request} object to insert
	 * @return the generated ID of the inserted request, or -1 if insertion fails
	 * @throws SQLException if a database access error occurs
	 */
	public int makeRequest(Request request) throws SQLException {
	    String sql = "INSERT INTO systemRequests (requester, title, content, checked) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setString(1, request.getRequester());
	        ps.setString(2, request.getTitle());
	        ps.setString(3, request.getContent());
	        ps.setBoolean(4, request.isChecked());
	        ps.executeUpdate();

	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                int id = rs.getInt(1);
	                request.setRequestId(id);
	                return id;
	            }
	        }
	    }
	    return -1;
	}

	/**
	 * <p> Method: ArrayList getAllRequests() </p>
	 * 
	 * <p> Description: Retrieves all requests from the systemRequests table. </p>
	 *
	 * @return a list of {@code Request} objects
	 * @throws SQLException if a database access error occurs
	 */
	public ArrayList<Request> getAllRequests() throws SQLException {
	    ArrayList<Request> list = new ArrayList<>();
	    String sql = "SELECT * FROM systemRequests";

	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Request r = new Request(
	                rs.getString("title"),
	                rs.getString("content"),
	                rs.getString("requester"),
                    rs.getBoolean("checked")
	            );
	            r.setRequestId(rs.getInt("id"));
	            r.setChecked(rs.getBoolean("checked"));
	            list.add(r);
	        }
	    }
	    return list;
	}

	/**
	 * <p> Method: ArrayList getAllRequestsByUser(String requester) </p>
	 * 
	 * <p> Description: Retrieves all requests made by a specific user. </p>
	 *
	 * @param requester the username of the requester
	 * @return a list of {@code Request} objects made by the specified user
	 * @throws SQLException if a database access error occurs
	 */
	public ArrayList<Request> getAllRequestsByUser(String requester) throws SQLException {
	    ArrayList<Request> list = new ArrayList<>();
	    String sql = "SELECT * FROM systemRequests WHERE requester = ?";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, requester);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Request r = new Request(
	                    rs.getString("title"),
	                    rs.getString("content"),
	                    rs.getString("requester"),
	                    rs.getBoolean("checked")
	                );
	                r.setRequestId(rs.getInt("id"));
	                r.setChecked(rs.getBoolean("checked"));
	                list.add(r);
	            }
	        }
	    }
	    return list;
	}

	/**
	 * <p> Method: Request getRequestByTitle(String title) </p>
	 * 
	 * <p> Description: Retrieves a request by its title. </p>
	 *
	 * @param title the title of the request
	 * @return the {@code Request} object if found, otherwise {@code null}
	 * @throws SQLException if a database access error occurs
	 */
	public Request getRequestByTitle(String title) throws SQLException {
	    String sql = "SELECT * FROM systemRequests WHERE title = ?";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setString(1, title);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Request r = new Request(
	                    rs.getString("title"),
	                    rs.getString("content"),
	                    rs.getString("requester"),
	                    rs.getBoolean("checked")
	                );
	                r.setRequestId(rs.getInt("id"));
	                r.setChecked(rs.getBoolean("checked"));
	                return r;
	            }
	        }
	    }
	    return null;
	}

	/**
	 * <p> Method: void updateRequestTitle(Request request, String title) </p>
	 * 
	 * <p> Description: Updates the title of an existing request in the database. </p>
	 * 
	 * @param request the {@code Request} object to update
	 * @param title the new title to set
	 * @throws SQLException if a database access error occurs
	 */
    public void updateRequestTitle(Request request, String title) throws SQLException {
        String sql = "UPDATE systemRequests SET title = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            request.updateTitle(title); // update the object
        }
    }

    /**
     * <p> Method: void updateRequestContent(Request request, String content) </p>
	 * 
	 * <p> Description: Updates the content of an existing request in the database. </p>
     *
     * @param request the {@code Request} object to update
     * @param content the new content to set
     * @throws SQLException if a database access error occurs
     */
    public void updateRequestContent(Request request, String content) throws SQLException {
        String sql = "UPDATE systemRequests SET content = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            request.updateContent(content); // update the object
        }
    }
    
    /**
     * <p> Method: void updateRequestChecked(Request request, boolean checked) </p>
	 * 
	 * <p> Description: Updates the checked status of an existing request in the database. </p>
     *
     * @param request the {@code Request} object to update
     * @param checked the new checked status to set
     * @throws SQLException if a database access error occurs
     */
    public void updateRequestChecked(Request request, boolean checked) throws SQLException {
        String sql = "UPDATE systemRequests SET checked = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, checked);
            ps.setInt(2, request.getRequestId());
            ps.executeUpdate();
            request.setChecked(checked); // update object
        }
    }

    /**
     * <p> Method: void deleteRequest(Request request) </p>
	 * 
	 * <p> Description: Deletes a request from the systemRequests table. </p>
     *
     * @param request the {@code Request} object to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteRequest(Request request) throws SQLException {
        String sql = "DELETE FROM systemRequests WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, request.getRequestId());
            ps.executeUpdate();
        }
    }
	
    /**
     * <p> Method: void insertStudentStatus(String userName) </p>
	 * 
	 * <p> Description: Inserts a new student status record with only the username. </p>
     *
     * @param userName the username of the student
     */
    public void insertStudentStatus(String userName) {
        String sql = "INSERT INTO StudentStatus (userName) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p> Method: void updateStudentStatus(StudentStatus status) </p>
	 * 
	 * <p> Description: Updates an existing student status record in the StudentStatus table. </p>
     *
     * @param status the {@code StudentStatus} object containing updated values
     */
    public void updateStudentStatus(StudentStatus status) {
        String sql = "UPDATE StudentStatus SET "
                + "postNumber = ?, "
                + "replyNumber = ?, "
                + "viewReceived = ?, "
                + "replyReceived = ?, "
                + "upvoteReceived = ?, "
                + "promotion = ?, "
                + "violation = ? "
                + "WHERE userName = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, status.getPostNumber());
            ps.setInt(2, status.getReplyNumber());
            ps.setInt(3, status.getViewReceived());
            ps.setInt(4, status.getReplyReceived());
            ps.setInt(5, status.getUpvoteReceived());
            ps.setInt(6, status.getPromotion());
            ps.setInt(7, status.getViolation());
            ps.setString(8, status.getUserName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p> Method: StudentStatus getStudentStatus(String userName) </p>
	 * 
	 * <p> Description: Retrieves the student status by username.
     * If the student status does not exist, a new record is inserted with default values. </p>
     *
     * @param userName the username of the student
     * @return the {@code StudentStatus} object, or {@code null} if a database error occurs
     */
    public StudentStatus getStudentStatus(String userName) {
        String sql = "SELECT * FROM StudentStatus WHERE userName = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new StudentStatus(
                        rs.getString("userName"),
                        rs.getInt("postNumber"),
                        rs.getInt("replyNumber"),
                        rs.getInt("viewReceived"),
                        rs.getInt("replyReceived"),
                        rs.getInt("upvoteReceived"),
                        rs.getInt("promotion"),
                        rs.getInt("violation")
                );
            } else {
                insertStudentStatus(userName);
                return new StudentStatus(userName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p> Method: int makeMessage(Message msg) </p>
	 * 
	 * <p> Description: Inserts a new message into the Message table. </p>
     *
     * @param msg the {@code Message} object to insert
     * @return the generated ID of the inserted message, or -1 if insertion fails
     */
    public int makeMessage(Message msg) {
        String query = "INSERT INTO Message (sender, receiver, subject, content, isRead) "
                     + "VALUES (?, ?, ?, ?, false)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, msg.getSender());
            pstmt.setString(2, msg.getReceiver());
            pstmt.setString(3, msg.getSubject());
            pstmt.setString(4, msg.getContent());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return -1;
        }
        return -1;
    }
    
    /**
     * <p> Method: void deleteStudentStatus(String userName) </p>
     * 
     * <p> Description: Deletes the student status record for the specified username 
     * from the StudentStatus table. This is useful for test setup/cleanup or 
     * removing a student's metrics from the database.</p>
     *
     * @param userName the username of the student whose status record should be deleted
     * @throws SQLException if a database access error occurs
     */
    public void deleteStudentStatus(String userName) throws SQLException {
        String sql = "DELETE FROM StudentStatus WHERE userName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.executeUpdate();
        }
    }

    /**
     * <p> Method: Message getMessage(int msgId) </p>
	 * 
	 * <p> Description: Retrieves a message by its ID. </p>
     *
     * @param msgId the unique ID of the message
     * @return the {@code Message} object if found, otherwise {@code null}
     */
    public Message getMessage(int msgId) {
        String query = "SELECT * FROM Message WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, msgId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Message(
                    rs.getInt("id"),
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("subject"),
                    rs.getString("content"),
                    rs.getBoolean("isRead")
                );
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * <p> Method: List getAllMessages(String username) </p>
	 * 
	 * <p> Description: Retrieves all messages received by a specific user. </p>
     *
     * @param username the receiver's username
     * @return a list of {@code Message} objects
     */
    public List<Message> getAllMessages(String username) {
        List<Message> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT * FROM Message WHERE receiver = ? ORDER BY id DESC")) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new Message(
                        rs.getInt("id"),
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getString("subject"),
                        rs.getString("content"),
                        rs.getBoolean("isRead")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return list;
    }

    /**
     * <p> Method: List getAllUnreadMessages(String username) </p>
	 * 
	 * <p> Description: Retrieves all unread messages received by a specific user. </p>
     *
     * @param username the receiver's username
     * @return a list of unread {@code Message} objects, or {@code null} if a database error occurs
     */
    public List<Message> getAllUnreadMessages(String username) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM Message "
                     + "WHERE receiver = ? AND isRead = false ORDER BY id DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                    rs.getInt("id"),
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("subject"),
                    rs.getString("content"),
                    rs.getBoolean("isRead")
                ));
            }
        } catch (SQLException e) {
            return null;
        }
        return messages;
    }

    /**
     * <p> Method: void markMessageAsRead(Message msg) </p>
	 * 
	 * <p> Description: Marks a message as read in the database. </p>
     *
     * @param msg the {@code Message} object to update
     */
    public void markMessageAsRead(Message msg) {
        String query = "UPDATE Message SET isRead = true WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, msg.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            return;
        }
    }
    
    /**
     * <p> Method: void deleteMessage(Message msg) </p>
	 * 
	 * <p> Description: Deletes a message from the Message table. </p>
     *
     * @param msg the {@code Message} object to delete
     */
    public void deleteMessage(Message msg) {
        String query = "DELETE FROM Message WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, msg.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            return;
        }
    }
    
	/*******
	 * <p> Method: void clearDatabase()</p>
	 * 
	 * <p> Description: Clear the database.</p>
	 * 
	 * @throws SQLException if issue occurred
	 */
	public void clearDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
}