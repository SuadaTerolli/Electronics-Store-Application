package com.electronicstore.view;

import com.electronicstore.model.Bill;
import com.electronicstore.model.User;
import javafx.application.Application;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashierViewMain extends Application {
    private User loggedInUser;
    private ObservableList<Bill> allBills;

    public CashierViewMain(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    private TableView<Bill> createTableView() {
        TableView<Bill> tableView = new TableView<>();

        TableColumn<Bill, String> billNumberColumn = new TableColumn<>("Bill Number");
        billNumberColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));

        TableColumn<Bill, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        TableColumn<Bill, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<Bill, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

        tableView.getColumns().addAll(billNumberColumn, quantityColumn, priceColumn, dateColumn);

        tableView.setPrefHeight(200);

        return tableView;
    }

    @Override
    public void start(Stage primaryStage) {

        allBills = FXCollections.observableArrayList(loadBillsFromFile("src/main/resources/files/bills.csv"));

        VBox leftSide = new VBox(20);
        leftSide.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");
        Label sector = new Label("Sector: " + getSectorForUser(loggedInUser));
        sector.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75); -fx-font-size: 16px;");

        Button createBillButton = new Button("Create Bill");
        createBillButton.setStyle("-fx-background-color: rgb(19, 68, 121); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 20;");
        createBillButton.setOnAction(event -> {
            try {
                new CashierCreateBillView(loggedInUser).start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        leftSide.getChildren().addAll(profileImage, profileName, role, sector, createBillButton);

        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(255, 255, 255);");
        topBar.setAlignment(Pos.CENTER);

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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage, spacer, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        GridPane center = new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");

        Label billsFromTodayLabel = new Label("Bills from today");
        billsFromTodayLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);");
        center.add(billsFromTodayLabel, 0, 0);

        TableView<Bill> billsFromTodayTable = createTableView();
        billsFromTodayTable.setItems(filterBillsByDate(new Date()));
        center.add(billsFromTodayTable, 0, 1);

        Label overviewOfBillsLabel = new Label("Overview of Bills (Total)");
        overviewOfBillsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);");
        center.add(overviewOfBillsLabel, 0, 2);

        TableView<Bill> overviewOfBillsTable = createTableView();
        overviewOfBillsTable.setItems(allBills);
        center.add(overviewOfBillsTable, 0, 3);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Cashier");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private ObservableList<Bill> loadBillsFromFile(String filePath) {
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                int billId = Integer.parseInt(fields[0].trim());
                String billNumber = fields[1].trim();
                Date billDate = dateFormat.parse(fields[2].trim());
                double totalAmount = Double.parseDouble(fields[3].trim());
                int totalQuantity = Integer.parseInt(fields[4].trim());
                bills.add(new Bill(billId, billNumber, billDate, totalQuantity, totalAmount));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return bills;
    }

    private ObservableList<Bill> filterBillsByDate(Date targetDate) {
        ObservableList<Bill> filteredBills = FXCollections.observableArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Bill bill : allBills) {
            if (dateFormat.format(bill.getBillDate()).equals(dateFormat.format(targetDate))) {
                filteredBills.add(bill);
            }
        }
        return filteredBills;
    }

    public String getSectorForUser(User user) {
        String filePath = "src/main/resources/files/user.csv";
        String sectorName = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip the header row
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


    public static void main(String[] args) {
        launch(args);
    }
}
