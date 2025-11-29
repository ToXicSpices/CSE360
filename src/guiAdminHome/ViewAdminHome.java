package guiAdminHome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import database.Database;
import entityClasses.Request;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;

/*******
 * <p> Title: ViewAdminHome Class. </p>
 * 
 * <p> Description: JavaFX-based Admin Home Page. This class provides GUI widgets
 * to allow an admin to perform tasks such as sending invitations, managing users,
 * and assigning roles. Uses the singleton pattern and follows the MVC View pattern.
 * Only one instance of this class is created.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-08-17 Initial version
 */
public class ViewAdminHome {

    /*-*******************************************************************************************
     * Application & Window Fields
     ********************************************************************************************/

    /** <p>Description: Width of the application window, initialized from main application constants.</p> */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** <p>Description: Height of the application window, initialized from main application constants.</p> */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /*-*******************************************************************************************
     * GUI Area 1: Page title and current user info
     ********************************************************************************************/

    /** <p>Description: Label displaying the page title.</p> */
    protected static Label label_PageTitle = new Label();

    /** <p>Description: Label showing the current user's details.</p> */
    protected static Label label_UserDetails = new Label();

    /** <p>Description: Button for updating the current user's account.</p> */
    protected static Button button_UpdateThisUser = new Button("Account Update");

    /** <p>Description: Horizontal separator line between Area 1 and Area 2.</p> */
    private static Line line_Separator1 = new Line(20, 95, width-20, 95);

    /*-*******************************************************************************************
     * GUI Area 3: Send Invitations
     ********************************************************************************************/

    /** <p>Description: Label indicating the Send Invitations section.</p> */
    protected static Label label_Invitations = new Label("Send An Invitation");

    /** <p>Description: Label for the email address input field.</p> */
    protected static Label label_InvitationEmailAddress = new Label("Email Address");

    /** <p>Description: TextField for entering the invitee's email address.</p> */
    protected static TextField text_InvitationEmailAddress = new TextField();

    /** <p>Description: ComboBox for selecting the role of the invited user.</p> */
    protected static ComboBox<String> combobox_SelectRole = new ComboBox<>();

    /** <p>Description: Array of supported roles for invitation selection.</p> */
    protected static String[] roles = {"Admin", "Student", "Staff"};

    /** <p>Description: Button to send the invitation email.</p> */
    protected static Button button_SendInvitation = new Button("Send Invitation");

    /** <p>Description: Alert displayed when the email format is invalid.</p> */
    protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);

    /** <p>Description: Alert displayed when the invitation is successfully sent.</p> */
    protected static Alert alertEmailSent = new Alert(AlertType.INFORMATION);

    /** <p>Description: Confirmation alert displayed before deleting a user.</p> */
    protected static Alert alert_DeleteUser = new Alert(Alert.AlertType.CONFIRMATION);

    /** <p>Description: Horizontal line separating the invitation area from admin actions.</p> */
    private static Line line_Separator3 = new Line(20, 185, width-20, 185);

    /*-*******************************************************************************************
     * GUI Area 3: Admin actions
     ********************************************************************************************/

    /** <p>Description: Button to open the Manage Invitations window.</p> */
    protected static Button button_ManageInvitations = new Button("Manage Invitations");

    /** <p>Description: Button to generate a one-time password for a user.</p> */
    protected static Button button_SetOnetimePassword = new Button("One-Time Password");

    /** <p>Description: Button to delete a user from the system.</p> */
    protected static Button button_DeleteUser = new Button("Delete a User");

    /** <p>Description: Button to list all users in the system.</p> */
    protected static Button button_ListUsers = new Button("List All Users");

    /** <p>Description: Button to open the Add/Remove Roles interface.</p> */
    protected static Button button_AddRemoveRoles = new Button("Add/Remove Roles");

    /** <p>Description: Button to view system requests submitted by staff users.</p> */
    protected static Button button_systemRequests = new Button("System Requests");

    /** <p>Description: Alert for functionality not yet implemented.</p> */
    protected static Alert alertNotImplemented = new Alert(AlertType.INFORMATION);

    /** <p>Description: Horizontal line separating admin actions from logout area.</p> */
    private static Line line_Separator4 = new Line(20, 525, width-20, 525);

    /*-*******************************************************************************************
     * GUI Area 4: Logout / Quit
     ********************************************************************************************/

    /** <p>Description: Button to log out the current user.</p> */
    protected static Button button_Logout = new Button("Logout");

    /** <p>Description: Button to quit the application.</p> */
    protected static Button button_Quit = new Button("Quit");

    /*-*******************************************************************************************
     * Invitation Table Components
     ********************************************************************************************/

    /** <p>Description: Stage for managing invitations.</p> */
    protected static Stage manageInvitationsStage = new Stage();

    /** <p>Description: TableView displaying invitations.</p> */
    protected static TableView<String> invitationTableView = new TableView<>();

    /** <p>Description: Column showing invited users' email addresses.</p> */
    protected static TableColumn<String, String> invitationEmailColumn = new TableColumn<>("Email");

    /** <p>Description: Column showing invitation codes.</p> */
    protected static TableColumn<String, String> invitationCodesColumn = new TableColumn<>("Codes");

    /** <p>Description: Column showing invited users' role.</p> */
    protected static TableColumn<String, String> invitationRoleColumn = new TableColumn<>("Role");

    /** <p>Description: Column containing a Copy Code button for each invitation.</p> */
    protected static TableColumn<String, String> invitationCopyCodeColumn = new TableColumn<>("Copy");

    /** <p>Description: Column containing actions (e.g., delete) for each invitation.</p> */
    protected static TableColumn<String, String> invitationActionColumn = new TableColumn<>("Action");

    /*-*******************************************************************************************
     * One-Time Password Table Components
     ********************************************************************************************/

    /** <p>Description: Stage for displaying one-time passwords.</p> */
    protected static Stage oneTimePasswordStage = new Stage();

    /** <p>Description: TableView showing one-time password info.</p> */
    protected static TableView<String> oneTimePasswordTableView = new TableView<>();

    /** <p>Description: Column showing username associated with a one-time password.</p> */
    protected static TableColumn<String, String> oneTimePasswordUsernameColumn = new TableColumn<>("Username");

    /** <p>Description: Column showing email associated with a one-time password.</p> */
    protected static TableColumn<String, String> oneTimePasswordEmailColumn = new TableColumn<>("Email");

    /** <p>Description: Column showing the generated one-time password code.</p> */
    protected static TableColumn<String, String> oneTimePasswordCodeColumn = new TableColumn<>("Code");

    /** <p>Description: Column containing actions for one-time password entries.</p> */
    protected static TableColumn<String, String> oneTimePasswordActionColumn = new TableColumn<>("Action");

    /** <p>Description: Column containing decline buttons for one-time password entries.</p> */
    protected static TableColumn<String, String> oneTimePasswordDeclineColumn = new TableColumn<>("Action");

    /*-*******************************************************************************************
     * Delete User Dialog & Alerts
     ********************************************************************************************/

    /** <p>Description: TextInputDialog to enter username for deletion.</p> */
    protected static TextInputDialog deleteUserDialog = new TextInputDialog();

    /** <p>Description: Alert displayed when a user to delete is not found.</p> */
    protected static Alert deleteUserNotFound = new Alert(AlertType.ERROR);

    /** <p>Description: Alert displayed after a user is successfully deleted.</p> */
    protected static Alert deleteUserDeleted = new Alert(AlertType.INFORMATION);

    /*-*******************************************************************************************
     * User List Components
     ********************************************************************************************/

    /** <p>Description: Stage displaying all users.</p> */
    protected static Stage userListStage = new Stage();

    /** <p>Description: TableView displaying all users.</p> */
    protected static TableView<User> userListTableView = new TableView<>();

    /** <p>Description: Column showing user account usernames.</p> */
    protected static TableColumn<User, String> userListUsernameColumn = new TableColumn<>("Username");

    /** <p>Description: Column showing full names of users.</p> */
    protected static TableColumn<User, String> userListNameColumn = new TableColumn<>("Name");

    /** <p>Description: Column showing email addresses of users.</p> */
    protected static TableColumn<User, String> userListEmailColumn = new TableColumn<>("Email");

    /** <p>Description: Column showing roles of users.</p> */
    protected static TableColumn<User, String> userListRolesColumn = new TableColumn<>("Roles");

    /** <p>Description: Column containing action buttons for each user.</p> */
    protected static TableColumn<User, Void> userListActionColumn = new TableColumn<>("Action");

    /** <p>Description: Label displaying total number of users.</p> */
    protected static Label label_NumberOfUsers = new Label("Number of Users: x");

    /** <p>Description: Stage for showing detailed user information.</p> */
    protected static Stage userListDetailStage = new Stage();

    /** <p>Description: VBox layout containing detailed user information widgets.</p> */
    protected static VBox userListDetailLayout = new VBox(10);

    /** <p>Description: Scene showing detailed user info in a popup.</p> */
    protected static Scene userDetailScene = new Scene(userListDetailLayout, 300, 200);

    /** <p>Description: Button to close the user detail scene.</p> */
    protected static Button userDetailCloseButton = new Button("Close");

    /*-*******************************************************************************************
     * System Requests Table Components
     ********************************************************************************************/

    /** <p>Description: TableView displaying system requests.</p> */
    protected static TableView<Request> systemRequestsTableView = new TableView<>();

    /** <p>Description: Column showing username of the requester.</p> */
    protected static TableColumn<Request, String> systemReqUserColumn = new TableColumn<>("Username");

    /** <p>Description: Column showing request title.</p> */
    protected static TableColumn<Request, String> systemReqTitleColumn = new TableColumn<>("Title");

    /** <p>Description: Column showing request status.</p> */
    protected static TableColumn<Request, String> systemReqStatusColumn = new TableColumn<>("Status");

    /** <p>Description: Column containing action buttons for requests.</p> */
    protected static TableColumn<Request, Void> systemReqActionColumn = new TableColumn<>("Action");

    /** <p>Description: Column containing delete buttons for requests.</p> */
    protected static TableColumn<Request, Void> systemReqDeleteColumn = new TableColumn<>("Delete");

    /** <p>Description: Label displaying total number of requests.</p> */
    protected static Label label_NumberOfRequests = new Label();

    /** <p>Description: Stage for showing detailed system request info.</p> */
    protected static Stage systemRequestSubstage = new Stage();

    /*-*******************************************************************************************
     * Singleton & JavaFX References
     ********************************************************************************************/

    /** <p>Description: Singleton instance of this class.</p> */
    private static ViewAdminHome theView;

    /** <p>Description: Reference to the main database.</p> */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /** <p>Description: Primary stage provided by the application.</p> */
    protected static Stage theStage;

    /** <p>Description: Root pane containing all GUI widgets.</p> */
    private static Pane theRootPane;

    /** <p>Description: Currently logged-in user object.</p> */
    protected static User theUser;

    /** <p>Description: Scene containing the admin home GUI.</p> */
    private static Scene theAdminHomeScene;

    /** <p>Description: Role ID corresponding to Admin (1=Admin, 2=Student, 3=Staff).</p> */
    private static final int theRole = 1;

    /*-*******************************************************************************************
     * Methods
     ********************************************************************************************/
    
    /**
     * <p>Method: displayAdminHome(Stage ps, User user)</p>
     *
     * <p>Description: Displays the Admin Home page. Updates dynamic content, sets the scene, 
     * and shows the stage for the currently logged-in user.</p>
     *
     * @param ps JavaFX Stage to display the page
     * @param user The current logged-in User
     */
    public static void displayAdminHome(Stage ps, User user) {
    	
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewAdminHome();

        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;

        clearTables();
        combobox_SelectRole.getSelectionModel().select(0);
        theDatabase.removeExpiredEntries();

        theStage.setTitle("CSE 360 Foundation Code: Admin Home Page");
        theStage.setScene(theAdminHomeScene);
        theStage.show();
    }

    /**
     * <p>Method: ViewAdminHome()</p>
     *
     * <p>Description: Initializes all GUI elements for the Admin Home page. 
     * This constructor is only called once for the singleton instance. Subsequent updates 
     * to the page are handled by displayAdminHome().</p>
     */
    private ViewAdminHome() {
        theRootPane = new Pane();
        theAdminHomeScene = new Scene(theRootPane, width, height);

        // GUI Area 1
        label_PageTitle.setText("Admin Home Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((event) -> ViewUserUpdate.displayUserUpdate(theStage, theUser));

        // GUI Area 2
        setupLabelUI(label_Invitations, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 105);
        label_Invitations.setText("Send An Invitation (Number of outstanding invitations: " + theDatabase.getNumberOfInvitations() + ")");
        setupLabelUI(label_InvitationEmailAddress, "Arial", 16, width, Pos.BASELINE_LEFT, 20, 140);
        setupTextUI(text_InvitationEmailAddress, "Arial", 16, 360, Pos.BASELINE_LEFT, 130, 135, true);

        setupComboBoxUI(combobox_SelectRole, "Dialog", 16, 90, 500, 135);
        List<String> list = new ArrayList<>();
        for (String role : roles) list.add(role);
        combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
        combobox_SelectRole.getSelectionModel().select(0);

        alertEmailSent.setTitle("Invitation");
        alertEmailSent.setHeaderText("Invitation was sent");

        setupButtonUI(button_SendInvitation, "Dialog", 16, 150, Pos.CENTER, 630, 135);
        button_SendInvitation.setOnAction((event) -> ControllerAdminHome.performInvitation());

        // GUI Area 3
        setupButtonUI(button_ManageInvitations, "Dialog", 16, 200, Pos.CENTER, 20, 200);
        button_ManageInvitations.setOnAction((event) -> createInvitationTableView());

        setupButtonUI(button_SetOnetimePassword, "Dialog", 16, 200, Pos.CENTER, 20, 250);
        button_SetOnetimePassword.setOnAction((event) -> createOneTimePasswordTableView());

        setupButtonUI(button_DeleteUser, "Dialog", 16, 200, Pos.CENTER, 20, 300);
        button_DeleteUser.setOnAction((event) -> ControllerAdminHome.deleteUser());

        setupButtonUI(button_ListUsers, "Dialog", 16, 200, Pos.CENTER, 20, 350);
        button_ListUsers.setOnAction((event) -> createUserListTableView());

        setupButtonUI(button_AddRemoveRoles, "Dialog", 16, 200, Pos.CENTER, 20, 400);
        button_AddRemoveRoles.setOnAction((event) -> ControllerAdminHome.addRemoveRoles());
        
        setupButtonUI(button_systemRequests, "Dialog", 16, 200, Pos.CENTER, 20, 450);
        button_systemRequests.setOnAction((event) -> createSystemRequestsTable());
        
        setupLabelUI(label_NumberOfUsers, "Arial", 16, width, Pos.CENTER, 240, 485);
        label_NumberOfUsers.setText("Number of users: " + theDatabase.getNumberOfUsers());
        
    	label_NumberOfUsers.setVisible(false);
        invitationTableView.setVisible(false);
        oneTimePasswordTableView.setVisible(false);
        userListTableView.setVisible(false);
        systemRequestsTableView.setVisible(false);

        // GUI Area 4
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> ControllerAdminHome.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> ControllerAdminHome.performQuit());

        // GUI Delete User
        deleteUserDialog.setTitle("Delete User");
    	deleteUserDialog.setHeaderText("Enter the username you wish to delete:");
    	deleteUserDialog.setContentText("Username:");
    	
    	alert_DeleteUser.setTitle("Alert");
        alert_DeleteUser.setHeaderText("Are you sure to delete this user?");
        alert_DeleteUser.setContentText("User data will not be recovered!");
        alert_DeleteUser.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
    	
    	deleteUserNotFound.setTitle("User Not Found");
    	deleteUserNotFound.setHeaderText(null);
    	
    	deleteUserDeleted.setTitle("User Deleted");
    	deleteUserDeleted.setHeaderText(null);
        
        // Add all widgets to root pane
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
            label_Invitations, label_InvitationEmailAddress, text_InvitationEmailAddress,
            combobox_SelectRole, button_SendInvitation, line_Separator3,
            button_ManageInvitations, button_SetOnetimePassword, button_DeleteUser,
            button_ListUsers, button_AddRemoveRoles, button_systemRequests,
            invitationTableView, oneTimePasswordTableView, userListTableView, label_NumberOfUsers, 
            systemRequestsTableView, line_Separator4,
            button_Logout, button_Quit
        );
    }

    /**
     * <p>Method: createInvitationTableView()</p>
     *
     * <p>Description: Creates and displays the TableView for all invitation emails.
     * Populates columns for email, role, code, and action buttons (Cancel, Copy code).
     * Adds hover and click functionality for copying codes and canceling invitations.</p>
     */
    protected static void createInvitationTableView() {
    	clearTables();
        List<String> invitations = theDatabase.getAllInvitationEmails();

        HashMap<String, String> emailToCode = new HashMap<>();
        HashMap<String, String> emailToRole = new HashMap<>();

        for (String[] entry : theDatabase.getInvitationEmailCodeRole()) {
            String email = entry[0];
            String code  = entry[1];
            String role  = entry[2];

            emailToCode.put(email, code);
            emailToRole.put(email, role);
        }

        invitationTableView.setLayoutX(240);
        invitationTableView.setLayoutY(200);
        invitationTableView.setPrefWidth(540);
        invitationTableView.setPrefHeight(280);
        invitationTableView.setFixedCellSize(30);
        invitationTableView.setSelectionModel(null);
        invitationTableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        invitationTableView.getColumns().clear();

        // Email column
        invitationEmailColumn.setPrefWidth(200);
        invitationEmailColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()));
        invitationEmailColumn.setStyle("-fx-alignment: CENTER;");

        // Role column
        invitationRoleColumn.setPrefWidth(120);
        invitationRoleColumn.setCellValueFactory(cd -> new SimpleStringProperty(emailToRole.getOrDefault(cd.getValue(), "")));
        invitationRoleColumn.setStyle("-fx-alignment: CENTER;");

        // Code column
        invitationCodesColumn.setPrefWidth(120);
        invitationCodesColumn.setCellValueFactory(cd -> new SimpleStringProperty(emailToCode.getOrDefault(cd.getValue(), "")));
        invitationCodesColumn.setStyle("-fx-alignment: CENTER;");
        invitationCodesColumn.setCellFactory(col -> {
            TableCell<String, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String code, boolean empty) {
                    super.updateItem(code, empty);
                    if (empty || code == null) {
                        setText(null);
                        setStyle("");
                        setCursor(Cursor.DEFAULT);
                    } else {
                        setText("\u2398 " + code);
                        setStyle("");
                        setCursor(Cursor.HAND);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);

            // Hover effect
            cell.setOnMouseEntered(e -> {
                if (!cell.isEmpty()) {
                    String code = cell.getItem();
                    cell.setText("\u2398 " + code);
                    cell.setStyle("-fx-background-color: #f2f2f2;");
                }
            });
            cell.setOnMouseExited(e -> {
                if (!cell.isEmpty()) {
                    String code = cell.getItem();
                    cell.setText("\u2398 " + code);
                    cell.setStyle("");
                }
            });

            // Click action: copy code
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    String code = cell.getItem();
                    if (code != null && !code.isEmpty()) {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(code);
                        clipboard.setContent(content);

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Copied");
                        alert.setHeaderText(null);
                        alert.setContentText("Successfully Copied: " + code);
                        alert.showAndWait();
                    }
                }
            });

            return cell;
        });

        // Action column
        invitationActionColumn.setPrefWidth(100);
        invitationActionColumn.setCellFactory(col -> {
            TableCell<String, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                        setCursor(Cursor.DEFAULT);
                    } else {
                        setText("Cancel");
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-underline: true;");
                        setCursor(Cursor.HAND);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);

            cell.setOnMouseEntered(e -> {
                if (!cell.isEmpty()) {
                    cell.setStyle("-fx-background-color: #f2f2f2; -fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-underline: true;");
                }
            });
            cell.setOnMouseExited(e -> {
                if (!cell.isEmpty()) {
                    cell.setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-underline: true;");
                }
            });
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    String email = cell.getTableView().getItems().get(cell.getIndex());
                    theDatabase.removeInvitationByEmail(email);
                    cell.getTableView().getItems().remove(email);

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invitation Removed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invitation for " + email + " has been cancelled.");
                    alert.showAndWait();
                }
            });

            return cell;
        });

        invitationTableView.getColumns().add(invitationEmailColumn);
        invitationTableView.getColumns().add(invitationRoleColumn);
        invitationTableView.getColumns().add(invitationCodesColumn);
        invitationTableView.getColumns().add(invitationActionColumn);
        invitationTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
        	invitationTableView.lookupAll(".column-header").forEach(node -> 
                node.setOnMouseDragged(Event::consume)
            );
        });
        invitationTableView.setColumnResizePolicy(tv -> true);
        invitationTableView.setItems(FXCollections.observableArrayList(invitations));
        invitationTableView.setVisible(true);
    }
    
    /**
     * <p>Method: createOneTimePasswordTableView()</p>
     *
     * <p>Description: Creates and displays the TableView for all one-time password entries.
     * Populates columns for username, email, code, and action buttons (Send, Decline, Copy code).
     * Implements hover and click functionality for sending or declining codes.</p>
     */
    protected static void createOneTimePasswordTableView() {
    	clearTables();
        Map<String, String> entries = theDatabase.getAllOneTimePasscodeEntries();
        ObservableList<String> items = FXCollections.observableArrayList(entries.keySet());
        oneTimePasswordTableView.setItems(items);
        oneTimePasswordTableView.getColumns().clear();
        oneTimePasswordTableView.setLayoutX(240);
        oneTimePasswordTableView.setLayoutY(200);
        oneTimePasswordTableView.setPrefWidth(540);
        oneTimePasswordTableView.setPrefHeight(280);
        oneTimePasswordTableView.setFixedCellSize(30);
        oneTimePasswordTableView.setSelectionModel(null);
        oneTimePasswordTableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        // Username Column
        oneTimePasswordUsernameColumn.setPrefWidth(100);
        oneTimePasswordUsernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(theDatabase.getUsernameByEmail(cell.getValue())));
        oneTimePasswordUsernameColumn.setStyle("-fx-alignment: CENTER;");
        
        // Email Column
        oneTimePasswordEmailColumn.setPrefWidth(140);
        oneTimePasswordEmailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()));
        oneTimePasswordEmailColumn.setStyle("-fx-alignment: CENTER;");

        // Code Column
        oneTimePasswordCodeColumn.setPrefWidth(120);
        oneTimePasswordCodeColumn.setCellValueFactory( cell -> new SimpleStringProperty(theDatabase.getPasscodeByEmail(cell.getValue())));
    	oneTimePasswordCodeColumn.setStyle("-fx-alignment: CENTER;");
    	oneTimePasswordCodeColumn.setCellFactory(col -> {
        TableCell<String, String> cell = new TableCell<>() {
            	@Override
            	protected void updateItem(String code, boolean empty) {
                	super.updateItem(code, empty);
                	if (empty || code == null || code.isEmpty()) {
                    setText(null);
                    	setStyle("");
                    	setCursor(Cursor.DEFAULT);
                	} else {
                    	setText("\u2398 " + code); // copy symbol
                    	setStyle("");
                    	setCursor(Cursor.HAND);
                	}
            	}
        	};
        	cell.setAlignment(Pos.CENTER);

        	// Hover effect
        	cell.setOnMouseEntered(e -> {
            	if (!cell.isEmpty()) {
                	String code = cell.getItem();
                	cell.setText("\u2398 " + code);
                	cell.setStyle("-fx-background-color: #f2f2f2;");
            	}
        	});
        	cell.setOnMouseExited(e -> {
            	if (!cell.isEmpty()) {
                	String code = cell.getItem();
                	cell.setText("\u2398 " + code);
                	cell.setStyle("");
            	}
        	});
        	
        	// Click action: copy code
        	cell.setOnMouseClicked(e -> {
            	if (!cell.isEmpty()) {
                	String code = cell.getItem();
                	if (code != null && !code.isEmpty()) {
                    	Clipboard clipboard = Clipboard.getSystemClipboard();
                    	ClipboardContent content = new ClipboardContent();
                    	content.putString(code);
                    	clipboard.setContent(content);

                    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    	alert.setTitle("Copied");
                    	alert.setHeaderText(null);
                    	alert.setContentText("Successfully Copied: " + code);
                    	alert.showAndWait();
                	}
            	}
        	});

        	return cell;
    	});
        oneTimePasswordCodeColumn.setStyle("-fx-alignment: CENTER;");

        // Send Column
        oneTimePasswordActionColumn.setPrefWidth(90);
        oneTimePasswordActionColumn.setCellFactory(col -> {
        	TableCell<String, String> cell = new TableCell<>() {
        		@Override
        		protected void updateItem(String item, boolean empty) {
        			super.updateItem(item, empty);
        			if (empty) {
        				setText(null);
        				setStyle("");
        				setCursor(Cursor.DEFAULT);
        				return;
        			}
        			String email = getTableView().getItems().get(getIndex());
        			String code = theDatabase.getPasscodeByEmail(email);

        			if (code == null || code.isEmpty()) {
        				setText("Send");
        				setStyle("-fx-font-weight: bold; -fx-text-fill: #0275d8; -fx-underline: true;");
        				setCursor(Cursor.HAND);
        			} else {
        				setText("Sent");
        				setStyle("-fx-text-fill: gray; -fx-opacity: 0.6;");
        				setCursor(Cursor.DEFAULT);
        			}
        		}
        	};
        	cell.setAlignment(Pos.CENTER);

        	// Hover effect: ONLY when send is allowed
        	cell.setOnMouseEntered(e -> {
        		if (!cell.isEmpty()) {
        			String email = cell.getTableView().getItems().get(cell.getIndex());
        			String code = theDatabase.getPasscodeByEmail(email);
        			if (code == null || code.isEmpty()) {
        				cell.setStyle("-fx-background-color: #f2f2f2; "
                                	+ "-fx-font-weight: bold; -fx-text-fill: #5bc0de; -fx-underline: true;");
        			}
        		}
        	});
        	cell.setOnMouseExited(e -> {
        		if (!cell.isEmpty()) {
        			String email = cell.getTableView().getItems().get(cell.getIndex());
        			String code = theDatabase.getPasscodeByEmail(email);
        			if (code == null || code.isEmpty()) {
        				cell.setStyle("-fx-font-weight: bold; -fx-text-fill: #0275d8; -fx-underline: true;");
        			} else {
        				cell.setStyle("-fx-text-fill: gray; -fx-opacity: 0.6;");
        			}
        		}
        	});

        	// Click action
        	cell.setOnMouseClicked(e -> {
        		if (!cell.isEmpty()) {
        			String email = cell.getTableView().getItems().get(cell.getIndex());
        			String code = theDatabase.getPasscodeByEmail(email);
        			if (code == null || code.isEmpty()) {
        				String passcode = theDatabase.generateAndUpdatePasscode(email);

        				Alert alert = new Alert(AlertType.INFORMATION);
        				alert.setTitle("Passcode Sent");
        				alert.setHeaderText(null);
        				alert.setContentText("Passcode sent to: " + email + "\nCode: " + passcode);
        				alert.showAndWait();

        				cell.getTableView().refresh();
        			}
        		}
        	});

        	return cell;
        });
        oneTimePasswordActionColumn.setStyle("-fx-alignment: CENTER;");

    	// Decline Column
        oneTimePasswordDeclineColumn.setPrefWidth(90);
    	oneTimePasswordDeclineColumn.setCellFactory(col -> {
			TableCell<String, String> cell = new TableCell<>() {

    			@Override
        		protected void updateItem(String item, boolean empty) {
        			super.updateItem(item, empty);
            		if (empty) {
            			setText(null);
                		setStyle("");
                		setCursor(Cursor.DEFAULT);
                		return;
            		}

            		setText("Decline");
            		setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-underline: true;");
            		setCursor(Cursor.HAND);
        		}
    		};
    	
    		cell.setAlignment(Pos.CENTER);
    		// Hover effect
    		cell.setOnMouseEntered(e -> {
    		if (!cell.isEmpty()) {
            	cell.setStyle("-fx-background-color: #f2f2f2; "
            			+ "-fx-font-weight: bold; -fx-text-fill: #ff6666; -fx-underline: true;");}
    		});
    		cell.setOnMouseExited(e -> {
    			if (!cell.isEmpty()) {
        			cell.setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-underline: true;");
        		}
    		});

    		// Click action
    		cell.setOnMouseClicked(e -> {
    			if (!cell.isEmpty()) {
        			String email = cell.getTableView().getItems().get(cell.getIndex());
            		theDatabase.removePasscodeRowByEmail(email);

            		cell.getTableView().getItems().remove(email);
            		cell.getTableView().refresh();
        		}
    		});
    
    		return cell;
		});
		oneTimePasswordDeclineColumn.setStyle("-fx-alignment: CENTER;");

        oneTimePasswordTableView.getColumns().add(oneTimePasswordUsernameColumn);
        oneTimePasswordTableView.getColumns().add(oneTimePasswordEmailColumn);
        oneTimePasswordTableView.getColumns().add(oneTimePasswordCodeColumn);
        oneTimePasswordTableView.getColumns().add(oneTimePasswordActionColumn);
        oneTimePasswordTableView.getColumns().add(oneTimePasswordDeclineColumn);
        oneTimePasswordTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            oneTimePasswordTableView.lookupAll(".column-header").forEach(node -> 
                node.setOnMouseDragged(Event::consume)
            );
        });
        oneTimePasswordTableView.setColumnResizePolicy(tv -> true);
        oneTimePasswordTableView.setVisible(true);
    }

    /**
     * <p>Method: createUserListTableView()</p>
     *
     * <p>Description: Creates and displays the TableView listing all users.
     * Populates columns for username, full name, email, roles, and a View action button.
     * Enables opening a substage for detailed user information.</p>
     */
    protected static void createUserListTableView() {
        clearTables();

        List<User> users = theDatabase.getAllUsers();
        userListTableView.getColumns().clear();

        // ---------------- TableView Layout ----------------
        userListTableView.setLayoutX(240);
        userListTableView.setLayoutY(200);
        userListTableView.setPrefWidth(540);
        userListTableView.setPrefHeight(280);
        userListTableView.setFixedCellSize(-1);
        userListTableView.setSelectionModel(null);
        userListTableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        // Username column
        userListUsernameColumn.setPrefWidth(100);
        userListUsernameColumn.setCellValueFactory(cellData ->  new SimpleStringProperty(cellData.getValue().getUserName()));
        userListUsernameColumn.setStyle("-fx-alignment: CENTER;");

        // Name column
        userListNameColumn.setPrefWidth(120);
        userListNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getFirstName() + " " +
                cellData.getValue().getMiddleName() + " " +
                cellData.getValue().getLastName()
            ));
        userListNameColumn.setStyle("-fx-alignment: CENTER;");

        // Email column
        userListEmailColumn.setPrefWidth(120);
        userListEmailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailAddress()));
        userListEmailColumn.setStyle("-fx-alignment: CENTER;");

        // Roles column
        userListRolesColumn.setPrefWidth(100);
        userListRolesColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            StringBuilder roles = new StringBuilder();
            if (user.getAdminRole()) roles.append("Admin\n");
            if (user.getStudentRole()) roles.append("Student\n");
            if (user.getStaffRole()) roles.append("Staff\n");
            String roleString = roles.toString().trim();
            return new SimpleStringProperty(roleString);
        });

        // Wrap text and dynamically resize row
        userListRolesColumn.setCellFactory(tc -> {
            TableCell<User, String> cell = new TableCell<>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(userListRolesColumn.widthProperty().subtract(10));
                    text.setTextAlignment(TextAlignment.CENTER);
                    setGraphic(text);
                }

                @Override
                protected void updateItem(String roles, boolean empty) {
                    super.updateItem(roles, empty);
                    if (empty || roles == null) {
                        text.setText("");
                        setPrefHeight(30);
                    } else {
                        text.setText(roles);
                        // adjust row height based on text
                        double height = text.getLayoutBounds().getHeight() + 10; // padding
                        setPrefHeight(Math.max(height, 30));
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        // Action column
        
        userListActionColumn.setPrefWidth(100);
        userListActionColumn.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                        setCursor(Cursor.DEFAULT);
                    } else {
                        setText("View");
                        setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #0275d8;");
                        setCursor(Cursor.HAND);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);

            // Hover effect
            cell.setOnMouseEntered(e -> {
                if (!cell.isEmpty()) {
                    cell.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #5bc0de;");
                }
            });
            cell.setOnMouseExited(e -> {
                if (!cell.isEmpty()) {
                    cell.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #0275d8;");
                }
            });

            // Click action: open substage with user details
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    User user = cell.getTableView().getItems().get(cell.getIndex());
                    showUserDetails(user);
                }
            });

            return cell;
        });

        
        // ---------------- Add Columns ----------------
    	userListTableView.getColumns().add(userListUsernameColumn);
    	userListTableView.getColumns().add(userListNameColumn);
    	userListTableView.getColumns().add(userListEmailColumn);
    	userListTableView.getColumns().add(userListRolesColumn);
    	userListTableView.getColumns().add(userListActionColumn);
    	userListTableView.setColumnResizePolicy(tv -> true);
    	userListTableView.setItems(FXCollections.observableArrayList(users));
    	
    	userListTableView.setVisible(true);
    	label_NumberOfUsers.setVisible(true);
    }
    
    /**
     * <p>Method: showUserDetails(User user)</p>
     *
     * <p>Description: Opens a modal substage showing detailed information about a selected user,
     * including username, full name, preferred name, email, and roles.</p>
     *
     * @param user The user whose details are displayed
     */
    private static void showUserDetails(User user) {
    	userListDetailStage.setTitle("User Details - " + user.getUserName());
        userListDetailStage.initModality(Modality.APPLICATION_MODAL);

        userListDetailLayout.setPadding(new Insets(15));
        userListDetailLayout.getChildren().addAll(
            new Label("Username: " + user.getUserName()),
            new Label("Full Name: " + user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName()),
            new Label("Preferred Name: " + user.getPreferredFirstName()),
            new Label("Email: " + user.getEmailAddress()),
            new Label("Roles: " +
                (user.getAdminRole() ? "Admin " : "") +
                (user.getStudentRole() ? "Student " : "") +
                (user.getStaffRole() ? "Staff" : ""))
        );

        userDetailCloseButton.setOnAction(ev -> userListDetailStage.close());
        userListDetailLayout.getChildren().add(userDetailCloseButton);
        userListDetailStage.setScene(userDetailScene);
        userListDetailStage.showAndWait();
    }
    
    /**
     * <p>Method: createSystemRequestsTable()</p>
     *
     * <p>Description: Creates and displays the TableView for system requests.
     * Populates columns for username, request title, status, View action, and Delete action.
     * Adds click functionality for updating status, viewing details, and deleting requests.</p>
     */
    protected static void createSystemRequestsTable() {
        clearTables();

        List<Request> requests = new ArrayList<>();
        try {
            requests = theDatabase.getAllRequests();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        systemRequestsTableView.getColumns().clear();

        systemRequestsTableView.setLayoutX(240);
        systemRequestsTableView.setLayoutY(200);
        systemRequestsTableView.setPrefWidth(540);
        systemRequestsTableView.setPrefHeight(280);
        systemRequestsTableView.setSelectionModel(null);
        systemRequestsTableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        // -------------------- USERNAME COLUMN --------------------
        systemReqUserColumn.setPrefWidth(120);
        systemReqUserColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRequester()));
        systemReqUserColumn.setStyle("-fx-alignment: CENTER;");

        // -------------------- TITLE COLUMN --------------------
        systemReqTitleColumn.setPrefWidth(210);
        systemReqTitleColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTitle()));
        systemReqTitleColumn.setCellFactory(col -> new TableCell<Request, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
                text.setTextAlignment(TextAlignment.CENTER);
                setGraphic(text);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText("");
                    setPrefHeight(30);
                } else {
                    text.setText(item);
                    double height = text.getLayoutBounds().getHeight() + 10;
                    setPrefHeight(Math.max(height, 30));
                }
            }
        });

     // -------------------- STATUS COLUMN --------------------
        systemReqStatusColumn.setPrefWidth(100);
        systemReqStatusColumn.setCellValueFactory(cd -> 
            new SimpleStringProperty(cd.getValue().isChecked() ? "checked" : "unchecked")
        );

        systemReqStatusColumn.setCellFactory(col -> {
            TableCell<Request, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);
                        setCursor(Cursor.HAND);
                        setAlignment(Pos.CENTER);

                        // Set color based on checked status
                        if ("checked".equals(status)) {
                            setStyle("-fx-text-fill: green;");   // green for checked
                        } else if ("unchecked".equals(status)) {
                            setStyle("-fx-text-fill: grey;");    // grey for unchecked
                        } else {
                            setStyle("-fx-text-fill: black;");   // default
                        }
                    }
                }
            };

            // click toggles status
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    Request req = cell.getTableView().getItems().get(cell.getIndex());
                    boolean newState = !req.isChecked();
                    try {
                        theDatabase.updateRequestChecked(req, newState);
                        req.updateChecked(newState);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    systemRequestsTableView.refresh();
                }
            });

            return cell;
        });

        // -------------------- VIEW ACTION COLUMN --------------------
        systemReqActionColumn.setPrefWidth(80);
        systemReqActionColumn.setCellFactory(col -> {
            TableCell<Request, Void> cell = new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("View");
                        setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #0275d8;");
                        setCursor(Cursor.HAND);
                        setAlignment(Pos.CENTER);
                    }
                }
            };

            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    Request req = systemRequestsTableView.getItems().get(cell.getIndex());
                    openRequestSubstage(req, systemRequestsTableView);
                }
            });

            return cell;
        });

        // -------------------- DELETE ACTION COLUMN --------------------
        systemReqDeleteColumn.setPrefWidth(80);
        systemReqDeleteColumn.setCellFactory(col -> {
            TableCell<Request, Void> cell = new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("Delete");
                        setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-text-fill: #d9534f;");
                        setCursor(Cursor.HAND);
                        setAlignment(Pos.CENTER);
                    }
                }
            };

            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    Request req = systemRequestsTableView.getItems().get(cell.getIndex());
                    try {
                        theDatabase.deleteRequest(req);
                        systemRequestsTableView.getItems().remove(req);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            return cell;
        });

        // -------------------- ADD COLUMNS --------------------
        systemRequestsTableView.getColumns().add(systemReqUserColumn);
        systemRequestsTableView.getColumns().add(systemReqTitleColumn);
        systemRequestsTableView.getColumns().add(systemReqStatusColumn);
        systemRequestsTableView.getColumns().add(systemReqActionColumn);
        systemRequestsTableView.getColumns().add(systemReqDeleteColumn);

        systemRequestsTableView.setItems(FXCollections.observableArrayList(requests));
        systemRequestsTableView.setVisible(true);
    }
    
    /**
     * <p>Method: openRequestSubstage(Request req, TableView tableView)</p>
     *
     * <p>Description: Opens a modal substage showing the details of a specific request.
     * Displays title, user, content, and a checkbox to mark the request as checked.
     * Updates the main TableView after changes.</p>
     *
     * @param req The request object to display
     * @param tableView The TableView that contains the request, used for refreshing after updates
     */
    private static void openRequestSubstage(Request req, TableView<Request> tableView) {
    	systemRequestSubstage.setTitle("Request Details");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Title label
        Label titleLabel = new Label(req.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // User label
        Label userLabel = new Label("User: " + req.getRequester());
        userLabel.setStyle("-fx-font-size: 14px;");

        // Content label with wrapping
        Label contentLabel = new Label(req.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400); // or whatever width fits your substage
        contentLabel.setStyle("-fx-font-size: 14px;");

        // Checkbox for status
        CheckBox checkedBox = new CheckBox("Checked");
        checkedBox.setSelected(req.isChecked());
        checkedBox.setOnAction(e -> {
            boolean newState = checkedBox.isSelected();
            try {
                theDatabase.updateRequestChecked(req, newState);
                req.updateChecked(newState);
                tableView.refresh(); // update main table
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> systemRequestSubstage.close());

        // Layout
        HBox controls = new HBox(10, checkedBox, closeButton);
        controls.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(titleLabel, userLabel, contentLabel, controls);

        Scene scene = new Scene(root, 450, 250);
        systemRequestSubstage.setScene(scene);
        systemRequestSubstage.show();
    }

    /**
     * <p>Method: clearTables()</p>
     *
     * <p>Description: Hides all dynamic TableViews in the Admin Home page.
     * Used to clear the view before showing a different table.</p>
     */
    protected static void clearTables() {
    	invitationTableView.setVisible(false);
    	oneTimePasswordTableView.setVisible(false);
    	userListTableView.setVisible(false);
    	label_NumberOfUsers.setVisible(false);
    	systemRequestsTableView.setVisible(false);
    }
    
    /*-*******************************************************************************************
     * Helper Methods
     ********************************************************************************************/
    
    /**********
     * <p> Method: setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) </p>
     * <p> Description: Helper method to initialize a Label's font, size, alignment, and position.</p>
     * @param l Label to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment (e.g., left, center, right)
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**********
     * <p> Method: setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) </p>
     * <p> Description: Helper method to initialize a Button's font, size, alignment, and position.</p>
     * @param b Button to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**********
     * <p> Method: setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) </p>
     * <p> Description: Helper method to initialize a TextField's font, size, alignment, position, and editability.</p>
     * @param t TextField to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param e Editable flag
     */
    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setEditable(e);
    }

    /**********
     * <p> Method: setupComboBoxUI(ComboBox c, String ff, double f, double w, double x, double y) </p>
     * <p> Description: Helper method to initialize a ComboBox's font, width, and position.</p>
     * @param c ComboBox to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    private void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y){
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }

}
