package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Kernel.FaceDetector.FaceDetector;
import Kernel.Reconnaissance.RecognitionSysCatactiristics;
import Kernel.Reconnaissance.RecognitionSystem;
import Kernel.Utils.DataBase;
import ProgressBar.RingProgressIndicator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class C_dashboardExperim {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label persons;
	@FXML
	private Label resolution;
	@FXML
	private Label faces;

	@FXML
	private Label timePredict;

	@FXML
	private Label ressemblanceDegree;

	@FXML
	private LineChart<Number, Number> roc;

	@FXML
	private LineChart<Number, Number> scoreGraph;

	@FXML
	private HBox rocCurve1;

	@FXML
	private Label score;

	@FXML
	private BarChart<String, Number> bars;

	@FXML
	private Label recognition;

	@FXML
	private Label confusion;

	@FXML
	private Label rejection;

	@FXML
	private ImageView reconstructed;

	@FXML
	private ImageView closestImage;

	@FXML
	private Slider threPrecision;

	@FXML
	private Slider numberEigens;

	@FXML
	private HBox distanceChoice;

	@FXML
	private ChoiceBox<distance> distances;

	@FXML
	private Label orlPath;

	@FXML
	private Button orlPicker;

	@FXML
	private Spinner<Integer> nbTest;

	@FXML
	private HBox distanceChoice1;

	@FXML
	private Spinner<Integer> nbTraining;

	@FXML
	private Button back;

	@FXML
	private Button chooseImage;

	private static File path;
	@FXML
	private Button execute;
	private static String pathtemp;

	@FXML
	private Label nbreFaces;
	private static DataBase db;

	@FXML
	private Button choose;

 //Handling buttons
@FXML
public void chooseImage(ActionEvent event) throws IOException {
    // TODO Autogenerated

    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PGM", "*.pgm"), new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("JPG", "*.jpg"));
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    fileChooser.setTitle("Chooisissez un visage � identifier");
    File choose = fileChooser.showOpenDialog(stage);

    if (choose != null) {
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
        ringProgressIndicator.setVisible(true);
        ringProgressIndicator.makeIndeterminate();
        Scene scene = new Scene(ringProgressIndicator);
        dialogStage.setScene(scene);
        dialogStage.show();
        String ext = FilenameUtils.getExtension(choose.getAbsolutePath());

        FaceDetector fd = new FaceDetector(choose);
    

            try {
            	 if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
                if (fd.detectFace()) {

                   choose=fd.getExtraction_jpg();

                    choose = converter.convertFormat(ImageIO.read(choose), db.getNUMROWS(), db.getNUMCOLS());
                    // System.out.println("hey there,,covert jpg png to pgm");
                }
                }
                
            
                } catch (Exception e) {
                // TODO: handle exception
            	dialogStage.close();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Mauvaise Image");
                alert.setContentText("L'image ne contient pas de visage ou que le visage n'est pas assez clair !");
                alert.showAndWait();
                }
             
   

               
                //  int 	 p=Main.acp.predict(pickedImage.getAbsolutePath());
                int p = RecognitionSysCatactiristics.getProcess().predict(choose.getAbsolutePath());


                //closeee  image code take the first one

                if (p > 0) {
                    File pf = new File(db.getPATH() + "//s" + p + "//1.pgm");
                    String path = converter.convertFormat(pf.getAbsolutePath(), "test");
                    Image new1 = new Image(new File("test.jpg").toURI().toString());
                    closestImage.setImage(new1);
                    closestImage.setVisible(true);
                    closestImage.setCache(true);
                } else {

                    String paa = "src/UI/assets/unkown.jpg";
                    File relativeFile = new File(paa);
                    // System.out.println(relativeFile.toURI().toString());

                    Image newb = new Image(relativeFile.toURI().toString());

                    this.closestImage.setImage(newb);
                    this.closestImage.setVisible(true);
                    this.closestImage.setCache(true);

                }
                //code to show the the reconstructed  image


                String path2 = "";
                try {
                    path2 = converter.convertFormat("reconstructed_face.pgm", "test");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Image new2 = new Image(new File(path2).toURI().toString());

                reconstructed.setImage(new2);
                reconstructed.setVisible(true);
                reconstructed.setCache(true);


                //formule pour calculer le degre de ressemblance


                double mindist = RecognitionSysCatactiristics.getProcess().getDis_min();
                double ressemblance = RecognitionSysCatactiristics.getThreshold_Value() - mindist;
                ressemblance = ressemblance / RecognitionSysCatactiristics.getThreshold_Value();
                ressemblance = 0.2 * ressemblance + 0.8;
                ressemblance = ressemblance * 100;
               // System.out.println("le degre de ressemblance est de :" + ressemblance);
                ressemblanceDegree.setText(Double.toString(ressemblance));

dialogStage.close();
           
    }
       
       
    

    }

// Event Listener on Button[#orlPicker].onAction
	@FXML
	public void chooseORL(ActionEvent event) throws IOException, InterruptedException {

		Stage stage = new Stage();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		directoryChooser.setTitle("Choisissez votre base de donn�es");
		File pickedPath = directoryChooser.showDialog(stage);
		if (pickedPath != null) {
			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.TRANSPARENT);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
			ringProgressIndicator.setVisible(true);
			ringProgressIndicator.makeIndeterminate();
			Scene scene = new Scene(ringProgressIndicator);
			dialogStage.setScene(scene);
			dialogStage.show();
			path = new File("temporary");
			FileUtils.deleteQuietly(path);
			path = new File("temporary");
			pathtemp = path.getAbsolutePath();
			try {
				FileUtils.copyDirectory(pickedPath, path);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//reorganize();
			dialogStage.close();
		     } else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("choissisez votre base de donn�es ");

			alert.setContentText(" dossier non s�l�ctionn� !");

			alert.showAndWait();
		}

		// TODO Autogenerat
	}

	// Event Listener on Button[#back].onAction
	@FXML
	public void backToMain(ActionEvent event) throws IOException {
		// TODO Autogenerated
		Main.manager.showScene(Main.manager.getScene("main"));
	}

	// Event Listener on Button[#execute].onAction
	@FXML
	public void execute(ActionEvent event) {
		// TODO Autogenerated
		try {
			db = new DataBase(nbTraining.getValue(), pathtemp, 100);
		} catch (IOException e1) {
			// TODO Auto-generated catch block

    		Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("base de donn�es non conforme ");

			alert.setContentText("veuillez prendre le soin d'utiliser une base de donn�es conforme !!!");

			alert.showAndWait();
		}

         if(nbTest.getValue()+nbTraining.getValue()<db.getNUMBEROFIMAGESMAXPERPERSON()) {
		// code to get distance
		int dis = distances.getSelectionModel().getSelectedIndex() + 1;

		// System.out.println("distances"+dis);

		// code to get threshold step
		double step = threPrecision.getValue();

		try {
			db = new DataBase(nbTraining.getValue(), pathtemp, 100);
			RecognitionSysCatactiristics.setK((int) this.numberEigens.getValue());
			RecognitionSysCatactiristics.trainModel(db, dis, nbTest.getValue(), step);
			RecognitionSysCatactiristics.calculateRates();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		recognition.setText(Double.toString(RecognitionSysCatactiristics.getTauxDeReconnaissance()));
		rejection.setText(Double.toString(RecognitionSysCatactiristics.getTauxDeRejet()));
		confusion.setText(Double.toString(RecognitionSysCatactiristics.getTauxDeConfusion()));

//needs to be changed wrong	
		score.setText(Double.toString(RecognitionSysCatactiristics.getThresh().getF1_score()));

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();

		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.getData().clear();
		XYChart.Series serie1 = new XYChart.Series();

		serie1.setName("FRR");
		XYChart.Series serie2 = new XYChart.Series();
		serie2.setName("FAR");
		XYChart.Series<Number, Number> serie3 = new XYChart.Series<>(), serie4 = new XYChart.Series<>();
		serie3.setName("Recall");

		for (int i = 0; i < RecognitionSysCatactiristics.getThresholds().size(); i++) {

			serie1.getData()
					.add(new XYChart.Data(RecognitionSysCatactiristics.getThresholds().get(i).getThreshold_value(),
							RecognitionSysCatactiristics.getThresholds().get(i).getFRR()));
			serie2.getData()
					.add(new XYChart.Data(RecognitionSysCatactiristics.getThresholds().get(i).getThreshold_value(),
							RecognitionSysCatactiristics.getThresholds().get(i).getFAR()));
			serie3.getData()
					.add(new XYChart.Data(RecognitionSysCatactiristics.getThresholds().get(i).getThreshold_value(),
							RecognitionSysCatactiristics.getThresholds().get(i).getRecall()));
		}

		// serie3.setData(serie1.getData());
		// serie4.setData(serie2.getData());

		// serie4.setName("FAR");
		roc.getData().clear();
		scoreGraph.getData().clear();
		try {

			roc.getData().addAll(serie1, serie2);
			scoreGraph.getData().addAll(serie3);
		} catch (Exception e) {
			// System.out.print(e.getMessage());
			e.printStackTrace();
		}

		XYChart.Series<String, Number> barSerie1 = new XYChart.Series<>();
		barSerie1.getData().add(new XYChart.Data<>("6", 15));
		barSerie1.getData().add(new XYChart.Data<>("8", 45));
		barSerie1.getData().add(new XYChart.Data<>("10", 90));
		barSerie1.getData().add(new XYChart.Data<>("12", 225));
		barSerie1.setName("Time");
		bars.getData().clear();
		bars.getData().add(barSerie1);
		timePredict.setText(Long.toString(RecognitionSysCatactiristics.getExecution_time()));
		persons.setText(Integer.toString(RecognitionSysCatactiristics.getMaxPersonsINDataBase()));
		int prdct = RecognitionSysCatactiristics.getNumberOfPersons()
				* RecognitionSysCatactiristics.getMaxPicsParPersonne();
		faces.setText(Integer.toString(prdct));// *RecognitionSysCatactiristics.getMaxPicsParPersonne()));
		String str = Integer.toString(db.getNUMROWS()) + "*" + Integer.toString(db.getNUMCOLS());
		resolution.setText(str);

	}
         else {
        		Alert alert = new Alert(AlertType.INFORMATION);
    			alert.setTitle("diminuer les images prises pour l'entrainement ");

    			alert.setContentText("vous pouvez pas utiliser des images plus que celles qui sont dans votre base !!!");

    			alert.showAndWait();
			
		}
	}

	private ObservableList<distance> distancesObsList;

	public enum distance {
		Mahalanobis, Manhatten, Euclid
	}

	@SuppressWarnings("unchecked")
	@FXML
	void initialize() throws IOException {

		assert persons != null : "fx:id=\"persons\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert faces != null : "fx:id=\"faces\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert timePredict != null : "fx:id=\"timePredict\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert ressemblanceDegree != null : "fx:id=\"ressemblanceDegree\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert roc != null : "fx:id=\"roc\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert rocCurve1 != null : "fx:id=\"rocCurve1\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert score != null : "fx:id=\"score\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert bars != null : "fx:id=\"bars\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert recognition != null : "fx:id=\"recognition\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert confusion != null : "fx:id=\"confusion\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert rejection != null : "fx:id=\"rejection\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert reconstructed != null : "fx:id=\"reconstructed\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert closestImage != null : "fx:id=\"closestImage\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert threPrecision != null : "fx:id=\"threPrecision\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert numberEigens != null : "fx:id=\"numberEigens\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert distanceChoice != null : "fx:id=\"distanceChoice\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert distances != null : "fx:id=\"distances\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert orlPath != null : "fx:id=\"orlPath\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert orlPicker != null : "fx:id=\"orlPicker\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert nbTest != null : "fx:id=\"nbTest\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert distanceChoice1 != null : "fx:id=\"distanceChoice1\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert nbTraining != null : "fx:id=\"nbTraining\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert back != null : "fx:id=\"back\" was not injected: check your FXML file 'DashBoard.fxml'.";
		assert chooseImage != null : "fx:id=\"chooseImage\" was not injected: check your FXML file 'DashBoard.fxml'.";

		distancesObsList = FXCollections.observableArrayList(distance.values());
		// System.out.print(distancesObsList.size());
		distances.setItems(distancesObsList);

		int maxTestImages = 3;
		int initialTestImages = 1;
		nbTest.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxTestImages, initialTestImages));

		int maxTrainingImages = 7;
		int initialTrainingImages = 5;
		nbTraining.setValueFactory(
				new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxTrainingImages, initialTrainingImages));

	}

	// utils

	public void reorganize() throws IOException, InterruptedException {
		File originalDir = new File(pathtemp);

		File[] folders = originalDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (int i = 0; i < folders.length; i++) {
			File temp = new File(folders[i].getAbsolutePath());
			temp.renameTo(new File(originalDir.getAbsoluteFile() + "/ss" + (i + 1)));
		}

		for (int i = 0; i < folders.length; i++) {
			File temp = new File(originalDir.getAbsolutePath() + "/ss" + (i + 1));
			temp.renameTo(new File(originalDir.getAbsoluteFile() + "/s" + (i + 1)));
		}

		for (int i = 0; i < folders.length; i++) {
			File file2 = new File(folders[i].getAbsolutePath());
			File[] images1 = file2.listFiles();

			try {
				for (int j = 0; j < folders.length; j++) {
					if (images1[j].isFile()) {
						File rename = new File(file2.getAbsoluteFile() + "\\" + (j + 1) + ".pgm");
						if (!rename.exists())
							images1[j].renameTo(rename);

						// images1[j].renameTo(new File(file2.getAbsoluteFile() + "\\" + (j + 1) +
						// ".pgm"));
					}
				}

			} catch (Exception e) {
				file2.getName();
			}

		}
	}
}