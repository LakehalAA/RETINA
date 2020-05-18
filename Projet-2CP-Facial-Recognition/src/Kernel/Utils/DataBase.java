package Kernel.Utils;
import org.apache.commons.io.FileUtils;
import org.ejml.simple.SimpleMatrix;

import Controllers.C_dashboard;
import Controllers.Main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class DataBase {

    private static  int NUMBEROFTRAININGPERSONS;
    private final int NUMBEROFPICTURES;
    private static int NUMROWS;
    private static int NUMCOLS;
    private  static String PATH;
    private static int NUMBERMAXOFPRESONS;
    private static int count;
    private static  int NUMBEROFIMAGESMAXPERPERSON;//default 10
	private static int min;
    public static int getNUMBERMAXOFPRESONS() {
		return NUMBERMAXOFPRESONS;
	}
    public static int getNUMBEROFIMAGESMAXPERPERSON() {
    	return NUMBEROFIMAGESMAXPERPERSON;
    }

	public int getNUMBEROFTRAININGPERSONS() {
        return NUMBEROFTRAININGPERSONS;
    }
    

    public static void setNUMBEROFIMAGESMAXPERPERSON(int nUMBEROFIMAGESMAXPERPERSON) {
		NUMBEROFIMAGESMAXPERPERSON = nUMBEROFIMAGESMAXPERPERSON;
	}


	public int getNUMBEROFPICTURES() {
        return NUMBEROFPICTURES;
    }

    public static int getNUMROWS() {
        return NUMROWS;
    }

    public static int getNUMCOLS() {
        return NUMCOLS;
    }

    public static String getPATH() {
        return PATH;
    }

    public DataBase( int numberofpictures, String path) throws IOException {

        
        NUMBEROFPICTURES = numberofpictures;
      
        PATH = path;
        File file = new File(path + "\\s1\\1.pgm");
        
        SimpleMatrix temp = new SimpleMatrix(ImageProcessing.PGMIO.read(file));
        NUMROWS = temp.numRows();
        NUMCOLS = temp.numCols();
        
        
        File database=new File(path);
		count=0;
		File[] folderss=database.listFiles();
		
		count=0;
  		File[] files = folderss[0].listFiles(new FileFilter() {

			@Override

			public boolean accept(File pathname) {
				count++;

				return pathname.isFile();

			}
	     	});
  		NUMBEROFIMAGESMAXPERPERSON=count;
	    
  		NUMBEROFIMAGESMAXPERPERSON=count;
		count=0;
		
  	 
  		File[] folders = database.listFiles();
  		for (int i = 0; i < folders.length; i++) {
  			if (folders[i].isDirectory()) {
  				count++	;
			}
  			else {
				FileUtils.deleteQuietly(folders[i]);
			}
			
		}
  	
  		NUMBERMAXOFPRESONS=count;
  	  NUMBEROFTRAININGPERSONS=NUMBERMAXOFPRESONS;

    
}
 //this constructed is linked to the exemprimental window   
public DataBase( int numberofpictures, String path,double tauxDePersonnesConnues) throws IOException {

        
        NUMBEROFPICTURES = numberofpictures;
        
        PATH = path;
        File file = new File(path + "\\s1\\1.pgm");
        SimpleMatrix temp = new SimpleMatrix(ImageProcessing.PGMIO.read(file));
        NUMROWS = temp.numRows();
        NUMCOLS = temp.numCols();
        
        
        File database=new File(path);
        
        
        count=0;
		File[] folderss=database.listFiles();
		
		count=0;
			count=0;
			File[] files = folderss[0].listFiles(new FileFilter() {

				@Override

				public boolean accept(File pathname) {
					count++;
                  // System.out.println(pathname.getAbsolutePath());
					return pathname.isFile();

				}
				
			});
	
  		NUMBEROFIMAGESMAXPERPERSON=count;
		count=0;
		
  	 
  		File[] folders = database.listFiles();
  		for (int i = 0; i < folders.length; i++) {
  			if (folders[i].isDirectory()) {
  				count++	;
			}
  			else {
				FileUtils.deleteQuietly(folders[i]);
			}
			
		}
  	
  		NUMBERMAXOFPRESONS=count;
  		NUMBEROFTRAININGPERSONS=count;
  		NUMBEROFTRAININGPERSONS=(int) (NUMBERMAXOFPRESONS*tauxDePersonnesConnues/100);//we take 75% as known while others unkown for calculating rates

    
}



}
