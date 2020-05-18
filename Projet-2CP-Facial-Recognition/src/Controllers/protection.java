package Controllers;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;


import Kernel.Reconnaissance.RecognitionSystem;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.util.Zip4jUtil;
import org.apache.commons.io.FileUtils;

public interface protection {

    private static void clearBuffer (){
        File[] files = new File("ProtectionBuffer").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        for (File file:files) {
            file.delete();
        }
    }

    private static void clearBuffer2 (){
        File[] files = new File("FormattingBuffer").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        for (File file:files) {
            file.delete();
        }
    }

    static void copyFromBufferTo (String path) throws IOException {
        File[] files = new File("ProtectionBuffer").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        for (File file:files) {
            FileUtils.copyFile(file, new File(path + "/" + file.getName()));
        }
    }

    static void copyToBuffer (String path) throws IOException {
        clearBuffer();
        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        for (File file:files) {
            FileUtils.copyFile(file, new File("ProtectionBuffer/" + file.getName()));
        }
    }

    private static void copyToBuffer2 (String path) throws IOException {
        clearBuffer2();
        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });

        for (File file:files) {
            FileUtils.copyFile(file, new File("FormattingBuffer/" + file.getName()));
        }
    }

    static void Riguel( String path ) throws IOException {

        if(!path.equals("ProtectionBuffer")){
            clearBuffer();
            clearBuffer2();

            File[] files = new File(path).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
            });

            int i = 1;
            for (File file : files) {
                converter.convertAddFormat(ImageIO.read(file), 112, 98, "ProtectionBuffer/" + i);
                i++;
            }
        }
        else{
            copyToBuffer2("ProtectionBuffer");
            clearBuffer();
            String path1="FormattingBuffer";
            File[] files = new File(path1).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !pathname.isDirectory();
                }
            });

            //System.out.print(files.length);

            int i = 1;
            for (File file : files) {
                converter.convertAddFormat(ImageIO.read(file), RecognitionSystem.getDatabase().getNUMROWS(), RecognitionSystem.getDatabase().getNUMCOLS(), "ProtectionBuffer/" + i);
                i++;
            }
            clearBuffer2();
        }
    }


    //This is the method to use while adding people to the ORL, it renames, formats and redimensions the pics of a person and adds them to the convenient folder in the ORL
    static void safeAddORL(String pathPerson, String pathORL) throws IOException {
        Riguel(pathPerson);

        File[] files = new File(pathORL).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        copyFromBufferTo(pathORL + "/" + "s" + String.valueOf(files.length+1));
      //  DataBase.setNUMBERMAXOFPRESONS(DataBase.getNUMBERMAXOFPRESONS() + 1);
    }

    static void main(String[] args) throws IOException {

    }

    //A method used to export into a password secured .zip file
    public static void hash(String source, String destination, String password) throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        ZipFile zipfile = new ZipFile(destination,password.toCharArray());
        zipfile.addFolder(new File(source),zipParameters);

    }
}
