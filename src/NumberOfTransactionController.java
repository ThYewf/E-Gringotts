import java.sql.Connection;
import java.sql.ResultSet;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class NumberOfTransactionController implements Initializable{
    private final String ADMINPAGE = "AdminInterface.fxml";
    private final int ADMINPAGEHEIGHT = 300;
    private final int ADMINPAGEWIDTH = 500;

    @FXML
    private TableColumn<Transaction, Double> amountColum;//undone
    @FXML
    private Button backButton;//undone
    @FXML
    private Label numberOfTransacrtionLabel;//undone
    @FXML
    private TableColumn<Transaction, String> receipientIDColumn;//undone
    @FXML
    private TableColumn<Transaction, String> transactionIDColumn;//undone
    @FXML
    private TableView<Transaction> transactionTable;//undone
    @FXML
    private TableColumn<Transaction, String> typeColumn;//undone
    @FXML
    private TableColumn<Transaction, String> userIDColumn;//undone

    ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    //setup the table
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set up the columns in the table
        transactionIDColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("transactionID"));//undone
        userIDColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("userID"));//undone
        receipientIDColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("recipientID"));//undone
        typeColumn.setCellValueFactory(new PropertyValueFactory<Transaction, String>("category"));//undone
        amountColum.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("amount"));//undone

        //get the list of user register today
        ObservableList<Transaction> transactionList = getUserList();
        transactionTable.setItems(transactionList);

        //get the total number of user register today
        numberOfTransacrtionLabel.setText("Total number of transaction made today: " + transactionList.size());
    }

    //get the list of user register today
    public ObservableList<Transaction> getUserList() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection conectDb = connectNow.getConnection();

        try {
            String query = "SELECT * FROM transactions3 WHERE date = CURDATE()";//undone
            ResultSet result = conectDb.createStatement().executeQuery(query);

            while(result.next()) {
                Transaction transaction = new Transaction();
                transaction.setUserID(result.getString("userID"));//undone
                transaction.setTransactionID(result.getString("transactionID"));//undone
                transaction.setRecipientID(result.getString("recipientID"));//undone
                transaction.setCategory(result.getString("category"));//undone
                transaction.setAmount(Double.parseDouble(result.getString("amount")));//fixed
                transactionList.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }//end of try-catch

        return transactionList;
    }

    //back to admin page
    public void backButtonOnAction(ActionEvent event){
        try{
            //load admininterface fxml
            Parent root = FXMLLoader.load(getClass().getResource(ADMINPAGE));
            //create a new stage
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, ADMINPAGEWIDTH, ADMINPAGEHEIGHT));
            loginStage.setResizable(false);
            loginStage.show();

            //get the current stage and close it
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch
    }//end of backButtonOnAction

}