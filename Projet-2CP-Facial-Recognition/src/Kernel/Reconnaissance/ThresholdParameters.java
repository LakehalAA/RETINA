package Kernel.Reconnaissance;
public class ThresholdParameters {
    private double threshold_value;
    private double recall ;
    private double precision ;
    private double accuracy ;
    private double F1_score;
    private double FAR;
    private double FRR;

    //Getters & Setters
    public double getThreshold_value() {
        return threshold_value;
    }

    public double getFAR() {
        return FAR;
    }

    public void setFAR(double FAR) {
        this.FAR = FAR;
    }

    public double getFRR() {
        return FRR;
    }

    public void setFRR(double FRR) {
        this.FRR = FRR;
    }

    public void setThreshold_value(double threshold_value) {
        this.threshold_value = threshold_value;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double reacall) {
        this.recall = reacall;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setF1_score(double f1_score) {
        F1_score = f1_score;
    }

    public double getF1_score() {
        return F1_score;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }


    public ThresholdParameters(double threshold_value, double recall, double precision, double accuracy, double f1_score, double FAR, double FRR) {
        this.threshold_value = threshold_value;
        this.recall = recall;
        this.precision = precision;
        this.accuracy = accuracy;
        F1_score = f1_score;
        this.FAR = FAR;
        this.FRR = FRR;
    }

    //Method used for tests
    public void afficher(){
        //System.out.println("Threshold value = "+this.threshold_value+"   Recall = "+(this.recall*100)+"  Precision ="+(this.precision*100)+"   Accuracy = "+(this.accuracy*100)+" F1-score = "+(this.F1_score*100)+ ConsoleColors.RED+"  FAR = "+this.FAR*100+ ConsoleColors.RESET+ ConsoleColors.BLUE+"   FRR = "+this.FRR*100+ ConsoleColors.RESET);
    }

}


