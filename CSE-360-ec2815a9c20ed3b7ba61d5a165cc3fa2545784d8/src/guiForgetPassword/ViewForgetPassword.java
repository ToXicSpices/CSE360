package guiForgetPassword;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;


/*******
 * <p> Title: ViewForgetPassword Class. </p>
 * 
 * <p> Description: The Java/FX-based ForgetPassword Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewForgetPassword {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	protected static Label label_PageTitle = new Label();
	protected static Label label_Intro = new Label();
	protected static Label label_UEIntro = new Label();
	protected static Label label_Passcode = new Label();
	
	protected static TextField text_UsernameOrEmail = new TextField();
	protected static TextField text_OneTimePasscode = new TextField();
	
	protected static Button button_Request = new Button("Request One-time Password");
	protected static Button button_Enter = new Button("Enter");

	protected static Label label_RecoverPassword = new Label(" Password Recovery.");
    protected static Label label_RecoverPasswordLine = new Label("Please enter a new password.");
    protected static TextField text_Username = new TextField();
    protected static PasswordField text_Password1 = new PasswordField();
    protected static PasswordField text_Password2 = new PasswordField();
    protected static Label label_ShowPassword = new Label();
    protected static Button button_UsernameHelp = new Button("?");
    protected static Button button_PasswordHelp = new Button("?");
    protected static Button button_ShowPassword = new Button("Show Password");
    protected static Button button_HidePassword = new Button("Hide Password");
    protected static Button button_ResetPassword = new Button("Reset Password");
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);
	protected static Alert alertUsernamePasswordHelp = new Alert(AlertType.INFORMATION);
	
	protected static Button button_Return = new Button("Return to Login");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewForgetPassword theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	
	private static Scene theForgetPasswordScene;		// The shared Scene each invocation populates

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayForgetPassword(Stage psr) </p>
	 * 
	 * 
	 */
	public static void displayForgetPassword(Stage ps) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;

		// If not yet established, populate the static aspects of the GUI
		theView = new ViewForgetPassword();		// Instantiate singleton if needed
		if (theView == null);

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		
		text_UsernameOrEmail.setText("");
		text_OneTimePasscode.setText("");
		text_Password1.setText("");
		text_Password2.setText("");
		text_Password1.textProperty().addListener((observable, oldValue, newValue)
				-> { ControllerForgetPassword.setPassword(); });

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Forget Password Page");
		theStage.setScene(theForgetPasswordScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: ViewForgetPassword() </p>
	 * 
	 */
	private ViewForgetPassword() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theForgetPasswordScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		label_PageTitle.setText("Recover Password");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 10);
		
		label_Intro.setText("Admin will see your recover request as entered the username/email");
		setupLabelUI(label_Intro, "Arial", 20, width, Pos.CENTER, 0, 60);
		
		label_UEIntro.setText("Enter you Username or Email Address:");
		setupLabelUI(label_UEIntro, "Arial", 20, width, Pos.CENTER, 0, 110);
		
		label_Passcode.setText("Enter your one-time password from admin:");
		setupLabelUI(label_Passcode, "Arial", 20, width, Pos.CENTER, 0, 260);
		
		text_UsernameOrEmail.setPromptText("Enter username/email address here");
		setupTextUI(text_UsernameOrEmail, "Arial", 18, 400, Pos.CENTER, 200, 150, true);
		text_UsernameOrEmail.textProperty().addListener((observable, oldValue, newValue) 
		-> {ControllerForgetPassword.setUsernameOrEmail(); });
		
		text_OneTimePasscode.setPromptText("Enter one-time password here");
		setupTextUI(text_OneTimePasscode, "Arial", 18, 400, Pos.CENTER, 200, 300, true);
		text_OneTimePasscode.textProperty().addListener((observable, oldValue, newValue) 
				-> {ControllerForgetPassword.setOneTimePasscode(); });
		
		setupButtonUI(button_Request, "Dialog", 18, 400, Pos.CENTER, 200, 200);
		button_Request.setOnAction((event) -> {ControllerForgetPassword.doRequestCode(); });
		
		setupButtonUI(button_Enter, "Dialog", 18, 400, Pos.CENTER, 200, 350);
		button_Enter.setOnAction((event) -> {ControllerForgetPassword.doEnterCode(); });
		
        setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 520);
        button_Return.setOnAction((event) -> {ControllerForgetPassword.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 520);
        button_Quit.setOnAction((event) -> {ControllerForgetPassword.performQuit(); });

        
        
    	setupLabelUI(label_RecoverPassword, "Arial", 32, width, Pos.CENTER, 0, 10);
    	setupLabelUI(label_RecoverPasswordLine, "Arial", 24, width, Pos.CENTER, 0, 70);
    	
    	setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, false);
		
		// Establish the text input operand field for the password
		setupTextUI(text_Password1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, true);
		text_Password1.setPromptText("Enter the Password");
		
		// Establish the text input operand field to confirm the password
		setupTextUI(text_Password2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 270, true);
		text_Password2.setPromptText("Enter the Password Again");
        
		// Establish the label to show input password
		setupLabelUI(label_ShowPassword, "Arial", 16, width, Pos.BASELINE_LEFT, 50, 245);
				
		// Set up the Show Password button
		setupButtonUI(button_ShowPassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
		button_ShowPassword.setOnAction((event) -> {
			theRootPane.getChildren().add(label_ShowPassword);
			theRootPane.getChildren().add(button_HidePassword);
			theRootPane.getChildren().remove(button_ShowPassword);
		});
						
		// Set up the Hide Password button
		setupButtonUI(button_HidePassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
		button_HidePassword.setOnAction((event) -> {
			theRootPane.getChildren().remove(label_ShowPassword);
			theRootPane.getChildren().remove(button_HidePassword);
			theRootPane.getChildren().add(button_ShowPassword);
		});
						
		// Establish the help button for the password
		setupButtonUI(button_PasswordHelp, "Dialog", 18, 18, Pos.BASELINE_LEFT, 350, 210);
		button_PasswordHelp.setOnAction((event) -> {
			ViewForgetPassword.alertUsernamePasswordHelp.setTitle("Username/Password Help");
			ViewForgetPassword.alertUsernamePasswordHelp.setHeaderText("Password Requirements:");
			ViewForgetPassword.alertUsernamePasswordHelp.setContentText(
					"Needs at least one uppercase alphabet\n"
					+ "Needs at least one lowercase alphabet\n"
					+ "Needs at least one number\n"
					+ "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n"
					+ "Needs to be within 8-32 characters");
			ViewForgetPassword.alertUsernamePasswordHelp.showAndWait();
		});
        
		setupButtonUI(button_ResetPassword, "Dialog", 18, 200, Pos.CENTER, 475, 210);
		button_ResetPassword.setOnAction((event) -> {ControllerForgetPassword.doResetPassword(); });
        
        
        
		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_Intro, text_UsernameOrEmail, text_OneTimePasscode,
			label_UEIntro, label_Passcode, button_Request, button_Enter,
			button_Return, button_Quit);
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
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text field
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
}
