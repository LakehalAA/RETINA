package Kernel.Utils;


import java.util.ArrayList;
import java.util.Iterator;

import org.ejml.data.Complex_F64;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;


public interface CalculMatriciel {

	
	public static SimpleMatrix mean(SimpleMatrix matrixList)
	
	{
		  SimpleMatrix tempMean = new SimpleMatrix(0, 0);
	        SimpleMatrix tempMeanAdjusted = new SimpleMatrix(0, 0);
	       
	        for (int i = 0; i < matrixList.numCols(); i++)  {
	            SimpleMatrix tempImage = matrixList.extractVector(false, i);
	            try {
	               tempMean=tempMean.plus(tempImage);

	            } catch (Exception e) {
	                tempMean = tempImage;
	            }
	        }
	         tempMean=tempMean.divide(matrixList.numCols());
	         return tempMean;
	}
	
	
	
	public static SimpleMatrix calculateCovariance(SimpleMatrix A ) {
		SimpleMatrix covarianceMatrix = A.mult(A.transpose());
		return covarianceMatrix;
	}
// a utilser pour retourner MeanAdjustedMatrix
	public static SimpleMatrix centraliser(SimpleMatrix A ,SimpleMatrix mean)
	{
		 SimpleMatrix Mmatrixed = new SimpleMatrix(A.numRows(), 0);
	        for (int i = 0; i < A.numCols(); i++) {
	            Mmatrixed = Mmatrixed.combine(0, Mmatrixed.numCols(), mean);

	        }
			return A.minus(Mmatrixed);
	}
	public static SimpleMatrix MatVectPropre(SimpleMatrix A,int dimension )//n:NUMBEROFPERSONS * NUMBEROFPICTURES
	{
		SimpleEVD eigValD = A.eig();
		ArrayList<Complex_F64> eigenValues = (ArrayList<Complex_F64>) eigValD.getEigenvalues();
		SimpleMatrix EVM = new SimpleMatrix(0, 0);
		for (int i = 0; i < dimension; i++) {
			EVM = EVM.combine(0, EVM.numCols(), (SimpleMatrix) eigValD.getEigenVector(i));
		}
		return EVM;
		
		
	}
	public static  SimpleMatrix duplicVect(SimpleMatrix vect,int times)
	{
		SimpleMatrix dup=new SimpleMatrix(0,0);
		for (int i = 0; i < times; i++) {
			dup = dup.combine(0, dup.numCols(), vect );
		}
		return dup;
		
		
	}
	
}
