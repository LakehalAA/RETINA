package Controllers;

import Kernel.FaceDetector.FaceDetector;
import Kernel.Reconnaissance.RecognitionSystem;
import Kernel.Utils.DataBase;
import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import static Controllers.C_dashboard.setRetrain;

public class C_addperson {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<webcamInfo> cam;

    @FXML
    private StackPane paneCam;

    @FXML
    private ImageView view;

    @FXML
    private Button redo;

    @FXML
    private Label picOrder;

    @FXML
    private Label maxPics;

    @FXML
    private Button validerPic;

    @FXML
    private Button takePic;

    boolean stop;

    double aspectRatio=16;

    int ordreCam = 0;

    Webcam webcam = null;

    int picturingOrder = 0;
    DataBase db=RecognitionSystem.getDatabase();

    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

    ObservableList<webcamInfo> camsList = FXCollections.observableArrayList();

    private class webcamInfo implements converter {

        String camName;
        int order;
        double aR = 4/3;

        public String getCamName() {
            return camName;
        }

        public void setCamName(String camName) {
            this.camName = camName;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public double getaR() {
            return aR;
        }

        public void setaR(double aR) {
            this.aR = aR;
        }

        @Override
        public String toString(){
            return camName;
        }

    }

    @FXML
    void initialize(){
        picturingOrder = 0;
        init();
        cam.setItems(camsList);

        cam.valueProperty().addListener(new ChangeListener<webcamInfo>() {
            @Override
            public void changed(ObservableValue<? extends webcamInfo> observableValue, webcamInfo webcamInfo, webcamInfo t1) {
                webcam = Webcam.getWebcams().get(t1.getOrder());
                initializeCam(t1.getOrder());
            }
        });

        takePic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                shoot(actionEvent);
            }
        });

        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reStartCam();
            }
        });

        validerPic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    validatePic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void init(){
        for (Webcam webcam : Webcam.getWebcams()) {
            webcamInfo info = new webcamInfo();
            info.setCamName(webcam.getName());
            info.setOrder(ordreCam);
            camsList.add(info);
            ordreCam++;
        }
    };

    void reStartCam(){
        startCamStream(cam.getSelectionModel().getSelectedItem().order);

        takePic.setDisable(false);
        validerPic.setDisable(true);
        redo.setVisible(false);

    }

    void initializeCam(int ordre){
        takePic.setDisable(false);
        validerPic.setDisable(true);
        redo.setVisible(false);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                webcam = Webcam.getWebcams().get(ordre);
                webcam.open();
                camsList.get(ordre).setaR((double)webcam.getImage().getWidth()/webcam.getImage().getHeight());
                resizeViewport(camsList.get(ordre).getaR());
                startCamStream(ordre);
                return null;

            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        if(!webcam.getName().equals("DroidCam Source 3 0")){
            view.setRotate(0);
        }else{
            view.setRotate(90);
        }

    }

    private void startCamStream( int a ) {
        stop = false;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AtomicReference<Image> ref = new AtomicReference<>();
                BufferedImage img = null;

                while (!stop){
                    try{
                        if((img = webcam.getImage())!=null){

                            ref.set(SwingFXUtils.toFXImage(img,null));
                            img.flush();

                            Platform.runLater(() -> imageProperty.setValue(ref.get()));
                        }

                        Thread.sleep(20);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        view.imageProperty().bind(imageProperty);

    }

    private void resizeViewport(double aR){
        double height = paneCam.getHeight();
        double width = paneCam.getWidth();

        Rectangle clip = new Rectangle();

        clip.setHeight(height);
        clip.setWidth(height);
        clip.setX(45);

        view.preserveRatioProperty().setValue(false);
        view.setFitHeight(height);
        view.setFitWidth(height*aR);
        view.clipProperty().setValue(clip);
    }

    @FXML
    void shoot(ActionEvent actionEvent){
        stopCamStream();
    }

    void stopCamStream(){
        stop = true;
        takePic.setDisable(true);
        validerPic.setDisable(false);
        redo.setVisible(true);
    }

    void validatePic() throws IOException {

        if(picturingOrder < 6) {

            Rectangle clip = new Rectangle();
            BufferedImage image = null;
            BufferedImage inter = new BufferedImage((int) imageProperty.getValue().getWidth(), (int) imageProperty.getValue().getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D grapher = inter.createGraphics();
            grapher.drawImage(SwingFXUtils.fromFXImage(imageProperty.getValue(), null), 0, 0, null);
            grapher.dispose();
            clip.setHeight(480);
            clip.setWidth(480);
            File f;
            BufferedImage rotatedImage = null;
            if(webcam.getName().equals("DroidCam Source 3 0")){
                final double rads = Math.toRadians(90);
                final double sin = Math.abs(Math.sin(rads));
                final double cos = Math.abs(Math.cos(rads));
                final int w = (int) Math.floor(inter.getWidth() * cos + inter.getHeight() * sin);
                final int h = (int) Math.floor(inter.getHeight() * cos + inter.getWidth() * sin);
                rotatedImage = new BufferedImage(w, h, inter.getType());
                final AffineTransform at = new AffineTransform();
                at.translate(w / 2, h / 2);
                at.rotate(rads,0, 0);
                at.translate(-inter.getWidth() / 2, -inter.getHeight() / 2);
                final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                rotateOp.filter(inter,rotatedImage);
                f = converter.convertFormat(rotatedImage, RecognitionSystem.getDatabase().getNUMCOLS(), RecognitionSystem.getDatabase().getNUMROWS());
            }else{
                f = converter.convertFormat(inter, DataBase.getNUMROWS(), DataBase.getNUMCOLS());
            }

            FileUtils.touch(new File("outputcam.png"));
            System.out.print(converter.convertFormat(f.getPath(),"outputcam.png"));
            FaceDetector fd = new FaceDetector(new File("outputcam.png.jpg"));
            //  converter.convertFormat(inter, 112,92);
            if (fd.detectFace()) {

                if(webcam.getName().equals("DroidCam Source 3 0")) {
                    converter.convertAddFormat(rotatedImage, db.getNUMROWS(), db.getNUMCOLS(),"BufferPerson/" + String.valueOf(picturingOrder+1));
                }else{
                    converter.convertAddFormat(inter, db.getNUMROWS(), db.getNUMCOLS(), "BufferPerson/" + String.valueOf(picturingOrder+1));
                }
                picturingOrder++;
                picOrder.setText(0 + String.valueOf(picturingOrder + 1));
                reStartCam();

          } else {

              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setHeaderText(null);
              alert.setContentText("L'image ne s'agit pas d'un visage, ou contient un visage non clair !");
              alert.showAndWait();

          }
        }else {

            Rectangle clip = new Rectangle();
            BufferedImage inter = null;
            inter = SwingFXUtils.fromFXImage(imageProperty.getValue(), null);
            clip.setHeight(480);
            clip.setWidth(480);
            File f;
            BufferedImage rotatedImage = null;
            if(webcam.getName().equals("DroidCam Source 3 0")){
                final double rads = Math.toRadians(90);
                final double sin = Math.abs(Math.sin(rads));
                final double cos = Math.abs(Math.cos(rads));
                final int w = (int) Math.floor(inter.getWidth() * cos + inter.getHeight() * sin);
                final int h = (int) Math.floor(inter.getHeight() * cos + inter.getWidth() * sin);
                rotatedImage = new BufferedImage(w, h, inter.getType());
                final AffineTransform at = new AffineTransform();
                at.translate(w / 2, h / 2);
                at.rotate(rads,0, 0);
                at.translate(-inter.getWidth() / 2, -inter.getHeight() / 2);
                final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                rotateOp.filter(inter,rotatedImage);
                f = converter.convertFormat(rotatedImage, RecognitionSystem.getDatabase().getNUMCOLS(), RecognitionSystem.getDatabase().getNUMROWS());
            }else{
                f = converter.convertFormat(inter, DataBase.getNUMROWS(), DataBase.getNUMCOLS());
            }

            FileUtils.touch(new File("outputcam.png"));
            converter.convertFormat(f.getPath(),"outputcam.png");
            FaceDetector fd = new FaceDetector(new File("outputcam.png.jpg"));
            //  converter.convertFormat(inter, 112,92);
            try {
				
			
			
            if (fd.detectFace()) {

                if(webcam.getName().equals("DroidCam Source 3 0")) {
                   
                	converter.convertAddFormat(rotatedImage, db.getNUMROWS(), db.getNUMCOLS(),"BufferPerson/" + String.valueOf(picturingOrder+1));
                	

                }else{
                    converter.convertAddFormat(inter, db.getNUMROWS(), db.getNUMCOLS(), "BufferPerson/" + String.valueOf(picturingOrder+1));
                }
                
            	//converter.convertAddFormat(rotatedImage, db.getNUMROWS(), db.getNUMCOLS(),"BufferPerson/" + String.valueOf(picturingOrder+1));

                File[] files = new File(db.getPATH()).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });

                FileUtils.copyDirectory(new File("BufferPerson/"), new File(RecognitionSystem.getDatabase().getPATH()+"/s" + (files.length + 1)));
                setRetrain(true);
                sceneManager.showScene(sceneManager.getScene("admin"));

          } else {
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setHeaderText(null);
              alert.setContentText("L'image ne s'agit pas d'un visage, ou contient un visage non clair !");
              alert.showAndWait();
          }
            } catch (Exception e) {
				// TODO: handle exception
            	 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setHeaderText(null);
                 alert.setContentText("L'image ne s'agit pas d'un visage, ou contient un visage non clair !");
                 alert.showAndWait();
			}
            }

    };

    public void backToMain(ActionEvent a) throws IOException
    {
        try {
        FileUtils.deleteQuietly(new File("outputcam.png"));
        FileUtils.deleteQuietly(new File("testing.pgm"));
        DataBase database=null;
		try {
		      database=new DataBase(5,RecognitionSystem.getDatabase().getPATH());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		   int trainingPicsParPersonne=5;
		   int PicsToAjustThreshold_ParPersonne=1;
		   double threshold_Step=0.0005;
		   int numberOfTrainingPersons=database.getNUMBERMAXOFPRESONS();
		   int  dischoice=1;
		
		   RecognitionSystem.setMaxPersonsINDataBase(database.getNUMBERMAXOFPRESONS());

		   try {
			RecognitionSystem.trainModel(database, dischoice, trainingPicsParPersonne, PicsToAjustThreshold_ParPersonne, threshold_Step);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
    	}
        catch (Exception e){ }
        sceneManager.showScene(sceneManager.getScene("admin"));

    }

}
