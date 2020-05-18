package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class C_onboarding {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button welcome;

    @FXML
    void initialize() {
        assert welcome != null : "fx:id=\"welcome\" was not injected: check your FXML file 'On_Boarding.fxml'.";

        welcome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    sceneManager.showScene(sceneManager.getScene("slider"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
