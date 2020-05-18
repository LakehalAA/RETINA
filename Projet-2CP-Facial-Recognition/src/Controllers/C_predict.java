package Controllers;
import Kernel.FaceDetector.FaceDetector;
import Kernel.Reconnaissance.RecognitionSystem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.scene.control.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


   public class C_predict implements Initializable {


   	   @FXML
       Button retourner;

       @FXML
       private ResourceBundle resources;

       @FXML
       private URL location;

       @FXML
       private Label resultField;

       @FXML
       private ImageView choosenImg;

       @FXML
       private ImageView closeImage;

       @FXML
       private TextField nbTestImages;

       @FXML
       private TextField nbTrainingImages;

       @FXML
       private TextField nbPersons;

       @FXML
       private TextField stepThreshold;

       @FXML
       private Button chooseImage;

       @FXML
       private Button addORL;

       @FXML
       private ImageView reconstructed;

       protected static String pathToPredict;

       File pickedImage;

       private String ORL;

       @Override
       public void initialize(URL arg0, ResourceBundle arg1) {

             DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        	// TODO Auto-generated method stub
       	
             assert resultField != null : "fx:id=\"resultField\" was not injected: check your FXML file 'Predict.fxml'.";
             assert choosenImg != null : "fx:id=\"choosenImg\" was not injected: check your FXML file 'Predict.fxml'.";
             assert closeImage != null : "fx:id=\"closeImage\" was not injected: check your FXML file 'Predict.fxml'.";
             assert nbTestImages != null : "fx:id=\"nbTestImages\" was not injected: check your FXML file 'Predict.fxml'.";
             assert nbTrainingImages != null : "fx:id=\"nbTrainingImages\" was not injected: check your FXML file 'Predict.fxml'.";
             assert nbPersons != null : "fx:id=\"nbPersons\" was not injected: check your FXML file 'Predict.fxml'.";
             assert stepThreshold != null : "fx:id=\"stepThreshold\" was not injected: check your FXML file 'Predict.fxml'.";
             assert chooseImage != null : "fx:id=\"chooseImage\" was not injected: check your FXML file 'Predict.fxml'.";
             assert addORL != null : "fx:id=\"addORL\" was not injected: check your FXML file 'Predict.fxml'.";

             resultField.setTooltip(new Tooltip("Identité des personnes connues"));

             //Button Handling
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
             if (pathToPredict != null) {
             if(!pathToPredict.isEmpty())
             {
                 String pathHisto = "historique/"+"his"+(HistoryList.getHistoryList().size());

            	    //to convert pgm to jpg and save the file in history folder    
            	        try {
							converter.convertFormat(pathToPredict, pathHisto);
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							//continue 
						} 
            	 int p=0;
     		   try {

     		       p=RecognitionSystem.getProcess().predict(pathToPredict);

     		      } catch (IOException e) {
     			  // TODO Auto-generated catch block
     			  e.printStackTrace();
     		       }

          	String path="";
          	try {
   			 path=converter.convertFormat(pathToPredict, "test");
   		    } catch (IOException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		    }
           Image new1 = new Image(new File(path).toURI().toString());
           choosenImg.setImage(new1);
           choosenImg.setVisible(true);
           choosenImg.setCache(true);
           
           //code to show the the reconstructed image
           String path2 = "";
           try {
               path2 = converter.convertFormat("reconstructed_face.pgm", "test");
           } catch (IOException e) {
               e.printStackTrace();
           }
          
           Image new2 = new Image(new File(path2).toURI().toString());

           this.reconstructed.setImage(new2);
           this.reconstructed.setVisible(true);
           this.reconstructed.setCache(true);

           if (p >= 1) {
               resultField.setText("La personne est connue , c'est S" + p);
               path2 =RecognitionSystem.getDatabase().getPATH()+"//s"+p+"//1.pgm";
               try {
                   path2 = converter.convertFormat(path2, "test");
                   HistoryList.addHistory(true, pathHisto+".jpg", dtf.format(LocalDateTime.now()));
               } catch (IOException | ClassNotFoundException e) {
                   e.printStackTrace();
               }
                //Code to show the similar image
                new2 = new Image(new File(path2).toURI().toString());

               this.closeImage.setImage(new2);
               this.closeImage.setVisible(true);
               this.closeImage.setCache(true);
           } else {
               if (p == 0) {
                   String paa = "src/UI/assets/unkown.jpg";
                   File relativeFile = new File(paa);
                   Image newb = new Image(relativeFile.toURI().toString());

                   this.closeImage.setImage(newb);
                   this.closeImage.setVisible(true);
                   this.closeImage.setCache(true);
                   resultField.setText("Cette personne est inconnue ! ");

               } else {
                   resultField.setText("Cette personne ne figure pas dans notre base");
               }
               try {
                   HistoryList.addHistory(false, pathHisto+".jpg", dtf.format(LocalDateTime.now()));
               } catch (IOException | ClassNotFoundException e) {
                   e.printStackTrace();
               }
           }
              

          	 pathToPredict="";
             }
             }

       	
       }

     

       void pickImage() throws IOException{
           Stage stage = new Stage();
           FileChooser fileChooser = new FileChooser();
           fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PGM","*.pgm"), new FileChooser.ExtensionFilter("PNG","*.png"), new FileChooser.ExtensionFilter("JPG","*.jpg"));
           fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
           fileChooser.setTitle("Choose a picture to match");
           pickedImage = fileChooser.showOpenDialog(stage);
           String pathHisto = "historique/"+"his"+(HistoryList.getHistoryList().size());
           DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

           String ext = FilenameUtils.getExtension(pickedImage.getAbsolutePath());

           FaceDetector fd =new FaceDetector(pickedImage);
           File histoFile = null;
               try {
                   if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
                       if(fd.detectFace()){
                    	   pickedImage = fd.getExtraction_jpg();
                    	  
                       pickedImage=converter.convertFormat(ImageIO.read(pickedImage), RecognitionSystem.getDatabase().getNUMROWS(), RecognitionSystem.getDatabase().getNUMCOLS());
                      
                   }
                   }
                    else {

                           try {
                               converter.convertFormat(pickedImage.getAbsolutePath(),"test");
                        	   FaceDetector fdd = new FaceDetector(new File("test.jpg"));
                               if(fdd.detectFace()) {
                                   C_predict.pathToPredict = pickedImage.getAbsolutePath();
                               }
                           
							histoFile=pickedImage;
						} catch (Exception e) {
							// TODO: handle exception
						}
                           
                   }
                   int p = RecognitionSystem.getProcess().predict(pickedImage.getAbsolutePath());

                   //code to show the chosen image
                   String path = converter.convertFormat(pickedImage.getAbsolutePath(), pathHisto);
                   Image new1 = new Image(new File(path).toURI().toString());
                   choosenImg.setImage(new1);
                   choosenImg.setVisible(true);
                   choosenImg.setCache(true);

                   //code to show the the similair image
                   String path2 = "";
                   //convertir pgm en jpg de l'image reconstruite
                   try {
                       path2 = converter.convertFormat("reconstructed_face.pgm", "test");
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                  
                   Image new2 = new Image(new File(path2).toURI().toString());

                   this.reconstructed.setImage(new2);
                   this.reconstructed.setVisible(true);
                   this.reconstructed.setCache(true);

                   if (p >= 1) {
                       resultField.setText("Cette personne est connue c'est S " + p);
                       path2 =RecognitionSystem.getDatabase().getPATH()+"//s"+p+"//1.pgm";
                       try {
                           path2 = converter.convertFormat(path2, "test");
                           HistoryList.addHistory(true, pathHisto+".jpg", dtf.format(LocalDateTime.now()));
                       } catch (IOException | ClassNotFoundException e) {
                           e.printStackTrace();
                       }

                       new2 = new Image(new File(path2).toURI().toString());

                       this.closeImage.setImage(new2);
                       this.closeImage.setVisible(true);
                       this.closeImage.setCache(true);
                   } else {
                       if (p == 0) {
                           String paa = "src/UI/assets/unkown.jpg";
                           File relativeFile = new File(paa);


                           Image newb = new Image(relativeFile.toURI().toString());

                           this.closeImage.setImage(newb);
                           this.closeImage.setVisible(true);
                           this.closeImage.setCache(true);
                           resultField.setText("Cette personne est inconnue !");

                       } else {
                           resultField.setText("Cette personne ne figure pas dans notre base de données ");
                       }
                       try {
                           HistoryList.addHistory(false, pathHisto+".jpg", dtf.format(LocalDateTime.now()));
                       } catch (IOException | ClassNotFoundException e) {
                           e.printStackTrace();
                       }
                   }

               } catch (Exception e) {
                   // TODO: handle exception
            	   e.printStackTrace();
                   Alert alert = new Alert(AlertType.INFORMATION);
                   alert.setTitle("Mauvaise Image");
                   alert.setContentText("L'image ne contient pas de visage ou que le visage n'est pas assez clair !");
                   alert.showAndWait();
               }


       }


      public  void  chooseIm(ActionEvent act) throws IOException
      {

      }
      public void ret(ActionEvent a) throws IOException
      {
          FileUtils.deleteQuietly(new File("test.jpg"));
   	   Main.manager.showScene(Main.manager.getScene("main"));
      }


   }




