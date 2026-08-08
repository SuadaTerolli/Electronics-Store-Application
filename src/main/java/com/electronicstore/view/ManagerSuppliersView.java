package com.electronicstore.view;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.electronicstore.model.Supplier;
import java.util.stream.Collectors;

import com.electronicstore.model.Category;
import com.electronicstore.model.Item;
import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManagerSuppliersView extends Application {
    private User loggedInUser;

    public ManagerSuppliersView(User loggedInUser)
    {
        this.loggedInUser=loggedInUser;
    }
    @Override
    public void start(Stage primaryStage){
        List<Supplier> suppliers = loadSuppliers("src/main/resources/files/suppliers.dat");
        List<Category> categories = loadCategories("src/main/resources/files/categories.csv");
        List<Item> items = loadItems("src/main/resources/files/items.csv", categories, suppliers);
        Map<String, Integer> sectors = loadSectors("src/main/resources/files/sectors.csv");


        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(255, 255, 255);");
        topBar.setAlignment(Pos.CENTER);
        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        ComboBox<String> sectionBox = new ComboBox<>();
        sectionBox.getItems().addAll(sectors.keySet());
        sectionBox.setValue("Choose Sector");
        sectionBox.setStyle("-fx-background-color: white;-fx-border-color:rgb(5, 39, 75); -fx-text-fill: rgb(92, 143, 198); -fx-font-weight: bold; -fx-font-size: 16px;-fx-background-radius: 15;-fx-border-radius: 15;");
        sectionBox.setPrefWidth(300);
        sectionBox.setPrefWidth(200);

        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

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
        topBar.getChildren().addAll(topLeftImage, leftSpacer, sectionBox, rightSpacer, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        VBox leftPart=new VBox(20);
        leftPart.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");


        leftPart.getChildren().addAll(profileImage, profileName, role);

        GridPane center=new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");
        center.setAlignment(Pos.CENTER);
        Button goBack=new Button ("< Go Back");
        goBack.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        center.add(goBack, 0, 0);
        goBack.setOnAction(event -> {
            ManagerView managerView = new ManagerView(loggedInUser);
            try {
                managerView.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Label suppliersLabel=new Label("Suppliers");
        suppliersLabel.setStyle("-fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size: 35;");
        center.add(suppliersLabel, 0, 1);

        TableView<SupplierEntry> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefSize(600, 400);


        TableColumn<SupplierEntry, String> supplierColumn = new TableColumn<>("Supplier");
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<SupplierEntry, String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<SupplierEntry, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<SupplierEntry, Double> purchasePriceColumn = new TableColumn<>("Purchase Price");
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));

        table.getColumns().addAll(supplierColumn, itemNameColumn, categoryColumn, purchasePriceColumn);

        sectionBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Choose Sector")) {
                int sectorId = sectors.get(newVal);
                List<SupplierEntry> filteredEntries = items.stream()
                        .filter(item -> item.getCategory() != null && item.getCategory().getCategoryId() == sectorId)
                        .map(item -> new SupplierEntry(
                                item.getSupplier().getName(),
                                item.getName(),
                                item.getCategory().getName(),
                                item.getPurchasePrice()
                        ))
                        .collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(filteredEntries));
            } else {
                table.setItems(FXCollections.observableArrayList());
            }
        });
        center.add(table, 0, 2);

        BorderPane pane=new BorderPane();
        pane.setLeft(leftPart);
        pane.setCenter(center);
        pane.setTop(topLayout);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Manager View");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }


    public List<Supplier> loadSuppliers(String binaryFilePath) {
        List<Supplier> suppliers = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(binaryFilePath))) {
            while (dis.available() > 0) {
                int id = dis.readInt();
                String name = dis.readUTF();
                suppliers.add(new Supplier(id, name, 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    private List<Category> loadCategories(String filePath) {
        List<Category> categories = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                categories.add(new Category(
                        Integer.parseInt(fields[0].trim()),
                        fields[1].trim()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }
    private List<Item> loadItems(String filePath, List<Category> categories, List<Supplier> suppliers) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int itemId = Integer.parseInt(fields[0].trim());
                String name = fields[1].trim();
                String categoryName = fields[2].trim();
                String supplierName = fields[3].trim();

                Category category = categories.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                        .findFirst()
                        .orElse(null);

                Supplier supplier = suppliers.stream()
                        .filter(s -> s.getName().equalsIgnoreCase(supplierName))
                        .findFirst()
                        .orElse(null);

                if (category == null) {
                    System.err.println("Category not found for item: " + name);
                    continue;
                }
                if (supplier == null) {
                    System.err.println("Supplier not found for item: " + name);
                    continue;
                }

                double purchasePrice = Double.parseDouble(fields[5].trim());
                double sellingPrice = Double.parseDouble(fields[6].trim());
                int quantity = Integer.parseInt(fields[7].trim());

                items.add(new Item(itemId, name, category, supplier, null, purchasePrice, sellingPrice, quantity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
    private Map<String, Integer> loadSectors(String filePath) {
        Map<String, Integer> sectors = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                sectors.put(fields[1].trim(), Integer.parseInt(fields[0].trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sectors;
    }
    public static class SupplierEntry {
        private final SimpleStringProperty supplier;
        private final SimpleStringProperty itemName;
        private final SimpleStringProperty category;
        private final SimpleDoubleProperty purchasePrice;

        public SupplierEntry(String supplier, String itemName, String category, double purchasePrice) {
            this.supplier = new SimpleStringProperty(supplier);
            this.itemName = new SimpleStringProperty(itemName);
            this.category = new SimpleStringProperty(category);
            this.purchasePrice = new SimpleDoubleProperty(purchasePrice);
        }

        public String getSupplier() {
            return supplier.get();
        }

        public String getItemName() {
            return itemName.get();
        }

        public String getCategory() {
            return category.get();
        }

        public double getPurchasePrice() {
            return purchasePrice.get();
        }
    }
    public static void main(String[] args){
        launch(args);
    }
}
