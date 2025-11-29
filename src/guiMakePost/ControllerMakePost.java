package guiMakePost;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import entityClasses.User;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.StudentStatus;

/**
 * <p> Title: ControllerMakePost Class </p>
 * 
 * <p> Description: This controller class handles actions for the guiMakePost view.
 * It provides static methods to set post fields, add/remove tags, create posts,
 * and navigate to the user's home page. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @version 1.01 2025-11-13 Fully documented with Javadocs
 */
public class ControllerMakePost {

    /*-********************************************************************************************
     * Fields
     */

    /** Holds the result of dialogs for adding or removing tags */
    private static Optional<String> result;

    /** Holds the post title */
    private static String title = "";

    /** Holds the post subtitle */
    private static String subtitle = "";

    /** Holds the post content */
    private static String content = "";

    /** Holds the tags of the post */
    protected static ArrayList<String> tags = new ArrayList<String>();

    /**********
     * <p> Default constructor </p>
     * <p> Description: ControllerMakePost is not intended to be instantiated, 
     * but a default constructor is included for completeness. </p>
     */
    private ControllerMakePost() {}

    /**********
     * <p> Method: setTitle </p>
     * <p> Description: Sets the post title from the UI text field. </p>
     */
    protected static void setTitle() {
        title = ViewMakePost.text_Title.getText();
    }

    /**********
     * <p> Method: setSubtitle </p>
     * <p> Description: Sets the post subtitle from the UI text field. </p>
     */
    protected static void setSubtitle() {
        subtitle = ViewMakePost.text_Subtitle.getText();
    }

    /**********
     * <p> Method: setContent </p>
     * <p> Description: Sets the post content from the UI text area. </p>
     */
    protected static void setContent() {
        content = ViewMakePost.text_Content.getText();
    }

    /**********
     * <p> Method: doAddTag </p>
     * <p> Description: Adds a new tag to the post after validating it.
     * Updates the UI label and refreshes remove tag dialog options.
     * Limits the number of tags to 3. </p>
     */
    protected static void doAddTag() {
        result = ViewMakePost.dialogAddTags.showAndWait();
        result.ifPresent(tag -> {
            if (tags.size() >= 3) {
                ViewMakePost.alertAddTagError.setHeaderText("Too many tags!");
                ViewMakePost.alertAddTagError.setContentText("No more than 3 tags");
                ViewMakePost.alertAddTagError.showAndWait();
                return;
            }
            String tagError = ModelMakePost.checkValidTag(tag);
            if (!tagError.isEmpty()) {
                ViewMakePost.alertAddTagError.setHeaderText(tagError);
                ViewMakePost.alertAddTagError.showAndWait();
                return;
            }
            tags.add(tag.toLowerCase());
            String labelTags = "";
            for (int i = 0; i < tags.size(); i++) {
                labelTags += tags.get(i);
                if (i < tags.size() - 1) labelTags += " | ";
            }
            if (labelTags.length() > 40) {
                labelTags = labelTags.substring(0, 40) + "...";
            }
            ViewMakePost.label_TagsContent.setText(labelTags);
            ViewMakePost.dialogRemoveTag = new ChoiceDialog<String>(tags.get(0), tags);
            ViewMakePost.dialogRemoveTag.setTitle("Remove Tag");
            ViewMakePost.dialogRemoveTag.setHeaderText("Choose a tag to remove");
            ViewMakePost.dialogAddTags = new TextInputDialog("");
            ViewMakePost.dialogAddTags.setTitle("Add Tag");
            ViewMakePost.dialogAddTags.setHeaderText("Add a New Tag");
        });
    }

    /**********
     * <p> Method: doRemoveTag </p>
     * <p> Description: Removes a selected tag from the post and updates the UI label.
     * Refreshes the remove tag dialog options. </p>
     */
    protected static void doRemoveTag() {
        result = ViewMakePost.dialogRemoveTag.showAndWait();
        result.ifPresent(tag -> {
            tags.removeIf(t -> t.equals(tag));
            String labelTags = String.join(" | ", tags);
            if (labelTags.length() > 40) labelTags = labelTags.substring(0, 40) + "...";
            ViewMakePost.label_TagsContent.setText(labelTags);
            if (!tags.isEmpty()) {
                ViewMakePost.dialogRemoveTag = new ChoiceDialog<>(tags.get(0), tags);
            } else {
                ViewMakePost.dialogRemoveTag = new ChoiceDialog<>("<none>", tags);
            }
            ViewMakePost.dialogRemoveTag.setTitle("Remove Tag");
            ViewMakePost.dialogRemoveTag.setHeaderText("Choose a tag to remove");
        });
    }

    /**********
     * <p> Method: doMakePost </p>
     * <p> Description: Creates and submits a new post to the database.
     * Validates title, subtitle, and content. On success, navigates to the user's home page
     * and clears the UI fields. </p>
     */
    protected static void doMakePost() {
        String titleError = ModelMakePost.checkValidTitle(title);
        if (!titleError.isEmpty()) {
            ViewMakePost.alertTitleError.setHeaderText(titleError);
            ViewMakePost.alertTitleError.showAndWait();
            return;
        } else if (ViewMakePost.theDatabase.doesPostExistByTitle(title)) {
            ViewMakePost.alertTitleError.setHeaderText("Exists post with the same Title!");
            ViewMakePost.alertTitleError.showAndWait();
            return;
        }
        String subtitleError = ModelMakePost.checkValidSubtitle(subtitle);
        if (!subtitleError.isEmpty()) {
            ViewMakePost.alertSubtitleError.setHeaderText(subtitleError);
            ViewMakePost.alertSubtitleError.showAndWait();
            return;
        }
        String contentError = ModelMakePost.checkValidContent(content);
        if (!contentError.isEmpty()) {
            ViewMakePost.alertContentError.setHeaderText(contentError);
            ViewMakePost.alertContentError.showAndWait();
            return;
        }
        Post post = new Post(title, subtitle, content, ViewMakePost.theUser.getUserName(),
                tags, ViewMakePost.combobox_SelectThread.getValue());

        try {
            int genPostId = ViewMakePost.theDatabase.makePost(post);
            post.setPostId(genPostId);
            StudentStatus status = ViewMakePost.theDatabase.getStudentStatus(ViewMakePost.theUser.getUserName());
            status.setPostNumber(status.getPostNumber()+1);
            ViewMakePost.theDatabase.updateStudentStatus(status);
            goToUserHomePage(ViewMakePost.theStage, ViewMakePost.theUser);
        } catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to make a post: " +
                    e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }

        ViewMakePost.theDatabase.markPostAsRead(ViewMakePost.theUser.getUserName(), post.getPostId());
        ViewMakePost.text_Title.setText("");
        ViewMakePost.text_Subtitle.setText("");
        ViewMakePost.text_Content.setText("");
        ViewMakePost.label_TagsContent.setText("");
        ViewMakePost.combobox_SelectThread.getSelectionModel().selectFirst();
        tags = new ArrayList<>();
    }

    /**********
     * <p> Method: goToUserHomePage </p>
     * <p> Description: Navigates to the appropriate home page based on user's role.
     * 1 = Admin, 2 = Student, 3 = Staff. </p>
     * 
     * @param theStage The JavaFX stage
     * @param theUser The current user
     */
    protected static void goToUserHomePage(Stage theStage, User theUser) {
        int theRole = applicationMain.FoundationsMain.activeHomePage;
        switch (theRole) {
            case 1:
                guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
                break;
            case 2:
                guiStudentHome.ViewStudentHome.displayStudentHome(theStage, theUser);
                break;
            case 3:
                guiStaffHome.ViewStaffHome.displayStaffHome(theStage, theUser);
                break;
            default:
                System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " +
                        theRole);
                System.exit(0);
        }
    }

    /**********
     * <p> Method: performQuit </p>
     * <p> Description: Exits the program with exit code 0. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}
