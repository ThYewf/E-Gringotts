

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;

import javax.mail.MessagingException;

public class MarauderMapController {
    private final String SECUREPINPAGE = "TransactionSecurePinInterface.fxml";
    private final int SECUREPINPAGEHEIGHT = 350;
    private final int SECUREPINPAGEWIDTH = 500;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "wia1002";

    @FXML
    private TextField AmountTextField;

    @FXML
    private TextField CategoryTextField;

    @FXML
    private Button ComfirmButton;

    @FXML
    private TextField WhatForTextField;

    @FXML
    private TextField reciepientTextField;

    @FXML
    private Label transferBalanceLabel;

    @FXML
    private Label userDoesNotExistLabel;

    @FXML
    private Label categoryErrorLabel;

    @FXML
    private Label whatForErrorLabel;

    @FXML
    private Label transactionSuccessfullyLabel;

    @FXML
    private Label amountLabel;

    @FXML
    private Label currentUserLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label recipientLabel;

    @FXML
    private Label transactionIDLabel;
    
    @FXML
    void ComfirmButtonOnAction(MouseEvent event) {
        User<UserTier> receipient = getReceipient();//user that will receive the transfer
        User<UserTier> currentUser = LoggedInUser();//user that is logged in

        if(receipient == null) return;

        double amount = Double.parseDouble(AmountTextField.getText());
        String category = CategoryTextField.getText();
        String whatFor = WhatForTextField.getText();

        if(amount > currentUser.getBalance()) {//the amount is more than the balance of the logged in user
            transferBalanceLabel.setText("Insufficient balance");
            return;
        }

        if(amount <= 0) {
            transferBalanceLabel.setText("Invalid amount");
            return;
        }

        if(category.equals("")) {
            categoryErrorLabel.setText("Category cannot be empty");
            return;
        }

        if(whatFor.equals("")) {
            whatForErrorLabel.setText("What for cannot be empty");
            return;
        }

        //prompt user to enter secure pin before transfer
        //if the secure pin is correct then transfer the amount to the receipient
        //if the secure pin enter wrong 3 time then the application will close
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SECUREPINPAGE));
            Parent root = loader.load();

            TransactionSecurePinController sPinController = loader.getController();
            sPinController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Enter Secure Pin");
            stage.setScene(new Scene(root, SECUREPINPAGEWIDTH, SECUREPINPAGEHEIGHT));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void proceedWithTransaction() {
        String userEmail = getUserEmail();
        User<UserTier> receipient = getReceipient();
        User<UserTier> currentUser = LoggedInUser();
        double amount = Double.parseDouble(AmountTextField.getText());
        String category = CategoryTextField.getText();

        if (isAnyActiveUser()) {
                // Send the login notification email
        if (userEmail != null) {
            emailsender emailSender = new emailsender();
            try {
                emailSender.sendEmail(userEmail, "Transaction Notification", "You have successfully make a transactions.");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
            transactionSuccessfullyLabel.setText("Transaction successfully!");
            Transaction newTransaction = addTransactionToDatabase(amount, category);
            showTransactionReceipt(currentUser, receipient, amount, newTransaction);
        }
    }

    
    public String getUserEmail() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String email = "";
    
        try {
            String query = "SELECT address FROM Users WHERE status = 'active'";
            ResultSet result = connectDb.createStatement().executeQuery(query);
    
            if(result.next()) {
                email = result.getString("address");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    
        return email;
    }

    private void showTransactionReceipt(User<UserTier> currentUser, User<UserTier> recipient, double amount, Transaction newTransaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt.fxml"));
            Parent root = loader.load();

            MarauderMapController controller = loader.getController();
            controller.setTransactionDetails(newTransaction, currentUser, recipient);

            Stage stage = new Stage();
            stage.setTitle("Transaction Receipt");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTransactionDetails(Transaction transaction, User<UserTier> currentUser, User<UserTier> recipient) {
        transactionIDLabel.setText(transaction.getTransactionID());
        dateLabel.setText(transaction.getDate().toString());
        amountLabel.setText(String.format("%.2f", transaction.getAmount()));
        currentUserLabel.setText(currentUser.getFirst_name() + " " + currentUser.getLast_name());
        recipientLabel.setText(recipient.getFirst_name() + " " + recipient.getLast_name());
    }

    public Transaction addTransactionToDatabase(double amount, String category) {
        User<UserTier> recipient = getReceipient();
        User<UserTier> currentUser = LoggedInUser();

        // Assuming you have these details available
        double newCurrentUserBalance = currentUser.getBalance() - amount;
        double newRecipientBalance = recipient.getBalance() + amount;

        // Update the balance for both users in their respective tables (assuming userInfo)
        updateBalanceInUserDatabase(currentUser.getUserID(), newCurrentUserBalance);
        updateBalanceInUserDatabase(recipient.getUserID(), newRecipientBalance);

        // Create the transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionID(newTransaction.generateTransactionID());
        newTransaction.setUserID(currentUser.getUserID());
        newTransaction.setRecipientID(recipient.getUserID());
        newTransaction.setAmount(amount);
        newTransaction.setBalance(newCurrentUserBalance);
        newTransaction.setDate(newTransaction.generateCurrentDateTime());
        newTransaction.setCategory(category);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO transactions3 (transactionID, userID, recipientID, amount, balance, date, category) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, newTransaction.getTransactionID());
                preparedStatement.setString(2, newTransaction.getUserID());
                preparedStatement.setString(3, newTransaction.getRecipientID());
                preparedStatement.setDouble(4, newTransaction.getAmount());
                preparedStatement.setDouble(5, newTransaction.getBalance());
                preparedStatement.setTimestamp(6, newTransaction.getDate());
                preparedStatement.setString(7, newTransaction.getCategory());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newTransaction;
    }

    public void updateBalanceInUserDatabase(String userID, double newBalance){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET balance = ? WHERE userID = ?")) {
                preparedStatement.setDouble(1, newBalance);
                preparedStatement.setString(2, userID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    //from the name or phone number enter by user search for the user in the database
    private User<UserTier> getReceipient() {
        ArrayList<User<UserTier>> userList = userList();//array list of user 
        User<UserTier> receipient;//user that will receive the transfer

        if(isNumeric(reciepientTextField.getText()) == true){ //search by phone number
            if(searchByPhone(userList, reciepientTextField.getText()) == -1) {
                userDoesNotExistLabel.setText("User does not exist");
                return null;
            }else {
                receipient = userList.get(searchByPhone(userList, reciepientTextField.getText()));
            }
            
        } else { //search by name
            if(searchByName(userList, reciepientTextField.getText()) == -1) {
                userDoesNotExistLabel.setText("User does not exist");
                return null;
            }else {
                receipient = userList.get(searchByName(userList, reciepientTextField.getText()));
            }
        }

        return receipient;
    }

    private int searchByPhone(ArrayList<User<UserTier>> userList, String receipient) {
        for(int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getPhone().equals(receipient)) return i;//return the index of the user
        }

        return -1;//user not found
    }

    private int searchByName(ArrayList<User<UserTier>> userList, String receipient) {
        for(int i = 0; i < userList.size(); i++) {
            String fullname = userList.get(i).getFirst_name() + " " + userList.get(i).getLast_name();
            if(fullname.equals(receipient)) return i;//return the index of the user
        }

        return -1;//user not found
    }

    //check the receipient enter by user is numeric or string
    //if numeric then search for the user by phone number
    //if string then search for the user by name
    private boolean isNumeric(String receipient) {
        try {
            Integer.parseInt(receipient);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //get an array list of user from database 
    private ArrayList<User<UserTier>> userList() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        ArrayList<User<UserTier>> userList = new ArrayList<User<UserTier>>();
        try {
            String query = "SELECT * FROM users";//select all user from database
            ResultSet result = connectDb.createStatement().executeQuery(query);

            while(result.next()) {
                String userID = result.getString("userID");
                String first_name = result.getString("first_name");
                String last_name = result.getString("last_name");
                String phone = result.getString("phone");
                double balance = result.getDouble("balance");
                userList.add(new User<UserTier>(userID, first_name, last_name, phone, balance));
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }//end of try-catch

        return userList;
    }

        //get the logged in user
        public User<UserTier> LoggedInUser(){
            DatabaseConnection2 connectNow = new DatabaseConnection2();
            Connection connectDb = connectNow.getConnection();
            String getActiveUser = "SELECT * FROM users WHERE status = 'Active'";//get the user with active status in database
            User<UserTier> currentUser;
        
            try{
                Statement statement = connectDb.createStatement();
                ResultSet result = statement.executeQuery(getActiveUser);
        
                while(result.next()){
                    String userID = result.getString("userID");
                    String first_name = result.getString("first_name");
                    String last_name = result.getString("last_name");
                    String phone = result.getString("phone");
                    double balance = result.getDouble("balance");
                    currentUser = new User<UserTier>(userID, first_name, last_name, phone, balance);
                    return currentUser;
                }

            } catch(Exception e){
                e.printStackTrace();
                e.getCause();
            }
            
            return null;
        }

        private boolean isAnyActiveUser() {
            DatabaseConnection2 connectNow = new DatabaseConnection2();
            Connection connectDb = connectNow.getConnection();
            String getActiveUser = "SELECT * FROM users WHERE status = 'Active'";//get the user with active status in database

            try{
                Statement statement = connectDb.createStatement();
                ResultSet result = statement.executeQuery(getActiveUser);
        
                while(result.next()){//if there is any active user in the database
                    return true;
                }
        
            } catch(Exception e){
                e.printStackTrace();
                e.getCause();
            }
            return false;
       }
    }