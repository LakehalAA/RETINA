package Controllers;
import Kernel.Utils.DataBase;
import Kernel.Utils.ImageProcessing;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;

public interface converter {
	   static File convertFormat(BufferedImage inputImage, double width, double height) throws IOException, IOException {
		   	double[][] image = new double[(int) height][(int) width];
		   	double AspectRatio = height/width;
		   	BufferedImage inter = null;
		   	BufferedImage inter2 = new BufferedImage((int)width,(int)height,BufferedImage.TYPE_INT_ARGB);
		    if((inputImage.getHeight()/AspectRatio) <= inputImage.getWidth()) {
				inter = inputImage.getSubimage((int) (inputImage.getWidth() / 2 - inputImage.getHeight() / (AspectRatio * 2)), 0, (int) (inputImage.getHeight() / AspectRatio), inputImage.getHeight());
			}else{
				inter = inputImage.getSubimage(0, (int)(inputImage.getHeight() / 2 - inputImage.getWidth() * AspectRatio / 2), (int) (inputImage.getWidth()), (int) (inputImage.getWidth()*AspectRatio));
			}
	        Graphics2D g2d = inter2.createGraphics();
	        g2d.drawImage(inter,0,0,  (int) width, (int) height,null);
	        g2d.dispose();

	       for (int i = 0; i <height  ; i++) {
	           for (int j = 0; j < width ; j++) {
	               	int color = inter2.getRGB(j,i);
	                int r = (color>>16) & 0xFF;
	                int g = (color>>8) & 0xFF;
	                int b = (color) & 0xFF;

	                image[i][j] = (double)( r + g + b )/3;
	            }
	        }

	        String path="testiing"+Math.random()+".pgm";
	        File file=new File(path);
	        ImageProcessing.PGMIO.write(image, file);
	        return file;

	    }

	static void convertAddFormat(BufferedImage inputImage, double height, double width, String pathOut) throws IOException {
	   	
		double[][] image = new double[(int) height][(int) width];
		double AspectRatio = height/width;
		BufferedImage inter = null;

		if((inputImage.getHeight()/AspectRatio) <= inputImage.getWidth()) {
			inter = inputImage.getSubimage((int) (inputImage.getWidth() / 2 - inputImage.getHeight() / (AspectRatio * 2)), 0, (int) (inputImage.getHeight() / AspectRatio), inputImage.getHeight());
		}else{
			inter = inputImage.getSubimage(0, (int)(inputImage.getHeight() / 2 - inputImage.getWidth() * AspectRatio / 2), (int) (inputImage.getWidth()), (int)(inputImage.getWidth()*AspectRatio));
		}

		Graphics2D g2d = inputImage.createGraphics();
		g2d.drawImage(inter,0,0, (int) width, (int) height, null);
		g2d.dispose();
		for (int i = 0; i <height  ; i++) {
			for (int j = 0; j < width ; j++) {
				int color = inputImage.getRGB(j,i);

				int r = (color>>16) & 0xFF;
				int g = (color>>8) & 0xFF;
				int b = (color) & 0xFF;

				image[i][j] = (double)( r + g + b )/3;
			}
		}

		String path=pathOut+".pgm";
		File file=new File(path);
		ImageProcessing.PGMIO.write(image, file);

	}
	   
	   
	   
   
 /**   public static String convert(File file, String name) throws IOException {
        SimpleMatrix matrix = new SimpleMatrix(ImageProcessing.PGMIO.read(file.getAbsoluteFile()));
        double[][]matrix2 = ImageProcessing.PGMIO.simpleToDouble(matrix);
        BufferedImage image = new BufferedImage(matrix.numRows(),matrix.numCols(),BufferedImage.TYPE_INT_RGB) ;
String path="";
        try {
            for (int i = 0; i < matrix.numCols(); i++) {
                for (int j = 0; j < matrix.numCols(); j++) {
                    double a = matrix2[i][j];
                    Color newColor = new Color((int)(Math.ceil(a)), (int)(Math.ceil(a)), (int)(Math.ceil(a)));
                    image.setRGB(j, i, newColor.getRGB());
                }
            }
            path=name+".jpg";
            File output = new File(path);
            ImageIO.write(image, "jpg", output);
        } catch (
                Exception e) {
        }
        
        return path;
    }*/

   
 //this methode is more correct to use with jpg and pgn files       
	public static String convertFormat(String inputImagePath,String pathOut) throws IOException {
		
		int[] myImage =ImageProcessing.PGMIO.readToCovert(new File(inputImagePath));
		double [][] dis=ImageProcessing.PGMIO.read(new File(inputImagePath));
		int height=dis[0].length;
		int	width=dis.length;
		BufferedImage im = new BufferedImage(height,width,BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = im.getRaster();

		for(int h=0;h<dis.length;h++)
		{
		       for(int w=0;w<dis[0].length;w++)
		       {
		           raster.setSample(w,h,0, myImage[h * height + w]); 
		       }
		}

		ByteArrayOutputStream myJpg = new ByteArrayOutputStream();
		pathOut=pathOut+".jpg";
		File f=new File(pathOut);
		
		boolean r= ImageIO.write(im, "jpg", f);
		return f.getAbsolutePath();

		}


}
