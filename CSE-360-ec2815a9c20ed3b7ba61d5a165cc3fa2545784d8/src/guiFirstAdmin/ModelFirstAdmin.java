package guiFirstAdmin;

/*******
 * <p> Title: ModelFirstAdmin Class. </p>
 * 
 * <p> Description: The First System Startup Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting a username and password and then saving it in the
 * database.  When the code is enhanced for input validation, this model may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ModelFirstAdmin {
	public static String recognizerErrorMessage = "";	// The error message text
	public static String recognizerInput = "";			// The input being processed
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is running
	private static int inputSize = 0;			// A numeric value may not exceed 16 characters
	
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = '\n'; 
		}
	}
	
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
						
				// A-Z, a-z, 0-9 -> State 1
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
	
	
	/* Check password validation
	 * 		Needs at least one uppercase alphabet
	 * 		Needs at least one lowercase alphabet
	 * 		Needs at least one number
	 * 		Needs at least one special character
	 * 		Needs to be within 8-32 characters
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
	
	/* Check email validation
	 * 		Needs a local part with at least one character of any alphabets, numbers or printable characters
	 * 			Local should not start with periods
	 * 			Local should not contain consecutive periods (..) unless in quotation
	 * 		Needs quotation on both sides if input special characters
	 * 		Needs "@" sign to split local and domain parts
	 * 		Needs a domain part with at least one character of any alphabets, numbers, periods(.) or hyphens(-)
	 * 			Domain should not start with periods or hyphens
	 * 			Domain should not end with hyphens
	 */
	public static String checkValidEmail(String input) {
		if(input.length() <= 0)
			return "Email is empty";
		
		String printableCharacters = "!#$%&'*+-/=?^_`{|}~";
		String specialCharacters = " (),:;<>@[\\].";
		int emailLimit = 254;
		
		state = 0;
		inputLine = input;
		currentCharNdx = 0;
		currentChar = input.charAt(0);
		recognizerInput = input;
		running = true;
		nextState = -1;
		inputSize = 0;
		
		while (running) {
			System.out.println("current char is: " + currentCharNdx);
			switch (state) {
			
			// State 0 deals with special case of local-part of email address, local part cannot be empty and dot cannot be at start
			
			// A-Z, a-z, 0-9, printable char -> State 1
			// " -> State 2
			
			case 0:
				System.out.println(state);
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(currentChar) >= 0)) {		// check printable characters
					nextState = 1;
					inputSize++;
					System.out.println("checkpoint: alpha-print");
				}
				else if (currentChar == '"') {									// check quotation (")
					nextState = 2;
					inputSize++;
					System.out.println("checkpoint: quote");
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar == '@') {	
					recognizerErrorMessage = "Local part of email address should not be empty";
					running = false;
					System.out.println("checkpoint: @");
				}
				else if ((specialCharacters.indexOf(currentChar) >= 0)) {
					recognizerErrorMessage = "Needs quotations(\") for special characters like space and \"(),:;<>@[\\]\"";
					running = false;
					System.out.println("checkpoint: special");
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at local part of email address";
					running = false;
					System.out.println("checkpoint: invalid");
				}
				else {
					recognizerErrorMessage = "Local part of email address should not be empty";
					running = false;
					System.out.println("checkpoint: empty");
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					System.out.println("checkpoint: input too big");
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				System.out.println("checkpoint: state 0 finished");
				break;
			
			// State 1 deals with local part of the email address ('xxx' in xxx@yyy)
				
				// A-Z, a-z, 0-9, printable char -> State 1
				// " -> State 2
				// . -> State 3
				// @ -> State 4
			
			case 1:
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(currentChar) >= 0)) {		// check printable characters
					nextState = 1;
					inputSize++;
				}
				else if (currentChar == '"') {									// check quotation (")
					nextState = 2;
					inputSize++;
				}
				else if (currentChar == '.') {
					nextState = 3;
					inputSize++;
				}
				else if (currentChar == '@') {	
					nextState = 4;
					inputSize++;
				}
				
				
				// Here lists some potential errors due to invalid inputs
				else if ((specialCharacters.indexOf(currentChar) >= 0)) {
					recognizerErrorMessage = "Needs quotations(\") for special characters like space and \"(),:;<>@[\\]\"";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at local part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "Needs \"@\" for email address";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
				
			// State 2 deals with special case of local-part of email address, special characters can be used within quotations
				
			// A-Z, a-z, 0-9, printable char, special char -> State 2
			// " -> State 1
				
			case 2:
				System.out.println(state);
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(currentChar) >= 0) ||		// check printable characters
						(specialCharacters.indexOf(currentChar) >= 0)) {			// check special characters
					nextState = 2;
					inputSize++;
				}
				else if (currentChar == '"') {									// check ending quotation(")
					nextState = 1;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at local part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "Needs another quote(\") to end quotation";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
			
			// State 3 deals with special case of local-part of email, should not end with . nor consecutive .
				
			// A-Z, a-z, 0-9 -> State 3
				
			case 3:
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(currentChar) >= 0)) {		// check printable characters
					nextState = 1;
					inputSize++;
				}
				else if (currentChar == '"') {									// check quotation (")
					nextState = 2;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar == '@') {	
					recognizerErrorMessage = "Local part of email address should not end with dots";
					running = false;
				}
				else if (currentChar == '.') {
					recognizerErrorMessage = "Should not be consecutive dots in local part of email address";
					running = false;
				}
				else if ((specialCharacters.indexOf(currentChar) >= 0)) {
					recognizerErrorMessage = "Needs quotations(\") for special characters like space and \"(),:;<>@[\\]\"";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at local part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "Local part of email address should not end with dots";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
				
			// State 4 deals with special case of domain-part of email, domain should not start with - or . nor be empty
				
			// A-Z, a-z, 0-9 -> State 5
				
			case 4:	
				System.out.println(state);
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9')) {			// check 0-9
					nextState = 5;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar == '-') {
					recognizerErrorMessage = "Domain part of email address should not start with hyphens(-)";
					running = false;
				}
				else if (currentChar == '.') {
					recognizerErrorMessage = "Domain part of email address should not start with periods(.)";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "Domain part of email address should not be empty";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
			
			// State 5 deals with domain-part of email ('yyy' of xxx@yyy)
				
			// A-Z, a-z, 0-9, "." -> State 5
			// - -> State 6
				
			// This is the final state of finite machine
					
			case 5:	
				System.out.println(state);
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(currentChar == '.')) {									// check "."
					nextState = 5;
					inputSize++;
				}
				else if (currentChar == '-') {									// check "-"
					nextState = 6;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "";
					running = false;
				}
					
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
				
			// State 4 deals with special case of domain-part of email, domain cannot end with hyphens(-)
				
			// A-Z, a-z, 0-9 -> State 3
			// - -> State 4
				
			case 6:
				System.out.println(state);
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9')) {			// check 0-9
					nextState = 5;
					inputSize++;
				}
				else if (currentChar == '-') {									// check "-"
					nextState = 6;
				}
				
				// Here lists some potential error due to invalid inputs
				else if (currentChar == '.') {
					recognizerErrorMessage = "domain part of email address should not end with hyphens(-)";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "domain part of email address should not end with hyphens(-)";
					running = false;
				}

				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
				break;
			}
			
			if (running) {
				moveToNextCharacter();
				state = nextState;
				nextState = -1;
			}
		}
		System.out.println("end");
		return recognizerErrorMessage;
	}
}