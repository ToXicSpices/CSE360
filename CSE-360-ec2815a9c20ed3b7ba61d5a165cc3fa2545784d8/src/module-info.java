/**
 * e
 */
module FoundationsF25_2 {
    requires javafx.controls;
    requires java.sql;

    // Comment for Javadoc generation
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    opens applicationMain to javafx.graphics, javafx.fxml;
}
