package Controllers;

import Kernel.Reconnaissance.RecognitionSystem;
import Kernel.Reconnaissance.Reconaissance;
import Kernel.Utils.DataBase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class Main extends Application {
   public static int timetoInit=0;
   public static int NbrePersonne;

    static class Admin{
        private static String name;
        private static String pwd;

        Admin(String na, String pd){
            name=na;
            pwd=pd;
        }

        public static boolean testAdmin(String nam, String pw){
            return ((new String(nam + pw)).compareTo(name + pwd) == 0);
        }
    }

    static public sceneManager manager;
   
    public static Admin admin = new Admin("lynda", "della");



	@Override
    public void start(Stage primaryStage) throws Exception{

	   TrainSystem();

       manager = new sceneManager(primaryStage);

        manager.addScene("slider", new sceneManager.Scene(600,900,"Welcome!", "../UI/Slider.fxml"));
        manager.addScene("main", new sceneManager.Scene(600,900,"Main", "../UI/Main.fxml"));
        manager.addScene("cam", new sceneManager.Scene(500,300,"Taking Picture", "../UI/Take_Picture.fxml"));
        manager.addScene("onboard", new sceneManager.Scene(600,900,"Retina", "../UI/On_Boarding.fxml"));
        manager.addScene("predict", new sceneManager.Scene(600,900,"Predict", "../UI/Predict.fxml"));
        manager.addScene("admin", new sceneManager.Scene(600,900,"Admin", "../UI/Admin_Dashboard.fxml"));
        manager.addScene("addPerson", new sceneManager.Scene(500,300,"Add a new Person", "../UI/Add_Person.fxml"));
        manager.addScene("experiment", new sceneManager.Scene(680,1300,"Experimental  ", "../UI/Dashboard.fxml"));
        manager.addScene("verify", new sceneManager.Scene(600,900,"verify  ", "../UI/Verify.fxml"));

       /** Parent root = FXMLLoader.load(getClass().getResource("../UI/On_Boarding.fxml"));
        primaryStage.setTitle("Retina");
        primaryStage.setScene(new Scene(root, 900, 600));
        
        primaryStage.show();*/
        manager.showScene(manager.getScene("onboard"));

        if((new File("historique/history.ser")).length()>0){
            HistoryList.readList();
        }

    }
	
	public static void TrainSystem()
    {
		
		 DataBase database=null;
			

		try {
		 database=new DataBase(5,"./ORL");
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
		  //System.out.println(RecognitionSystem.getDatabase().getPATH());
		}
    


    public static void main(String[] args) {
        launch(args);
    }
}