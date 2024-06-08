import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

public class UserInterfaceController {
    private final String LOGINPAGE = "LoginInterface.fxml";
    private final int LOGINPAGEHEIGHT = 400;
    private final int LOGINPAGEWIDTH = 600;
    private User<UserTier> currentUser = LoggedInUser();

    @FXML
    private Button DivinationDataButton;

    @FXML
    private Button GringottsExchangeButton;

    @FXML
    private Button MarauderMapButton;

    @FXML
    private Button PensievePastButton;

    @FXML
    private Label userLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Label balanceLabel;

    @FXML
    public void initialize() {
        userLabel.setText("Welcome, " + currentUser.getUserID());//set the user label to the logged in user
        balanceLabel.setText("Balance: " + displayBalance());//diplay the balance of the logged in user
        DivinationDataButton.setOnAction(event -> switchScene("divination.fxml"));
        GringottsExchangeButton.setOnAction(event -> switchScene("exchangerate.fxml"));
        MarauderMapButton.setOnAction(event -> switchScene("MarauderMapInterface.fxml"));
        PensievePastButton.setOnAction(event -> switchScene("PensievePastInterface.fxml"));
    }

    private void switchScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) DivinationDataButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the logged in user
    public User<UserTier> LoggedInUser(){
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String getActiveUser = "SELECT * FROM Users WHERE status = 'ACTIVE'";//get the user with active status in database
        User<UserTier> user = new User<UserTier>();
    
        try{
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(getActiveUser);
    
            while(rs.next()){
                user.setUserID(rs.getString("userID"));
                user.setFirst_name(rs.getString("first_name"));
                user.setLast_name(rs.getString("last_name"));
                user.setPassword(rs.getString("password"));
                user.setAddress(rs.getString("address"));
                user.setPhone(rs.getString("phone"));
                user.setPin(rs.getInt("pin"));
                user.setAccount(rs.getString("account"));
                user.setBalance(rs.getDouble("balance"));
                user.setTier(UserTier.valueOf(rs.getString("tier")));
                user.setDate(rs.getString("date"));
                user.setStatus(Status.valueOf(rs.getString("status")));
            }
    
        } catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return user;
    }

    //back to login page when logout button is clicked
    public void logoutButtonOnAction(ActionEvent evenet) { 
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout");
        alert.setContentText("Are you sure you want to logout?");

        if(alert.showAndWait().get() == ButtonType.OK){
            try {
                //redirect to login page
                inactivateUser(LoggedInUser());
                Parent root = FXMLLoader.load(getClass().getResource(LOGINPAGE));
                Stage loginStage = new Stage();
                loginStage.setTitle("");
                loginStage.setScene(new Scene(root, LOGINPAGEWIDTH, LOGINPAGEHEIGHT));
                loginStage.setResizable(false);
                loginStage.show();

                //close admin page
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
            
            } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            }//end of try-catch
        }
    }//end of logoutButtonOnAction

    //inactivate user, when user logout the status will change ACTIVE to INACTIVE
    private void inactivateUser(User<UserTier> user) {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String inactivateUserQuery = "UPDATE Users SET status = 'INACTIVE' WHERE userID = '" + user.getUserID() + "'";

        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(inactivateUserQuery);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
    
    private double displayBalance(){
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        double balance = 0;
        String getActiveUser = "SELECT status, balance FROM Users WHERE status = 'ACTIVE'";//get the user with active status in database

        try{
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(getActiveUser);
    
            while(rs.next()){
                balance = rs.getDouble("balance");
            }
    
        } catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

        return balance;
    }
}
