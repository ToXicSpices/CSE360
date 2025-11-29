package guiMakePost;

/*******
 * <p> Title: ModelMakePost Class. </p>
 * 
 * <p> Description: The MakePost Page Model.</p>
 *  
 */
public class ModelMakePost {
	/**Default Constructor, not used.*/
	private ModelMakePost() {};
	
	/**
	 * <p> Method: String checkValidTitle(String title) </p>
	 * 
	 * <p> Description: input validation of title.
	 * maximum length: 120 characters,
	 * minimum length: 5 characters,
	 * title not existed
	 * allow alphabets, numbers and special characters only. </p>
	 * 
	 * @param title is the title to be validate
	 * 
	 * @return the error message, empty if no error
	 * 
	 */
	protected static String checkValidTitle(String title) {
		String specialChar = "!#$%&'*+-/=?^_`{|}~ (),:;<>@[\\].\"";
		if (title.length() < 5) {
			return "Too short! At least 5 characters.";
		}
		else if (title.length() > 120) {
			return "Too long! No more than 120 characters.";
		}
		else {
			for (int i = 0; i < title.length(); i++) {
				if (!((title.charAt(i) >= 'A' && title.charAt(i) <= 'Z' ) ||
						(title.charAt(i) >= 'a' && title.charAt(i) <= 'z' ) ||
						(title.charAt(i) >= '0' && title.charAt(i) <= '9' ) ||
						(specialChar.indexOf(title.charAt(i)) >= 0))) {
					return "Invalid Input";
				}
			}
		}
		return "";
	}
	
	/**
	 * <p> Method: String checkValidSubtitle(String subtitle) </p>
	 * 
	 * <p> Description: input validation of subtitle.
	 * maximum length: 80 characters,
	 * maximum lines: 1 line,
	 * allow any character. </p>
	 * 
	 * @param subtitle is the subtitle to be validate
	 * 
	 * @return the error message, empty if no error
	 * 
	 */
	protected static String checkValidSubtitle(String subtitle) {
		if (subtitle.length() > 80) {
			return "Too long! No more than 80 characters";
		}
		else {
			for (int i = 0; i < subtitle.length(); i++) {
				if (subtitle.charAt(i) == '\n') {
					return "No more than 1 line!";
				}
			}
		}
		return "";
	}
	
	/**
	 * <p> Method: String checkValidContent(String content) </p>
	 * 
	 * <p> Description: input validation of content.
	 * maximum length: 2200 characters,
	 * minimum length: 10 characters,
	 * allow any character. </p>
	 * 
	 * @param content is the content to be validate
	 * 
	 * @return the error message, empty if no error
	 * 
	 */
	protected static String checkValidContent(String content) {
		if (content.length() < 10) {
			return "Too short! At least 10 characters.";
		}
		else if (content.length() > 2200) {
			return "Too long! No more than 2200 characters.";
		}
		return "";
	}
	
	/**
	 * <p> Method: String checkValidTag(String tag) </p>
	 * 
	 * <p> Description: input validation of tag.
	 * maximum length: 15 characters,
	 * minimum length: 2 characters,
	 * allow alphabets and numbers. </p>
	 * 
	 * @param tag is the tag to be validate
	 * 
	 * @return the error message, empty if no error
	 * 
	 */
	protected static String checkValidTag(String tag) {
		if (tag.length() < 2) {
			return "Too short! At least 2 characters.";
		}
		else if (tag.length() > 15) {
			return "Too long! No more than 15 characters";
		}
		else {
			for (int i = 0; i < tag.length(); i++) {
				if (!((tag.charAt(i) >= 'A' && tag.charAt(i) <= 'Z' ) ||
						(tag.charAt(i) >= 'a' && tag.charAt(i) <= 'z' ))) {
					return "Invalid Input";
				}
			}
		}
		return "";
	}
	
}