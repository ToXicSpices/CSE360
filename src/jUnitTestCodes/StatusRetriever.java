package jUnitTestCodes;

import java.util.ArrayList;

import database.Database;
import entityClasses.Post;
import entityClasses.StudentStatus;

public class StatusRetriever {
	
	private static Database dbHelper;
	
	/**Default*/
	public StatusRetriever() {
	}
	
	/**
	 * Constuctor
	 * @param dbHelper is the database to pass
	 */
	public StatusRetriever(Database dbHelper) {
		StatusRetriever.dbHelper = dbHelper;
	}
	
	/**
	 * <p>Method: getStudentTotalPosts(String username)</p>
	 *
	 * <p>Description: Returns the total number of posts made by a specific student.</p>
	 *
	 * @param username The username of the student
	 * @return The total number of posts
	 */
	public static int getStudentTotalPosts(String username) {
		return dbHelper.getNumberOfPosts(username);
	}
	
	/**
	 * <p>Method: getStudentTotalReplies(String username)</p>
	 *
	 * <p>Description: Returns the total number of replies made by a specific student.</p>
	 *
	 * @param username The username of the student
	 * @return The total number of replies
	 */
	public static int getStudentTotalReplies(String username) {
		return dbHelper.getNumberOfReplies(username);
	}
	
	/**
	 * <p>Method: getStudentTotalRepliesGot(String username)</p>
	 *
	 * <p>Description: Returns the total number of replies received on all posts created by the student.</p>
	 *
	 * @param username The username of the student
	 * @return The total replies received
	 */
	public static int getStudentTotalRepliesGot(String username) {
		int total = 0;
		
		// Get all posts created by the user
		ArrayList<Post> posts = dbHelper.getPostsFromUserNewestFirst(username);
		
		// Sum replies from each post
		for (Post p : posts) {
	        int postId = p.getPostId();
	        total += dbHelper.getPostNumberOfReplies(postId);
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
	public static int getStudentTotalUpvotesGot(String username) {
	    int total = 0;

	    // Get all posts created by the user
	    ArrayList<Post> posts = dbHelper.getPostsFromUserNewestFirst(username);

	    // Sum upvotes from each post
	    for (Post p : posts) {
	        int postId = p.getPostId();
	        total += dbHelper.getPostUpvotes(postId);
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
	public static int getStudentTotalViewsGot(String username) {
	    int totalViews = 0;

	    // Get all posts written by this user
	    ArrayList<Post> posts = dbHelper.getPostsFromUserNewestFirst(username);

	    // Sum views for each post
	    for (Post p : posts) {
	        int postId = p.getPostId();
	        totalViews += dbHelper.getPostViews(postId);
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
	public static double getParticipation(StudentStatus s) {
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
	public static double getPerformance(StudentStatus s) {
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
	public static double getTotalGrade(double participation, double performance) {
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
	public static String getGradeMark(double grade) {
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
     * <p>Method: performRefreshStatus(StudentStatus status)</p>
     *
     * <p>Description: Refreshes a student's post/reply/view/upvote statistics from the
     * database and updates the student's status in the database.</p>
     *
     * @param status The StudentStatus object to refresh
     */
    public static void performRefreshStatus(StudentStatus status) {
    	status.setPostNumber(getStudentTotalPosts(status.getUserName()));
    	status.setReplyNumber(getStudentTotalReplies(status.getUserName()));
    	status.setViewReceived(getStudentTotalViewsGot(status.getUserName()));
    	status.setReplyReceived(getStudentTotalRepliesGot(status.getUserName()));
    	status.setUpvoteReceived(getStudentTotalUpvotesGot(status.getUserName()));
    	dbHelper.updateStudentStatus(status);
    }
}
