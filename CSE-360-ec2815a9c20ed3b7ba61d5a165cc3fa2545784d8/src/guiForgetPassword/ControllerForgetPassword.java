package guiForgetPassword;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerForgetPassword {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/	
	
	protected static String input = "";
	protected static String email = "";
	protected static String code = "";
	protected static String username = "";
	protected static String passcode = "";
	
	protected static void setUsernameOrEmail() {
		input = ViewForgetPassword.text_UsernameOrEmail.getText();
		return;
	}
	
	protected static void setOneTimePasscode() {
		code = ViewForgetPassword.text_OneTimePasscode.getText();
		return;
	}
	
	protected static void setPassword() {
		String password = ViewForgetPassword.text_Password1.getText();
		if (password.length() <= 32) {
			ViewForgetPassword.label_ShowPassword.setText(password);
		}
		else {
			ViewForgetPassword.label_ShowPassword.setText("Too long!");
		}
		return;
	}
	
	protected static void doRequestCode() {
		if (ViewForgetPassword.theDatabase.getUserAccountDetails(input)) {
			email = ViewForgetPassword.theDatabase.getEmailAddress(input);
		} 
		else if (!ViewForgetPassword.theDatabase.getUsernameByEmail(input).isEmpty()) {
			email = input;
		}
		else {
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
			requestExist.setContentText("Username/Password recovery has been sent to the admin. Be patient for their reponse.");
			requestExist.showAndWait();
			return;
		}
		ViewForgetPassword.theDatabase.registerOneTimePasscodeRequest(email);
		Alert requestSent = new Alert(AlertType.INFORMATION);
		requestSent.setTitle("Request successfully sent to the admin");
		requestSent.setHeaderText("Successfully Sent");
		requestSent.setContentText("Wait for one-time password from the admin.");
		requestSent.showAndWait();
		System.out.println("finished!");
		return;
	}
	
	protected static void doEnterCode() {
		if (ViewForgetPassword.theDatabase.getUserAccountDetails(input)) {
			email = ViewForgetPassword.theDatabase.getEmailAddress(input);
		} 
		else if (!ViewForgetPassword.theDatabase.getUsernameByEmail(input).isEmpty()) {
			email = input;
		}
		else {
			Alert notFound = new Alert(AlertType.ERROR);
	        notFound.setTitle("Username/Email Not Found");
	        notFound.setHeaderText(null);
	        notFound.setContentText("No user with username or email \"" + input + "\" was found.");
	        notFound.showAndWait();
			return;
		}
		
		if (ViewForgetPassword.theDatabase.isValidOneTimePasscode(email, code)) {
			username = ViewForgetPassword.theDatabase.getUsernameByEmail(email);
			passcode = code;
			System.out.println("Username got: "+username);
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
		}
		else {
			Alert invalidCode = new Alert(AlertType.ERROR);
			invalidCode.setTitle("Invalid One-time Password");
			invalidCode.setHeaderText(null);
			invalidCode.setContentText("Your one-time password is invalid.");
			invalidCode.showAndWait();
			return;
		}
	}
	
	protected static void doResetPassword() {
		String password = ViewForgetPassword.text_Password1.getText();
		
		// Make sure the password is valid
		String passwordError = ModelForgetPassword.checkValidPassword(password);
		if (!passwordError.isEmpty()) {
			ViewForgetPassword.text_Password1.setText("");
			ViewForgetPassword.text_Password2.setText("");
			ViewForgetPassword.alertUsernamePasswordHelp.setTitle("Username/Password Help");
			ViewForgetPassword.alertUsernamePasswordHelp.setHeaderText("Password Requirements:");
			ViewForgetPassword.alertUsernamePasswordHelp.setContentText(
					"Needs at least one uppercase alphabet\n"
					+ "Needs at least one lowercase alphabet\n"
					+ "Needs at least one number\n"
					+ "Needs at least one special character: !#$%&'*+-/=?^_`{|}~\n"
					+ "Needs to be within 8-32 characters");
			ViewForgetPassword.alertUsernamePasswordHelp.showAndWait();
			System.out.println(passwordError);
			return;
		}
		
		// Make sure the two passwords are the same.	
		if (ViewForgetPassword.text_Password1.getText().
				compareTo(ViewForgetPassword.text_Password2.getText()) != 0) {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewForgetPassword.text_Password1.setText("");
			ViewForgetPassword.text_Password2.setText("");
			// If the passwords do not match, this alert dialog will tell the user
			ViewForgetPassword.alertUsernamePasswordError.setTitle("Passwords Do Not Match");
			ViewForgetPassword.alertUsernamePasswordError.setHeaderText("The two passwords must be identical.");
			ViewForgetPassword.alertUsernamePasswordError.setContentText("Correct the passwords and try again.");
			ViewForgetPassword.alertUsernamePasswordError.showAndWait();
			return;
		}
		
		ViewForgetPassword.theDatabase.updatePassword(username, password);
			
		ViewForgetPassword.theDatabase.removePasscodeAfterUse(passcode);

		guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgetPassword.theStage);
	}
	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewForgetPassword.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}

}
