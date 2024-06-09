import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PieChartController implements Initializable{
    private final String ADMINPAGE = "AdminInterface.fxml";
    private final int ADMINPAGEHEIGHT = 300;
    private final int ADMINPAGEWIDTH = 500;
    int platinum = 0;
    int gold = 0;
    int silver = 0;

    @FXML
    private PieChart pieChart;
    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUser();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Platinum Patronus", platinum),
                new PieChart.Data("Golden Galleon", gold),
                new PieChart.Data("Silver Snitch", silver)
        );

        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " amount: ", data.pieValueProperty()
                        )
                )
        );

        pieChart.getData().addAll(pieChartData);
    }

    private void getUser() {
        DatabaseConnection2 db = new DatabaseConnection2();
        Connection conectDb = db.getConnection();

        try {
            String query = "SELECT tier FROM Users";
            ResultSet result = conectDb.createStatement().executeQuery(query);

            while(result.next()) {
                String tier = result.getString("tier");
                if(tier.equals("PLATINUM")) {
                    platinum++;
                } else if(tier.equals("GOLDEN")) {
                    gold++;
                } else if(tier.equals("SILVER")) {
                    silver++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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