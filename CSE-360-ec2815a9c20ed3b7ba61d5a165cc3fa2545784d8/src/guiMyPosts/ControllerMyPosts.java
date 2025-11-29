package guiMyPosts;

import entityClasses.User;
import entityClasses.Post;
import javafx.stage.Stage;

/**
 * <p> Title: ControllerMyPosts Class</p>
 * <p> Description: this is the controller class of guiMyPosts, 
 * which will perform actions due to user's interaction on ViewMyPosts. </p>
 */
public class ControllerMyPosts {
	
	/**Default constructor, not used*/
	private ControllerMyPosts() {}
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	/**
     * <p> Method: void performDeletePost(Post post) </p>
     * 
     * <p> Description: Displays a confirmation alert and, if the user selects YES, 
     * deletes the specified post from the database using the existing deletePost() method.
     * After deletion, refreshes the displayed posts list to ensure the UI immediately 
     * reflects the removal. Replies to the deleted post will remain visible, but will 
     * indicate that the original post no longer exists. </p>
     * 
     * @param post The Post object that the user selected to delete
     */
	protected static void performDeletePost(Post post) {
        // Create confirmation dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        alert.setHeaderText("Are you sure to delete this post?");
        alert.setContentText("Title: " + post.getTitle() + "\n\nContents of this post will be permanently deleted!\nReplies to this post will remain visible but will show that the original post was deleted.");

        alert.getButtonTypes().setAll(
            javafx.scene.control.ButtonType.YES,
            javafx.scene.control.ButtonType.NO
        );

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.YES) {
            // Delete the post using the same method as ViewMyPosts
            ViewMyPosts.theDatabase.deletePost(post.getPostId());
            
            // Refresh the posts list to reflect the deletion
            ViewMyPosts.posts = ViewMyPosts.theDatabase.getPostsFromUserNewestFirst(ViewMyPosts.theUser.getUserName());
            ViewMyPosts.refreshPostsDisplay();
        }
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
	 * <p> Method: void performLogout() </p>
	 * <p> Description: logout and redirect to user login page </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewMyPosts.theStage);
	}
	
	/**
	 * <p> Method: void performQuit() </p>
	 * <p> Description: exit the program with exit code 0 </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}

}