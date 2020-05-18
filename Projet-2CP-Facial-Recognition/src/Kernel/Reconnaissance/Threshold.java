package Kernel.Reconnaissance;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ejml.simple.SimpleMatrix;

import Kernel.Utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
public class Threshold {

    private static double threshold = 0;
    private static  ThresholdParameters thresh = new ThresholdParameters(0,0,0,0,0,0,0);
    private static  ArrayList<Double> thresholds = new ArrayList<>();
    private static  ArrayList<ThresholdParameters> threshies = new ArrayList<>();
    private  SimpleMatrix confusionM = new SimpleMatrix(0,0) ;
    //les valeurs attribuées par défaut
    private static  int NUMBEROFTESTPICTURES = 2 ;
    private static  int NB_expected = 0 ;
    private static  int NB_hits = 0 ;

    private static  double THRESHOLD_STEP = 0.005;
    @SuppressWarnings("unused")
   

    //Getters & Setters


    public static  double getThreshold(){
        return threshold;
    }
    public static ThresholdParameters getThresh() {
		return thresh;
	}
	public static  ArrayList<ThresholdParameters> getThresholdsParm(){
        return threshies;
    }
    public static  ArrayList<Double> getThresholds() {
        return thresholds;
    }


    public ArrayList<ThresholdParameters> getThreshies() {
        return threshies;
    }











    public static   void thresholds_calculator( int nb_test, double step,int disChoix, ACP ACP) throws IOException {
    String    ORL_path = ACP.getDataBase().getPATH();
        NUMBEROFTESTPICTURES = nb_test;
        THRESHOLD_STEP = step ;
      int   dischoice=disChoix;
      /*  ArrayList<SimpleMatrix> TrainedImages = new ArrayList<SimpleMatrix>(); //the 5 images used in training
        for (int i = 0; i < ACP.trainingPictures.getMatrix().getNumCols(); i++) {
            TrainedImages.add(ACP.trainingPictures.extractVector(false, i)); //(10,1)
        }*/

        ArrayList<Double> dis_min = new ArrayList<Double>();
        ArrayList<Integer> predicts = new ArrayList<>();
        ArrayList<Integer> excpected = new ArrayList<>();

        int hits =0 ;
        int total_number = ACP.getDataBase().getNUMBEROFTRAININGPERSONS()*NUMBEROFTESTPICTURES;
        NB_expected = total_number;
        for (int itr1 = 1; itr1 <= ACP.getDataBase().getNUMBEROFTRAININGPERSONS(); itr1++) {
            for (int itr2 = ACP.getDataBase().getNUMBEROFPICTURES()+1; itr2 <= (NUMBEROFTESTPICTURES+ACP.getDataBase().getNUMBEROFPICTURES()) ; itr2++) {
                SimpleMatrix test = new SimpleMatrix(ImageProcessing.PGMIO.read(new File(ORL_path+"\\s"+itr1+"\\"+itr2+".pgm")));
                test.reshape(ACP.getDataBase().getNUMROWS() * ACP.getDataBase().getNUMCOLS(), 1);
                SimpleMatrix original = test;
                test = test.minus(ACP.getMean());
              //  ACP.getMean().print();
            //  ACP.getTrainingMatrix().print();
                SimpleMatrix test2 = ACP.getTransform().mult(test);
             //   System.out.println("s"+itr1 + "pic"+itr2);

                SimpleMatrix distances=null;
                switch (dischoice) {
                    case 1:
                        distances= Distance.distanceMahalanobis(test2,ACP.getTrainingMatrix());

                        break;
                    case 2:


                        distances= Distance.disManhattan(test2, ACP.getTrainingMatrix());

                        break;
                    case 3:

                        distances= Distance.disEuclidienne(ACP.getTrainingMatrix(), test2);
                        //     distances=Distance.disManhattan(test2, ACP.trainingPictures);


                        break;

                    default:
                        distances= Distance.distanceMahalanobis(test2,ACP.getTrainingMatrix());
                        ;
                }

                int returnedProfile=0;


                for(int j=0; j<ACP.getTrainingMatrix().numCols() ;j++){
                    if(distances.get(j)<distances.get(returnedProfile))
                        returnedProfile = j;
                }
                dis_min.add(distances.get(returnedProfile));
                if((returnedProfile/ACP.getDataBase().getNUMBEROFPICTURES() +1)==itr1) hits++;
                excpected.add(itr1);
                predicts.add(returnedProfile/ACP.getDataBase().getNUMBEROFPICTURES() +1);
            }
        }


        threshies =Calcul(dis_min,predicts,excpected,THRESHOLD_STEP) ;


    }
    public static double att;
    public static  ArrayList<ThresholdParameters> Calcul(ArrayList<Double> dis_min,ArrayList<Integer> predicts,ArrayList<Integer> excpected,double step ) {
        //returning the max and min between all distances :
        double max =  Collections.max(dis_min);
        double min = 0;
        double curseur_threshold = min ;
    
        ArrayList<ThresholdParameters> thresholdies = new ArrayList<ThresholdParameters>();

        while(curseur_threshold < max){
            thresholds.add(curseur_threshold);
            SimpleMatrix confusion_matrix = new SimpleMatrix(2,2);
            for(int i=0 ; i<dis_min.size() ; i++){
                if(dis_min.get(i) > curseur_threshold){
                    //we have a negative :
                    if(excpected.get(i)==predicts.get(i)) confusion_matrix.set(0,1,confusion_matrix.get(0,1)+1);
                    else confusion_matrix.set(1,1,confusion_matrix.get(1,1)+1);
                }
                else{
                    //we have a positive !
                    if(excpected.get(i)==predicts.get(i)) confusion_matrix.set(0,0,confusion_matrix.get(0,0)+1);
                    else confusion_matrix.set(1,0,confusion_matrix.get(1,0)+1);
                }

            }

            ThresholdParameters thr=modifyParameters( confusion_matrix, curseur_threshold);
            thresholdies.add(thr);
            // confusion_matrices.add(confusion_matrix);
            curseur_threshold = curseur_threshold + step * max ;
        }
        return thresholdies;
    }
    public static  ThresholdParameters modifyParameters(SimpleMatrix confusion_matrix,double curseur_threshold)
    {
        ThresholdParameters thr = new ThresholdParameters(curseur_threshold,0,0,0, 0,0,0);

        thr.setRecall((confusion_matrix.get(0,0)/(confusion_matrix.get(0,0) + confusion_matrix.get(0,1))));
        thr.setPrecision((confusion_matrix.get(0,0)/(confusion_matrix.get(0,0) + confusion_matrix.get(1,0))));
        thr.setAccuracy((confusion_matrix.get(0,0)+confusion_matrix.get(1,1))/(confusion_matrix.get(0,0)+confusion_matrix.get(0,1)+confusion_matrix.get(1,0)+confusion_matrix.get(1,1)));
        thr.setF1_score(2*(thr.getPrecision()*thr.getRecall())/(thr.getPrecision()+thr.getRecall()));
        thr.setFAR(confusion_matrix.get(1,0)/(confusion_matrix.get(1,0)+confusion_matrix.get(1,1)));
        thr.setFRR(confusion_matrix.get(0,1)/(confusion_matrix.get(0,1)+confusion_matrix.get(0,0)));

        return thr;
    }
    public static void threshold_adjust( ){
        ArrayList<Double> differences = new ArrayList<>();
        
        for(int i=0 ; i<threshies.size() ; i++){
            differences.add(Math.abs((threshies.get(i).getFAR()-threshies.get(i).getFRR())));

        }

        double min_diff = Collections.min(differences);
        int point = 0 ;
        for(int i=0 ; i<differences.size() ; i++)
        {
            if(min_diff == differences.get(i)) point = i ;
        }
        
        threshold = threshies.get(point).getThreshold_value();
        thresh = threshies.get(point);
     
     //   confusionM = confusionMatrices.get(point);

     
    }




}
