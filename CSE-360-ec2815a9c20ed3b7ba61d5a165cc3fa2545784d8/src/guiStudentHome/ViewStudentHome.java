package guiStudentHome;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import database.Database;
import entityClasses.Post;
import entityClasses.User;

/**********
 * <p> Method: ViewStudentHome() </p>
 * 
 * <p> Description: This method initializes all the elements of the graphical user interface.
 * This method determines the location, size, font, color, and change and event handlers for
 * each GUI object.</p>
 * 
 */

public class ViewStudentHome {
	
	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI for page
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	
	// GUI for search posts
	protected static ComboBox <String> combobox_Thread = new ComboBox <String>();
	protected static CheckBox checkBox_showUnreadPosts = new CheckBox("Show Unread Posts");
	protected static TextField text_SearchPost = new TextField("");
	protected static Button button_SearchPost = new Button("Search");
	protected static Button button_ClearSearchPost = new Button("Clear");
	
	// GUI for post list
	protected static BorderPane root = new BorderPane();
	protected static VBox postsList;
	protected static VBox header;
	protected static VBox center;
	protected static HBox footer;
	
	// GUI for redirect buttons
	protected static Button button_UpdateThisUser = new Button("Account Update");
	protected static Button button_ViewMyPosts = new Button("My Posts");
	protected static Button button_ViewMyGrades = new Button("My Grades");
	protected static Button button_MakePost = new Button("+ Post");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	// Application state
	private static ViewStudentHome theView;
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static Stage theStage;
	protected static User theUser;
	protected static ArrayList<Post> posts;
	protected static ArrayList<String> threads;
	private static Scene theViewStudentHomeScene;
	protected static final int theRole = 2;

	/**********
	 * <p> Method: displayStudentHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Student Home page to be displayed.</p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStudentHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		posts = theDatabase.getAllPostsNewestFirst();
		threads = theDatabase.getAllThreads();
		threads.add(0, "All");
		
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		theView = new ViewStudentHome();
		if (theView == null); 	// not used, for ignore warning use
		
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
				
		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewStudentHomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewStudentHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 */
	private ViewStudentHome() {
		BorderPane root = new BorderPane();
		
		header = createHeader();
		root.setTop(header);
		
		center = createCenterSection();
		root.setCenter(center);
		
		footer = createFooter();
		root.setBottom(footer);
		
		theViewStudentHomeScene = new Scene(root, width, height);
	}
	
	/**
	 * <p> Method: VBox createHeader() </p>
	 * <p> Description: set up the header for student home page, 
	 * consist of title, user details, update button and my my posts button.</p>
	 * @return the VBox of header
	 */
	private VBox createHeader() {
		VBox header = new VBox(15);
		header.setPadding(new Insets(20));
		
		HBox titleRow = new HBox();
		titleRow.setAlignment(Pos.CENTER_LEFT);
		
		label_PageTitle.setText("Student Home Page");
		label_PageTitle.setFont(Font.font("System", 24));
		
		titleRow.getChildren().add(label_PageTitle);
		
		HBox controlsRow = new HBox();
		controlsRow.setAlignment(Pos.CENTER_LEFT);
		
		label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
		label_UserDetails.setFont(Font.font("System", 16));
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		
		setupButton(button_ViewMyPosts, 120, 35);
		button_ViewMyPosts.setOnAction(event -> 
			ControllerStudentHome.performViewMyPosts());
		
		setupButton(button_UpdateThisUser, 140, 35);
		button_UpdateThisUser.setOnAction(event -> 
			guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));
		
		setupButton(button_ViewMyGrades, 120, 35);
		button_ViewMyGrades.setOnAction(event -> ControllerStudentHome.performViewMyGrades());
		buttonBox.getChildren().addAll(button_ViewMyPosts, button_ViewMyGrades, button_UpdateThisUser);
		
		controlsRow.getChildren().addAll(label_UserDetails, spacer, buttonBox);
		
		header.getChildren().addAll(titleRow, controlsRow);
		return header;
	}
	
	/**
	 * <p> Method: VBox createCenterSection() </p>
	 * <p> Description: set up the center area for student home page, 
	 * consist of search post text field, search and clear search buttons, and make post button.</p>
	 * @return the VBox of center area
	 */
	private VBox createCenterSection() {
		VBox center = new VBox(20);
		center.setPadding(new Insets(20));
		
		VBox searchSection = new VBox(10);
		
		Label searchLabel = new Label("Search Posts");
		searchLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
		
		HBox titleSection = new HBox(20);
		
		setupComboBoxUI(combobox_Thread, "System", 14, 80);
		combobox_Thread.setItems(FXCollections.observableArrayList(threads));
		combobox_Thread.getSelectionModel().selectFirst();
		combobox_Thread.setOnAction((event) -> {ControllerStudentHome.performAllFilters(); });

		titleSection.getChildren().addAll(searchLabel, combobox_Thread);
		
		HBox searchBox = new HBox(5);
		searchBox.setAlignment(Pos.CENTER_LEFT);
		
		text_SearchPost.setPromptText("Enter keywords to search posts...");
		text_SearchPost.setPrefWidth(400);
		text_SearchPost.setFont(Font.font("System", 14));
		
		setupButton(button_SearchPost, 80, 35);
		button_SearchPost.setOnAction(event -> ControllerStudentHome.performAllFilters());
		
		setupButton(button_ClearSearchPost, 80, 35);
		button_ClearSearchPost.setOnAction(event -> ControllerStudentHome.performClear());
		
		setupButton(button_MakePost, 140, 35);
		button_MakePost.setText("Create New Post");
		button_MakePost.setOnAction(event -> 
			ControllerStudentHome.performMakePost());
		
		Region searchSpacer = new Region();
		HBox.setHgrow(searchSpacer, Priority.ALWAYS);
		
		searchBox.getChildren().addAll(text_SearchPost, button_SearchPost, button_ClearSearchPost, searchSpacer, button_MakePost);
		searchSection.getChildren().addAll(titleSection, searchBox);
		
		VBox postsSection = createPostsSection();
		
		center.getChildren().addAll(searchSection, postsSection);
		return center;
	}
	
	/**
	 * <p> Method: VBox createPostsSection() </p>
	 * <p> Description: set up post list area in a scroll pane.</p>
	 * @return the VBox of post list
	 */
	private VBox createPostsSection() {
		VBox postsSection = new VBox(10);
		HBox titleSection = new HBox(10);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		Label postsLabel = new Label("Recent Posts");
		postsLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
		
        checkBox_showUnreadPosts.setFont(Font.font("System", 14));
        checkBox_showUnreadPosts.setOnAction(e -> ControllerStudentHome.performAllFilters());
        
        titleSection.getChildren().addAll(postsLabel, spacer, checkBox_showUnreadPosts);
		
		postsList = new VBox(8);
		postsList.setPadding(new Insets(10));
		populatePostsList();
		
		ScrollPane scrollPane = new ScrollPane(postsList);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefHeight(350);
		
		postsSection.getChildren().addAll(titleSection, scrollPane);
		return postsSection;
	}
	
	/**
	 * <p> Method: VBox createPostItem(Post post) </p>
	 * <p> Description: create all post items from post list.</p>
	 * @param post is the post object to be generated the post GUI item
	 * @return the VBox of post items
	 */
	private static VBox createPostItem(Post post) {
	    VBox postItem = new VBox(8);
	    postItem.setPadding(new Insets(12));
	    postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

	    // Content section that handles click to view post
	    VBox contentSection = new VBox(5);

	    Label titleLabel = new Label(post.getTitle());
	    titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
	    titleLabel.setWrapText(true);

	    HBox infoRow = new HBox();
	    infoRow.setAlignment(Pos.CENTER_LEFT);

	    VBox labelBox = new VBox(10);
	    
	    Label tagsLabel = new Label("Tags: " + post.getTagsString());
	    
	    Label authorLabel = new Label("By: " + post.getOwner());
	    authorLabel.setFont(Font.font("System", 12));

	    labelBox.getChildren().addAll(authorLabel, tagsLabel);
	    
	    Region infoSpacer = new Region();
	    HBox.setHgrow(infoSpacer, Priority.ALWAYS);

	    VBox statsBox = new VBox(2);
	    statsBox.setAlignment(Pos.CENTER_RIGHT);

	    String threadStatus = theDatabase.getPostThread(post.getPostId());
	    Label threadStatusLabel = new Label("Thread: " + threadStatus);
	    threadStatusLabel.setFont(Font.font("System", 11));
	    
	    String readStatus = theDatabase.hasUserReadPost(theUser.getUserName(), post.getPostId());
	    Label readStatusLabel = new Label("Status: " + readStatus);
	    readStatusLabel.setFont(Font.font("System", 11));

	    int totalReplies = theDatabase.getPostNumberOfReplies(post.getPostId());
	    int unreadReplies = theDatabase.getNumberOfUnreadReplies(post.getPostId(), theUser.getUserName());
	    Label repliesLabel = new Label(totalReplies + " replies, " + unreadReplies + " unread");
	    repliesLabel.setFont(Font.font("System", 11));

	    statsBox.getChildren().addAll(threadStatusLabel, readStatusLabel, repliesLabel);

	    infoRow.getChildren().addAll(labelBox, infoSpacer, statsBox);
	    contentSection.getChildren().addAll(titleLabel, infoRow);

	    postItem.getChildren().add(contentSection);

	    // Click handler for viewing post (only on content, not delete button)
	    contentSection.setOnMouseClicked(e -> guiPostPage.ViewPostPage.displayPostPage(theStage, theUser, post));

	    // Hover effects
	    postItem.setOnMouseEntered(e -> 
	        postItem.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: #f5f5f5;"));
	    postItem.setOnMouseExited(e -> 
	        postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;"));

	    return postItem;
	}

	/**
	 * <p> Method: HBox createFooter() </p>
	 * <p> Description: create footer for Student Home page,
	 * consist of logout and quit buttons.</p>
	 * @return the HBox of buttons
	 */
	private HBox createFooter() {
		HBox footer = new HBox(15);
		footer.setPadding(new Insets(15, 20, 20, 20));
		footer.setAlignment(Pos.CENTER_RIGHT);
		
		setupButton(button_Logout, 100, 35);
		button_Logout.setOnAction(event -> 
			ControllerStudentHome.performLogout());
		
		setupButton(button_Quit, 80, 35);
		button_Quit.setOnAction(event -> 
			ControllerStudentHome.performQuit());
		
		footer.getChildren().addAll(button_Logout, button_Quit);
		return footer;
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	/**
	 * <p> Method: void populatePostsList() </p>
	 * <p> Description: helper method for createPostItem() to loop through posts.</p>
	 */
	private static void populatePostsList() {
		postsList.getChildren().clear();
		if (posts != null && posts.size() > 0) {
			for (Post post : posts) {
				postsList.getChildren().add(createPostItem(post));
			}
		} else {
			Label noPostsLabel = new Label("No posts available");
			noPostsLabel.setFont(Font.font("System", 14));
			noPostsLabel.setPadding(new Insets(20));
			postsList.getChildren().add(noPostsLabel);
		}
	}

	/**
	 * <p> Method: void refreshPostsDisplay() </p>
	 * <p> Description: helper method refresh the post list area.</p>
	 */
	protected static void refreshPostsDisplay() {
		populatePostsList();
	}

	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param width The width of the Button
	 * @param height The height of the Button
	 */
	private void setupButton(Button button, double width, double height) {
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setFont(Font.font("System", 13));
	}
	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
	}
}
