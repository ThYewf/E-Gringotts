
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TransactionSecurePinController {

    @FXML
    private PasswordField securePinField;
    @FXML
    private Label incorrectPasswordLabel;
    @FXML
    private Button confirmButton;

    private int attempt = 0;
    private String userID = getActiveUser();
    private MarauderMapController mainController;

    public void setMainController(MarauderMapController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void confirmButtonOnAction() {
        int pin = Integer.parseInt(securePinField.getText()); // the pin entered by user
        SecurePin securePin = new SecurePin();

        if (securePin.validatePin(pin, this.userID)) { // if the password entered is correct then close the window of secure pin
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
            mainController.proceedWithTransaction(); // Call the method to proceed with the transaction
        } else { // if the password entered is incorrect user has 2 more attempts to enter the correct password, if all attempts are wrong then close the application
            incorrectPassword();
        }
    }

    public void incorrectPassword() {
        attempt++;
        if (attempt == 1) {
            incorrectPasswordLabel.setText("Incorrect Password, 2 more attempts");
        } else if (attempt == 2) {
            incorrectPasswordLabel.setText("Incorrect Password, 1 more attempt");
        } else {
            inactivateUser();
            Platform.exit(); // close the application, terminate all stages and exit the application
        }
    }

    public String getActiveUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String userID = "";

        String active = "SELECT userID, status FROM users WHERE status = 'ACTIVE'";

        try {
            Statement statement = connectDb.createStatement();
            ResultSet rs = statement.executeQuery(active);

            while (rs.next()) {
                userID = rs.getString("userID");
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return userID;
    }

    private void inactivateUser() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection connectDb = connectNow.getConnection();
        String inactivateUserQuery = "UPDATE users SET status = 'INACTIVE' WHERE status = 'ACTIVE'";

        try {
            Statement statement = connectDb.createStatement();
            statement.executeUpdate(inactivateUserQuery);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
