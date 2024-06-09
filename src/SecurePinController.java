import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.mail.MessagingException;

import javafx.application.Platform;

public class SecurePinController {
    private final String userPage = "UserInterface.fxml";
    private final String adminPage = "AdminInterface.fxml";
    private final int userPageHeight = 500;
    private final int userPageWidth = 320;
    private final int adminPageHeight = 500;
    private final int adminPageWidth = 300; 

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
            checkTier(userID); //check and update user tier before redirect to user page
            userPage();//run user page after user login


             String userEmail = getUserEmail();

        // Send the login notification email
        if (userEmail != null) {
            emailsender emailSender = new emailsender();
            try {
                emailSender.sendEmail(userEmail, "Login Notification", "You have successfully logged in.");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
        }else{//if the password enter is incorrect user have 2 more attemp to enter the correct password, if all attemp is wrong then close the application
            incorrectPassword();
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

    //before redirect to user page, check user balance and tier
    //if user balance is more then 30000 and less than 70000 = gold tier
    //if user balance is more then 70000 = platinum tier
    private void checkTier(String userID) {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();

        String checkBalance = "SELECT balance, tier FROM Users WHERE tier != '" + userID + "'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(checkBalance);

            while(rs.next()) {
                if(rs.getString("tier").equals("GOBLIN")){
                    return;
                } else if(rs.getDouble("balance") > 30000 && rs.getDouble("balance") < 70000 && !rs.getString("tier").equals("GOLDEN")) { //user balance is between range of 30000 - 70000 and tier is not golden, update user tier
                    String updateTier = "UPDATE Users SET tier = 'GOLDEN' WHERE userID = '" + userID + "'";
                    statement.executeUpdate(updateTier);
                } else if(rs.getDouble("balance") > 70000 && !rs.getString("tier").equals("PLATINUM")) { //user balance is more than 70000 and tier is not platinum, update user tier
                    String updateTier = "UPDATE Users SET tier = 'PLATINUM' WHERE userID = '" + userID + "'";
                    statement.executeUpdate(updateTier);
                } else if(rs.getDouble("balance") < 30000 && !rs.getString("tier").equals("SILVER")) { //user balance is less than 30000 and tier is not silver, update user tier
                    String updateTier = "UPDATE Users SET tier = 'SILVER' WHERE userID = '" + userID + "'";
                    statement.executeUpdate(updateTier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
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

        //check the user tier and redirect to the appropriate interface
    public void userPage(){
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();

        String verifyLogin = "SELECT userID, tier, status FROM Users WHERE status = 'ACTIVE'";//get the activate user from the database

        try{
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(verifyLogin);

            while(rs.next()){
                if(rs.getString("userID") != null){//check if the active user exist in the database

                    //check the user tier and direct to the appropriate interface
                    //PLATINUM user
                    if(rs.getString("Tier").equals("PLATINUM")){//fxml file defined on top
                        Parent root = FXMLLoader.load(getClass().getResource(userPage));//go to new interface
                        Stage platinumStage = new Stage();
                        platinumStage.setTitle("Platinum Patronus");
                        platinumStage.setScene(new Scene(root, userPageHeight, userPageWidth));
                        platinumStage.setResizable(false);
                        platinumStage.show();

                    //GOLDEN user
                    }else if(rs.getString("Tier").equals("GOLDEN")){//fxml file defined on top
                        Parent root = FXMLLoader.load(getClass().getResource(userPage));//go to new interface
                        Stage goldenStage = new Stage();
                        goldenStage.setTitle("Golden Galleon");
                        goldenStage.setScene(new Scene(root, userPageHeight, userPageWidth));
                        goldenStage.setResizable(false);
                        goldenStage.show();

                    //SILVER user
                    }else if(rs.getString("Tier").equals("SILVER")){//fxml file defined on top
                        Parent root = FXMLLoader.load(getClass().getResource(userPage));//go to new interface
                        Stage silverStage = new Stage();
                        silverStage.setTitle("Silver Snitch");
                        silverStage.setScene(new Scene(root, userPageHeight, userPageWidth));
                        silverStage.setResizable(false);
                        silverStage.show();

                    //admin page
                    }else{
                        Parent root = FXMLLoader.load(getClass().getResource(adminPage));//fxml file defined on top
                        Stage goblinStage = new Stage();
                        goblinStage.setTitle("GOBLIN");
                        goblinStage.setScene(new Scene(root, adminPageHeight, adminPageWidth));
                        goblinStage.setResizable(false);
                        goblinStage.show();
                    }//end of if-else
                }//end of if
            }//end of while
            
        } catch(Exception e){
            
            e.getCause();
        }//end of try-catch
    }//end of userPage
}