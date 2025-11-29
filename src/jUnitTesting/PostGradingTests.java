package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.ArrayList;
import database.Database;
import entityClasses.Post;
import entityClasses.User;

/**
 * Tests for grading feature on Posts.
 */
public class PostGradingTests {

    private Database dbHelper;

    private final String STUDENT = "studentUser";
    private final String STAFF = "staffUser";

    @BeforeEach
    public void setup() throws SQLException {
        dbHelper = new Database();
        dbHelper.connectToDatabase();
        dbHelper.clearDatabase();
        dbHelper.connectToDatabase();
        dbHelper.addThread("General");
        // Create student and staff accounts
        User student = new User(STUDENT, "pw", "F","M","L","PF","s@email", false, true, false);
        User staff = new User(STAFF, "pw", "F","M","L","PF","t@email", false, false, true);
        dbHelper.register(student);
        dbHelper.register(staff);
    }

    @AfterEach
    public void teardown() throws SQLException {
        dbHelper.clearDatabase();
    }

    @Test
    public void testGradeAssignAndEdit() throws SQLException {
        Post p = new Post("Grade Me","","Content body", STUDENT, new ArrayList<>(), "General");
        int id = dbHelper.makePost(p);
        assertTrue(id > 0);
        dbHelper.setPostGrade(id, "95", "Good work", STAFF);
        assertEquals("95", dbHelper.getPostGrade(id));
        assertEquals("Good work", dbHelper.getPostFeedback(id));
        assertFalse(dbHelper.isPostGradeReleased(id));
        // Edit grade
        dbHelper.setPostGrade(id, "98", "Improved after re-eval", STAFF);
        assertEquals("98", dbHelper.getPostGrade(id));
        assertEquals("Improved after re-eval", dbHelper.getPostFeedback(id));
    }

    @Test
    public void testReleaseAllGrades() throws SQLException {
        Post p1 = new Post("P1","","C1", STUDENT, new ArrayList<>(), "General");
        Post p2 = new Post("P2","","C2", STUDENT, new ArrayList<>(), "General");
        int id1 = dbHelper.makePost(p1);
        int id2 = dbHelper.makePost(p2);
        dbHelper.setPostGrade(id1, "A", "Excellent", STAFF);
        dbHelper.setPostGrade(id2, "B", "Solid", STAFF);
        dbHelper.releaseAllGrades();
        assertTrue(dbHelper.isPostGradeReleased(id1));
        assertTrue(dbHelper.isPostGradeReleased(id2));
    }
}
