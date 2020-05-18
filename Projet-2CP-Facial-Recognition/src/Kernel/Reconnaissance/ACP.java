package Kernel.Reconnaissance;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;
import Kernel.Utils.*;
import java.io.File;
import java.io.IOException;

public class ACP {
    
    //les constantes attribuées par défauts
  static DataBase dataBase;
    int numberOfTrainingPics;
    //retourner les matrices suivantes
    private static SimpleMatrix eigenfaces;
    public static SimpleMatrix getTransform() {
		return transform;
	}

	private static SimpleMatrix transform;
    private static SimpleMatrix mean;
    private static SimpleMatrix trainingPictures;
    private static SimpleMatrix reconstruction_space ;
    

private static int k=10;
    public ACP(DataBase database, int numberoftrainingpictures) {
	// TODO Auto-generated constructor stub
    	this.dataBase=database;
    	this.numberOfTrainingPics=numberoftrainingpictures;
    	
}

	//Getters & Setters
 

    public SimpleMatrix getEigenfaces() {
        return eigenfaces;
    }

    

    public static DataBase getDataBase() {
		return dataBase;
	}

	
	
    public static SimpleMatrix getMean() {
        return mean;
    }

   

    public static SimpleMatrix getTrainingMatrix() {
        return trainingPictures;
    }

    
    public  void train() throws IOException {



        //M is -in the end- the mean vector
        //A has as columns all the image vectors
        SimpleMatrix M = new SimpleMatrix(0, 0);
        SimpleMatrix A = new SimpleMatrix(0, 0);
        SimpleMatrix C = new SimpleMatrix(0, 0);

        for (int itr1 = 1; itr1 <=dataBase.getNUMBEROFTRAININGPERSONS(); itr1++) {
            for (int itr2 = 1; itr2 <= this.numberOfTrainingPics; itr2++) {
              File  file = new File(dataBase.getPATH()+"\\s" + itr1 + "\\" + itr2 + ".pgm");
                C = new SimpleMatrix(ImageProcessing.PGMIO.read(file));
                C.reshape(this.dataBase.getNUMROWS() * this.dataBase.getNUMCOLS(), 1);
              
                A = A.combine(0, A.numCols(), C);
            }
        }
        M = M.divide(this.dataBase.getNUMROWS() * this.dataBase.getNUMCOLS());
        mean = M;
        M=CalculMatriciel.mean(A);
        mean = M;

        
       //soustraire l'image moyenne de A
        A =CalculMatriciel.centraliser(A, mean);
      //  ACP.getMean().printDimensions();

       // A.printDimensions();
        //calcul de la matrice covarience 
        SimpleMatrix L =CalculMatriciel.calculateCovariance(A.transpose());
        SimpleMatrix EVM=CalculMatriciel.MatVectPropre(L, k);
        
 
        //EVM.printDimensions();
        //10,304*10
        
        SimpleMatrix U = (A.mult(EVM));  //EigenFaces.
        eigenfaces = U;





        
        //displaying the eigenfaces :
        for (int i = 0; i <= this.k-1; i++) {//k number of eigen vectors
            SimpleMatrix N = U.extractVector(false, i);
            //System.out.println("EigenFace n° " + (i + 1));
            //N.print();
            N.reshape(this.dataBase.getNUMROWS() , this.dataBase.getNUMCOLS());
            //N.printDimensions();
            double[][] U2 = ImageProcessing.PGMIO.simpleToDouble(N);
            U2 =ImageProcessing.mat2gray(U2);
            ImageProcessing.writeeigens(U2, new File("eigenface"+i+".pgm"),255);
        }








        //U.printDimensions();

        //10*200 
        trainingPictures = U.transpose().mult(A);

        //Base.print();
        transform = U.transpose();

SimpleMatrix Mmatrixed=CalculMatriciel.duplicVect(M, A.numCols());
        //reconstruction of original space
        SimpleMatrix reconstructed_space = transform.pseudoInverse().mult(trainingPictures).plus(Mmatrixed);
        reconstructed_space = reconstructed_space.minus(A);
        reconstruction_space = reconstructed_space ;
    }

	public  int getK() {
		return k;
	}

	public  void setK(int k) {
		this.k = k;
	}

	public static SimpleMatrix getReconstruction_space() {
		return reconstruction_space;
	}
    
    
    
    
    
    
    
    
    
    


}
