

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExchangeRateController {

    @FXML
    private TextField amount;

    @FXML
    private Button confirm1;

    @FXML
    private Button confirm2;

    @FXML
    private MenuButton desMenu;

    @FXML
    private TextField destinationInput;

    @FXML
    private TextField erInput;

    @FXML
    private TextField feeRates;

    @FXML
    private TextField sourceInput;

    @FXML
    private MenuButton srcMenu;

    @FXML
    private Label result;

    @FXML
    private Button backButton;

    private ExchangeRateModel model;

    @FXML
    private void initialize() {
        model = new ExchangeRateModel();
        populateMenuButtons();
        backButton.setOnAction(event -> switchScene("UserInterface.fxml"));
    }

    @FXML
     private void switchScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void confirm(ActionEvent event) {
        String source = sourceInput.getText();
        String destination = destinationInput.getText();
        double rate = Double.parseDouble(erInput.getText());
        double fee = Double.parseDouble(feeRates.getText());

        model.addCurrency(source, destination, rate, fee);

        // Clear the input fields
        sourceInput.setText("");
        destinationInput.setText("");
        erInput.setText("");
        feeRates.setText("");

        // Refresh the MenuButtons
        srcMenu.getItems().clear();
        desMenu.getItems().clear();
        populateMenuButtons();
    }

    private void populateMenuButtons() {
        List<String> currencies = model.getUniqueCurrencies();

        for (String currency : currencies) {
            MenuItem srcMenuItem = new MenuItem(currency);
            srcMenuItem.setOnAction(e -> srcMenu.setText(currency));
            srcMenu.getItems().add(srcMenuItem);

            MenuItem desMenuItem = new MenuItem(currency);
            desMenuItem.setOnAction(e -> desMenu.setText(currency));
            desMenu.getItems().add(desMenuItem);
        }
    }

    @FXML
    private void calculate(ActionEvent event) {
        String source = srcMenu.getText();
        String destination = desMenu.getText();
        double amountValue = Double.parseDouble(amount.getText());
    
        double exchangeRate = model.calculateExchangeRate(source, destination, amountValue);
        double totalFee = model.calculateTotalFee(source, destination, amountValue);
    
        if (exchangeRate == 0) {
            return;
        }
        
        String userID = model.getActiveUserID();
    
        model.exchangeCurrency(userID, source, destination, amountValue);
    
        // Assuming you have a method to get the user's balance
        double balance = model.getUserBalance(userID);
    
        // Add the receipt title and emoji to the text
        result.setText(String.format("⭐ E-GRINGOTTS RECEIPT ⭐\n\nAmount Received: %.2f\nTotal Fee: %.2f\nBalance: %.2f", exchangeRate, totalFee, balance));
    }
}



