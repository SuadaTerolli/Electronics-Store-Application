package com.electronicstore.view;

import com.electronicstore.model.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdministratorStatistics extends Application {
    private User loggedInUser;

    public AdministratorStatistics(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void start(Stage primaryStage) {
        // Load data
        ArrayList<User> users = loadUsers("src/main/resources/files/user.csv");
        List<Category> categories = loadCategories("src/main/resources/files/categories.csv");
        List<Supplier> suppliers = loadSuppliers("src/main/resources/files/suppliers.dat");
        ArrayList<Item> items = new ArrayList<>(loadItems("src/main/resources/files/items.csv", categories, suppliers));
        ArrayList<Bill> bills = loadBills("src/main/resources/files/bills.csv", items);

        VBox leftSide = new VBox(20);
        leftSide.setStyle("-fx-padding: 20; -fx-background-color: rgb(255, 255, 255);");

        ImageView profileImage = new ImageView(new Image(getClass().getResource("/user.png").toExternalForm()));
        profileImage.setFitHeight(100);
        profileImage.setFitWidth(100);

        Label profileName = new Label(loggedInUser.getName());
        profileName.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 20;");
        Label role = new Label("Role: " + loggedInUser.getAccess_level());
        role.setStyle("-fx-font-weight: bold; -fx-text-fill: rgb(5, 39, 75);-fx-font-size: 18;");

        leftSide.getChildren().addAll(profileImage, profileName, role);

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
        logoutButton.setStyle("-fx-background-color:  white; -fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold ");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage, spacer, logoutButton);
        topBar.setAlignment(Pos.CENTER);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        GridPane center = new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");

        Button backBtn = new Button("< Go Back");
        backBtn.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        backBtn.setOnAction(e -> {
            AdministratorView statisticsView = new AdministratorView(loggedInUser);
            try {
                statisticsView.start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label statisticsTxt = new Label("Statistics");
        statisticsTxt.setStyle("-fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size:35 ");

        ComboBox<String> periodDropdown = new ComboBox<>();
        periodDropdown.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        periodDropdown.setPromptText("Select Period");
        periodDropdown.setStyle("-fx-border-color: rgb(5, 39, 75); -fx-font-weight: bold;");

        TableView<FinancialSummary> summariesTable = new TableView<>();
        summariesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<FinancialSummary, Double> incomeColumn = new TableColumn<>("Total Income");
        incomeColumn.setCellValueFactory(new PropertyValueFactory<>("totalIncome"));
        TableColumn<FinancialSummary, Double> costColumn = new TableColumn<>("Total Costs");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("totalCosts"));
        summariesTable.getColumns().addAll(incomeColumn, costColumn);

        TableView<FinancialSummary> detailsTable = new TableView<>();
        detailsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<FinancialSummary, Double> salaryColumn = new TableColumn<>("Salaries");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salaries"));
        TableColumn<FinancialSummary, Double> purchaseCostColumn = new TableColumn<>("Items Purchased Costs");
        purchaseCostColumn.setCellValueFactory(new PropertyValueFactory<>("itemsPurchasedPrice"));
        detailsTable.getColumns().addAll(salaryColumn, purchaseCostColumn);

        periodDropdown.setOnAction(e -> {
            String period = periodDropdown.getValue();
            if (period != null) {
                FinancialSummary summary = calculateStatisticsForPeriod(period, users, items, bills);
                summariesTable.setItems(FXCollections.observableArrayList(summary));
                detailsTable.setItems(FXCollections.observableArrayList(summary));
            }
        });

        center.add(backBtn, 0, 0);
        center.add(statisticsTxt, 0, 1);
        center.add(periodDropdown, 0, 2);
        center.add(summariesTable, 0, 3);
        center.add(detailsTable, 0, 4);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("Administrator Statistics Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private ArrayList<User> loadUsers(String filePath) {
        ArrayList<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                users.add(new User(
                        Integer.parseInt(fields[0].trim()),
                        fields[1].trim(),
                        fields[2].trim(),
                        new SimpleDateFormat("yyyy-MM-dd").parse(fields[3].trim()),
                        Long.parseLong(fields[4].trim()),
                        fields[5].trim(),
                        Double.parseDouble(fields[6].trim()),
                        fields[7].trim(),
                        fields[8].trim()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private List<Category> loadCategories(String filePath) {
        List<Category> categories = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header
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

    private List<Supplier> loadSuppliers(String binaryFilePath) {
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
    private List<Item> loadItems(String filePath, List<Category> categories, List<Supplier> suppliers) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                try {
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

                    if (category == null || supplier == null) {
                        System.err.println("Category or Supplier not found for item: " + name);
                        continue;
                    }

                    Date purchaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(fields[4].trim());
                    double purchasePrice = Double.parseDouble(fields[5].trim());
                    double sellingPrice = Double.parseDouble(fields[6].trim());
                    int quantity = Integer.parseInt(fields[7].trim());
                    int sectorId = Integer.parseInt(fields[8].trim());

                    items.add(new Item(itemId, name, category, supplier, purchaseDate, purchasePrice, sellingPrice, quantity, sectorId));
                } catch (Exception ex) {
                    System.err.println("Error processing line: " + line);
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private ArrayList<Bill> loadBills(String filePath, ArrayList<Item> items) {
        ArrayList<Bill> bills = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                bills.add(new Bill(
                        Integer.parseInt(fields[0].trim()),
                        fields[1].trim(),
                        new SimpleDateFormat("yyyy-MM-dd").parse(fields[2].trim()),
                        new ArrayList<>(),
                        Double.parseDouble(fields[3].trim()),
                        Integer.parseInt(fields[4].trim())
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bills;
    }
    private FinancialSummary calculateStatisticsForPeriod(String period, ArrayList<User> users, ArrayList<Item> items, ArrayList<Bill> bills) {
        Date startDate = new Date();
        Date endDate = new Date();

        switch (period) {
            case "Daily":
                startDate = getStartOfDay(new Date());
                endDate = getEndOfDay(new Date());
                break;
            case "Weekly":
                startDate = getStartOfWeek(new Date());
                endDate = getEndOfDay(new Date());
                break;
            case "Monthly":
                startDate = getStartOfMonth(new Date());
                endDate = getEndOfDay(new Date());
                break;
            case "Yearly":
                startDate = getStartOfYear(new Date());
                endDate = getEndOfDay(new Date());
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);

        double totalIncome = 0.0;
        for (Bill bill : bills) {
            if (!bill.getBillDate().before(startDate) && !bill.getBillDate().after(endDate)) {
                totalIncome += bill.getTotalAmount();
            }
        }

        double totalSalaries = 0.0;
        for (User user : users) {
            totalSalaries += user.getSalary();
        }

        double totalItemPurchaseCost = 0.0;
        for (Bill bill : bills) {
            if (!bill.getBillDate().before(startDate) && !bill.getBillDate().after(endDate)) {
                for (BillItem billItem : bill.getBillItems()) {
                    totalItemPurchaseCost += billItem.getItem().getPurchasePrice() * billItem.getItemQuantity();
                }
            }
        }

        double totalCosts = totalSalaries + totalItemPurchaseCost;

        return new FinancialSummary(startDate, endDate, totalIncome, totalCosts, totalSalaries, totalItemPurchaseCost);
    }
    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private Date getStartOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return getStartOfDay(cal.getTime());
    }

    private Date getStartOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(cal.getTime());
    }

    private Date getStartOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return getStartOfDay(cal.getTime());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
