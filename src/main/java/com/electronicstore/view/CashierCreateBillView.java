package com.electronicstore.view;

import com.electronicstore.model.Bill;
import com.electronicstore.model.BillItem;
import com.electronicstore.model.Category;
import com.electronicstore.model.Item;
import com.electronicstore.model.Supplier;
import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.ListChangeListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashierCreateBillView extends Application {
    private User loggedInUser;
    private Label totalPriceLbl = new Label("0.00");
    private ObservableList<BillItem> billItems = FXCollections.observableArrayList();
    File billsFile = new File("src/main/resources/files/bills.csv");
    File itemsFile = new File("src/main/resources/files/items.csv");

    public CashierCreateBillView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @SuppressWarnings({ "unchecked", "unlikely-arg-type" })
    @Override
    public void start(Stage primaryStage) {
        VBox leftSide = new VBox(20);
        leftSide.setStyle("-fx-padding: 40; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(80);
        profileImage.setFitWidth(80);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");
        Label sector = new Label("Sector: " + getSectorForUser(loggedInUser));
        sector.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        leftSide.getChildren().addAll(profileImage, profileName, role,sector);

        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(255, 255, 255);");

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
        logoutButton.setStyle("-fx-background-color: white; -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold ");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75); -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        GridPane center = new GridPane();
        center.setVgap(10);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");

        Button goBack = new Button("< Go Back");
        goBack.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        center.add(goBack, 0, 0);
        goBack.setOnAction(e -> {
            CashierViewMain cashierView = new CashierViewMain(loggedInUser);
            try {
                cashierView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label createNewBill = new Label("Create New Bill");
        createNewBill.setStyle("-fx-text-fill: rgb(0, 0, 0); -fx-font-weight: bold; -fx-font-size: 20;");
        center.add(createNewBill, 0, 1);

        ComboBox<Item> itemBox = new ComboBox<>();
        ObservableList<Item> items = FXCollections.observableArrayList(new ItemLoader().loadItems());
        itemBox.setItems(items);

        itemBox.setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        itemBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        itemBox.setPromptText("Select An Item");
        itemBox.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: rgb(92, 143, 198); -fx-font-size: 14px;");
        center.add(itemBox, 0, 2);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-text-fill: black; -fx-font-size: 14px;");
        center.add(quantityField, 1, 2);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: rgb(187, 0, 0); -fx-font-size: 14px;");
        errorLabel.setVisible(false);
        center.add(errorLabel, 0, 3, 2, 1);

        TableView<BillItem> billTable = new TableView<>();
        billTable.setStyle("-fx-padding: 10;");
        billTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        billTable.setPrefHeight(600);
        billTable.setPrefWidth(600);
        billTable.setPlaceholder(new Label("No items added."));

        Label totalLabel = new Label("Total Price: ");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        totalPriceLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox priceBox = new HBox(totalLabel, totalPriceLbl);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        center.add(priceBox, 2, 6);

        ObservableList<BillItem> billItems = FXCollections.observableArrayList();
        billTable.setItems(billItems);

        billItems.addListener((ListChangeListener<BillItem>) change-> {
            double total = billItems.stream()
                    .mapToDouble(billItem -> billItem.getItem().getSellingPrice() * billItem.getItemQuantity())
                    .sum();
            totalPriceLbl.setText(String.format("%.2f", total));
        });

        TableColumn<BillItem, String> itemColumn = new TableColumn<>("Item");
        itemColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItem().getName()));

        TableColumn<BillItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getItemQuantity()).asObject());

        TableColumn<BillItem, Void> editColumn = new TableColumn<>("Edit Quantity");

        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("+");
            private final Button subtractButton = new Button("-");
            private final Button removeButton = new Button("Remove");

            {
                addButton.setOnAction(event -> {
                    BillItem billItem = getTableView().getItems().get(getIndex());
                    billItem.setItemQuantity(billItem.getItemQuantity() + 1);
                    billTable.refresh();
                });

                subtractButton.setOnAction(event -> {
                    BillItem billItem = getTableView().getItems().get(getIndex());
                    if (billItem.getItemQuantity() > 1) {
                        billItem.setItemQuantity(billItem.getItemQuantity() - 1);
                    } else {
                        getTableView().getItems().remove(billItem);
                    }
                    billTable.refresh();
                });

                removeButton.setOnAction(event -> {
                    BillItem billItem = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(billItem);
                    billTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, addButton, subtractButton, removeButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        TableColumn<BillItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getItem().getSellingPrice() * data.getValue().getItemQuantity()).asObject());

        billTable.getColumns().addAll(itemColumn, quantityColumn, editColumn, priceColumn);

        Button addItem = new Button("+");
        addItem.setStyle("-fx-background-color: rgb(228, 234, 239); -fx-text-fill: blue; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%;");
        addItem.setOnAction(event -> {
            Item selectedItem = itemBox.getValue();
            String quantityText = quantityField.getText();
            errorLabel.setVisible(false);

            if (selectedItem == null) {
                errorLabel.setText("Please select an item.");
                errorLabel.setVisible(true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    errorLabel.setText("Quantity must be greater than zero.");
                    errorLabel.setVisible(true);
                    return;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Invalid quantity. Please enter a valid number.");
                errorLabel.setVisible(true);
                return;
            }

            BillItem existingItem = billItems.stream()
                    .filter(item -> item.getItem().equals(selectedItem))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setItemQuantity(existingItem.getItemQuantity() + quantity);
            } else {
                billItems.add(new BillItem(selectedItem, quantity));
            }

            itemBox.setValue(null);
            quantityField.clear();
        });

        center.add(billTable, 2, 0, 1, 5);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Item...");
        searchField.setStyle("-fx-background-color: rgb(255, 255, 255); -fx-font-size: 14px;");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-size: 14px;");

        searchButton.setOnAction(event -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            ObservableList<BillItem> filteredItems = FXCollections.observableArrayList();

            for (BillItem item : billItems) {
                if (item.getItem().getName().toLowerCase().contains(searchTerm)) {
                    filteredItems.add(item);
                }
            }

            if (filteredItems.isEmpty()) {
                billTable.setPlaceholder(new Label("No items match the search term."));
            }
            billTable.setItems(filteredItems);
        });

        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: rgb(228, 234, 239); -fx-text-fill: rgb(19, 68, 121); -fx-font-size: 14px;");
        resetButton.setOnAction(event -> {
            billTable.setItems(billItems);
            searchField.clear();
            billTable.setPlaceholder(new Label("No items added."));
        });

        HBox searchBox = new HBox(searchField, searchButton, resetButton);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        center.add(searchBox, 2, 5);
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Button addItembtn = new Button("+");
        addItembtn.setStyle("-fx-background-color: rgb(228, 234, 239); -fx-text-fill: blue; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50%;");

        Button createBillButton = new Button("Create Bill");
        createBillButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 20;");

        createBillButton.setOnAction(event -> {
            if (billItems.isEmpty()) {
                errorLabel.setText("Cannot create an empty bill.");
                errorLabel.setVisible(true);
                return;
            }

            Date currentDate = new Date();
            double totalPrice = billItems.stream()
                    .mapToDouble(billItem -> billItem.getItem().getSellingPrice() * billItem.getItemQuantity())
                    .sum();

            ArrayList<BillItem> billItemList = new ArrayList<>(billItems);

            int sectorId = 1;

            int newBillId = generateNewBillId(); // Ensure you have a method to generate a new Bill ID
            String billNumber = "INV" + String.format("%03d", newBillId);

            Bill newBill = new Bill(
                    newBillId,
                    billNumber,
                    currentDate,
                    billItemList,
                    totalPrice,
                    sectorId
            );

            appendBillToCSV(newBill);

            billItems.clear();
            errorLabel.setVisible(false);
            showAlert(Alert.AlertType.INFORMATION, "Bill created successfully.");
        });
        addItembtn.setOnAction(event -> {
            Item selectedItem = itemBox.getValue();
            String quantityText = quantityField.getText();

            errorLabel.setVisible(false);

            if (selectedItem == null || selectedItem.equals("Select An Item")) {
                errorLabel.setText("Please select an item.");
                errorLabel.setVisible(true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    errorLabel.setText("Quantity must be greater than zero.");
                    errorLabel.setVisible(true);
                    return;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Invalid quantity. Please enter a valid number.");
                errorLabel.setVisible(true);
                return;
            }

            boolean itemExists = false;
            for (BillItem item : billItems) {
                if (item.getItem().equals(selectedItem)) {
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                billItems.add(new BillItem(selectedItem, quantity));
            }

            itemBox.setStyle("Select An Item");
            quantityField.clear();
        });

        buttonContainer.getChildren().addAll(addItembtn, createBillButton);
        center.add(buttonContainer, 0, 4, 2, 1);
        GridPane.setHalignment(buttonContainer, HPos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cashier - Create Bill");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private int generateNewBillId() {
        File billsFile = new File("src/main/resources/files/bills.csv");
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(billsFile))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                int billId = Integer.parseInt(line.split(",")[0]);
                maxId = Math.max(maxId, billId);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error generating Bill ID: " + e.getMessage());
        }
        return maxId + 1;
    }

    private void appendBillToCSV(Bill bill) {
        File billsFile = new File("src/main/resources/files/bills.csv");
        File itemsFile = new File("src/main/resources/files/items.csv");

        try (BufferedWriter billsWriter = new BufferedWriter(new FileWriter(billsFile, true))) {
            StringBuilder itemsString = new StringBuilder();
            for (BillItem billItem : bill.getBillItems()) {
                Item item = billItem.getItem();
                int quantitySold = billItem.getItemQuantity();
                itemsString.append(item.getName())
                        .append(" (")
                        .append(item.getSupplier().getName())
                        .append(") Quantity: ")
                        .append(quantitySold)
                        .append(" ");

                updateItemQuantity(itemsFile, item.getItemId(), -quantitySold);
            }

            if (itemsString.length() > 0) {
                itemsString.setLength(itemsString.length() - 2);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(bill.getBillDate());

            String billEntry = String.format(
                    "%d,%s,%s,%.2f,%d,\"%s\",%d",
                    bill.getBillId(),
                    bill.getBillNumber(),
                    formattedDate,
                    bill.getTotalAmount(),
                    bill.getSectorId(),
                    itemsString,
                    4
            );
            billsWriter.write(billEntry);
            billsWriter.newLine();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating bills file: " + e.getMessage());
        }
    }


    private void updateItemQuantity(File itemsFile, int itemId, int quantityChange) {
        File tempFile = new File("src/main/resources/files/tempItems.csv");
        try (
                BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String header = reader.readLine();
            writer.write(header);
            writer.newLine();

            String line;
            boolean itemFound = false;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                int currentItemId = Integer.parseInt(columns[0]);

                if (currentItemId == itemId) {
                    int currentQuantity = Integer.parseInt(columns[8]);
                    int newQuantity = currentQuantity + quantityChange;

                    if (newQuantity < 0) {
                        showAlert(Alert.AlertType.ERROR, "Not enough stock for item: " + columns[1]);
                        return;
                    }

                    columns[8] = String.valueOf(newQuantity);
                    itemFound = true;
                }

                writer.write(String.join(",", columns));
                writer.newLine();
            }

            if (!itemFound) {
                showAlert(Alert.AlertType.ERROR, "Item with ID " + itemId + " not found.");
                return;
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error updating items file: " + e.getMessage());
        }

        if (!itemsFile.delete()) {
            showAlert(Alert.AlertType.ERROR, "Failed to delete original items file.");
            return;
        }

        if (!tempFile.renameTo(itemsFile)) {
            showAlert(Alert.AlertType.ERROR, "Failed to rename temporary file to original file.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public String getSectorForUser(User user) {
        String filePath = "src/main/resources/files/user.csv";
        String sectorName = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String username = parts[2].trim();
                    String sectorNameValue = parts[9].trim();
                    if (username.equals(user.getUsername())) {
                        sectorName = sectorNameValue.isEmpty() ? null : sectorNameValue;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sectorName;
    }

    public class ItemLoader {

        public List<Item> loadItems() {
            List<Item> items = new ArrayList<>();
            String filePath = "src/main/resources/files/items.csv";

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    int itemId = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    Category category = new Category();
                    Supplier supplier = new Supplier();
                    Date purchaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(data[4].trim());
                    double purchasePrice = Double.parseDouble(data[5].trim());
                    double sellingPrice = Double.parseDouble(data[6].trim());
                    int quantity = Integer.parseInt(data[7].trim());

                    Item item = new Item(itemId, name, category, supplier, purchaseDate, purchasePrice, sellingPrice, quantity);
                    items.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return items;
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

}