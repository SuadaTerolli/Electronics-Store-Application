# Electronics Store Application

A JavaFX desktop application for managing the daily operations of an electronics store. The system supports role-based access for administrators, managers, and cashiers, with tools for inventory management, billing, employee management, supplier management, sales tracking, and reporting.

## Features

- Role-based login for Administrator, Manager, and Cashier users
- Inventory management for electronics items and categories
- Supplier and sector management
- Customer bill creation and sales tracking
- Cashier performance tracking
- Employee management for administrators
- CSV and binary-file based data storage
- PDF report generation for billing data

## Technologies

- Java 17
- JavaFX
- Maven
- Apache PDFBox
- CSV and binary files for local persistence

## Project Structure

```text
src/main/java/com/electronicstore
  controller/   Application controllers
  model/        Domain models
  util/         Utility classes
  view/         JavaFX screens and UI logic

src/main/resources
  files/        CSV and binary data files
  reports/      Report output resources
  *.png         UI image assets
```

## Requirements

- JDK 17 or newer
- Maven
- JavaFX dependencies, resolved through Maven

## Run the Application

From the project root, run:

```bash
mvn clean javafx:run
```

The configured main class is:

```text
com.electronicstore.view.LogIn
```

## Data Storage

The application stores its data locally in resource files, including:

- `src/main/resources/files/user.csv`
- `src/main/resources/files/items.csv`
- `src/main/resources/files/categories.csv`
- `src/main/resources/files/bills.csv`
- `src/main/resources/files/suppliers.csv`
- `src/main/resources/files/suppliers.dat`
- `src/main/resources/files/sectors.csv`

## Architecture

The project follows an MVC-style structure:

- Models represent store data such as users, items, bills, suppliers, and financial summaries.
- Views define the JavaFX screens for each user role.
- Controllers coordinate login and application behavior.

## Future Improvements

- Database-backed persistence
- More advanced sales analytics
- Online payment support
- Export options for additional report formats
