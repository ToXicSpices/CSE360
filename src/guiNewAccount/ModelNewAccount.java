package guiNewAccount;

/*******
 * <p> Title: ModelNewAccount Class. </p>
 * 
 * <p> Description: The NewAccount Page Model. This class provides validation methods for
 * usernames and passwords on the New Account page. Username and password inputs are validated
 * using a finite state machine (FSM) and character checks. The class ensures that:</p>
 * <ul>
 *   <li>Usernames start with an alphabet, can include A-Z, a-z, 0-9, ".", "-", "_", and are 4-16 characters long</li>
 *   <li>Passwords contain at least one uppercase letter, one lowercase letter, one number, one special character, and are 8-32 characters long</li>
 * </ul>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-08-15 Initial version
 *
 */
public class ModelNewAccount {
	/**Default constructor, not used*/
	private ModelNewAccount() {}
	
	/** The error message produced during validation, empty if valid */
    public static String recognizerErrorMessage = "";  
    
    /** The input being processed during FSM execution */
    public static String recognizerInput = "";         

    /** The current state of the FSM */
    private static int state = 0;                       
    
    /** The next state of the FSM */
    private static int nextState = 0;                   
    
    /** The input line (username or password) being processed */
    private static String inputLine = "";               
    
    /** The current character being processed in the input line */
    private static char currentChar;                    

    /** The index of the current character in the input line */
    private static int currentCharNdx;                 
    
    /** Flag to indicate if the FSM is still running */
    private static boolean running;                     
    
    /** Counter for the input size; ensures limits for username/password */
    private static int inputSize = 0;                   

    /*******
     * <p> Method: moveToNextCharacter() </p>
     * 
     * <p> Description: Advances the FSM to the next character in the input line. If the end 
     * of the input line is reached, sets the current character to newline.</p>
     *
     */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = '\n'; 
		}
	}
	
	/*******
     * <p> Method: checkValidUsername(String input) </p>
     * 
     * <p> Description: Validates a username using a finite state machine (FSM). It ensures:</p>
     * <ul>
     *   <li>Username starts with an alphabet (A-Z, a-z)</li>
     *   <li>Can contain letters, numbers, ".", "-", "_" after the first character</li>
     *   <li>Has 4-16 characters</li>
     * </ul>
     *
     * @param input The username string to validate
     * @return Returns an error message string if invalid, or empty string if valid
     *
     */
	public static String checkValidUsername(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			return "Username is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		running = true;						// Start the loop
		nextState = -1;						// There is no next state
				
		// This is the place where semantic actions for a transition to the initial state occur
				
		inputSize = 0;					// Initialize the UserName size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
						
				// The current character is checked against A-Z, a-z, 0-9. If any are matched
				// the FSM goes to state 1
						
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
					nextState = 1;
							
					// Count the character 
					inputSize++;
							
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else {
					running = false;
				}
						
				// The execution of this state is finished
				break;
					
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a period that transitions to state 2 

						
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
							
					// Count the character
					inputSize++;
				}
				// . -> State 2
				else if ((currentChar == '.') ||							// Check for .
							(currentChar == '-') ||						// Check for -
							(currentChar == '_')) {						// Check for _
					nextState = 2;
							
					// Count the .
					inputSize++;
				}				
				// If it is none of those characters, the FSM halts
				else {
					recognizerErrorMessage = "";
					running = false;
				}
						
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (inputSize > 16)
					running = false;
				break;			
						
			case 2: 
				// State 2 deals with a character after a period in the name.
						
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
							
					// Count the odd digit
					inputSize++;
							
				}
				else if ((currentChar == '.') ||						// Check for .
						(currentChar == '-') ||							// Check for -
						(currentChar == '_')) {							// Check for _
				nextState = 2;
						
				// Count the .
				inputSize++;
				
				}
				// If it is none of those characters, the FSM halts
				else {
					running = false;
				}

				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (inputSize > 16)
					running = false;
				break;			
			}
					
			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();
				
				// Move to the next state
				state = nextState;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
			
		}
				
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		if (state == 0) {
			return "A Username must start with alphabet!(A-Z or a-z).";
		}
		else if (state == 2) { 
			return "A Username character after a period, underscore or minus sign must be A-Z, a-z, 0-9.";
		}
		else if (inputSize < 4) {
			// UserName is too small
			return "A Username must have at least 4 characters.\n";
		}
		else if (inputSize > 16) {
			return "A Username must have no more than 16 characters.";
		}
		else if (currentCharNdx < input.length()) {
			// There are characters remaining in the input, so the input is not valid
			return "A Username character may only contain the characters A-Z, a-z, 0-9, \".\", \"-\", \"_\".";
		}
		
		return "";
	}
	
	
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
		
		state = 0;
		inputLine = input;
		currentCharNdx = 0;
		currentChar = input.charAt(0);
		recognizerInput = input;
		running = true;
		nextState = -1;
		inputSize = 0;
		
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