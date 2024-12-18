import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class FinancialApp extends Application {

    private TableView<Transaction> table;
    private ObservableList<Transaction> transactionList;
    private Label balanceLabel, incomeLabel, expenseLabel;
    private double balance = 0.0, totalIncome = 0.0, totalExpenses = 0.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the data list
        transactionList = FXCollections.observableArrayList();

        // Root Layout with Menu Bar
        BorderPane root = new BorderPane();
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Tab Pane for Organizing UI
        TabPane tabPane = new TabPane();
        Tab transactionTab = new Tab("Transactions", createTransactionTab());
        Tab summaryTab = new Tab("Summary", createSummaryTab());
        transactionTab.setClosable(false);
        summaryTab.setClosable(false);
        tabPane.getTabs().addAll(transactionTab, summaryTab);

        root.setCenter(tabPane);

        // Scene and Stage Setup
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700&display=swap");
        primaryStage.setTitle("Enhanced Financial Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "About", "Enhanced Financial Tracker v1.0"));
        helpMenu.getItems().add(aboutItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private VBox createTransactionTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #f4f4f4; -fx-font-family: 'Roboto';");

        // Input Fields
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
        typeComboBox.setPromptText("Type");

        Button addButton = new Button("Add Transaction");
        addButton.setOnAction(e -> addTransaction(descriptionField, amountField, typeComboBox));

        HBox inputFields = new HBox(10, descriptionField, amountField, typeComboBox, addButton);
        inputFields.setPadding(new Insets(10));
        inputFields.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 5; -fx-padding: 10;");

        // Table
        table = new TableView<>();
        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descriptionCol.setMinWidth(300);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> data.getValue().typeProperty());

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty().asObject());

        table.getColumns().addAll(descriptionCol, typeCol, amountCol);
        table.setItems(transactionList);

        layout.getChildren().addAll(inputFields, table);
        return layout;
    }

    private VBox createSummaryTab() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #e8f5e9; -fx-font-family: 'Roboto';");

        incomeLabel = new Label("Total Income: ₱0.00");
        incomeLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: green;");

        expenseLabel = new Label("Total Expenses: ₱0.00");
        expenseLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: red;");

        balanceLabel = new Label("Balance: ₱0.00");
        balanceLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: black;");

        layout.getChildren().addAll(incomeLabel, expenseLabel, balanceLabel);
        return layout;
    }

    private void addTransaction(TextField descriptionField, TextField amountField, ComboBox<String> typeComboBox) {
        String description = descriptionField.getText();
        String type = typeComboBox.getValue();
        String amountText = amountField.getText();

        if (description.isEmpty() || type == null || amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (type.equals("Expense")) {
                amount = -amount;
                totalExpenses += Math.abs(amount);
            } else {
                totalIncome += amount;
            }

            // Add to table and update balance
            transactionList.add(new Transaction(description, type, Math.abs(amount)));
            balance += amount;
            updateSummary();

            // Clear fields
            descriptionField.clear();
            amountField.clear();
            typeComboBox.setValue(null);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Amount must be a number!");
        }
    }

    private void updateSummary() {
        balanceLabel.setText(String.format("Balance: ₱%.2f", balance));
        incomeLabel.setText(String.format("Total Income: ₱%.2f", totalIncome));
        expenseLabel.setText(String.format("Total Expenses: ₱%.2f", totalExpenses));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Transaction {
        private final SimpleStringProperty description;
        private final SimpleStringProperty type;
        private final SimpleDoubleProperty amount;

        public Transaction(String description, String type, double amount) {
            this.description = new SimpleStringProperty(description);
            this.type = new SimpleStringProperty(type);
            this.amount = new SimpleDoubleProperty(amount);
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public String getType() {
            return type.get();
        }

        public SimpleStringProperty typeProperty() {
            return type;
        }

        public double getAmount() {
            return amount.get();
        }

        public SimpleDoubleProperty amountProperty() {
            return amount;
        }
    }
}
