package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

@SuppressWarnings("unused")
public class C_slider  {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox slide1;

    @FXML
    private VBox slide2;

    @FXML
    private VBox slide3;

    @FXML
    private Rectangle firstButton;

    @FXML
    private Rectangle secondButton;

    @FXML
    private Rectangle thirdButton;

    @FXML
    private Button start;

    private int normal_width = 10;
    private int stretched_width = 20;
    private Rectangle[] buttons;
    private VBox[] slides;

    @FXML
    void initialize() {
        buttons = new Rectangle[]{firstButton, secondButton, thirdButton};
        slides = new VBox[]{slide1, slide2, slide3};
        assert slide1 != null : "fx:id=\"slide1\" was not injected: check your FXML file 'Slider.fxml'.";
        assert slide2 != null : "fx:id=\"slide2\" was not injected: check your FXML file 'Slider.fxml'.";
        assert slide3 != null : "fx:id=\"slide3\" was not injected: check your FXML file 'Slider.fxml'.";
        assert firstButton != null : "fx:id=\"firstButton\" was not injected: check your FXML file 'Slider.fxml'.";
        assert secondButton != null : "fx:id=\"secondButton\" was not injected: check your FXML file 'Slider.fxml'.";
        assert thirdButton != null : "fx:id=\"thirdButton\" was not injected: check your FXML file 'Slider.fxml'.";

        for (int i = 0; i < 3; i++) {
            final int k = i;
            buttons[i].setOnMouseClicked(mouseEvent -> changeSlide(k));
        }

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    sceneManager.showScene(sceneManager.getScene("main"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void changeSlide(int order){
        for (int i = 0; i < 3; i++) {
            if( i != order){
                if(buttons[i].getWidth()==25){
                    Timeline scaledown = new Timeline();
                    scaledown.getKeyFrames().add(new KeyFrame(Duration.millis(200),new KeyValue(buttons[i].widthProperty(), 15, Interpolator.EASE_OUT)));
                    scaledown.setCycleCount(1);
                    scaledown.playFromStart();

                    Timeline fadeDown = new Timeline();
                    fadeDown.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(buttons[i].fillProperty(), Color.web("#b5b5b5"), Interpolator.EASE_OUT)));
                    fadeDown.setCycleCount(1);
                    fadeDown.playFromStart();

                    Timeline fadeOut = new Timeline();
                    fadeOut.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(slides[i].opacityProperty(), 0, Interpolator.EASE_OUT)));
                    fadeOut.setCycleCount(1);
                    fadeOut.playFromStart();

                }

                if (i<order && slides[i].getTranslateX()!= -100){

                    Timeline slidetoView= new Timeline();
                    slidetoView.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(slides[i].translateXProperty(), -100, Interpolator.EASE_OUT)));
                    slidetoView.setCycleCount(1);
                    slidetoView.playFromStart();

                }

                if (i>order && slides[i].getTranslateX()!= 100){

                    Timeline slidetoView= new Timeline();
                    slidetoView.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(slides[i].translateXProperty(), 100, Interpolator.EASE_OUT)));
                    slidetoView.setCycleCount(1);
                    slidetoView.playFromStart();

                }
            } else {
                if(buttons[i].getWidth()==15){

                    Timeline scaledown = new Timeline();
                    scaledown.getKeyFrames().add(new KeyFrame(Duration.millis(200),new KeyValue(buttons[i].widthProperty(), 25, Interpolator.EASE_OUT)));
                    scaledown.setCycleCount(1);
                    scaledown.playFromStart();

                    Timeline fadeDown = new Timeline();
                    fadeDown.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(buttons[i].fillProperty(), Color.web("#707070"), Interpolator.EASE_OUT)));
                    fadeDown.setCycleCount(1);
                    fadeDown.playFromStart();

                    Timeline fadeIn = new Timeline();
                    fadeIn.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(slides[i].opacityProperty(), 1, Interpolator.EASE_OUT)));
                    fadeIn.setCycleCount(1);
                    fadeIn.playFromStart();

                }

                if(slides[i].getTranslateX() != 0){
                    Timeline slidetoView= new Timeline();
                    slidetoView.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(slides[i].translateXProperty(), 0, Interpolator.EASE_OUT)));
                    slidetoView.setCycleCount(1);
                    slidetoView.playFromStart();
                }

            }
        }
    }
}
