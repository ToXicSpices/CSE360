package guiStaffHome;

import java.sql.SQLException;
import java.util.ArrayList;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ModelStaffHome Class. </p>
 * 
 * <p> Description: The Staff Home Page Model. This class handles all data operations
 * related to viewing, creating, updating, and deleting posts and replies.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-11-28 Updated with CRUD operations for posts and replies
 *  
 */

public class ModelStaffHome {
	
	private static Database database = applicationMain.FoundationsMain.database;
	
	// Post CRUD Operations
	
	/**
	 * Get all posts from the database (newest first)
	 * @return ArrayList of all posts
	 */
	public static ArrayList<Post> getAllPosts() {
		return database.getAllPostsNewestFirst();
	}
	
	/**
	 * Search posts by title
	 * @param searchText The text to search for in post titles
	 * @return ArrayList of posts matching the search criteria
	 */
	public static ArrayList<Post> searchPostsByTitle(String searchText) {
		return database.searchPostsByTitle(searchText);
	}
	
	/**
	 * Get a specific post by ID
	 * @param postId The ID of the post
	 * @return The Post object
	 */
	public static Post getPostById(int postId) {
		return database.getPostById(postId);
	}
	
	/**
	 * Create a new post
	 * @param post The post to create
	 * @return The generated post ID, or -1 if failed
	 */
	public static int createPost(Post post) {
		try {
			return database.makePost(post);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Update a post's title
	 * @param postId The ID of the post
	 * @param title The new title
	 */
	public static void updatePostTitle(int postId, String title) {
		database.updatePostTitle(postId, title);
	}
	
	/**
	 * Update a post's subtitle
	 * @param postId The ID of the post
	 * @param subtitle The new subtitle
	 */
	public static void updatePostSubtitle(int postId, String subtitle) {
		database.updatePostSubitle(postId, subtitle);
	}
	
	/**
	 * Update a post's content
	 * @param postId The ID of the post
	 * @param content The new content
	 */
	public static void updatePostContent(int postId, String content) {
		database.updatePostContent(postId, content);
	}
	
	/**
	 * Delete a post
	 * @param postId The ID of the post to delete
	 */
	public static void deletePost(int postId) {
		database.deletePost(postId);
	}
	
	// Reply CRUD Operations
	
	/**
	 * Get all replies for a specific post
	 * @param postId The ID of the post
	 * @return ArrayList of replies
	 */
	public static ArrayList<Reply> getRepliesForPost(int postId) {
		return database.getRepliesByPostId(postId);
	}
	
	/**
	 * Create a new reply
	 * @param reply The reply to create
	 * @return The generated reply ID, or -1 if failed
	 */
	public static int createReply(Reply reply) {
		try {
			return database.makeReply(reply);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Update a reply's content
	 * @param replyId The ID of the reply
	 * @param content The new content
	 */
	public static void updateReplyContent(int replyId, String content) {
		database.updateReplyContent(replyId, content);
	}
	
	/**
	 * Delete a reply
	 * @param replyId The ID of the reply to delete
	 */
	public static void deleteReply(int replyId) {
		database.deleteReply(replyId);
	}
	
	/**
	 * Check if a post title already exists
	 * @param title The title to check
	 * @return true if exists, false otherwise
	 */
	public static boolean doesPostExist(String title) {
		return database.doesPostExistByTitle(title);
	}

	// Grading operations
	public static void gradePost(int postId, String grade, String feedback, String gradedBy) {
		database.setPostGrade(postId, grade, feedback, gradedBy);
	}

	public static void releaseAllGrades() {
		database.releaseAllGrades();
	}

	public static ArrayList<Post> getAllGradedPosts() { return database.getAllGradedPosts(); }

	public static boolean isPostGradeReleased(int postId) { return database.isPostGradeReleased(postId); }

	public static String getPostGrade(int postId) { return database.getPostGrade(postId); }

	public static String getPostFeedback(int postId) { return database.getPostFeedback(postId); }
}
