package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;

import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;

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

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewStudent;
	private boolean currentNewStaff;

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
			//statement.execute("DROP ALL OBJECTS");

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
				+ "newStudent BOOL DEFAULT FALSE, "
				+ "newStaff BOOL DEFAULT FALSE)";
		statement.execute(userTable); 
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    
	    // Create the One-time Passwords and Requests table
	    String oneTimePasscodesTable = "CREATE TABLE IF NOT EXISTS OneTimePasscodes ("
	            + "passcode VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255))";
	    statement.execute(oneTimePasscodesTable);
	   
	    // Create Threads table
	    String threadTable = "CREATE TABLE IF NOT EXISTS Threads ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "threadName VARCHAR(255) UNIQUE)";
	    statement.execute(threadTable);
	    
	    // Create Posts table
	    String postTable = "CREATE TABLE IF NOT EXISTS Posts ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "title VARCHAR(255) UNIQUE, "
	            + "subtitle VARCHAR(255), "
	            + "content VARCHAR(2200), "
	            + "owner VARCHAR(255), "
	            + "thread INT DEFAULT 1, "
	            + "tags VARCHAR(255), "
	            + "grade VARCHAR(10), "
	            + "feedback VARCHAR(1000), "
	            + "gradedBy VARCHAR(255), "
	            + "gradeReleased BOOL DEFAULT FALSE, "
	            + "FOREIGN KEY (thread) REFERENCES Threads(id) ON DELETE SET DEFAULT)";
	    statement.execute(postTable);

	    // Defensive: ensure columns exist if schema already created without them
	    try { statement.execute("ALTER TABLE Posts ADD COLUMN IF NOT EXISTS grade VARCHAR(10)"); } catch (SQLException ignore) {}
	    try { statement.execute("ALTER TABLE Posts ADD COLUMN IF NOT EXISTS feedback VARCHAR(1000)"); } catch (SQLException ignore) {}
	    try { statement.execute("ALTER TABLE Posts ADD COLUMN IF NOT EXISTS gradedBy VARCHAR(255)"); } catch (SQLException ignore) {}
	    try { statement.execute("ALTER TABLE Posts ADD COLUMN IF NOT EXISTS gradeReleased BOOL DEFAULT FALSE"); } catch (SQLException ignore) {}

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
				+ "lastName, preferredFirstName, emailAddress, adminRole, newStudent, newStaff) "
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
			
			currentNewStudent = user.getNewStudent();
			pstmt.setBoolean(9, currentNewStudent);
			
			currentNewStaff = user.getNewStaff();
			pstmt.setBoolean(10, currentNewStaff);
			
			pstmt.executeUpdate();
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
	            user.setStudentUser(rs.getBoolean("newStudent"));
	            user.setStaffUser(rs.getBoolean("newStaff"));
	            users.add(user);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
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
				+ "newStudent = TRUE";
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
				+ "newStaff = TRUE";
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
		if (user.getNewStudent()) numberOfRoles++;
		if (user.getNewStaff()) numberOfRoles++;
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
	// For a given invitation code, return the associated email address of an empty string
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

	    String query = "UPDATE OneTimePasscodes SET passcode = ? WHERE emailAddress = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, passcode);
	        pstmt.setString(2, emailAddress);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return passcode;
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
	 * <p> Method: isValidOneTimePasscode(String email, String passcode) </p>
	 * 
	 * <p> Description: check whether is the input email and one-time password is on the table.</p>
	 *  
	 * @param email the input email that needs to be checked
	 *  
	 * @param passcode the input one-time password that needs to be checked 
	 *  
	 * @return true if there exists an entry on the table.
	 */
	public boolean isValidOneTimePasscode(String email, String passcode) {
	    String query = "SELECT COUNT(*) AS count FROM OneTimePasscodes WHERE emailAddress = ? AND passcode = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, passcode);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            int count = rs.getInt("count");
	            return count > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/*******
	 * <p> Method: removePasscodeAfterUse(String passcode) </p>
	 * 
	 * <p> Description: remove the one-time password on the table after user successfullt resets
	 * the password.</p>
	 *  
	 * @param passcode the one-time password that needs to be removed 
	 */
	public void removePasscodeAfterUse(String passcode) {
	    String query = "SELECT COUNT(*) AS count FROM OneTimePasscodes WHERE passcode = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, passcode);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the onetimepasscode table
	        	if (counter > 0) {
        			query = "DELETE FROM OneTimePasscodes WHERE passcode = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, passcode);
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
	    	currentNewStudent = rs.getBoolean(10);
	    	currentNewStaff = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	/*******
	 * <p> String getUsernameByEmail(String email) </p>
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
			String query = "UPDATE userDB SET newStudent = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStudent = true;
				else
					currentNewStudent = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET newStaff = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewStaff = true;
				else
					currentNewStaff = false;
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
	 * <p> Method: boolean getCurrentNewStudent() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewStudent() { return currentNewStudent;};
	
	
	/*******
	 * <p> Method: boolean getCurrentNewStaff() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewStaff() { return currentNewStaff;};
	 
	
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
	            post.setGrade(rs.getString("grade"));
	            post.setFeedback(rs.getString("feedback"));
	            post.setGradedBy(rs.getString("gradedBy"));
	            post.setGradeReleased(rs.getBoolean("gradeReleased"));
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
	            post.setGrade(rs.getString("grade"));
	            post.setFeedback(rs.getString("feedback"));
	            post.setGradedBy(rs.getString("gradedBy"));
	            post.setGradeReleased(rs.getBoolean("gradeReleased"));
	            posts.add(post);
	        }

	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return posts;
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

	/* Grading Feature Methods */

	/** Assign or update a grade and feedback for a post */
	public void setPostGrade(int postId, String grade, String feedback, String gradedBy) {
		String query = "UPDATE Posts SET grade = ?, feedback = ?, gradedBy = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, grade);
			pstmt.setString(2, feedback);
			pstmt.setString(3, gradedBy);
			pstmt.setInt(4, postId);
			pstmt.executeUpdate();
		} catch (SQLException e) { e.printStackTrace(); }
	}

	/** Release grades (make them visible) for all graded posts */
	public void releaseAllGrades() {
		String query = "UPDATE Posts SET gradeReleased = TRUE WHERE grade IS NOT NULL";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.executeUpdate();
		} catch (SQLException e) { e.printStackTrace(); }
	}

	/** Get list of all posts that have been graded */
	public ArrayList<Post> getAllGradedPosts() {
		ArrayList<Post> graded = new ArrayList<>();
		String query = "SELECT * FROM Posts WHERE grade IS NOT NULL ORDER BY id DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				Post p = new Post(
					rs.getString("title"),
					rs.getString("subtitle"),
					rs.getString("content"),
					rs.getString("owner"),
					getPostTags(id),
					getThreadName(rs.getInt("thread"))
				);
				p.setPostId(id);
				p.setGrade(rs.getString("grade"));
				p.setFeedback(rs.getString("feedback"));
				p.setGradedBy(rs.getString("gradedBy"));
				p.setGradeReleased(rs.getBoolean("gradeReleased"));
				graded.add(p);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return graded;
	}

	/** Get list of graded & released posts for a specific user */
	public ArrayList<Post> getReleasedGradesForUser(String owner) {
		ArrayList<Post> graded = new ArrayList<>();
		String query = "SELECT * FROM Posts WHERE owner = ? AND grade IS NOT NULL AND gradeReleased = TRUE ORDER BY id DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, owner);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				Post p = new Post(
					rs.getString("title"),
					rs.getString("subtitle"),
					rs.getString("content"),
					rs.getString("owner"),
					getPostTags(id),
					getThreadName(rs.getInt("thread"))
				);
				p.setPostId(id);
				p.setGrade(rs.getString("grade"));
				p.setFeedback(rs.getString("feedback"));
				p.setGradedBy(rs.getString("gradedBy"));
				p.setGradeReleased(rs.getBoolean("gradeReleased"));
				graded.add(p);
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return graded;
	}

	/** Determine if user is a student */
	public boolean isUserStudent(String userName) {
		String query = "SELECT newStudent FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getBoolean(1);
		} catch (SQLException e) { e.printStackTrace(); }
		return false;
	}

	/** Get a single post's grade information */
	public String getPostGrade(int postId) {
		String query = "SELECT grade FROM Posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getString(1);
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}

	public String getPostFeedback(int postId) {
		String query = "SELECT feedback FROM Posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getString(1);
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}

	public boolean isPostGradeReleased(int postId) {
		String query = "SELECT gradeReleased FROM Posts WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getBoolean(1);
		} catch (SQLException e) { e.printStackTrace(); }
		return false;
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
	 */
	public void updatePostTitle(int postId, String title) {
	    String query = "UPDATE Posts SET title = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, title);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	 */
	public void updatePostSubitle(int postId, String subtitle) {
	    String query = "UPDATE Posts SET subtitle = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, subtitle);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	 */
	public void updatePostContent(int postId, String content) {
	    String query = "UPDATE Posts SET content = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, content);
	        pstmt.setInt(2, postId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	 * <p> Description: empty </p>
	 * @param replyId e
	 * @return e
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
	 */
	public void updateReplyContent(int replyId, String content) {
	    String query = "UPDATE Replies SET content = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, content);
	        pstmt.setInt(2, replyId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
	 */
	public void updateReplyOwnerUsername(int replyId, String owner) {
	    String query = "UPDATE Replies SET owner = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, owner);
	        pstmt.setInt(2, replyId);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
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
    	String sql = "MERGE INTO PostReadStatus (userName, postId, isRead) " +
                "KEY(userName, postId) " +
                "VALUES (?, ?, TRUE)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setInt(2, postId);
            ps.executeUpdate();
        } catch (SQLException e) {
	        e.printStackTrace();
	    }
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
	
	/*******
	 * <p> Method: void clearDatabase()</p>
	 * 
	 * <p> Description: Clear the database.</p>
	 * 
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