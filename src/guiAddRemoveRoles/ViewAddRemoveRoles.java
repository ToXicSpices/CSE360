package guiAddRemoveRoles;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
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

/*******
 * <p> Title: ViewAddRemoveRoles Class. </p>
 * 
 * <p> Description: JavaFX-based page for changing the assigned roles to users.
 * This class provides the GUI and methods to display, select, add, and remove
 * roles for a selected user in the system.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.00 2025-08-20 Initial version
 */
public class ViewAddRemoveRoles {
    
    /*-*******************************************************************************************
     * Attributes
     ********************************************************************************************/

    /** Width of the application window */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

    /** Height of the application window */
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    /** Label for the page title */
    protected static Label label_PageTitle = new Label();

    /** Label for displaying user details */
    protected static Label label_UserDetails = new Label();

    /** Button to update the selected user's account */
    protected static Button button_UpdateThisUser = new Button("Account Update");

    /** Horizontal line used as a separator between GUI sections */
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

    /** Label prompting the admin to select a user */
    protected static Label label_SelectUser = new Label("Select a user to be updated:");

    /** ComboBox for selecting a user to update */
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<String>();

    /** List of roles that can be added to a user */
    protected static List<String> addList = new ArrayList<String>();

    /** Button to add a selected role */
    protected static Button button_AddRole = new Button("Add This Role");

    /** List of roles that can be removed from a user */
    protected static List<String> removeList = new ArrayList<String>();

    /** Button to remove a selected role */
    protected static Button button_RemoveRole = new Button("Remove This Role");

    /** Label showing current roles of the selected user */
    protected static Label label_CurrentRoles = new Label("This user's current roles:");

    /** Label prompting selection of a role to be added */
    protected static Label label_SelectRoleToBeAdded = new Label("Select a role to be added:");

    /** ComboBox for selecting a role to add */
    protected static ComboBox<String> combobox_SelectRoleToAdd = new ComboBox<String>();

    /** Label prompting selection of a role to be removed */
    protected static Label label_SelectRoleToBeRemoved = new Label("Select a role to be removed:");

    /** ComboBox for selecting a role to remove */
    protected static ComboBox<String> combobox_SelectRoleToRemove = new ComboBox<String>();

    /** Horizontal line separator for GUI layout */
    protected static Line line_Separator4 = new Line(20, 525, width-20, 525);

    /** Button to return to previous page */
    protected static Button button_Return = new Button("Return");

    /** Button to log out of the application */
    protected static Button button_Logout = new Button("Logout");

    /** Button to quit the application */
    protected static Button button_Quit = new Button("Quit");

    /** Singleton instance of the ViewAddRemoveRoles class */
    private static ViewAddRemoveRoles theView;

    /** Reference to the in-memory database */
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /** JavaFX stage for this GUI */
    protected static Stage theStage;

    /** Root pane containing all GUI widgets */
    protected static Pane theRootPane;

    /** Current user of the application */
    protected static User theUser;

    /** JavaFX scene for this page */
    public static Scene theAddRemoveRolesScene = null;

    /** Username of the user whose roles are being updated */
    protected static String theSelectedUser = "";

    /** Role to be added */
    protected static String theAddRole = "";

    /** Role to be removed */
    protected static String theRemoveRole = "";

    /*-*******************************************************************************************
     * Constructors
     ********************************************************************************************/

    /**********
     * <p>Displays the Add/Remove Roles page for a given user.</p>
     * 
     * <p>Initializes the stage, sets up the GUI if needed, populates dynamic content, and makes
     * the scene visible.</p>
     * 
     * @param ps The JavaFX Stage to be used
     * @param user The user whose roles will be updated
     */
    public static void displayAddRemoveRoles(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        if (theView == null) theView = new ViewAddRemoveRoles();
        ControllerAddRemoveRoles.repaintTheWindow();
        ControllerAddRemoveRoles.doSelectUser();
    }

    /**********
     * <p>Initializes all the GUI elements, including labels, buttons, combo boxes,
     * and separators.</p>
     * 
     * <p>This is called once due to the singleton pattern; dynamic fields are updated
     * separately.</p>
     */
    public ViewAddRemoveRoles() {
        theRootPane = new Pane();
        theAddRemoveRolesScene = new Scene(theRootPane, width, height);

        // GUI Area 1
        label_PageTitle.setText("Add/Removed Roles Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: " + theUser.getUserName());
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((event) -> 
            {guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

        // GUI Area 2a
        setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
        setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);
        List<String> userList = theDatabase.getUserList();    
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
        combobox_SelectUser.getSelectionModel().select(0);
        combobox_SelectUser.getSelectionModel().selectedItemProperty()
            .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                ControllerAddRemoveRoles.doSelectUser());

        // GUI Area 2b
        setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);    
        setupLabelUI(label_SelectRoleToBeAdded, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 210);
        setupComboBoxUI(combobox_SelectRoleToAdd, "Dialog", 16, 150, 280, 205);
        setupButtonUI(button_AddRole, "Dialog", 16, 150, Pos.CENTER, 460, 205);
        button_AddRole.setOnAction((event) -> ControllerAddRemoveRoles.performAddRole());
        setupButtonUI(button_RemoveRole, "Dialog", 16, 150, Pos.CENTER, 460, 275);            
        button_RemoveRole.setOnAction((event) -> ControllerAddRemoveRoles.performRemoveRole());
        setupLabelUI(label_SelectRoleToBeRemoved, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 280);    
        setupComboBoxUI(combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);    

        // GUI Area 3        
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((event) -> ControllerAddRemoveRoles.performReturn());

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((event) -> ControllerAddRemoveRoles.performLogout());
    
        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((event) -> ControllerAddRemoveRoles.performQuit());
    }    

    /*-*******************************************************************************************
     * Helper Methods
     ********************************************************************************************/

    /**********
     * <p>Initializes a Label with standard font, size, alignment, width, and position.</p>
     * 
     * @param l The Label object
     * @param ff Font family
     * @param f Font size
     * @param w Label width
     * @param p Alignment (e.g., Pos.CENTER)
     * @param x X-axis position
     * @param y Y-axis position
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }

    /**********
     * <p>Initializes a Button with standard font, size, alignment, width, and position.</p>
     * 
     * @param b The Button object
     * @param ff Font family
     * @param f Font size
     * @param w Button width
     * @param p Alignment
     * @param x X-axis position
     * @param y Y-axis position
     */
    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }

    /**********
     * <p>Initializes a ComboBox with standard font, size, width, and position.</p>
     * 
     * @param c The ComboBox object
     * @param ff Font family
     * @param f Font size
     * @param w ComboBox width
     * @param x X-axis position
     * @param y Y-axis position
     */
    protected static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y){
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}
