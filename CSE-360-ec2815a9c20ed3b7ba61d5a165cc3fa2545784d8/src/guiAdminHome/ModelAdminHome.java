package guiAdminHome;

/*******
 * <p> Title: ModelAdminHome Class. </p>
 * 
 * <p> Description: The AdminHome Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting role information and saving it in the
 * database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ModelAdminHome {

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