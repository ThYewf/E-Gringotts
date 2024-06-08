import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;
import java.sql.Connection;
import java.sql.Statement;


public class divinationController {

    @FXML
    private ChoiceBox<String> categories;

    @FXML
    private Button categorized;

    @FXML
    private DatePicker datepicker;

    @FXML
    private Button filterButton;

    @FXML
    private ComboBox<String> monthly;

    @FXML
    private ChoiceBox<String> payment;

    @FXML
    private PieChart pieChart;

    @FXML
    private Button backButton;

    @FXML
    private Label totalExpense;

    @FXML
    private Button clearButton;

    private divinationModel model = new divinationModel();

    String activeUserId = model.activeUserId; // replace with your active user id

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> switchScene("UserInterface.fxml"));
        
        String url = "jdbc:mysql://localhost:3306/user"; 
        String username = "root"; 
        String password = "wia1002"; 
      
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
        // Populate the categories ChoiceBox
        categories.setItems(FXCollections.observableArrayList(getDistinctValues(connection, "category")));

        // Populate the monthly ComboBox
        monthly.setItems(FXCollections.observableArrayList(getDistinctValues(connection, "MONTH(date)")));

        // Populate the payment ChoiceBox
        payment.setItems(FXCollections.observableArrayList(getDistinctValues(connection, "payment_method")));
    } catch (Exception e) {
        e.printStackTrace();
    }

        showPieChart();
        categorized.setOnAction(event -> showPieChart());
        filterButton.setOnAction(event -> updatePieChart());
        clearButton.setOnAction(event -> clearSelections());
    }
    
    private void clearSelections() {
        datepicker.setValue(null);
        categories.setValue(null);
        monthly.setValue(null);
        payment.setValue(null);
    }

    private List<String> getDistinctValues(Connection connection, String columnName) throws Exception {
        List<String> values = new ArrayList<>();
    
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DISTINCT " + columnName + " FROM transactions3 WHERE userID = '" + activeUserId + "'")) {
            while (resultSet.next()) {
                values.add(resultSet.getString(1));
            }
        }
    
        return values;
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

    private void showPieChart() {
       
        Map<String, Double> categoryExpenditureMap = model.getCategoryExpenditure(activeUserId);
    
        // Calculate the total sum
        double totalSum = categoryExpenditureMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
    totalExpense.setText(String.format("Total Expenses: %.2f", totalSum));
        // Create a PieChart and populate it with the data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryExpenditureMap.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            data.nameProperty().bind(
                    Bindings.concat(
                            data.getName(), " ", 
                            Bindings.format(
                                    "%.1f%%",
                                    Bindings.divide(
                                            data.pieValueProperty(),
                                            totalSum
                                    ).multiply(100)
                            )
                    )
            );
            pieChartData.add(data);
        }
    
        pieChart.setData(pieChartData);
    }

    private void updatePieChart() {
    LocalDate date = datepicker.getValue();
    String category = (String) categories.getValue();
    String monthly = (String) this.monthly.getValue();
    String paymentMethod = (String) payment.getValue();

    Map<String, Double> filteredExpenditure = model.getFilteredExpenditure(activeUserId, date, category, monthly, paymentMethod);
    updatePieChartData(filteredExpenditure);

    
    // Calculate the total expenses and set the value of the Label
    double totalExpensesValue = filteredExpenditure.values().stream().mapToDouble(Double::doubleValue).sum();
    totalExpense.setText(String.format("Total Expenses: %.2f", totalExpensesValue));
}

private void updatePieChartData(Map<String, Double> filteredExpenditure) {
    // Calculate the total sum
    double totalSum = filteredExpenditure.values().stream().mapToDouble(Double::doubleValue).sum();

    // Create a PieChart and populate it with the data
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    for (Map.Entry<String, Double> entry : filteredExpenditure.entrySet()) {
        PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
        data.nameProperty().bind(
                Bindings.concat(
                        data.getName(), " ",
                        Bindings.format(
                                "%.1f%%",
                                Bindings.divide(
                                        data.pieValueProperty(),
                                        totalSum
                                ).multiply(100)
                        )
                )
        );
        pieChartData.add(data);
    }

    pieChart.setData(pieChartData);
}
}