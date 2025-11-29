package guiUserUpdate;

/*******
 * <p> Title: ModelUserUpdate Class. </p>
 * 
 * <p> Description: This class provides validation for user input fields related to updating user 
 * information. Currently, it implements email validation using a finite state machine (FSM) to 
 * ensure the email format meets standard constraints.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-11-13 Initial version
 *
 */
public class ModelUserUpdate {
	
	/**Default constructor, not used*/
	private ModelUserUpdate() {}
	
	/** The error message produced during email validation, empty if valid */
    public static String recognizerErrorMessage = "";  
    
    /** The input being processed during FSM execution */
    public static String recognizerInput = "";         
    
    /** The current state value of the finite state machine */
    private static int state = 0;                       
    
    /** The next state value of the finite state machine */
    private static int nextState = 0;                   
    
    /** The input line (email string) being processed */
    private static String inputLine = "";               
    
    /** The current character being processed in the input line */
    private static char currentChar;                    

    /** The index of the current character in the input line */
    private static int currentCharNdx;                 
    
    /** Flag to indicate if the FSM is still running */
    private static boolean running;                     
    
    /** Counter for the input size; ensures numeric/email length limits */
    private static int inputSize = 0;         
	
	 /*******
     * <p> Method: moveToNextCharacter() </p>
     * 
     * <p> Description: Advances the FSM to the next character in the input line. If the end 
     * of the input line is reached, sets the current character to newline and stops FSM processing.</p>
     *
     */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = '\n';
			running = false;
		}
	}
	
	/*******
     * <p> Method: checkValidEmail(String input) </p>
     * 
     * <p> Description: Validates an email address using a finite state machine. The FSM checks 
     * the following conditions:</p>
     * <ul>
     *   <li>The local part must contain at least one character from alphabets, numbers, or printable characters.</li>
     *   <li>If special characters are used in the local part, they must be enclosed in quotes.</li>
     *   <li>The '@' character splits the local and domain parts.</li>
     *   <li>The domain part must contain at least one character from alphabets, numbers, periods (.), or hyphens (-).</li>
     *   <li>The domain cannot start with a period or hyphen, and cannot end with a hyphen.</li>
     *   <li>Email must not exceed 254 characters.</li>
     * </ul>
     *
     * @param input Specifies the email address string to be validated.
     *
     * @return Returns an error message string if the email is invalid, or an empty string if valid.
     *
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