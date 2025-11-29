package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p> Title: ViewUserUpdate Class. </p>
 * 
 * <p> Description: The Java/FX-based User Update Page.  This page enables the user to update the
 * attributes about the user held by the system.  Currently, this page does not provide a mechanism
 * to change the Username and not all of the functions on this page are implemented.
 * 
 * Currently the following attributes can be updated:
 * 		- First Name
 * 		- Middle Name
 * 		- Last Name
 * 		- Preferred First Name
 * 		- Email Address
 * The page uses dialog boxes for updating these items.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01		2025-08-19 Initial version plus new internal documentation
 *  
 */

public class ViewUserUpdate {

	/*-********************************************************************************************

	Attributes

	*/

	// These are the application values required by the user interface

	/** Application window width obtained from the main application. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Application window height obtained from the main application. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.

	// Unlike many of the other pages, the GUI on this page is not organized into areas and the user
	// is not able to logout, return, or quit from this page

	// These widgets display the purpose of the page and guide the user.

	/** Label displaying the page title for updating a user's account details. */
	private static Label label_ApplicationTitle = new Label("Update a User's Account Details");

	/** Label describing the purpose of this page. */
	private static Label label_Purpose = 
			new Label(" Use this page to define or update your account information."); 

	// These are static output labels and do not change during execution

	/** Label for "Username" field. */
	private static Label label_Username = new Label("Username:");

	/** Label for "Password" field. */
	private static Label label_Password = new Label("Password:");

	/** Label for "First Name" field. */
	private static Label label_FirstName = new Label("First Name:");

	/** Label for "Middle Name" field. */
	private static Label label_MiddleName = new Label("Middle Name:");

	/** Label for "Last Name" field. */
	private static Label label_LastName = new Label("Last Name:");

	/** Label for "Preferred First Name" field. */
	private static Label label_PreferredFirstName = new Label("Preferred First Name:");

	/** Label for "Email Address" field. */
	private static Label label_EmailAddress = new Label("Email Address:");

	// These are dynamic labels and they change based on the user and user interactions.

	/** Label showing the current username for the user. */
	private static Label label_CurrentUsername = new Label();

	/** Label showing the current password for the user. */
	private static Label label_CurrentPassword = new Label();

	/** Label showing the current first name for the user. */
	private static Label label_CurrentFirstName = new Label();

	/** Label showing the current middle name for the user. */
	private static Label label_CurrentMiddleName = new Label();

	/** Label showing the current last name for the user. */
	private static Label label_CurrentLastName = new Label();

	/** Label showing the current preferred first name for the user. */
	private static Label label_CurrentPreferredFirstName = new Label();

	/** Label showing the current email address for the user. */
	private static Label label_CurrentEmailAddress = new Label();

	// These buttons enable the user to edit the various dynamic fields.  The username and the
	// passwords for a user are currently not editable.

	/** Button to update the username (currently disabled). */
	private static Button button_UpdateUsername = new Button("Update Username");

	/** Button to update the password (currently disabled). */
	private static Button button_UpdatePassword = new Button("Update Password");

	/** Button to update the first name. */
	private static Button button_UpdateFirstName = new Button("Update First Name");

	/** Button to update the middle name. */
	private static Button button_UpdateMiddleName = new Button("Update Middle Name");

	/** Button to update the last name. */
	private static Button button_UpdateLastName = new Button("Update Last Name");

	/** Button to update the preferred first name. */
	private static Button button_UpdatePreferredFirstName = new Button("Update Preferred First Name");

	/** Button to update the email address. */
	private static Button button_UpdateEmailAddress = new Button("Update Email Address");

	// This button enables the user to finish working on this page and proceed to the user's home
	// page determined by the user's role at the time of log in.

	/** Button to proceed to the user's home page. */
	private static Button button_ProceedToUserHomePage = new Button("Proceed to the User Home Page");

	// This is the end of the GUI widgets for this page.

	// These are the set of pop-up dialog boxes that are used to enable the user to change the
	// the values of the various account detail items.

	/** Dialog box to update the first name. */
	private static TextInputDialog dialogUpdateFirstName;

	/** Dialog box to update the middle name. */
	private static TextInputDialog dialogUpdateMiddleName;

	/** Dialog box to update the last name. */
	private static TextInputDialog dialogUpdateLastName;

	/** Dialog box to update the preferred first name. */
	private static TextInputDialog dialogUpdatePreferredFirstName;

	/** Dialog box to update the email address. */
	private static TextInputDialog dialogUpdateEmailAddresss;

	// These attributes are used to configure the page and populate it with this user's information

	/** Singleton instance of the ViewUserUpdate class for this page. */
	private static ViewUserUpdate theView;	

	// This enables access to the application's database

	/** Reference to the application's database. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX stage where this page is displayed. */
	private static Stage theStage;

	/** The root Pane holding all GUI widgets for this page. */
	private static Pane theRootPane;

	/** The current user using the application. */
	private static User theUser;

	/** The Scene object for this page. */
	public static Scene theUserUpdateScene = null;

	/** The result from a pop-up input dialog. */
	private static Optional<String> result;

	/*-********************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayUserUpdate(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the UserUpdate page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayUserUpdate(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theUser = user;
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewUserUpdate();
		
		// Set the widget values that change from use of page to another use of the page.
		String s = "";
		
		// Set the dynamic aspects of the window based on the user logged in and the current state
		// of the various account elements.
		s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
    	if (s == null || s.length() < 1)label_CurrentUsername.setText("<none>");
    	else label_CurrentUsername.setText(s);
		
		s = theUser.getPassword();
    	if (s == null || s.length() < 1)label_CurrentPassword.setText("<none>");
    	else label_CurrentPassword.setText(s);
    	
		s = theUser.getFirstName();
    	if (s == null || s.length() < 1)label_CurrentFirstName.setText("<none>");
    	else label_CurrentFirstName.setText(s);
       
        s = theUser.getMiddleName();
    	if (s == null || s.length() < 1)label_CurrentMiddleName.setText("<none>");
    	else label_CurrentMiddleName.setText(s);
        
        s = theUser.getLastName();
    	if (s == null || s.length() < 1)label_CurrentLastName.setText("<none>");
    	else label_CurrentLastName.setText(s);
        
		s = theUser.getPreferredFirstName();
    	if (s == null || s.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
    	else label_CurrentPreferredFirstName.setText(s);
        
		s = theUser.getEmailAddress();
    	if (s == null || s.length() < 1)label_CurrentEmailAddress.setText("<none>");
    	else label_CurrentEmailAddress.setText(s);

		// Set the title for the window, display the page, and wait for the Admin to do something
    	theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
        theStage.setScene(theUserUpdateScene);
		theStage.show();
	}

	
	/**********
	 * <p> Method: ViewUserUpdate() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * <p> This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayUserUpdate method.</p>
	 * 
	 */
	
	private ViewUserUpdate() {

		// Create the Pane for the list of widgets and the Scene for the window
		
		System.out.println("checkpoint -- viewUserUpdate");
		
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);

		// Initialize the pop-up dialogs to an empty text filed.
		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");

		// Establish the label for each of the dialogs.
		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");
		
		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");
		
		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");
		
		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");
		
		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");

		// Label theScene with the name of the startup screen, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Label to display the welcome message for the first theUser
        setupLabelUI(label_Purpose, "Arial", 20, width, Pos.CENTER, 0, 50);
        
        // Display the titles, values, and update buttons for the various admin account attributes.
        // If the attributes is null or empty, display "<none>".
        
        // USername
        setupLabelUI(label_Username, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 100);
        setupLabelUI(label_CurrentUsername, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 100);
        setupButtonUI(button_UpdateUsername, "Dialog", 18, 275, Pos.CENTER, 500, 93);
       
        // password
        setupLabelUI(label_Password, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 150);
        setupLabelUI(label_CurrentPassword, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 150);
        setupButtonUI(button_UpdatePassword, "Dialog", 18, 275, Pos.CENTER, 500, 143);
        
        // First Name
        setupLabelUI(label_FirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 200);
        setupLabelUI(label_CurrentFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 200);
        setupButtonUI(button_UpdateFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 193);
        button_UpdateFirstName.setOnAction((event) -> {result = dialogUpdateFirstName.showAndWait();
        	result.ifPresent(name -> theDatabase.updateFirstName(theUser.getUserName(), result.get()));
        	theDatabase.getUserAccountDetails(theUser.getUserName());
         	String newName = theDatabase.getCurrentFirstName();
           	theUser.setFirstName(newName);
        	if (newName == null || newName.length() < 1)label_CurrentFirstName.setText("<none>");
        	else label_CurrentFirstName.setText(newName);
         	});
               
        // Middle Name
        setupLabelUI(label_MiddleName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 250);
        setupLabelUI(label_CurrentMiddleName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 250);
        setupButtonUI(button_UpdateMiddleName, "Dialog", 18, 275, Pos.CENTER, 500, 243);
        button_UpdateMiddleName.setOnAction((event) -> {result = dialogUpdateMiddleName.showAndWait();
    		result.ifPresent(name -> theDatabase.updateMiddleName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String newName = theDatabase.getCurrentMiddleName();
           	theUser.setMiddleName(newName);
        	if (newName == null || newName.length() < 1)label_CurrentMiddleName.setText("<none>");
        	else label_CurrentMiddleName.setText(newName);
    		});
        
        // Last Name
        setupLabelUI(label_LastName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 300);
        setupLabelUI(label_CurrentLastName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 300);
        setupButtonUI(button_UpdateLastName, "Dialog", 18, 275, Pos.CENTER, 500, 293);
        button_UpdateLastName.setOnAction((event) -> {result = dialogUpdateLastName.showAndWait();
    		result.ifPresent(name -> theDatabase.updateLastName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String newName = theDatabase.getCurrentLastName();
           	theUser.setLastName(newName);
      	if (newName == null || newName.length() < 1)label_CurrentLastName.setText("<none>");
        	else label_CurrentLastName.setText(newName);
    		});
        
        // Preferred First Name
        setupLabelUI(label_PreferredFirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 
        		5, 350);
        setupLabelUI(label_CurrentPreferredFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 
        		200, 350);
        setupButtonUI(button_UpdatePreferredFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 343);
        button_UpdatePreferredFirstName.setOnAction((event) -> 
        	{result = dialogUpdatePreferredFirstName.showAndWait();
    		result.ifPresent(name -> 
    		theDatabase.updatePreferredFirstName(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String newName = theDatabase.getCurrentPreferredFirstName();
           	theUser.setPreferredFirstName(newName);
         	if (newName == null || newName.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
        	else label_CurrentPreferredFirstName.setText(newName);
     		});
        
        // Email Address
        setupLabelUI(label_EmailAddress, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 400);
        setupLabelUI(label_CurrentEmailAddress, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 400);
        setupButtonUI(button_UpdateEmailAddress, "Dialog", 18, 275, Pos.CENTER, 500, 393);
        button_UpdateEmailAddress.setOnAction((event) -> {result = dialogUpdateEmailAddresss.showAndWait();
    		result.ifPresent(name -> theDatabase.updateEmailAddress(theUser.getUserName(), result.get()));
    		theDatabase.getUserAccountDetails(theUser.getUserName());
    		String newEmail = theDatabase.getCurrentEmailAddress();
    		System.out.println("checkpoint -- viewUserUpdate - email address");
    		if (guiUserUpdate.ModelUserUpdate.checkValidEmail(newEmail).isEmpty()) {
    			System.out.println(guiUserUpdate.ModelUserUpdate.checkValidEmail(newEmail));
    			theUser.setEmailAddress(newEmail);
    		}
    		else {
    			theUser.setEmailAddress("<none>");
    		}
        	if (newEmail == null || newEmail.length() < 1)label_CurrentEmailAddress.setText("<none>");
        	else label_CurrentEmailAddress.setText(newEmail);
 			});
        
        // Set up the button to proceed to this user's home page
        setupButtonUI(button_ProceedToUserHomePage, "Dialog", 18, 300, 
        		Pos.CENTER, width/2-150, 450);
        button_ProceedToUserHomePage.setOnAction((event) -> 
        	{ControllerUserUpdate.goToUserHomePage(theStage, theUser);});
    	
        // Populate the Pane's list of children widgets
        theRootPane.getChildren().addAll(
        		label_ApplicationTitle, label_Purpose, label_Username,
        		label_CurrentUsername, 
        		label_Password, label_CurrentPassword, 
        		button_UpdatePassword, 
        		label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
        		label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
        		label_LastName, label_CurrentLastName, button_UpdateLastName,
        		label_PreferredFirstName, label_CurrentPreferredFirstName,
        		button_UpdatePreferredFirstName, button_UpdateEmailAddress,
        		label_EmailAddress, label_CurrentEmailAddress, 
        		button_ProceedToUserHomePage);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}