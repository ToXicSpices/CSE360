package guiUserUpdate;

public class Model {
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
			running = false;
		}
	}
	
	/* Check email validation
	 * 		Needs a local part with at least one character of any alphabets, numbers or printable characters
	 * 		Needs quotation on both sides if input special characters
	 * 		Needs "@" sign to split local and domain parts
	 * 		Needs a domain part with at least one character of any alphabets, numbers, periods(.) or hyphens(-)
	 * 			Domain should not start with periods or hyphens
	 * 			Domain should not end with hyphens
	 */
	public static String checkValidEmail(String input) {
		if(input.length() <= 0)
			return "\n*** ERROR *** The input is empty";
		
		String printableCharacters = "!#$%&'*+-/=?^_`{|}~";
		String specialCharacters = " (),:;<>@[\\]";
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
			switch (state) {
			
			// State 0 deals with local part of the email address ('xxx' in xxx@yyy)
			
			// A-Z, a-z, 0-9, printable char -> State 0
			// " -> State 1
			// @ -> State 2
			// special char -> error
			
			case 0:
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(emailLimit) >= 0)) {		// check printable characters
					nextState = 0;
					inputSize++;
				}
				else if (currentChar == '"') {									// check quotation (")
					nextState = 1;
					inputSize++;
				}
				else if (currentChar == '@') {									// check @
					if (inputSize == 0) {
						recognizerErrorMessage = "*** ERROR *** local part of email address should not be empty";
						running = false;
					}
					nextState = 2;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if ((printableCharacters.indexOf(emailLimit) >= 0)) {
					recognizerErrorMessage = "*** ERROR *** Needs quotations(\") for special characters like space and \"(),:;<>@[\\]\"";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "*** ERROR *** Invalid character locates at local part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "*** ERROR *** Needs \"@\" for email address";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit)
					recognizerErrorMessage = "*** ERROR *** Email length should not exceed 254 characters";
					running = false;
				break;
				
			// State 1 deals with special case of local-part of email address, special characters can be used within quotations
				
			// A-Z, a-z, 0-9, printable char, special char -> State 1
			// " -> State 0
				
			case 1:
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(printableCharacters.indexOf(emailLimit) >= 0) ||		// check printable characters
						(specialCharacters.indexOf(emailLimit) >= 0)) {			// check special characters
					nextState = 1;
					inputSize++;
				}
				else if (currentChar == '"') {									// check ending quotation(")
					nextState = 0;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar == '@') {
					recognizerErrorMessage = "*** ERROR *** Needs another quote(\") to end quotation";
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "*** ERROR *** Invalid character locates at local part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "*** ERROR *** Needs \"@\" for email address";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit)
					recognizerErrorMessage = "*** ERROR *** Email length should not exceed 254 characters";
					running = false;
				break;
				
			// State 2 deals with special case of domain-part of email, domain should not start with - or . nor be empty
				
			// A-Z, a-z, 0-9 -> State 3
				
			case 2:	
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9')) {			// check 0-9
					nextState = 3;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar == '-') {
					recognizerErrorMessage = "*** ERROR *** domain part of email address should not start with hyphens(-)";
					running = false;
				}
				else if (currentChar == '.') {
					recognizerErrorMessage = "*** ERROR *** domain part of email address should not start with periods(.)";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "*** ERROR *** Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "*** ERROR *** Domain part of email address should not be empty";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit)
					recognizerErrorMessage = "*** ERROR *** Email length should not exceed 254 characters";
					running = false;
				break;
			
			// State 3 deals with domain-part of email ('yyy' of xxx@yyy)
				
			// A-Z, a-z, 0-9, "." -> State 3
			// - -> State 4
				
			// This is the final state of finite machine
					
			case 3:	
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9') ||			// check 0-9
						(currentChar == '.')) {									// check "."
					nextState = 3;
					inputSize++;
				}
				else if (currentChar == '-') {									// check "-"
					nextState = 4;
					inputSize++;
				}
				
				// Here lists some potential errors due to invalid inputs
				else if (currentChar != '\n') {
					recognizerErrorMessage = "*** ERROR *** Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "";
					running = false;
				}
					
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit)
					recognizerErrorMessage = "*** ERROR *** Email length should not exceed 254 characters";
					running = false;
				break;
				
			// State 4 deals with special case of domain-part of email, domain cannot end with hyphens(-)
				
			// A-Z, a-z, 0-9 -> State 3
			// - -> State 4
				
			case 4:
				if ((currentChar >= 'A' && currentChar <= 'Z') ||				// check A-Z
						(currentChar >= 'a' && currentChar <= 'z') ||			// check a-z
						(currentChar >= '0' && currentChar <= '9')) {			// check 0-9
					nextState = 3;
					inputSize++;
				}
				else if (currentChar == '-') {									// check "-"
					nextState = 4;
				}
				
				// Here lists some potential error due to invalid inputs
				else if (currentChar == '.') {
					recognizerErrorMessage = "*** ERROR *** domain part of email address should not end with hyphens(-)";
					running = false;
				}
				else if (currentChar != '\n') {
					recognizerErrorMessage = "*** ERROR *** Invalid character locates at domain part of email address";
					running = false;
				}
				else {
					recognizerErrorMessage = "*** ERROR *** domain part of email address should not end with hyphens(-)";
					running = false;
				}

				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit)
					recognizerErrorMessage = "*** ERROR *** Email length should not exceed 254 characters";
					running = false;
				break;
			}

			if (running) {
				moveToNextCharacter();
				state = nextState;
				nextState = -1;
			}
		}
		return recognizerErrorMessage;
	}
}