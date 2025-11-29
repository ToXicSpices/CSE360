package guiPostPage;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.scene.text.FontWeight;
import java.util.Optional;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ViewPostPage Class. </p>
 *
 * <p> Description: The Java/FX-based Post Page. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.01    2025-10-14 Updated: repliesBox and robust refresh
 *
 */

public class ViewPostPage {

	
	/*-*******************************************************************************************

    Attributes

	 */

	// These are the application values required by the user interface
	/** Application window width obtained from the main application. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Application window height obtained from the main application. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI. 

	// GUI for page
	/** Label displaying the title of the page. */
	protected static Label label_PageTitle = new Label();

	/** ScrollPane for the main post area. */
	protected static ScrollPane pane_PostArea;

	/** Root container of the page (BorderPane layout). */
	protected static BorderPane root = new BorderPane();

	/** VBox container for the header section of the page. */
	protected static VBox header = new VBox(15);

	// GUI for post
	/** Label for the post's main title. */
	protected static Label label_PostTitle = new Label();

	/** Label for the post's subtitle or secondary heading. */
	protected static Label label_PostSubtitle = new Label();

	/** Label displaying the post owner's username. */
	protected static Label label_PostUser = new Label();

	/** Label displaying the post content. */
	protected static Label label_PostContent = new Label();

	/** VBox container holding the post content area. */
	protected static VBox box_PostArea = new VBox();
	
	/** VBox container holding the post content section. */
	protected static VBox box_PostSection = new VBox(10);

	// GUI for post buttons
	/** HBox container holding the post buttons. */
	protected static HBox box_PostButtons = new HBox(10);
	
	/** Button to upvote this post. */
	protected static Button button_Upvote = new Button("Upvote");
	
	/** Button to enter edit mode for the post. */
	protected static Button button_EditPost = new Button("Edit Post");

	/** Button to save changes after editing a post. */
	protected static Button button_SavePost = new Button("Save Post");

	/** Button to cancel editing a post. */
	protected static Button button_CancelEdit = new Button("Cancel Edit");

	/** TextField to edit the post title. */
	protected static TextField edit_PostTitle = new TextField();

	/** TextField to edit the post subtitle. */
	protected static TextField edit_PostSubtitle = new TextField();

	/** TextArea to edit the post content. */
	protected static TextArea edit_PostContent = new TextArea();

	// GUI for reply buttons
	/** HBox container for Send and Cancel reply buttons. */
	protected static HBox box_SendCancel = new HBox();

	/** HBox container for reply-related info, e.g., unread replies checkbox. */
	protected static HBox box_ReplyUnread = new HBox();

	/** Button to reply to the post. */
	protected static Button button_Reply = new Button("Reply to this post");

	/** Checkbox to filter and show only unread replies. */
	protected static CheckBox checkBox_showUnreadReplies = new CheckBox("Show Unread Replies");

	/** TextArea for writing a reply. */
	protected static TextArea text_reply = new TextArea();

	/** Button to send the written reply. */
	protected static Button button_SendReply = new Button("Send");

	/** Button to cancel composing a reply. */
	protected static Button button_CancelReply = new Button("Cancel");

	// GUI for replies
	/** VBox container holding all replies to the current post. */
	protected static VBox repliesBox = new VBox();

	// GUI for redirect buttons
	/** Button to return to the previous page or home page. */
	protected static Button button_Return = new Button("Return");

	/** Button to log out the user. */
	protected static Button button_Logout = new Button("Logout");

	/** Button to quit the application. */
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.

	// These attributes are used to configure the page and populate it with this user's information
	/** Singleton instance of this ViewPostPage for managing page state. */
	protected static ViewPostPage theView;

	// Reference for the in-memory database so this package has access
	/** Reference to the application's database for retrieving posts and replies. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX stage where this post page will be displayed. */
	protected static Stage theStage;

	/** Currently logged-in user viewing the post. */
	protected static User theUser;

	/** The currently displayed post. */
	protected static Post thePost;

	/** List of replies associated with the currently displayed post. */
	protected static ArrayList<Reply> replies;

	/** Scene object for the post page. */
	private static Scene thePostPageScene;

	/** Role identifier of the current user (used for UI logic). Default is -1. */
	protected static int theRole = -1;


    /**
     * <p> Method: void displayPostPage(Stage ps, User user, int role, Post post) </p>
     * <p> Description: Displays the post page with the given parameters and initializes the UI state.
     * This method serves as the main entry point for viewing a post page. </p>
     *
     * @param ps The Stage object for the JavaFX window
     * @param user The User object representing the current logged-in user
     * @param post The Post object to be displayed on this page
     */
    
    public static void displayPostPage(Stage ps, User user, Post post) {
    	
    	// Establish the references
        theStage = ps;
        theUser = user;
        thePost = post;
        theRole = applicationMain.FoundationsMain.activeHomePage;

        // Populate the dynamic aspects of the GUI with the data. 
        theDatabase.getUserAccountDetails(user.getUserName());
        applicationMain.FoundationsMain.activeHomePage = theRole;
        replies = theDatabase.getRepliesByPostId(thePost.getPostId());
        
        // Refresh post content and replies
        refreshPostContent();
        refreshReplies();
        
     	// populate the static aspects of the GUI
        if (theView == null) theView = new ViewPostPage();
        
        // update the read/unread status of post and replies
        theDatabase.markPostAsRead(theUser.getUserName(), post.getPostId());
        theDatabase.markRepliesAsRead(theUser.getUserName(), post.getPostId());
        
        // Set the title for the window, display the page, and wait for the Admin to do something
        theStage.setTitle("CSE 360 Foundations: Post Page");
        theStage.setScene(thePostPageScene);
        theStage.show();
    }
    
    /**
     * <p> Method: ViewPostPage() </p>
     * <p> Description: Private constructor that initializes the complete UI layout for the post page.
     * Sets up the main BorderPane structure with header, content area, and footer.
     * Configures all buttons, text areas, and event handlers for user interactions. </p>
     */
    
    private ViewPostPage() {
    	
    	// Set up the header
        header.setPadding(new Insets(20));
        
        label_PageTitle.setText("Post Page");
        label_PageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        label_PageTitle.setAlignment(Pos.CENTER);
        header.getChildren().add(label_PageTitle);
        
        root.setTop(header);
        setupPostContent();

        setupButtonUI(button_Upvote, "Dialog", 16, 150, Pos.CENTER, 0, 0);
        if (theDatabase.isUpvoted(theUser.getUserName(), thePost.getPostId()) || thePost.getOwner().equals(theUser.getUserName())) {
            button_Upvote.setDisable(true);
            button_Upvote.setStyle("-fx-opacity: 0.5;");
        }
        button_Upvote.setOnAction(e -> {
            theDatabase.makePostUpvote(theUser.getUserName(), thePost.getPostId());
            System.out.print(theDatabase.getStudentStatus(theUser.getUserName()).toString());
			button_Upvote.setDisable(true);
			button_Upvote.setStyle("-fx-opacity: 0.5;");
        });
        
        // Set up post edit buttons
        setupButtonUI(button_EditPost, "Dialog", 16, 150, Pos.CENTER, 0, 0);
        button_EditPost.setOnAction(event -> enablePostEdit());

        setupButtonUI(button_SavePost, "Dialog", 16, 150, Pos.CENTER, 0, 0);
        button_SavePost.setOnAction(event -> savePostEdit());
        button_SavePost.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        setupButtonUI(button_CancelEdit, "Dialog", 16, 150, Pos.CENTER, 0, 0);
        button_CancelEdit.setOnAction(event -> cancelPostEdit());

        box_PostArea.setSpacing(15);
        box_PostArea.setPadding(new Insets(20));
        buildPostArea();
        
        // Set up reply buttons
        checkBox_showUnreadReplies.setFont(Font.font("Dialog", 18));
        checkBox_showUnreadReplies.setOnAction(e -> {
            if (checkBox_showUnreadReplies.isSelected()) {
            	replies = theDatabase.getRepliesByPostId(thePost.getPostId());
            } else {
            	replies = theDatabase.getUnreadReplies(theUser.getUserName(), thePost.getPostId());
            }
        });
        setupButtonUI(button_Reply, "Dialog", 16, 200, Pos.CENTER, 0, 0);
        button_Reply.setOnAction(event -> showReplyEditor());
        Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
        box_ReplyUnread.getChildren().addAll(button_Reply, spacer, checkBox_showUnreadReplies);
        
        setupButtonUI(button_SendReply, "Dialog", 14, 100, Pos.CENTER, 0, 0);
        button_SendReply.setOnAction(event -> ControllerPostPage.doReply());
        setupButtonUI(button_CancelReply, "Dialog", 14, 100, Pos.CENTER, 0, 0);
        button_CancelReply.setOnAction(event -> ControllerPostPage.doCancel());
        box_SendCancel.setSpacing(10);
        box_SendCancel.getChildren().addAll(button_CancelReply, button_SendReply);
        
        text_reply.setPromptText("Write your reply here...");
        text_reply.setPrefWidth(650);
        text_reply.setPrefHeight(100);
        text_reply.setWrapText(true);
        
        // Set up scroll pane for the post area
        pane_PostArea = new ScrollPane(box_PostArea);
        pane_PostArea.setFitToWidth(true);
        pane_PostArea.setPrefHeight(400);
        root.setCenter(pane_PostArea);

        // Set up footer buttons
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15, 20, 20, 20));
        footer.setAlignment(Pos.CENTER);

        setupButtonUI(button_Return, "Dialog", 14, 100, Pos.CENTER, 0, 0);
        button_Return.setOnAction(event -> ControllerPostPage.goToUserHomePage(theStage, theUser));

        setupButtonUI(button_Logout, "Dialog", 14, 100, Pos.CENTER, 0, 0);
        button_Logout.setOnAction(event -> ControllerPostPage.performLogout());

        setupButtonUI(button_Quit, "Dialog", 14, 100, Pos.CENTER, 0, 0);
        button_Quit.setOnAction(event -> ControllerPostPage.performQuit());

        footer.getChildren().addAll(button_Return, button_Logout, button_Quit);
        root.setBottom(footer);

        thePostPageScene = new Scene(root, width, height);
        
    }

    /*-********************************************************************************************

    Helper methods to reduce code length

     */
    
    /**
     * <p> Method: void enablePostEdit() </p>
     * <p> Description: Initializes and configures all post content display and editing elements.
     * Sets up fonts, sizes, and visibility states for both display labels and edit fields.
     * The edit fields are initially hidden and only shown during edit mode. </p>
     */
    private static void setupPostContent() {
        label_PostTitle.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        label_PostTitle.setWrapText(true);
        label_PostTitle.setMaxWidth(650);

        label_PostSubtitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 22));
        label_PostSubtitle.setWrapText(true);
        label_PostSubtitle.setMaxWidth(650);

        // Hide subtitle entirely if empty (prevents blank space)
        if (thePost != null && 
            (thePost.getSubtitle() == null || thePost.getSubtitle().trim().isEmpty())) {
            label_PostSubtitle.setManaged(false);
            label_PostSubtitle.setVisible(false);
        }

        // Username aligned right
        label_PostUser.setFont(Font.font("Arial", 20));
        label_PostUser.setStyle("-fx-text-fill: #666666;");
        label_PostUser.setMaxWidth(Double.MAX_VALUE);
        label_PostUser.setAlignment(Pos.CENTER_RIGHT);

        // Indent content
        label_PostContent.setFont(Font.font("Arial", 16));
        label_PostContent.setWrapText(true);
        label_PostContent.setMaxWidth(650);
        label_PostContent.setPadding(new Insets(0, 0, 0, 25)); // << indent

        // Edit fields
        edit_PostTitle.setFont(Font.font("Arial", 16));
        edit_PostTitle.setPrefWidth(650);
        edit_PostTitle.setVisible(false);

        edit_PostSubtitle.setFont(Font.font("Arial", 14));
        edit_PostSubtitle.setPrefWidth(650);
        edit_PostSubtitle.setVisible(false);

        edit_PostContent.setFont(Font.font("Arial", 14));
        edit_PostContent.setPrefWidth(650);
        edit_PostContent.setPrefHeight(150);
        edit_PostContent.setWrapText(true);
        edit_PostContent.setVisible(false);
    }


	/**
	 * <p> Method: void enablePostEdit() </p>
     * <p> Description: Constructs the main post area layout including display and edit elements.
	 * Configures visibility of buttons based on user permissions (post owner).
	 * Adds sections for post content, action buttons, and replies. </p>
	 */
    private static void buildPostArea() {
    	// Post section
        box_PostArea.getChildren().clear();
        box_PostSection.setPadding(new Insets(15));
        box_PostSection.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");
        box_PostSection.getChildren().add(label_PostTitle);
        if (label_PostSubtitle.isVisible()) box_PostSection.getChildren().add(label_PostSubtitle);
        box_PostSection.getChildren().addAll(
        		label_PostUser,
        		new Separator(),
        		label_PostContent,
        		edit_PostTitle,
        		edit_PostSubtitle,
        		edit_PostContent,
        		new Separator(),
        		button_Upvote
        );

        // Button row
        box_PostButtons.setAlignment(Pos.CENTER_LEFT);
        
        // Post owner
        if (thePost != null && thePost.getOwner().equals(theUser.getUserName())) {
        	box_PostButtons.getChildren().addAll(button_EditPost, button_SavePost, button_CancelEdit);
            button_EditPost.setVisible(true);
            button_SavePost.setVisible(false);
            button_CancelEdit.setVisible(false);
        }
        box_PostArea.getChildren().addAll(
            box_PostSection,
            box_PostButtons,
            new Separator(),
            box_ReplyUnread,
            repliesBox
        );
    }
    
    /**
     * <p>Method: enablePostEdit()</p>
     *
     * <p>Description: Enables editing mode for a post. Hides the display labels and shows
     * editable text fields pre-filled with the current post's title, subtitle, and content.
     * Adjusts button visibility for editing.</p>
     */
    private static void enablePostEdit() {

        label_PostTitle.setVisible(false);
        label_PostSubtitle.setVisible(false);
        label_PostContent.setVisible(false);

        // Show edit fields
        edit_PostTitle.setText(thePost.getTitle());
        edit_PostSubtitle.setText(thePost.getSubtitle());
        edit_PostContent.setText(thePost.getContent());

        edit_PostTitle.setVisible(true);
        edit_PostSubtitle.setVisible(true);
        edit_PostContent.setVisible(true);

        edit_PostSubtitle.setManaged(true); // <<< IMPORTANT FIX

        button_EditPost.setVisible(false);
        button_SavePost.setVisible(true);
        button_CancelEdit.setVisible(true);
    }
    
    /**
     * <p>Method: savePostEdit()</p>
     *
     * <p>Description: Saves the changes made to the post. Validates that title and content
     * are not empty, updates the database and local post object, refreshes the display, and
     * exits editing mode.</p>
     */
    private static void savePostEdit() {
        String newTitle = edit_PostTitle.getText().trim();
        String newSubtitle = edit_PostSubtitle.getText().trim();
        String newContent = edit_PostContent.getText().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            showAlert("Validation Error", "Title and content cannot be empty.");
            return;
        }

       
        try {
        	// Update post in database
			theDatabase.updatePostTitle(thePost.getPostId(), newTitle);
			theDatabase.updatePostSubtitle(thePost.getPostId(), newSubtitle);
	        theDatabase.updatePostContent(thePost.getPostId(), newContent);
	        
	        // Update the local post object
	        thePost.updateTitle(newTitle);
	        thePost.updateSubtitle(newSubtitle);
	        thePost.updateContent(newContent);
	        
	        // Refresh display
	        refreshPostContent();
	        cancelPostEdit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        
        
    }

    /**
     * <p>Method: cancelPostEdit()</p>
     *
     * <p>Description: Cancels the post editing mode. Hides editable fields, restores the
     * display labels based on whether subtitle exists, and resets button visibility.</p>
     */
    private static void cancelPostEdit() {

        edit_PostTitle.setVisible(false);
        edit_PostSubtitle.setVisible(false);
        edit_PostContent.setVisible(false);

        // Restore label visibility correctly
        label_PostTitle.setVisible(true);
        label_PostContent.setVisible(true);

        if (thePost.getSubtitle() != null && !thePost.getSubtitle().trim().isEmpty()) {
            label_PostSubtitle.setManaged(true);
            label_PostSubtitle.setVisible(true);
        } else {
            label_PostSubtitle.setManaged(false);
            label_PostSubtitle.setVisible(false);
        }

        button_EditPost.setVisible(true);
        button_SavePost.setVisible(false);
        button_CancelEdit.setVisible(false);
    }
    
    /**
     * <p>Method: showReplyEditor()</p>
     *
     * <p>Description: Displays the reply editor under a post. Inserts the text field and
     * send/cancel buttons at the correct position and hides the unread reply indicator.</p>
     */
    private static void showReplyEditor() {
        int repliesIndex = box_PostArea.getChildren().indexOf(repliesBox);
        if (repliesIndex >= 0) {
            box_PostArea.getChildren().add(repliesIndex, text_reply);
            box_PostArea.getChildren().add(repliesIndex + 1, box_SendCancel);
        }
        box_ReplyUnread.setVisible(false);
    }
    
    /**
	 * <p> Method: void enablePostEdit() </p>
     * <p> Creates a VBox representing a single reply item with content, owner info,
	 * and edit/delete buttons if the reply belongs to the current user.
	 *
	 * @param reply The Reply object containing reply details
	 * @return A VBox containing the formatted reply item
	 */
    private static VBox createReplyItem(Reply reply) {
        VBox replyItem = new VBox(8);
        replyItem.setPadding(new Insets(12));
        replyItem.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        // Display elements
        Label replyContent = new Label(reply.getContent());
        replyContent.setFont(Font.font("Arial", 14));
        replyContent.setWrapText(true);
        replyContent.setMaxWidth(650);

        Label replyInfo = new Label("By: " + reply.getOwner());
        replyInfo.setFont(Font.font("Arial", 12));
        replyInfo.setStyle("-fx-text-fill: #666666;");

        // Edit elements (initially hidden)
        TextArea replyEdit = new TextArea(reply.getContent());
        replyEdit.setFont(Font.font("Arial", 14));
        replyEdit.setPrefWidth(650);
        replyEdit.setPrefHeight(80);
        replyEdit.setWrapText(true);
        replyEdit.setVisible(false);

        replyItem.getChildren().addAll(replyContent, replyEdit, replyInfo);

        // Add edit/delete buttons for replies owned by current user
        if (reply.getOwner().equals(theUser.getUserName())) {
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(5, 0, 0, 0));

            Button editButton = new Button("Edit");
            Button saveButton = new Button("Save");
            Button cancelButton = new Button("Cancel");
            Button deleteButton = new Button("Delete");

            // Setup button styles
            editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

            // Initially hide save/cancel buttons
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
            if (reply.getOwner().compareTo(theUser.getUserName()) != 0) {
            	editButton.setVisible(false);
            	deleteButton.setVisible(false);
            }

            // Edit button action
            editButton.setOnAction(e -> {
                replyContent.setVisible(false);
                replyEdit.setVisible(true);
                editButton.setVisible(false);
                deleteButton.setVisible(false);
                saveButton.setVisible(true);
                cancelButton.setVisible(true);
            });

            // Save button action
            saveButton.setOnAction(e -> {
                String newContent = replyEdit.getText().trim();
                if (!newContent.isEmpty()) {
                	try {
                		theDatabase.updateReplyContent(reply.getReplyId(), newContent);
                		reply.setContent(newContent);
                        replyContent.setText(newContent);
                        
                        // Switch back to display mode
                        replyEdit.setVisible(false);
                        replyContent.setVisible(true);
                        saveButton.setVisible(false);
                        cancelButton.setVisible(false);
                        editButton.setVisible(true);
                        deleteButton.setVisible(true);
            		} catch (SQLException e2) {
            			e2.printStackTrace();
            		}
                }
            });

            // Cancel button action
            cancelButton.setOnAction(e -> {
                replyEdit.setText(reply.getContent()); // Reset text
                replyEdit.setVisible(false);
                replyContent.setVisible(true);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                editButton.setVisible(true);
                deleteButton.setVisible(true);
            });

            // Delete button action
            deleteButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Reply");
                alert.setHeaderText("Are you sure you want to delete this reply?");
                alert.setContentText("This action cannot be undone.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                	ControllerPostPage.deleteReply(reply);
                }
            });

            buttonBox.getChildren().addAll(editButton, saveButton, cancelButton, deleteButton);
            replyItem.getChildren().add(buttonBox);
        }

        return replyItem;
    }

    /**
	 * <p> Method: void enablePostEdit() </p>
     * <p> Description: Refreshes the displayed post content from the current Post object.
	 * Updates title, subtitle, owner, and content labels to reflect any changes. </p>
	 */
    protected static void refreshPostContent() {
        if (thePost != null) {
            label_PostTitle.setText(thePost.getTitle());
            label_PostSubtitle.setText(thePost.getSubtitle());
            label_PostUser.setText("By: " + thePost.getOwner());
            label_PostContent.setText(thePost.getContent());
        }
        
        if (thePost != null && (thePost.getOwner().compareTo(theUser.getUserName()) == 0)) {
            button_EditPost.setVisible(true);
            button_SavePost.setVisible(false);
            button_CancelEdit.setVisible(false);
        }
        else {
        	button_EditPost.setVisible(false);
            button_SavePost.setVisible(false);
            button_CancelEdit.setVisible(false);
        }
    }

    /**
   	 * <p> Method: void refreshReplies() </p>
     * <p> Refreshes the displayed replies from specific reply list. </p>
   	 */
    protected static void refreshReplies() {
    	replies = theDatabase.getRepliesByPostId(thePost.getPostId());
        repliesBox.getChildren().clear();
        
        if (replies != null && !replies.isEmpty()) {
            Label repliesHeader = new Label("Replies (" + replies.size() + ")");
            repliesHeader.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 16));
            repliesBox.getChildren().add(repliesHeader);

            for (Reply reply : replies) {
                VBox replyItem = createReplyItem(reply);
                repliesBox.getChildren().add(replyItem);
            }
        }

        // Ensure the Reply button is present (if the user canceled or after send)
        if (!box_ReplyUnread.isVisible()) {
        	box_ReplyUnread.setVisible(true);
        }

        // Remove reply editor if present
        box_PostArea.getChildren().removeAll(text_reply, box_SendCancel);
        text_reply.clear();
    }

    /**
     * <p> Method: void enablePostEdit() </p>
     * <p> Description: Displays a warning alert dialog with the specified title and content message.
     * Used for validation errors and user notifications throughout the post page. </p>
     *
     * @param title The title text for the alert dialog
     * @param content The main message content to display to the user
     */
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(theStage);
        alert.showAndWait();
    }

    /**********
     * Private local method to initialize the standard fields for a button
     *
     * @param b     The Button object to be initialized
     * @param ff    The font to be used
     * @param f     The size of the font to be used
     * @param w     The width of the Button
     * @param p     The alignment (e.g. left, centered, or right)
     * @param x     The location from the left edge (x axis)
     * @param y     The location from the top (y axis)
     */
    
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}