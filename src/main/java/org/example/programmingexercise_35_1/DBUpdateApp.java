package org.example.programmingexercise_35_1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class DBUpdateApp extends Application {
    private DBConnectionPanel connectionPanel = new DBConnectionPanel();
    private Connection connection;
    private Stage batchUpdateStage;

    @Override
    public void start(Stage primaryStage) {
        Button connectButton = new Button("Connect to Database");

        // Set up connection window layout
        VBox connectionLayout = new VBox(10);
        connectionLayout.setPadding(new Insets(10));
        connectionLayout.getChildren().addAll(connectionPanel, connectButton);

        Scene connectionScene = new Scene(connectionLayout, 450, 200);
        primaryStage.setTitle("Database Connection");
        primaryStage.setScene(connectionScene);
        primaryStage.show();

        // Action for connecting to the database
        connectButton.setOnAction(e -> {
            if (connectToDatabase()) {
                openBatchUpdateWindow();
            }
        });

        primaryStage.setOnCloseRequest(e -> closeConnection()); // Close connection on exit
    }

    private boolean connectToDatabase() {
        try {
            connection = DriverManager.getConnection(connectionPanel.getUrl(), connectionPanel.getUser(), connectionPanel.getPassword());
            if (connection != null) {
                System.out.println("Successfully connected to the database!");
                return true;
            }
        } catch (SQLException ex) {
            System.err.println("Failed to connect to database: " + ex.getMessage());
        }
        return false;
    }

    private void openBatchUpdateWindow() {
        if (batchUpdateStage == null) {
            batchUpdateStage = new Stage();
            batchUpdateStage.setTitle("Batch Update Comparison");

            // Batch Update and Non-Batch Update buttons
            Button batchUpdateButton = new Button("Batch Update");
            Button nonBatchUpdateButton = new Button("Non-Batch Update");

            // Labels to display elapsed times
            Label batchUpdateResultLabel = new Label("Batch Update Result: Not yet executed.");
            Label nonBatchUpdateResultLabel = new Label("Non-Batch Update Result: Not yet executed.");

            // Actions for batch and non-batch updates
            batchUpdateButton.setOnAction(e -> performBatchUpdate(batchUpdateResultLabel));
            nonBatchUpdateButton.setOnAction(e -> performNonBatchUpdate(nonBatchUpdateResultLabel));

            // Layout for batch update
            VBox batchUpdateLayout = new VBox(10);
            batchUpdateLayout.setAlignment(Pos.CENTER);
            batchUpdateLayout.getChildren().addAll(batchUpdateButton, batchUpdateResultLabel);

            // Layout for non-batch update
            VBox nonBatchUpdateLayout = new VBox(10);
            nonBatchUpdateLayout.setAlignment(Pos.CENTER);
            nonBatchUpdateLayout.getChildren().addAll(nonBatchUpdateButton, nonBatchUpdateResultLabel);

            // Combine both batch and non-batch layouts
            VBox mainUpdateLayout = new VBox(20);
            mainUpdateLayout.setPadding(new Insets(10));
            mainUpdateLayout.setAlignment(Pos.CENTER);
            mainUpdateLayout.getChildren().addAll(nonBatchUpdateLayout, batchUpdateLayout);

            Scene batchUpdateScene = new Scene(mainUpdateLayout, 500, 400);
            batchUpdateStage.setScene(batchUpdateScene);
        }
        batchUpdateStage.show();
    }

    private void performBatchUpdate(Label resultLabel) {
        if (!isConnected()) {
            resultLabel.setText("Batch Update Result: No active database connection.");
            return;
        }

        try {
            connection.setAutoCommit(false);
            String query = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            Random random = new Random();
            // Start time in nanoseconds
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                preparedStatement.setDouble(1, random.nextDouble());
                preparedStatement.setDouble(2, random.nextDouble());
                preparedStatement.setDouble(3, random.nextDouble());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            connection.commit();

            long elapsedTime = System.nanoTime() - startTime; // Elapsed time in nanoseconds
            long elapsedTimeInMicroseconds = elapsedTime / 1000; // Convert to microseconds

            resultLabel.setText("Batch update completed\nElapsed time: " + elapsedTimeInMicroseconds + " microseconds.");
        } catch (SQLException ex) {
            resultLabel.setText("Batch Update Result: Failed - " + ex.getMessage());
            try {
                if (connection != null) {
                    // Rollback if there's an error during batch update
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                resultLabel.setText("Batch Update Result: Failed to rollback - " + rollbackEx.getMessage());
            }
        }
    }

    private void performNonBatchUpdate(Label resultLabel) {
        if (!isConnected()) {
            resultLabel.setText("Non-Batch Update Result: No active database connection.");
            return;
        }

        try {
            String query = "INSERT INTO Temp (num1, num2, num3) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            Random random = new Random();
            long startTime = System.nanoTime(); // Start time in nanoseconds

            for (int i = 0; i < 1000; i++) {
                preparedStatement.setDouble(1, random.nextDouble());
                preparedStatement.setDouble(2, random.nextDouble());
                preparedStatement.setDouble(3, random.nextDouble());
                preparedStatement.executeUpdate();
            }

            long elapsedTime = System.nanoTime() - startTime; // Elapsed time in nanoseconds
            long elapsedTimeInMicroseconds = elapsedTime / 1000; // Convert to microseconds

            resultLabel.setText("Non-Batch update completed\nElapsed time: " + elapsedTimeInMicroseconds + " microseconds.");
        } catch (SQLException ex) {
            resultLabel.setText("Non-Batch Update Result: Failed - " + ex.getMessage());
        }
    }

    private boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException ex) {
            System.err.println("Failed to verify connection: " + ex.getMessage());
            return false;
        }
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException ex) {
            System.err.println("Failed to close the database connection: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
