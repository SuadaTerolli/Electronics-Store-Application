package com.electronicstore.view;

import com.electronicstore.controller.LogInController;
import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LogIn extends Application {

    private LogInController loginController = new LogInController();

    @Override
    public void start(Stage primaryStage) {

        loginController.loadUsersFromFile("/files/user.csv");

        ImageView imageView = new ImageView(getClass().getResource("/logo.png").toExternalForm());
        imageView.setPreserveRatio(true);

        VBox imagePane = new VBox(imageView);
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setStyle("-fx-background-color: white;");
        imagePane.setPrefWidth(300);

        Label titleLabel = new Label("Portal Login");
        titleLabel.setStyle("-fx-font-size: 70px; -fx-font-weight: bold; -fx-text-fill: #1E4D92;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 14px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #1E4D92; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size:20px");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(45);

        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            User user = loginController.authenticate(username, password);

            if (user != null) {
                messageLabel.setText("Login Successful! Redirecting...");
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                navigateToRoleView(user.getAccess_level(), primaryStage, user);
            } else {
                messageLabel.setText("Invalid Username or Password.");
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            }
        });

        VBox formPane = new VBox(65, titleLabel, usernameField, passwordField, loginButton, messageLabel);
        formPane.setPadding(new Insets(20));
        formPane.setAlignment(Pos.CENTER);
        formPane.setStyle("-fx-background-color: #B3E5FC;");
        formPane.setPrefWidth(300);

        SplitPane splitPane = new SplitPane(imagePane, formPane);
        splitPane.setDividerPositions(0.5);

        Scene scene = new Scene(splitPane);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void navigateToRoleView(String role, Stage stage, User user) {
        Scene scene;

        switch (role.toLowerCase()) {
            case "manager":
                new ManagerView(user).start(stage);
                return;
            case "administrator":
                new AdministratorView(user).start(stage);
                return;
            case "cashier":
                new CashierViewMain(user).start(stage);
                return;
            default:
                scene = new Scene(new Label("Unknown Role!"), 400, 200);
                break;
        }
        stage.setScene(scene);
        stage.setTitle(role + " View");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
