package guiForgetPassword;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerForgetPassword Class. </p>
 * 
 * <p> Description: This class provides controller actions for the Forget Password page.
 * All methods are static and are invoked by the View singleton. It allows users to request
 * a one-time passcode, enter the passcode, and reset their password securely.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerForgetPassword {

    /*-********************************************************************************************
     * Fields
     */

    /** Holds the last entered username or email */
    protected static String input = "";

    /** Holds the email corresponding to the account being recovered */
    protected static String email = "";

    /** Holds the one-time passcode entered by the user */
    protected static String code = "";

    /** Holds the username of the account being recovered */
    protected static String username = "";

    /** Holds the one-time passcode validated by the admin */
    protected static String passcode = "";

    /**********
     * <p> Default constructor </p>
     * <p> Description: ControllerForgetPassword is not intended to be instantiated,
     * but a default constructor is included for completeness. </p>
     */
    protected ControllerForgetPassword() {
        // No implementation needed, class is fully static
    }

    /**********
     * <p> Method: setUsernameOrEmail </p>
     * <p> Description: Sets the local copy of the username or email entered by the user. </p>
     */
    protected static void setUsernameOrEmail() {
        input = ViewForgetPassword.text_UsernameOrEmail.getText();
    }

    /**********
     * <p> Method: setOneTimePasscode </p>
     * <p> Description: Sets the local copy of the one-time passcode entered by the user. </p>
     */
    protected static void setOneTimePasscode() {
        code = ViewForgetPassword.text_OneTimePasscode.getText();
    }

    /**********
     * <p> Method: setPassword </p>
     * <p> Description: Updates the password label as the user types a new password. Shows
     * "Too long!" if password exceeds 32 characters. </p>
     */
    protected static void setPassword() {
        String password = ViewForgetPassword.text_Password1.getText();
        if (password.length() <= 32) {
            ViewForgetPassword.label_ShowPassword.setText(password);
        } else {
            ViewForgetPassword.label_ShowPassword.setText("Too long!");
        }
    }

    /**********
     * <p> Method: doRequestCode </p>
     * <p> Description: Processes a request for a one-time passcode. Checks if the user exists,
     * validates that a request has not already been sent, and registers a new passcode request
     * in the database. </p>
     */
    protected static void doRequestCode() {
        if (ViewForgetPassword.theDatabase.getUserAccountDetails(input)) {
            email = ViewForgetPassword.theDatabase.getEmailAddress(input);
        } else if (!ViewForgetPassword.theDatabase.getUsernameByEmail(input).isEmpty()) {
            email = input;
        } else {
            Alert notFound = new Alert(AlertType.ERROR);
            notFound.setTitle("Username/Email Not Found");
            notFound.setHeaderText(null);
            notFound.setContentText("No user with username or email \"" + input + "\" was found.");
            notFound.showAndWait();
            return;
        }

        if (ViewForgetPassword.theDatabase.emailaddressHasBeenSentPasscode(email)) {
            Alert requestExist = new Alert(AlertType.ERROR);
            requestExist.setTitle("Request Already Sent");
            requestExist.setHeaderText(null);
            requestExist.setContentText("Username/Password recovery has been sent to the admin. Be patient for their response.");
            requestExist.showAndWait();
            return;
        }

        ViewForgetPassword.theDatabase.registerOneTimePasscodeRequest(email);

        Alert requestSent = new Alert(AlertType.INFORMATION);
        requestSent.setTitle("Request Successfully Sent");
        requestSent.setHeaderText("Successfully Sent");
        requestSent.setContentText("Wait for one-time password from the admin.");
        requestSent.showAndWait();
        System.out.println("finished!");
    }

    /**********
     * <p> Method: doEnterCode </p>
     * <p> Description: Validates the one-time passcode entered by the user. If valid,
     * prepares the password reset UI. Otherwise, shows an error alert. </p>
     */
    protected static void doEnterCode() {
        if (ViewForgetPassword.theDatabase.getUserAccountDetails(input)) {
            email = ViewForgetPassword.theDatabase.getEmailAddress(input);
        } else if (!ViewForgetPassword.theDatabase.getUsernameByEmail(input).isEmpty()) {
            email = input;
        } else {
            Alert notFound = new Alert(AlertType.ERROR);
            notFound.setTitle("Username/Email Not Found");
            notFound.setHeaderText(null);
            notFound.setContentText("No user with username or email \"" + input + "\" was found.");
            notFound.showAndWait();
            return;
        }

        if (ViewForgetPassword.theDatabase.getPasscodeByEmail(email).equals(code)) {
            username = ViewForgetPassword.theDatabase.getUsernameByEmail(email);
            passcode = code;
            System.out.println("Username got: " + username);
            ViewForgetPassword.theRootPane.getChildren().setAll(
                ViewForgetPassword.label_RecoverPassword,
                ViewForgetPassword.label_RecoverPasswordLine,
                ViewForgetPassword.text_Username,
                ViewForgetPassword.text_Password1,
                ViewForgetPassword.text_Password2,
                ViewForgetPassword.button_ShowPassword,
                ViewForgetPassword.button_UsernameHelp,
                ViewForgetPassword.button_PasswordHelp,
                ViewForgetPassword.button_ResetPassword,
                ViewForgetPassword.button_Return,
                ViewForgetPassword.button_Quit
            );
            ViewForgetPassword.text_Username.setText(username);
        } else {
            Alert invalidCode = new Alert(AlertType.ERROR);
            invalidCode.setTitle("Invalid One-time Password");
            invalidCode.setHeaderText(null);
            invalidCode.setContentText("Your one-time password is invalid.");
            invalidCode.showAndWait();
        }
    }

    /**********
     * <p> Method: doResetPassword </p>
     * <p> Description: Resets the user's password after validating it and confirming
     * the password match. Updates the database and removes the used passcode. Navigates
     * back to the login page. </p>
     */
    protected static void doResetPassword() {
        String password = ViewForgetPassword.text_Password1.getText();

        // Validate password
        String passwordError = ModelForgetPassword.checkValidPassword(password);
        if (!passwordError.isEmpty()) {
            ViewForgetPassword.text_Password1.setText("");
            ViewForgetPassword.text_Password2.setText("");
            ViewForgetPassword.alertUsernamePasswordHelp.setTitle("Username/Password Help");
            ViewForgetPassword.alertUsernamePasswordHelp.setHeaderText("Password Requirements:");
            ViewForgetPassword.alertUsernamePasswordHelp.setContentText(
                "Needs at least one uppercase alphabet\n" +
                "Needs at least one lowercase alphabet\n" +
                "Needs at least one number\n" +
                "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n" +
                "Needs to be within 8-32 characters");
            ViewForgetPassword.alertUsernamePasswordHelp.showAndWait();
            System.out.println(passwordError);
            return;
        }

        // Validate password match
        if (!ViewForgetPassword.text_Password1.getText()
                .equals(ViewForgetPassword.text_Password2.getText())) {
            ViewForgetPassword.text_Password1.setText("");
            ViewForgetPassword.text_Password2.setText("");
            ViewForgetPassword.alertUsernamePasswordError.setTitle("Passwords Do Not Match");
            ViewForgetPassword.alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
            ViewForgetPassword.alertUsernamePasswordError.setContentText("Correct the passwords and try again.");
            ViewForgetPassword.alertUsernamePasswordError.showAndWait();
            return;
        }

        ViewForgetPassword.theDatabase.updatePassword(username, password);
        ViewForgetPassword.theDatabase.removePasscodeRowByEmail(email);

        guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgetPassword.theStage);
    }

    /**********
     * <p> Method: performLogout </p>
     * <p> Description: Logs the user out and returns to the login page. </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgetPassword.theStage);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates the program execution. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
