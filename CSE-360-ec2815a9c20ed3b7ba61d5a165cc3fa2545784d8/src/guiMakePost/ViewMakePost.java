package guiMakePost;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import database.Database;
import entityClasses.User;
import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/*******
 * <p> Title: ViewMakePost Class. </p>
 * 
 * <p> Description: The Java/FX-based ViewMakePost Page.</p>
 *  
 */

public class ViewMakePost {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI.
	
	// GUI for Page
	protected static Label label_PageTitle = new Label();
	protected static Line line_Separator = new Line(20, 45, width-20, 45);
	
	// GUI for thread inputs
	protected static Label label_ThreadIntro = new Label();
	protected static ComboBox<String> combobox_SelectThread;
	
	// GUI for tags inputs
	protected static Label label_TagsIntro = new Label();
	protected static Label label_TagsContent = new Label();
	protected static Button button_AddTag = new Button("+");
	protected static Button button_RemoveTag = new Button("-");
	protected static TextInputDialog dialogAddTags;
    protected static ChoiceDialog<String> dialogRemoveTag;
	
	// GUI for title inputs
	protected static Label label_TitleIntro = new Label();
	protected static TextField text_Title = new TextField();
	
	// GUI for subtitle inputs
	protected static Label label_SubtitleIntro = new Label();
	protected static TextField text_Subtitle = new TextField();
	
	// GUI for content inputs
	protected static Label label_ContentIntro = new Label();
	protected static TextArea text_Content = new TextArea();
	
	// GUI for replies
	protected static CheckBox checkBox_allowReplies = new CheckBox("Allow replies to this post.");

	// GUI for redirect buttons
	protected static Button button_Return = new Button("Cancel");
	protected static Button button_MakePost = new Button("Post");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
    
	// GUI for alerts
    protected static Alert alertTitleError = new Alert(AlertType.INFORMATION);
    protected static Alert alertSubtitleError = new Alert(AlertType.INFORMATION);
    protected static Alert alertContentError = new Alert(AlertType.INFORMATION);
    protected static Alert alertAddTagError = new Alert(AlertType.INFORMATION);
    protected static Alert alert_discardPost = new Alert(Alert.AlertType.CONFIRMATION);
    
    // This is the end of the GUI objects for the page.
    
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewMakePost theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	protected static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	protected static ArrayList<String> threads = new ArrayList<String>();
    protected static ObservableList<String> itemsObservableList;
	
	private static Scene theMakePostScene;		// The shared Scene each invocation populates
	protected static int theRole = -1;

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayMakePost(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Make Post page to be displayed. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayMakePost(Stage ps, User user) {
		
		// Establish the references to the GUI, the current user and the current role
		theStage = ps;
		theUser = user;
		theRole = applicationMain.FoundationsMain.activeHomePage;
		
		// Establish the thread list from database
		threads = theDatabase.getAllThreads();
		itemsObservableList = FXCollections.observableArrayList(threads);
		combobox_SelectThread = new ComboBox<>(itemsObservableList);
		
		// populate the static aspects of the GUI
		theView = new ViewMakePost();		// Instantiate singleton every time
        if (theView == null);				// not used, ignore warning purpose
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Make Post Page");
		theStage.setScene(theMakePostScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: ViewMakePost() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayMakePost method.</p>
	 * 
	 */
	private ViewMakePost() {
	    
	    // Create the Pane for the list of widgets and the Scene for the window
	    theRootPane = new Pane();
	    theMakePostScene = new Scene(theRootPane, width, height);	// Create the scene
	    
	    // Set the title for the window
	    
	    // Populate the window with the title and other common widgets and set their static state
	    
	    label_PageTitle.setText("Create New Post");
	    setupLabelUI(label_PageTitle, "Arial", 24, width, Pos.CENTER, 0, 15);
	    
	    // Thread selection - top left
	    label_ThreadIntro.setText("Select Thread:");
	    setupLabelUI(label_ThreadIntro, "Arial", 16, width, Pos.BOTTOM_LEFT, 60, 75);
	    combobox_SelectThread.getSelectionModel().selectFirst();
	    setupComboBoxUI(combobox_SelectThread, "Dialog", 14, 200, 60, 95);
	    
	    // Tags section - top right
	    label_TagsIntro.setText("Tags:");
	    setupLabelUI(label_TagsIntro, "Arial", 16, width, Pos.BOTTOM_LEFT, 450, 75);
	    label_TagsContent.setText("");
	    setupLabelUI(label_TagsContent, "Arial", 14, width, Pos.BOTTOM_LEFT, 450, 120);
	    dialogAddTags = new TextInputDialog("");
	    dialogAddTags.setTitle("Add Tag");
	    dialogAddTags.setHeaderText("Add a New Tag");
	    setupButtonUI(button_AddTag, "Dialog", 14, 30, Pos.BOTTOM_LEFT, 490, 95);
	    button_AddTag.setOnAction((event) -> { ControllerMakePost.doAddTag();});
	    dialogRemoveTag = new ChoiceDialog<String>("<none>", ControllerMakePost.tags);
	    dialogRemoveTag.setTitle("Remove Tag");
	    dialogRemoveTag.setHeaderText("Choose a tag to remove");
	    setupButtonUI(button_RemoveTag, "Dialog", 14, 30, Pos.BOTTOM_LEFT, 525, 95);
	    button_RemoveTag.setOnAction((event) -> { ControllerMakePost.doRemoveTag();});
	    
	    // Title section
	    label_TitleIntro.setText("Post Title");
	    setupLabelUI(label_TitleIntro, "Arial", 16, width, Pos.BOTTOM_LEFT, 60, 150);
	    setupTextUI(text_Title, "Arial", 14, 680, Pos.CENTER_LEFT, 60, 170, true);
	    text_Title.setPromptText("Enter a descriptive title for your post");
	    text_Title.textProperty().addListener((observable, oldValue, newValue) 
	            -> {ControllerMakePost.setTitle(); });
	    
	    // Subtitle section
	    label_SubtitleIntro.setText("Subtitle");
	    setupLabelUI(label_SubtitleIntro, "Arial", 16, width, Pos.BOTTOM_LEFT, 60, 210);
	    setupTextUI(text_Subtitle, "Arial", 14, 680, Pos.CENTER_LEFT, 60, 230, true);
	    text_Subtitle.setPromptText("Add a brief subtitle or description");
	    text_Subtitle.textProperty().addListener((observable, oldValue, newValue) 
	            -> {ControllerMakePost.setSubtitle(); });
	    
	    // Content section
	    label_ContentIntro.setText("Post Body");
	    setupLabelUI(label_ContentIntro, "Arial", 16, width, Pos.BOTTOM_LEFT, 60, 270);
	    setupTextAreaUI(text_Content, "Arial", 14, 680, 160, Pos.TOP_LEFT, 60, 290, true);
	    text_Content.setPromptText("Write your detailed post content here");
	    text_Content.textProperty().addListener((observable, oldValue, newValue) 
	            -> {ControllerMakePost.setContent(); });
	    
	    // Checkbox for replies
	    checkBox_allowReplies.setLayoutX(60);
	    checkBox_allowReplies.setLayoutY(470);
	    checkBox_allowReplies.setFont(Font.font("Dialog", 14));
	    checkBox_allowReplies.setOnAction(e -> {
	        if (checkBox_allowReplies.isSelected()) {
	            System.out.println("Checked!"); 	// not implemented yet
	        } else {
	            System.out.println("Unchecked!");	// not implemented yet
	        }
	    });
	    
	    // Alert configurations
	    alertTitleError.setTitle("Invalid Title Input!");
	    alertTitleError.setHeaderText(null);
	    alertTitleError.setContentText("Within 10-60 charaters.\n"
	            + "Characters allowed in the title:\n"
	            + "Alphabets(a-z, A-Z)\n"
	            + "Numbers(0-9)\n"
	            + "Space( )\n"
	            + "Special Characters: !#$%&'*+-/=?^_`{|}~(),:;<>@[\\].\"");

	    alertSubtitleError.setTitle("Invalid Subtitle Input!");
	    alertSubtitleError.setHeaderText(null);
	    alertSubtitleError.setContentText("Within 0-80 charaters.\n"
	            + "no more than two lines");
	    
	    alertContentError.setTitle("Invalid Content Input!");
	    alertContentError.setHeaderText(null);
	    alertContentError.setContentText("Within 50-2200 charaters.\n");
	    
	    alertAddTagError.setTitle("Invalid Tag Input!");
	    alertAddTagError.setHeaderText(null);
	    alertAddTagError.setContentText("Only alphabets are allowed.\n"
	            + "Within 3-10 charaters.\n"
	            + "No more than 5 tags");
	    
	    alert_discardPost.setTitle("Alert");
	    alert_discardPost.setHeaderText("Are you sure to discard this draft?");
	    alert_discardPost.setContentText("Your progress will not be saved!");
	    alert_discardPost.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
	    
	    // Bottom buttons
	    setupButtonUI(button_MakePost, "Dialog", 16, 120, Pos.CENTER, 200, 520);
	    button_MakePost.setOnAction((event) -> {ControllerMakePost.doMakePost();});
	    
	    setupButtonUI(button_Return, "Dialog", 16, 120, Pos.CENTER, 340, 520);
	    button_Return.setOnAction((event) -> {
	        Optional<ButtonType> result = alert_discardPost.showAndWait();
	        if (result.isPresent() && result.get() == ButtonType.YES) {
	            ControllerMakePost.goToUserHomePage(theStage, theUser);
	        }
	        });
	    
	    setupButtonUI(button_Quit, "Dialog", 16, 120, Pos.CENTER, 480, 520);
	    button_Quit.setOnAction((event) -> {ControllerMakePost.performQuit(); });

	    // This is the end of the GUI initialization code
	    
	    // Place all of the widget items into the Root Pane's list of children
	    theRootPane.getChildren().addAll(
	        label_PageTitle, line_Separator, 
	        label_TitleIntro, label_SubtitleIntro, label_ContentIntro, label_TagsIntro, 
	        label_ThreadIntro, combobox_SelectThread, label_TagsContent,
	        text_Title, text_Subtitle, text_Content, checkBox_allowReplies,
	        button_AddTag, button_RemoveTag, button_MakePost,
	        button_Return, button_Logout, button_Quit);
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
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text area
	 */
	private void setupTextAreaUI(TextArea t, String ff, double f, double w, double h, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinHeight(h);
		t.setMaxHeight(h);
		t.setMinWidth(w);
		t.setMaxWidth(w);
		VBox vbox = new VBox(t);
		vbox.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, 
			double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}