package guiStudentHome;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Message;
import entityClasses.Post;
import entityClasses.StudentStatus;
import entityClasses.User;

/**********
 * <p> Title: ViewStudentHome Class </p>
 * 
 * <p> Description: This class initializes and manages the Student Home Page graphical user interface (GUI).
 * It provides methods to build and display the GUI components, including headers, footers, search posts,
 * and post listings. The class interacts with the database to fetch posts and threads and displays them 
 * for the currently logged-in student.</p>
 * 
 * <p> Note: This class relies on ControllerStudentHome for event handling and actions.</p>
 *
 */
public class ViewStudentHome {
    
	/** Default application window width. */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    
    /** Default application window height. */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // --- GUI elements ---

    /** Label displaying the page title. */
    protected static Label label_PageTitle = new Label();
    
    /** Label displaying the student grade. */
    protected static Label label_Grade = new Label();
    
    /** Label displaying the details of the logged-in user. */
    protected static Label label_UserDetails = new Label();

    // --- Search Posts GUI ---

    /** ComboBox for selecting a thread to filter posts. */
    protected static ComboBox<String> combobox_Thread = new ComboBox<>();
    
    /** CheckBox to toggle display of unread posts. */
    protected static CheckBox checkBox_showUnreadPosts = new CheckBox("Show Unread Posts");
    
    /** TextField for entering search text for posts. */
    protected static TextField text_SearchPost = new TextField("");
    
    /** Button to execute a search for posts. */
    protected static Button button_SearchPost = new Button("Search");
    
    /** Button to clear the search field and reset post display. */
    protected static Button button_ClearSearchPost = new Button("Clear");

    // --- Post list GUI ---

    /** Root BorderPane for organizing the Student Home Page layout. */
    protected static BorderPane root = new BorderPane();
    
    /** VBox to hold the list of posts. */
    protected static VBox postsList;
    
    /** VBox for the header section of the page. */
    protected static VBox header;
    
    /** VBox for the central section of the page. */
    protected static VBox center;
    
    /** HBox for the footer section of the page. */
    protected static HBox footer;

    // --- Redirect / navigation buttons ---

    /** Button to navigate to show message list. */
    protected static Button button_Message = new Button("Messages");
    
    /** Button to navigate to account update page. */
    protected static Button button_UpdateThisUser = new Button("Account Update");
    
    /** Button to navigate to the user's posts page. */
    protected static Button button_ViewMyPosts = new Button("My Posts");
    
    /** Button to create a new post. */
    protected static Button button_MakePost = new Button("+ Post");
    
    /** Button to log out the current user. */
    protected static Button button_Logout = new Button("Logout");
    
    /** Button to quit the application. */
    protected static Button button_Quit = new Button("Quit");
    
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

    /** Singleton instance of the ViewStudentHome class for this page. */
    private static ViewStudentHome theView;
    
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
    
    /** Scene object representing the Student Home Page. */
    private static Scene theViewStudentHomeScene;
    
    /** Role identifier for student users (constant value 2). */
    protected static final int theRole = 2;

    /**********
     * <p> Method: displayStudentHome(Stage ps, User user) </p>
     * <p> Description: Entry point for displaying the Student Home page. Initializes the stage, 
     * fetches user posts and threads, and sets the scene for the stage.</p>
     * 
     * @param ps The JavaFX Stage to display the GUI
     * @param user The currently logged-in User
     */
    public static void displayStudentHome(Stage ps, User user) {
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

        if (theView == null) theView = new ViewStudentHome();
        
        refreshStudentGrade();
        refreshPostsDisplay();

        theDatabase.getUserAccountDetails(user.getUserName());

        label_UserDetails.setText("User: " + theUser.getUserName());
        theStage.setTitle("CSE 360 Foundations: Student Home Page");
        theStage.setScene(theViewStudentHomeScene);
        theStage.show();
    }

    /**********
     * <p> Constructor: ViewStudentHome() </p>
     * <p> Description: Initializes all GUI components for the Student Home page, including
     * the header, center, and footer sections, and sets the scene.</p>
     */
    private ViewStudentHome() {
        BorderPane root = new BorderPane();

        header = createHeader();
        root.setTop(header);

        center = createCenterSection();
        root.setCenter(center);

        footer = createFooter();
        root.setBottom(footer);
        
        createMessageListSubstage();
        createSendMessageSubstage();
        createMessageDetailsSubstage();

        theViewStudentHomeScene = new Scene(root, width, height);
    }

    /**********
     * <p> Method: createHeader() </p>
     * <p> Description: Builds the header section of the Student Home page, including the title,
     * user details, and navigation buttons for account update and viewing user's posts.</p>
     * @return VBox containing the header
     */
    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(20));

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        label_PageTitle.setText("Student Home Page");
        label_PageTitle.setFont(Font.font("System", 24));
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        label_Grade.setText("<Grade>");
        label_Grade.setFont(Font.font("System", 24));
        
        titleRow.getChildren().addAll(label_PageTitle, spacer1, label_Grade);

        HBox controlsRow = new HBox();
        controlsRow.setAlignment(Pos.CENTER_LEFT);
        label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
        label_UserDetails.setFont(Font.font("System", 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        setupButton(button_Message, 120, 35);
        button_Message.setOnAction(event -> showMessageListSubstage());
        
        setupButton(button_ViewMyPosts, 120, 35);
        button_ViewMyPosts.setOnAction(event -> ControllerStudentHome.performViewMyPosts());

        setupButton(button_UpdateThisUser, 140, 35);
        button_UpdateThisUser.setOnAction(event -> 
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser));

        buttonBox.getChildren().addAll(button_Message, button_ViewMyPosts, button_UpdateThisUser);
        controlsRow.getChildren().addAll(label_UserDetails, spacer, buttonBox);

        header.getChildren().addAll(titleRow, controlsRow);
        return header;
    }

    /**********
     * <p> Method: createCenterSection() </p>
     * <p> Description: Builds the center section of the Student Home page, including the
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
        combobox_Thread.setOnAction((event) -> ControllerStudentHome.performAllFilters());

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
        button_MakePost.setOnAction(event -> ControllerStudentHome.performMakePost());

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
        button_Logout.setOnAction(event -> ControllerStudentHome.performLogout());

        setupButton(button_Quit, 80, 35);
        button_Quit.setOnAction(event -> ControllerStudentHome.performQuit());

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
    
    /**
     * <p>Method: refreshStudentGrade()</p>
     *
     * <p>Description: Refreshes the current student's grade display. Retrieves the student's status
     * from the database, calculates total grade based on participation and performance, converts it
     * to a grade mark, and updates the grade label in the UI.</p>
     */
    protected static void refreshStudentGrade() {
    	StudentStatus s = theDatabase.getStudentStatus(theUser.getUserName());
    	ControllerStudentHome.performRefreshStatus(s);
    	double grade = ModelStudentHome.getTotalGrade(ModelStudentHome.getParticipation(s), ModelStudentHome.getPerformance(s));
    	String mark = ModelStudentHome.getGradeMark(grade);
    	label_Grade.setText(mark + " (" + String.format("%.1f%%", grade) + ")");
    }

    /**********
     * <p> Method: refreshPostsDisplay() </p>
     * <p> Description: Refreshes the posts list area by repopulating the post items.</p>
     */
    protected static void refreshPostsDisplay() {
        populatePostsList();
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
            ControllerStudentHome.performSendMessage(
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

        messageDetailsReplyButton.setOnAction(e -> ControllerStudentHome.performReplyMessage(msg));

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
