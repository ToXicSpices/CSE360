package guiStaffHome;

import java.util.ArrayList;

import entityClasses.Post;
import entityClasses.StudentStatus;

/*******
 * <p> Title: ModelStaffHome Class. </p>
 * 
 * <p> Description: The Role2Home Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting role information and saving it in the
 * database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */
public class ModelStaffHome {

	/**Default constructor, not used*/
	private ModelStaffHome() {}
	
	/**
	 * <p>Method: getStudentTotalPosts(String username)</p>
	 *
	 * <p>Description: Returns the total number of posts made by a specific student.</p>
	 *
	 * @param username The username of the student
	 * @return The total number of posts
	 */
	protected static int getStudentTotalPosts(String username) {
		return ViewStaffHome.theDatabase.getNumberOfPosts(username);
	}
	
	/**
	 * <p>Method: getStudentTotalReplies(String username)</p>
	 *
	 * <p>Description: Returns the total number of replies made by a specific student.</p>
	 *
	 * @param username The username of the student
	 * @return The total number of replies
	 */
	protected static int getStudentTotalReplies(String username) {
		return ViewStaffHome.theDatabase.getNumberOfReplies(username);
	}
	
	/**
	 * <p>Method: getStudentTotalRepliesGot(String username)</p>
	 *
	 * <p>Description: Returns the total number of replies received on all posts created by the student.</p>
	 *
	 * @param username The username of the student
	 * @return The total replies received
	 */
	protected static int getStudentTotalRepliesGot(String username) {
		int total = 0;
		
		// Get all posts created by the user
		ArrayList<Post> posts = ViewStaffHome.theDatabase.getPostsFromUserNewestFirst(username);
		
		// Sum replies from each post
		for (Post p : posts) {
	        int postId = p.getPostId();
	        total += ViewStaffHome.theDatabase.getPostNumberOfReplies(postId);
	    }

	    return total;
	}
	
	/**
	 * <p>Method: getStudentTotalUpvotesGot(String username)</p>
	 *
	 * <p>Description: Returns the total number of upvotes received on all posts by the student.</p>
	 *
	 * @param username The username of the student
	 * @return The total upvotes received
	 */
	protected static int getStudentTotalUpvotesGot(String username) {
	    int total = 0;

	    // Get all posts created by the user
	    ArrayList<Post> posts = ViewStaffHome.theDatabase.getPostsFromUserNewestFirst(username);

	    // Sum upvotes from each post
	    for (Post p : posts) {
	        int postId = p.getPostId();
	        total += ViewStaffHome.theDatabase.getPostUpvotes(postId);
	    }

	    return total;
	}
	
	/**
	 * <p>Method: getStudentTotalViewsGot(String username)</p>
	 *
	 * <p>Description: Returns the total number of views received on all posts by the student.</p>
	 *
	 * @param username The username of the student
	 * @return The total views received
	 */
	protected static int getStudentTotalViewsGot(String username) {
	    int totalViews = 0;

	    // Get all posts written by this user
	    ArrayList<Post> posts = ViewStaffHome.theDatabase.getPostsFromUserNewestFirst(username);

	    // Sum views for each post
	    for (Post p : posts) {
	        int postId = p.getPostId();
	        totalViews += ViewStaffHome.theDatabase.getPostViews(postId);
	    }

	    return totalViews;
	}

	/**
	 * <p>Method: getParticipation(StudentStatus s)</p>
	 *
	 * <p>Description: Calculates a participation score based on the student's posts and replies.
	 * Maximum 5 posts and 5 replies are counted. Posts weigh 60%, replies weigh 40%.</p>
	 *
	 * @param s The StudentStatus object
	 * @return Participation percentage (0-100)
	 */
	protected static double getParticipation(StudentStatus s) {
		int p = s.getPostNumber();
		int r = s.getReplyNumber();
		if (p > 5) p = 5;					// Maximum 5 posts
		if (r > 5) r = 5;					// Maximum 5 replies
		return ((p / 5.0) * 0.6 + 			// Post weighs 60%
				(r / 5.0) * 0.4) * 100;		// Reply weighs 40%
	}
	
	/**
	 * <p>Method: getPerformance(StudentStatus s)</p>
	 *
	 * <p>Description: Calculates a performance score for a student based on replies received,
	 * upvotes, views per post, promotions, and violations. Weighted contribution: 
	 * reply/post 30%, upvotes 10%, view/post 60%, promotions +5% each, violations -10% each.</p>
	 *
	 * @param s The StudentStatus object
	 * @return Performance percentage (0-100)
	 */
	protected static double getPerformance(StudentStatus s) {
		if (s.getPostNumber() == 0) return 0;
		
		double rdp = s.getReplyReceived() * 1.0 / s.getPostNumber();
		double up = s.getUpvoteReceived() * 1.0;
		double vdp = s.getViewReceived() * 1.0 / s.getPostNumber();
		
		if (rdp > 1) rdp = 1 + (rdp - 1) * 0.1;		// If the ratio of replies got per posts posted is greater than 1, 10% of excess part will be extra credit
		if (up > 5) up = 5;							// Maximum 5 upvotes
		if (vdp > 1) vdp = 1 + (vdp - 1) * 0.05;	// If the ratio of views got per posts posted is greater than 1, 5% of excess part will be extra credit
		
		return (rdp * 0.3 +							// reply/post weighs 30%
                up / 5.0 * 0.1 +					// upvotes weighs 10%
                vdp * 0.6 +							// view/post weighs 60%					
                s.getPromotion() * 0.05 -			// promotion will be 5% extra per each
                s.getViolation() * 0.1) * 100;		// violation will be -10% extra per each
	}
	
	/**
	 * <p>Method: getTotalGrade(double participation, double performance)</p>
	 *
	 * <p>Description: Calculates a total grade combining participation (80%) and performance (20%).</p>
	 *
	 * @param participation Participation score (0-100)
	 * @param performance Performance score (0-100)
	 * @return Total grade (0-100)
	 */
	protected static double getTotalGrade(double participation, double performance) {
		return participation * 0.8 + 				// participation weighs 80%
				performance * 0.2;					// performance weighs 20%
	}
	
	/**
	 * <p>Method: getGradeMark(double grade)</p>
	 *
	 * <p>Description: Converts a numeric grade into a letter grade based on standard thresholds.</p>
	 *
	 * @param grade Numeric grade (0-100)
	 * @return Letter grade as String (e.g., "A+", "B-", "F")
	 */
	protected static String getGradeMark(double grade) {
		if (grade >= 97) {
			return "A+";
		}
		if (grade >= 93) {
			return "A";
		}
		if (grade >= 90) {
			return "A-";
		}
		if (grade >= 87) {
			return "B+";
		}
		if (grade >= 83) {
			return "B";
		}
		if (grade >= 80) {
			return "B-";
		}
		if (grade >= 77) {
			return "C+";
		}
		if (grade >= 73) {
			return "C";
		}
		if (grade >= 70) {
			return "C-";
		}
		if (grade >= 60) {
			return "D";
		}
		if (grade < 60) {
			return "F";
		}
		return "N/A";
	}
	
	/**
	 * <p>Method: validateThreads(String name)</p>
	 *
	 * <p>Description: Validates a thread name. Checks for empty names, invalid characters,
	 * maximum length, and existence in the database.</p>
	 *
	 * @param name The thread name
	 * @return Error message if invalid, or empty string if valid
	 */
	protected static String validateThreads(String name) {
		if (name.isEmpty()) {
            return "Thread name cannot be empty.";
        } 
		else if (!name.matches("[A-Za-z0-9]+")) {
			return "Thread may only contain alphabets or numbers.";
		}
		else if (name.length() > 20) {
			return "Thread name cannot be longer than 20 characters.";
		}
		return "";
	}
	
	/**
	 * <p>Method: validateRequests(String title, String content)</p>
	 *
	 * <p>Description: Validates a request's title and content. Checks for empty fields and maximum length limits.</p>
	 *
	 * @param title The request title
	 * @param content The request content
	 * @return Error message if invalid, or empty string if valid
	 */
	protected static String validateRequests(String title, String content) {
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
	
	/**
	 * <p>Method: validateMessage(String receiver, String subject, String content)</p>
	 *
	 * <p>Description: Validates message fields. Checks for empty fields, maximum lengths,
	 * and whether the receiver exists in the database.</p>
	 *
	 * @param receiver The recipient's username
	 * @param subject The message subject
	 * @param content The message content
	 * @return Error message if invalid, or empty string if valid
	 */
	protected static String validateMessage(String receiver, String subject, String content) {
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
