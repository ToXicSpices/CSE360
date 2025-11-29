package guiAdminHome;

/*******
 * <p> Title: ModelAdminHome Class. </p>
 * 
 * <p> Description: The AdminHome Page Model. This class handles email input 
 * validation through a finite state machine (FSM). Although it has other 
 * attributes for potential FSM use, the primary functionality currently 
 * implemented is email validation.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-08-15 Initial version
 */
public class ModelAdminHome {
	/**Default constructor, not used*/
	private ModelAdminHome() {}

    /** Error message produced by the recognizer during validation */
    public static String recognizerErrorMessage = "";

    /** The input string being processed by the recognizer */
    public static String recognizerInput = "";

    /** The current state of the finite state machine */
    private static int state = 0;

    /** The next state of the finite state machine */
    private static int nextState = 0;

    /** The full input line being analyzed */
    private static String inputLine = "";

    /** The current character being analyzed in the input line */
    private static char currentChar;

    /** The index of the current character in the input line */
    private static int currentCharNdx;

    /** Flag indicating whether the FSM is currently running */
    private static boolean running;

    /** Tracks the size of the input being processed; limits numeric input to 16 characters */
    private static int inputSize = 0;

    /**********
     * <p> Advances to the next character in the input line for the FSM. </p>
     * <p> Updates the currentChar and currentCharNdx. If the end of the line is reached,
     * currentChar is set to newline ('\n').</p>
     */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = '\n'; 
		}
	}
	
	/**********
     * <p> Validates whether the provided string is a valid email address. </p>
     * <p> The validation checks for:</p>
     * <ul>
     * <li>Local part rules: at least one character, cannot start with '.', cannot have consecutive '.' unless in quotes.</li>
     * <li>Quotes: Special characters must be enclosed in quotes.</li>
     * <li>Presence of '@' separating local and domain parts.</li>
     * <li>Domain rules: at least one character, cannot start with '.' or '-', cannot end with '-', allows letters, numbers, '.' and '-'.</li>
     * <li>Email length smaller or equals to 254 characters.</li>
     * </ul>
     *
     * @param input The email string to validate
     * @return An error message if invalid, or empty string if valid
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
			switch (state) {
			
			// State 0 deals with special case of local-part of email address, local part cannot be empty and dot cannot be at start
			
			// A-Z, a-z, 0-9, printable char -> State 1
			// " -> State 2
			
			case 0:
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
					recognizerErrorMessage = "Local part of email address should not be empty";
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
					recognizerErrorMessage = "Local part of email address should not be empty";
					running = false;
				}
				
				// The execution of this state is finished
				// If the size is larger than email address limit of 254, the loop must stop
				if (inputSize > emailLimit) {
					recognizerErrorMessage = "Email length should not exceed 254 characters";
					running = false;
				}
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
		return recognizerErrorMessage;
	}
	
}