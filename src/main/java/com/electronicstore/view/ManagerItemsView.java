package com.electronicstore.view;

import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ManagerItemsView extends Application {
    private User loggedInUser;
    private TableView<Item> table;
    private HashMap<String, Integer> sectorMapping;

    public ManagerItemsView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public HashMap<String, Integer> loadSectorsFromFile(String filePath) {
        HashMap<String, Integer> sectorMapping = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                int sectorId = Integer.parseInt(fields[0]);
                String sectorName = fields[1];
                sectorMapping.put(sectorName, sectorId);
            }
        } catch (IOException e) {
            System.out.println("Problem loading sector data");
            e.printStackTrace();
        }
        return sectorMapping;
    }

    @Override
    public void start(Stage primaryStage) {
        sectorMapping = loadSectorsFromFile("src/main/resources/files/sectors.csv");

        VBox leftSide = new VBox(20);
        leftSide.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");
        leftSide.setPrefWidth(200);

        ImageView profileImage = new ImageView(getClass().getResource("/user.png").toExternalForm());
        profileImage.setFitHeight(130);
        profileImage.setFitWidth(130);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");

        Button createItem = new Button("Create Item");
        createItem.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        createItem.setOnAction(e -> {
            ManagerCreateItemView createItemView = new ManagerCreateItemView(loggedInUser);
            try {
                createItemView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button createCategory = new Button("Create Category");
        createCategory.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        createCategory.setOnAction(e -> {
            ManagerCreateCategoryView createCategoryView = new ManagerCreateCategoryView(loggedInUser);
            try {
                createCategoryView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        leftSide.getChildren().addAll(profileImage, profileName, role, createItem, createCategory);

        HBox topBar = new HBox(15);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(245, 245, 245);");
        topBar.setPrefHeight(80);
        topBar.setAlignment(Pos.CENTER);

        ComboBox<String> sectorComboBox = new ComboBox<>();
        sectorComboBox.getItems().addAll(sectorMapping.keySet());
        sectorComboBox.setValue("Choose Sector");
        sectorComboBox.setStyle("-fx-background-color: white;-fx-border-color:rgb(5, 39, 75); -fx-text-fill: rgb(92, 143, 198); -fx-font-weight: bold; -fx-font-size: 16px;-fx-background-radius: 15;-fx-border-radius: 15;");
        sectorComboBox.setPrefWidth(300);
        sectorComboBox.setPrefWidth(200);
        sectorComboBox.setOnAction(e -> {
            String selectedSector = sectorComboBox.getValue();
            if (!"Choose Sector".equals(selectedSector)) {
                List<Item> filteredItems = filterItemsBySector("src/main/resources/files/items.csv", selectedSector);
                table.setItems(FXCollections.observableArrayList(filteredItems));
            }
        });

        Button goBack = new Button("< Go Back");
        goBack.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        goBack.setOnAction(e -> {
            ManagerView managerView = new ManagerView(loggedInUser);
            try {
                managerView.start(primaryStage);
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView topLeftImage = new ImageView(getClass().getResource("/logo.png").toExternalForm());
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage, spacer, sectorComboBox, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75); -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        VBox center = new VBox(20);
        center.setStyle("-fx-padding: 20;");

        Label title = new Label("Items");
        title.setStyle("-fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size: 28px;");

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefSize(600, 400);

        TableColumn<Item, String> nameColumn = new TableColumn<>("Item Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Item, String> supplierColumn = new TableColumn<>("Supplier");
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<Item, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Item, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Item, String> stockWarningColumn = new TableColumn<>("Stock Warning");
        stockWarningColumn.setCellValueFactory(new PropertyValueFactory<>("stockWarning"));
        stockWarningColumn.setCellFactory(column -> new TableCell<Item, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setTextFill(javafx.scene.paint.Color.RED);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        table.getColumns().addAll(nameColumn, categoryColumn, supplierColumn, sellingPriceColumn, quantityColumn, stockWarningColumn);

        List<Item> items = loadItems("src/main/resources/files/items.csv");
        table.setItems(FXCollections.observableArrayList(items));

        center.getChildren().addAll(goBack, title, table);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Dashboard/Manager/Items");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private List<Item> loadItems(String filePath) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 8) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }
                items.add(new Item(
                        fields[1].trim(),
                        fields[2].trim(),
                        fields[3].trim(),
                        Double.parseDouble(fields[6].trim()),
                        Integer.parseInt(fields[7].trim())
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private List<Item> filterItemsBySector(String filePath, String sectorName) {
        List<Item> filteredItems = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 8) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }
                int sectorId = Integer.parseInt(fields[8].trim());
                String itemSectorName = sectorMapping.entrySet().stream()
                        .filter(entry -> entry.getValue() == sectorId)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);

                if (itemSectorName != null && itemSectorName.equalsIgnoreCase(sectorName)) {
                    filteredItems.add(new Item(
                            fields[1].trim(),
                            fields[2].trim(),
                            fields[3].trim(),
                            Double.parseDouble(fields[6].trim()),
                            Integer.parseInt(fields[7].trim())
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredItems;
    }


    public static class Item {
        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleStringProperty supplier;
        private final SimpleDoubleProperty sellingPrice;
        private final SimpleIntegerProperty quantity;
        private final SimpleStringProperty stockWarning;

        public Item(String name, String category, String supplier, double sellingPrice, int quantity) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.supplier = new SimpleStringProperty(supplier);
            this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.stockWarning = new SimpleStringProperty("");  // Since this datafield is final it
                                                                // must be initialized in every constructor
            if (quantity < 5) {
                this.stockWarning.set("Low stock!");
            }
        }

        public Item(String name, String category, String supplier, double sellingPrice, int quantity,String stockWarning) {
            this.name = new SimpleStringProperty(name);
            this.category = new SimpleStringProperty(category);
            this.supplier = new SimpleStringProperty(supplier);
            this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.stockWarning = new SimpleStringProperty("");
            if (quantity < 5) {
                this.stockWarning.set("Low stock!");
            }
        }

        public String getName() {
            return name.get();
        }

        public String getCategory() {
            return category.get();
        }

        public String getSupplier() {
            return supplier.get();
        }

        public double getSellingPrice() {
            return sellingPrice.get();
        }

        public int getQuantity() {
            return quantity.get();
        }
        public String getStockWarning() {
            return stockWarning.get();
        }

        public void setStockWarning(String stockWarning) {
            this.stockWarning.set(stockWarning);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
