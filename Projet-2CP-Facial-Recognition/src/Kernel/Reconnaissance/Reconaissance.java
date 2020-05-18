package Kernel.Reconnaissance;

import Kernel.Utils.DataBase;
import Kernel.Utils.Distance;
import Kernel.Utils.ImageProcessing;


import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;

public class Reconaissance {
	private  int k=10;
    public  int getK() {
		return k;
	}


private static double dis_min;


	public  void setK(int k) {
		this.k = k;
	}

	private  ACP ACP;
	private static int distanceChoice;
	private static SimpleMatrix projection;
	private static SimpleMatrix original;
	
   

    public Reconaissance(DataBase database, int numberOftraingPics,int nb_tests, int distanceChoice,double step,int VP) throws IOException{


        this.ACP = new ACP(database,numberOftraingPics);
        ACP.setK(VP);
        this.distanceChoice=distanceChoice;
      
        
        	  ACP.train();
			Threshold.thresholds_calculator(nb_tests,step,distanceChoice,ACP);
			   Threshold.threshold_adjust();
		
     
        
        
    }

  

 

    public int predict(String path) throws IOException {
        //calculating the projection of the image in 'path'

        String result;
        int returned_profile = 0;

        SimpleMatrix test = new SimpleMatrix(ImageProcessing.PGMIO.read(new File(path)));
        test.reshape(ACP.getDataBase().getNUMROWS() * ACP.getDataBase().getNUMCOLS(), 1);

        test = test.minus(ACP.getMean());
        this.original = test;

        SimpleMatrix test2 = ACP.getTransform().mult(test);
        this.projection = test2;
       // test2 = ACP.getTrainingMatrix().mult(test2);
        int returnedProfile =0;
        SimpleMatrix distances=null;
        switch (distanceChoice) {
            case 1:
                distances= Distance.distanceMahalanobis(test2,ACP.getTrainingMatrix());

                break;
            case 2:


                distances= Distance.disManhattan(test2, ACP.getTrainingMatrix());
                //	distances=Distance.disEuclidienne(ACP.trainingPictures, test2);


                break;
            case 3:
                distances= Distance.disEuclidienne(ACP.getTrainingMatrix(), test2);


                break;

            default:
                distances= Distance.distanceMahalanobis(test2,ACP.getTrainingMatrix());
                ;
        }
        double minimumdist=distances.get(0);
        for (int i = 0; i<ACP.getTrainingMatrix().numCols(); i++){

            double dist = distances.get(i);
            if(dist< minimumdist){
                minimumdist = dist;
                returnedProfile = i;
            }
        }
        returned_profile = returnedProfile/ACP.getDataBase().getNUMBEROFPICTURES() +1;
        if(minimumdist > Threshold.getThreshold()){result="Unknown person in the dataset";
            returned_profile = 0;}
        else {
            returned_profile = returnedProfile/ACP.getDataBase().getNUMBEROFPICTURES() +1;
            result = ("the profile is known in the dataset , it's the person :: "+(Double.toString(Math.ceil(returnedProfile/ACP.getDataBase().getNUMBEROFPICTURES() +1))));
        }
  dis_min=minimumdist;

        //reconstruction phase
    
          SimpleMatrix  projection = ACP.getTrainingMatrix().extractVector(false,returnedProfile);
          reconstruct_face(projection,returnedProfile);
       
        /*SimpleMatrix Mp = ACP.mean;
        Mp.reshape(ACP.getNUMROWS(),ACP.getNUMCOLS());
        double[][] U2 = PGMIO.simpleToDouble(Mp);
        U2 = PGMIO.mat2gray(U2);
        PGMIO.write(U2, new File("mean_face.pgm"));*/

        return returned_profile;
    }

    public void reconstruct_face(SimpleMatrix project,int returned_profile) throws IOException {
        /*SimpleMatrix mat2 = ACP.getReconstruction_space().extractVector(false,returned_profile);
        mat2.reshape(ACP.getDataBase().getNUMROWS(),ACP.getDataBase().getNUMCOLS());*/
        SimpleMatrix mat2 = ACP.getTransform().pseudoInverse().mult(this.projection).plus(ACP.getMean());
        mat2 = mat2.minus(this.original);
        mat2.reshape(ACP.getDataBase().getNUMROWS(),ACP.getDataBase().getNUMCOLS());
        double [][] mat = ImageProcessing.PGMIO.simpleToDouble(mat2);
        mat = ImageProcessing.mat2gray(mat);
        ImageProcessing.PGMIO.writetoReconstruct(mat, new File("reconstructed_face.pgm"));

    }





	public static  double getDis_min() {
		// TODO Auto-generated method stub
		return dis_min;
	}

   
}
