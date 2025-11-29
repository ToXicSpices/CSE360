package jUnitTestCodes;

public class MessageValidator {

	/**Default*/
	private MessageValidator() {}
	
	/**
	 * <p>Method: validateMessage(String receiver, String subject, String content)</p>
	 *
	 * <p>Description: Validates message fields. Checks for empty fields, maximum lengths,
	 * and whether the receiver exists in the database.
	 * From ModelStudentHome/ModelStaffHome</p>
	 *
	 * @param receiver The recipient's username
	 * @param subject The message subject
	 * @param content The message content
	 * @return Error message if invalid, or empty string if valid
	 */
	public static String validateMessage(String receiver, String subject, String content) {
		if (receiver.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            return "All fields are required.";
        }
		if (subject.length() > 100) {
			return "Subject cannot exceed 100 characters.";
		}
		if (content.length() > 2200) {
			return "Content cannot exceed 2200 characters.";
		}
		return "";
	}
}
