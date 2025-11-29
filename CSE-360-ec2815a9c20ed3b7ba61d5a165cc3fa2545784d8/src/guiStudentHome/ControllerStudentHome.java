package guiStudentHome;

import java.util.ArrayList;
import entityClasses.Post;

/**
 * <p> Title: ControllerStudentHome Class</p>
 * <p> Description: this is the controller class of guiStudentHome, 
 * which will perform actions due to user's interaction on ViewStudentHome. </p>
 */
public class ControllerStudentHome {
	
	/**Default constructor, not used*/
	private ControllerStudentHome() {}
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**********
	 * <p> Method: void performViewMyPosts() </p>
	 * 
	 * <p> Description: This method redirects to guiMyPosts page. </p>
	 * 
	 */
	protected static void performViewMyPosts() {
		guiMyPosts.ViewMyPosts.displayMyPosts(ViewStudentHome.theStage, ViewStudentHome.theUser);
	}
	
	/**********
	 * <p> Method: void performMakePost() </p>
	 * 
	 * <p> Description: This method redirects to guiMakePost page. </p>
	 * 
	 */
	protected static void performMakePost() {
		guiMakePost.ViewMakePost.displayMakePost(ViewStudentHome.theStage, ViewStudentHome.theUser);
	}
	
	/**********
	 * <p> Method: void performSearch() </p>
	 * 
	 * <p> Description: This method changes the post list in ViewStudentHome filtered by keyword. </p>
	 * 
	 * @return the list of posts
	 */
	protected static ArrayList<Post> performSearch(ArrayList<Post> postList) {
        String term = ViewStudentHome.text_SearchPost.getText().trim();
        if (term.isEmpty()) {
            return postList;
        } else {
        	ArrayList<Post> resultList = new ArrayList<>();
    		for (Post post : postList) {
    			if (post.getTitle().toLowerCase().contains(term.toLowerCase())) {	// ignore cases
    				resultList.add(post);
    			}
    		}
    		return resultList;
        }
    }
	
	/**********
	 * <p> Method: void performClear() </p>
	 * 
	 * <p> Description: This method clears the post search. </p>
	 * 
	 */
	protected static void performClear() {
		ViewStudentHome.text_SearchPost.setText("");
		performAllFilters();
	}
	
	/**********
	 * <p> Method: ArrayList<Post> performThreadFilter() </p>
	 * 
	 * <p> Description: This method will return the post list under specific thread. </p>
	 * 
	 * @return the list of posts
	 */
	protected static ArrayList<Post> performThreadFilter(ArrayList<Post> postList) {
		try {
			if (ViewStudentHome.combobox_Thread.getValue().compareTo("All") == 0) {
				return postList;
			}
				
			else {
				ArrayList<Post> resultList = new ArrayList<>();
	    		for (Post post : postList) {
	    			if (post.getThread().compareTo(ViewStudentHome.combobox_Thread.getValue()) == 0) {
	    				resultList.add(post);
	    			}
	    		}
	    		return resultList;
			}
		} catch (NullPointerException e) {
			return postList;
		}
	}
	
	/**********
	 * <p> Method: ArrayList<Post> performUnreadFilter() </p>
	 * 
	 * <p> Description: This method will return the post list unread by user. </p>
	 * 
	 * @return the list of posts
	 */
	protected static ArrayList<Post> performUnreadFilter(ArrayList<Post> postList) {
		if (ViewStudentHome.checkBox_showUnreadPosts.isSelected()) {
			ArrayList<Post> resultList = new ArrayList<>();
    		for (Post post : postList) {
    			if (ViewStudentHome.theDatabase.isPostUnread(ViewStudentHome.theUser.getUserName(), post.getPostId())) {
    				resultList.add(post);
    			}
    		}
    		return resultList;
        } else {
        	return postList;
        }
	}
	
	/**********
	 * <p> Method: performAllFilters() </p>
	 * 
	 * <p> Description: This method will combine all filters for search, thread and unread. </p>
	 * 
	 */
	protected static void performAllFilters() {
		ArrayList<Post> resultList = ViewStudentHome.theDatabase.getAllPostsNewestFirst();
		resultList = performUnreadFilter(resultList);
		resultList = performThreadFilter(resultList);
		resultList = performSearch(resultList);
		ViewStudentHome.posts = resultList;
		ViewStudentHome.refreshPostsDisplay();
	}
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}

	/**********
	 * <p> Method: void performViewMyGrades() </p>
	 * 
	 * <p> Description: Shows a dialog listing this student's released grades and feedback. </p>
	 */
	protected static void performViewMyGrades() {
		java.util.ArrayList<entityClasses.Post> graded = ViewStudentHome.theDatabase.getReleasedGradesForUser(ViewStudentHome.theUser.getUserName());
		javafx.scene.control.Alert alert;
		if (graded.isEmpty()) {
			alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
			alert.setTitle("Grades");
			alert.setHeaderText("No released grades yet");
			alert.setContentText("Your posts have not been graded or released.");
			alert.showAndWait();
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (entityClasses.Post p : graded) {
			sb.append(p.getTitle()).append(" | Grade: ")
			  .append(p.getGrade())
			  .append("\nFeedback: ")
			  .append(p.getFeedback()==null?"":p.getFeedback())
			  .append("\n---\n");
		}
		alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle("My Grades");
		alert.setHeaderText("Released Grades");
		alert.setContentText(sb.toString());
		alert.showAndWait();
	}
}