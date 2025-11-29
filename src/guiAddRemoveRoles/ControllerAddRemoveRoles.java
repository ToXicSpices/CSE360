package guiAddRemoveRoles;

import database.Database;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;

/*******
 * <p> Title: ControllerAddRemoveRoles Class. </p>
 * 
 * <p> Description: Controller for the Add/Remove Roles page. This class provides the actions
 * for the GUI, managing users and their assigned roles. It is implemented as a static
 * class (all methods are static), so it is not instantiated. The view is a singleton, and
 * the database is accessed via a shared reference.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-04-20 Initial version
 */
public class ControllerAddRemoveRoles {

	/**Default constructor, not used*/
	private ControllerAddRemoveRoles() {}
	
    /*-********************************************************************************************
     * Database Reference
     */

    /** Reference to the in-memory database for accessing and updating user roles */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /*-********************************************************************************************
     * User Interface Actions
     */

    /**********
     * <p> Method: doSelectUser </p>
     * <p> Description: Fetches the selected user from the ComboBox, retrieves that user's
     * account details from the database, and updates the related widgets to reflect
     * the currently selected user.</p>
     * 
     */
    protected static void doSelectUser() {
        ViewAddRemoveRoles.theSelectedUser = 
                (String) ViewAddRemoveRoles.combobox_SelectUser.getValue();
        theDatabase.getUserAccountDetails(ViewAddRemoveRoles.theSelectedUser);
        setupSelectedUser();
    }

    /**********
     * <p> Method: repaintTheWindow </p>
     * <p> Description: Determines the current state of the page and displays the appropriate
     * widgets on the pane. If no user is selected, shows only the user selection interface.
     * Otherwise, shows the full role management interface for the selected user.</p>
     * 
     */
    protected static void repaintTheWindow() {
        // Clear existing widgets
        ViewAddRemoveRoles.theRootPane.getChildren().clear();

        if (ViewAddRemoveRoles.theSelectedUser.compareTo("<Select a User>") == 0) {
            // Display only selection interface
            ViewAddRemoveRoles.theRootPane.getChildren().addAll(
                ViewAddRemoveRoles.label_PageTitle,
                ViewAddRemoveRoles.label_UserDetails,
                ViewAddRemoveRoles.button_UpdateThisUser,
                ViewAddRemoveRoles.line_Separator1,
                ViewAddRemoveRoles.label_SelectUser,
                ViewAddRemoveRoles.combobox_SelectUser,
                ViewAddRemoveRoles.line_Separator4,
                ViewAddRemoveRoles.button_Return,
                ViewAddRemoveRoles.button_Logout,
                ViewAddRemoveRoles.button_Quit
            );
        } else {
            // Display full interface for managing roles
            ViewAddRemoveRoles.theRootPane.getChildren().addAll(
                ViewAddRemoveRoles.label_PageTitle,
                ViewAddRemoveRoles.label_UserDetails,
                ViewAddRemoveRoles.button_UpdateThisUser,
                ViewAddRemoveRoles.line_Separator1,
                ViewAddRemoveRoles.label_SelectUser,
                ViewAddRemoveRoles.combobox_SelectUser,
                ViewAddRemoveRoles.label_CurrentRoles,
                ViewAddRemoveRoles.label_SelectRoleToBeAdded,
                ViewAddRemoveRoles.combobox_SelectRoleToAdd,
                ViewAddRemoveRoles.button_AddRole,
                ViewAddRemoveRoles.label_SelectRoleToBeRemoved,
                ViewAddRemoveRoles.combobox_SelectRoleToRemove,
                ViewAddRemoveRoles.button_RemoveRole,
                ViewAddRemoveRoles.line_Separator4,
                ViewAddRemoveRoles.button_Return,
                ViewAddRemoveRoles.button_Logout,
                ViewAddRemoveRoles.button_Quit
            );
        }

        // Set stage title and scene
        ViewAddRemoveRoles.theStage.setTitle("CSE 360 Foundation Code: Admin Operations Page");
        ViewAddRemoveRoles.theStage.setScene(ViewAddRemoveRoles.theAddRemoveRolesScene);
        ViewAddRemoveRoles.theStage.show();
    }

    /**********
     * <p> Method: setupSelectedUser </p>
     * <p> Description: Updates the ComboBoxes and labels to reflect the roles the selected
     * user currently has and what roles can be added or removed.</p>
     * 
     */
    private static void setupSelectedUser() {
        System.out.println("*** Entering setupSelectedUser");

        // Prepare list of roles to add
        ViewAddRemoveRoles.addList.clear();
        ViewAddRemoveRoles.addList.add("<Select a role>");
        if (!theDatabase.getCurrentAdminRole())
            ViewAddRemoveRoles.addList.add("Admin");
        if (!theDatabase.getCurrentStudentRole())
            ViewAddRemoveRoles.addList.add("Student");
        if (!theDatabase.getCurrentStaffRole())
            ViewAddRemoveRoles.addList.add("Staff");

        // Prepare list of roles to remove
        ViewAddRemoveRoles.removeList.clear();
        ViewAddRemoveRoles.removeList.add("<Select a role>");
        if (theDatabase.getCurrentAdminRole())
            ViewAddRemoveRoles.removeList.add("Admin");
        if (theDatabase.getCurrentStudentRole())
            ViewAddRemoveRoles.removeList.add("Student");
        if (theDatabase.getCurrentStaffRole())
            ViewAddRemoveRoles.removeList.add("Staff");

        // Build string showing current roles
        boolean notTheFirst = false;
        String theCurrentRoles = "";

        if (theDatabase.getCurrentAdminRole()) {
            theCurrentRoles += "Admin";
            notTheFirst = true;
        }
        if (theDatabase.getCurrentStudentRole()) {
            if (notTheFirst)
                theCurrentRoles += ", Student";
            else {
                theCurrentRoles += "Student";
                notTheFirst = true;
            }
        }
        if (theDatabase.getCurrentStaffRole()) {
            if (notTheFirst)
                theCurrentRoles += ", Staff";
            else {
                theCurrentRoles += "Staff";
                notTheFirst = true;
            }
        }

        // Update UI widgets
        ViewAddRemoveRoles.label_CurrentRoles.setText("This user's current roles: " + theCurrentRoles);
        ViewAddRemoveRoles.setupComboBoxUI(ViewAddRemoveRoles.combobox_SelectRoleToAdd, "Dialog",
                16, 150, 280, 205);
        ViewAddRemoveRoles.combobox_SelectRoleToAdd.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.addList));
        ViewAddRemoveRoles.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);
        ViewAddRemoveRoles.setupButtonUI(ViewAddRemoveRoles.button_AddRole, "Dialog", 16, 150, Pos.CENTER, 460, 205);
        ViewAddRemoveRoles.setupComboBoxUI(ViewAddRemoveRoles.combobox_SelectRoleToRemove, "Dialog",
                16, 150, 280, 275);
        ViewAddRemoveRoles.combobox_SelectRoleToRemove.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.removeList));
        ViewAddRemoveRoles.combobox_SelectRoleToRemove.getSelectionModel().select(0);

        // Refresh UI
        repaintTheWindow();
    }

    /**********
     * <p> Method: performAddRole </p>
     * <p> Description: Adds the selected role from the ComboBox to the user's roles in the database.</p>
     * 
     */
    protected static void performAddRole() {
        ViewAddRemoveRoles.theAddRole = (String) ViewAddRemoveRoles.combobox_SelectRoleToAdd.getValue();

        if (ViewAddRemoveRoles.theAddRole.compareTo("<Select a role>") != 0) {
            if (theDatabase.updateUserRole(ViewAddRemoveRoles.theSelectedUser, ViewAddRemoveRoles.theAddRole, "true")) {
                ViewAddRemoveRoles.combobox_SelectRoleToAdd = new ComboBox<String>();
                ViewAddRemoveRoles.combobox_SelectRoleToAdd.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.addList));
                ViewAddRemoveRoles.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);
                setupSelectedUser();
            }
        }
    }

    /**********
     * <p> Method: performRemoveRole </p>
     * <p> Description: Removes the selected role from the ComboBox from the user's roles in the database.</p>
     * 
     */
    protected static void performRemoveRole() {
        ViewAddRemoveRoles.theRemoveRole = (String) ViewAddRemoveRoles.combobox_SelectRoleToRemove.getValue();

        if (ViewAddRemoveRoles.theRemoveRole.compareTo("<Select a role>") != 0) {
            if (theDatabase.updateUserRole(ViewAddRemoveRoles.theSelectedUser, ViewAddRemoveRoles.theRemoveRole, "false")) {
                ViewAddRemoveRoles.combobox_SelectRoleToRemove = new ComboBox<String>();
                ViewAddRemoveRoles.combobox_SelectRoleToRemove.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.addList));
                ViewAddRemoveRoles.combobox_SelectRoleToRemove.getSelectionModel().clearAndSelect(0);
                setupSelectedUser();
            }
        }
    }

    /**********
     * <p> Method: performReturn </p>
     * <p> Description: Returns the Admin user to the Admin Home page.</p>
     * 
     */
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewAddRemoveRoles.theStage, ViewAddRemoveRoles.theUser);
    }

    /**********
     * <p> Method: performLogout </p>
     * <p> Description: Logs out the current user and returns to the normal login page.</p>
     * 
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewAddRemoveRoles.theStage);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates program execution and exits the application.</p>
     * 
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
