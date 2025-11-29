package guiNewAccount;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;

/**
 * <p> Title: ControllerNewAccount Class </p>
 * 
 * <p> Description: This controller handles the actions for the guiNewAccount view.
 * It provides static methods to set the password, create new users, and quit the application.
 * All methods interact directly with the View and the in-memory Database. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerNewAccount {

	/**Default constructor, not used*/
	private ControllerNewAccount() {}
	
    /*-********************************************************************************************
     * Fields
     */

    /** Reference to the in-memory database so this package has access */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: setPassword </p>
     * <p> Description: Sets the password field in the UI and displays it in the label
     * if it does not exceed 32 characters. </p>
     */
    protected static void setPassword() {
        String password = ViewNewAccount.text_Password1.getText();
        if (password.length() <= 32) {
            ViewNewAccount.label_ShowPassword.setText(password);
        } else {
            ViewNewAccount.label_ShowPassword.setText("Too long!");
        }
    }

    /**********
     * <p> Method: doCreateUser </p>
     * <p> Description: Creates a new user account when the User Setup button is clicked.
     * Validates username, password, and confirmation password. Checks for uniqueness of username.
     * Assigns role based on invitation code and sets the email. If validation passes, the user
     * is registered in the database and navigates to the User Update page. </p>
     */
    protected static void doCreateUser() {

        // Fetch the username and password
        String username = ViewNewAccount.text_Username.getText();
        String password = ViewNewAccount.text_Password1.getText();

        System.out.println("** Account for Username: " + username + "; theInvitationCode: " +
                ViewNewAccount.theInvitationCode + "; email address: " +
                ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);

        int roleCode = 0;
        User user = null;

        // Check for existing username
        if (theDatabase.getUserAccountDetails(username)) {
            ViewNewAccount.alertUsernamePasswordError.setTitle("Username Invalid");
            ViewNewAccount.alertUsernamePasswordError.setHeaderText(null);
            ViewNewAccount.alertUsernamePasswordError.setContentText("Username already in use");
            ViewNewAccount.alertUsernamePasswordError.showAndWait();
            return;
        }

        // Validate username
        String usernameError = ModelNewAccount.checkValidUsername(username);
        if (!usernameError.isEmpty()) {
            ViewNewAccount.alertUsernamePasswordError.setTitle("Username Invalid");
            ViewNewAccount.alertUsernamePasswordError.setHeaderText(usernameError);
            ViewNewAccount.alertUsernamePasswordError.setContentText(
                    "Needs at least 4 characters\n"
                    + "May use any upper or lower case alphabet\n"
                    + "May use any number\n"
                    + "May use special characters: \".\", \"-\", \"_\"\n"
                    + "Must not use special characters at the end\n"
                    + "Must not exceed 16 characters");
            ViewNewAccount.alertUsernamePasswordError.showAndWait();
            System.out.println(usernameError);
            return;
        }
        System.out.println("Username is valid");

        // Validate password
        String passwordError = ModelNewAccount.checkValidPassword(password);
        if (!passwordError.isEmpty()) {
            ViewNewAccount.text_Password1.setText("");
            ViewNewAccount.text_Password2.setText("");
            ViewNewAccount.alertUsernamePasswordHelp.setTitle("Username/Password Help");
            ViewNewAccount.alertUsernamePasswordHelp.setHeaderText("Password Requirements:");
            ViewNewAccount.alertUsernamePasswordHelp.setContentText(
                    "Needs at least one uppercase alphabet\n"
                    + "Needs at least one lowercase alphabet\n"
                    + "Needs at least one number\n"
                    + "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n"
                    + "Needs to be within 8-32 characters");
            ViewNewAccount.alertUsernamePasswordHelp.showAndWait();
            System.out.println(passwordError);
            return;
        }
        System.out.println("Password is valid");

        // Confirm passwords match
        if (!ViewNewAccount.text_Password1.getText().equals(ViewNewAccount.text_Password2.getText())) {
            ViewNewAccount.text_Password1.setText("");
            ViewNewAccount.text_Password2.setText("");
            ViewNewAccount.alertUsernamePasswordError.setTitle("Passwords Do Not Match");
            ViewNewAccount.alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
            ViewNewAccount.alertUsernamePasswordError.setContentText("Correct the passwords and try again.");
            ViewNewAccount.alertUsernamePasswordError.showAndWait();
            return;
        }
        System.out.println("Passwords matched");

        // Assign role based on invitation
        switch (ViewNewAccount.theRole) {
            case "Admin":
                roleCode = 1;
                user = new User(username, password, "", "", "", "", "", true, false, false);
                break;
            case "Student":
                roleCode = 2;
                user = new User(username, password, "", "", "", "", "", false, true, false);
                break;
            case "Staff":
                roleCode = 3;
                user = new User(username, password, "", "", "", "", "", false, false, true);
                break;
            default:
                System.out.println("**** Trying to create a New Account for a role that does not exist!");
                System.exit(0);
        }

        // Set email and active role
        user.setEmailAddress(ViewNewAccount.emailAddress);
        applicationMain.FoundationsMain.activeHomePage = roleCode;

        // Register user in database
        try {
            theDatabase.register(user);
        } catch (SQLException e) {
            System.err.println("*** ERROR *** Database error: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        // Remove invitation after use
        theDatabase.removeInvitationAfterUse(ViewNewAccount.theInvitationCode);

        // Fetch user details
        theDatabase.getUserAccountDetails(username);

        // Navigate to the User Update page
        guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates execution of the program with exit code 0. </p>
     */
    protected static void performQuit() {
        System.out.println("Perform Quit");
        System.exit(0);
    }
}
