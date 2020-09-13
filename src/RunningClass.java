
import java.io.FileNotFoundException;

public class RunningClass {
    
    public static void main(String[] args) throws FileNotFoundException {
        int numberOfTextFiles = 5;
        
        for (int i = 1; i<=numberOfTextFiles; i++){
            // 2 is empty, no point in working on it. And 4 is much too long (5 too...)
            
        	if (i!=2){
	            System.out.println("");
	            System.out.println("");
	            System.out.println("File : " + i);
	            // Instance creation
	            Instance I = new Instance("Tests/test" + i +".txt");
	
	            // Preprocessing : 
	            Preprocessing(I, i, true);
	       
	                // Processing :
	            Processing(I,i, true);
        		}
    		}
        } 

    public static void Preprocessing(Instance I, int textFileNumber, boolean toTxt){
        PreProcessing preProcessingI = new PreProcessing(I);

        if (toTxt){
            System.out.println("Remaining triplet : " + preProcessingI.I.remainingTripletsNumber());
            preProcessingI.I.sortAllInstances(null);
            preProcessingI.I.toTxtFormat("test" + textFileNumber + "NoRemoval.txt");
        }
        // Q2) Naive proprocessing :
        preProcessingI.naivePreProcessing();
        
        
        if (toTxt){
            System.out.println("Remaining triplet : " + preProcessingI.I.remainingTripletsNumber());
            preProcessingI.I.sortAllInstances(null); // we have to sort it along "p" axis to plot it.s
            preProcessingI.I.toTxtFormat("test" + textFileNumber + "naiveRemoval.txt");
        }
        // Q3) IP-dominated preprocessing :
        preProcessingI.I.resetAllX();
        preProcessingI.removeIPDominated();

        if (toTxt){
            System.out.println("Remaining triplet : " + preProcessingI.I.remainingTripletsNumber());
            preProcessingI.I.sortAllInstances(null);
            preProcessingI.I.toTxtFormat("test" + textFileNumber + "IPremoval.txt");
        }
        // Q4)
        
        preProcessingI.removeLPDominated();


        // already sorted along p-axis
        if (toTxt){
            System.out.println("Remaining triplet : " + preProcessingI.I.remainingTripletsNumber());
            preProcessingI.I.toTxtFormat("test" + textFileNumber + "LPremoval.txt");
        }
    }

    public static void Processing(Instance I, int textFileNumber, boolean toTxt){
        
        Processing processingI = new Processing(I);
        
        System.out.println("");
        System.out.println("Test file " + textFileNumber + " Processing ");
        System.out.println("Number of remaining triplets : " + I.remainingTripletsNumber());
        
        // greedy processing
        processingI.I.resetAllX();

        double time1 = System.nanoTime();
        processingI.greedyProcessing();
        double time2 = System.nanoTime();

        if (toTxt){
            processingI.printSolution("greedy");
            System.out.print("Running time : ");
            System.out.println(10e-6f*(time2-time1));
        }

        // dpProcessing
        if (textFileNumber!=4 && textFileNumber!=5){
	        processingI.I.resetAllX();
	        time1 = System.nanoTime();
	        processingI.dpProcessing();
	        time2 = System.nanoTime();
	        if (toTxt){
	            processingI.printSolution("DP");
	            System.out.print("Running time : ");
	            System.out.println(10e-6f*(time2-time1));
	             
	        }
        }
        
        // bbProcessing
        if (textFileNumber!=4 && textFileNumber!=5){
	        processingI.I.resetAllX();
	        
	        time1 = System.nanoTime();
	        processingI.bbProcessing();
	        time2 = System.nanoTime();
	
	        if (toTxt){
	            processingI.printSolution("BB");
	            System.out.print("Running time : ");
	            System.out.println(10e-6f*(time2-time1));
	        }
        }
        
    }
}