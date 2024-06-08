import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;


public class LoginController {
    private final String registerPage = "RegisterInterface.fxml";
    private final int registerPageHeight = 600;
    private final int registerPageWidth = 570;
    private final String securePinPage = "SecurePinInterface.fxml";
    private final int securePinPageHeight = 500;
    private final int securePinPageWidth = 350;

    @FXML
    private Button cancelButton;
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField enterPasswordField;


    public void loginButtonOnAction(ActionEvent event){
        if(usernameTextField.getText().isBlank() == false && enterPasswordField.getText().isBlank() == false){
            if(validateLogin() == true){//password enter by user is correct
                activateUser();//activate the user after login
                securePinPage();//run secure pin page after user login
                 
                 
            }else{//password enter by user is incorrect
                loginMessageLabel.setText("Invalid login. Please try again.");
            }//end of if-else
        }else{//either username or password is empty
            loginMessageLabel.setText("Please enter username and password");
        }//end of if-else
    }//end of loginButtonOnAction


    //quit the application
    public void cancelButtonOnAction(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit E-Gringotts");
        alert.setContentText("Are you sure you want to exit?");

        if(alert.showAndWait().get() == ButtonType.OK){
            inactivateUser();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
        
    }//end of cancelButtonOnAction


    //check if the user is in the database
    public boolean validateLogin(){
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        PasswordHashing passwordHashing = new PasswordHashing();//create a new passwordHashing object

        String password = enterPasswordField.getText();//password enter by user
        String verifyLogin = "SELECT salt, password FROM Users WHERE userID = ?";

        try(PreparedStatement preparedStatement = connectDb.prepareStatement(verifyLogin)) {
            preparedStatement.setString(1, usernameTextField.getText());
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()){
                String salt = rs.getString("salt");//get the salt from the database
                byte[] saltByte = passwordHashing.stringSaltToByte(salt);//convert string salt to byte[]
                String securePassword = rs.getString("password");//get the hashed password from the database
                String hashedPassword = passwordHashing.generateHash(password, saltByte);//generate hash from password entered by user and salt from database

                if(hashedPassword.equals(securePassword)){//compare the hashed password with the hashed password in the database
                    return true;
                }//end of if
            }
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch
            
        return false;
    }//end of validateLogin


    //go to register page
    public void signupButtonOnAction(ActionEvent event){
        try{
            Parent root = FXMLLoader.load(getClass().getResource(registerPage));//go to new interface
            Stage registerStage = new Stage();
            registerStage.setTitle("Register User");
            registerStage.setScene(new Scene(root, registerPageHeight, registerPageWidth));
            registerStage.setResizable(false);
            registerStage.show();

            //get the current stage and close it
            Stage currentStage = (Stage) signupButton.getScene().getWindow();
            currentStage.close();

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch
    }//end of signupButtonOnAction

    //activate the user after login, after user login the status will change from INACTIVE to ACTIVE
    private void activateUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String getUser = "SELECT * FROM Users WHERE userID = '" + usernameTextField.getText() + "'";

        try{
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(getUser);

            while(rs.next()){
                if(rs.getString("userID") != null){
                    String activateUser = "UPDATE Users SET status = 'ACTIVE' WHERE userID = '" + usernameTextField.getText() + "'";
                    statement.executeUpdate(activateUser);
                }//end of if
            }//end of while
            
        } catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch
    }

    //deactiavte the user after logout, after user logout the status will change from ACTIVE to INACTIVATE
    public void inactivateUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String getUser = "SELECT userID, status FROM Users WHERE status = 'ACTIVE'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(getUser);

            while (rs.next()) {
                if (rs.getString("status") != null) {
                    String userID = rs.getString("userID");
                    String inactivateUser = "UPDATE Users SET status = 'INACTIVE' WHERE userID = '" + userID + "'" ;
                    statement.executeUpdate(inactivateUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    //run secure pin page after user login
    public void securePinPage(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource(securePinPage));//go to new interface
            Stage securePinStage = new Stage();
            securePinStage.setTitle("Secure Pin");
            securePinStage.setScene(new Scene(root, securePinPageHeight, securePinPageWidth));
            securePinStage.setResizable(false);
            securePinStage.show();


            //get the current stage and close it
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch

    }//end of securePinPage
    
}//end of class