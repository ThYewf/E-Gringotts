import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StatisticDataController implements Initializable{
    private final String ADMINPAGE = "AdminInterface.fxml";
    private final int ADMINPAGEHEIGHT = 300;
    private final int ADMINPAGEWIDTH = 500;

    int silver = 0;
    int gold = 0;
    int platinum = 0;

    @FXML
    private Button backButton;

    @FXML
    private BubbleChart<String, Integer> barChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUser();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();

        //set te user displable name for this series
        series.setName("user");

        //add data to the series
        XYChart.Data<String, Integer> seriesSilver = new XYChart.Data<>("Silver", silver);
        series.getData().add(seriesSilver);
        series.getData().add(new XYChart.Data<>("Golden", gold));
        series.getData().add(new XYChart.Data<>("Platinum", platinum));

        //add XYChart series to the barchart object
        barChart.getData().add(series);
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

   private void getUser() {
    DatabaseConnection2 connectNow = new DatabaseConnection2();
    Connection connectDb = connectNow.getConnection();
    String getTier = "SELECT tier FROM Users";

    try {
        ResultSet result = connectDb.createStatement().executeQuery(getTier);

        while (result.next()) {
            String userTier = result.getString("tier");
            
            if (userTier.equals("SILVER")) {
                silver++;
            } else if (userTier.equals("GOLDEN")) {
                gold++;
            } else if (userTier.equals("PLATINUM")) {
                platinum++;
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        e.getCause();
    } finally {
        try {
            connectDb.close(); // Close the connection when done
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

}
