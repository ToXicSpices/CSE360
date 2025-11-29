package guiUserLogin;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

/**
 * <p> Title: ControllerUserLogin Class </p>
 * 
 * <p> Description: This controller handles the actions for the guiUserLogin view.
 * It provides static methods for login, account setup, password recovery, and quitting the application. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerUserLogin {
	/**Default constructor, not used*/
	private ControllerUserLogin() {}
	
    /*-********************************************************************************************
     * User Interface Actions for this page
     **********************************************************************************************/
    
    // Reference for the in-memory database so this package has access
	/** The Database */
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    /** The JavaFX Stage on which this login page is displayed. */
    private static Stage theStage;    
    
    /**********
     * <p> Method: doForgetUsernamePassword </p>
     * <p> Description: Opens the forget username/password view for the user. </p>
     */
    protected static void doForgetUsernamePassword() {
        guiForgetPassword.ViewForgetPassword.displayForgetPassword(ViewUserLogin.theStage);
    }
    
    /**********
     * <p> Method: doLogin </p>
     * <p> Description: Attempts to log in the user. Validates username and password, determines 
     * the number of roles assigned to the user, and navigates to the appropriate home page or 
     * role selection page. </p>
     * @param ts The stage to display the home page or role selection page
     */
    protected static void doLogin(Stage ts) {
        theStage = ts;
        String username = ViewUserLogin.text_Username.getText();
        String password = ViewUserLogin.text_Password.getText();
        boolean loginResult = false;
        
        // Fetch the user and verify the username
        if (!theDatabase.getUserAccountDetails(username)) {
            // Generic error message to avoid leaking information
            ViewUserLogin.alertUsernamePasswordError.setContentText(
                    "Incorrect username/password. Try again!");
            ViewUserLogin.alertUsernamePasswordError.showAndWait();
            return;
        }
        
        // Check password
        String actualPassword = theDatabase.getCurrentPassword();
        if (password.compareTo(actualPassword) != 0) {
            ViewUserLogin.alertUsernamePasswordError.setContentText(
                    "Incorrect username/password. Try again!");
            ViewUserLogin.alertUsernamePasswordError.showAndWait();
            return;
        }
        
        // Establish this user's details
        User user = new User(username, password, theDatabase.getCurrentFirstName(), 
                theDatabase.getCurrentMiddleName(), theDatabase.getCurrentLastName(), 
                theDatabase.getCurrentPreferredFirstName(), theDatabase.getCurrentEmailAddress(), 
                theDatabase.getCurrentAdminRole(), 
                theDatabase.getCurrentStudentRole(), theDatabase.getCurrentStaffRole());
        
        // Determine number of roles
        int numberOfRoles = theDatabase.getNumberOfRoles(user);        
        
        if (numberOfRoles == 1) {
            // Single Account Home Page
            if (user.getAdminRole()) {
                loginResult = theDatabase.loginAdmin(user);
                if (loginResult) guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
            } else if (user.getStudentRole()) {
                loginResult = theDatabase.loginStudent(user);
                if (loginResult) guiStudentHome.ViewStudentHome.displayStudentHome(theStage, user);
            } else if (user.getStaffRole()) {
                loginResult = theDatabase.loginStaff(user);
                if (loginResult) guiStaffHome.ViewStaffHome.displayStaffHome(theStage, user);
            } else {
                System.out.println("***** UserLogin goToUserHome request has an invalid role");
            }
        } else if (numberOfRoles > 1) {
            // Multiple Account Home Page - role selection
            System.out.println("*** Going to displayMultipleRoleDispatch");
            guiMultipleRoleDispatch.ViewMultipleRoleDispatch.displayMultipleRoleDispatch(theStage, user);
        }
    }
    
    /**********
     * <p> Method: doSetupAccount </p>
     * <p> Description: Opens the new account setup view for a user with an invitation code. </p>
     * @param theStage The stage to display the new account page
     * @param invitationCode The invitation code provided for creating a new account
     */
    protected static void doSetupAccount(Stage theStage, String invitationCode) {
        guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates the execution of the application. All data should already be
     * saved in the database. </p>
     */
    protected static void performQuit() {
        System.out.println("Perform Quit");
        System.exit(0);
    }    
}
