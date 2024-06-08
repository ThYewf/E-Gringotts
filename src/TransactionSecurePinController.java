import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.application.Platform;

public class TransactionSecurePinController {

    @FXML
    private PasswordField securePinField;
    @FXML
    private Label incorrectPasswordLabel;
    @FXML
    private Button confirmButton;

    private int attemp = 0;
    private String userID = getActiveUser();

    public void confirmButtonOnAction(){
        int pin = Integer.parseInt(securePinField.getText());//the pin enter by user
        SecurePin securePin = new SecurePin();

        if(securePin.validatePin(pin,this.userID) == true){//if the password enter is correct then close the window of secure pin
            
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
        }else{//if the password enter is incorrect user have 2 more attemp to enter the correct password, if all attemp is wrong then close the application
            incorrectPassword();
        }
    }

    //if the password enter is incorrect user have 2 more attemp to enter the correct password, if all attemp is wrong then close the application
    public void incorrectPassword(){
        attemp++;
        if(attemp == 1){
            incorrectPasswordLabel.setText("Incorrect Password, 2 more attemp");
        }else if(attemp == 2){
            incorrectPasswordLabel.setText("Incorrect Password, 1 more attemp");
        }else{        
            inactivateUser();
            Platform.exit(); //close the application, terminate all stage and exit the application
        }
    }

    //search for active user to get the userID to know which user is logging in
    public String getActiveUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String userID = "";

        String active = "SELECT userID, status FROM Users WHERE status = 'active'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(active);
    
            while(rs.next()) {
                userID = rs.getString("userID");
            } 

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return userID;
    }

    //inactivate user, when user logout the status will change ACTIVE to INACTIVE
    private void inactivateUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String inactivateUserQuery = "UPDATE Users SET status = 'INACTIVE' WHERE status = 'ACTIVE'";

        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(inactivateUserQuery);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
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