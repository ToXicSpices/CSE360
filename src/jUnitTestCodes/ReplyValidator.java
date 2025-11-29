package jUnitTestCodes;

public class ReplyValidator {

	/**Default not used*/
	private ReplyValidator() {}
	
	/**
	 * <p> Method: String validateReply(String content) </p>
	 * <p> Description: Validates the content of a reply before submission. Checks if 
	 * the reply is empty or exceeds 1000 characters. If invalid, displays an alert 
	 * to the user and returns a corresponding error message. Returns an empty string 
	 * if the reply content is valid.</p>
	 *
	 * @param content The text content of the reply to validate.
	 * @return An error message if invalid, or an empty string if valid.
	 */
	public static String validateReply(String content) {
		if (content.isEmpty()) {
            return "Please type something before sending.";
        }
        if (content.length() > 1000) {
            return "Replies cannot exceed 1000 characters.";
        }
        return "";
	}
}
