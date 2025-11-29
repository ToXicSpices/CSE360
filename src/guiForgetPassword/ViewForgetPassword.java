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
 * <p> Description: JavaFX-based Forget Password page. This class provides the GUI widgets
 * and functionality to allow a user to request a one-time password, enter it, and reset
 * their password. This page is implemented as a singleton to ensure only one instance
 * exists at any time.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-04-20 Initial version
 */
public class ViewForgetPassword {

    /*-*******************************************************************************************
     * Application Attributes
     */

    /** Window width */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** Window height */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /*-*******************************************************************************************
     * GUI Labels
     */

    /** Main title label for the page */
    protected static Label label_PageTitle = new Label();

    /** Introductory text for instructions */
    protected static Label label_Intro = new Label();

    /** Label instructing user to enter username/email */
    protected static Label label_UEIntro = new Label();

    /** Label instructing user to enter one-time password */
    protected static Label label_Passcode = new Label();

    /** Label for password recovery section */
    protected static Label label_RecoverPassword = new Label("Password Recovery.");

    /** Label for password recovery instructions */
    protected static Label label_RecoverPasswordLine = new Label("Please enter a new password.");

    /** Label to show entered password when "Show Password" is clicked */
    protected static Label label_ShowPassword = new Label();

    /*-*******************************************************************************************
     * GUI Text Fields
     */

    /** TextField for entering username or email */
    protected static TextField text_UsernameOrEmail = new TextField();

    /** TextField for entering one-time passcode */
    protected static TextField text_OneTimePasscode = new TextField();

    /** TextField for username in password recovery */
    protected static TextField text_Username = new TextField();

    /** PasswordField for entering new password */
    protected static PasswordField text_Password1 = new PasswordField();

    /** PasswordField for confirming new password */
    protected static PasswordField text_Password2 = new PasswordField();

    /*-*******************************************************************************************
     * GUI Buttons
     */

    /** Button to request a one-time password */
    protected static Button button_Request = new Button("Request One-time Password");

    /** Button to enter one-time password */
    protected static Button button_Enter = new Button("Enter");

    /** Help button for username */
    protected static Button button_UsernameHelp = new Button("?");

    /** Help button for password requirements */
    protected static Button button_PasswordHelp = new Button("?");

    /** Button to show password */
    protected static Button button_ShowPassword = new Button("Show Password");

    /** Button to hide password */
    protected static Button button_HidePassword = new Button("Hide Password");

    /** Button to reset password */
    protected static Button button_ResetPassword = new Button("Reset Password");

    /** Button to return to login page */
    protected static Button button_Return = new Button("Return to Login");

    /** Button to quit application */
    protected static Button button_Quit = new Button("Quit");

    /*-*******************************************************************************************
     * Alerts
     */

    /** Alert for username or password errors */
    protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

    /** Alert for username/password help */
    protected static Alert alertUsernamePasswordHelp = new Alert(AlertType.INFORMATION);

    /*-*******************************************************************************************
     * Other Attributes
     */

    /** Singleton instance of this view */
    private static ViewForgetPassword theView;

    /** Reference to the in-memory database */
    protected static Database theDatabase = applicationMain.FoundationsMain.database;

    /** JavaFX stage */
    protected static Stage theStage;

    /** Root pane for GUI elements */
    protected static Pane theRootPane;

    /** Scene shared by this page */
    private static Scene theForgetPasswordScene;

    /*-*******************************************************************************************
     * Constructors
     */

    /**********
     * <p> Method: displayForgetPassword(Stage ps) </p>
     * <p> Description: Displays the Forget Password page by initializing GUI elements and clearing fields.</p>
     * @param ps JavaFX Stage to display the scene
     */
    public static void displayForgetPassword(Stage ps) {
        theStage = ps;

        // Instantiate singleton
        theView = new ViewForgetPassword();
        if (theView == null); // placeholder

        // Remove expired one-time passwords
        theDatabase.removeExpiredEntries(); 

        // Clear dynamic input fields
        text_UsernameOrEmail.setText("");
        text_OneTimePasscode.setText("");
        text_Password1.setText("");
        text_Password2.setText("");
        text_Password1.textProperty().addListener((observable, oldValue, newValue)
            -> { ControllerForgetPassword.setPassword(); });

        // Set stage title and display
        theStage.setTitle("CSE 360 Foundations: Forget Password Page");
        theStage.setScene(theForgetPasswordScene);
        theStage.show();
    }

    /**********
     * <p> Method: ViewForgetPassword() </p>
     * <p> Description: Constructor that initializes all GUI widgets, layout, and event listeners.</p>
     */
    private ViewForgetPassword() {
        theRootPane = new Pane();
        theForgetPasswordScene = new Scene(theRootPane, width, height);

        // --- Setup Labels ---
        label_PageTitle.setText("Recover Password");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 10);

        label_Intro.setText("Admin will see your recover request as entered the username/email");
        setupLabelUI(label_Intro, "Arial", 20, width, Pos.CENTER, 0, 60);

        label_UEIntro.setText("Enter your Username or Email Address:");
        setupLabelUI(label_UEIntro, "Arial", 20, width, Pos.CENTER, 0, 110);

        label_Passcode.setText("Enter your one-time password from admin:");
        setupLabelUI(label_Passcode, "Arial", 20, width, Pos.CENTER, 0, 260);

        setupLabelUI(label_RecoverPassword, "Arial", 32, width, Pos.CENTER, 0, 10);
        setupLabelUI(label_RecoverPasswordLine, "Arial", 24, width, Pos.CENTER, 0, 70);
        setupLabelUI(label_ShowPassword, "Arial", 16, width, Pos.BASELINE_LEFT, 50, 245);

        // --- Setup TextFields ---
        text_UsernameOrEmail.setPromptText("Enter username/email address here");
        setupTextUI(text_UsernameOrEmail, "Arial", 18, 400, Pos.CENTER, 200, 150, true);
        text_UsernameOrEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            ControllerForgetPassword.setUsernameOrEmail();
        });

        text_OneTimePasscode.setPromptText("Enter one-time password here");
        setupTextUI(text_OneTimePasscode, "Arial", 18, 400, Pos.CENTER, 200, 300, true);
        text_OneTimePasscode.textProperty().addListener((obs, oldVal, newVal) -> {
            ControllerForgetPassword.setOneTimePasscode();
        });

        setupTextUI(text_Username, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, false);
        setupTextUI(text_Password1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, true);
        text_Password1.setPromptText("Enter the Password");
        setupTextUI(text_Password2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 270, true);
        text_Password2.setPromptText("Enter the Password Again");

        // --- Setup Buttons ---
        setupButtonUI(button_Request, "Dialog", 18, 400, Pos.CENTER, 200, 200);
        button_Request.setOnAction(event -> {ControllerForgetPassword.doRequestCode(); });

        setupButtonUI(button_Enter, "Dialog", 18, 400, Pos.CENTER, 200, 350);
        button_Enter.setOnAction(event -> {ControllerForgetPassword.doEnterCode(); });

        setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, 20, 520);
        button_Return.setOnAction(event -> {ControllerForgetPassword.performLogout(); });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 520);
        button_Quit.setOnAction(event -> {ControllerForgetPassword.performQuit(); });

        setupButtonUI(button_ShowPassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
        button_ShowPassword.setOnAction(event -> {
            theRootPane.getChildren().add(label_ShowPassword);
            theRootPane.getChildren().add(button_HidePassword);
            theRootPane.getChildren().remove(button_ShowPassword);
        });

        setupButtonUI(button_HidePassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
        button_HidePassword.setOnAction(event -> {
            theRootPane.getChildren().remove(label_ShowPassword);
            theRootPane.getChildren().remove(button_HidePassword);
            theRootPane.getChildren().add(button_ShowPassword);
        });

        setupButtonUI(button_PasswordHelp, "Dialog", 18, 18, Pos.BASELINE_LEFT, 350, 210);
        button_PasswordHelp.setOnAction(event -> {
            alertUsernamePasswordHelp.setTitle("Username/Password Help");
            alertUsernamePasswordHelp.setHeaderText("Password Requirements:");
            alertUsernamePasswordHelp.setContentText(
                "Needs at least one uppercase alphabet\n" +
                "Needs at least one lowercase alphabet\n" +
                "Needs at least one number\n" +
                "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n" +
                "Needs to be within 8-32 characters"
            );
            alertUsernamePasswordHelp.showAndWait();
        });

        setupButtonUI(button_ResetPassword, "Dialog", 18, 200, Pos.CENTER, 475, 210);
        button_ResetPassword.setOnAction(event -> {ControllerForgetPassword.doResetPassword(); });

        // --- Add all widgets to root pane ---
        theRootPane.getChildren().addAll(
            label_PageTitle, label_Intro, text_UsernameOrEmail, text_OneTimePasscode,
            label_UEIntro, label_Passcode, button_Request, button_Enter,
            button_Return, button_Quit
        );
    }

    /*-********************************************************************************************
     * Helper Methods
     */

    /**********
     * <p> Method: setupLabelUI </p>
     * <p> Description: Initializes a label with font, size, alignment, and coordinates.</p>
     * @param l Label to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }

    /**********
     * <p> Method: setupButtonUI </p>
     * <p> Description: Initializes a button with font, size, alignment, and coordinates.</p>
     * @param b Button to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }

    /**********
     * <p> Method: setupTextUI </p>
     * <p> Description: Initializes a TextField or PasswordField with font, size, alignment, position, and editability.</p>
     * @param t TextField to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     * @param p Alignment
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param e Editable if true
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
