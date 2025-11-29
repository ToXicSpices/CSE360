package guiStaffHome;

import java.util.ArrayList;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.scene.control.Alert;

public class ControllerStaffHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}	

	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
	
	// Post CRUD Actions
	
	/**
	 * Load all posts from the database
	 */
	protected static void loadAllPosts() {
		ArrayList<Post> posts = ModelStaffHome.getAllPosts();
		ViewStaffHome.displayPosts(posts);
	}
	
	/**
	 * Search posts by title
	 */
	protected static void performSearch() {
		String searchText = ViewStaffHome.text_SearchPost.getText();
		
		if (searchText == null || searchText.trim().isEmpty()) {
			showError("Please enter a search term");
			return;
		}
		
		ArrayList<Post> searchResults = ModelStaffHome.searchPostsByTitle(searchText);
		ViewStaffHome.displayPosts(searchResults);
		
		if (searchResults.isEmpty()) {
			showError("No posts found matching: " + searchText);
		}
	}
	
	/**
	 * Clear search and reload all posts
	 */
	protected static void performClearSearch() {
		ViewStaffHome.text_SearchPost.clear();
		loadAllPosts();
	}
	
	/**
	 * View a specific post and its replies
	 */
	protected static void viewPost(int postId) {
		Post post = ModelStaffHome.getPostById(postId);
		ArrayList<Reply> replies = ModelStaffHome.getRepliesForPost(postId);
		ViewStaffHome.displayPostDetails(post, replies);
	}
	
	/**
	 * Create a new post
	 */
	protected static void createNewPost(String title, String subtitle, String content, 
			String owner, ArrayList<String> tags, String thread) {
		
		// Validate required fields
		if (title == null || title.trim().isEmpty()) {
			showError("Title is required");
			return;
		}
		
		if (content == null || content.trim().isEmpty()) {
			showError("Content is required");
			return;
		}
		
		// Check if post title already exists
		if (ModelStaffHome.doesPostExist(title)) {
			showError("A post with this title already exists");
			return;
		}
		
		// Create the post
		Post newPost = new Post(title, subtitle, content, owner, tags, thread);
		int postId = ModelStaffHome.createPost(newPost);
		
		if (postId > 0) {
			showSuccess("Post created successfully!");
			loadAllPosts(); // Refresh the list
		} else {
			showError("Failed to create post");
		}
	}
	
	/**
	 * Update an existing post
	 */
	protected static void updatePost(int postId, String title, String subtitle, String content) {
		if (title != null && !title.trim().isEmpty()) {
			ModelStaffHome.updatePostTitle(postId, title);
		}
		
		if (subtitle != null) {
			ModelStaffHome.updatePostSubtitle(postId, subtitle);
		}
		
		if (content != null && !content.trim().isEmpty()) {
			ModelStaffHome.updatePostContent(postId, content);
		}
		
		showSuccess("Post updated successfully!");
		loadAllPosts(); // Refresh the list
	}
	
	/**
	 * Delete a post
	 */
	protected static void deletePost(int postId) {
		ModelStaffHome.deletePost(postId);
		showSuccess("Post deleted successfully!");
		loadAllPosts(); // Refresh the list
	}
	
	// Reply CRUD Actions
	
	/**
	 * Create a new reply to a post
	 */
	protected static void createReply(int postId, String content, String owner) {
		if (content == null || content.trim().isEmpty()) {
			showError("Reply content is required");
			return;
		}
		
		Reply newReply = new Reply(content, owner, postId);
		int replyId = ModelStaffHome.createReply(newReply);
		
		if (replyId > 0) {
			showSuccess("Reply created successfully!");
			viewPost(postId); // Refresh the post view
		} else {
			showError("Failed to create reply");
		}
	}
	
	/**
	 * Update an existing reply
	 */
	protected static void updateReply(int replyId, int postId, String content) {
		if (content == null || content.trim().isEmpty()) {
			showError("Reply content is required");
			return;
		}
		
		ModelStaffHome.updateReplyContent(replyId, content);
		showSuccess("Reply updated successfully!");
		viewPost(postId); // Refresh the post view
	}
	
	/**
	 * Delete a reply
	 */
	protected static void deleteReply(int replyId, int postId) {
		ModelStaffHome.deleteReply(replyId);
		showSuccess("Reply deleted successfully!");
		viewPost(postId); // Refresh the post view
	}
	
	// Helper methods for user feedback
	
	private static void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private static void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Grading workflows
	protected static void gradePost(int postId, String grade, String feedback, String gradedBy) {
		if (grade == null || grade.trim().isEmpty()) { showError("Grade is required"); return; }
		ModelStaffHome.gradePost(postId, grade.trim(), feedback == null ? "" : feedback.trim(), gradedBy);
		showSuccess("Grade saved");
		loadAllPosts();
	}

	protected static void performReleaseGrades() {
		ModelStaffHome.releaseAllGrades();
		showSuccess("All graded posts released to students.");
		loadAllPosts();
	}

	protected static void performViewAllGrades() {
		ArrayList<Post> graded = ModelStaffHome.getAllGradedPosts();
		if (graded.isEmpty()) { showError("No graded posts yet"); return; }
		StringBuilder sb = new StringBuilder();
		for (Post p : graded) {
			sb.append(p.getTitle()).append(" | Grade: ")
			  .append(p.getGrade()==null?"(none)":p.getGrade())
			  .append(p.isGradeReleased()?" (Released)":" (Hidden)")
			  .append("\nFeedback: ")
			  .append(p.getFeedback()==null?"":p.getFeedback())
			  .append("\n---\n");
		}
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("All Graded Posts");
		alert.setHeaderText("Graded Posts Summary");
		alert.setContentText(sb.toString());
		alert.showAndWait();
	}

}
