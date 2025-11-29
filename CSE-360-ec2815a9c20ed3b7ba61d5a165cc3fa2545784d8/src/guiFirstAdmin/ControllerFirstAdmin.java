package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";		
	private static String adminEmail = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	
	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		if (adminPassword1.length() <= 32) {
			ViewFirstAdmin.label_ShowPassword.setText(adminPassword1);
		}
		else {
			ViewFirstAdmin.label_ShowPassword.setText("Too long!");
		}
		return;
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		return;
	}
	
	protected static void setAdminEmail() {
		adminEmail = ViewFirstAdmin.text_AdminEmail.getText();		
		return;
	}
	
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		
		// Make sure the username is valid
		String usernameError = ModelFirstAdmin.checkValidUsername(adminUsername);
		if (!usernameError.isEmpty()) {
			ViewFirstAdmin.alertSignUpError.setTitle("Username Invalid");
			ViewFirstAdmin.alertSignUpError.setHeaderText(usernameError);
			ViewFirstAdmin.alertSignUpError.setContentText(
					"Needs at least 4 characters\n"
					+ "May use any upper or lower case alphabet\n"
					+ "May use any number\n"
					+ "May use special characters: \".\", \"-\", \"_\"\n"
					+ "Must not use special characters at the end\n"
					+ "Must not exceed 16 characters");
			ViewFirstAdmin.alertSignUpError.showAndWait();
			System.out.println(usernameError);
			return;
		}
		System.out.println("Username is valid");
		
		// Make sure the password is valid
		String passwordError = ModelFirstAdmin.checkValidPassword(adminPassword1);
		if (!passwordError.isEmpty()) {
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.alertSignUpError.setTitle("Password Invalid");
			ViewFirstAdmin.alertSignUpError.setHeaderText(passwordError);
			ViewFirstAdmin.alertSignUpError.setContentText(
					"Needs at least one uppercase alphabet\n"
					+ "Needs at least one lowercase alphabet\n"
					+ "Needs at least one number\n"
					+ "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n"
					+ "Needs to be within 8-32 characters");
			ViewFirstAdmin.alertSignUpError.showAndWait();
			System.out.println(passwordError);
			return;
		}
		System.out.println("Password is valid");
		
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) != 0) {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.alertSignUpError.setTitle("Passwords Do Not Match");
			ViewFirstAdmin.alertSignUpError.setHeaderText("The two passwords must be identical.");
			ViewFirstAdmin.alertSignUpError.setContentText("Correct the passwords and try again.");
			ViewFirstAdmin.alertSignUpError.showAndWait();
			System.out.println("The two passwords do not match");
    		return;
		}
		System.out.println("Passwords matched");
		
		// Make sure the email is valid
		String emailError = ModelFirstAdmin.checkValidEmail(adminEmail);
		if (!emailError.isEmpty()) {
			ViewFirstAdmin.alertSignUpError.setTitle("Email Invalid");
			ViewFirstAdmin.alertSignUpError.setHeaderText(emailError);
			ViewFirstAdmin.alertSignUpError.setContentText("Email address must be in the correct format: xxx@yyy(.zzz).");
			ViewFirstAdmin.alertSignUpError.showAndWait();
			System.out.println(emailError);
    		return;
		}
		System.out.println("Email is valid");
		
		// Sign up success
		// Create the passwords and proceed to the user home page
    	User user = new User(adminUsername, adminPassword1, "", "", "", "", adminEmail, true, false, false);
        try {
        	// Create a new User object with admin role and register in the database
        	theDatabase.register(user);
        	}
        catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to register a user: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        
        // User was established in the database, so navigate to the User Login Page
    	guiUserLogin.ViewUserLogin.displayUserLogin(ViewFirstAdmin.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}