package guiStudentHome;

import java.util.ArrayList;

import entityClasses.Message;
import entityClasses.Post;
import entityClasses.StudentStatus;

/**
 * <p> Title: ControllerStudentHome Class </p>
 * 
 * <p> Description: This controller handles the actions for the guiStudentHome view.
 * It provides static methods to manage posts, apply filters, navigate to other pages, 
 * logout, and quit the application. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerStudentHome {
    
    /** Default constructor, not used */
    private ControllerStudentHome() {}
    
    /*-*******************************************************************************************
     * User Interface Actions for this page
     **********************************************************************************************/
    
    /**********
     * <p> Method: performViewMyPosts </p>
     * <p> Description: Redirects to guiMyPosts page for the current user. </p>
     */
    protected static void performViewMyPosts() {
        guiMyPosts.ViewMyPosts.displayMyPosts(ViewStudentHome.theStage, ViewStudentHome.theUser);
    }
    
    /**********
     * <p> Method: performMakePost </p>
     * <p> Description: Redirects to guiMakePost page for the current user. </p>
     */
    protected static void performMakePost() {
        guiMakePost.ViewMakePost.displayMakePost(ViewStudentHome.theStage, ViewStudentHome.theUser);
    }
    
    /**********
     * <p> Method: performSearch </p>
     * <p> Description: Filters the given post list based on the search keyword entered by the user. </p>
     * @param postList The original list of posts to filter
     * @return A list of posts matching the search keyword
     */
    protected static ArrayList<Post> performSearch(ArrayList<Post> postList) {
        String term = ViewStudentHome.text_SearchPost.getText().trim();
        if (term.isEmpty()) {
            return postList;
        } else {
            ArrayList<Post> resultList = new ArrayList<>();
            for (Post post : postList) {
                if (post.getTitle().toLowerCase().contains(term.toLowerCase())) { // ignore case
                    resultList.add(post);
                }
            }
            return resultList;
        }
    }
    
    /**********
     * <p> Method: performClear </p>
     * <p> Description: Clears the post search field and refreshes all post filters. </p>
     */
    protected static void performClear() {
        ViewStudentHome.text_SearchPost.setText("");
        performAllFilters();
    }
    
    /**********
     * <p> Method: performThreadFilter </p>
     * <p> Description: Filters the given post list by the selected thread. </p>
     * @param postList The original list of posts to filter
     * @return A list of posts that belong to the selected thread
     */
    protected static ArrayList<Post> performThreadFilter(ArrayList<Post> postList) {
        try {
            if (ViewStudentHome.combobox_Thread.getValue().compareTo("All") == 0) {
                return postList;
            } else {
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
     * <p> Method: performUnreadFilter </p>
     * <p> Description: Filters the given post list to include only unread posts if the unread checkbox is selected. </p>
     * @param postList The original list of posts to filter
     * @return A list of unread posts if filter applied; otherwise, returns the original list
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

    /**
     * <p>Method: performSendMessage(String receiver, String subject, String content)</p>
     *
     * <p>Description: Validates and sends a message to a receiver. Updates the message list
     * and closes the send message substage on success.</p>
     *
     * @param receiver The username of the message recipient
     * @param subject The subject of the message
     * @param content The content of the message
     */
    protected static void performSendMessage(String receiver, String subject, String content) {
    	String error = ModelStudentHome.validateMessage(receiver, subject, content);
    	if (!error.isEmpty()) {
    		ViewStudentHome.showError(null, error);
    		return;
    	}
    	if (!ViewStudentHome.theDatabase.getUserAccountDetails(receiver)) {
    		ViewStudentHome.showError(null, "Receiver does not exist.");
			return;
		}

        Message msg = new Message(
        		-1,
        		ViewStudentHome.theUser.getUserName(),
                receiver,
                subject,
                content,
                false
        );

        int msgId = ViewStudentHome.theDatabase.makeMessage(msg);
        if (msgId <= 0) {
        	ViewStudentHome.showError(null, "Failed to send message.");
            return;
        }

        ViewStudentHome.showInfo(null, "Message sent.");
        ViewStudentHome.sendMessageSubstage.close();
        ViewStudentHome.refreshMessageList();
    }

    /**
     * <p>Method: performDeleteMessage(Message msg)</p>
     *
     * <p>Description: Deletes a specified message from the database, refreshes the message list,
     * and closes the message details substage.</p>
     *
     * @param msg The message to delete
     */
    protected static void performDeleteMessage(Message msg) {
        if (msg == null) return;

        ViewStudentHome.theDatabase.deleteMessage(msg);
        ViewStudentHome.refreshMessageList();
        ViewStudentHome.messageDetailsSubstage.close();
    }

    /**
     * <p>Method: performMessageSearch(String keyword)</p>
     *
     * <p>Description: Updates the message search field with a keyword and refreshes the message list
     * based on the search criteria.</p>
     *
     * @param keyword The search keyword
     */
    protected static void performMessageSearch(String keyword) {
    	ViewStudentHome.messageListSearchField.setText(keyword);
    	ViewStudentHome.refreshMessageList();
    }

    /**
     * <p>Method: performReplyMessage(Message original)</p>
     *
     * <p>Description: Prepares the send message GUI to reply to an existing message. Sets the
     * receiver, subject, and clears content, then shows the send message substage.</p>
     *
     * @param original The original message being replied to
     */
    protected static void performReplyMessage(Message original) {
    	ViewStudentHome.sendMessageReceiverField.setText(original.getSender());
    	ViewStudentHome.sendMessageSubjectField.setText("Re: " + original.getSubject());
    	ViewStudentHome.sendMessageContentArea.setText("");

    	ViewStudentHome.sendMessageSubstage.setScene(ViewStudentHome.sendMessageScene);
    	ViewStudentHome.sendMessageSubstage.showAndWait();
    }
    
    /**********
     * <p> Method: performAllFilters </p>
     * <p> Description: Applies all filters (search, thread, unread) to the post list and refreshes the display. </p>
     */
    protected static void performAllFilters() {
        ArrayList<Post> resultList = ViewStudentHome.theDatabase.getAllPostsNewestFirst();
        resultList = performUnreadFilter(resultList);
        resultList = performThreadFilter(resultList);
        resultList = performSearch(resultList);
        ViewStudentHome.posts = resultList;
        ViewStudentHome.refreshPostsDisplay();
    }
    
    /**
     * <p>Method: performRefreshStatus(StudentStatus status)</p>
     *
     * <p>Description: Refreshes a student's post/reply/view/upvote statistics from the
     * database and updates the student's status in the database.</p>
     *
     * @param status The StudentStatus object to refresh
     */
    protected static void performRefreshStatus(StudentStatus status) {
    	status.setPostNumber(ModelStudentHome.getStudentTotalPosts(status.getUserName()));
    	status.setReplyNumber(ModelStudentHome.getStudentTotalReplies(status.getUserName()));
    	status.setViewReceived(ModelStudentHome.getStudentTotalViewsGot(status.getUserName()));
    	status.setReplyReceived(ModelStudentHome.getStudentTotalRepliesGot(status.getUserName()));
    	status.setUpvoteReceived(ModelStudentHome.getStudentTotalUpvotesGot(status.getUserName()));
    	ViewStudentHome.theDatabase.updateStudentStatus(status);
    }
    
    /**********
     * <p> Method: performLogout </p>
     * <p> Description: Logs out the current user and navigates to the normal login page. </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
    }
    
    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates the execution of the program. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
