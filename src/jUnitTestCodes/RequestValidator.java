package jUnitTestCodes;

public class RequestValidator {
	/**Default*/
	private RequestValidator() {}
	/**
	 * <p>Method: validateRequests(String title, String content)</p>
	 *
	 * <p>Description: Validates a request's title and content. Checks for empty fields and maximum length limits.</p>
	 *
	 * @param title The request title
	 * @param content The request content
	 * @return Error message if invalid, or empty string if valid
	 */
	public static String validateRequests(String title, String content) {
		if (title.isEmpty()) {
            return "Title cannot be empty.";
        }
		else if (title.length() >= 80) {
			return "Title cannot be longer than 80 characters";
		}
		else if (content.isEmpty()) {
			return "Content cannot be empty.";
		}
		else if (content.length() >= 2200) {
			return "Content cannot be longer than 2200 characters";
		}
		return "";
	}
}
