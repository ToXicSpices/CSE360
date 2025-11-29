package guiForgetPassword;

/*******
 * <p> Title: ModelForgetPassword Class. </p>
 * 
 * <p> Description: This class provides validation methods for usernames and passwords in a 
 * "Forget Password" workflow. It uses finite state machines (FSM) to ensure that user inputs 
 * meet defined constraints:</p>
 * <ul>
 *   <li>Usernames must start with an alphabet, can include A-Z, a-z, 0-9, ".", "-", "_", and be 4-16 characters long.</li>
 *   <li>Passwords must contain at least one uppercase, one lowercase, one number, one special character, and be 8-32 characters long.</li>
 * </ul>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-11-13 Initial version
 *
 */
	public class ModelForgetPassword {
		/**Default constructor, not used*/
		private ModelForgetPassword() {}
		
		/** The error message produced during validation, empty if valid */
	    public static String recognizerErrorMessage = "";  
	    
	    /** The input being processed during FSM execution */
	    public static String recognizerInput = "";         

	    /** The input line (username or password) being processed */
	    private static String inputLine = "";               
	    
	    /** The current character being processed in the input line */
	    private static char currentChar;                    

	    /** The index of the current character in the input line */
	    private static int currentCharNdx;                 
	    
	    /** Flag to indicate if the FSM is still running */
	    private static boolean running;                     
	    
	    /*******
	     * <p> Method: checkValidPassword(String input) </p>
	     * 
	     * <p> Description: Validates a password string to ensure it meets the following criteria:</p>
	     * <ul>
	     *   <li>Contains at least one uppercase letter</li>
	     *   <li>Contains at least one lowercase letter</li>
	     *   <li>Contains at least one number</li>
	     *   <li>Contains at least one special character</li>
	     *   <li>Has 8-32 characters</li>
	     * </ul>
	     *
	     * @param input The password string to validate
	     * @return Returns an error message string if invalid, or empty string if valid
	     *
	     */
		public static String checkValidPassword(String input) {
			if(input.length() <= 0)
				return "Password is empty";
			
			inputLine = input;
			currentCharNdx = 0;
			currentChar = input.charAt(0);
			recognizerInput = input;
			running = true;
			boolean foundUpperCase = false;
			boolean foundLowerCase = false;
			boolean foundNumericDigit = false;
			boolean foundSpecialChar = false;
			boolean foundProperLength = false;
			
			while (running) {
				// The cascading if statement sequentially tries the current character against all of
					// the valid transitions, each associated with one of the requirements
					if (currentChar >= 'A' && currentChar <= 'Z') {
						foundUpperCase = true;
					} else if (currentChar >= 'a' && currentChar <= 'z') {
						foundLowerCase = true;
					} else if (currentChar >= '0' && currentChar <= '9') {
						foundNumericDigit = true;
					} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
						foundSpecialChar = true;
					} else {
						return "An invalid character has been found in password!";
					}
					if ((currentCharNdx >= 7) && (currentCharNdx <= 32)) {		// ProperLength needs to be 8-32 characters
						foundProperLength = true;
					}
					else {
						foundProperLength = false;								// If exceeds 32 characters, it fails
					}
							
					// Go to the next character if there is one
					currentCharNdx++;
					if (currentCharNdx >= inputLine.length())
						running = false;
					else
						currentChar = input.charAt(currentCharNdx);
			}

			if (!foundUpperCase)
				return "Needs a upper case alphabet for password";
			
			if (!foundLowerCase)
				return "Needs a lower case alphabet for password";
			
			if (!foundNumericDigit)
				return "Needs a number for password";
				
			if (!foundSpecialChar)
				return "Needs a special for password";
				
			if (!foundProperLength)
				return "Must be within 8-32 characters";

			return "";
		}
	}
