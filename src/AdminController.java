import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AdminController {
    private final String LOGINPAGE = "LoginInterface.fxml";
    private final int LOGINPAGEHEIGHT = 400;
    private final int LOGINPAGEWIDTH = 600;

    private final String NUMOFUSERPAGE = "NumberOfUserInterface.fxml";
    private final int NUMOFUSERAGEHEIGHT = 600;
    private final int NUMOFUSERPAGEWIDTH = 400;

    private final String NUMOFTRANSACTIONPAGE = "NumberOfTransaction.fxml";
    private final int NUMOFTRANSACTIONPAGEHEIGHT = 600;
    private final int NUMOFTRANSACTIONPAGEWIDTH = 400;

    private final String DATAPAGE = "StatisticDataInterface.fxml";
    private final int DATAPAGEHEIGHT = 600;
    private final int DATAPAGEWIDTH = 400;

    @FXML
    private Label HelloLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button numberOfUserButton;
    @FXML
    private Button numberOfTransactionButton;
    @FXML
    private Button StatisticalDataButton;

    //back to login page when logout button is clicked
    public void logoutButtonOnAction(ActionEvent evenet) { 
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout");
        alert.setContentText("Are you sure you want to logout?");

        if(alert.showAndWait().get() == ButtonType.OK){
            try {
                //redirect to login page
                inactivateUser(setLoggedInUser());
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

    //get the logged in user
    public User<UserTier> setLoggedInUser(){
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

    //number of user page pop up
    public void numberOfUserButtonOnAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(NUMOFUSERPAGE));
            Stage numberOfUserStage = new Stage();
            numberOfUserStage.setTitle("Number of User");
            numberOfUserStage.setScene(new Scene(root, NUMOFUSERAGEHEIGHT, NUMOFUSERPAGEWIDTH));
            numberOfUserStage.setResizable(false);
            numberOfUserStage.show();

            //get the current stage and close it
            Stage currentStage = (Stage) numberOfUserButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }//end of numberOfUserButtonOnAction

    public void numberOfTransactionButtonOnAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(NUMOFTRANSACTIONPAGE));
            Stage numberOfTransactionStage = new Stage();
            numberOfTransactionStage.setTitle("Number of Transaction");
            numberOfTransactionStage.setScene(new Scene(root, NUMOFTRANSACTIONPAGEHEIGHT, NUMOFTRANSACTIONPAGEWIDTH));
            numberOfTransactionStage.setResizable(false);
            numberOfTransactionStage.show();

            //get the current stage and close it
            Stage currentStage = (Stage) numberOfTransactionButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }//end of numberOfTransactionButtonOnAction

    public void StatisticalDataButtonOnAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(DATAPAGE));
            Stage StatisticalDataStage = new Stage();
            StatisticalDataStage.setTitle("Statistical Data");
            StatisticalDataStage.setScene(new Scene(root, DATAPAGEHEIGHT, DATAPAGEWIDTH));
            StatisticalDataStage.setResizable(false);
            StatisticalDataStage.show();

             //get the current stage and close it
            Stage currentStage = (Stage) StatisticalDataButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }//end of StatisticalDataButtonOnAction
}