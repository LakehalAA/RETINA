package Controllers;

import Kernel.FaceDetector.FaceDetector;
import Kernel.Reconnaissance.RecognitionSystem;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;



public class C_verify implements Initializable {


    protected static String pathToPredict;
    @FXML
    private ImageView infoResult;

    @FXML
    private javafx.scene.control.Button chooseImage;

    @FXML
    private Button retourner;

    @FXML
    private Label result;

    @FXML
    private ImageView choosenImg;

    @FXML
    private TextField identity;

    File pickedImage;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert infoResult != null : "fx:id=\"infoResult\" was not injected: check your FXML file 'Verify.fxml'.";
        assert chooseImage != null : "fx:id=\"chooseImage\" was not injected: check your FXML file 'Verify.fxml'.";
        assert retourner != null : "fx:id=\"retourner\" was not injected: check your FXML file 'Verify.fxml'.";
        assert result != null : "fx:id=\"result\" was not injected: check your FXML file 'Verify.fxml'.";
        assert choosenImg != null : "fx:id=\"choosenImg\" was not injected: check your FXML file 'Verify.fxml'.";
        assert identity != null : "fx:id=\"identity\" was not injected: check your FXML file 'Verify.fxml'.";

        Image new1 = new Image(new File("src/UI/assets/true_false.jpg").toURI().toString());
        infoResult.setImage(new1);
        infoResult.setVisible(true);
        infoResult.setCache(true);

        chooseImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    pickImage();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


    }

    //Handling buttons
    void pickImage() throws IOException {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PGM", "*.pgm"), new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Choisissez un visage à vérifier");
        pickedImage = fileChooser.showOpenDialog(stage);
        pathToPredict = pickedImage.getAbsolutePath();

        String ext = FilenameUtils.getExtension(pickedImage.getAbsolutePath());

        FaceDetector fd = new FaceDetector(pickedImage);
        try {
            if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
                if(fd.detectFace()){
                pickedImage = fd.getExtraction_pgm();
                }
            }
        else {
                converter.convertFormat(pickedImage.getAbsolutePath(),"test");
                FaceDetector fdd = new FaceDetector(new File("test.jpg"));
                if(fdd.detectFace()) {
                    C_predict.pathToPredict = pickedImage.getAbsolutePath();

                }
            }
            int p = RecognitionSystem.getProcess().predict(pickedImage.getAbsolutePath());

            //code to show the chosen image
            String path = converter.convertFormat(pickedImage.getAbsolutePath(), "test");
            Image new1 = new Image(new File("test.jpg").toURI().toString());
            choosenImg.setImage(new1);
            choosenImg.setVisible(true);
            choosenImg.setCache(true);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mauvaise Image");
            alert.setContentText("L'image ne contient pas de visage ou que le visage n'est pas assez clair !");
            alert.showAndWait();
        }
    }


    public void ret(ActionEvent a) throws IOException
    {
        Main.manager.showScene(Main.manager.getScene("main"));
    }


    public void verify(ActionEvent actionEvent) {
        if (pathToPredict != null) {
            if (!pathToPredict.isEmpty()) {

                int p = 0;
                try {
                    p = RecognitionSystem.getProcess().predict(pathToPredict);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
;
                String path = "";
                try {
                    path = converter.convertFormat(pathToPredict, "test");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                javafx.scene.image.Image new1 = new Image(new File(path).toURI().toString());
                choosenImg.setImage(new1);
                choosenImg.setVisible(true);
                choosenImg.setCache(true);

                if(identity.getText().equalsIgnoreCase("s"+p)){
                    result.setText("Vérifiée! identité et visage compatibles");
                    String paa="src/UI/assets/true.png";
                    File relativeFile = new File(paa);

                    Image newb = new Image (relativeFile.toURI().toString());

                    this.infoResult.setImage(newb);
                    this.infoResult.setVisible(true);
                    this.infoResult.setCache(true);
                }
                else{
                    result.setText("Attention! Le visage ne correspond pas à l'idéntité entrée  ");
                    String paa="src/UI/assets/false.png";
                    File relativeFile = new File(paa);

                    Image newb = new Image (relativeFile.toURI().toString());

                    this.infoResult.setImage(newb);
                    this.infoResult.setVisible(true);
                    this.infoResult.setCache(true);
                }
            }

        }
    }
}
