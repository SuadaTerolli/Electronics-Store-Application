package com.electronicstore.view;

import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class ManagerView extends Application {
    private User loggedInUser;

    public ManagerView(User loggedInUser)
    {
        this.loggedInUser=loggedInUser;
    }
    @Override
    public void start(Stage primaryStage){

        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        Button supplierButton = new Button("Supplier");
        supplierButton.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-background-radius: 20px; -fx-padding: 5 15;");
        supplierButton.setOnAction(event -> {
            ManagerSuppliersView suppliersView = new ManagerSuppliersView(loggedInUser);
            try {
                suppliersView.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            try {
                new LogIn().start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ImageView logoutIcon = new ImageView(new Image(getClass().getResource("/logout.png").toExternalForm()));
        logoutIcon.setFitHeight(40);
        logoutIcon.setFitWidth(40);
        logoutButton.setGraphic(logoutIcon);
        logoutButton.setStyle("-fx-background-color:  white; -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size: 16px;");

        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage,profileImage, leftSpacer, rightSpacer, supplierButton, logoutButton);
        topBar.setAlignment(Pos.CENTER);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        VBox leftPart=new VBox(20);
        leftPart.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");
        leftPart.setPrefWidth(200);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size:20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size:18;");

        leftPart.getChildren().addAll(profileImage, profileName, role);

        Button button1=new Button("Bills");
        button1.setWrapText(true);
        button1.setStyle("-fx-background-color: rgb(222, 220, 224); -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-background-radius: 20;-fx-border-width: 3px; -fx-border-radius: 20; -fx-border-color: rgb(5, 39, 75); -fx-font-size: 30;");
        button1.setPrefWidth(800);
        button1.setPrefHeight(150);
        button1.setOnAction(event -> {
            try {
                new ManagerViewBills(loggedInUser).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button button2=new Button("Cashier Performance");
        button2.setWrapText(true);
        button2.setStyle("-fx-background-color: rgb(222, 220, 224); -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-width: 3px; -fx-border-radius: 20; -fx-border-color: rgb(5, 39, 75);-fx-font-size: 30;");
        button2.setPrefWidth(800);
        button2.setPrefHeight(150);
        button2.setOnAction(event -> {
            try {
                new CashierView(loggedInUser).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button button3=new Button("Manage Items");
        button3.setWrapText(true);
        button3.setStyle("-fx-background-color: rgb(222, 220, 224); -fx-text-fill: rgb(5, 39, 75);-fx-font-weight: bold; -fx-background-radius: 20; -fx-border-width: 3px; -fx-border-radius: 20; -fx-border-color: rgb(5, 39, 75); -fx-font-size:30;");
        button3.setPrefWidth(800);
        button3.setPrefHeight(150);
        button3.setOnAction(event -> {
            try {
                new ManagerItemsView(loggedInUser).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox centerSpace=new VBox(20);
        centerSpace.getChildren().addAll(button1,button2,button3);
        centerSpace.setAlignment(Pos.CENTER);

        StackPane centerPane = new StackPane(centerSpace);
        centerPane.setAlignment(Pos.CENTER);


        BorderPane pane=new BorderPane();
        pane.setLeft(leftPart);
        pane.setCenter(centerPane);
        pane.setTop(topLayout);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Manager View");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }
    public static void main(String[] args){
        launch(args);
    }
}