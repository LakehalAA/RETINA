package Kernel.Reconnaissance;
import Kernel.Utils.*;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class main {
    public static void main(String [] args) throws IOException {
int dischoice=2;//1mahalanobis 2manhattan 3euclidienne
      
        
        System.out.println("haha");
     RecognitionSysCatactiristics system = new RecognitionSysCatactiristics();
    int trainingPicsParPersonne=5;
   int PicsToAjustThreshold_ParPersonne=1;
   double threshold_Step=0.00005;
   //int numberOfTrainingPersons=30;
   
 /**   RecognitionSysCatactiristics.trainModel(".\\ORL", dischoice, trainingPicsParPersonne, PicsToAjustThreshold_ParPersonne, threshold_Step);
    System.out.println("threshold value "+RecognitionSysCatactiristics.getThreshold_Value());
    System.out.println("time of execution is :"+ RecognitionSysCatactiristics.getExecution_time());*/
    //pour cette methode j ai utiliser 30 personne comme connues et 10 inconnues de la mm base de donnees
RecognitionSysCatactiristics.calculateRates();
System.out.println("time of execution is :"+ RecognitionSysCatactiristics.getExecution_time());

DataBase db=new DataBase(5, ".\\ORL", 100);
System.out.println("db"+db.getNUMBEROFIMAGESMAXPERPERSON());
RecognitionSysCatactiristics.setK(7);
RecognitionSysCatactiristics.trainModel(db,dischoice,1,0.0005 );
//RecognitionSysCatactiristics.trainModel(db, dischoice, PicsToAjustThreshold_ParPersonne, threshold_Step);
RecognitionSysCatactiristics.calculateRates();
System.out.println(RecognitionSysCatactiristics.getExecution_time());




System.out.println("time of execution is :"+ RecognitionSysCatactiristics.getExecution_time());

DataBase dbb=new DataBase(5, ".\\ORL", 100);
System.out.println("db"+dbb.getNUMBEROFIMAGESMAXPERPERSON());
RecognitionSysCatactiristics.trainModel(dbb,dischoice,1,0.0005 );
//RecognitionSysCatactiristics.trainModel(db, dischoice, PicsToAjustThreshold_ParPersonne, threshold_Step);
RecognitionSysCatactiristics.calculateRates();
System.out.println(RecognitionSysCatactiristics.getExecution_time());
RecognitionSystem.trainModel(db, dischoice, 5, 1,0.0005);


    }
}
