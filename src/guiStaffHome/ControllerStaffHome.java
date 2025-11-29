package guiStaffHome;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import entityClasses.Message;
import entityClasses.Post;
import entityClasses.Request;
import entityClasses.StudentStatus;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
/**
 * <p> Title: ControllerStaffHome Class </p>
 * 
 * <p> Description: This controller handles the actions for the guiStaffHome view.
 * It provides static methods to update user account information, logout, and quit the application.
 * All methods interact directly with the View. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerStaffHome {
	
	/**Default constructor, not used*/
	private ControllerStaffHome() {}

    /*-*******************************************************************************************
     * User Interface Actions for this page
     **********************************************************************************************/
    
	/**********
     * <p> Method: performViewMyPosts </p>
     * <p> Description: Redirects to guiMyPosts page for the current user. </p>
     */
    protected static void performViewMyPosts() {
        guiMyPosts.ViewMyPosts.displayMyPosts(ViewStaffHome.theStage, ViewStaffHome.theUser);
    }
    
    /**********
     * <p> Method: performMakePost </p>
     * <p> Description: Redirects to guiMakePost page for the current user. </p>
     */
    protected static void performMakePost() {
        guiMakePost.ViewMakePost.displayMakePost(ViewStaffHome.theStage, ViewStaffHome.theUser);
    }
    
    /**********
     * <p> Method: performSearch </p>
     * <p> Description: Filters the given post list based on the search keyword entered by the user. </p>
     * @param postList The original list of posts to filter
     * @return A list of posts matching the search keyword
     */
    protected static ArrayList<Post> performSearch(ArrayList<Post> postList) {
        String term = ViewStaffHome.text_SearchPost.getText().trim();
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
        ViewStaffHome.text_SearchPost.setText("");
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
            if (ViewStaffHome.combobox_Thread.getValue().compareTo("All") == 0) {
                return postList;
            } else {
                ArrayList<Post> resultList = new ArrayList<>();
                for (Post post : postList) {
                    if (post.getThread().compareTo(ViewStaffHome.combobox_Thread.getValue()) == 0) {
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
        if (ViewStaffHome.checkBox_showUnreadPosts.isSelected()) {
            ArrayList<Post> resultList = new ArrayList<>();
            for (Post post : postList) {
                if (ViewStaffHome.theDatabase.isPostUnread(ViewStaffHome.theUser.getUserName(), post.getPostId())) {
                    resultList.add(post);
                }
            }
            return resultList;
        } else {
            return postList;
        }
    }
    
    /**********
     * <p> Method: performAllFilters </p>
     * <p> Description: Applies all filters (search, thread, unread) to the post list and refreshes the display. </p>
     */
    protected static void performAllFilters() {
        ArrayList<Post> resultList = ViewStaffHome.theDatabase.getAllPostsNewestFirst();
        resultList = performUnreadFilter(resultList);
        resultList = performThreadFilter(resultList);
        resultList = performSearch(resultList);
        ViewStaffHome.posts = resultList;
        ViewStaffHome.refreshPostsDisplay();
    }
    
    /**
     * <p>Method: performMakeRequest(String title, String content)</p>
     *
     * <p>Description: Validates and submits a new request for the currently logged-in staff user.
     * If validation fails, shows an error. Otherwise, saves the request to the database, refreshes
     * the requests list, and closes the request substage.</p>
     *
     * @param title The title of the request
     * @param content The content/details of the request
     */
    protected static void performMakeRequest(String title, String content) {
        String error = ModelStaffHome.validateRequests(title, content);
        if (!error.isEmpty()) {
        	ViewStaffHome.showError(null, error);
        }
        Request req = new Request(title, content, ViewStaffHome.theUser.getUserName(), false);
        try {
            int id = ViewStaffHome.theDatabase.makeRequest(req);
            if (id != -1) {
            	ViewStaffHome.showInfo(null, "Request submitted.");
            	ViewStaffHome.refreshSystemRequestsList();
            	ViewStaffHome.makeRequestSubstage.close();
            } else {
            	ViewStaffHome.showError(null, "Failed to submit request");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            ViewStaffHome.showError(null, "Database error: " + ex.getMessage());
        }
    }
    
    /**
     * <p>Method: performUpdateRequest(Request req, String newTitle, String newContent)</p>
     *
     * <p>Description: Updates an existing request's title and content after validation.
     * Refreshes the system requests list and closes the update substage.</p>
     *
     * @param req The request to update
     * @param newTitle The new title
     * @param newContent The new content
     */
    protected static void performUpdateRequest(Request req, String newTitle, String newContent) {
    	String error = ModelStaffHome.validateRequests(newTitle, newContent);
        if (!error.isEmpty()) {
        	ViewStaffHome.showError(null, error);
        	return;
        }
    	try {
    		ViewStaffHome.theDatabase.updateRequestTitle(req, newTitle);
    		ViewStaffHome.theDatabase.updateRequestContent(req, newContent);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    	ViewStaffHome.refreshSystemRequestsList();
    	ViewStaffHome.openRequestSubstage.close();
    }
    
    /**
     * <p>Method: performAddThread()</p>
     *
     * <p>Description: Adds a new discussion thread after validation. Updates the threads
     * table view and shows an info message on success.</p>
     */
    protected static void performAddThread() {
    	String name = ViewStaffHome.manageThreadsAddField.getText().trim();
    	String error = "";
    	error = ModelStaffHome.validateThreads(name);
    	if (!error.isEmpty()) ViewStaffHome.showError(null, error);
    	try {
			if (ViewStaffHome.theDatabase.existsThreadName(name)) {
				ViewStaffHome.showError(null, "Thread already exists.");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			ViewStaffHome.showError(null, "Database error.");
			return;
		}
    	ViewStaffHome.theDatabase.addThread(name);
		ViewStaffHome.showInfo(null, "Thread added successfully.");
		ViewStaffHome.manageThreadsTableView.setItems(FXCollections.observableArrayList(ViewStaffHome.theDatabase.getAllThreads()));
    }
    

    /**
     * <p>Method: performUpdateThread(String oldName)</p>
     *
     * <p>Description: Updates the name of an existing thread. Prompts user for the new name,
     * validates it, updates the database, and refreshes the threads table.</p>
     *
     * @param oldName The current name of the thread to update
     */
    protected static void performUpdateThread(String oldName) {
    	ViewStaffHome.manageThreadsUpdateDialog.setHeaderText("Update Thread Name");
    	ViewStaffHome.manageThreadsUpdateDialog.setContentText("Enter new name:");

        Optional<String> result = ViewStaffHome.manageThreadsUpdateDialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get().trim();

            String error = "";
        	error = ModelStaffHome.validateThreads(newName);
        	if (!error.isEmpty()) {
    			ViewStaffHome.showError(null, error);
    		}
        	else {
        		try {
					ViewStaffHome.theDatabase.updateThreadName(ViewStaffHome.theDatabase.getThreadId(oldName), newName);
					ViewStaffHome.showInfo(null, "Thread added successfully.");
	        		ViewStaffHome.manageThreadsTableView.setItems(FXCollections.observableArrayList(ViewStaffHome.theDatabase.getAllThreads()));
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
    }
    
    /**
     * <p>Method: performDeleteThread(String name)</p>
     *
     * <p>Description: Deletes a thread after user confirmation, updates the database,
     * and refreshes the threads table view.</p>
     *
     * @param name The name of the thread to delete
     */
    protected static void performDeleteThread(String name) {
        Optional<ButtonType> res = ViewStaffHome.showConfirm("Delete Thread", "Are you sure you want to delete \"" + name + "\"?");
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                int id = ViewStaffHome.theDatabase.getThreadId(name);
                ViewStaffHome.theDatabase.deleteThread(id);
                ViewStaffHome.showInfo(null, "Thread deleted.");
                ViewStaffHome.manageThreadsTableView.setItems(FXCollections.observableArrayList(ViewStaffHome.theDatabase.getAllThreads()));

            } catch (SQLException ex) {
                ex.printStackTrace();
                ViewStaffHome.showError(null, "Database error.");
            }
        }
    }
    
    /**
     * <p>Method: performStudentSearch()</p>
     *
     * <p>Description: Searches for students in the database based on a keyword. Updates
     * the student table view with matching results and applies sorting order.</p>
     */
    protected static void performStudentSearch() {
    	String keyword = ViewStaffHome.studentSearchField.getText().trim().toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User u : ViewStaffHome.theDatabase.getAllStudents()) {
            String fullName = (u.getFirstName() + " " + u.getMiddleName() + " " + u.getLastName()).toLowerCase();
            if (u.getUserName().toLowerCase().contains(keyword) ||
                fullName.contains(keyword) ||
                u.getPreferredFirstName().toLowerCase().contains(keyword)) {
                filteredList.add(u);
            }
        }

        if ("A-Z".equals(ViewStaffHome.studentListSortCombobox.getValue()))
            filteredList.sort((u1, u2) -> u1.getUserName().compareToIgnoreCase(u2.getUserName()));
        else
            filteredList.sort((u1, u2) -> u2.getUserName().compareToIgnoreCase(u1.getUserName()));

        ViewStaffHome.studentListTableView.setItems(filteredList);
    }
    
    /**
     * <p>Method: performStudentClear()</p>
     *
     * <p>Description: Clears the student search field and resets the student table view
     * with all students from the database, sorted according to the current sort order.</p>
     */
    protected static void performStudentClear() {
    	ViewStaffHome.studentSearchField.clear();
        ObservableList<User> list = FXCollections.observableArrayList(ViewStaffHome.theDatabase.getAllStudents());
        if ("A-Z".equals(ViewStaffHome.studentListSortCombobox.getValue()))
            list.sort((u1, u2) -> u1.getUserName().compareToIgnoreCase(u2.getUserName()));
        else
            list.sort((u1, u2) -> u2.getUserName().compareToIgnoreCase(u1.getUserName()));

        ViewStaffHome.studentListTableView.setItems(list);
    }
    
    /**
     * <p>Method: performStudentSort()</p>
     *
     * <p>Description: Sorts the current student list in the table view based on the
     * selected sort order (A-Z or Z-A).</p>
     */
    protected static void performStudentSort() {
    	ObservableList<User> currentList = ViewStaffHome.studentListTableView.getItems();
        if ("A-Z".equals(ViewStaffHome.studentListSortCombobox.getValue()))
            currentList.sort((u1, u2) -> u1.getUserName().compareToIgnoreCase(u2.getUserName()));
        else
            currentList.sort((u1, u2) -> u2.getUserName().compareToIgnoreCase(u1.getUserName()));

        ViewStaffHome.studentListTableView.refresh();
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
    	status.setPostNumber(ModelStaffHome.getStudentTotalPosts(status.getUserName()));
    	status.setReplyNumber(ModelStaffHome.getStudentTotalReplies(status.getUserName()));
    	status.setViewReceived(ModelStaffHome.getStudentTotalViewsGot(status.getUserName()));
    	status.setReplyReceived(ModelStaffHome.getStudentTotalRepliesGot(status.getUserName()));
    	status.setUpvoteReceived(ModelStaffHome.getStudentTotalUpvotesGot(status.getUserName()));
    	ViewStaffHome.theDatabase.updateStudentStatus(status);
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
    	String error = ModelStaffHome.validateMessage(receiver, subject, content);
    	if (!error.isEmpty()) {
    		ViewStaffHome.showError(null, error);
    		return;
    	}
    	if (!ViewStaffHome.theDatabase.getUserAccountDetails(receiver)) {
    		ViewStaffHome.showError(null, "Receiver does not exist.");
			return;
		}

        Message msg = new Message(
        		-1,
        		ViewStaffHome.theUser.getUserName(),
                receiver,
                subject,
                content,
                false
        );

        int msgId = ViewStaffHome.theDatabase.makeMessage(msg);
        if (msgId <= 0) {
        	ViewStaffHome.showError(null, "Failed to send message.");
            return;
        }

        ViewStaffHome.showInfo(null, "Message sent.");
        ViewStaffHome.sendMessageSubstage.close();
        ViewStaffHome.refreshMessageList();
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

        ViewStaffHome.theDatabase.deleteMessage(msg);
        ViewStaffHome.refreshMessageList();
        ViewStaffHome.messageDetailsSubstage.close();
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
    	ViewStaffHome.messageListSearchField.setText(keyword);
    	ViewStaffHome.refreshMessageList();
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
    	ViewStaffHome.sendMessageReceiverField.setText(original.getSender());
    	ViewStaffHome.sendMessageSubjectField.setText("Re: " + original.getSubject());
    	ViewStaffHome.sendMessageContentArea.setText("");

    	ViewStaffHome.sendMessageSubstage.setScene(ViewStaffHome.sendMessageScene);
    	ViewStaffHome.sendMessageSubstage.showAndWait();
    }
    
    /**********
     * <p> Method: performUpdate </p>
     * <p> Description: Navigates to the User Update page for the current staff user. </p>
     */
    protected static void performUpdate() {
        guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
    }

    /**********
     * <p> Method: performLogout </p>
     * <p> Description: Logs out the current user and navigates to the User Login page. </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Terminates the execution of the application with exit code 0. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
