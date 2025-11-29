package guiAdminHome;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.ButtonType;

import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ControllerAdminHome Class. </p>
 * 
 * <p> Description: This class provides the controller actions for the Admin Home Page.
 * All methods are static and are invoked by the View singleton. It allows management
 * of users, sending invitations, handling passwords, deleting users, listing users, and
 * adding/removing roles. Most of these methods use JavaFX components to interact with
 * the user.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerAdminHome {

	/**Default constructor, not used*/
	private ControllerAdminHome() {}
	
    /*-*******************************************************************************************
     * Fields
     */

    /** Reference to the in-memory database for accessing user and invitation data */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: performInvitation </p>
     * <p> Description: Sends an email invitation for a potential user to create an account.
     * Checks that the email is valid and has not been previously used. Displays
     * confirmation alerts.</p>
     * 
     */
    protected static void performInvitation() {
        String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
        if (invalidEmailAddress(emailAddress)) {
            return;
        }

        if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
            ViewAdminHome.alertEmailError.setContentText(
                    "An invitation has already been sent to this email address.");
            ViewAdminHome.alertEmailError.showAndWait();
            return;
        }

        String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
        String invitationCode = theDatabase.generateInvitationCode(emailAddress, theSelectedRole);
        String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
                     " was sent to: " + emailAddress;
        System.out.println(msg);
        ViewAdminHome.alertEmailSent.setContentText(msg);
        ViewAdminHome.alertEmailSent.showAndWait();

        ViewAdminHome.text_InvitationEmailAddress.setText("");
        ViewAdminHome.label_Invitations.setText("Send An Invitation (Number of outstanding invitations: "
        + theDatabase.getNumberOfInvitations() + ")");
    }

    /**********
     * <p> Method: deleteUser </p>
     * <p> Description: Prompts for a username and deletes that user from the system.
     * Updates the Admin Home UI to reflect the current number of users. Shows
     * alerts if the user does not exist or confirms deletion.</p>
     * 
     */
    protected static void deleteUser() {
    	Optional<String> result = ViewAdminHome.deleteUserDialog.showAndWait();
        if (!result.isPresent()) return;

        String userName = result.get().trim();
        Optional<ButtonType> result2 = ViewAdminHome.alert_DeleteUser.showAndWait();
        if (result2.isPresent() && result2.get() == ButtonType.NO) return;
        List<User> users = theDatabase.getAllUsers();
        Optional<User> match = users.stream().filter(u -> u.getUserName().equals(userName)).findFirst();
        
        if (!match.isPresent()) {
        	ViewAdminHome.deleteUserNotFound.setContentText("No user with username \"" + userName + "\" was found.");
        	ViewAdminHome.deleteUserNotFound.showAndWait();
            return;
        }
        
        theDatabase.deleteUser(userName);

        ViewAdminHome.deleteUserDeleted.setContentText("User \"" + userName + "\" has been deleted.");
        ViewAdminHome.deleteUserDeleted.showAndWait();
        
        ViewAdminHome.label_NumberOfUsers.setText("Number of users: " + theDatabase.getNumberOfUsers());
        
        ViewAdminHome.createUserListTableView();
    }

    /**********
     * <p> Method: addRemoveRoles </p>
     * <p> Description: Opens the Add/Remove Roles page for the selected user.</p>
     * 
     */
    protected static void addRemoveRoles() {
        guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
                ViewAdminHome.theUser);
    }

    /**********
     * <p> Method: invalidEmailAddress </p>
     * <p> Description: Validates the given email address and displays an alert if invalid.
     * Currently only checks for non-empty and syntactic validity.</p>
     * 
     * @param emailAddress The email address string to validate
     * @return boolean True if the email is invalid, false otherwise
     */
    protected static boolean invalidEmailAddress(String emailAddress) {
        if (emailAddress.length() == 0) {
            ViewAdminHome.alertEmailError.setContentText(
                    "Correct the email address and try again.");
            ViewAdminHome.alertEmailError.showAndWait();
            return true;
        } else if (!ModelAdminHome.checkValidEmail(emailAddress).isEmpty()) {
            ViewAdminHome.alertEmailError.setContentText(
                    "Correct the email address and try again.");
            ViewAdminHome.alertEmailError.showAndWait();
            return true;
        }
        return false;
    }

    /**********
     * <p> Method: performLogout </p>
     * <p> Description: Logs the current admin user out and returns to the login page.</p>
     * 
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates program execution.</p>
     * 
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
