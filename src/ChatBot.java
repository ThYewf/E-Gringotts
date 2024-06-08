import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;

import java.io.IOException;

public class ChatBot extends Application {

    private final OkHttpClient client = new OkHttpClient();
    private String conversationHistory = "";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ChatBot");

        TextField userTextField = new TextField();
        TextArea chatArea = new TextArea();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> {
            String userText = userTextField.getText();
            userTextField.clear();

            chatArea.appendText("User: " + userText + "\n");

            try {
                String response = chat(userText);
                chatArea.appendText("AI: " + response + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        VBox vbox = new VBox(userTextField, chatArea, sendButton);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public String chat(String message) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"message\":\"" + message + "\", \"history\":\"" + conversationHistory + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
            .url("http://localhost:5000/chat")
            .post(body)
            .build();
    
        Response response = client.newCall(request).execute();
        if (response.body() != null) {
            try (ResponseBody responseBody = response.body()) {
                String responseText = responseBody.string();
                conversationHistory += "User: " + message + "\nAI: " + responseText + "\n";
                return responseText;
            }
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}