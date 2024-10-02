module org.example.programmingexercise_35_1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.example.programmingexercise_35_1 to javafx.fxml;
    exports org.example.programmingexercise_35_1;
}