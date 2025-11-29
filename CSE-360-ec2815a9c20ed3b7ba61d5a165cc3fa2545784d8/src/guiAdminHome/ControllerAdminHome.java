package guiAdminHome;

import java.util.List;
import java.util.Optional;
import javafx.util.Callback;

import database.Database;
import entityClasses.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations() {
	    List<String> invitations = theDatabase.getAllInvitationEmails();

	    if (invitations.isEmpty()) {
	        ViewAdminHome.alertNotImplemented.setTitle("No Invitations");
	        ViewAdminHome.alertNotImplemented.setHeaderText(null);
	        ViewAdminHome.alertNotImplemented.setContentText("There are no users being invited.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }

	    Stage manageInvitationsStage = new Stage();
	    manageInvitationsStage.setTitle("User Invitations List");

	    TableView<String> tableView = new TableView<>();
	    tableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
	    tableView.getColumns().clear();

	    TableColumn<String, String> emailColumn = new TableColumn<>("Email");
	    emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
	    tableView.getColumns().add(emailColumn);

	    TableColumn<String, Void> actionColumn = new TableColumn<>("Action");

	    Callback<TableColumn<String, Void>, TableCell<String, Void>> cellFactory = new Callback<>() {
	        @Override
	        public TableCell<String, Void> call(final TableColumn<String, Void> param) {
	            return new TableCell<>() {
	                private final Button btn = new Button("Cancel");
	                {
	                    btn.setOnAction((event) -> {
	                        String email = getTableView().getItems().get(getIndex());
	                        
	                        theDatabase.removeInvitationByEmail(email);
	                        
	                        tableView.getItems().remove(email);

	                        Alert alert = new Alert(AlertType.INFORMATION);
	                        alert.setTitle("Invitation Removed");
	                        alert.setHeaderText(null);
	                        alert.setContentText("Invitation for " + email + " has been cancelled.");
	                        alert.showAndWait();
	                    });
	                }

	                @Override
	                public void updateItem(Void item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (empty) setGraphic(null);
	                    else setGraphic(btn);
	                }
	            };
	        }
	    };

	    actionColumn.setCellFactory(cellFactory);
	    tableView.getColumns().add(actionColumn);

	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    tableView.setItems(FXCollections.observableArrayList(invitations));

	    VBox layout = new VBox(tableView);
	    layout.setSpacing(10);
	    layout.setPadding(new Insets(10));

	    Scene scene = new Scene(layout, 600, 400);
	    manageInvitationsStage.setScene(scene);
	    manageInvitationsStage.show();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		List<String> requests = theDatabase.getEmailsWithoutPasscode();
    
		if (requests.isEmpty()) {
			ViewAdminHome.alertNotImplemented.setTitle("No Requests");
			ViewAdminHome.alertNotImplemented.setHeaderText(null);
			ViewAdminHome.alertNotImplemented.setContentText("There are no users requesting a one-time password.");
			ViewAdminHome.alertNotImplemented.showAndWait();
			return;
		}
    
		Stage oneTimePasswordStage = new Stage();
		oneTimePasswordStage.setTitle("Password Request List");
    
		TableView<String> tableView = new TableView<>();
		tableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
		tableView.getColumns().clear();

		TableColumn<String, String> usernameColumn = new TableColumn<>("Username");
		usernameColumn.setCellValueFactory(cellData -> 
        	new SimpleStringProperty(theDatabase.getUsernameByEmail(cellData.getValue()))
				);

		TableColumn<String, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

		tableView.getColumns().addAll(usernameColumn, emailColumn);

		TableColumn<String, Void> actionColumn = new TableColumn<>("Action");

		Callback<TableColumn<String, Void>, TableCell<String, Void>> cellFactory = new Callback<>() {
			@Override
			public TableCell<String, Void> call(final TableColumn<String, Void> param) {
				return new TableCell<>() {
					private final Button btn = new Button("Send Passcode");
					{
						btn.setOnAction((event) -> {
							String email = getTableView().getItems().get(getIndex());
							String passcode = theDatabase.generateAndUpdatePasscode(email);
                        	Alert alert = new Alert(AlertType.INFORMATION);
                        	System.out.println("Passcode: " + passcode);
                        	alert.setTitle("Passcode Sent");
                        	alert.setHeaderText(null);
                        	alert.setContentText("Sent passcode for: " + email + "\nPasscode: " + passcode);
                        	alert.showAndWait();
                        	tableView.getItems().remove(email);
						});
					}
					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) setGraphic(null);
						else setGraphic(btn);
					}
				};
			}
		};

    actionColumn.setCellFactory(cellFactory);
    tableView.getColumns().add(actionColumn);

    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    tableView.setItems(FXCollections.observableArrayList(requests));

    VBox layout = new VBox(tableView);
    layout.setSpacing(10);
    layout.setPadding(new Insets(10));

    Scene scene = new Scene(layout, 600, 400);
    oneTimePasswordStage.setScene(scene);
    oneTimePasswordStage.show();
}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Delete User");
	    dialog.setHeaderText("Enter the username you wish to delete:");
	    dialog.setContentText("Username:");

	    Optional<String> result = dialog.showAndWait();
	    if (!result.isPresent()) {
	        return; // user cancelled
	    }
	    
	    String userName = result.get().trim();
	    
	    Optional<ButtonType> result2 = ViewAdminHome.alert_DeleteUser.showAndWait();
    	if (result2.isPresent() && result2.get() == ButtonType.NO) {return;}

	    List<User> users = theDatabase.getAllUsers();
	    Optional<User> match = users.stream()
	    		.filter(u -> u.getUserName().equals(userName))
	    		.findFirst();

	    if (!match.isPresent()) {
	        Alert notFound = new Alert(AlertType.ERROR);
	        notFound.setTitle("User Not Found");
	        notFound.setHeaderText(null);
	        notFound.setContentText("No user with username \"" + userName + "\" was found.");
	        notFound.showAndWait();
	        return;
	    }

	    theDatabase.deleteUser(userName);

	    Alert deleted = new Alert(AlertType.INFORMATION);
	    deleted.setTitle("User Deleted");
	    deleted.setHeaderText(null);
	    deleted.setContentText("User \"" + userName + "\" has been deleted.");
	    deleted.showAndWait();

	    ViewAdminHome.label_NumberOfUsers.setText(
	        "Number of users: " + theDatabase.getNumberOfUsers());
	    
	    if (theDatabase.getNumberOfUsers() == 0) {
	    	guiFirstAdmin.ViewFirstAdmin.displayFirstAdmin(ViewAdminHome.theStage);
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
	    List<User> users = theDatabase.getAllUsers();
	    
	    if (users.isEmpty()) {
	        ViewAdminHome.alertNotImplemented.setTitle("No Users");
	        ViewAdminHome.alertNotImplemented.setHeaderText("No users found.");
	        ViewAdminHome.alertNotImplemented.setContentText("There are no users in the system.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }
	    
	    Stage userListStage = new Stage();
	    userListStage.setTitle("User List");
	    
	    TableView<User> tableView = new TableView<>();
	    tableView.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

	    tableView.getColumns().clear();
	    

	    TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
	    usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));

	    TableColumn<User, String> nameColumn = new TableColumn<>("Name");
	    nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
	        cellData.getValue().getFirstName() + " " +
	        cellData.getValue().getMiddleName() + " " +
	        cellData.getValue().getLastName()
	    ));

	    TableColumn<User, String> emailColumn = new TableColumn<>("Email");
	    emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailAddress()));
	    
	    TableColumn<User, String> rolesColumn = new TableColumn<>("Roles");
	    rolesColumn.setCellValueFactory(cellData -> 
	    {
	        User user = cellData.getValue();
	        String roles;
	        if (user.getNewStudent() == true) 
	        {
	            roles = "Student";
	        } else if (user.getNewStaff() == true) 
	        {
	            roles = "Staff";
	        } else {
	            roles = "Admin";
	        }
	        return new SimpleStringProperty(roles);
	     });

	    tableView.getColumns().addAll(usernameColumn, nameColumn, emailColumn, rolesColumn);
	    
	    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    tableView.setItems(FXCollections.observableArrayList(users));

	    VBox layout = new VBox(tableView);
	    layout.setSpacing(10);
	    layout.setPadding(new Insets(10));


	    Scene scene = new Scene(layout, 600, 400);
	    userListStage.setScene(scene);
	    userListStage.show();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		else if (!ModelAdminHome.checkValidEmail(emailAddress).isEmpty()) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}