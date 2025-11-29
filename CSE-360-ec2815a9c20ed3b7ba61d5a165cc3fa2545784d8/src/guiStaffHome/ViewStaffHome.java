package guiStaffHome;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import java.util.ArrayList;
import javafx.scene.control.Alert.AlertType;


/*******
 * <p> Title: ViewStaffHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Staff Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewStaffHome {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
		
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: Discussion Posts and Replies Management
	protected static Label label_PostsTitle = new Label("Discussion Posts");
	protected static TextField text_SearchPost = new TextField("");
	protected static Button button_SearchPost = new Button("Search");
	protected static Button button_ClearSearch = new Button("Clear");
	protected static ScrollPane scrollPane_Posts = new ScrollPane();
	protected static VBox vbox_PostsList = new VBox(10);
	protected static Button button_CreateNewPost = new Button("Create New Post");
	protected static Button button_RefreshPosts = new Button("Refresh");
	protected static Button button_ReleaseGrades = new Button("Release Grades");
	protected static Button button_ViewAllGrades = new Button("View Grades");
	
	// Details panel (shown when viewing a specific post)
	protected static VBox vbox_PostDetails = new VBox(10);
	protected static ScrollPane scrollPane_PostDetails = new ScrollPane();
	protected static Button button_BackToList = new Button("Back to List");
	
	
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStaffHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	
	private static Scene theStaffHomeScene;		// The shared Scene each invocation populates
	protected static final int theRole = 3;		// Admin: 1; Role1: 2; Staff: 3

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayStaffHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Staff Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStaffHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewStaffHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());// Set the username

		// Load posts after user is initialized
		ControllerStaffHome.loadAllPosts();

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theStaffHomeScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: ViewStaffHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayStaffHome method.</p>
	 * 
	 */
	private ViewStaffHome() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffHomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// GUI Area 1 - Header Section
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "System", 24, width, Pos.CENTER, 0, 20);
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "System", 16, width-200, Pos.BASELINE_LEFT, 20, 60);
		
		setupButtonUI(button_UpdateThisUser, "System", 14, 140, Pos.CENTER, 640, 55);
		button_UpdateThisUser.setOnAction((event) -> {ControllerStaffHome.performUpdate(); });
		
		// GUI Area 2 - Discussion Posts Management
		setupLabelUI(label_PostsTitle, "System", 16, 200, Pos.CENTER_LEFT, 20, 105);
		
		// Setup search functionality
		text_SearchPost.setPromptText("Search posts by title...");
		text_SearchPost.setLayoutX(220);
		text_SearchPost.setLayoutY(103);
		text_SearchPost.setPrefWidth(250);
		text_SearchPost.setFont(Font.font("System", 13));
		
		setupButtonUI(button_SearchPost, "System", 13, 70, Pos.CENTER, 480, 100);
		button_SearchPost.setOnAction((event) -> {ControllerStaffHome.performSearch(); });
		
		setupButtonUI(button_ClearSearch, "System", 13, 70, Pos.CENTER, 560, 100);
		button_ClearSearch.setOnAction((event) -> {ControllerStaffHome.performClearSearch(); });
		
		// Setup action buttons at top right
		setupButtonUI(button_CreateNewPost, "System", 14, 130, Pos.CENTER, 640, 100);
		button_CreateNewPost.setText("+ New Post");
		button_CreateNewPost.setOnAction((event) -> {showCreatePostDialog(); });

		// Place grade control buttons near Account Update to avoid overlap
		setupButtonUI(button_ReleaseGrades, "System", 12, 110, Pos.CENTER, 500, 55);
		button_ReleaseGrades.setOnAction(e -> ControllerStaffHome.performReleaseGrades());

		setupButtonUI(button_ViewAllGrades, "System", 12, 110, Pos.CENTER, 380, 55);
		button_ViewAllGrades.setOnAction(e -> ControllerStaffHome.performViewAllGrades());
		
		setupButtonUI(button_RefreshPosts, "System", 14, 80, Pos.CENTER, 640, 100);
		button_RefreshPosts.setOnAction((event) -> {ControllerStaffHome.loadAllPosts(); });
		button_RefreshPosts.setVisible(false);
		
		setupButtonUI(button_BackToList, "System", 14, 120, Pos.CENTER, 640, 100);
		button_BackToList.setOnAction((event) -> {showPostsList(); });
		button_BackToList.setVisible(false);
		
		// Setup posts list scroll pane
		scrollPane_Posts.setLayoutX(20);
		scrollPane_Posts.setLayoutY(135);
		scrollPane_Posts.setPrefWidth(width - 40);
		scrollPane_Posts.setPrefHeight(360);
		scrollPane_Posts.setContent(vbox_PostsList);
		scrollPane_Posts.setFitToWidth(true);
		vbox_PostsList.setPadding(new Insets(10));
		vbox_PostsList.setSpacing(8);
		
		// Setup post details scroll pane (hidden by default)
		scrollPane_PostDetails.setLayoutX(20);
		scrollPane_PostDetails.setLayoutY(135);
		scrollPane_PostDetails.setPrefWidth(width - 40);
		scrollPane_PostDetails.setPrefHeight(380);
		scrollPane_PostDetails.setContent(vbox_PostDetails);
		scrollPane_PostDetails.setFitToWidth(true);
		scrollPane_PostDetails.setVisible(false);
		vbox_PostDetails.setPadding(new Insets(10));
		vbox_PostDetails.setSpacing(8);
		
		
		// GUI Area 3 - Logout and Quit buttons (positioned below the dark separator line at Y=525)
        setupButtonUI(button_Logout, "System", 14, 100, Pos.CENTER, 580, 535);
        button_Logout.setOnAction((event) -> {ControllerStaffHome.performLogout(); });
        
        setupButtonUI(button_Quit, "System", 14, 80, Pos.CENTER, 690, 535);
        button_Quit.setOnAction((event) -> {ControllerStaffHome.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
			theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			label_PostsTitle, text_SearchPost, button_SearchPost, button_ClearSearch,
			button_ViewAllGrades, button_ReleaseGrades,
			scrollPane_Posts, scrollPane_PostDetails, 
			button_CreateNewPost, button_RefreshPosts, button_BackToList,
		        line_Separator4, button_Logout, button_Quit);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/*-********************************************************************************************

	Posts and Replies Display Methods

	 */
	
	/**
	 * Display a list of posts in the main view
	 */
	protected static void displayPosts(ArrayList<Post> posts) {
		vbox_PostsList.getChildren().clear();
		
		if (posts.isEmpty()) {
			Label emptyLabel = new Label("No posts available");
			emptyLabel.setFont(Font.font("Arial", 16));
			vbox_PostsList.getChildren().add(emptyLabel);
			return;
		}
		
		for (Post post : posts) {
			VBox postBox = createPostCard(post);
			vbox_PostsList.getChildren().add(postBox);
		}
		
		showPostsList();
	}
	
	/**
	 * Create a card UI for a single post in the list
	 */
	private static VBox createPostCard(Post post) {
		VBox card = new VBox(8);
		card.setPadding(new Insets(12));
		card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
		
		// Content section that handles click to view post
		VBox contentSection = new VBox(5);
		
		// Post title
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
		titleLabel.setWrapText(true);
		
		// Post info row
		HBox infoRow = new HBox(10);
		infoRow.setAlignment(Pos.CENTER_LEFT);
		
		VBox labelBox = new VBox(5);
		
		Label ownerLabel = new Label("By: " + post.getOwner());
		ownerLabel.setFont(Font.font("System", 12));
		
		// Content preview
		String contentPreview = post.getContent();
		if (contentPreview.length() > 80) {
			contentPreview = contentPreview.substring(0, 80) + "...";
		}
		Label contentLabel = new Label(contentPreview);
		contentLabel.setFont(Font.font("System", 11));
		contentLabel.setWrapText(true);
		
		labelBox.getChildren().addAll(ownerLabel, contentLabel);
		
		infoRow.getChildren().add(labelBox);
		
		contentSection.getChildren().addAll(titleLabel, infoRow);
		
		// Action buttons row
		HBox buttonBox = new HBox(8);
		buttonBox.setPadding(new Insets(5, 0, 0, 0));
		
		Button editButton = new Button("Edit");
		editButton.setFont(Font.font("System", 11));
		editButton.setPrefWidth(60);
		editButton.setPrefHeight(25);
		editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		editButton.setOnAction(e -> {
			e.consume(); // Prevent triggering post click
			showEditPostDialog(post);
		});
		
		Button deleteButton = new Button("Delete");
		deleteButton.setFont(Font.font("System", 11));
		deleteButton.setPrefWidth(60);
		deleteButton.setPrefHeight(25);
		deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
		deleteButton.setOnAction(e -> {
			e.consume(); // Prevent triggering post click
			if (confirmDelete("Are you sure you want to delete this post?")) {
				ControllerStaffHome.deletePost(post.getPostId());
			}
		});
		
		// Grading button (only for staff viewing student posts)
		if (theDatabase.isUserStudent(post.getOwner())) {
			String gradeLabel = (post.getGrade() == null ? "Grade" : "Edit Grade");
			Button gradeButton = new Button(gradeLabel);
			gradeButton.setFont(Font.font("System", 11));
			gradeButton.setPrefWidth(80);
			gradeButton.setPrefHeight(25);
			gradeButton.setStyle("-fx-background-color: #673AB7; -fx-text-fill: white;");
			gradeButton.setOnAction(e -> {
				e.consume();
				showGradeDialog(post);
			});
			buttonBox.getChildren().addAll(editButton, deleteButton, gradeButton);
		} else {
			buttonBox.getChildren().addAll(editButton, deleteButton);
		}
		
		card.getChildren().addAll(contentSection, buttonBox);
		
		// Click handler for viewing post (only on content section, not buttons)
		contentSection.setOnMouseClicked(e -> ControllerStaffHome.viewPost(post.getPostId()));
		
		// Hover effect
		card.setOnMouseEntered(e -> 
			card.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: #f5f5f5;"));
		card.setOnMouseExited(e -> 
			card.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;"));
		
		return card;
	}
	
	/**
	 * Display detailed view of a post with its replies
	 */
	protected static void displayPostDetails(Post post, ArrayList<Reply> replies) {
		vbox_PostDetails.getChildren().clear();
		
		if (post == null) {
			Label errorLabel = new Label("Post not found");
			errorLabel.setFont(Font.font("System", 16));
			vbox_PostDetails.getChildren().add(errorLabel);
			return;
		}
		
		// Post details card with improved layout
		VBox postDetailsBox = new VBox(12);
		postDetailsBox.setPadding(new Insets(20));
		postDetailsBox.setStyle("-fx-border-color: black; -fx-border-width: 2;");
		
		// Title at the top - bold and larger
		Label titleLabel = new Label(post.getTitle());
		titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
		titleLabel.setWrapText(true);
		titleLabel.setStyle("-fx-text-fill: #1a1a1a;");
		
		postDetailsBox.getChildren().add(titleLabel);
		
		// Subtitle below title if exists
		if (post.getSubtitle() != null && !post.getSubtitle().isEmpty()) {
			Label subtitleLabel = new Label(post.getSubtitle());
			subtitleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
			subtitleLabel.setWrapText(true);
			subtitleLabel.setStyle("-fx-text-fill: #555555;");
			postDetailsBox.getChildren().add(subtitleLabel);
		}
		
		// Owner info
		Label ownerLabel = new Label("Posted by: " + post.getOwner());
		ownerLabel.setFont(Font.font("System", 12));
		ownerLabel.setStyle("-fx-text-fill: #666666;");
		postDetailsBox.getChildren().add(ownerLabel);
		
		// Separator line
		Label separator = new Label("━".repeat(90));
		separator.setStyle("-fx-text-fill: #cccccc;");
		postDetailsBox.getChildren().add(separator);
		
		// Body/Content after separator
		Label contentLabel = new Label(post.getContent());
		contentLabel.setFont(Font.font("System", 14));
		contentLabel.setWrapText(true);
		contentLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-line-spacing: 0.2em;");
		postDetailsBox.getChildren().add(contentLabel);
		
		// Grade/Edit Grade button for student post
		if (post != null && theDatabase.isUserStudent(post.getOwner())) {
			Button gradeButton = new Button(post.getGrade()==null?"Grade":"Edit Grade");
			gradeButton.setFont(Font.font("System", 11));
			gradeButton.setPrefWidth(90);
			gradeButton.setPrefHeight(25);
			gradeButton.setStyle("-fx-background-color: #673AB7; -fx-text-fill: white;");
			gradeButton.setOnAction(e -> { showGradeDialog(post); });
			postDetailsBox.getChildren().add(gradeButton);
		}

		// Replies section header
		HBox repliesHeader = new HBox(10);
		repliesHeader.setPadding(new Insets(15, 0, 10, 0));
		
		Label repliesLabel = new Label("Replies (" + replies.size() + ")");
		repliesLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
		
		Button addReplyButton = new Button("Add Reply");
		addReplyButton.setFont(Font.font("System", 11));
		addReplyButton.setPrefWidth(80);
		addReplyButton.setPrefHeight(25);
		addReplyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		
		repliesHeader.getChildren().addAll(repliesLabel, addReplyButton);
		
		vbox_PostDetails.getChildren().addAll(postDetailsBox, repliesHeader);
		
		// Inline reply input area (hidden by default)
		VBox replyInputBox = new VBox(8);
		replyInputBox.setPadding(new Insets(10));
		replyInputBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
		replyInputBox.setVisible(false);
		replyInputBox.setManaged(false);
		
		Label replyPrompt = new Label("Add your reply:");
		replyPrompt.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
		
		TextArea replyTextArea = new TextArea();
		replyTextArea.setPromptText("Write your reply here...");
		replyTextArea.setPrefRowCount(3);
		replyTextArea.setWrapText(true);
		replyTextArea.setFont(Font.font("System", 12));
		
		HBox replyButtonBox = new HBox(8);
		Button submitReplyButton = new Button("Submit");
		submitReplyButton.setFont(Font.font("System", 11));
		submitReplyButton.setPrefWidth(70);
		submitReplyButton.setPrefHeight(25);
		submitReplyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		submitReplyButton.setOnAction(e -> {
			String content = replyTextArea.getText();
			if (content != null && !content.trim().isEmpty()) {
				ControllerStaffHome.createReply(post.getPostId(), content, theUser.getUserName());
				replyTextArea.clear();
				replyInputBox.setVisible(false);
				replyInputBox.setManaged(false);
			}
		});
		
		Button cancelReplyButton = new Button("Cancel");
		cancelReplyButton.setFont(Font.font("System", 11));
		cancelReplyButton.setPrefWidth(70);
		cancelReplyButton.setPrefHeight(25);
		cancelReplyButton.setOnAction(e -> {
			replyTextArea.clear();
			replyInputBox.setVisible(false);
			replyInputBox.setManaged(false);
		});
		
		replyButtonBox.getChildren().addAll(submitReplyButton, cancelReplyButton);
		replyInputBox.getChildren().addAll(replyPrompt, replyTextArea, replyButtonBox);
		
		// Add Reply button action to show the input box
		addReplyButton.setOnAction(e -> {
			replyInputBox.setVisible(true);
			replyInputBox.setManaged(true);
		});
		
		vbox_PostDetails.getChildren().add(replyInputBox);
		
		// Display each reply
		if (replies.isEmpty()) {
			Label noRepliesLabel = new Label("No replies yet.");
			noRepliesLabel.setFont(Font.font("System", 12));
			noRepliesLabel.setStyle("-fx-text-fill: #888888;");
			noRepliesLabel.setPadding(new Insets(10, 0, 0, 10));
			vbox_PostDetails.getChildren().add(noRepliesLabel);
		} else {
			for (Reply reply : replies) {
				VBox replyBox = createReplyCard(reply, post.getPostId());
				vbox_PostDetails.getChildren().add(replyBox);
			}
		}
		
		showPostDetails();
	}
	
	/**
	 * Create a card UI for a single reply
	 */
	private static VBox createReplyCard(Reply reply, int postId) {
		VBox card = new VBox(8);
		card.setPadding(new Insets(10));
		card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
		
		Label ownerLabel = new Label(reply.getOwner() + " replied:");
		ownerLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
		
		// Content label (shown by default)
		Label contentLabel = new Label(reply.getContent());
		contentLabel.setFont(Font.font("System", 12));
		contentLabel.setWrapText(true);
		
		// Edit text area (hidden by default)
		TextArea editTextArea = new TextArea(reply.getContent());
		editTextArea.setPrefRowCount(3);
		editTextArea.setWrapText(true);
		editTextArea.setFont(Font.font("System", 12));
		editTextArea.setVisible(false);
		editTextArea.setManaged(false);
		
		HBox buttonBox = new HBox(8);
		buttonBox.setPadding(new Insets(5, 0, 0, 0));
		
		Button editButton = new Button("Edit");
		editButton.setFont(Font.font("System", 11));
		editButton.setPrefWidth(60);
		editButton.setPrefHeight(25);
		editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		
		Button saveButton = new Button("Save");
		saveButton.setFont(Font.font("System", 11));
		saveButton.setPrefWidth(60);
		saveButton.setPrefHeight(25);
		saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
		saveButton.setVisible(false);
		saveButton.setManaged(false);
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setFont(Font.font("System", 11));
		cancelButton.setPrefWidth(60);
		cancelButton.setPrefHeight(25);
		cancelButton.setVisible(false);
		cancelButton.setManaged(false);
		
		editButton.setOnAction(e -> {
			// Switch to edit mode
			contentLabel.setVisible(false);
			contentLabel.setManaged(false);
			editTextArea.setVisible(true);
			editTextArea.setManaged(true);
			editButton.setVisible(false);
			editButton.setManaged(false);
			saveButton.setVisible(true);
			saveButton.setManaged(true);
			cancelButton.setVisible(true);
			cancelButton.setManaged(true);
		});
		
		saveButton.setOnAction(e -> {
			String newContent = editTextArea.getText();
			if (newContent != null && !newContent.trim().isEmpty()) {
				ControllerStaffHome.updateReply(reply.getReplyId(), postId, newContent);
			}
		});
		
		cancelButton.setOnAction(e -> {
			// Switch back to view mode
			editTextArea.setText(reply.getContent());
			contentLabel.setVisible(true);
			contentLabel.setManaged(true);
			editTextArea.setVisible(false);
			editTextArea.setManaged(false);
			editButton.setVisible(true);
			editButton.setManaged(true);
			saveButton.setVisible(false);
			saveButton.setManaged(false);
			cancelButton.setVisible(false);
			cancelButton.setManaged(false);
		});
		
		Button deleteButton = new Button("Delete");
		deleteButton.setFont(Font.font("System", 11));
		deleteButton.setPrefWidth(60);
		deleteButton.setPrefHeight(25);
		deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
		deleteButton.setOnAction(e -> {
			if (confirmDelete("Are you sure you want to delete this reply?")) {
				ControllerStaffHome.deleteReply(reply.getReplyId(), postId);
			}
		});
		
		buttonBox.getChildren().addAll(editButton, saveButton, cancelButton, deleteButton);
		
		card.getChildren().addAll(ownerLabel, contentLabel, editTextArea, buttonBox);
		
		return card;
	}
	
	/**
	 * Show the posts list view
	 */
	private static void showPostsList() {
		scrollPane_Posts.setVisible(true);
		scrollPane_PostDetails.setVisible(false);
		text_SearchPost.setVisible(true);
		button_SearchPost.setVisible(true);
		button_ClearSearch.setVisible(true);
		button_CreateNewPost.setVisible(true);
		button_RefreshPosts.setVisible(false);
		button_BackToList.setVisible(false);
	}
	
	/**
	 * Show the post details view
	 */
	private static void showPostDetails() {
		scrollPane_Posts.setVisible(false);
		scrollPane_PostDetails.setVisible(true);
		text_SearchPost.setVisible(false);
		button_SearchPost.setVisible(false);
		button_ClearSearch.setVisible(false);
		button_CreateNewPost.setVisible(false);
		button_RefreshPosts.setVisible(true);
		button_BackToList.setVisible(true);
	}
	
	/*-********************************************************************************************

	Dialog Methods for Creating and Editing

	 */
	
	/**
	 * Show dialog for creating a new post
	 */
	private static void showCreatePostDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create New Post");
		dialog.setHeaderText("Enter post details:");
		
		// Create form fields
		Label titleLbl = new Label("Title: *");
		titleLbl.setFont(Font.font("System", 12));
		TextField titleField = new TextField();
		titleField.setPromptText("Enter a descriptive title (required)");
		titleField.setFont(Font.font("System", 12));
		
		Label subtitleLbl = new Label("Subtitle:");
		subtitleLbl.setFont(Font.font("System", 12));
		TextField subtitleField = new TextField();
		subtitleField.setPromptText("Optional subtitle");
		subtitleField.setFont(Font.font("System", 12));
		
		Label contentLbl = new Label("Content: *");
		contentLbl.setFont(Font.font("System", 12));
		TextArea contentArea = new TextArea();
		contentArea.setPromptText("Enter your post content (required)");
		contentArea.setPrefRowCount(5);
		contentArea.setWrapText(true);
		contentArea.setFont(Font.font("System", 12));
		
		Label tagsLbl = new Label("Tags:");
		tagsLbl.setFont(Font.font("System", 12));
		TextField tagsField = new TextField();
		tagsField.setPromptText("Comma-separated tags (optional)");
		tagsField.setFont(Font.font("System", 12));
		
		Label threadLbl = new Label("Thread/Category:");
		threadLbl.setFont(Font.font("System", 12));
		TextField threadField = new TextField();
		threadField.setPromptText("Thread or category name");
		threadField.setText("General");
		threadField.setFont(Font.font("System", 12));
		
		VBox content = new VBox(8);
		content.getChildren().addAll(
			titleLbl, titleField,
			subtitleLbl, subtitleField,
			contentLbl, contentArea,
			tagsLbl, tagsField,
			threadLbl, threadField
		);
		content.setPadding(new Insets(15));
		
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().setPrefWidth(450);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				String title = titleField.getText();
				String subtitle = subtitleField.getText();
				String contentText = contentArea.getText();
				String thread = threadField.getText();
				
				ArrayList<String> tags = new ArrayList<>();
				if (!tagsField.getText().trim().isEmpty()) {
					String[] tagArray = tagsField.getText().split(",");
					for (String tag : tagArray) {
						tags.add(tag.trim());
					}
				}
				
				ControllerStaffHome.createNewPost(title, subtitle, contentText, 
					theUser.getUserName(), tags, thread);
			}
		});
	}
	
	/**
	 * Show dialog for editing an existing post
	 */
	private static void showEditPostDialog(Post post) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Post");
		dialog.setHeaderText("Edit post details:");
		
		// Create form fields with existing values
		Label titleLbl = new Label("Title: *");
		titleLbl.setFont(Font.font("System", 12));
		TextField titleField = new TextField(post.getTitle());
		titleField.setFont(Font.font("System", 12));
		
		Label subtitleLbl = new Label("Subtitle:");
		subtitleLbl.setFont(Font.font("System", 12));
		TextField subtitleField = new TextField(post.getSubtitle() != null ? post.getSubtitle() : "");
		subtitleField.setFont(Font.font("System", 12));
		
		Label contentLbl = new Label("Content: *");
		contentLbl.setFont(Font.font("System", 12));
		TextArea contentArea = new TextArea(post.getContent());
		contentArea.setPrefRowCount(5);
		contentArea.setWrapText(true);
		contentArea.setFont(Font.font("System", 12));
		
		VBox content = new VBox(8);
		content.getChildren().addAll(
			titleLbl, titleField,
			subtitleLbl, subtitleField,
			contentLbl, contentArea
		);
		content.setPadding(new Insets(15));
		
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().setPrefWidth(450);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				ControllerStaffHome.updatePost(post.getPostId(), 
					titleField.getText(), 
					subtitleField.getText(), 
					contentArea.getText());
			}
		});
	}

	/** Dialog for assigning/editing grade */
	private static void showGradeDialog(Post post) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle((post.getGrade()==null?"Grade Post":"Edit Grade"));
		dialog.setHeaderText("Assign grade and feedback:");

		Label gradeLbl = new Label("Grade (e.g. 95 or A): *");
		TextField gradeField = new TextField(post.getGrade()==null?"":post.getGrade());
		gradeField.setFont(Font.font("System", 12));

		Label feedbackLbl = new Label("Feedback:");
		TextArea feedbackArea = new TextArea(post.getFeedback()==null?"":post.getFeedback());
		feedbackArea.setPrefRowCount(4);
		feedbackArea.setWrapText(true);

		VBox content = new VBox(8);
		content.getChildren().addAll(gradeLbl, gradeField, feedbackLbl, feedbackArea);
		content.setPadding(new Insets(12));

		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				ControllerStaffHome.gradePost(post.getPostId(), gradeField.getText(), feedbackArea.getText(), theUser.getUserName());
			}
		});
	}
	
	/**
	 * Show dialog for creating a new reply
	 */
	private static void showCreateReplyDialog(int postId) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Add Reply");
		dialog.setHeaderText("Enter your reply:");
		
		Label contentLbl = new Label("Your Reply: *");
		contentLbl.setFont(Font.font("System", 12));
		
		TextArea contentArea = new TextArea();
		contentArea.setPromptText("Write your reply here (required)");
		contentArea.setPrefRowCount(4);
		contentArea.setWrapText(true);
		contentArea.setFont(Font.font("System", 12));
		
		VBox content = new VBox(8);
		content.getChildren().addAll(contentLbl, contentArea);
		content.setPadding(new Insets(15));
		
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().setPrefWidth(400);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				ControllerStaffHome.createReply(postId, contentArea.getText(), 
					theUser.getUserName());
			}
		});
	}
	
	/**
	 * Show dialog for editing an existing reply
	 */
	private static void showEditReplyDialog(Reply reply, int postId) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Edit Reply");
		dialog.setHeaderText("Edit your reply:");
		
		Label contentLbl = new Label("Reply Content: *");
		contentLbl.setFont(Font.font("System", 12));
		
		TextArea contentArea = new TextArea(reply.getContent());
		contentArea.setPrefRowCount(4);
		contentArea.setWrapText(true);
		contentArea.setFont(Font.font("System", 12));
		
		VBox content = new VBox(8);
		content.getChildren().addAll(contentLbl, contentArea);
		content.setPadding(new Insets(15));
		
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().setPrefWidth(400);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		dialog.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				ControllerStaffHome.updateReply(reply.getReplyId(), postId, 
					contentArea.getText());
			}
		});
	}
	
	/**
	 * Show confirmation dialog for delete actions
	 */
	private static boolean confirmDelete(String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setHeaderText(null);
		alert.setContentText(message);
		
		return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
	}
}
