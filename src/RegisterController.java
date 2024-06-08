import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

public class RegisterController {

    @FXML
    private Button backToLoginButton;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField; 
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private TextField userIDTextField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField phonenumberTextField;
    @FXML
    private TextField securePinTextField;
    
    //register user to database
    public void registerButtonOnAction(ActionEvent event) throws NoSuchAlgorithmException{
        //check if password and confirm password match
        if(passwordField.getText().equals(confirmPasswordField.getText())){
            confirmPasswordLabel.setText("Password match");
            registerUser();
        }else{
            confirmPasswordLabel.setText("Password does not match");
        }
    }

    //back to login page
    public void loginButtonOnAction(ActionEvent event){
        try{
            //load logininterface fxml
            Parent root = FXMLLoader.load(getClass().getResource("LoginInterface.fxml"));
            //create a new stage
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.setResizable(false);
            loginStage.show();

            //get the current stage and close it
            Stage currentStage = (Stage) backToLoginButton.getScene().getWindow();
            currentStage.close();

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }//end of try-catch
    }//end of signupButtonOnAction

    public void registerUser() throws NoSuchAlgorithmException{
        DatabaseConnection2 connectNow = new DatabaseConnection2();//create a new database connection
        Connection connectDb = connectNow.getConnection();//connect to the database
        User<UserTier> user = new User<>();//create a new user object for account generation 
        SecurePin securePin = new SecurePin();//create a new secure pin object for pin encryption
        PasswordHashing passwordHash = new PasswordHashing();//create a new password hashing object for password encryption
        byte[] saltByte = passwordHash.createSalt();//generate a new salt for password encryption each time register new user

        String userID = userIDTextField.getText();
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        String address = addressTextField.getText();
        String phone = phonenumberTextField.getText();
        String pin = securePin.encryptPin(securePinTextField.getText());//encrypt the pin enter by user
        String account = user.generateAccountNumber();//auto generate account number
        String password = passwordHash.generateHash(passwordField.getText(), saltByte);//encrypt the password enter by user
        

        String salt = passwordHash.bytestoStringBase64(saltByte);//the salt for password encryption
        String tier = "SILVER";//by default new register user are silver tier
        String date = LocalDate.now().toString();//get the current date
        String status = Status.INACTIVE.toString();

        String insertFields = "INSERT INTO Users(userID, first_name, last_name, address, phone, salt, password, pin, account, tier, date, status) VALUES('";
        String insertValues = userID + "','" + firstname + "','" + lastname + "','" + address + "','" + phone +"','"+ salt + "','" + password + "','" + pin + "','" + account + "','" + tier +"','" + date + "','" + status +"')";
        String insertToRegister = insertFields + insertValues;

        try{
            Statement statement = connectDb.createStatement();
        
            statement.executeUpdate(insertToRegister);
            registrationMessageLabel.setText("User has been registered successfully!");
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }
}