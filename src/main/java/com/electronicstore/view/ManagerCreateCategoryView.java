package com.electronicstore.view;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.electronicstore.model.User;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ManagerCreateCategoryView extends Application {

    private final String categoriesFilePath = "src/main/resources/files/categories.csv";
    private User loggedInUser;

    public ManagerCreateCategoryView() {
    }

    public ManagerCreateCategoryView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void start(Stage primaryStage) {

        // Top bar
        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(255, 255, 255);");
        topBar.setAlignment(Pos.CENTER);
        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button supplierButton = new Button("Suppliers");
        supplierButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        supplierButton.setOnAction(e -> {
            ManagerSuppliersView supplierView = new ManagerSuppliersView(loggedInUser);
            try {
                supplierView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button logoutButton = new Button("Logout");
        ImageView logoutIcon = new ImageView(new Image(getClass().getResource("/logout.png").toExternalForm()));
        logoutIcon.setFitHeight(40);
        logoutIcon.setFitWidth(40);
        logoutButton.setGraphic(logoutIcon);
        logoutButton.setStyle("-fx-background-color:  white; -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold ");

        logoutButton.setOnAction(event -> {
            try {
                new LogIn().start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        topBar.getChildren().addAll(topLeftImage,leftSpacer, rightSpacer, supplierButton, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        // Left part
        VBox leftPart = new VBox(20);
        leftPart.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");
        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);


        Label profileName = new Label( (loggedInUser != null ? loggedInUser.getName() : "Manager"));
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        Label role = new Label("Role: Manager");
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        leftPart.getChildren().addAll(profileImage,profileName, role);

        // Center part
        GridPane center = new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");
        center.setAlignment(Pos.CENTER);

        Button goBack = new Button("< Go Back");
        goBack.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        goBack.setPrefWidth(100);
        goBack.setPrefHeight(40);
        goBack.setOnAction(e -> {
            ManagerItemsView itemsView = new ManagerItemsView(loggedInUser);
            try {
                itemsView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        center.add(goBack, 0, 0);

        Label addNewCategory = new Label("Add New Item Category");
        addNewCategory.setStyle("-fx-text-fill: rgb(0, 0, 0); -fx-font-weight: bold; -fx-font-size:30 ");

        center.add(addNewCategory, 0, 1);

        TextField categoryName = new TextField();
        categoryName.setPromptText("Category Name");
        categoryName.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        categoryName.setPrefWidth(400);
        categoryName.setPrefHeight(50);
        center.add(categoryName, 0, 2);

        Button createCategoryButton = new Button("Create Category");
        createCategoryButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 20;");
        createCategoryButton.setPrefWidth(300);
        createCategoryButton.setPrefHeight(40);
        createCategoryButton.setOnAction(e -> {
            try {
                String name = categoryName.getText().trim();
                if (name.isEmpty()) {
                    showAlert(AlertType.ERROR, "Category name cannot be empty.");
                    return;
                }

                appendCategoryToCSV(name);
                showAlert(AlertType.INFORMATION, "Category added successfully!");

                categoryName.clear();
            } catch (IOException ex) {
                showAlert(AlertType.ERROR, "Error writing to the categories file.");
            }
        });
        center.add(createCategoryButton, 0, 3, 2, 1);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftPart);
        pane.setCenter(center);
        pane.setTop(topLayout);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Manager - Create Category");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void appendCategoryToCSV(String category) throws IOException {
        int newId = getLastIdFromCSV() + 1;
        String newCategoryEntry = newId + "," + category + "\n";

        try (FileWriter writer = new FileWriter(categoriesFilePath, true)) {
            writer.write(newCategoryEntry);
        }
    }

    private int getLastIdFromCSV() throws IOException {
        int lastId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(categoriesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id")) continue;
                String[] fields = line.split(",");
                lastId = Integer.parseInt(fields[0].trim());
            }
        }
        return lastId;
    }

    private void showAlert(AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }
    public static void main(String[] args){
        launch(args);
    }


}