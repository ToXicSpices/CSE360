package guiPostPage;
import entityClasses.User;
import entityClasses.Reply;
import entityClasses.StudentStatus;
import javafx.stage.Stage;
import java.util.Optional;
import java.sql.SQLException;

/**
 * <p> Title: ControllerPostPage Class</p>
 * <p> Description: this is the controller class of guiPostPage, 
 * which will perform actions due to user's interaction on ViewPostPage. </p>
 */
public class ControllerPostPage {
	
    /**Default constructor, not used*/
	private ControllerPostPage() {}
		
	/*-*******************************************************************************************
	User Interface Actions for this page
	
	**********************************************************************************************/
	
	/**
	 * <p> Method: void doReply() </p>
	 * <p> Description: Handles the reply submission process with validation and 
	 * database persistence. Validates reply content for length requirements, creates 
	 * a new Reply object, saves it to the database, and updates the UI by clearing 
	 * the reply editor and refreshing the replies display.</p>
	 */
	 protected static void doReply() {
	        // Read current content directly
	        String content = ViewPostPage.text_reply.getText();

	        // Validation with user feedback
	        String error = ModelPostPage.validateReply(content);
	        if (!error.isEmpty()) ViewPostPage.showAlert(null, error);

	        Reply reply = new Reply(content, ViewPostPage.theUser.getUserName(), ViewPostPage.thePost.getPostId());

	        try {
	            int genReplyId = ViewPostPage.theDatabase.makeReply(reply);
	            reply.setReplyId(genReplyId);
	            ViewPostPage.theDatabase.markRepliesAsRead(ViewPostPage.theUser.getUserName(), ViewPostPage.thePost.getPostId());
	            StudentStatus status = ViewPostPage.theDatabase.getStudentStatus(ViewPostPage.theUser.getUserName());
	            status.setReplyNumber(status.getReplyNumber()+1);
	            ViewPostPage.theDatabase.updateStudentStatus(status);
	            System.out.println("Successfully sent");
	        } catch (SQLException e) {
	            System.err.println("*** ERROR *** Database error trying to make a reply: " + e.getMessage());
	            e.printStackTrace();
	            ViewPostPage.showAlert("Database Error", "Unable to send reply. Please try again.");
	            return;
	        }

	        // Clear the text area and restore buttons/controls safely
	        ViewPostPage.text_reply.setText("");

	        // Remove the reply editor if present and restore the Reply button if missing
	        if (ViewPostPage.box_PostArea.getChildren().contains(ViewPostPage.text_reply)) {
	            ViewPostPage.box_PostArea.getChildren().removeAll(ViewPostPage.text_reply);
	        }
	        if (ViewPostPage.box_PostArea.getChildren().contains(ViewPostPage.box_SendCancel)) {
	            ViewPostPage.box_PostArea.getChildren().removeAll(ViewPostPage.box_SendCancel);
	        }

	        if (!ViewPostPage.box_PostArea.getChildren().contains(ViewPostPage.box_ReplyUnread)) {
	            // Put the reply button back near where it originally was; if we can't find the 'post content'
	            // insertion index, just add it before repliesBox (if present) to keep UI stable.
	            int repliesIndex = ViewPostPage.box_PostArea.getChildren().indexOf(ViewPostPage.repliesBox);
	            if (repliesIndex >= 0) {
	                ViewPostPage.box_PostArea.getChildren().add(repliesIndex, ViewPostPage.box_ReplyUnread);
	            } else {
	                // fallback: append near the end before replies
	                ViewPostPage.box_PostArea.getChildren().add(ViewPostPage.box_ReplyUnread);
	            }
	        }

	        // Refresh reply list from DB and update UI
	        ViewPostPage.refreshReplies();
	    }

	  	/**
	  	 * <p> Method: void doCancel() </p>
	  	 * <p> Description: Cancels the reply process by clearing the reply editor and restoring the Reply button.
	     * Ensures that the UI is updated correctly by removing the reply text area and
	     * send/cancel buttons, and re-adding the Reply button if it is not already present. </p>
	     */
	    protected static void doCancel() {
	        ViewPostPage.text_reply.setText("");
	        // Remove reply editor and restore Reply button safely
	        ViewPostPage.box_PostArea.getChildren().removeAll(ViewPostPage.text_reply, ViewPostPage.box_SendCancel);
	        if (!ViewPostPage.box_PostArea.getChildren().contains(ViewPostPage.box_ReplyUnread)) {
	            int repliesIndex = ViewPostPage.box_PostArea.getChildren().indexOf(ViewPostPage.repliesBox);
	            if (repliesIndex >= 0) {
	                ViewPostPage.box_PostArea.getChildren().add(repliesIndex, ViewPostPage.box_ReplyUnread);
	            } else {
	                ViewPostPage.box_PostArea.getChildren().add(ViewPostPage.box_ReplyUnread);
	            }
	        }
	    }
	    
	    /*****
	     * <p> Method: void goToUserHomePage(theStage, theUser) </p>
	     * 
	     * <p> Description: This method uses the role selected during login to proceed to that role's home page. </p>
	     * 
	     * @param theStage - The stage to display the home page on
	     * @param theUser  - The user who is logged in
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
	    
	    /*****
	     * <p> Method: void updatePost() </p>
	     * 
	     * <p> Description: This method allows the user to update the content of the post.
	     * It opens a text input dialog pre-filled with the current post content,
	     * validates the new content, updates it in the database, and refreshes the display. </p>
	     */
		protected static void updatePost() {
			// Create a simple text input dialog for updating post content
			javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(ViewPostPage.thePost.getContent());
			dialog.setTitle("Update Post");
			dialog.setHeaderText("Update Post Content");
			dialog.setContentText("Content:");
			dialog.getEditor().setMinHeight(100);

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent() && !result.get().trim().isEmpty()) {
				String newContent = result.get().trim();
				if (newContent.length() >= 10 && newContent.length() <= 2200) {
					try {
						ViewPostPage.theDatabase.updatePostContent(ViewPostPage.thePost.getPostId(), newContent);
						ViewPostPage.refreshPostContent();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					ViewPostPage.showAlert("Invalid Content", "Post content must be between 10 and 2200 characters.");
				}
			}
		}
	    
	    /*****
	     * <p> Method: editReply(reply) </p>
	     * 
	     * <p> Description: This method allows the user to edit the content of a reply.
	     * It opens a text input dialog pre-filled with the current reply content,
	     * validates the new content, updates it in the database, and refreshes the display. </p>
	     * 
	     * @param reply - The reply object to be edited
	     */
		protected static void editReply(Reply reply) {
			javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(reply.getContent());
			dialog.setTitle("Edit Reply");
			dialog.setHeaderText("Edit Reply Content");
			dialog.setContentText("Content:");
			dialog.getEditor().setMinHeight(80);

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent() && !result.get().trim().isEmpty()) {
				String newContent = result.get().trim();
				if (newContent.length() <= 1000) {
					try {
						ViewPostPage.theDatabase.updateReplyContent(reply.getReplyId(), newContent);
						ViewPostPage.refreshReplies();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					ViewPostPage.showAlert("Invalid Content", "Reply content must be between 1 and 1000 characters.");
				}
			}
		}
	    
		/**
		 * <p> Method: void deleteReply(Reply reply) </p>
		 * <p> Description: displays a confirmation dialog and deletes the specified reply if confirmed.
		 * Shows a confirmation alert to prevent accidental deletions, and if the user
		 * confirms, removes the reply from the database and refreshes the replies display.</p>
		 *
		 * @param reply The Reply object to be deleted
		 */
	    protected static void deleteReply(Reply reply) {
	    	ViewPostPage.theDatabase.deleteReply(reply.getReplyId());
            StudentStatus status = ViewPostPage.theDatabase.getStudentStatus(ViewPostPage.theUser.getUserName());
            status.setReplyNumber(status.getReplyNumber()-1);
            ViewPostPage.theDatabase.updateStudentStatus(status);
            ViewPostPage.refreshReplies();
	    }
	    
	    /**
	     * <p> Method: void performLogout() </p>
	     * <p> Description: Logs out the current user and returns to the login screen.</p>
	     *
	     */	    
	    protected static void performLogout() {
	        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPostPage.theStage);
	    }
	    
	    /**
	     * <p> Method: void performLogout() </p>
	     * <p> Description: Terminates the application.</p>
	     */
	    protected static void performQuit() {
	       System.exit(0);
	    }

}