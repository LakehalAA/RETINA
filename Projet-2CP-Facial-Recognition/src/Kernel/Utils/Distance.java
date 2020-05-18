package Kernel.Utils;

import org.ejml.simple.SimpleMatrix;

public interface Distance {

	static SimpleMatrix disEuclidienne(SimpleMatrix Base ,SimpleMatrix test)
	{
		SimpleMatrix testMatrix = new SimpleMatrix(0, 0);
        for( int i=0 ; i<Base.numCols() ; i++) {
            testMatrix = testMatrix.combine(0, testMatrix.numCols(), test);
        }


        SimpleMatrix difference = Base.minus(testMatrix);

        SimpleMatrix differenceProduct = difference.transpose().mult(difference);

        int returnedProfile = 0;
        
        SimpleMatrix diags = differenceProduct.diag();
        for (int j = 0; j < diags.numRows(); j++) {

            diags.set(j, Math.sqrt(diags.get(j)));
        }
		return diags;
		
	}
/**	static SimpleMatrix disMahalanobis(SimpleMatrix Base ,SimpleMatrix test)
	{
		SimpleMatrix distances=new SimpleMatrix(Base.numCols(), 1);
		for (int i = 0; i < Base.numCols(); i++) {
			double value=distancemahala(Base.extractVector(false, i),test,Base);
			distances.set(i, 0, value);
		}
		
		return distances;
		
	}*/
	
	
	 public static double distancemahala(SimpleMatrix V1,SimpleMatrix V2, SimpleMatrix Base){
	        /*parameters : V1 , V2, vectors with same dimension
	                     Base, Eigenfaces matrix
	          returns the Mahalanobis distance between V1 and V2
	        */

	        SimpleMatrix Covariance = covarianceeignfaces(Base);
	        SimpleMatrix v1minusv2 =V1.minus(V2);
	     /**   System.out.println("(v1minusv2).transpose().mult(Covariance.invert()).mult(v1minusv2)");
	        ((v1minusv2).transpose().mult(Covariance.invert()).mult(v1minusv2)).print();
	        ((v1minusv2).transpose().mult(Covariance.invert()).mult(v1minusv2)).printDimensions();*/

	        
	        return Math.sqrt(((v1minusv2).transpose().mult(Covariance.invert()).mult(v1minusv2)).get(0,0));
	        
	    }
	 
	 
	 
	 
	 
	 
	 
	 public static SimpleMatrix distanceMahalanobis(SimpleMatrix test, SimpleMatrix Base){//optimized
	        /*parameters : V1 , V2, vectors with same dimension
	                     Base, Eigenfaces matrix
	          returns the Mahalanobis distance between V1 and V2
	        */
          SimpleMatrix ret=new SimpleMatrix(Base.numCols(), 1);
	        SimpleMatrix Covariance = covarianceeignfaces(Base);
	        SimpleMatrix testMatrix = new SimpleMatrix(0, 0);
	        for( int i=0 ; i<Base.numCols() ; i++) {
	            testMatrix = testMatrix.combine(0, testMatrix.numCols(), test);
	        }

           //  Base.printDimensions();
          //   testMatrix.printDimensions();
	        SimpleMatrix difference = Base.minus(testMatrix);
	        	 SimpleMatrix res=(difference).transpose().mult(Covariance.invert()).mult(difference);
	     //   ((difference).transpose().mult(Covariance.invert()).mult(difference)).print();
            SimpleMatrix distances=res.diag();
           
  
	        for (int i = 0; i < distances.numRows(); i++) {
		        double rett= Math.sqrt((distances.get(i,0)));
		        distances.set(i, 0, rett);

			}
	     
	        return distances;
	        
	    }


	    private static SimpleMatrix covarianceeignfaces(SimpleMatrix Base){

	        /*parameters : Base, Eigenfaces matrix
	          returns the Covariance matrix of the Eigenfaces
	        */

	        SimpleMatrix X;

	        if (Base.numCols()>Base.numRows()){
	            X = Base.transpose();
	        }else{
	            X = Base;
	        }

	        int n = X.numRows();
	        SimpleMatrix Xt = X.transpose();
	        int m = Xt.numRows();

	        // Means:
	        SimpleMatrix x = new SimpleMatrix(m, 1);
	        for(int r=0; r<m; r++ ){
	            x.set(r, 0, Xt.extractVector(true, r).elementSum() / n);
	        }

	        // Covariance matrix:
	        SimpleMatrix S = new SimpleMatrix(m, m);
	        for(int r=0; r<m; r++){
	            for(int c=0; c<m; c++){
	                if(r > c){
	                    S.set(r, c, S.get(c, r));
	                } else {
	                    double cov = Xt.extractVector(true, r).minus( x.get((r), 0) ).dot(Xt.extractVector(true, c).minus( x.get((c), 0) ).transpose());
	                    S.set(r, c, (cov / n));
	                }
	            }
	        }

	        return S;
	    }
	    
	    
	    public static double dis_manhattan(SimpleMatrix mat1,SimpleMatrix mat2){
	        double inter = 0 ;
	        for (int i = 0; i <mat1.numRows() ; i++) {
	            inter += Math.abs(mat1.get(i) - mat2.get(i));
	        }
	        return inter;
	    }
	    
	    public static SimpleMatrix disManhattan(SimpleMatrix test,SimpleMatrix Base)
	    {
	    	SimpleMatrix distance=new SimpleMatrix(Base.numCols(),1);
	    	for (int i = 0; i <Base.numCols(); i++) {
	    		distance.set(i,0, dis_manhattan(Base.extractVector(false, i), test));
				
			}
	    	
			return distance;
	    	
	    	
	    	
	    	
	    	
	    }
	
	    
	    
}
