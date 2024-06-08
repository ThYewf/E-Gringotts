import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class PensievePastController implements Initializable{
    //to create a list for transaction history of a user
    private ObservableList<Transaction> listTransaction=FXCollections.observableArrayList();
    //the crudentials to connect to database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "wia1002";

    //the table to display transaction history
    @FXML
    private TableView<Transaction> table; //tableFXID
    //the columns in the table
    @FXML
    private TableColumn<Transaction, String> transactionID;
    @FXML
    private TableColumn<Transaction, String> userID;
    @FXML
    private TableColumn<Transaction, String> recipientID;
    @FXML
    private TableColumn<Transaction, Double> amount;
    @FXML
    private TableColumn<Transaction, Double> balance;
    @FXML
    private TableColumn<Transaction, Timestamp> date;
    @FXML
    private TableColumn<Transaction, String> category;

    //create a choice box to select the filter options
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Button okBtn;
    @FXML
    private TextField textField;
    
    //display the transaction history based on the choice selected
    @FXML
    void filterBtn(MouseEvent event) {
        String choice=choiceBox.getSelectionModel().getSelectedItem();
        switch (choice) {
            case "Most Recent":{
                textField.setVisible(false);
                okBtn.setVisible(false);
                Collections.sort(listTransaction, Comparator.comparing(Transaction::getDate).reversed());
                table.setItems(listTransaction);
            }break;
            case "Highest Amount":{
                textField.setVisible(false);
                okBtn.setVisible(false);
                Collections.sort(listTransaction, Comparator.comparingDouble(Transaction::getAmount).reversed());
                table.setItems(listTransaction);
            }break;    
            case "Lowest Amount":{
                textField.setVisible(false);
                okBtn.setVisible(false);
                Collections.sort(listTransaction, Comparator.comparingDouble(Transaction::getAmount));
                table.setItems(listTransaction);
            }break;
            case "Category":{
                textField.setVisible(true);
                okBtn.setVisible(true);
                okBtnClick(event);
            }break; 
            case "Amount Threshold":{
                textField.setVisible(true);
                okBtn.setVisible(true);
                okBtnClick(event);
            }break; 
            default:
                break;
        }
    }

    @FXML
    void okBtnClick(MouseEvent event) {
        String choice = choiceBox.getSelectionModel().getSelectedItem();
        String text = textField.getText();
        if ("Category".equals(choice)) {
            ObservableList<Transaction> filteredList = listTransaction.stream()
                .filter(transaction -> transaction.getCategory().equalsIgnoreCase(text))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            table.setItems(filteredList);
        } else if ("Amount Threshold".equals(choice)) {
            try {
                double threshold = Double.parseDouble(text);
                ObservableList<Transaction> filteredList = listTransaction.stream()
                    .filter(transaction -> transaction.getAmount() >= threshold)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
                table.setItems(filteredList);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount threshold: " + text);
            }
        }
    }

    //initialise the choice box and table
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBox();
        initializeTableView();
        textField.setVisible(false);
        okBtn.setVisible(false);

        String userID = getLoggedInUserID();
        if (userID != null) {
            loadTransactionFromDatabase(userID);
        }
    }



    //initialize the table columns
    public void initializeTableView() {
        transactionID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        recipientID.setCellValueFactory(new PropertyValueFactory<>("recipientID"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        date.setCellValueFactory(new PropertyValueFactory<>( "date"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

     //initialize the choice box for filtering transaction history
     public void initializeChoiceBox() {
        choiceBox.setItems(FXCollections.observableArrayList("Most Recent", "Highest Amount", "Lowest Amount", "Category", "Amount Threshold"));
    }


    private String getLoggedInUserID() {
        String userID = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT userID FROM users WHERE status = 'Active'")) {
                if (resultSet.next()) {
                    userID = resultSet.getString("userID");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userID;
    }

    //to read the records from transaction history database
    private void loadTransactionFromDatabase(String userID) {
        listTransaction.clear(); // Clear previous transactions
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT * FROM transactions3 WHERE userID = '" + userID + "'")) {
                while (resultSet.next()) {
                    String transactionID = resultSet.getString("transactionID");
                    String recipientID = resultSet.getString("recipientID");
                    double amount = resultSet.getDouble("amount");
                    double balance = resultSet.getDouble("balance");
                    Timestamp date = resultSet.getTimestamp("date");
                    String category = resultSet.getString("category");
                    listTransaction.add(new Transaction(transactionID, userID, recipientID, amount, balance, date, category));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
