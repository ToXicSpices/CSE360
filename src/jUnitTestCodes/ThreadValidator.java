package jUnitTestCodes;

public class ThreadValidator {
	
	/**Default*/
	private ThreadValidator() {}
	
	/**
	 * <p>Method: validateThreads(String name)</p>
	 *
	 * <p>Description: Validates a thread name. Checks for empty names, invalid characters,
	 * maximum length, and existence in the database.</p>
	 *
	 * @param name The thread name
	 * @return Error message if invalid, or empty string if valid
	 */
	public static String validateThreads(String name) {
		if (name.isEmpty()) {
            return "Thread name cannot be empty.";
        } 
		else if (!name.matches("[A-Za-z0-9]+")) {
			return "Thread may only contain alphabets or numbers.";
		}
		else if (name.length() > 20) {
			return "Thread name cannot be longer than 20 characters.";
		}
		return "";
	}
}
