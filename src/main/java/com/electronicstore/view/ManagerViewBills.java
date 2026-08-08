package com.electronicstore.view;
import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class ManagerViewBills extends Application {
    private User loggedInUser;

    public ManagerViewBills(User loggedInUser)
    {
        this.loggedInUser=loggedInUser;
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

        VBox leftSide = new VBox(20);
        leftSide.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");
        leftSide.setPrefWidth(200);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");

        HBox topBar = new HBox(15);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: rgb(245, 245, 245);");
        topBar.setPrefHeight(80);

        HashMap<String, Integer> sectorMapping = loadSectorsFromFile("src/main/resources/files/sectors.csv");

        ComboBox<String> sectorComboBox = new ComboBox<>();
        sectorComboBox.getItems().addAll(sectorMapping.keySet());
        sectorComboBox.setValue("Choose Sector");
        sectorComboBox.setStyle("-fx-background-color: white;-fx-border-color:rgb(5, 39, 75); -fx-text-fill: rgb(92, 143, 198); -fx-font-weight: bold; -fx-font-size: 16px;-fx-background-radius: 15;-fx-border-radius: 15;");
        sectorComboBox.setPrefWidth(300);
        sectorComboBox.setPrefWidth(200);



        ComboBox<String> periodComboBox = new ComboBox<>();
        periodComboBox.getItems().addAll(
                "Daily","Monthly","Yearly"
        );
        periodComboBox.setValue("Choose Period");
        periodComboBox.setStyle("-fx-background-color: white;-fx-border-color:rgb(5, 39, 75); -fx-text-fill: rgb(92, 143, 198); -fx-font-weight: bold; -fx-font-size: 16px;-fx-background-radius: 15;-fx-border-radius: 15;");
        periodComboBox.setPrefWidth(300);
        periodComboBox.setPrefWidth(200);

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
        logoutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size: 14px;");
        logoutButton.setOnAction(event -> {
            try {
                new LogIn().start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);
        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        leftSide.getChildren().addAll(profileImage, profileName, role );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().addAll(topLeftImage, spacer, sectorComboBox, periodComboBox, logoutButton);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        Label listOfBills = new Label("Bills");
        listOfBills.setStyle("-fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size: 35;");

        TableView<Bill> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefSize(600, 400);

        TableColumn<Bill, String> billNumberColumn = new TableColumn<>("Bill Number");
        billNumberColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));

        TableColumn<Bill, String> itemsColumn = new TableColumn<>("Items");
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));

        TableColumn<Bill, Double> soldPriceColumn = new TableColumn<>("Total Amount");
        soldPriceColumn.setCellValueFactory(new PropertyValueFactory<>("soldPrice"));

        table.getColumns().addAll(billNumberColumn, itemsColumn, soldPriceColumn);

        ObservableList<Bill> allBills = loadBillsFromFile("src/main/resources/files/bills.csv");
        FilteredList<Bill> filteredBills = new FilteredList<>(allBills, p -> true);
        sectorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredBills.setPredicate(bill -> {
                boolean matchesSector = (newVal == null || newVal.isEmpty()) ||
                        bill.getSectorId() == sectorMapping.getOrDefault(newVal, -1);
                boolean matchesPeriod = matchesPeriod(bill, periodComboBox.getValue());
                return matchesSector && matchesPeriod;
            });
        });
        table.setItems(filteredBills);
        Button exportButton = new Button("Export to PDF");
        exportButton.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        exportButton.setOnAction(e -> {
            exportBillsToPDF(filteredBills);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bills exported to PDF successfully!");
            alert.showAndWait();
        });

        topBar.getChildren().add(exportButton);
        sectorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredBills.setPredicate(bill -> {
                boolean matchesSector = (newVal == null || newVal.isEmpty()) || bill.getItems().contains(newVal);
                return matchesSector && matchesPeriod(bill, periodComboBox.getValue());
            });
        });

        periodComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            filteredBills.setPredicate(bill -> {
                boolean matchesSector = (sectorComboBox.getValue() == null || sectorComboBox.getValue().isEmpty()) ||
                        bill.getSectorId() == sectorMapping.getOrDefault(sectorComboBox.getValue(), -1);
                boolean matchesPeriod = matchesPeriod(bill, newVal);
                return matchesSector && matchesPeriod;
            });
        });

        VBox center = new VBox(10,goBack, listOfBills, table);
        center.setStyle("-fx-padding: 20;");

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Dashboard/Manager/Bills");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private boolean matchesPeriod(Bill bill, String period) {
        if (period == null || period.isEmpty() || period.equals("Choose Period")) return true;
        LocalDate billDate = bill.getDate();
        LocalDate now = LocalDate.now();
        switch (period) {
            case "Daily":
                return billDate.isEqual(now);
            case "Monthly":
                return billDate.getMonth() == now.getMonth() && billDate.getYear() == now.getYear();
            case "Yearly":
                return billDate.getYear() == now.getYear();
            default:
                return true;
        }
    }
    public static ObservableList<Bill> loadBillsFromFile(String filePath) {
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String billNumber = fields[1];
                LocalDate billDate = LocalDate.parse(fields[2], formatter);
                double totalAmount = Double.parseDouble(fields[3]);
                int sectorId = Integer.parseInt(fields[4]);
                String items = fields[5];
                bills.add(new Bill(billNumber, billDate, totalAmount, items, sectorId));
            }
        } catch (IOException e) {
            System.out.println("Problem loading bills");
            e.printStackTrace();
        }
        return bills;
    }


    public static class Bill {
        private final SimpleStringProperty billNumber;
        private final SimpleDoubleProperty soldPrice;
        private final SimpleStringProperty items;
        private final LocalDate date;
        private final Integer sectorId;

        public Bill(String billNumber, LocalDate date, double soldPrice, String items, int sectorId) {
            this.billNumber = new SimpleStringProperty(billNumber);
            this.soldPrice = new SimpleDoubleProperty(soldPrice);
            this.items = new SimpleStringProperty(items);
            this.date = date;
            this.sectorId = sectorId;
        }

        public String getBillNumber() {
            return billNumber.get();
        }

        public double getSoldPrice() {
            return soldPrice.get();
        }

        public String getItems() {
            return items.get();
        }

        public LocalDate getDate() {
            return date;
        }

        public int getSectorId() {
            return sectorId;
        }
    }

    public void exportBillsToPDF(ObservableList<Bill> bills) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Bills Table:");
            contentStream.newLine();

            contentStream.showText("Bill Number       | Total Amount     | Items");
            contentStream.newLine();

            for (Bill bill : bills) {
                String line = String.format("%-18s | %-17.2f | %s",
                        bill.getBillNumber(),
                        bill.getSoldPrice(),
                        bill.getItems());
                contentStream.showText(line);
                contentStream.newLine();
            }

            contentStream.endText();
        } catch (IOException e) {
            System.err.println("Error writing to PDF: " + e.getMessage());
        }

        try {
            document.save(new File("src/main/resources/reports/bills_table.pdf"));
            document.close();
            System.out.println("PDF saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving PDF: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}