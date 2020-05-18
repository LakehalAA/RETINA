package Controllers;

import Kernel.Reconnaissance.RecognitionSystem;
import Kernel.Utils.DataBase;
import ProgressBar.RingProgressIndicator;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class C_dashboard implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    private int NbrePersonne = 0;

    @FXML
    private TabPane tabs;

    @FXML
    private Tab history;

    @FXML
    private ImageView imageview;
    private File database = null;

    @FXML
    private ListView<HBox> listView;

    @FXML
    private Hyperlink importPic;

    @FXML
    private Button takePic;

    @FXML
    private Tab stats;

    @FXML
    private Pane chart;

    @FXML
    private Button export;

    @FXML
    private Tab dashboard;
    private static boolean retrain=false;

    public static void setRetrain(boolean retrain) {
        C_dashboard.retrain = retrain;
    }

    ObservableList<HBox> filesList;

    @FXML
    private ButtonBase importPerson;

    @FXML
    private Button back;

    @FXML
    private Button batchadd;

    @FXML
    private Button changeORL;

    private DataBase db=RecognitionSystem.getDatabase();//database
    private int nbreOfPersonIndatabase= db.getNUMBERMAXOFPRESONS();
    private String dataSet= db.getPATH();

    // Event Listener on Button[#batchadd].onAction
    @FXML
    public void batchAdd(ActionEvent actionEvent) throws IOException, InterruptedException {
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
        ringProgressIndicator.setVisible(true);
        ringProgressIndicator.makeIndeterminate();
        Scene scene = new Scene(ringProgressIndicator);
        dialogStage.setScene(scene);
        dialogStage.show();
        JFileChooser chooser = new JFileChooser();
        FileChooser choose=new FileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setCurrentDirectory(new File("user.home"));
        chooser.setDialogTitle("multi-sélection");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(null);

        File[] files1= chooser.getSelectedFiles();

        File[] listOFnewFiles=new File[files1.length];
        for (int i = 0; i <files1.length; i++) {
            try {
                nbreOfPersonIndatabase++;
                listOFnewFiles[i]= new File(db.getPATH()+"//s" +nbreOfPersonIndatabase);
                FileUtils.copyDirectory(new File(files1[i].getAbsolutePath()),listOFnewFiles[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //renommer les images :)
        for (int i = 0; i < listOFnewFiles.length; i++) {
            File file2 = new File(listOFnewFiles[i].getAbsolutePath());
            File[] images1 = file2.listFiles();

            try {
                for (int j = 0; j < images1.length; j++) {
                    if (images1[j].isFile()) {
                        images1[j].renameTo(new File(file2.getAbsoluteFile() + "\\" + (j + 1) + ".pgm"));
                    }
                }

            } catch (Exception e) {
                file2.getName();
            }

        }
        retrain=true;

        File[] fFolders = new File(db.getPATH()).listFiles(File::isDirectory);
        updateList(fFolders);
        dialogStage.close();
    }

    // Event Listener on Button[#changeORL].onAction
    @FXML
    public void changeORL(ActionEvent e) throws IOException, InterruptedException {

        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
        ringProgressIndicator.setVisible(true);
        ringProgressIndicator.makeIndeterminate();
        Scene scene = new Scene(ringProgressIndicator);
        dialogStage.setScene(scene);
        dialogStage.show();

        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser.setTitle("Choisissez une ORL à manipuler");
        File Set = directoryChooser.showDialog(stage);
        dialogStage.close();
        dataSet = Set.getAbsolutePath();

        //NbrePersonne = fFolders.length;
        String path="./temp";
        FileUtils.deleteDirectory(new File(path));
        FileUtils.forceMkdir(new File("./temp"));
        File f=new File(path);
        FileUtils.copyDirectory(new File(dataSet),new File(f.getAbsolutePath()));
        db=new DataBase(5,path);
        System.out.println(DataBase.getNUMBERMAXOFPRESONS());
        // reorganize("./temp");
        File[] fFolders = new File(db.getPATH()).listFiles(File::isDirectory);
        assert fFolders != null;
        updateList(fFolders);
        retrain=true;

    }

    void run() throws IOException {

        Image new1 = new Image(new File("reconstruct.jpg").toURI().toString());
        Image new2 = new Image(new File("close_match.jpg").toURI().toString());

    }

    public void takePict(ActionEvent event) throws IOException {

        sceneManager.showScene(sceneManager.getScene("addPerson"));

    }

    /*** adding a person code *********
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * @param a
     * @throws IOException
     */

    public void addPerson(ActionEvent a) throws IOException {


        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
        ringProgressIndicator.setVisible(true);
        ringProgressIndicator.makeIndeterminate();
        Scene scene = new Scene(ringProgressIndicator);
        dialogStage.setScene(scene);
        dialogStage.show();

        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser.setTitle("Choisissez une personne à rajouter");
        File pickedPerson = directoryChooser.showDialog(stage);

        try {

            if(pickedPerson!=null) {
                NbrePersonne++;
                int numberofnewFile=this.nbreOfPersonIndatabase+1 ;
                FileUtils.copyDirectory(pickedPerson, new File(RecognitionSystem.getDatabase().getPATH()+"\\s"+numberofnewFile));


                File[] folders = database.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                nbreOfPersonIndatabase++;
                retrain=true;
                updateList(folders);
            }
        } catch (IOException e) {
            dialogStage.close();
            e.printStackTrace();
        }
        dialogStage.close();

    }



    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub

        assert tabs != null : "fx:id=\"tabs\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert dashboard != null : "fx:id=\"history\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert importPic != null : "fx:id=\"importPic\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert takePic != null : "fx:id=\"takePic\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert stats != null : "fx:id=\"stats\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";
        assert chart != null : "fx:id=\"chart\" was not injected: check your FXML file 'Admin_Dashboard.fxml'.";


        database = new File(DataBase.getPATH());
        takePic.setTooltip(new Tooltip("Ajouter une personne ici"));
        batchadd.setTooltip(new Tooltip("Ajouter plusieurs personnes"));
        changeORL.setTooltip(new Tooltip("Changer le data-set"));
        export.setTooltip(new Tooltip("Exporter en .zip securise"));

        filesList = FXCollections.observableArrayList();
        NbrePersonne = 0;
        File[] folders = database.listFiles(new FileFilter() {

            @Override

            public boolean accept(File pathname) {
                NbrePersonne++;

                return pathname.isDirectory();

            }
        });

        dashboard.setStyle("-fx-font-family: Gotham; -fx-font-weight: bold;");

        assert folders != null : "ORL is empty dude";

        updateList(folders);

        Task<Void> listener = new Task<>() {
            @Override
            protected Void call() throws Exception {

                final Path path = FileSystems.getDefault().getPath(DataBase.getPATH());
                try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                    while (true) {
                        final WatchKey wk = watchService.take();
                        boolean executed = false;
                        for (WatchEvent<?> ignored : wk.pollEvents()) {
                            if (!executed) {
                                File[] fFolders = database.listFiles(File::isDirectory);
                                Platform.runLater(() -> {
                                    updateList(fFolders);
                                });
                                executed = true;
                            }
                        }

                        boolean valid = wk.reset();
                        if (!valid) {
                            System.out.println("Key has been unregistered");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                return null;
            }

        };

        Thread th = new Thread(listener);
        th.start();

    }

    private void updateList(File[] folders) {

        filesList.clear();

        // File file1 = new File(database.getAbsolutePath().toString()+"\\s"+NbrePersonne);

        for (File file : folders) {
            HBox person = new HBox();
            person.setSpacing(10);
            Pane spacer = new Pane();
            person.getChildren().add(new Label(file.getName()));
            HBox.setHgrow(spacer, Priority.ALWAYS);
            person.getChildren().add(spacer);

            /******show pic code
             *
             *
             *
             *
             *
             *
             *
             *
             *
             *
             *
             *
             * show the fist image in specified folder
             *
             *
             *
             */

            Button Show = new Button("Afficher");
            Show.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    //System.out.print("Show");
                    String path = file.getAbsolutePath() + "\\1.pgm";
                    //System.out.println(path);
                    String newPath = "";

                    Image new1 = null;
                    try {
                        newPath = converter.convertFormat(path, "showing");

                        new1 = new Image(new File("showing.jpg").toURI().toString());

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    imageview.setImage(new1);
                    imageview.setVisible(true);
                    imageview.setCache(true);
                    FileUtils.deleteQuietly(new File("showing.jpg"));
                }
            });


/***deletion  code   ***
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

            Button Delete = new Button("Supprimer");
            Delete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Confirmation de la suppression");
                    dialog.setHeaderText("Etes-vous sure de vouloir supprimer la personne ?");

                    ButtonType loginButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

                    AtomicBoolean reAsk = new AtomicBoolean(true);

                    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);

                    Button cnlBtn = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);

                    cnlBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            reAsk.set(false);
                        }
                    });

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(20, 150, 10, 10));

                    PasswordField password = new PasswordField();
                    password.setPromptText("Admin Password");

                    grid.add(new Label("Admin Password :"), 0, 0);
                    grid.add(password, 0, 1);

                    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                    loginButton.setDisable(true);

                    password.textProperty().addListener((observable, oldValue, newValue) -> {
                        loginButton.setDisable(newValue.trim().isEmpty());
                    });

                    dialog.getDialogPane().setContent(grid);

                    Platform.runLater(password::requestFocus);

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == loginButtonType) {
                            return password.getText();
                        }
                        return null;
                    });
                    /**optimized deleting code**/
                    do{

                        AtomicReference<Optional<String>> result = new AtomicReference<>(dialog.showAndWait());

                        result.get().ifPresent(pass ->{
                            if (pass.compareTo("della3")==0) {
                                reAsk.set(false);
                                dialog.close();
                                FileUtils.deleteQuietly(file);
                                try {
                                    reorganize(database.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                File[] temp = database.listFiles(File::isDirectory);
                                updateList(temp);
                                nbreOfPersonIndatabase--;
                                retrain=true;

                            }
                        });

                    } while (reAsk.get());



                }

            });


            Show.getStyleClass().add("show");
            Delete.getStyleClass().add("delete");

            person.getChildren().addAll(Show, Delete);

            HBox.setMargin(listView, new Insets(10, 0, 10, 0));
            filesList.add(person);

        }

        listView.setItems(filesList);

    }


    public static void reorganize(String path) throws IOException, InterruptedException {
        //Renaming all sub-folders
        File Directory = new File(path);
        File[] folders = Directory.listFiles(File::isDirectory);

        for(int i=0; i<folders.length ;i++){
            folders[i].renameTo(new File(Directory.getAbsolutePath()+"/ss"+(i+1)));
        }

        File Directoryy = new File(path);
        File[] folderss = Directory.listFiles(File::isDirectory);
        for(int i=0; i<folderss.length ;i++){
            folderss[i].renameTo(new File(Directoryy.getAbsolutePath()+"/s"+(i+1)));
        }

        //Renaming images inside all folders
        File Directory2 = new File(path);
        File[] folders2 = Directory.listFiles(File::isDirectory);

        for(int i=0; i<folders2.length; i++){
            File parent = new File(folders2[i].getAbsolutePath());
            File[] images = parent.listFiles(File::isFile);
            for(int j=0 ; j<images.length ;j++){
                images[j].renameTo(new File(parent.getAbsolutePath()+"/ss"+(j+1)+".pgm"));
            }
        }

        File Directoryy2 = new File(path);
        File[] folderss2 = Directory.listFiles(File::isDirectory);

        for(int i=0; i<folderss2.length; i++){
            File parent = new File(folderss2[i].getAbsolutePath());
            File[] images = parent.listFiles(File::isFile);
            for(int j=0 ; j<images.length ;j++){
                images[j].renameTo(new File(parent.getAbsolutePath()+"/"+(j+1)+".pgm"));
            }
        }



    }


    public void backToMain(ActionEvent e) throws IOException {

        if (retrain) {

            Stage dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            RingProgressIndicator ringProgressIndicator = new RingProgressIndicator();
            ringProgressIndicator.setVisible(true);
            ringProgressIndicator.makeIndeterminate();
            Scene scene = new Scene(ringProgressIndicator);
            dialogStage.setScene(scene);
            dialogStage.show();

            Stage stage = new Stage();
            try {
                db=new DataBase(5, db.getPATH());
//System.out.println("nombre de personnes"+db.getNUMBEROFTRAININGPERSONS());
                int trainingPicsParPersonne = 5;
                int PicsToAjustThreshold_ParPersonne = 1;
                double threshold_Step = 0.0005;
                int numberOfTrainingPersons = NbrePersonne;
                int dischoice = 1;

                RecognitionSystem.trainModel(db, dischoice, trainingPicsParPersonne,
                        PicsToAjustThreshold_ParPersonne, threshold_Step);

                // System.out.println("number of training persons"+db.getNUMBEROFTRAININGPERSONS());

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            dialogStage.close();
        }

        sceneManager.showScene(sceneManager.getScene("main"));

    }



    public void export(){
        Stage stage = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser.setTitle("Choisissez un emplacement");
        File pickedLoc = directoryChooser.showDialog(stage);
        export_password(pickedLoc);
    }


    private Button ok ;
    private PasswordField pF;

    public void export_password(File pickedLoc){
        Stage stage1 = new Stage();
        ok = new Button();
        ok.setText("Ok");
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File export = new File(pickedLoc+"/exported_ORL.zip");
                try {
                    protection.hash(database.getAbsolutePath(),export.getAbsolutePath(),pF.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage1.close();
            }
        });

        final Label message = new Label("");
        Label label = new Label("mot de passe");

        pF = new PasswordField();

        HBox hb = new HBox(10, label, pF);
        hb.setAlignment(Pos.CENTER_LEFT);

        HBox hb1 = new HBox(10,ok);
        hb1.setAlignment(Pos.CENTER_LEFT);



        VBox vb = new VBox(10, hb, hb1, message);
        vb.setPadding(new Insets(10));

        stage1.setScene(new Scene(vb,300,100));
        stage1.show();

    }



}
