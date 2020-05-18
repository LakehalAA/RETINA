package Kernel.FaceDetector;


import Controllers.converter;
import Kernel.Reconnaissance.RecognitionSystem;
import Kernel.Utils.ImageProcessing;
import org.apache.commons.io.FileUtils;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.rectangle;


public class FaceDetector {

    private File image;
    private SimpleMatrix reproduced_rgb;
    private SimpleMatrix rgb_matrix;
    private SimpleMatrix ycbcr_space;
    private SimpleMatrix binary_image;
    private SimpleMatrix Crs;
    private SimpleMatrix Cbs;
    private SimpleMatrix Ys;
    private SimpleMatrix Hue;
    private SimpleMatrix Saturation;
    private double Cr = 0;
    private int NUMROWS = 0;
    private int NUMCOLS = 0;
    private static SimpleMatrix m1 = new SimpleMatrix(3, 1);
    private static SimpleMatrix m2 = new SimpleMatrix(3, 3);
    private SimpleMatrix m3 = new SimpleMatrix(3, 1);
    private int true_set;
    private ArrayList<Pointy> face_pixels;
    private BufferedImage bufImg;
    private File extraction_jpg;
    private File extraction_pgm;
    private File filter;
    private boolean nature;
    private File extraction;


    //Getters & Setters
    public SimpleMatrix getBinary_image() {
        return binary_image;
    }

    public SimpleMatrix getReproduced_rgb() {
        return reproduced_rgb;
    }

    public BufferedImage getBufImg() {
        return bufImg;
    }

    public File getExtraction_jpg() {
        return extraction_jpg;
    }

    public File getExtraction_pgm() {
        return extraction_pgm;
    }

    public File getFilter() {
        return filter;
    }

    public FaceDetector(File image) throws IOException {
        this.image = image;
    }

    //Lecture de l'image et initialisation
    public void initiator() throws IOException {
        m1.setColumn(0, 0, 16, 128, 128);
        m2.setColumn(0, 0, 0.299, -0.168736, 0.5);
        m2.setColumn(1, 0, 0.587, -0.331264, -0.418688);
        m2.setColumn(2, 0, 0.114, 0.5, -0.081312);

        BufferedImage bi = ImageIO.read(image);
        bufImg = bi;
        rgb_matrix = new SimpleMatrix(bi.getHeight(), bi.getWidth());
        NUMROWS = bi.getHeight();
        NUMCOLS = bi.getWidth();
        binary_image = new SimpleMatrix(NUMROWS, NUMCOLS);
        Crs = new SimpleMatrix(NUMROWS, NUMCOLS);
        Cbs = new SimpleMatrix(NUMROWS, NUMCOLS);
        Ys = new SimpleMatrix(NUMROWS, NUMCOLS);
        Hue = new SimpleMatrix(NUMROWS,NUMCOLS);
        Saturation = new SimpleMatrix(NUMROWS,NUMCOLS);
        ycbcr_space = new SimpleMatrix(3, 1);
        ycbcr_space.set(0, 0, 0);
        ycbcr_space.set(1, 0, 0);
        SimpleMatrix rgb = new SimpleMatrix(3, 1);
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) {
                Color color = new Color(bi.getRGB(j, i));
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),null);
                rgb_matrix.set(i, j, color.getRed() / 3 + color.getGreen() / 3 + color.getBlue() / 3);
                rgb.set(0, 0, color.getRed());
                rgb.set(1, 0, color.getGreen());
                rgb.set(2, 0, color.getBlue());
                m3 = m1.plus((m2.mult(rgb)));
                ycbcr_space.set(2, 0, m3.get(2, 0));
                Crs.set(i, j, m3.get(2, 0));
                Cbs.set(i, j, m3.get(1, 0));
                Ys.set(i, j, m3.get(0, 0));
                Hue.set(i,j,hsb[0]);
                Saturation.set(i,j,hsb[1]);
                Cr = Cr + m3.get(2, 0);
            }
        }
        Cr = Cr / (NUMCOLS * NUMROWS);
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) {
                if ((Ys.get(i, j) >= 80) && (Cbs.get(i, j) >= 85) && (Cbs.get(i, j) <= 135) && (Crs.get(i, j) >= 135) && (Crs.get(i, j) <= 180) && (Hue.get(i,j)>=0) && (Hue.get(i,j)<=50) && (Saturation.get(i,j)>=0.23) && (Saturation.get(i,j)<=0.68))
                    binary_image.set(i, j, 255);
                else binary_image.set(i, j, 0);
            }
        }
    }


    //Detecting and clustering all white regions
    public void FloodFilling() {

        SimpleMatrix treated = new SimpleMatrix(NUMROWS, NUMCOLS);
        treated.zero();
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) {
                if (binary_image.get(i, j) == 255) treated.set(i, j, 1);
            }
        }
        ArrayList<ArrayList> groups = new ArrayList<>();

        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) {

                if ((binary_image.get(i, j) == 255) && (treated.get(i, j) == 1)) {

                    Queue<Point> queue = new LinkedList<Point>();
                    queue.add(new Point(j, i));
                    ArrayList<Pointy> group = new ArrayList<>();
                    group.add(new Pointy(i, j));
                    int pixelCount = 0;
                    while (!queue.isEmpty()) {
                        Point p = queue.remove();

                        if ((p.x >= 0)
                                && (p.x < NUMCOLS && (p.y >= 0) && (p.y < NUMROWS))) {
                            if ((treated.get((int)p.y,(int) p.x) == 1) && (binary_image.get((int)p.y,(int) p.x) == 255)) {
                                treated.set((int)p.y,(int) p.x, 0);
                                pixelCount++;
                                group.add(new Pointy(p.y, p.x));
                                queue.add(new Point(p.x + 1, p.y));
                                queue.add(new Point(p.x - 1, p.y));
                                queue.add(new Point(p.x, p.y + 1));
                                queue.add(new Point(p.x, p.y - 1));
                            }
                        }
                    }
                    groups.add(group);
                }

            }
        }

        int face_index = 0;
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).size() >= groups.get(face_index).size()) face_index = i;
        }

    }

    //Applying the binary mask to the original image
    public void smoothing() {
        SimpleMatrix binary = new SimpleMatrix(NUMROWS, NUMCOLS);
        binary.zero();
        for (int k = 0; k < face_pixels.size(); k++) {
            binary.set((int) face_pixels.get(k).getX(), (int) face_pixels.get(k).getY(), 255);
        }
        binary_image = binary;
        reproduced_rgb = rgb_matrix;
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) {
                if (binary.get(i, j) == 0) reproduced_rgb.set(i, j, 0);
            }
        }
    }

    //Finding the smallest polygone and bounding rectangle to decide upon the skin region nature
    public boolean nature_detector() {
        List<Point> points = new ArrayList<Point>();
        boolean face = false;
        for (int k = 0; k < face_pixels.size(); k++) {
            points.add(new Point(face_pixels.get(k).getX(),face_pixels.get(k).getY()));
        }

        Polygon hull = new Polygon(ConvexHull.makeHull(points));
        Rectangle rect = Caliper.boundingBox(hull);

        double height = rect.getHeight();
        double width = rect.getWidth();
        double perimeter = 2 * (width + height);
        double area = face_pixels.size();
        double compactness = (area / Math.pow(perimeter, 2));
        double solidity = (area / (height * width));
        double orientation = width / height;
        if ((compactness > 0.025) && (solidity > 0.5218) && (orientation > 0.9) && (orientation < 2.1)) {
            face = true;
        }

        if (face == false) reproduced_rgb.zero();
        return face;

    }

    public File extracting(String source){
        Mat cropImage;
        int x = 0,y = 0,height = 0,width = 0;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
       // System.out.println("\nRunning FaceDetector");


        
        // Instantiating the CascadeClassifier
        String xmlFile = ".\\libraries\\opencv_java\\fxml\\lbpcascade_frontalface.xml";
      //  CascadeClassifier classifier = new CascadeClassifier(xmlFile);
        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load(xmlFile);
        
        Mat image = imread(source);

        MatOfRect face_Detections = new MatOfRect();
        faceDetector.detectMultiScale(image, face_Detections);
       // System.out.println(String.format("Detected %s faces",  face_Detections.toArray().length));
        Rect rect_Crop=null;
        Rect rectCrop = null;
        for (Rect rect : face_Detections.toArray()) {
            rectangle(image, new org.opencv.core.Point(rect.x, rect.y), new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
           rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);


        }

        if(face_Detections.toArray().length!=0) this.nature=true;
        else this.nature=false;

        Mat image_roi = new Mat(image,rectCrop);
        Imgcodecs.imwrite("temp_extract.jpg",image_roi);
        return (new File("temp_extract.jpg"));
    }




    public boolean detectFace() throws IOException {
        FaceDetector fd = new FaceDetector(this.image);
       // System.out.println("Flood filling process ...");
        //System.out.println("Region smoothing process ...");
        extraction = new File(String.valueOf(extracting(this.image.getAbsolutePath())));
        BufferedImage bf = ImageIO.read(extraction);
        extraction_pgm = converter.convertFormat(bf, bf.getWidth(),bf.getHeight());


        extraction_jpg = new File(converter.convertFormat(extraction_pgm.getAbsolutePath(), "extracted"));
        double add = Math.random();
        FileUtils.copyFile(extraction,new File("extractions/extractions"+add+".jpg"));
        FileUtils.deleteQuietly(new File("temp_extract.jpg"));
        return this.nature;
    }

   /* public static void main(String args[]) throws IOException {
        FaceDetector fd = new FaceDetector(new File("C:\\Users\\DELL-10\\Desktop\\STARKUS\\Integration_28_04_2020\\Utilities\\att_faces-orl\\s4\\6.pgm"));
        fd.detectFace();

    }*/


}