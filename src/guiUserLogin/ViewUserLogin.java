package guiUserLogin;

import database.Database;
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

/**********
 * <p> Title: ViewUserLogin Class </p>
 * 
 * <p> Description: Java/FX-based User Login Page. Provides interface for existing users to log in 
 * and for invited users to set up new accounts using invitation codes. Includes fields for 
 * username, password, invitation code, and associated buttons for actions.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-04-20 Initial version
 */
public class ViewUserLogin {

	// --- Application window size ---

    /** Application window width obtained from the main application. */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** Application window height obtained from the main application. */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // --- GUI widgets ---

    /** Label displaying the application title at the top of the login page. */
    private static Label label_ApplicationTitle = new Label("Foundation Application Startup Page");

    /** Label displaying the operational start title or section heading. */
    private static Label label_OperationalStartTitle = new Label("Log In or Invited User Account Setup ");

    /** Label providing instructions for logging in. */
    private static Label label_LogInInsrtuctions = new Label(
        "Enter your user name and password and then click on the LogIn button"
    );

    /** Alert box for username/password errors. */
    protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

    /** TextField for entering the username. */
    protected static TextField text_Username = new TextField();

    /** PasswordField for entering the password. */
    protected static PasswordField text_Password = new PasswordField();

    /** Button to submit the login credentials. */
    private static Button button_Login = new Button("Log In");

    /** Button to handle forgotten username or password requests. */
    private static Button button_Forget = new Button("Forget Username/Password?");

    /** Label providing instructions for setting up a new account with an invitation code. */
    private static Label label_AccountSetupInsrtuctions = new Label(
        "No account? Enter your invitation code and click on the Account Setup button"
    );

    /** TextField for entering the invitation code for account setup. */
    private static TextField text_Invitation = new TextField();

    /** Button to trigger account setup using an invitation code. */
    private static Button button_SetupAccount = new Button("Setup Account");

    /** Button to quit the application from the login page. */
    private static Button button_Quit = new Button("Quit");

    /** The JavaFX Stage on which this login page is displayed. */
    protected static Stage theStage;

    /** Root pane holding all GUI widgets for the login page. */
    private static Pane theRootPane;

    /** Scene object representing the login page. */
    public static Scene theUserLoginScene = null;

    /** Singleton instance of the ViewUserLogin class for this page. */
    private static ViewUserLogin theView = null;

    /**********
     * <p> Method: displayUserLogin(Stage ps) </p>
     * <p> Description: Entry point to display the User Login page. Resets fields, removes expired
     * invitation codes, and shows the stage.</p>
     * 
     * @param ps The JavaFX Stage for this GUI
     */
    public static void displayUserLogin(Stage ps) {
        theStage = ps;

        if (theView == null) theView = new ViewUserLogin();

        // Clean expired invitations
        Database theDatabase = applicationMain.FoundationsMain.database;
        theDatabase.removeExpiredEntries();

        // Reset dynamic fields
        text_Username.setText("");
        text_Password.setText("");
        text_Invitation.setText("");

        // Show the page
        theStage.setTitle("CSE 360 Foundation Code: User Login Page");
        theStage.setScene(theUserLoginScene);
        theStage.show();
    }

    /**********
     * <p> Constructor: ViewUserLogin() </p>
     * <p> Description: Initializes all GUI components for User Login page. Sets fonts, positions,
     * sizes, prompts, and event handlers for all widgets. Singleton pattern is used.</p>
     */
    private ViewUserLogin() {
        theRootPane = new Pane();
        theUserLoginScene = new Scene(theRootPane, width, height);

        // Main application title
        setupLabelUI(label_ApplicationTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
        setupLabelUI(label_OperationalStartTitle, "Arial", 24, width, Pos.CENTER, 0, 60);

        // Login section
        setupLabelUI(label_LogInInsrtuctions, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 120);
        setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, true);
        text_Username.setPromptText("Enter Username");

        setupTextUI(text_Password, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, true);
        text_Password.setPromptText("Enter Password");

        setupButtonUI(button_Login, "Dialog", 18, 250, Pos.CENTER, 475, 160);
        button_Login.setOnAction((event) -> ControllerUserLogin.doLogin(theStage));

        setupButtonUI(button_Forget, "Dialog", 18, 250, Pos.CENTER, 475, 210);
        button_Forget.setOnAction((event) -> ControllerUserLogin.doForgetUsernamePassword());

        // Login error alert
        alertUsernamePasswordError.setTitle("Invalid username/password!");
        alertUsernamePasswordError.setHeaderText(null);

        // Invitation account setup section
        setupLabelUI(label_AccountSetupInsrtuctions, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 300);
        setupTextUI(text_Invitation, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 340, true);
        text_Invitation.setPromptText("Enter Invitation Code");

        setupButtonUI(button_SetupAccount, "Dialog", 18, 200, Pos.CENTER, 475, 340);
        button_SetupAccount.setOnAction((event) -> 
            ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText())
        );

        // Quit button
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 520);
        button_Quit.setOnAction((event) -> ControllerUserLogin.performQuit());

        // Add all widgets to root pane
        theRootPane.getChildren().addAll(
            label_ApplicationTitle, label_OperationalStartTitle,
            label_LogInInsrtuctions, label_AccountSetupInsrtuctions,
            text_Username, text_Password, text_Invitation,
            button_Login, button_Forget, button_SetupAccount, button_Quit
        );
    }

    /**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l The Label object
     * @param ff Font family
     * @param f Font size
     * @param w Label width
     * @param p Alignment (e.g., Pos.CENTER)
     * @param x X-axis position
     * @param y Y-axis position
	 */
	
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 * 
     * @param t The TextField to initialize
     * @param ff Font family
     * @param f Font size
     * @param w Width
     * @param p Alignment
     * @param x X position
     * @param y Y position
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
}
