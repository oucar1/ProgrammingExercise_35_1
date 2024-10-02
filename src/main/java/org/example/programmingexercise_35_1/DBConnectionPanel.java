package org.example.programmingexercise_35_1;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DBConnectionPanel extends GridPane {
    private TextField urlField = new TextField();
    private TextField userField = new TextField();
    private PasswordField passwordField = new PasswordField();

    public DBConnectionPanel() {
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setVgap(10);

        this.add(new Label("JDBC URL:"), 0, 0);
        this.add(urlField, 1, 0);
        this.add(new Label("Username:"), 0, 1);
        this.add(userField, 1, 1);
        this.add(new Label("Password:"), 0, 2);
        this.add(passwordField, 1, 2);

        // Set default values for easy testing
        urlField.setText("jdbc:mysql://localhost:3306/batch_db");
        userField.setText("root");
        passwordField.setText("Utku2022!");
    }

    public String getUrl() {
        return urlField.getText().trim();
    }

    public String getUser() {
        return userField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText().trim();
    }
}
