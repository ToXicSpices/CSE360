package guiMakePost;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import entityClasses.User;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import entityClasses.Post;

/**
 * <p> Title: ControllerMakePost Class</p>
 * <p> Description: this is the controller class of guiMakePost, 
 * which will perform actions due to user's interaction on ViewMakePost. </p>
 */
public class ControllerMakePost {
	
	private static Optional<String> result;
	private static String title = "";
	private static String subtitle = "";
	private static String content = "";	
	protected static ArrayList<String> tags = new ArrayList<String>();
	/**Default Constructor, not used.*/
	private ControllerMakePost() {}
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	/**
	 * <p> Method: void setTitle() </p>
	 * <p> Description: Sets the post title from the UI text field.
	 * Retrieves text from ViewMakePost.text_Title and stores it in the title field.</p>
	 */
	protected static void setTitle() {
		title = ViewMakePost.text_Title.getText();
	}
	
	/**
	 * <p> Method: void setSubtitle() </p>
	 * <p> Description: Sets the post subtitle from the UI text field.
	 * Retrieves text from ViewMakePost.text_Subtitle and stores it in the subtitle field. </p>
	 */
	protected static void setSubtitle() {
		subtitle = ViewMakePost.text_Subtitle.getText();
	}
	
	/**
	 * <p> Method: void setContent() </p>
	 * <p> Description: Sets the post content from the UI text area.
	 * Retrieves text from ViewMakePost.text_Content and stores it in the content field. </p>
	 */
	protected static void setContent() {
		content = ViewMakePost.text_Content.getText();
	}
	
	/**
	 * <p> Method: void doAddTag() </p>
	 * <p> Description: Handles adding a new tag to the post.
	 * Shows a dialog for tag input, validates the tag, adds it to the tags list (max 3),
	 * and updates the UI display. Also refreshes the remove tag dialog options. </p>
	 */
	protected static void doAddTag() {
		result = ViewMakePost.dialogAddTags.showAndWait();
		result.ifPresent(tag -> {	
			if (tags.size() >= 3) {
				ViewMakePost.alertAddTagError.setHeaderText("Too many tags!");
				ViewMakePost.alertAddTagError.setContentText("No more than 3 tags");
				ViewMakePost.alertAddTagError.showAndWait();
				return;
			}
			String tagError = ModelMakePost.checkValidTag(tag);
			if (!tagError.isEmpty()) {
				ViewMakePost.alertAddTagError.setHeaderText(tagError);
				ViewMakePost.alertAddTagError.showAndWait();
				return;
			}
			tags.add(tag.toLowerCase());
			String labelTags = "";
			for (int i = 0; i < tags.size(); i++) {
				labelTags += tags.get(i);
				if (i < tags.size()-1) {
					labelTags += " | ";
				}
			}
			if (labelTags.length() > 40) {
				labelTags = labelTags.substring(0, 40);
				labelTags += "...";
			}
			ViewMakePost.label_TagsContent.setText(labelTags);
			ViewMakePost.dialogRemoveTag = new ChoiceDialog<String>(tags.get(0), tags);
			ViewMakePost.dialogRemoveTag.setTitle("Remove Tag");
			ViewMakePost.dialogRemoveTag.setHeaderText("Choose a tag to remove");
			ViewMakePost.dialogAddTags = new TextInputDialog("");
			ViewMakePost.dialogAddTags.setTitle("Add Tag");
			ViewMakePost.dialogAddTags.setHeaderText("Add a New Tag");
		});
	}
	
	/**
	 * <p> Method: void doRemoveTag() </p>
	 * <p> Description: Removes a selected tag from the post.
	 * Displays a choice dialog to select a tag to remove.
	 * Updates the tags list and UI label. </p>
	 */
	protected static void doRemoveTag() {
		result = ViewMakePost.dialogRemoveTag.showAndWait();
		result.ifPresent(tag -> {
			for (int i = 0; i < tags.size(); i++) {
				if (tags.get(i).compareTo(tag) == 0) {
					tags.remove(i);
					break;
				}
			}
			String labelTags = "";
			for (int i = 0; i < tags.size(); i++) {
				labelTags += tags.get(i);
				if (i < tags.size()-1) {
					labelTags += " | ";
				}
			}
			if (labelTags.length() > 40) {
				labelTags = labelTags.substring(0, 40);
				labelTags += "...";
			}
			ViewMakePost.label_TagsContent.setText(labelTags);
			if (tags.size() > 0) ViewMakePost.dialogRemoveTag = new ChoiceDialog<String>(tags.get(0), tags);
			else ViewMakePost.dialogRemoveTag = new ChoiceDialog<String>("<none>", tags);
			ViewMakePost.dialogRemoveTag.setTitle("Remove Tag");
			ViewMakePost.dialogRemoveTag.setHeaderText("Choose a tag to remove");
		});
     
	}
	
	/**
	 * <p> Method: void doRemoveTag() </p>
	 * <p> Description: Creates and submits a new post to the database.
	 * Validates title, subtitle, and content fields. If validation passes,
	 * creates a Post object and submits it to the database.
	 * On success, navigates back to the user's home page. </p>
	 */
	protected static void doMakePost() {
		String titleError = ModelMakePost.checkValidTitle(title);
		if (!titleError.isEmpty()) {
			ViewMakePost.alertTitleError.setHeaderText(titleError);
			ViewMakePost.alertTitleError.showAndWait();
			return;
		}
		else if (ViewMakePost.theDatabase.doesPostExistByTitle(title)) {
			ViewMakePost.alertTitleError.setHeaderText("Exists post with the same Title!");
			ViewMakePost.alertTitleError.showAndWait();
			return;
		}
		String subtitleError = ModelMakePost.checkValidSubtitle(subtitle);
		if (!subtitleError.isEmpty()) {
			ViewMakePost.alertSubtitleError.setHeaderText(subtitleError);
			ViewMakePost.alertSubtitleError.showAndWait();
			return;
		}
		String contentError = ModelMakePost.checkValidContent(content);
		if (!contentError.isEmpty()) {
			ViewMakePost.alertContentError.setHeaderText(contentError);
			ViewMakePost.alertContentError.showAndWait();
			return;
		}
		Post post = new Post(title, subtitle, content, ViewMakePost.theUser.getUserName(), 
				tags, ViewMakePost.combobox_SelectThread.getValue());
		
		try {
        	int genPostId = ViewMakePost.theDatabase.makePost(post);
        	post.setPostId(genPostId);
        	
        	goToUserHomePage(ViewMakePost.theStage, ViewMakePost.theUser);
        	}
        catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to make a post: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		ViewMakePost.theDatabase.markPostAsRead(ViewMakePost.theUser.getUserName(), post.getPostId());
		ViewMakePost.text_Title.setText("");
		ViewMakePost.text_Subtitle.setText("");
		ViewMakePost.text_Content.setText("");
		ViewMakePost.label_TagsContent.setText("");
		ViewMakePost.combobox_SelectThread.getSelectionModel().selectFirst();
		tags = new ArrayList<String>();
	}
	
	/**
	 * <p> Method: void goToUserHomePage(Stage theStage, User theUser) </p>
	 * <p> Description: Navigates to the appropriate user home page based on their role.
	 * Uses the active role from FoundationsMain to determine which home page to display:
	 * 1 = Admin Home, 2 = Student Home, 3 = Staff Home. </p>
	 * 
	 * @param theStage The JavaFX stage to display the new scene
	 * @param theUser The current user object
	 */
	protected static void goToUserHomePage(Stage theStage, User theUser) {
		
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiStudentHome.ViewStudentHome.displayStudentHome(theStage, theUser);
			break;
		case 3:
			guiStaffHome.ViewStaffHome.displayStaffHome(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + 
					theRole);
			System.exit(0);
		}
 	}
	
	/**
	 * <p> Method: void performQuit() </p>
	 * <p> Description: exit the program with exit code 0 </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}