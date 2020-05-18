package Kernel.Utils;
import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;

public final class ImageProcessing {
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
public static void convert(File file, String name) throws IOException {
    SimpleMatrix matrix = new SimpleMatrix(PGMIO.read(file.getAbsoluteFile()));
    double[][]matrix2 = PGMIO.simpleToDouble(matrix);
    try {
        BufferedImage image = new BufferedImage(matrix.numRows(),matrix.numCols(),BufferedImage.TYPE_INT_RGB) ;
        for (int i = 0; i < matrix.numCols(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                double a = matrix2[i][j];
                Color newColor = new Color((int)(Math.ceil(a)), (int)(Math.ceil(a)), (int)(Math.ceil(a)));
                image.setRGB(j, i, newColor.getRGB());
            }
        }
        File output = new File(name+".jpg");
        ImageIO.write(image, "jpg", output);
    } catch (
            Exception e) {
    }
}



    public static void convert(SimpleMatrix binary, String name) throws IOException {
        try {
            BufferedImage image = new BufferedImage(binary.numCols(),binary.numRows(),BufferedImage.TYPE_INT_RGB);
            for(int i=0; i<binary.numRows(); i++) {
                for(int j=0; j<binary.numCols(); j++) {
                    int a = (int) binary.get(i,j);
                    Color newColor = new Color(a,a,a);
                    image.setRGB(j,i,newColor.getRGB());
                }
            }
            File output = new File(name+".jpg");
            ImageIO.write(image, "jpg", output);
        }
        catch (Exception e){
        }
    }
    public static double[][] mat2gray(double[][] matrix2) {
        double min = 0.0D;
        double max = 0.0D;
        double[][] matrix = new double[matrix2.length][matrix2[0].length];
        matrix = matrix2;

        int i;
        int j;
        for(i = 0; i < matrix.length; ++i) {
            for(j = 0; j < matrix[0].length; ++j) {
                if (matrix[i][j] < min) {
                    min = matrix[i][j];
                }
            }
        }

        for(i = 0; i < matrix.length; ++i) {
            for(j = 0; j < matrix[0].length; ++j) {
                matrix[i][j] += Math.abs(min);
            }
        }

        for(i = 0; i < matrix.length; ++i) {
            for(j = 0; j < matrix[0].length; ++j) {
                if (matrix[i][j] > max) {
                    max = matrix[i][j];
                }
            }
        }

        for(i = 0; i < matrix.length; ++i) {
            for(j = 0; j < matrix[0].length; ++j) {
                matrix[i][j] = Math.ceil(matrix[i][j] / max * 255.0D);
            }
        }

        return matrix;
    }
    public static void writeeigens(double[][] image, File file, int maxval) throws IOException {
        if (maxval > 255) {
            throw new IllegalArgumentException("The maximum gray value cannot exceed 255.");
        } else {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));

            try {
                stream.write("P5".getBytes());
                stream.write("\n".getBytes());
                stream.write(Integer.toString(image[0].length).getBytes());
                stream.write(" ".getBytes());
                stream.write(Integer.toString(image.length).getBytes());
                stream.write("\n".getBytes());
                stream.write(Integer.toString(maxval).getBytes());
                stream.write("\n".getBytes());

                for(int i = 0; i < image.length; ++i) {
                    for(int j = 0; j < image[0].length; ++j) {
                        double p = image[i][j];
                        if (p > (double)maxval) {
                            throw new IOException("Pixel value " + p + " outside of range [0, " + maxval + "].");
                        }

                        stream.write((int)(image[i][j]));
                    }
                }
            } finally {
                stream.close();
            }

        }
    }

  

    public static final class PGMIO {
    	
    	
    	
    	 public static int[] readToCovert(File file) throws IOException {
    	        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

    	        double[][] var12;
    	        int image1d[];
    	        try {
    	            if (!next(stream).equals("P5")) {
    	                throw new IOException("File " + file + " is not a binary PGM image.");
    	            }

    	            int col = Integer.parseInt(next(stream));
    	            int row = Integer.parseInt(next(stream));
    	            int max = Integer.parseInt(next(stream));
    	            if (max < 0 || max > 255) {
    	                throw new IOException("The image's maximum gray value must be in range [0, 255].");
    	            }

    	            double[][] image = new double[row][col];
    	              image1d=new int[row*col];
    	int k=0;
    	            for(int i = 0; i < row; ++i) {
    	                for(int j = 0; j < col; ++j) {
    	                    int p = stream.read();
    	                    if (p == -1) {
    	                        throw new IOException("Reached end-of-file prematurely.");
    	                    }

    	                    if (p < 0 || p > max) {
    	                        throw new IOException("Pixel value " + p + " outside of range [0, " + max + "].");
    	                    }

    	                    image[i][j] = (double)p;
    	                    
    	                    image1d[k]=p;
    	                    k++;
    	                    
    	                }
    	            }
    	 

    	            var12 = image;
    	        } finally {
    	            stream.close();
    	        }

    	        return image1d;
    	    }


        private static final String MAGIC = "P5";
        private static final char COMMENT = '#';
        private static final int MAXVAL = 255;

        private PGMIO() {
        }
        public static double[][] simpleToDouble(SimpleMatrix mat) {
            double[][] matrixDouble = new double[mat.getMatrix().getNumRows()][mat.getMatrix().getNumCols()];

            for(int i = 0; i < mat.getMatrix().getNumRows(); ++i) {
                for(int j = 0; j < mat.getMatrix().getNumCols(); ++j) {
                    matrixDouble[i][j] = mat.get(i, j);
                }
            }

            return matrixDouble;
        }

        public static double[][] read(File file) throws IOException {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

            double[][] var12;
            try {
                if (!next(stream).equals("P5")) {
                    throw new IOException("File " + file + " is not a binary PGM image.");
                }

                int col = Integer.parseInt(next(stream));
                int row = Integer.parseInt(next(stream));
                int max = Integer.parseInt(next(stream));
                if (max < 0 || max > 255) {
                    throw new IOException("The image's maximum gray value must be in range [0, 255].");
                }

                double[][] image = new double[row][col];

                for(int i = 0; i < row; ++i) {
                    for(int j = 0; j < col; ++j) {
                        int p = stream.read();
                        if (p == -1) {
                            throw new IOException("Reached end-of-file prematurely.");
                        }

                        if (p < 0 || p > max) {
                            throw new IOException("Pixel value " + p + " outside of range [0, " + max + "].");
                        }

                        image[i][j] = (double)p;
                    }
                }

                var12 = image;
            } finally {
                stream.close();
            }

            return var12;
        }

        private static String next(InputStream stream) throws IOException {
            ArrayList bytes = new ArrayList();

            while(true) {
                int b = stream.read();
                if (b == -1) {
                    break;
                }

                char c = (char)b;
                if (c == '#') {
                    while(true) {
                        int d = stream.read();
                        if (d == -1 || d == 10 || d == 13) {
                            break;
                        }
                    }
                } else if (!Character.isWhitespace(c)) {
                    bytes.add((byte)b);
                } else if (bytes.size() > 0) {
                    break;
                }
            }

            byte[] bytesArray = new byte[bytes.size()];

            for(int i = 0; i < bytesArray.length; ++i) {
                bytesArray[i] = (Byte)bytes.get(i);
            }

            return new String(bytesArray);
        }

        public static void write(double[][] image, File file) throws IOException {
            write(image, file, 255);
        }

        public static void write(double[][] image, File file, int maxval) throws IOException {
            if (maxval > 255) {
                throw new IllegalArgumentException("The maximum gray value cannot exceed 255.");
            } else {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));

                try {
                    stream.write("P5".getBytes());
                    stream.write("\n".getBytes());
                    stream.write(Integer.toString(image[0].length).getBytes());
                    stream.write(" ".getBytes());
                    stream.write(Integer.toString(image.length).getBytes());
                    stream.write("\n".getBytes());
                    stream.write(Integer.toString(maxval).getBytes());
                    stream.write("\n".getBytes());

                    for(int i = 0; i < image.length; ++i) {
                        for(int j = 0; j < image[0].length; ++j) {
                            double p = image[i][j];
                            if (p < 0.0D || p > (double)maxval) {
                                throw new IOException("Pixel value " + p + " outside of range [0, " + maxval + "].");
                            }

                            stream.write((int)image[i][j]);
                        }
                    }
                } finally {
                    stream.close();
                }

            }
        }
        public static void writetoReconstruct(double[][] image, File file) throws IOException {
        	writetoRecunstruct(image, file,255);
        }
        
        public static void writetoRecunstruct(double[][] image, File file, int maxval) throws IOException {
       	 if (maxval > 255) {
                throw new IllegalArgumentException("The maximum gray value cannot exceed 255.");
            } else {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));

                try {
                    stream.write("P5".getBytes());
                    stream.write("\n".getBytes());
                    stream.write(Integer.toString(image[0].length).getBytes());
                    stream.write(" ".getBytes());
                    stream.write(Integer.toString(image.length).getBytes());
                    stream.write("\n".getBytes());
                    stream.write(Integer.toString(maxval).getBytes());
                    stream.write("\n".getBytes());

                    for(int i = 0; i < image.length; ++i) {
                        for(int j = 0; j < image[0].length; ++j) {
                            double p = image[i][j];
                            if (p > (double)maxval) {
                                image[i][j]=255;
                            }

                            stream.write((int)(255-image[i][j]));
                        }
                    }
                } finally {
                    stream.close();
                }

            }
        }
    }
  


        public static double[][] matrixToArray(SimpleMatrix mat) {
            double[][] matrixDouble = new double[mat.getMatrix().getNumRows()][mat.getMatrix().getNumCols()];

            for(int i = 0; i < mat.getMatrix().getNumRows(); ++i) {
                for(int j = 0; j < mat.getMatrix().getNumCols(); ++j) {
                    matrixDouble[i][j] = mat.get(i, j);
                }
            }

            return matrixDouble;
        }

        public static double[][] matrixToGray(double[][] matrix2) {
            double min = 0.0D;
            double max = 0.0D;
            double[][] matrix = new double[matrix2.length][matrix2[0].length];
            matrix = matrix2;

            int i;
            int j;
            for(i = 0; i < matrix.length; ++i) {
                for(j = 0; j < matrix[0].length; ++j) {
                    if (matrix[i][j] < min) {
                        min = matrix[i][j];
                    }
                }
            }

            for(i = 0; i < matrix.length; ++i) {
                for(j = 0; j < matrix[0].length; ++j) {
                    matrix[i][j] += Math.abs(min);
                }
            }

            for(i = 0; i < matrix.length; ++i) {
                for(j = 0; j < matrix[0].length; ++j) {
                    if (matrix[i][j] > max) {
                        max = matrix[i][j];
                    }
                }
            }

            for(i = 0; i < matrix.length; ++i) {
                for(j = 0; j < matrix[0].length; ++j) {
                    matrix[i][j] = Math.ceil(matrix[i][j] / max * 255.0D);
                }
            }

            return matrix;
        }

   
    
    public static SimpleMatrix projectToFaceSpace(SimpleMatrix input,SimpleMatrix meanImage,SimpleMatrix transform){
        SimpleMatrix res = input;
        res.reshape(meanImage.numRows(),meanImage.numCols());
        res=res.minus(meanImage);
        return transform.mult(res);
    }
}

