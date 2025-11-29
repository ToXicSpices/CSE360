package guiMultipleRoleDispatch;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/**********
 * <p> Title: ViewMultipleRoleDispatch Class </p>
 * 
 * <p> Description: Java/FX-based page for users with multiple roles. Provides interface
 * to select and perform a specific role (Admin, Staff, or Student). Includes logout and
 * quit functionality.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-08-20 Initial version
 */
public class ViewMultipleRoleDispatch {

// --- Application window size ---
    
    /** Default application window width */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    
    /** Default application window height */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // --- GUI widgets ---
    
    /** Label displaying the page title */
    private static Label label_PageTitle = new Label("Multiple Role Dispatch Page");
    
    /** Label displaying the logged-in user details */
    private static Label label_UserDetails = new Label();
    
    /** Horizontal line separator for layout */
    private static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    
    /** Label prompting the user to choose a role */
    private static Label label_WhichRole = new Label("Which role do you wish to play:");
    
    /** ComboBox for selecting the desired role */
    protected static ComboBox<String> combobox_SelectRole = new ComboBox<>();
    
    /** Button to confirm and perform the selected role */
    private static Button button_PerformRole = new Button("Perform Role");
    
    /** Horizontal line separator for layout */
    private static Line line_Separator4 = new Line(20, 525, width - 20, 525);
    
    /** Button to log out of the application */
    private static Button button_Logout = new Button("Logout");
    
    /** Button to quit the application */
    private static Button button_Quit = new Button("Quit");

    // --- References & state ---
    
    /** Singleton instance of this class */
    private static ViewMultipleRoleDispatch theView;
    
    /** Reference to the application database */
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    /** The JavaFX stage where the scene is displayed */
    protected static Stage theStage;
    
    /** Root pane for holding all GUI widgets */
    private static Pane theRootPane;
    
    /** The currently logged-in user */
    protected static User theUser;
    
    /** Scene object representing the multiple role dispatch page */
    private static Scene theMultipleRoleDispatchScene = null;

    /**********
     * <p> Method: displayMultipleRoleDispatch(Stage ps, User user) </p>
     * <p> Description: Entry point to show the Multiple Role Dispatch page. Initializes GUI
     * if needed, populates role options, and shows the stage.</p>
     * 
     * @param ps The JavaFX Stage for this GUI
     * @param user The current logged-in User
     */
    public static void displayMultipleRoleDispatch(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewMultipleRoleDispatch();

        // Reset combo box selection
        combobox_SelectRole.getSelectionModel().select(0);

        theStage.setTitle("CSE 360 Foundation Code: Multiple Role Dispatch");
        theStage.setScene(theMultipleRoleDispatchScene);
        theStage.show();
    }

    /**********
     * <p> Constructor: ViewMultipleRoleDispatch() </p>
     * <p> Description: Initializes all GUI widgets, including labels, combo box, buttons,
     * and separators. Populates role options based on current user.</p>
     */
    private ViewMultipleRoleDispatch() {
        theRootPane = new Pane();
        theMultipleRoleDispatchScene = new Scene(theRootPane, width, height);

        // GUI Area 1: Page title and user details
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        // GUI Area 2: Role selection
        setupLabelUI(label_WhichRole, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 110);
        setupComboBoxUI(combobox_SelectRole, "Dialog", 16, 100, 305, 105);

        // Populate ComboBox with available roles
        theDatabase.getUserAccountDetails(theUser.getUserName());
        List<String> roles = new ArrayList<>();
        roles.add("<Select a role>");
        if (theUser.getAdminRole()) roles.add("Admin");
        if (theUser.getStudentRole()) roles.add("Student");
        if (theUser.getStaffRole()) roles.add("Staff");
        combobox_SelectRole.setItems(FXCollections.observableArrayList(roles));

        setupButtonUI(button_PerformRole, "Dialog", 16, 100, Pos.CENTER, 495, 105);
        button_PerformRole.setOnAction(event -> 
            guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performRole()
        );

        // GUI Area 3: Logout & Quit buttons
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction(event -> 
            guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performLogout()
        );

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction(event -> 
            guiMultipleRoleDispatch.ControllerMultipleRoleDispatch.performQuit()
        );

        // Add widgets to root pane
        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_UserDetails,
            line_Separator1,
            label_WhichRole,
            combobox_SelectRole,
            button_PerformRole,
            line_Separator4,
            button_Logout,
            button_Quit
        );
    }

    /**********
     * <p> Helper method to setup a Label </p>
     * 
     * @param l The Label to configure
     * @param ff The font family for the label
     * @param f The font size for the label
     * @param w The minimum width of the label
     * @param p The alignment for the label content
     * @param x The X coordinate for the label layout
     * @param y The Y coordinate for the label layout
     */
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**********
     * <p> Helper method to setup a Button </p>
     * 
     * @param b The Button to configure
     * @param ff The font family for the button text
     * @param f The font size for the button text
     * @param w The minimum width of the button
     * @param p The alignment for the button content
     * @param x The X coordinate for the button layout
     * @param y The Y coordinate for the button layout
     */
    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**********
     * <p> Helper method to setup a ComboBox </p>
     * 
     * @param c The ComboBox to configure
     * @param ff The font family for the ComboBox text
     * @param f The font size for the ComboBox text
     * @param w The minimum width of the ComboBox
     * @param x The X coordinate for the ComboBox layout
     * @param y The Y coordinate for the ComboBox layout
     */
    private void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}
