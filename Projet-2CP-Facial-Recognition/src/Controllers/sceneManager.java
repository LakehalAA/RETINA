package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class sceneManager  extends Stage{

    static Stage primaryStage;


    public sceneManager(Stage stage){
        primaryStage = stage;
    }

    public static class Scene {

        public Scene(int height, int width, String name, String path) {
            this.height = height;
            this.width = width;
            this.name = name;
            this.path = path;
        }

        private int height;
        private int width;
        private String name;
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static HashMap<String, Scene> scenesList = new HashMap<>();

    public static void addScene (String name, Scene scene ){
        scenesList.put(name, scene);
    }

    public static void showScene(Scene scene) throws IOException {

        Parent root = FXMLLoader.load(sceneManager.class.getResource(scene.path));
        javafx.scene.Scene active = new javafx.scene.Scene(root, scene.getWidth(), scene.getHeight());

        primaryStage.setTitle(scene.getName());
        primaryStage.setScene(active);
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public static Scene getScene(String name){
        return scenesList.get(name);
    }

    public HashMap<String, Scene> getScenesList() {
        return scenesList;
    }

    public void setScenesList(HashMap<String, Scene> scenesList) {
        this.scenesList = scenesList;
    }

	

}
