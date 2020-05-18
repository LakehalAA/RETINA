package Kernel.Reconnaissance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Kernel.Utils.DataBase;

public class RecognitionSystem {
	
	
	
	
	

	private static  int dischoice=1;//1mahalanobis 2manhattan 3euclidienne
	private static int numberOfPersons=40;
	private static int trainingPicsParPersonne=5;//on prend 5 par defaut 
	private static  String ORL_PATH;
    private static DataBase database ;//=
    private static int PicsToAjustThreshold_ParPersonne=2;//pour calculer le seuil on prend 2 par defaut
    private static double threshold_Step;//par defaut 0.00005
   
    private static Reconaissance process;// = new Reconaissance(orl,trainingPicsParPersonne,PicsToAjustThreshold_ParPersonne,dischoice,threshold_Step);
   
    private static double Threshold_Value;

    private static ArrayList<ThresholdParameters> thresholds;//to so the graph of FAR AND FRR
    private static long execution_time;//nanosecondes
    private static int  MaxPicsParPersonne=10;
    private static int maxPersonsINDataBase=40;//pour l instant a modifier lorsque utilisateur ajoute une ppersonne 
    
    
  
	public static ArrayList<ThresholdParameters> getThresholds() {
		return thresholds;
	}

	public static int getNumberOfPersons() {
		return numberOfPersons;
	}

	public static int getTrainingPicsParPersonne() {
		return trainingPicsParPersonne;
	}

//utiliser de la fenetre de prediction juste on applant classe.getProcess.pridict(path)
	public static Reconaissance getProcess() {
		return process;
	}



	
/**used to get the graph*/
	
/************/
	/**execution time needs to be shown in exprimental fennetre**/
	public static long getExecution_time() {
		return execution_time;
	}

	public static void setMaxPersonsINDataBase(int maxPersonsINDataBase) {
		maxPersonsINDataBase = maxPersonsINDataBase;
	}
	
	
    
    public static DataBase getDatabase() {
		return database;
	}

	public static void trainModel(DataBase database,int dischoice,int trainingPicsParPersonne,int PicsToAjustThreshold_ParPersonne, double threshold_Step) throws IOException
    {
           database=database;
        	long startTime = System.currentTimeMillis();
        	ORL_PATH=database.getPATH();
        	trainingPicsParPersonne=trainingPicsParPersonne;
        	numberOfPersons=database.getNUMBEROFTRAININGPERSONS();

           
			process=new Reconaissance(database,trainingPicsParPersonne,PicsToAjustThreshold_ParPersonne,dischoice,threshold_Step,10);
			Threshold_Value=Threshold.getThreshold();
			//System.out.println(Threshold_Value);
	    	long endTime = System.currentTimeMillis();
        	execution_time = (endTime - startTime); 
			thresholds=Threshold.getThresholdsParm();
		
    }


}
