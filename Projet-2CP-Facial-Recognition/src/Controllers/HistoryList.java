package Controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class HistoryList {

    private static Image trueSymb;

    static {
        try {
            trueSymb = new Image(new FileInputStream(new File("src/UI/assets/true.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Image falseSymb;

    static {
        try {
            falseSymb = new Image(new FileInputStream(new File("src/UI/assets/false.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class History implements Serializable{

        transient private VBox historyCard = new VBox();
        private boolean state;
        private String imagePath;
        private String dateTimeInGMT;

        public History(boolean state, String imagePath, String dateTimeInGMT) throws FileNotFoundException {

            VBox cardHistory = new VBox();
            ImageView imageHistory = new ImageView(new Image(new FileInputStream(imagePath)));
            imageHistory.preserveRatioProperty().setValue(true);
            imageHistory.setFitHeight(150);
            HBox detailsHistory = new HBox();
            ImageView stateHistory = new ImageView(state ? trueSymb : falseSymb);
            stateHistory.preserveRatioProperty().setValue(true);
            stateHistory.setFitWidth(20);
            Pane spacer = new Pane();
            Pane vSpacer = new Pane();
            DropShadow dropShadow = new DropShadow();
            dropShadow.setBlurType(BlurType.GAUSSIAN);
            dropShadow.setColor(Color.color(0.1, 0.1, 0.1, 0.1));
            dropShadow.setWidth(10);
            dropShadow.setRadius(20);
            dropShadow.setOffsetY(10);
            HBox.setHgrow(spacer, Priority.ALWAYS);

            detailsHistory.getChildren().add(new Label(dateTimeInGMT));
            detailsHistory.getChildren().add(spacer);
            detailsHistory.getChildren().add(stateHistory);
            detailsHistory.alignmentProperty().setValue(Pos.CENTER);
            detailsHistory.setStyle("-fx-padding: 15");
            VBox.setMargin(detailsHistory, new Insets(2, 2, 2, 2));
            cardHistory.setStyle("-fx-background-color: #FFF; -fx-background-radius: 5");
            Rectangle clip = new Rectangle(150, 150);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            imageHistory.setClip(clip);
            cardHistory.getChildren().add(imageHistory);
            VBox.setVgrow(vSpacer, Priority.ALWAYS);
            VBox.setVgrow(clip, Priority.ALWAYS);
            cardHistory.getChildren().add(vSpacer);
            cardHistory.getChildren().add(detailsHistory);
            cardHistory.setEffect(dropShadow);

            this.historyCard = cardHistory;
            this.state = state;
            this.imagePath = imagePath;
            this.dateTimeInGMT = dateTimeInGMT;

        }

        public VBox getHistoryCard() {
            return historyCard;
        }

        public void setHistoryCard(VBox historyCard) {
            this.historyCard = historyCard;
        }

        public boolean isState() {
            return state;
        }

        public boolean getState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getDateTimeInGMT() {
            return dateTimeInGMT;
        }

        public void setDateTimeInGMT(String dateTimeInGMT) {
            this.dateTimeInGMT = dateTimeInGMT;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    static ArrayList<History> historyList = new ArrayList<History>();;

    public static void addHistory( boolean state, String image, String dateTimeInGMT ) throws IOException, ClassNotFoundException {
        historyList.add(new History(state, image, dateTimeInGMT));
        saveList();
    }

    private static void saveList() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File("historique/history.ser"))));
        oos.writeObject(historyList);
        oos.close();
    }

    public static void readList() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File("historique/history.ser"))));
        ArrayList<History> inter = (ArrayList<History>) ois.readObject();
        ois.close();

        historyList.clear();
        for (History history: inter) {
            addHistory(history.getState(), history.getImagePath(), history.dateTimeInGMT);
        }

    }

    public static ArrayList<History> getHistoryList(){
        return historyList;
    }

    public static void main(String[] args) throws IOException {
    }

}