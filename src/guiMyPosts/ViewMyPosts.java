package guiMyPosts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
 * <p> Method: ViewMyPosts() </p>
 * 
 * <p> Description: This method initializes all the elements of the graphical user interface.
 * This method determines the location, size, font, color, and change and event handlers for
 * each GUI object.</p>
 * 
 */
public class ViewMyPosts {

	// These are the application values required by the user interface
	/** Application window width obtained from the main application. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Application window height obtained from the main application. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI widgets
	/** Label displaying the title of the page ("My Posts"). */
	protected static Label label_PageTitle = new Label();

	/** Button that navigates the user back to their home page. */
	protected static Button button_Return = new Button("Return");

	/** Button that logs the user out of the application. */
	protected static Button button_Logout = new Button("Logout");

	/** Button that quits the application entirely. */
	protected static Button button_Quit = new Button("Quit");

	// GUI for post list area
	/** VBox container for all posts-related content, including scrollable list. */
	protected static VBox postsSection = new VBox(10);

	/** VBox that holds the list of post items for display. */
	protected static VBox postsList = new VBox(8);

	// Application state
	/** Singleton instance of the current ViewMyPosts page. */
	private static ViewMyPosts theView;

	/** Reference to the application's database for retrieving posts and user info. */
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX stage where this page will be displayed. */
	protected static Stage theStage;

	/** Currently logged-in user whose posts are displayed. */
	protected static User theUser;

	/** List of posts retrieved for the current user, sorted newest first. */
	protected static ArrayList<Post> posts;

	/** Scene object for the "My Posts" page. */
	private static Scene theViewMyPostsScene;

	/** Role identifier of the current user (used for UI logic). */
	protected static int theRole;


    /**
     * <p> Method: void displayMyPosts(Stage ps, User user) </p>
     * <p> Description: Builds and displays the "My Posts" page. Loads the user's posts,
     * creates the UI scene, and shows it in the given stage. </p>
     *
     * @param ps The JavaFX stage to use
     * @param user The current logged-in user
     */
    public static void displayMyPosts(Stage ps, User user) {

        theStage = ps;
        theUser = user;
        theRole = applicationMain.FoundationsMain.activeHomePage;

        posts = theDatabase.getPostsFromUserNewestFirst(user.getUserName());

        System.out.println("All posts got: " + posts.size());

        // Construct UI first so scene gets initialized
        theView = new ViewMyPosts();
        if (theView == null);

        theDatabase.getUserAccountDetails(user.getUserName());

        theStage.setTitle("My Posts");
        theStage.setScene(theViewMyPostsScene);
        theStage.show();
    }

    /**
     * <p> Method: ViewMyPosts() </p>
     * <p> Description: Private constructor that builds all GUI elements and layout
     * for the My Posts page. Creates the scene for displayMyPosts(). </p>
     */
    private ViewMyPosts() {
        BorderPane root = new BorderPane();

        label_PageTitle.setText("My Posts");
        label_PageTitle.setFont(Font.font("System", 24));
        BorderPane.setAlignment(label_PageTitle, Pos.CENTER);
        root.setTop(label_PageTitle);

        postsList.setPadding(new Insets(10));
        populatePostsList();

        ScrollPane scrollPane = new ScrollPane(postsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        postsSection.getChildren().clear();
        postsSection.getChildren().add(scrollPane);
        root.setCenter(postsSection);

        HBox footer = createFooter();
        root.setBottom(footer);

        theViewMyPostsScene = new Scene(root, width, height);
    }

    /**
     * <p> Method: VBox createPostItem(Post post) </p>
     * <p> Description: Builds a post item UI including post title, owner,
     * reply counts, unread info, and a delete button. </p>
     *
     * @param post The post to visually display
     * @return VBox containing formatted UI for one post
     */
    private static VBox createPostItem(Post post) {

        VBox postItem = new VBox(8);
        postItem.setPadding(new Insets(12));
        postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;");

        VBox contentSection = new VBox(5);

        // Title
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        titleLabel.setWrapText(true);

        // Post information row
        HBox infoRow = new HBox();
        infoRow.setAlignment(Pos.CENTER_LEFT);

        Label authorLabel = new Label("By: " + post.getOwner());
        authorLabel.setFont(Font.font("System", 12));

        Region infoSpacer = new Region();
        HBox.setHgrow(infoSpacer, Priority.ALWAYS);

        VBox statsBox = new VBox(2);
        statsBox.setAlignment(Pos.CENTER_RIGHT);

        String readStatus = theDatabase.hasUserReadPost(theUser.getUserName(), post.getPostId());
        Label statusLabel = new Label("Status: " + readStatus);
        statusLabel.setFont(Font.font("System", 11));

        int totalReplies = theDatabase.getPostNumberOfReplies(post.getPostId());
        int unreadReplies = theDatabase.getNumberOfUnreadReplies(post.getPostId(), theUser.getUserName());
        Label repliesLabel = new Label(totalReplies + " replies, " + unreadReplies + " unread");
        repliesLabel.setFont(Font.font("System", 11));

        statsBox.getChildren().addAll(statusLabel, repliesLabel);
        infoRow.getChildren().addAll(authorLabel, infoSpacer, statsBox);

        // Action row (Delete)
        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(5, 0, 0, 0));

        Button deleteButton = new Button("Delete");
        deleteButton.setFont(Font.font("System", 11));
        deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        deleteButton.setPrefWidth(60);
        deleteButton.setPrefHeight(25);

        deleteButton.setOnAction(e -> {
            e.consume();
            ControllerMyPosts.performDeletePost(post);
        });

        actionRow.getChildren().add(deleteButton);

        // Add formatted layout sections
        contentSection.getChildren().addAll(titleLabel, infoRow, actionRow);
        postItem.getChildren().add(contentSection);
        contentSection.setOnMouseClicked(e -> guiPostPage.ViewPostPage.displayPostPage(theStage, theUser, post));
        
        // Hover effect
        postItem.setOnMouseEntered(e ->
            postItem.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: #f5f5f5;"));
        postItem.setOnMouseExited(e ->
            postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;"));

        return postItem;
    }

    /**
     * <p> Method: HBox createFooter() </p>
     * <p> Description: Builds lower section containing Return, Logout, Quit buttons. </p>
     *
     * @return Configured HBox for footer buttons
     */
    private HBox createFooter() {
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15, 20, 20, 20));
        footer.setAlignment(Pos.CENTER_RIGHT);

        setupButton(button_Return, 100, 35);
        button_Return.setOnAction(event -> ControllerMyPosts.goToUserHomePage(theStage, theUser));

        setupButton(button_Logout, 100, 35);
        button_Logout.setOnAction(event -> ControllerMyPosts.performLogout());

        setupButton(button_Quit, 80, 35);
        button_Quit.setOnAction(event -> ControllerMyPosts.performQuit());

        footer.getChildren().addAll(button_Return, button_Logout, button_Quit);
        return footer;
    }

    /**
     * <p> Method: void populatePostsList() </p>
     * <p> Description: Clears and refreshes the display of posts in postsList. </p>
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
     * <p> Description: Public refresh trigger for controller after modifications
     * (e.g. deleting posts). </p>
     */
    protected static void refreshPostsDisplay() {
        populatePostsList();
    }

    /**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param button		The Button object to be initialized
	 * @param width			The width of the Button
	 * @param height		The height of the Button
	 */
    private void setupButton(Button button, double width, double height) {
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setFont(Font.font("System", 13));
    }
}
