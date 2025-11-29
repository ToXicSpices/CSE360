package guiPostPage;

/**
 * <p> Title: ModelPostPage Class. </p>
 * 
 * <p> Description: The Post Page Model. This class is not used as there is no
 * data manipulated by this MVC beyond accepting role information and saving it in the
 * database.</p>
 *  
 */
public class ModelPostPage {

	/**Default constructor, not used*/
	private ModelPostPage() {}
	
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
	protected static String validateReply(String content) {
		if (content.isEmpty()) {
            ViewPostPage.showAlert("Empty reply", "Please type something before sending.");
            return "Please type something before sending.";
        }
        if (content.length() > 1000) {
            ViewPostPage.showAlert("Reply too long", "Replies cannot exceed 1000 characters.");
            return "Replies cannot exceed 1000 characters.";
        }
        return "";
	}
}