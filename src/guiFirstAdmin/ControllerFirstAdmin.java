package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerFirstAdmin Class. </p>
 * 
 * <p> Description: This class provides controller actions for the First Admin setup page.
 * All methods are static and are invoked by the View singleton. It allows the first admin
 * to enter a username, password, email, and register themselves in the database. Validations
 * for username, password, and email are included. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerFirstAdmin {

    /*-********************************************************************************************
     * Fields
     */

    /** Holds the last entered admin username */
    private static String adminUsername = "";

    /** Holds the last entered admin password (first field) */
    private static String adminPassword1 = "";

    /** Holds the last entered admin password (confirmation field) */
    private static String adminPassword2 = "";  

    /** Holds the last entered admin email */
    private static String adminEmail = "";

    /** Reference to the in-memory database */
    protected static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Default constructor </p>
     * <p> Description: ControllerFirstAdmin is not intended to be instantiated, but a default
     * constructor is included for completeness. </p>
     */
    protected ControllerFirstAdmin() {
        // No implementation needed, class is fully static
    }

    /**********
     * <p> Method: setAdminUsername </p>
     * <p> Description: Called when the user types into the username field in the view. Updates
     * the local copy of the username. </p>
     */
    protected static void setAdminUsername() {
        adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
    }

    /**********
     * <p> Method: setAdminPassword1 </p>
     * <p> Description: Called when the user types into the first password field. Updates the
     * local copy and shows the password (or error if too long) in the view. </p>
     */
    protected static void setAdminPassword1() {
        adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
        if (adminPassword1.length() <= 32) {
            ViewFirstAdmin.label_ShowPassword.setText(adminPassword1);
        } else {
            ViewFirstAdmin.label_ShowPassword.setText("Too long!");
        }
    }

    /**********
     * <p> Method: setAdminPassword2 </p>
     * <p> Description: Called when the user types into the second password field. Updates the
     * local copy of the confirmation password. </p>
     */
    protected static void setAdminPassword2() {
        adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();        
    }

    /**********
     * <p> Method: setAdminEmail </p>
     * <p> Description: Called when the user types into the email field. Updates the local
     * copy of the email address. </p>
     */
    protected static void setAdminEmail() {
        adminEmail = ViewFirstAdmin.text_AdminEmail.getText();        
    }

    /**********
     * <p> Method: doSetupAdmin </p>
     * <p> Description: Called when the user presses the button to set up the admin account.
     * Validates username, password, password confirmation, and email. If all validations
     * succeed, registers the new admin user in the database and navigates to the login page.</p>
     * 
     * @param ps The stage of the current view
     * @param r  Role number (used internally, typically admin role)
     */
    protected static void doSetupAdmin(Stage ps, int r) {

        // Validate username
        String usernameError = ModelFirstAdmin.checkValidUsername(adminUsername);
        if (!usernameError.isEmpty()) {
            ViewFirstAdmin.alertSignUpError.setTitle("Username Invalid");
            ViewFirstAdmin.alertSignUpError.setHeaderText(usernameError);
            ViewFirstAdmin.alertSignUpError.setContentText(
                "Needs at least 4 characters\n" +
                "May use any upper or lower case alphabet\n" +
                "May use any number\n" +
                "May use special characters: \".\", \"-\", \"_\"\n" +
                "Must not use special characters at the end\n" +
                "Must not exceed 16 characters");
            ViewFirstAdmin.alertSignUpError.showAndWait();
            System.out.println(usernameError);
            return;
        }
        System.out.println("Username is valid");

        // Validate password
        String passwordError = ModelFirstAdmin.checkValidPassword(adminPassword1);
        if (!passwordError.isEmpty()) {
            ViewFirstAdmin.text_AdminPassword1.setText("");
            ViewFirstAdmin.text_AdminPassword2.setText("");
            ViewFirstAdmin.alertSignUpError.setTitle("Password Invalid");
            ViewFirstAdmin.alertSignUpError.setHeaderText(passwordError);
            ViewFirstAdmin.alertSignUpError.setContentText(
                "Needs at least one uppercase alphabet\n" +
                "Needs at least one lowercase alphabet\n" +
                "Needs at least one number\n" +
                "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n" +
                "Needs to be within 8-32 characters");
            ViewFirstAdmin.alertSignUpError.showAndWait();
            System.out.println(passwordError);
            return;
        }
        System.out.println("Password is valid");

        // Validate password confirmation
        if (!adminPassword1.equals(adminPassword2)) {
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

        // Validate email
        String emailError = ModelFirstAdmin.checkValidEmail(adminEmail);
        if (!emailError.isEmpty()) {
            ViewFirstAdmin.alertSignUpError.setTitle("Email Invalid");
            ViewFirstAdmin.alertSignUpError.setHeaderText(emailError);
            ViewFirstAdmin.alertSignUpError.setContentText(
                "Email address must be in the correct format: xxx@yyy(.zzz).");
            ViewFirstAdmin.alertSignUpError.showAndWait();
            System.out.println(emailError);
            return;
        }
        System.out.println("Email is valid");

        // Register new admin user
        User user = new User(adminUsername, adminPassword1, "", "", "", "", adminEmail, true, false, false);
        try {
            theDatabase.register(user);
        } catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to register a user: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        // Navigate to User Login page
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewFirstAdmin.theStage);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates program execution gracefully. Leaves the database
     * ready for next startup. </p>
     */
    protected static void performQuit() {
        System.out.println("Perform Quit");
        System.exit(0);
    }    
}
