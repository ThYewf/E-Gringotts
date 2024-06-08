import java.sql.Connection;
import java.sql.ResultSet;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class NumOfUserController implements Initializable{
    private final String ADMINPAGE = "AdminInterface.fxml";
    private final int ADMINPAGEHEIGHT = 300;
    private final int ADMINPAGEWIDTH = 500;

    @FXML
    private TableColumn<User<UserTier>, String> dateColumn;
    @FXML
    private TableColumn<User<UserTier>, String> firstNameColumn;
    @FXML
    private TableColumn<User<UserTier>, String> lastNameColumn;
    @FXML
    private TableColumn<User<UserTier>, String> phoneNumColumn;
    @FXML
    private Label totalNumOfUserLabel;
    @FXML
    private TableColumn<User<UserTier>, String> userIDColumn;
    @FXML
    private TableView<User<UserTier>> userTable;
    @FXML
    private Button backButton;

    ObservableList<User<UserTier>> userList = FXCollections.observableArrayList();

    //setup the table
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set up the columns in the table
        userIDColumn.setCellValueFactory(new PropertyValueFactory<User<UserTier>, String>("userID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User<UserTier>, String>("first_name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User<UserTier>, String>("last_name"));
        phoneNumColumn.setCellValueFactory(new PropertyValueFactory<User<UserTier>, String>("phone"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<User<UserTier>, String>("date"));

        //get the list of user register today
        ObservableList<User<UserTier>> userList = getUserList();
        userTable.setItems(userList);

        //get the total number of user register today
        totalNumOfUserLabel.setText("Total number of user register today: " + userList.size());
    }

    //get the list of user register today
    public ObservableList<User<UserTier>> getUserList() {
        DatabaseConnection2 connectNow = new DatabaseConnection2();
        Connection conectDb = connectNow.getConnection();

        try {
            String query = "SELECT * FROM Users WHERE date = CURDATE()";
            ResultSet result = conectDb.createStatement().executeQuery(query);

            while(result.next()) {
                User<UserTier> user = new User<UserTier>();
                user.setUserID(result.getString("userID"));
                user.setFirst_name(result.getString("first_name"));
                user.setLast_name(result.getString("last_name"));
                user.setPhone(result.getString("phone"));
                user.setDate(result.getString("date"));
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }//end of try-catch

        return userList;
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