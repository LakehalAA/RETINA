package Controllers;

import Kernel.FaceDetector.FaceDetector;
import Kernel.Reconnaissance.RecognitionSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;

public class C_main {
	
	@FXML
	private TabPane tabs;
	@FXML
	private Tab history;
	@FXML
	private FlowPane wrappingList;
	@FXML
	private Hyperlink onlineHelp;
	@FXML
	private Hyperlink importPic;
	@FXML
	private Button takePic;
	@FXML
	private Button verifye;
	@FXML
	private Button predict;
	@FXML
	private Button experiment;
	@FXML
	private Tab stats;
	@FXML
	private Pane chart;
	@FXML
	private Tab admin;
	@FXML
	private TextField name;
	@FXML
	private PasswordField pwd;
	@FXML
	private RadioButton remember;
	private Object IOException;


	// Event Listener on Button[#verifye].onAction

	//Handling buttons to other windows
	
	public void verify() throws IOException {
		Main.manager.showScene(Main.manager.getScene("verify"));
	}

	public void experiment(ActionEvent actionEvent) throws IOException {
		Main.manager.showScene(Main.manager.getScene("experiment"));
	}

	public void predict(ActionEvent actionEvent) throws IOException {
		Main.manager.showScene(Main.manager.getScene("predict"));
	}



	File pickedImage;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;


	@FXML
	private Button logIn;

	private ObservableList<VBox> historyList;

	public static int nbreHistFile;

	public FlowPane getWrappingList() {
		return wrappingList;
	}

	public void setWrappingList(FlowPane wrappingList) {
		this.wrappingList = wrappingList;
	}


	@FXML
	void initialize() throws IOException, InvalidFormatException {


		assert onlineHelp != null : "fx:id=\"onlineHelp\" was not injected: check your FXML file 'Main.fxml'.";
		assert importPic != null : "fx:id=\"importPic\" was not injected: check your FXML file 'Main.fxml'.";
		assert takePic != null : "fx:id=\"takePic\" was not injected: check your FXML file 'Main.fxml'.";
		assert verifye != null : "fx:id=\"verifye\" was not injected: check your FXML file 'Main.fxml'.";
		assert predict != null : "fx:id=\"predict\" was not injected: check your FXML file 'Main.fxml'.";
		assert experiment != null : "fx:id=\"experiment\" was not injected: check your FXML file 'Main.fxml'.";

		//Setting over mouse hover text
		verifye.setTooltip(new Tooltip("Mode vérification"));
		predict.setTooltip(new Tooltip("Mode identification"));
		experiment.setTooltip(new Tooltip("Mode expérimental"));
		takePic.setTooltip(new Tooltip("Photo par camera"));


		//checking if admin is remembered
		try {
			File administrator = new File("admin.txt");
			if (administrator.exists()) {
				try {

				Scanner scan = new Scanner(administrator);
				name.setText(scan.nextLine());
				pwd.setText(scan.nextLine());
				scan.close();}
				catch (NoSuchElementException e){

				}
			}
		} catch (FileNotFoundException e) {
			FileUtils.touch(new File("admin.txt"));
		}


		//Button handling
		takePic.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				try {
					sceneManager.showScene(sceneManager.getScene("cam"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});


		logIn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (Main.admin.testAdmin(name.getText(), pwd.getText())) {
					try {
						sceneManager.showScene(sceneManager.getScene("admin"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					   Alert alert = new Alert(AlertType.INFORMATION);
	                    alert.setTitle("Utilisateur ou mot de passe incorrect");
	                    alert.setContentText("Veuillez verifier le nom d'utilisateur et/ou le mot de passe !!");
	                    alert.showAndWait();
				}
			}
		});

		//History management
		historyList = FXCollections.observableArrayList();

		history.setStyle("-fx-font-family: Gotham; -fx-font-weight: bold;");
		stats.setStyle("-fx-font-family: Gotham; -fx-font-weight: bold");
		admin.setStyle("-fx-font-family: Gotham; -fx-font-weight: bold");

		try {
			File database = new File("historique");
			File[] folders = database.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});

			assert folders != null;
			Arrays.sort(folders, Comparator.comparingLong(File::lastModified).reversed());

			//Listing pictures from history
			int j = 0;

			while (j <= Math.min(5, HistoryList.getHistoryList().size() - 1)) {
				VBox cardHistory = HistoryList.getHistoryList().get(HistoryList.getHistoryList().size() - 1 - j).getHistoryCard();
				FlowPane.setMargin(cardHistory, new Insets(10, 20, 15, 0));
				wrappingList.getChildren().add(cardHistory);
				j++;

			}

			final NumberAxis xAxis = new NumberAxis();
			final NumberAxis yAxis = new NumberAxis();

			xAxis.setLabel("Valeurs du seuil");
			yAxis.setLabel("FRR/FAR");

			LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
			XYChart.Series serie1 = new XYChart.Series();
			serie1.setName("FRR");
			XYChart.Series serie2 = new XYChart.Series();
			serie2.setName("FAR");

			for (int i = 0; i < RecognitionSystem.getThresholds().size(); i++) {

				serie1.getData().add(new XYChart.Data(RecognitionSystem.getThresholds().get(i).getThreshold_value(), RecognitionSystem.getThresholds().get(i).getFRR()));
				serie2.getData().add(new XYChart.Data(RecognitionSystem.getThresholds().get(i).getThreshold_value(), RecognitionSystem.getThresholds().get(i).getFAR()));

			}

			lineChart.getData().addAll(serie1, serie2);
			chart.getChildren().add(lineChart);
		} catch (Exception e) {
			FileUtils.forceMkdir(new File("historique"));
		}

	}

	//Remember me feature
	@FXML
	public void remember(ActionEvent actionEvent) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("admin.txt"));
		bw.write(name.getText());
		bw.newLine();
		bw.write(pwd.getText());
		bw.close();
	}

	//Forget me feature
	public void forget(ActionEvent actionEvent) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter("admin.txt"));
	}

	@FXML
	void openPicker(ActionEvent actionEvent) throws IOException, InvalidFormatException {
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PGM", "*.pgm"),
		new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setTitle("Choisissez un visage à identifier");
		pickedImage = fileChooser.showOpenDialog(stage);

			String ext = FilenameUtils.getExtension(pickedImage.getAbsolutePath());

	    	FaceDetector fd = new FaceDetector(pickedImage);
		if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
			try {
				if(fd.detectFace()) {
					pickedImage = fd.getExtraction_jpg();
					pickedImage=converter.convertFormat(ImageIO.read(pickedImage), RecognitionSystem.getDatabase().getNUMROWS(), RecognitionSystem.getDatabase().getNUMCOLS());
					C_predict.pathToPredict=pickedImage.getAbsolutePath();
				}
				Main.manager.showScene(Main.manager.getScene("predict"));

			}catch (Exception e) {
				// TODO: handle exception
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Mauvaise Image");
				alert.setContentText("L'image ne contient pas de visage ou que le visage n'est pas assez clair !");
				alert.showAndWait();
			}
		}else {
				C_predict.pathToPredict = pickedImage.getAbsolutePath();
				Main.manager.showScene(Main.manager.getScene("predict"));
		}
	}
}
