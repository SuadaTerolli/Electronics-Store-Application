package com.electronicstore.view;


import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagerCreateItemView extends Application {
    private final String filePath = "src/main/resources/files/items.csv";
    private User loggedInUser;

    public ManagerCreateItemView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    public HashMap<String, Integer> loadCategoriesFromFile(String filePath) {
        HashMap<String, Integer> categoryMapping = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                int sectorId = Integer.parseInt(fields[0]);
                String sectorName = fields[1];
                categoryMapping.put(sectorName, sectorId);
            }
        } catch (IOException e) {
            System.out.println("Problem loading sector data");
            e.printStackTrace();
        }
        return categoryMapping;
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

        Button categoryButton = new Button("Create Category");
        categoryButton.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        categoryButton.setOnAction(e -> {
            ManagerCreateCategoryView categoryView = new ManagerCreateCategoryView(loggedInUser);
            try {
                categoryView.start(primaryStage);
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

        topBar.getChildren().addAll(topLeftImage, leftSpacer, rightSpacer, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75); -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        // Left part
        VBox leftPart = new VBox(20);
        leftPart.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        leftPart.getChildren().addAll(profileImage, profileName, role,categoryButton);

        // Center part
        GridPane center = new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");
        center.setAlignment(Pos.CENTER);

        Button goBack = new Button("< Go Back");
        goBack.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-background-radius: 20px; -fx-padding: 5 15;");
        goBack.setPrefWidth(200);
        goBack.setPrefHeight(50);
        goBack.setOnAction(e -> {
            ManagerItemsView itemsView = new ManagerItemsView(loggedInUser);
            try {
                itemsView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        center.add(goBack, 0, 0);

        Label addNewItem = new Label("Add New Item");
        addNewItem.setStyle("-fx-text-fill: rgb(0, 0, 0); -fx-font-weight: bold; -fx-font-size:30 ");
        center.add(addNewItem, 0, 1);

        TextField itemName = new TextField();
        itemName.setPromptText("Item Name");
        itemName.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        itemName.setPrefWidth(400);
        itemName.setPrefHeight(50);
        center.add(itemName, 0, 2);

        ComboBox<String> itemCategoryBox = new ComboBox<>();
        try {
            List<String> categories = getCategoriesFromCSV("src/main/resources/files/categories.csv");
            itemCategoryBox.getItems().addAll(categories);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading categories!", ButtonType.OK);
            alert.showAndWait();
        }
        itemCategoryBox.setPromptText("Choose Category");
        itemCategoryBox.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: rgb(92, 143, 198); -fx-font-size: 14px;");
        itemCategoryBox.setPrefWidth(400);
        itemCategoryBox.setPrefHeight(50);
        center.add(itemCategoryBox, 1, 2);

        HashMap<String, Integer> sectorMapping = loadCategoriesFromFile("src/main/resources/files/categories.csv");


        TextField supplierField = new TextField();
        supplierField.setPromptText("Supplier");
        supplierField.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        supplierField.setPrefWidth(400);
        supplierField.setPrefHeight(50);
        center.add(supplierField, 0, 3);

        TextField purchaseDate = new TextField();
        purchaseDate.setPromptText("Purchase Date (YYYY-MM-DD)");
        purchaseDate.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        purchaseDate.setPrefWidth(400);
        purchaseDate.setPrefHeight(50);
        center.add(purchaseDate, 1, 3);

        TextField purchasePrice = new TextField();
        purchasePrice.setPromptText("Purchase Price");
        purchasePrice.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        purchasePrice.setPrefWidth(400);
        purchasePrice.setPrefHeight(50);
        center.add(purchasePrice, 0, 4);

        TextField sellingPrice = new TextField();
        sellingPrice.setPromptText("Selling Price");
        sellingPrice.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        sellingPrice.setPrefWidth(400);
        sellingPrice.setPrefHeight(50);
        center.add(sellingPrice, 1, 4);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        quantityField.setPrefWidth(400);
        quantityField.setPrefHeight(50);
        center.add(quantityField, 0, 5);

        Button createItemButton = new Button("Create Item");
        createItemButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20; -fx-background-radius: 20;");
        createItemButton.setPrefWidth(400);
        createItemButton.setPrefHeight(60);
        createItemButton.setOnAction(e -> {
            try {
                String name = itemName.getText();
                String category = itemCategoryBox.getValue();
                String supplier = supplierField.getText();
                String date = purchaseDate.getText();
                double purchase = Double.parseDouble(purchasePrice.getText());
                double selling = Double.parseDouble(sellingPrice.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                appendItemToCSV(name, category, supplier, date, purchase, selling, quantity);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added successfully!", ButtonType.OK);
                alert.showAndWait();

                itemName.clear();
                itemCategoryBox.setValue("Choose Category");
                supplierField.clear();
                purchaseDate.clear();
                purchasePrice.clear();
                sellingPrice.clear();
                quantityField.clear();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please check the fields.", ButtonType.OK);
                alert.showAndWait();
            }
        });
        center.add(createItemButton, 0, 6, 2, 1);
        GridPane.setHalignment(createItemButton, javafx.geometry.HPos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftPart);
        pane.setCenter(center);
        pane.setTop(topLayout);

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Manager - Create Item");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void appendItemToCSV(String name, String category, String supplier, String date, double purchasePrice, double sellingPrice, int quantity) throws IOException {
        int newId = getLastIdFromCSV() + 1;
        String newItemEntry = newId + "," + name + "," + category + "," + supplier + "," + date + "," + purchasePrice + "," + sellingPrice + "," + quantity + "\n";

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(newItemEntry);
        }
    }

    private int getLastIdFromCSV() throws IOException {
        int lastId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id")) continue;
                String[] fields = line.split(",");
                lastId = Integer.parseInt(fields[0].trim());
            }
        }
        return lastId;
    }
    private List<String> getCategoriesFromCSV(String categoriesFilePath) throws IOException {
        List<String> categories = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(categoriesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("id")) continue; // Skip header or empty lines
                String[] fields = line.split(",");
                if (fields.length > 1) {
                    categories.add(fields[1].trim());
                }
            }
        }
        return categories;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
