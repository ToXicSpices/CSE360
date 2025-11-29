package guiStaffHome;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.Database;
import entityClasses.Message;
import entityClasses.Post;
import entityClasses.User;
import entityClasses.Request;
import entityClasses.StudentStatus;

/**********
 * <p> Title: ViewStaffHome Class </p>
 * 
 * <p> Description: This class initializes and manages the Staff Home Page graphical user interface (GUI).
 * It provides methods to build and display the GUI components, including headers, footers, search posts,
 * and post listings. The class interacts with the database to fetch posts and threads and displays them 
 * for the currently logged-in Staff.</p>
 * 
 * <p> Note: This class relies on ControllerStaffHome for event handling and actions.</p>
 *
 */
public class ViewStaffHome {
    
	/** Default application window width. */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** Default application window height. */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// --- GUI elements ---
	/** Label displaying the current page title. */
	protected static Label label_PageTitle = new Label();

	/** Label displaying the current user's name and details. */
	protected static Label label_UserDetails = new Label();

	// -------------
	// --MAIN PAGE--
	// -------------

	// --- Search Posts GUI ---
	/** ComboBox for selecting thread filter when searching posts. */
	protected static ComboBox<String> combobox_Thread = new ComboBox<>();

	/** CheckBox to filter and show only unread posts. */
	protected static CheckBox checkBox_showUnreadPosts = new CheckBox("Show Unread Posts");

	/** TextField for entering search keywords for posts. */
	protected static TextField text_SearchPost = new TextField("");

	/** Button to execute post search. */
	protected static Button button_SearchPost = new Button("Search");

	/** Button to clear the post search field. */
	protected static Button button_ClearSearchPost = new Button("Clear");

	// --- Post list GUI ---
	/** Root layout container for the main page. */
	protected static BorderPane root = new BorderPane();

	/** VBox container for displaying the list of posts. */
	protected static VBox postsList;

	/** VBox container for header section. */
	protected static VBox header;

	/** VBox container for main center content. */
	protected static VBox center;

	/** HBox container for footer section. */
	protected static HBox footer;

	// --- Redirect / navigation buttons ---
	/** Button to navigate to the Messages substage. */
	protected static Button button_Message = new Button("Messages");

	/** Button to navigate to the View Students substage. */
	protected static Button button_ViewStudents = new Button("View Students");

	/** Button to navigate to the Manage Threads substage. */
	protected static Button button_ManageThreads = new Button("Manage Threads");

	/** Button to navigate to the System Requests substage. */
	protected static Button button_SystemRequests = new Button("System Requests");

	/** Button to navigate to Account Update substage. */
	protected static Button button_UpdateThisUser = new Button("Account Update");

	/** Button to filter and view the current user's posts. */
	protected static Button button_ViewMyPosts = new Button("My Posts");

	/** Button to open the Create Post dialog. */
	protected static Button button_MakePost = new Button("+ Post");

	/** Button to log out the current user. */
	protected static Button button_Logout = new Button("Logout");

	/** Button to quit the application entirely. */
	protected static Button button_Quit = new Button("Quit");

	// -----------------------------
	// --System Requests Sub-stage--
	// -----------------------------
	/** Stage for displaying system requests. */
	protected static Stage systemRequestsStage = new Stage();

	/** Main layout VBox for the system requests substage. */
	protected static VBox systemRequestsLayout = new VBox(15);

	/** HBox container for the title row in system requests substage. */
	protected static HBox systemRequestsTitleRow = new HBox(10);

	/** Label displaying "System Requests" title. */
	protected static Label systemRequestsTitleLabel = new Label("System Requests");

	/** Spacer region in the title row to separate elements. */
	protected static Region systemRequestsTitleSpacer = new Region();

	/** Button to create a new system request. */
	protected static Button systemRequestsMakeRequestButton = new Button("Make Request");

	/** HBox container for search row in system requests substage. */
	protected static HBox systemRequestsSearchRow = new HBox(10);

	/** ComboBox for selecting search field (title, content, user) in system requests. */
	protected static ComboBox<String> systemRequestsSearchComboBox = new ComboBox<>();

	/** TextField to enter search keywords for system requests. */
	protected static TextField systemRequestsSearchField = new TextField();

	/** Button to execute system request search. */
	protected static Button systemRequestsSearchButton = new Button("Search");

	/** Button to clear the system request search field. */
	protected static Button systemRequestsClearSearchButton = new Button("Clear");

	/** CheckBox to filter and show only the current user's requests. */
	protected static CheckBox systemRequestsShowMyRequestsCheckBox = new CheckBox("Show my requests");

	/** VBox container to display the list of system requests. */
	protected static VBox systemRequestsListLayout = new VBox(8);

	/** ScrollPane wrapping the system requests list layout. */
	protected static ScrollPane systemRequestsListPane = new ScrollPane(systemRequestsListLayout);

	/** Scene for system requests substage. */
	protected static Scene systemRequestsScene = new Scene(systemRequestsLayout, 650, 520);

	// --------------------------
	// --Make Request Sub-stage--
	// --------------------------
	/** Stage for creating a new system request. */
	protected static Stage makeRequestSubstage = new Stage();

	/** Main layout VBox for make request substage. */
	protected static VBox makeRequestLayout = new VBox(15);

	/** Label for request title input field. */
	protected static Label makeRequestTitleLabel = new Label("Request Title:");

	/** TextField for entering the request title. */
	protected static TextField makeRequestTitleTextField = new TextField();

	/** Label for request content input field. */
	protected static Label makeRequestContentLabel = new Label("Request Content:");

	/** TextArea for entering the request content. */
	protected static TextArea makeRequestContentTextArea = new TextArea();

	/** HBox container for submit/cancel buttons. */
	protected static HBox makeRequestButtonRow = new HBox(15);

	/** Button to submit the new system request. */
	protected static Button makeRequestSubmitButton = new Button("Submit");

	/** Button to cancel making a new request. */
	protected static Button makeRequestCancelButton = new Button("Cancel");

	/** Scene for make request substage. */
	protected static Scene makeRequestScene = new Scene(makeRequestLayout);

	// --------------------------
	// --Open Request Sub-stage--
	// ---------------------------
	/** Stage to display details of a selected system request. */
	protected static Stage openRequestSubstage = new Stage();

	/** Main layout VBox for open request substage. */
	protected static VBox openRequestLayout = new VBox(12);

	/** TextArea to display/edit the request title. */
	protected static TextArea openRequestTitleArea = new TextArea();

	/** Label displaying the requester username. */
	protected static Label  openRequestUserLabel = new Label();

	/** TextArea to display/edit the request content. */
	protected static TextArea openRequestContentArea = new TextArea();

	/** HBox container for open request buttons. */
	protected static HBox openRequestButtonRow = new HBox(10);

	/** Button to close the open request substage. */
	protected static Button openRequestCloseButton = new Button("Close");

	/** Button to save edits to the request. */
	protected static Button openRequestSaveButton = new Button("Save");

	/** Button to delete the request. */
	protected static Button openRequestDeleteButton = new Button("Delete");

	/** Scene for open request substage. */
	protected static Scene openRequestScene = new Scene(openRequestLayout, 500, 350);

	// ----------------------------
	// --Manage Threads Sub-stage--
	// ----------------------------
	/** Stage for managing forum threads. */
	protected static Stage manageThreadsSubstage = new Stage();

	/** Main layout VBox for manage threads substage. */
	protected static VBox manageThreadsLayout = new VBox(15);

	/** Scene for manage threads substage. */
	protected static Scene manageThreadsScene = new Scene(manageThreadsLayout, 500, 500);

	/** Label displaying "Threads" title. */
	protected static Label manageThreadsTitleLabel = new Label("Threads");

	/** HBox container for adding a new thread. */
	protected static HBox manageThreadsAddThreadRow = new HBox(10);

	/** TextField to enter a new thread name. */
	protected static TextField manageThreadsAddField = new TextField();

	/** Button to add a new thread. */
	protected static Button manageThreadsAddButton = new Button("Add");

	/** TableView displaying existing threads. */
	protected static TableView<String> manageThreadsTableView = new TableView<>();

	/** Column displaying thread names. */
	protected static TableColumn<String, String> manageThreadsNameColumn = new TableColumn<>("Name");

	/** Column containing "Update" actions for each thread. */
	protected static TableColumn<String, Void> manageThreadsUpdateColumn = new TableColumn<>("Action");

	/** Column containing "Delete" actions for each thread. */
	protected static TableColumn<String, Void> manageThreadsDeleteColumn = new TableColumn<>("Action");

	/** Dialog for updating thread names. */
	protected static TextInputDialog manageThreadsUpdateDialog;
    
	// --------------------------
	// --Student List Sub-stage--
	// --------------------------
	/** Stage displaying the list of all students. */
	protected static Stage studentListSubstage = new Stage();

	/** Main layout VBox for student list substage. */
	protected static VBox studentListLayout = new VBox(15);

	/** Scene for student list substage. */
	protected static Scene studentListScene = new Scene(studentListLayout, 950, 550);

	/** Label displaying "All Students" title. */
	protected static Label studentListTitleLabel = new Label("All Students");

	/** HBox container for student search row. */
	protected static HBox studentListSearchRow = new HBox(10);

	/** TextField to enter search keywords for students. */
	protected static TextField studentSearchField = new TextField();

	/** Button to execute student search. */
	protected static Button studentSearchButton = new Button("Search");

	/** Button to clear student search field. */
	protected static Button studentClearButton = new Button("Clear");

	/** ComboBox to select sort criteria for student list (name, grade, etc.). */
	protected static ComboBox<String> studentListSortCombobox = new ComboBox<>();

	/** TableView displaying student information. */
	protected static TableView<User> studentListTableView = new TableView<>();

	/** Column displaying student names. */
	protected static TableColumn<User, String> studentListNameColumn = new TableColumn<>("Name");

	/** Column displaying student usernames. */
	protected static TableColumn<User, String> studentListUsernameColumn = new TableColumn<>("Username");

	/** Column displaying student participation scores. */
	protected static TableColumn<User, String> studentListParticipationColumn = new TableColumn<>("Participation");

	/** Column displaying student performance scores. */
	protected static TableColumn<User, String> studentListPerformanceColumn = new TableColumn<>("Performance");

	/** Column displaying students' total grades. */
	protected static TableColumn<User, String> studentListTotalGradeColumn = new TableColumn<>("Total Grade");

	/** Column containing action buttons for details of each student. */
	protected static TableColumn<User, String> studentListActionDetailsColumn = new TableColumn<>("Action");

	/** Column containing action buttons for messaging each student. */
	protected static TableColumn<User, String> studentListActionMessageColumn = new TableColumn<>("Action");

	/** Column containing action buttons to promote a student. */
	private TableColumn<User, String> studentListActionPromoteColumn = new TableColumn<>("Promote");

	/** Column containing action buttons to record student violations. */
	private TableColumn<User, String> studentListActionViolationColumn = new TableColumn<>("Violation");

	// -----------------------------
	// --Student Details Sub-stage--
	// -----------------------------
	/** Stage displaying detailed information about a single student. */
	protected static Stage studentDetailsSubstage = new Stage();

	/** Main layout VBox for student details substage. */
	protected static VBox studentDetailsLayout = new VBox(10);

	/** Scene for student details substage. */
	protected static Scene studentDetailsScene = new Scene(studentDetailsLayout, 400, 400);

	/** HBox container for student details buttons (refresh, message, close). */
	protected static HBox studentDetailsButtonRow = new HBox(10);

	/** Label showing the student's full name. */
	protected static Label studentDetailsStudentNameLabel = new Label("Student: ");

	/** Label showing the student's username. */
	protected static Label studentDetailsUserNameLabel = new Label("Username: ");

	/** Label showing the student's overall grade. */
	protected static Label studentDetailsGradeLabel = new Label("Grade: ");

	/** Label showing the student's participation score. */
	protected static Label studentDetailsPaticipationLabel = new Label("Participation: ");

	/** Label showing the number of posts created by the student. */
	protected static Label studentDetailsNumPostsLabel = new Label("Number of Posts: ");

	/** Label showing the number of replies made by the student. */
	protected static Label studentDetailsNumRepliesLabel = new Label("Number of Replies: ");

	/** Label showing the student's performance score. */
	protected static Label studentDetailsPerformanceLabel = new Label("Performance: ");

	/** Label showing the number of views the student has received. */
	protected static Label studentDetailsGotViewsLabel = new Label("Number of Views Got: ");

	/** Label showing the number of replies received by the student. */
	protected static Label studentDetailsGotRepliesLabel = new Label("Number of Replies Got: ");

	/** Label showing the number of upvotes received by the student. */
	protected static Label studentDetailsGotUpvotesLabel = new Label("Number of Upvotes Got: ");

	/** Label showing the number of times the student has been promoted. */
	protected static Label studentDetailsPromotionsLabel = new Label("Number of Promotions: ");

	/** Label showing the number of violations recorded for the student. */
	protected static Label studentDetailsViolationsLabel = new Label("Number of Violations: ");

	/** Button to refresh the student’s details. */
	protected static Button studentDetailsRefreshButton = new Button("Refresh");

	/** Button to send a message to the student. */
	protected static Button studentDetailsMessageButton = new Button("Message");

	/** Button to close the student details substage. */
	protected static Button studentDetailsCloseButton = new Button("Close");

	// =========================
	// MESSAGE LIST SUBSTAGE (Inbox)
	// =========================
	/** Stage displaying the list of messages (Inbox). */
	protected static Stage messageListSubstage = new Stage();

	/** Main layout VBox for message list substage. */
	protected static VBox messageListLayout = new VBox(10);

	/** Scene for message list substage. */
	protected static Scene messageListScene = new Scene(messageListLayout, 600, 500);

	/** HBox container for title row in message list. */
	protected static HBox messageListTitleRow = new HBox(10);

	/** Label displaying "Messages" title. */
	protected static Label messageListTitleLabel = new Label("Messages");

	/** Spacer region in the message list title row. */
	protected static Region messageListTitleSpacer = new Region();

	/** Button to compose a new message. */
	protected static Button messageListSendButton = new Button("Send Message");

	/** HBox container for search row in message list. */
	protected static HBox messageListSearchRow = new HBox(10);

	/** TextField to enter search keywords for messages. */
	protected static TextField messageListSearchField = new TextField();

	/** Button to execute message search. */
	protected static Button messageListSearchButton = new Button("Search");

	/** Button to clear message search field. */
	protected static Button messageListClearSearchButton = new Button("Clear");

	/** CheckBox to filter and show only unread messages. */
	protected static CheckBox showUnreadOnlyCheckbox = new CheckBox("Show unread only");

	/** ScrollPane containing the list of messages. */
	protected static ScrollPane messageListScrollPane = new ScrollPane();

	/** VBox container inside ScrollPane for messages. */
	protected static VBox messageListContainer = new VBox(5);

	// =========================
	// SEND MESSAGE SUBSTAGE
	// =========================
	/** Stage for composing a new message. */
	protected static Stage sendMessageSubstage = new Stage();

	/** Main layout VBox for send message substage. */
	protected static VBox sendMessageLayout = new VBox(10);

	/** Scene for send message substage. */
	protected static Scene sendMessageScene = new Scene(sendMessageLayout, 450, 350);

	/** Label indicating the recipient field. */
	protected static Label sendMessageReceiverLabel = new Label("To:");

	/** TextField to enter recipient username. */
	protected static TextField sendMessageReceiverField = new TextField();

	/** Label indicating the subject field. */
	protected static Label sendMessageSubjectLabel = new Label("Subject:");

	/** TextField to enter the message subject. */
	protected static TextField sendMessageSubjectField = new TextField();

	/** Label indicating the message content area. */
	protected static Label sendMessageContentLabel = new Label("Message:");

	/** TextArea for entering the message content. */
	protected static TextArea sendMessageContentArea = new TextArea();

	/** HBox container for send/cancel buttons. */
	protected static HBox sendMessageButtonRow = new HBox(10);

	/** Button to cancel sending the message. */
	protected static Button sendMessageCancelButton = new Button("Cancel");

	/** Button to send the message. */
	protected static Button sendMessageSendButton = new Button("Send");

	// =========================
	// MESSAGE DETAILS SUBSTAGE
	// =========================
	/** Stage displaying details of a selected message. */
	protected static Stage messageDetailsSubstage = new Stage();

	/** Main layout VBox for message details substage. */
	protected static VBox messageDetailsLayout = new VBox(10);

	/** Scene for message details substage. */
	protected static Scene messageDetailsScene = new Scene(messageDetailsLayout, 500, 400);

	/** TextArea displaying the message subject. */
	protected static TextArea messageDetailsSubjectArea = new TextArea();

	/** Label showing the sender of the message. */
	protected static Label messageDetailsSenderLabel = new Label();

	/** TextArea displaying the content of the message. */
	protected static TextArea messageDetailsContentArea = new TextArea();

	/** HBox container for message detail buttons (Reply, Close). */
	protected static HBox messageDetailsButtonRow = new HBox(10);

	/** Button to reply to the message. */
	protected static Button messageDetailsReplyButton = new Button("Reply");

	/** Button to close the message details substage. */
	protected static Button messageDetailsCloseButton = new Button("Close");
    
    // --- Application state ---

    /** Singleton instance of the ViewStaffHome class for this page. */
    private static ViewStaffHome theView;
    
    /** Reference to the application's database. */
    protected static Database theDatabase = applicationMain.FoundationsMain.database;
    
    /** The JavaFX Stage where this page is displayed. */
    protected static Stage theStage;
    
    /** The current logged-in user. */
    protected static User theUser;
    
    /** List of posts fetched from the database. */
    protected static ArrayList<Post> posts;
    
    /** List of threads available in the database. */
    protected static ArrayList<String> threads;
    
    /** Scene object representing the Staff Home Page. */
    private static Scene theViewStaffHomeScene;
    
    /** Role identifier for Staff users (constant value 3). */
    protected static final int theRole = 3;
    
    /**********
     * <p> Method: displayStaffHome(Stage ps, User user) </p>
     * <p> Description: Entry point for displaying the Staff Home page. Initializes the stage, 
     * fetches user posts and threads, and sets the scene for the stage.</p>
     * 
     * @param ps The JavaFX Stage to display the GUI
     * @param user The currently logged-in User
     */
    public static void displayStaffHome(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        posts = theDatabase.getAllPostsNewestFirst();
        threads = theDatabase.getAllThreads();
        if (threads.isEmpty()) {
        	theDatabase.addThread("General");
        	threads.add("General");
        }
        threads.add(0, "All");

        applicationMain.FoundationsMain.activeHomePage = theRole;

        if (theView == null) theView = new ViewStaffHome();

        refreshPostsDisplay();
        
        theDatabase.getUserAccountDetails(user.getUserName());

        label_UserDetails.setText("User: " + theUser.getUserName());
        theStage.setTitle("CSE 360 Foundations: Staff Home Page");
        theStage.setScene(theViewStaffHomeScene);
        theStage.show();
    }

    /**********
     * <p> Constructor: ViewStaffHome() </p>
     * <p> Description: Initializes all GUI components for the Staff Home page, including
     * the header, center, and footer sections, and sets the scene.</p>
     */
    private ViewStaffHome() {
        BorderPane root = new BorderPane();

        header = createHeader();
        root.setTop(header);

        center = createCenterSection();
        root.setCenter(center);

        footer = createFooter();
        root.setBottom(footer);

        createSystemRequestsStage();
        createMakeRequestDialog();
        createOpenRequestSubstage();
        createManageThreadsTable();
        createViewStudentsListSubstage();
        createStudentDetailsSubstage();
        createMessageListSubstage();
        createSendMessageSubstage();
        createMessageDetailsSubstage();
        
        theViewStaffHomeScene = new Scene(root, width, height);
    }

    /**********
     * <p> Method: createHeader() </p>
     * <p> Description: Builds the header section of the Staff Home page, including the title,
     * user details, and navigation buttons for account update and viewing user's posts.</p>
     * @return VBox containing the header
     */
    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(20));

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        label_PageTitle.setText("Staff Home Page");
        label_PageTitle.setFont(Font.font("System", 24));
        
        HBox staffActionBox = new HBox(15);
        staffActionBox.setAlignment(Pos.CENTER_RIGHT);
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        setupButton(button_ViewStudents, 120, 35);
        button_ViewStudents.setOnAction(event -> showViewStudentsListSubstage());
        
        setupButton(button_ManageThreads, 120, 35);
        button_ManageThreads.setOnAction(event -> showManageThreadsTable());
        
        setupButton(button_SystemRequests, 140, 35);
        button_SystemRequests.setOnAction(event -> showSystemRequestsStage());
        
        staffActionBox.getChildren().addAll(button_ViewStudents, button_ManageThreads, button_SystemRequests);
        titleRow.getChildren().addAll(label_PageTitle, spacer1, staffActionBox);

        HBox controlsRow = new HBox();
        controlsRow.setAlignment(Pos.CENTER_LEFT);
        label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
        label_UserDetails.setFont(Font.font("System", 16));
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        setupButton(button_Message, 120, 35);
        button_Message.setOnAction(event -> showMessageListSubstage());
        
        setupButton(button_ViewMyPosts, 120, 35);
        button_ViewMyPosts.setOnAction(event -> ControllerStaffHome.performViewMyPosts());

        setupButton(button_UpdateThisUser, 140, 35);
        button_UpdateThisUser.setOnAction(event -> 
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));

        buttonBox.getChildren().addAll(button_Message, button_ViewMyPosts, button_UpdateThisUser);
        controlsRow.getChildren().addAll(label_UserDetails, spacer2, buttonBox);

        header.getChildren().addAll(titleRow, controlsRow);
        return header;
    }

    /**********
     * <p> Method: createCenterSection() </p>
     * <p> Description: Builds the center section of the Staff Home page, including the
     * post search area and the list of posts.</p>
     * @return VBox containing the center section
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
        combobox_Thread.setOnAction((event) -> ControllerStaffHome.performAllFilters());

        titleSection.getChildren().addAll(searchLabel, combobox_Thread);

        HBox searchBox = new HBox(5);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        text_SearchPost.setPromptText("Enter keywords to search posts...");
        text_SearchPost.setPrefWidth(400);
        text_SearchPost.setFont(Font.font("System", 14));

        setupButton(button_SearchPost, 80, 35);
        button_SearchPost.setOnAction(event -> ControllerStaffHome.performAllFilters());

        setupButton(button_ClearSearchPost, 80, 35);
        button_ClearSearchPost.setOnAction(event -> ControllerStaffHome.performClear());

        setupButton(button_MakePost, 140, 35);
        button_MakePost.setText("Create New Post");
        button_MakePost.setOnAction(event -> ControllerStaffHome.performMakePost());

        Region searchSpacer = new Region();
        HBox.setHgrow(searchSpacer, Priority.ALWAYS);

        searchBox.getChildren().addAll(text_SearchPost, button_SearchPost, button_ClearSearchPost, searchSpacer, button_MakePost);
        searchSection.getChildren().addAll(titleSection, searchBox);

        VBox postsSection = createPostsSection();

        center.getChildren().addAll(searchSection, postsSection);
        return center;
    }

    /**********
     * <p> Method: createPostsSection() </p>
     * <p> Description: Builds the posts list area in a scrollable VBox.</p>
     * @return VBox containing the posts list section
     */
    private VBox createPostsSection() {
        VBox postsSection = new VBox(10);
        HBox titleSection = new HBox(10);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label postsLabel = new Label("Recent Posts");
        postsLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));

        checkBox_showUnreadPosts.setFont(Font.font("System", 14));
        checkBox_showUnreadPosts.setOnAction(e -> ControllerStaffHome.performAllFilters());

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

    /**********
     * <p> Method: createPostItem(Post post) </p>
     * <p> Description: Generates a GUI item for a single post, including title, author, tags, 
     * thread, status, and number of replies.</p>
     * @param post The Post object to display
     * @return VBox representing the post item
     */
    private static VBox createPostItem(Post post) {
        VBox postItem = new VBox(8);
        postItem.setPadding(new Insets(12));
        postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

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
        int totalViews = theDatabase.getPostViews(post.getPostId());
        int totalUpvotes = theDatabase.getPostUpvotes(post.getPostId());
        Label repliesLabel = new Label(totalViews + " views, " + totalUpvotes + " upvotes, " + totalReplies + " replies, " + unreadReplies + " unread");
        repliesLabel.setFont(Font.font("System", 11));

        statsBox.getChildren().addAll(threadStatusLabel, readStatusLabel, repliesLabel);

        infoRow.getChildren().addAll(labelBox, infoSpacer, statsBox);
        contentSection.getChildren().addAll(titleLabel, infoRow);
        postItem.getChildren().add(contentSection);

        contentSection.setOnMouseClicked(e -> guiPostPage.ViewPostPage.displayPostPage(theStage, theUser, post));

        postItem.setOnMouseEntered(e ->
            postItem.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: #f5f5f5;"));
        postItem.setOnMouseExited(e ->
            postItem.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;"));

        return postItem;
    }

    /**********
     * <p> Method: createFooter() </p>
     * <p> Description: Creates the footer section with Logout and Quit buttons.</p>
     * @return HBox containing footer buttons
     */
    private HBox createFooter() {
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(15, 20, 20, 20));
        footer.setAlignment(Pos.CENTER_RIGHT);

        setupButton(button_Logout, 100, 35);
        button_Logout.setOnAction(event -> ControllerStaffHome.performLogout());

        setupButton(button_Quit, 80, 35);
        button_Quit.setOnAction(event -> ControllerStaffHome.performQuit());

        footer.getChildren().addAll(button_Logout, button_Quit);
        return footer;
    }

    /**********
     * <p> Method: populatePostsList() </p>
     * <p> Description: Populates the postsList VBox with GUI items for all posts.</p>
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

    /**********
     * <p> Method: refreshPostsDisplay() </p>
     * <p> Description: Refreshes the posts list area by repopulating the post items.</p>
     */
    protected static void refreshPostsDisplay() {
        populatePostsList();
    }

    /**
     * <p>Method: createSystemRequestsStage()</p>
     *
     * <p>Description: Initializes and configures the main System Requests stage, including
     * title row, search row, and the requests list pane.</p>
     */
    protected static void createSystemRequestsStage() {
    	systemRequestsStage.initOwner(theStage);
        systemRequestsStage.initModality(Modality.APPLICATION_MODAL);
        systemRequestsStage.setTitle("System Requests");
        
        systemRequestsLayout.setPadding(new Insets(15));
        
        // Title Row
        systemRequestsTitleRow.setAlignment(Pos.CENTER_LEFT);
        systemRequestsTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox.setHgrow(systemRequestsTitleSpacer, Priority.ALWAYS);
        
        systemRequestsMakeRequestButton.setOnAction(e -> {
            showMakeRequestDialog();
            refreshSystemRequestsList();
        });
        
        systemRequestsTitleRow.getChildren().addAll(systemRequestsTitleLabel, systemRequestsTitleSpacer, systemRequestsMakeRequestButton);
        
        // Search Row
        systemRequestsSearchRow.setAlignment(Pos.CENTER_LEFT);
        systemRequestsSearchComboBox.getItems().addAll("title", "content", "user");
        systemRequestsSearchComboBox.setValue("title");
        systemRequestsSearchField.setPromptText("Enter keyword");
        systemRequestsSearchButton.setOnAction(e -> refreshSystemRequestsList());
        systemRequestsClearSearchButton.setOnAction(e -> {
        	systemRequestsSearchField.clear();
            refreshSystemRequestsList();
        });
        systemRequestsShowMyRequestsCheckBox.setOnAction(e -> refreshSystemRequestsList());
        systemRequestsSearchRow.getChildren().addAll(
        		systemRequestsSearchComboBox,
                systemRequestsSearchField,
                systemRequestsSearchButton,
                systemRequestsClearSearchButton,
                systemRequestsShowMyRequestsCheckBox
        );
        
        // Request List
        systemRequestsListPane.setFitToWidth(true);
        systemRequestsListPane.setPrefHeight(400);
        
        systemRequestsLayout.getChildren().addAll(systemRequestsTitleRow, systemRequestsSearchRow, systemRequestsListPane);
    }
    
    /**
     * <p>Method: showSystemRequestsStage()</p>
     *
     * <p>Description: Refreshes and shows the System Requests stage.</p>
     */
    protected static void showSystemRequestsStage() {
        refreshSystemRequestsList();
        systemRequestsStage.setScene(systemRequestsScene);
        systemRequestsStage.show();
    }

    /**
     * <p>Method: refreshSystemRequestsList()</p>
     *
     * <p>Description: Retrieves all system requests from the database, applies filters for
     * search keywords and "show my requests", and displays them in the requests list layout.</p>
     */
    protected static void refreshSystemRequestsList() {
    	systemRequestsListLayout.getChildren().clear();
    	List<Request> requests;
    	try {
    		requests = theDatabase.getAllRequests();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return;
    	}
    	boolean showMineOnly = systemRequestsShowMyRequestsCheckBox.isSelected();
    	String selectedField = systemRequestsSearchComboBox.getValue();
    	String keyword = systemRequestsSearchField.getText().trim().toLowerCase();

    	// Show My Requests
    	if (showMineOnly) {
    		requests.removeIf(r -> !r.getRequester().equals(theUser.getUserName()));
    	}

    	// Keyword Fitering
    	if (!keyword.isEmpty()) {

    		switch (selectedField) {
    			case "title":
    				requests.removeIf(r -> !r.getTitle().toLowerCase().contains(keyword));
    				break;

    			case "content":
    				requests.removeIf(r -> !r.getContent().toLowerCase().contains(keyword));
    				break;

    			case "user":
            		requests.removeIf(r -> !r.getRequester().equalsIgnoreCase(keyword));
            		break;
    		}
    	}

    	// Display Result
    	for (Request req : requests) {

    		HBox row = new HBox(10);
    		row.setPadding(new Insets(8));
    		row.setAlignment(Pos.CENTER_LEFT);
    		row.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;");

    		// Title
    		Label title = new Label(req.getTitle());
    		title.setWrapText(true);
        	title.setPrefWidth(260);

        	// Requester
        	Label requesterLabel = new Label("from: " + req.getRequester());
        	requesterLabel.setPrefWidth(120);
        	requesterLabel.setStyle("-fx-text-fill: #333;");
        	requesterLabel.setAlignment(Pos.CENTER_LEFT);

        	// ---- STATUS ----
        	Label statusLabel = new Label(req.isChecked() ? "checked" : "unchecked");
        	statusLabel.setPrefWidth(90);
        	statusLabel.setAlignment(Pos.CENTER);
        	statusLabel.setStyle(req.isChecked() ? "-fx-text-fill: green;" : "-fx-text-fill: grey;");

        	row.getChildren().addAll(title, requesterLabel, statusLabel);

        	row.setOnMouseClicked(ev -> showOpenRequestSubstage(req));

        	systemRequestsListLayout.getChildren().add(row);
    	}

    	if (systemRequestsListLayout.getChildren().isEmpty()) {
    		Label emptyLabel = new Label("No requests to display");
        	emptyLabel.setPadding(new Insets(10));
        	systemRequestsListLayout.getChildren().add(emptyLabel);
    	}
    }
    
    /**
     * <p>Method: createMakeRequestDialog()</p>
     *
     * <p>Description: Initializes the dialog for creating a new system request, including
     * fields for title, content, and buttons for submitting or cancelling.</p>
     */
    protected static void createMakeRequestDialog() {
    	makeRequestSubstage.initModality(Modality.APPLICATION_MODAL);
    	makeRequestSubstage.setTitle("Make System Request");
    	makeRequestLayout.setPadding(new Insets(15));
        makeRequestLayout.setPrefWidth(400);
        
        // Title
        makeRequestTitleTextField.setPromptText("Enter title");
        
        // Content
        makeRequestContentTextArea.setPromptText("Describe your request...");
        makeRequestContentTextArea.setWrapText(true);
        makeRequestContentTextArea.setPrefHeight(150);
        
        // Buttons
        makeRequestButtonRow.setAlignment(Pos.CENTER_RIGHT);

        // Cancel button closes window
        makeRequestCancelButton.setOnAction(e -> makeRequestSubstage.close());

        // Submit button creates request
        makeRequestSubmitButton.setOnAction(e -> {ControllerStaffHome.performMakeRequest(makeRequestTitleTextField.getText().trim(), makeRequestContentTextArea.getText().trim());});

        makeRequestButtonRow.getChildren().addAll(makeRequestCancelButton, makeRequestSubmitButton);

        makeRequestLayout.getChildren().addAll(makeRequestTitleLabel, makeRequestTitleTextField, makeRequestContentLabel, makeRequestContentTextArea, makeRequestButtonRow);
    }
    
    /**
     * <p>Method: showMakeRequestDialog()</p>
     *
     * <p>Description: Clears previous input fields and shows the Make Request dialog.</p>
     */
    protected static void showMakeRequestDialog() {
    	makeRequestTitleTextField.setText("");
    	makeRequestContentTextArea.setText("");
        makeRequestSubstage.setScene(makeRequestScene);
        makeRequestSubstage.showAndWait();
    }
    
    /**
     * <p>Method: createOpenRequestSubstage()</p>
     *
     * <p>Description: Initializes a substage for viewing and editing individual requests,
     * including title, requester, content, and action buttons.</p>
     */
    protected static void createOpenRequestSubstage() {
    	openRequestSubstage.initOwner(systemRequestsStage);
        openRequestSubstage.initModality(Modality.APPLICATION_MODAL);
        openRequestSubstage.setTitle("Request Details");

        openRequestLayout.setPadding(new Insets(15));
        
        // Title
        openRequestTitleArea.setWrapText(true);
        openRequestTitleArea.setEditable(false);
        openRequestTitleArea.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");
        openRequestTitleArea.setPrefWidth(450);
        
        // User
        openRequestUserLabel.setStyle("-fx-font-size: 14px;");
        openRequestUserLabel.setAlignment(Pos.CENTER_RIGHT);

        // Content
        openRequestContentArea.setWrapText(true);
        openRequestContentArea.setEditable(false);
        openRequestContentArea.setPrefWidth(450);
        
        // Buttons
        openRequestButtonRow.setAlignment(Pos.CENTER_RIGHT);
    	openRequestCloseButton.setOnAction(e -> openRequestSubstage.close());
    	
    	openRequestLayout.getChildren().addAll(openRequestTitleArea, openRequestUserLabel, openRequestContentArea, openRequestButtonRow);
    }
    
    /**
     * <p>Method: showOpenRequestSubstage(Request req)</p>
     *
     * <p>Description: Displays details of a specific system request. If the current user is the requester,
     * allows editing and deletion. Updates database and refreshes the request list accordingly.</p>
     *
     * @param req The request to display
     */
    protected static void showOpenRequestSubstage(Request req) {
    	openRequestTitleArea.setText(req.getTitle());
        openRequestTitleArea.setPrefRowCount(Math.max(1, req.getTitle().split("\n").length));

        openRequestUserLabel.setText("from: " + req.getRequester());

        openRequestContentArea.setText(req.getContent());
        openRequestContentArea.setPrefRowCount(Math.max(3, req.getContent().split("\n").length));

        if (theUser.getUserName().equals(req.getRequester())) {
        	openRequestTitleArea.setEditable(true);
        	openRequestContentArea.setEditable(true);
        	
        	openRequestSaveButton.setOnAction(event -> {
                String newTitle = openRequestTitleArea.getText().trim();
                String newContent = openRequestContentArea.getText().trim();
                ControllerStaffHome.performUpdateRequest(req, newTitle, newContent);
            });

        	openRequestDeleteButton.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete this request?",
                        ButtonType.YES, ButtonType.NO);
                alert.setHeaderText("Confirm Deletion");
                alert.initOwner(openRequestSubstage);

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            theDatabase.deleteRequest(req);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        refreshSystemRequestsList();
                        openRequestSubstage.close();
                    }
                });
            });
        	
            openRequestButtonRow.getChildren().setAll(openRequestSaveButton, openRequestDeleteButton, openRequestCloseButton);
        } else {
        	openRequestButtonRow.getChildren().setAll(openRequestCloseButton);
        }
        
        openRequestSubstage.setScene(openRequestScene);
        openRequestSubstage.show();
    }
    
    /**
     * <p>Method: createManageThreadsTable()</p>
     *
     * <p>Description: Initializes the Manage Threads substage with a table view,
     * columns for thread name, update and delete actions, and controls for adding new threads.</p>
     */
    protected static void createManageThreadsTable() {
    	manageThreadsSubstage.setTitle("Manage Threads");
    	manageThreadsLayout.setPadding(new Insets(20));
    	manageThreadsTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    	
    	// Thread Table View
        manageThreadsTableView.setColumnResizePolicy(tv -> true);
        
        // Name Column
        manageThreadsNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        manageThreadsNameColumn.setStyle("-fx-alignment: CENTER;");

        // Add Thread
        manageThreadsAddField.setPromptText("Enter thread name");
        manageThreadsAddButton.setOnAction(e -> {ControllerStaffHome.performAddThread();});
        manageThreadsAddThreadRow.getChildren().addAll(manageThreadsAddField, manageThreadsAddButton);
        
        // Action(Update) Column
        manageThreadsUpdateColumn.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label("Update");

            {
                // default style
                label.setStyle("-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;");

                // hover
                label.setOnMouseEntered(e -> label.setStyle(
                    "-fx-text-fill: #0a65c2; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;"));

                label.setOnMouseExited(e -> label.setStyle(
                    "-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;"));

                // click action
                label.setOnMouseClicked(e -> {
                    String threadName = getTableView().getItems().get(getIndex());
                    manageThreadsUpdateDialog = new TextInputDialog(threadName);
                    ControllerStaffHome.performUpdateThread(threadName);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

     // Action(Delete) Column
        manageThreadsDeleteColumn.setCellFactory(col -> new TableCell<>() {

            private final Label label = new Label("Delete");

            {
                // normal style
                label.setStyle("-fx-text-fill: red; -fx-underline: false; -fx-font-weight: bold;");

                // hover effect
                label.setOnMouseEntered(e -> label.setStyle(
                    "-fx-text-fill: red; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;"));

                label.setOnMouseExited(e -> label.setStyle(
                    "-fx-text-fill: red; -fx-underline: false; -fx-font-weight: bold;"));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                String threadName = getTableView().getItems().get(getIndex());

                // Disable deletion for "General"
                if ("General".equalsIgnoreCase(threadName)) {
                    label.setStyle("-fx-text-fill: grey; -fx-underline: false; -fx-font-weight: bold;");
                    label.setOnMouseClicked(e -> {}); // no action
                    label.setOnMouseEntered(e -> label.setStyle(
                        "-fx-text-fill: grey; -fx-underline: false; -fx-cursor: default; -fx-font-weight: bold;"));
                    label.setOnMouseExited(e -> label.setStyle(
                        "-fx-text-fill: grey; -fx-underline: false; -fx-font-weight: bold;"));
                } else {
                    // restore clickable behavior
                    label.setStyle("-fx-text-fill: red; -fx-underline: false; -fx-font-weight: bold;");
                    label.setOnMouseClicked(e ->
                        ControllerStaffHome.performDeleteThread(threadName)
                    );
                    label.setOnMouseEntered(e -> label.setStyle(
                        "-fx-text-fill: red; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;"));
                    label.setOnMouseExited(e -> label.setStyle(
                        "-fx-text-fill: red; -fx-underline: false; -fx-font-weight: bold;"));
                }

                setGraphic(label);
                setAlignment(Pos.CENTER);
            }
        });

        // Add columns
        manageThreadsTableView.getColumns().add(manageThreadsNameColumn);
        manageThreadsTableView.getColumns().add(manageThreadsUpdateColumn);
        manageThreadsTableView.getColumns().add(manageThreadsDeleteColumn);
        manageThreadsTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            double total = newVal.doubleValue();
            manageThreadsNameColumn.setPrefWidth(total * 0.6666);
            manageThreadsUpdateColumn.setPrefWidth(total * 0.1666);
            manageThreadsDeleteColumn.setPrefWidth(total * 0.1666);
        });
        manageThreadsLayout.getChildren().addAll(manageThreadsTitleLabel, manageThreadsAddThreadRow, manageThreadsTableView);
    }
    
    /**
     * <p>Method: showManageThreadsTable()</p>
     *
     * <p>Description: Loads all threads into the Manage Threads table and shows the substage.</p>
     */
    protected static void showManageThreadsTable() {
    	manageThreadsTableView.setItems(FXCollections.observableArrayList(theDatabase.getAllThreads()));
        manageThreadsSubstage.setScene(manageThreadsScene);
        manageThreadsSubstage.show();
    }
    
    /**
     * <p>Method: createViewStudentsListSubstage()</p>
     *
     * <p>Description: Initializes the View Students substage with search, sort, and a table
     * showing student information, participation, performance, and action buttons for details,
     * messaging, promotion, and violation.</p>
     */
    protected void createViewStudentsListSubstage() {
    	studentListSubstage.initOwner(theStage);
        studentListSubstage.initModality(Modality.APPLICATION_MODAL);
    	studentListSubstage.setTitle("View Students");
    	studentListLayout.setPadding(new Insets(15));
    	// Title
    	studentListTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        studentListLayout.getChildren().add(studentListTitleLabel);

        // Search Row
        studentListSearchRow.setAlignment(Pos.CENTER_LEFT);
        studentSearchField.setPromptText("Enter username");
        studentSearchField.setPrefWidth(200);
        studentListSortCombobox.getItems().addAll("A-Z", "Z-A");
        studentListSortCombobox.getSelectionModel().selectFirst();
        studentListLayout.getChildren().add(studentListSearchRow);
        studentSearchButton.setOnAction(e -> {ControllerStaffHome.performStudentSearch();});
        studentClearButton.setOnAction(e -> {ControllerStaffHome.performStudentClear();});
        studentListSortCombobox.setOnAction(e -> {ControllerStaffHome.performStudentSort();});

        // Student List Table View
        studentListTableView.setColumnResizePolicy(tv -> true);
        
        // Name Column
        studentListNameColumn.setCellValueFactory(data -> {
            User u = data.getValue();
            String fullName = (u.getFirstName() + " " + u.getMiddleName() + " " + u.getLastName()).trim();
            if (fullName.isBlank()) fullName = "<Anonymous>";
            return new SimpleStringProperty(fullName);
        });
        
        // Username Column
        studentListUsernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserName()));

        // Participation Column
        studentListParticipationColumn.setCellValueFactory(data -> {
            User u = data.getValue();
            StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
            ControllerStaffHome.performRefreshStatus(s);
            if (s == null) return new SimpleStringProperty("0%");
            double participation = ModelStaffHome.getParticipation(s);
            return new SimpleStringProperty(String.format("%.1f%%", participation));
        });

        // Performance Column
        studentListPerformanceColumn.setCellValueFactory(data -> {
            User u = data.getValue();
            StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
            ControllerStaffHome.performRefreshStatus(s);
            if (s == null) return new SimpleStringProperty("0%");
            double performance = ModelStaffHome.getPerformance(s);
            return new SimpleStringProperty(String.format("%.1f%%", performance));
        });

        // Total Grade Column
        studentListTotalGradeColumn.setCellValueFactory(data -> {
            User u = data.getValue();
            StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
            ControllerStaffHome.performRefreshStatus(s);
            if (s == null) return new SimpleStringProperty("0%");
            double participation = ModelStaffHome.getParticipation(s);
            double performance = ModelStaffHome.getPerformance(s);
            double total = ModelStaffHome.getTotalGrade(participation, performance);
            return new SimpleStringProperty(ModelStaffHome.getGradeMark(total) + " (" + (String.format("%.1f%%", total)) + ")");
        });

     // Action(Details) Column
        studentListActionDetailsColumn.setCellFactory(col -> new TableCell<User, String>() {

            private final Label clickable = new Label("Details");

            {
                // style
                clickable.setStyle("-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;");

                // hover effect
                clickable.setOnMouseEntered(e -> clickable.setStyle(
                        "-fx-text-fill: #0a65c2; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;"));

                clickable.setOnMouseExited(e -> clickable.setStyle(
                        "-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;"));

                // click behavior
                clickable.setOnMouseClicked(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
                    showStudentDetailsSubstage(u, s);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(clickable);
                }
            }
        });

        // Action(Message) Column
        studentListActionMessageColumn.setCellFactory(col -> new TableCell<User, String>() {

            private final Label clickable = new Label("Message");

            {
                clickable.setStyle("-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;");

                clickable.setOnMouseEntered(e -> clickable.setStyle(
                    "-fx-text-fill: #0a65c2; -fx-underline: true; -fx-cursor: hand; -fx-font-weight: bold;"));

                clickable.setOnMouseExited(e -> clickable.setStyle(
                    "-fx-text-fill: #0a65c2; -fx-underline: false; -fx-font-weight: bold;"));

                clickable.setOnMouseClicked(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    showSendMessageSubstage(u.getUserName());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(clickable);
            }
        });
        
        // Action(Promote) Column
        studentListActionPromoteColumn.setCellFactory(col -> new TableCell<User, String>() {

            private final Label clickable = new Label("Promote");

            {
                clickable.setStyle("-fx-font-weight: bold; -fx-underline: false;");

                // Hover underline effect (always works)
                clickable.setOnMouseEntered(e -> {
                    clickable.setStyle(clickable.getStyle() + "; -fx-underline: true; -fx-cursor: hand;");
                });
                clickable.setOnMouseExited(e -> {
                    clickable.setStyle(clickable.getStyle().replace("; -fx-underline: true;", ""));
                });

                clickable.setOnMouseClicked(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
                    if (s == null) return;

                    // Toggle promotion count
                    if (s.getPromotion() == 0)
                        s.setPromotion(s.getPromotion()+1);  // enable
                    else
                        s.setPromotion(s.getPromotion()-1);  // disable

                    theDatabase.updateStudentStatus(s);
                    ControllerStaffHome.performRefreshStatus(s);

                    updateColorBasedOnStatus(s);
                    getTableView().refresh();
                });
            }

            private void updateColorBasedOnStatus(StudentStatus s) {
                if (s.getPromotion() == 0) {
                    clickable.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
                } else {
                    clickable.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                User u = getTableView().getItems().get(getIndex());
                StudentStatus s = theDatabase.getStudentStatus(u.getUserName());

                if (s != null) updateColorBasedOnStatus(s);
                setGraphic(clickable);
            }
        });

        // Action(Violation) Column
        studentListActionViolationColumn.setCellFactory(col -> new TableCell<User, String>() {

            private final Label clickable = new Label("Violation");

            {
                clickable.setStyle("-fx-font-weight: bold; -fx-underline: false;");

                // Hover underline effect
                clickable.setOnMouseEntered(e -> {
                    clickable.setStyle(clickable.getStyle() + "; -fx-underline: true; -fx-cursor: hand;");
                });
                clickable.setOnMouseExited(e -> {
                    clickable.setStyle(clickable.getStyle().replace("; -fx-underline: true;", ""));
                });

                clickable.setOnMouseClicked(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    StudentStatus s = theDatabase.getStudentStatus(u.getUserName());
                    if (s == null) return;

                    // Toggle violation
                    if (s.getViolation() == 0)
                        s.setViolation(s.getViolation()+1);
                    else
                        s.setViolation(s.getViolation()-1);

                    theDatabase.updateStudentStatus(s);
                    ControllerStaffHome.performRefreshStatus(s);

                    updateColorBasedOnStatus(s);
                    getTableView().refresh();
                });
            }

            private void updateColorBasedOnStatus(StudentStatus s) {
                if (s.getViolation() == 0) {
                    clickable.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
                } else {
                    clickable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                User u = getTableView().getItems().get(getIndex());
                StudentStatus s = theDatabase.getStudentStatus(u.getUserName());

                if (s != null) updateColorBasedOnStatus(s);
                setGraphic(clickable);
            }
        });


        // Add columns
        studentListTableView.getColumns().add(studentListNameColumn);
        studentListTableView.getColumns().add(studentListUsernameColumn);
        studentListTableView.getColumns().add(studentListParticipationColumn);
        studentListTableView.getColumns().add(studentListPerformanceColumn);
        studentListTableView.getColumns().add(studentListTotalGradeColumn);
        studentListTableView.getColumns().add(studentListActionDetailsColumn);
        studentListTableView.getColumns().add(studentListActionMessageColumn);
        studentListTableView.getColumns().add(studentListActionPromoteColumn);
        studentListTableView.getColumns().add(studentListActionViolationColumn);
        studentListLayout.getChildren().add(studentListTableView);
    }
    
    /**
     * <p>Method: showViewStudentsListSubstage()</p>
     *
     * <p>Description: Loads all students into the table view, refreshes their statuses,
     * and shows the substage.</p>
     */
    protected void showViewStudentsListSubstage() {
        ObservableList<User> studentList = FXCollections.observableArrayList(theDatabase.getAllStudents());
        for (User u : studentList) {
        	ControllerStaffHome.performRefreshStatus(theDatabase.getStudentStatus(u.getUserName()));
        }
        
        studentListTableView.setItems(studentList);

        studentListSubstage.setScene(studentListScene);
        studentListSubstage.show();
    }
    
    /**
     * <p>Method: createStudentDetailsSubstage()</p>
     *
     * <p>Description: Initializes a substage for displaying detailed information about a student,
     * including name, username, grade, participation, posts, replies, performance, received views/upvotes,
     * promotions, and violations.</p>
     */
    protected void createStudentDetailsSubstage() {
    	studentDetailsSubstage.initModality(Modality.APPLICATION_MODAL);
    	studentDetailsSubstage.setTitle("Student Details");

    	studentDetailsLayout.setPadding(new Insets(15));
    	
    	studentDetailsLayout.getChildren().addAll(
    			studentDetailsStudentNameLabel,
    			studentDetailsUserNameLabel,
    			studentDetailsGradeLabel,
    			studentDetailsPaticipationLabel,
    			studentDetailsNumPostsLabel,
    			studentDetailsNumRepliesLabel,
    			studentDetailsPerformanceLabel,
    			studentDetailsGotViewsLabel,
    			studentDetailsGotRepliesLabel,
    			studentDetailsGotUpvotesLabel,
    			studentDetailsPromotionsLabel,
    			studentDetailsViolationsLabel
    			);
    	studentDetailsCloseButton.setOnAction(e -> studentDetailsSubstage.close());
        studentDetailsButtonRow.getChildren().addAll(studentDetailsRefreshButton, studentDetailsMessageButton, studentDetailsCloseButton);
        studentDetailsLayout.getChildren().add(studentDetailsButtonRow);
    }
    
    /**
     * <p>Method: showStudentDetailsSubstage(User u, StudentStatus s)</p>
     *
     * <p>Description: Populates and shows the student details substage for the given user and status.
     * Provides refresh and message actions.</p>
     *
     * @param u The student user
     * @param s The student's status
     */
    protected void showStudentDetailsSubstage(User u, StudentStatus s) {
    	ControllerStaffHome.performRefreshStatus(s);
        String fullName = (u.getFirstName() + " " + u.getMiddleName() + " " + u.getLastName()).trim();
        if (fullName.isBlank()) fullName = "<Anonymous>";
        
        double grade = ModelStaffHome.getTotalGrade(ModelStaffHome.getParticipation(s), ModelStaffHome.getPerformance(s));
        String mark = ModelStaffHome.getGradeMark(grade);

        studentDetailsStudentNameLabel.setText("Student: " + fullName);
		studentDetailsUserNameLabel.setText("Username: " + u.getUserName());
		studentDetailsGradeLabel.setText("Grade: " + mark + " (" + String.format("%.1f%%", grade) + ")");
		studentDetailsPaticipationLabel.setText("Participation:");
		studentDetailsNumPostsLabel.setText("   Number of Posts: " + s.getPostNumber() + " / 5");
		studentDetailsNumRepliesLabel.setText("   Number of Replies: " + s.getReplyNumber() + " / 5");
		studentDetailsPerformanceLabel.setText("Performance:");
		studentDetailsGotViewsLabel.setText("   Number of Views Got: " + s.getViewReceived() + " / " + s.getPostNumber());
		studentDetailsGotRepliesLabel.setText("   Number of Replies Got: " + s.getReplyReceived() + " / " + s.getPostNumber());
		studentDetailsGotUpvotesLabel.setText("   Number of Upvotes Got: " + s.getUpvoteReceived() + " / 5");
		studentDetailsPromotionsLabel.setText("   Number of Promotions: " + s.getPromotion());
		studentDetailsViolationsLabel.setText("   Number of Violations: " + s.getViolation());

		studentDetailsRefreshButton.setOnAction(e -> ControllerStaffHome.performRefreshStatus(s));
		studentDetailsMessageButton.setOnAction(e -> showSendMessageSubstage(u.getUserName()));
		
        studentDetailsSubstage.setScene(studentDetailsScene);
        studentDetailsSubstage.show();
    }
    
    /**
     * <p>Method: createMessageListSubstage()</p>
     *
     * <p>Description: Initializes the Messages substage, including title, search controls,
     * filter for unread messages, and a scrollable container for message rows.</p>
     */
    protected static void createMessageListSubstage() {
        messageListSubstage.initOwner(theStage);
        messageListSubstage.initModality(Modality.APPLICATION_MODAL);
        messageListSubstage.setTitle("Messages");

        messageListLayout.setPadding(new Insets(15));

        // Title Row
        messageListTitleRow.setAlignment(Pos.CENTER_LEFT);
        messageListTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox.setHgrow(messageListTitleSpacer, Priority.ALWAYS);

        messageListSendButton.setOnAction(e -> showSendMessageSubstage(""));

        messageListTitleRow.getChildren().addAll(
                messageListTitleLabel,
                messageListTitleSpacer,
                messageListSendButton
        );

        // Search Row
        messageListSearchRow.setAlignment(Pos.CENTER_LEFT);
        messageListSearchField.setPromptText("Search subject...");
        messageListSearchButton.setOnAction(e -> refreshMessageList());
        messageListClearSearchButton.setOnAction(e -> {
            messageListSearchField.clear();
            refreshMessageList();
        });

        showUnreadOnlyCheckbox.setOnAction(e -> refreshMessageList());
        
        messageListSearchRow.getChildren().addAll(
                messageListSearchField,
                messageListSearchButton,
                messageListClearSearchButton,
                showUnreadOnlyCheckbox
        );
        
        // Scroll area
        messageListScrollPane.setFitToWidth(true);
        messageListScrollPane.setPrefHeight(400);
        
        messageListScrollPane.setContent(messageListContainer);

        messageListLayout.getChildren().addAll(
                messageListTitleRow,
                messageListSearchRow,
                messageListScrollPane
        );
    }

    /**
     * <p>Method: showMessageListSubstage()</p>
     *
     * <p>Description: Refreshes and displays the Messages substage.</p>
     */
    protected static void showMessageListSubstage() {
        refreshMessageList();
        messageListSubstage.setScene(messageListScene);
        messageListSubstage.show();
    }

    /**
     * <p>Method: refreshMessageList()</p>
     *
     * <p>Description: Retrieves all messages for the current user, applies search and unread
     * filters, and populates the message list container. Marks messages as read when opened.</p>
     */
    protected static void refreshMessageList() {
        messageListContainer.getChildren().clear();
        System.out.println("=== refreshMessageList called ===");

        List<Message> msgs = theDatabase.getAllMessages(theUser.getUserName());
        if (msgs == null) msgs = new ArrayList<>();

        String keyword = messageListSearchField.getText() == null ? "" : messageListSearchField.getText().trim().toLowerCase();
        boolean showUnreadOnly = showUnreadOnlyCheckbox.isSelected();

        // Filter by subject
        if (!keyword.isEmpty()) {
            msgs.removeIf(m -> m.getSubject() == null || !m.getSubject().toLowerCase().contains(keyword));
        }

        // Filter by unread
        if (showUnreadOnly) {
            msgs.removeIf(Message::isRead);
        }

        System.out.println("Number of messages after filtering: " + msgs.size());

        for (Message msg : msgs) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(8));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: white;");

            Label subject = new Label(msg.getSubject() == null ? "(no subject)" : msg.getSubject());
            subject.setWrapText(true);
            subject.setPrefWidth(250);
            subject.setStyle(msg.isRead() ? "-fx-text-fill: #333;" : "-fx-font-weight: bold; -fx-text-fill: black;");

            Label sender = new Label("from: " + (msg.getSender() == null ? "(unknown)" : msg.getSender()));
            sender.setStyle("-fx-text-fill: #333;");
            sender.setPrefWidth(120);

            Label status = new Label(msg.isRead() ? "read" : "unread");
            status.setPrefWidth(90);
            status.setAlignment(Pos.CENTER);
            status.setStyle(msg.isRead() ? "-fx-text-fill: green;" : "-fx-text-fill: grey;");

            row.getChildren().addAll(subject, sender, status);
            row.setOnMouseClicked(ev -> showMessageDetailsSubstage(msg));

            messageListContainer.getChildren().add(row);
        }

        if (messageListContainer.getChildren().isEmpty()) {
            Label empty = new Label("No messages to display");
            empty.setPadding(new Insets(10));
            messageListContainer.getChildren().add(empty);
        }
    }

    /**
     * <p>Method: createSendMessageSubstage()</p>
     *
     * <p>Description: Initializes the Send Message substage with fields for receiver, subject,
     * content, and buttons for sending or cancelling.</p>
     */
    protected static void createSendMessageSubstage() {
        sendMessageSubstage.initModality(Modality.APPLICATION_MODAL);
        sendMessageSubstage.setTitle("Send Message");

        sendMessageLayout.setPadding(new Insets(15));
        sendMessageLayout.setPrefWidth(400);

        // Receiver
        sendMessageReceiverField.setPromptText("Receiver username");

        // Subject
        sendMessageSubjectField.setPromptText("Subject");

        // Content
        sendMessageContentArea.setPromptText("Write your message...");
        sendMessageContentArea.setWrapText(true);
        sendMessageContentArea.setPrefHeight(150);

        // Buttons
        sendMessageButtonRow.setAlignment(Pos.CENTER_RIGHT);

        sendMessageCancelButton.setOnAction(e -> sendMessageSubstage.close());

        sendMessageSendButton.setOnAction(e -> {
            ControllerStaffHome.performSendMessage(
                    sendMessageReceiverField.getText().trim(),
                    sendMessageSubjectField.getText().trim(),
                    sendMessageContentArea.getText().trim()
            );
        });

        sendMessageButtonRow.getChildren().addAll(
                sendMessageCancelButton,
                sendMessageSendButton
        );

        sendMessageLayout.getChildren().addAll(
                sendMessageReceiverLabel,
                sendMessageReceiverField,
                sendMessageSubjectLabel,
                sendMessageSubjectField,
                sendMessageContentLabel,
                sendMessageContentArea,
                sendMessageButtonRow
        );
    }

    /**
     * <p>Method: showSendMessageSubstage(String receiver)</p>
     *
     * <p>Description: Pre-fills the receiver field and shows the Send Message substage.</p>
     *
     * @param receiver Username of the message receiver
     */
    protected static void showSendMessageSubstage(String receiver) {
        sendMessageReceiverField.setText(receiver);
        sendMessageSubjectField.clear();
        sendMessageContentArea.clear();

        sendMessageSubstage.setScene(sendMessageScene);
        sendMessageSubstage.showAndWait();
    }

    /**
     * <p>Method: createMessageDetailsSubstage()</p>
     *
     * <p>Description: Initializes the Message Details substage, including subject, sender,
     * content display, and action buttons.</p>
     */
    protected static void createMessageDetailsSubstage() {
        messageDetailsSubstage.initOwner(messageListSubstage);
        messageDetailsSubstage.initModality(Modality.APPLICATION_MODAL);
        messageDetailsSubstage.setTitle("Message Details");

        messageDetailsLayout.setPadding(new Insets(15));

        // Subject
        messageDetailsSubjectArea.setWrapText(true);
        messageDetailsSubjectArea.setEditable(false);
        messageDetailsSubjectArea.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        messageDetailsSubjectArea.setPrefWidth(450);

        // Sender
        messageDetailsSenderLabel.setStyle("-fx-font-size: 14px;");

        // Content
        messageDetailsContentArea.setWrapText(true);
        messageDetailsContentArea.setEditable(false);
        messageDetailsContentArea.setPrefWidth(450);

        // Buttons
        messageDetailsButtonRow.setAlignment(Pos.CENTER_RIGHT);

        messageDetailsCloseButton.setOnAction(e -> messageDetailsSubstage.close());

        messageDetailsLayout.getChildren().addAll(
                messageDetailsSubjectArea,
                messageDetailsSenderLabel,
                messageDetailsContentArea,
                messageDetailsButtonRow
        );
    }

    /**
     * <p>Method: showMessageDetailsSubstage(Message msg)</p>
     *
     * <p>Description: Populates and displays the Message Details substage for a specific message,
     * allows replying, and marks the message as read.</p>
     *
     * @param msg The message to display
     */
    protected static void showMessageDetailsSubstage(Message msg) {
        messageDetailsSubjectArea.setText(msg.getSubject());
        messageDetailsSubjectArea.setPrefRowCount(Math.max(1, msg.getSubject().split("\n").length));

        messageDetailsSenderLabel.setText("from: " + msg.getSender());

        messageDetailsContentArea.setText(msg.getContent());
        messageDetailsContentArea.setPrefRowCount(Math.max(3, msg.getContent().split("\n").length));

        messageDetailsReplyButton.setOnAction(e -> ControllerStaffHome.performReplyMessage(msg));

        messageDetailsButtonRow.getChildren().setAll(
                messageDetailsReplyButton,
                messageDetailsCloseButton
        );

        // Mark as read
        if (!msg.isRead()) {
            theDatabase.markMessageAsRead(msg);
            refreshMessageList();
        }

        messageDetailsSubstage.setScene(messageDetailsScene);
        messageDetailsSubstage.show();
    }
    
    /**
     * <p>Method: showInfo(String title, String content)</p>
     *
     * <p>Description: Displays an information alert with the given title and content.</p>
     *
     * @param title The alert title
     * @param content The alert content
     */
    protected static void showInfo(String title, String content) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setHeaderText(title);
    	alert.setContentText(content);
    	alert.show();
    }
    
    /**
     * <p>Method: showError(String title, String content)</p>
     *
     * <p>Description: Displays an error alert with the given title and content.</p>
     *
     * @param title The alert title
     * @param content The alert content
     */
    protected static void showError(String title, String content) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setHeaderText(title);
    	alert.setContentText(content);
    	alert.show();
    }
    
    /**
     * <p>Method: showConfirm(String title, String content)</p>
     *
     * <p>Description: Displays a confirmation alert with the given title and content.
     * Returns the button pressed by the user.</p>
     *
     * @param title The alert title
     * @param content The alert content
     * @return Optional containing the button clicked
     */
    protected static Optional<ButtonType> showConfirm(String title, String content) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setHeaderText(title);
    	alert.setContentText(content);
    	return alert.showAndWait();
    }
    
    /**********
     * <p> Method: setupButton(Button button, double width, double height) </p>
     * <p> Description: Helper method to initialize a button's size and font.</p>
     * @param button Button to configure
     * @param width Preferred width
     * @param height Preferred height
     */
    private void setupButton(Button button, double width, double height) {
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setFont(Font.font("System", 13));
    }

    /**********
     * <p> Method: setupComboBoxUI(ComboBox c, String ff, double f, double w) </p>
     * <p> Description: Helper method to initialize a ComboBox's font and width.</p>
     * @param c ComboBox to configure
     * @param ff Font family
     * @param f Font size
     * @param w Minimum width
     */
    private void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w) {
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
    }
}
