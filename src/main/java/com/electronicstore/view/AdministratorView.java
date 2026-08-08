package com.electronicstore.view;

import com.electronicstore.model.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdministratorView extends Application {
    private User loggedInUser;
    private HashMap<String, Integer> sectorMapping;

    public AdministratorView(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
    @Override
    public void start(Stage primaryStage) {
        String csvFilePath = "src/main/resources/files/user.csv";

        sectorMapping = loadSectorsFromFile("src/main/resources/files/sectors.csv");

        ObservableList<User> userData = loadUsersFromFile(csvFilePath);

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

        Button statisticsButton = new Button("Statistics");
        statisticsButton.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        statisticsButton.setOnAction(event -> {
            AdministratorStatistics statisticsView = new AdministratorStatistics(loggedInUser);
            try {
                statisticsView.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView topLeftImage = new ImageView(new Image(getClass().getResource("/logo.png").toExternalForm()));
        topLeftImage.setFitHeight(80);
        topLeftImage.setFitWidth(80);

        topBar.getChildren().addAll(topLeftImage, spacer, statisticsButton,logoutButton);
        topBar.setAlignment(Pos.CENTER);

        Region blueLine = new Region();
        blueLine.setStyle("-fx-background-color:rgb(5, 39, 75) ; -fx-min-height: 5px; -fx-max-height: 5px;");

        VBox topLayout = new VBox();
        topLayout.getChildren().addAll(topBar, blueLine);

        GridPane center=new GridPane();
        center.setVgap(30);
        center.setHgap(50);
        center.setStyle("-fx-padding: 20;");

        Label listOfUsers = new Label("List of Users");
        listOfUsers.setStyle("-fx-text-fill: rgb(5, 39, 75); -fx-font-weight: bold; -fx-font-size:35 ");

        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefSize(600, 400);
        table.setItems(userData);

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, Date> dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("date_of_birth"));

        TableColumn<User, Long> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("access_level"));

        TableColumn<User, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actionButtons = new HBox(10);

            {
                editButton.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditDialog(user);
                    saveUsersToFile(table.getItems());
                    getTableView().refresh();
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(user);
                    saveUsersToFile(table.getItems());
                });

                actionButtons.getChildren().addAll(editButton, deleteButton);
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });

        table.getColumns().addAll(nameColumn, dobColumn, phoneColumn, emailColumn, salaryColumn,roleColumn, actionsColumn);

        Button addUserButton = new Button("Add User");
        addUserButton.setStyle("-fx-background-color: rgb(5, 39, 75); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 20px; -fx-padding: 5 15;");
        addUserButton.setOnAction(event -> {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Add New User");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField idField = new TextField();
            TextField nameField = new TextField();
            TextField usernameField = new TextField();
            DatePicker dobPicker = new DatePicker();
            TextField phoneField = new TextField();
            TextField emailField = new TextField();
            TextField salaryField = new TextField();
            TextField passwordField = new TextField();

            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.setItems(FXCollections.observableArrayList("Cashier", "Manager"));
            roleComboBox.setPromptText("Select Role");

            ComboBox<String> sectorComboBox = new ComboBox<>();
            sectorComboBox.getItems().addAll(sectorMapping.keySet());
            sectorComboBox.setValue("Choose Sector");
            

            grid.add(new Label("ID:"), 0, 0);
            grid.add(idField, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("Username:"), 0, 2);
            grid.add(usernameField, 1, 2);
            grid.add(new Label("Date of Birth:"), 0, 3);
            grid.add(dobPicker, 1, 3);
            grid.add(new Label("Phone:"), 0, 4);
            grid.add(phoneField, 1, 4);
            grid.add(new Label("Email:"), 0, 5);
            grid.add(emailField, 1, 5);
            grid.add(new Label("Salary:"), 0, 6);
            grid.add(salaryField, 1, 6);
            grid.add(new Label("Password:"), 0, 7);
            grid.add(passwordField, 1, 7);
            grid.add(new Label("Role:"), 0, 8);
            grid.add(roleComboBox, 1, 8);
            grid.add(new Label("Sector: "),0,9);
            grid.add(sectorComboBox,1,9);

            dialog.getDialogPane().setContent(grid);

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        int id = Integer.parseInt(idField.getText());
                        String name = nameField.getText();
                        String username = usernameField.getText();
                        LocalDate dob = dobPicker.getValue();
                        long phone = Long.parseLong(phoneField.getText());
                        String email = emailField.getText();
                        double salary = Double.parseDouble(salaryField.getText());
                        String password = passwordField.getText();
                        String roleAdd = roleComboBox.getValue();
                        String sectorName = sectorComboBox.getValue();

                        if (name.isEmpty() || username.isEmpty() || dob == null || email.isEmpty() || password.isEmpty() || roleAdd == null || sectorName == null) {
                            throw new IllegalArgumentException("All fields must be filled out!");
                        }

                        User newUser = new User(id, name, username, java.sql.Date.valueOf(dob), phone, email, salary, password, roleAdd);
                        newUser.setSectorName(sectorName);
                        userData.add(newUser);
                        saveUsersToFile(userData);
                        return newUser;
                    } catch (Exception e) {
                        showErrorDialog("Invalid Input", "Please ensure all fields are filled correctly.");
                    }
                }
                return null;
            });


            dialog.showAndWait();
        });



        center.add(listOfUsers, 0, 0);
        center.add(addUserButton,1,0);
        center.add(table, 0, 1);

        GridPane.setHgrow(table, Priority.ALWAYS);
        GridPane.setVgrow(table, Priority.ALWAYS);

        BorderPane pane = new BorderPane();
        pane.setLeft(leftSide);
        pane.setTop(topLayout);
        pane.setCenter(center);

        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setTitle("Administrator Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        alert.showAndWait();
    }

    private ObservableList<User> loadUsersFromFile(String filePath) {
        ObservableList<User> users = FXCollections.observableArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String username = parts[2].trim();
                    Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(parts[3].trim());
                    long phoneNumber = Long.parseLong(parts[4].trim());
                    String email = parts[5].trim();
                    double salary = Double.parseDouble(parts[6].trim());
                    String password = parts[7].trim();
                    String accessLevel = parts[8].trim();
                    String sectorName=parts[9].trim();

                    User user = new User(id, name, username, dateOfBirth, phoneNumber, email, salary, password, accessLevel,sectorName);
                    users.add(user);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return users;
    }
    private void saveUsersToFile(ObservableList<User> users) {
        String filePath = "src/main/resources/files/user.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("id,name,username,datebirth,phone,email,salary,password,accesslevel,sectorName");
            writer.newLine();

            // Write user data
            for (User user : users) {
                writer.write(user.getId() + "," +
                        user.getName() + "," +
                        user.getUsername() + "," +
                        new SimpleDateFormat("yyyy-MM-dd").format(user.getDate_of_birth()) + "," +
                        user.getPhoneNumber() + "," +
                        user.getEmail() + "," +
                        user.getSalary() + "," +
                        user.getPassword() + "," +
                        user.getAccess_level() + "," +
                        user.getSectorName());
                writer.newLine();
            }
            System.out.println("File saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void showEditDialog(User user) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit User");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        HashMap<String, Integer> sectorMapping = loadSectorsFromFile("src/main/resources/files/sectors.csv");

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        TextField phoneField = new TextField(String.valueOf(user.getPhoneNumber()));
        TextField salaryField = new TextField(String.valueOf(user.getSalary()));
        TextField roleField = new TextField(user.getAccess_level());

        ComboBox<String> sectorComboBox = new ComboBox<>();
        sectorComboBox.getItems().addAll(sectorMapping.keySet());
        sectorComboBox.setValue(user.getSectorName());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Salary:"), 0, 3);
        grid.add(salaryField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleField, 1, 4);
        grid.add(new Label("Sector:"), 0, 5);
        grid.add(sectorComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setPhoneNumber(Long.parseLong(phoneField.getText()));
                user.setSalary(Double.parseDouble(salaryField.getText()));
                user.setAccess_level(roleField.getText());
                user.setSectorName(sectorComboBox.getValue());
            }
            return null;
        });

        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
