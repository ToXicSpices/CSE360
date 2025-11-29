package guiFirstAdmin;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * <p> Title: ViewFirstAdmin Class </p>
 * 
 * <p> Description: This class represents the GUI page for creating the very first Admin user.
 * It ensures the first admin can set a secure username, password, and email address. 
 * This avoids hardcoding credentials into the application.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn
 * @version 1.02 2025-11-13 Full field and parameter Javadocs
 */
public class ViewFirstAdmin {

    /*-********************************************************************************************
     * Fields
     ********************************************************************************************/

    /** The application window width */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** The application window height */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /** Label for the main application title */
    private static Label label_ApplicationTitle = new Label("Foundation Application Startup Page");

    /** Label for first line of welcome message */
    private static Label label_TitleLine1 = new Label(
        " You are the first user. You must be an administrator."
    );

    /** Label for second line of instructions */
    private static Label label_TitleLine2 = new Label(
        "Enter the Admin's Username, the Password twice, and then click on Setup Admin Account."
    );

    /** Alert to display any sign-up errors */
    protected static Alert alertSignUpError = new Alert(AlertType.INFORMATION);

    /** TextField for entering Admin username */
    protected static TextField text_AdminUsername = new TextField();

    /** PasswordField for entering Admin password */
    protected static PasswordField text_AdminPassword1 = new PasswordField();

    /** PasswordField for confirming Admin password */
    protected static PasswordField text_AdminPassword2 = new PasswordField();

    /** Label for showing password in plain text */
    protected static Label label_ShowPassword = new Label();

    /** TextField for entering Admin email */
    protected static TextField text_AdminEmail = new TextField();

    /** Help button for username */
    private static Button button_AdminUsernameHelp = new Button("?");

    /** Help button for password */
    private static Button button_AdminPasswordHelp = new Button("?");

    /** Button to show password */
    private static Button button_AdminShowPassword = new Button("Show Password");

    /** Button to hide password */
    private static Button button_AdminHidePassword = new Button("Hide Password");

    /** Button to set up Admin account */
    private static Button button_AdminSetup = new Button("Setup Admin Account");

    /** Button to quit the application */
    private static Button button_Quit = new Button("Quit");

    /** JavaFX Stage reference */
    protected static Stage theStage;

    /** Root Pane for layout */
    private static Pane theRootPane;

    /** Scene for this page */
    private static Scene theFirstAdminScene = null;

    /** Role code for Admin */
    private static final int theRole = 1;

    /*-********************************************************************************************
     * Constructor / Display
     ********************************************************************************************/

    /**
     * <p>Method: displayFirstAdmin(Stage ps)</p>
     *
     * <p>Description: Displays the first Admin setup page. Sets up the JavaFX stage and scene
     * for entering the initial admin account information. Only called once when the application
     * is first initialized.</p>
     *
     * @param ps The JavaFX Stage to display the GUI
     */
    public static void displayFirstAdmin(Stage ps) {
        theStage = ps;
        new ViewFirstAdmin(); // Only called once
        applicationMain.FoundationsMain.activeHomePage = theRole;
        theStage.setTitle("CSE 360 Foundation Code: First User Account Setup");
        theStage.setScene(theFirstAdminScene);
        theStage.show();
    }

    /**
     * <p>Method: ViewFirstAdmin()</p>
     *
     * <p>Description: Private constructor that initializes all GUI elements for the first Admin
     * setup page. Sets up labels, text fields, buttons, and help dialogs. Handles password
     * visibility toggling and button actions for user input validation and admin setup.</p>
     */
    private ViewFirstAdmin() {
        theRootPane = new Pane();
        theFirstAdminScene = new Scene(theRootPane, width, height);

        // Labels
        setupLabelUI(label_ApplicationTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
        setupLabelUI(label_TitleLine1, "Arial", 24, width, Pos.CENTER, 0, 70);
        setupLabelUI(label_TitleLine2, "Arial", 18, width, Pos.CENTER, 0, 130);

        // Text fields
        setupTextUI(text_AdminUsername, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 160, true);
        text_AdminUsername.setPromptText("Enter Admin Username");
        text_AdminUsername.textProperty().addListener((obs, oldVal, newVal) -> ControllerFirstAdmin.setAdminUsername());

        setupTextUI(text_AdminPassword1, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 210, true);
        text_AdminPassword1.setPromptText("Enter Admin Password");
        text_AdminPassword1.textProperty().addListener((obs, oldVal, newVal) -> ControllerFirstAdmin.setAdminPassword1());

        setupTextUI(text_AdminPassword2, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 270, true);
        text_AdminPassword2.setPromptText("Enter Admin Password Again");
        text_AdminPassword2.textProperty().addListener((obs, oldVal, newVal) -> ControllerFirstAdmin.setAdminPassword2());

        setupTextUI(text_AdminEmail, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 320, true);
        text_AdminEmail.setPromptText("Enter Admin Email");
        text_AdminEmail.textProperty().addListener((obs, oldVal, newVal) -> ControllerFirstAdmin.setAdminEmail());

        // Buttons
        setupButtonUI(button_AdminSetup, "Dialog", 18, 200, Pos.CENTER, 475, 210);
        button_AdminSetup.setOnAction(e -> ControllerFirstAdmin.doSetupAdmin(theStage, 1));

        setupButtonUI(button_AdminShowPassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
        button_AdminShowPassword.setOnAction(e -> {
            theRootPane.getChildren().add(label_ShowPassword);
            theRootPane.getChildren().add(button_AdminHidePassword);
            theRootPane.getChildren().remove(button_AdminShowPassword);
        });

        setupButtonUI(button_AdminHidePassword, "Dialog", 18, 200, Pos.CENTER, 475, 260);
        button_AdminHidePassword.setOnAction(e -> {
            theRootPane.getChildren().remove(label_ShowPassword);
            theRootPane.getChildren().remove(button_AdminHidePassword);
            theRootPane.getChildren().add(button_AdminShowPassword);
        });

        setupLabelUI(label_ShowPassword, "Arial", 16, width, Pos.BASELINE_LEFT, 50, 245);

        alertSignUpError.setTitle("Invalid username/password!");
        alertSignUpError.setHeaderText(null);

        // Help buttons
        setupButtonUI(button_AdminUsernameHelp, "Dialog", 18, 18, Pos.BASELINE_LEFT, 350, 160);
        button_AdminUsernameHelp.setOnAction(e -> {
            alertSignUpError.setTitle("Username/Password Help");
            alertSignUpError.setHeaderText("Username Requirements:");
            alertSignUpError.setContentText(
                    "Needs at least 4 characters\n" +
                    "May use any upper or lower case alphabet\n" +
                    "May use any number\n" +
                    "May use special characters: \".\", \"-\", \"_\"\n" +
                    "Must not use special characters at the end\n" +
                    "Must not exceed 16 characters");
            alertSignUpError.showAndWait();
        });

        setupButtonUI(button_AdminPasswordHelp, "Dialog", 18, 18, Pos.BASELINE_LEFT, 350, 210);
        button_AdminPasswordHelp.setOnAction(e -> {
            alertSignUpError.setTitle("Username/Password Help");
            alertSignUpError.setHeaderText("Password Requirements:");
            alertSignUpError.setContentText(
                    "Needs at least one uppercase alphabet\n" +
                    "Needs at least one lowercase alphabet\n" +
                    "Needs at least one number\n" +
                    "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n" +
                    "Needs to be within 8-32 characters");
            alertSignUpError.showAndWait();
        });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 520);
        button_Quit.setOnAction(e -> ControllerFirstAdmin.performQuit());

        theRootPane.getChildren().addAll(label_ApplicationTitle, label_TitleLine1,
                label_TitleLine2, text_AdminUsername,
                text_AdminPassword1, text_AdminPassword2, text_AdminEmail,
                button_AdminUsernameHelp, button_AdminPasswordHelp,
                button_AdminSetup, button_AdminShowPassword,
                button_Quit);
    }

    /*-********************************************************************************************
     * Helper Methods
     ********************************************************************************************/

    /**
     * Initializes a Label.
     * 
     * @param l  The Label to initialize
     * @param ff Font family
     * @param f Font size
     * @param w Width
     * @param p Alignment
     * @param x X position
     * @param y Y position
     */
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * Initializes a Button.
     * 
     * @param b  The Button to initialize
     * @param ff Font family
     * @param f Font size
     * @param w Width
     * @param p Alignment
     * @param x X position
     * @param y Y position
     */
    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**
     * Initializes a TextField.
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
    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setEditable(e);
    }
}
