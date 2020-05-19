package Kernel.Reconnaissance;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;

import Kernel.Utils.DataBase;

public  class RecognitionSysCatactiristics {
	

	private static  int dischoice=1;//1mahalanobis 2manhattan 3euclidienne
	private static int numberOfPersons=40;
	private static int trainingPicsParPersonne;//on prend 5 par defaut 
	private static  String ORL_PATH;
    private static DataBase database ;//=
    private static int PicsToAjustThreshold_ParPersonne;//pour calculer le seuil on prend 2 par defaut
    private static double threshold_Step;//par defaut 0.00005
   
    private static Reconaissance process;// = new Reconaissance(orl,trainingPicsParPersonne,PicsToAjustThreshold_ParPersonne,dischoice,threshold_Step);
    private static  ThresholdParameters thresh = new ThresholdParameters(0,0,0,0,0,0,0);
    

    public static ThresholdParameters getThresh() {
		return thresh;
	}


	private static double Threshold_Value;
    private static double tauxDeReconnaissance;
    private static int TP,TN,FP,FN;
    private static int TotalTests;
    private static double TauxDeConfusion;
    private static double TauxDeRejet;
    private static ArrayList<ThresholdParameters> thresholds;//to so the graph of FAR AND FRR
    private static ArrayList<Double> FAR=new ArrayList<Double>();
    private static ArrayList<Double> FRR=new ArrayList<Double>();
    private static long execution_time;//nanosecondes
    private static int  MaxPicsParPersonne=10;
    static int maxPersonsINDataBase=40;//pour l instant a modifier lorsque utilisateur ajoute une ppersonne 
    private static int NBRECONF;
    private static int k=10;
    /**calcul des taux**/
    public static void  calculateTauxReconnaissance() {
    	int somme=TP+TN;
    	//System.out.println("\n TP+TN    "+somme);
    	double res=TP+TN;
    			res=res/TotalTests;
		tauxDeReconnaissance=res*100;
	}


	public static int getK() {
		return k;
	}


	public static void setK(int k) {
		RecognitionSysCatactiristics.k = k;
	}


	public static void calculateTauxDeRejet() {
	//	System.out.println("\n FN   "+FN);
		double res=FN;
		double somme=TP+FN;
		res=res/somme*100;
		TauxDeRejet=res;
	}

	public static void calculateTauxDeConfusion() {
		int somme=FP+FN;
    //	System.out.println("\n confusion Nbreconf  "+somme);	
    //	double res=FP+FN;
	  double res=somme;
		res=res/TotalTests*100;

		TauxDeConfusion=res;
	}
	/**fin de calcul des taux**/
	/**get distance choice used in interface**/
	public static int getDischoice() {
		return dischoice;
	}

	public static int getNumberOfPersons() {
		return numberOfPersons;
	}

	public static int getTrainingPicsParPersonne() {
		return trainingPicsParPersonne;
	}

	public static int getPicsToAjustThreshold_ParPersonne() {
		return PicsToAjustThreshold_ParPersonne;
	}

	public static double getThreshold_Step() {
		return threshold_Step;
	}
//utiliser de la fenetre de prediction juste on applant classe.getProcess.pridict(path)
	public static Reconaissance getProcess() {
		return process;
	}
	public static int getTotalTests() {
		return TotalTests;
	}
//exprimental
	public static double getThreshold_Value() {
		return Threshold_Value;
	}

	public static double getTauxDeReconnaissance() {
		return tauxDeReconnaissance;
	}

	

	public static double getTauxDeConfusion() {
		return TauxDeConfusion;
	}

	public static double getTauxDeRejet() {
		return TauxDeRejet;
	}
/**used to get the graph*/
	public static ArrayList<Double> getFAR() {
		return FAR;
	}

	public static ArrayList<Double> getFRR() {
		return FRR;
	}
/************/
	/**execution time needs to be shown in exprimental fennetre**/
	public static long getExecution_time() {
		return execution_time;
	}

	public static void setMaxPersonsINDataBase(int maxPersonsINDataBase) {
		RecognitionSysCatactiristics.maxPersonsINDataBase = maxPersonsINDataBase;
	}
	
	
    //count of training pics done automaticly in constructor of database
 
    public static String getORL_PATH() {
		return ORL_PATH;
	}


	public static ArrayList<ThresholdParameters> getThresholds() {
		return thresholds;
	}


	public static int getMaxPicsParPersonne() {
		return MaxPicsParPersonne;
	}


	public static int getMaxPersonsINDataBase() {
		return maxPersonsINDataBase;
	}


	public static void calculateRates()
    {     	NBRECONF=TP=TN=FN=FP=TotalTests=0;

    	long startTime = System.currentTimeMillis();
    
    	//cette boucle c est sur les personne sur les quelles on a fait l entrainement donc consid�rer comme connues  75%
    		
    		//System.out.println(database.getNUMBEROFTRAININGPERSONS() +"  "+MaxPicsParPersonne+"   "+trainingPicsParPersonne+PicsToAjustThreshold_ParPersonne);
    	 for(int itr1=1; itr1<=database.getNUMBEROFTRAININGPERSONS(); itr1++){
             for(int itr2=trainingPicsParPersonne+PicsToAjustThreshold_ParPersonne+1; itr2<=MaxPicsParPersonne; itr2++){
                 try {
                     int Returned_profil=process.predict((database.getPATH()+"\\s"+itr1+"\\"+itr2+".pgm"));
                     if(0== Returned_profil) FN++;
                     else
                     { 
                     	if (itr1==Returned_profil)
                        TP++;
                     	else {	
							FP++;
						}
                     }
                    
                     TotalTests++;
                     }

				 catch (Exception e) {
					// TODO: handle exception
					// System.out.println("personne non trouvee ignored");
				}
               //  System.out.println(Returned_profil);
                
                 

             
         }
    	 }
    	 
    		calculateTauxReconnaissance();
        	calculateTauxDeRejet();
        	calculateTauxDeConfusion();

        /** System.out.println("reconnaissance   "+tauxDeReconnaissance);
         System.out.println("rejet  "+ TauxDeRejet);
         System.out.println("confusion"+ TauxDeConfusion);
         System.out.println("sur  "+TotalTests);
         System.out.println("***************************************");*/
    	 //la decision repose sur comment on va choisir de faire �a 
    	 String  databaseforunkown=database.getPATH();
    	 //sur les personnes inconnues si on prend moins de 100% de personnes pour l entrainement 

      for(int itr1=database.getNUMBEROFTRAININGPERSONS()+1; itr1<=database.getNUMBERMAXOFPRESONS(); itr1++){
             for(int itr2=1; itr2<=database.getNUMBEROFIMAGESMAXPERPERSON() ; itr2++){
            	  int Returned_profil;
				
					try {
						Returned_profil = process.predict((databaseforunkown+"\\s"+itr1+"\\"+itr2+".pgm"));
						  if(Returned_profil==0) TN++;//personne inconnue comme expected
			                 else FP++;//personne inconnue est traite comme connue
			                 TotalTests++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//System.out.println("file not found");
						//e.printStackTrace();
					}
             }
      
					
				
               //   System.out.println(Returned_profil);
      }
               
                 

             
         
    
         long endTime = System.currentTimeMillis();
    	
    	
         execution_time = execution_time+ (endTime - startTime); 

    	 

      	//calcul des taux voir les formule ci dessous dans les methodes 
      	calculateTauxReconnaissance();
      	calculateTauxDeRejet();
      	calculateTauxDeConfusion();
        /**  System.out.println("reconnaissance   "+tauxDeReconnaissance);
          System.out.println("rejet  "+ TauxDeRejet);
          System.out.println("confusion"+TauxDeConfusion);
          System.out.println("sur  "+TotalTests);
          System.out.println("***************************************");*/
    	 }
    
    
    public static void trainModel(DataBase db,int dischoice,int PicsToAjustThreshold_ParPersonne, double threshold_Step) throws IOException
    {
           database=db;
        	long startTime = System.currentTimeMillis();
        	ORL_PATH=database.getPATH();
        	numberOfPersons=database.getNUMBERMAXOFPRESONS();
        	trainingPicsParPersonne=database.getNUMBEROFPICTURES();
            MaxPicsParPersonne=database.getNUMBEROFIMAGESMAXPERPERSON();  
            maxPersonsINDataBase=database.getNUMBERMAXOFPRESONS();
          //  System.out.println(database.getNUMBEROFIMAGESMAXPERPERSON());
          //  System.out.println("stat");

			process=new Reconaissance(database,trainingPicsParPersonne,PicsToAjustThreshold_ParPersonne,dischoice,threshold_Step,k);
			Threshold_Value=Threshold.getThreshold();
           // System.out.println("nbre of eigen vectors is "+process.getK());

			//System.out.println(Threshold_Value);
			RecognitionSysCatactiristics.PicsToAjustThreshold_ParPersonne=PicsToAjustThreshold_ParPersonne;
	    	long endTime = System.currentTimeMillis();

        	execution_time = (endTime - startTime); 
			thresholds=Threshold.getThresholdsParm();
			thresh=Threshold.getThresh();
			for (int i = 0; i < thresholds.size(); i++) {
				FAR.add(thresholds.get(i).getFAR());
				FAR.add(thresholds.get(i).getFRR());
			}
			
			k=10;
		
    	
    }


	
    
   
    	
    	
    }
    


