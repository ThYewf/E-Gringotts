import javax.mail.MessagingException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button("Send Email");
        btn.setOnAction(event -> {
            emailsender emailSender = new emailsender();
            try {
                emailSender.sendEmail("***REMOVED***", "Test Subject", "Test Message");
                System.out.println("Email sent successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Failed to send email.");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("JavaFX Email Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
