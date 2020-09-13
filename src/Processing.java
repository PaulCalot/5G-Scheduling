import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;


public class Processing {
    Instance I;
    Triplet[] greedySolution;
    Triplet[] dpSolution;
    Triplet[] bbSolution;
    int maxUpperBound = 0;
    LinkedList<Triplet> allTriplets;

    public Processing(Instance i) {
        I = i;
        this.greedySolution = new Triplet[this.I.N+1];
        this.dpSolution = new Triplet[this.I.N+1];
        this.bbSolution = new Triplet[this.I.N+1];
        this.allTriplets = this.allTriplets();
    }

    // ---------------------------- Print solution --------------------------------- //
    public void printSolution(String solutionType){
        System.out.println("The " + solutionType + "solution is : ");
        System.out.println("Instance (N,K,M,P) : "+ this.I.N + ", " + this.I.K +", " + this.I.M + ", " +this.I.P );
        for (int n = 0; n<this.I.N+1; n++){
            if (solutionType == "greedy"){
                if (this.greedySolution[n] != null){
                    System.out.println(this.greedySolution[n].toString());
               }
            }
            else if (solutionType == "DP"){
                if (n != this.I.N){
                    if (this.dpSolution[n] != null){
                    System.out.println(this.dpSolution[n].toString());
               }}
            }
            else if (solutionType == "BB"){
                if (this.bbSolution[n] != null){
                    System.out.println(this.bbSolution[n].toString());
               }
            }
        }
        int[] PR = this.solutionUserRateAndPower(solutionType);
        System.out.println("Solution power / user rate : " + PR[0] + " / " + PR[1]); 
    }

    public int[] solutionUserRateAndPower(String solutionType){
        int power = 0;
        int userRate = 0;
        for (int n = 0; n<this.I.N+1; n++){
            if (solutionType == "greedy"){
                if (this.greedySolution[n] != null){
                    power += this.greedySolution[n].p * this.greedySolution[n].x;
                    userRate += this.greedySolution[n].r * this.greedySolution[n].x;
               }
            }
            else if (solutionType == "DP"){
                if (n != this.I.N){
                    if (this.dpSolution[n] != null){
                        power += this.dpSolution[n].p;
                        userRate += this.dpSolution[n].r;
                    }
                }
            }
            else if (solutionType == "BB"){
                if (this.bbSolution[n] != null){
                    power += this.bbSolution[n].p;
                    userRate += this.bbSolution[n].r;
               }
            }
        }

        return new int[]{power, userRate};
    }

    public static void printInstance(LinkedList<Triplet> tList) {
        System.out.println(tList.size());
        ListIterator<Triplet> listIt = tList.listIterator(0);
        while (listIt.hasNext()){
            Triplet t = listIt.next();
            System.out.println(t.toString());
        
        }
    }

    // ------------------------- others --------------------------------------------- //
    public LinkedList<Triplet> allTriplets(){
        LinkedList<Triplet> allTriplets = new LinkedList<>();
        for (int n = 0; n<this.I.N; n++){
            ListIterator<Triplet> listIt = this.I.tripletsList[n].listIterator(0);
            while (listIt.hasNext()){
                Triplet t = listIt.next();
                allTriplets.add(t);
            }
        }

        Collections.sort(allTriplets);
        return allTriplets;
    }
    // ------------------------- Q6) Greedy Algorithm -------------------------------- //
    
    // analogy with economics.
    public void greedyProcessing(){
        Collections.sort(this.allTriplets);
        this.greedySolver(this.allTriplets);
    }   

    

    public void greedySolver(LinkedList<Triplet> allSortedTriplets){
        ListIterator<Triplet> listItAll = allSortedTriplets.listIterator(0);

        int requiredPower = 0;

        while (listItAll.hasNext() && requiredPower<this.I.P){
;
            Triplet t = listItAll.next();

            if (this.greedySolution[t.n]==null){
                if (requiredPower + t.p <= this.I.P){
                    t.x = 1;
                    requiredPower += t.p * t.x;
                }
                else{
                    t.x = ((float) (this.I.P - requiredPower))/t.p;
                    requiredPower = this.I.P;
                }
                this.greedySolution[t.n]=t;
            }
            else{
                if (requiredPower + t.p -this.greedySolution[t.n].p <= this.I.P){
                    t.x = 1;
                    requiredPower += t.p - this.greedySolution[t.n].p ;
                    this.greedySolution[t.n].x=0;
                    this.greedySolution[t.n]=t;
                }
                else{
                    float x = ((float)this.I.P - requiredPower + (this.greedySolution[t.n].x -1) * this.greedySolution[t.n].p) / (t.p - this.greedySolution[t.n].p);
                    this.greedySolution[t.n].x=1-x;
                    t.x = x;
                    this.greedySolution[this.I.N] = t;
                    requiredPower = this.I.P;
                }
            }
        }        
    }
    
    
    
    // -------------------------- Q8) DP - algorithm ---------------------------------- // 

    public void dpProcessing(){

        int[][] objectiveFunction = new int[this.I.N][this.I.P+1];
        
        HashMap<Integer, Triplet[]>[] optimalSolutions = new HashMap[this.I.N]; // to store the solutions
        //optimalSolutions[0] = new HashMap<>();

        
       /* for (int p = 0; p<this.I.P+1; p++){
            optimalSolutions[0].put(p,new Triplet[0]); // we initialize with only one triplet, which is a false one. 
            // the n is to say that this {0,n} does not contain n.
            // the last one keeps the best solution so far
        }*/

        this.I.sortAllInstances(new Comparator<Triplet>(){
            public int compare (Triplet t1, Triplet t2){
                return (t1.p-t2.p); // sorting by increasing p.
            }
        }); // we sort along the p-axis.


        // we add a channel each time. So optimaleSolutions[n].get(p) has the best solution for the n-first channels and power = p.
        // To find the best solution for (n+1, p), we have to take the max, for all p and all triplet in channel n of R(n-1, p) + r_n, so that Sum(p) + p_n < P.
        

        Triplet initTriplet = new Triplet(0,-1,-1,0,0);

        // initialization for n = 0 
        
        optimalSolutions[0] = new HashMap<>();

        for (int i = 0; i<=this.I.P; i++){
            objectiveFunction[0][i] = 0;
            optimalSolutions[0].put(i, new Triplet[1]);

            ListIterator<Triplet> listIt = this.I.tripletsList[0].listIterator(0);
            while (listIt.hasNext()){
                Triplet t = listIt.next();
                if (t.p>i){ 
                    // the following ones are even bigger along p-axis. So we break.
                    optimalSolutions[0].get(i)[0] = initTriplet; 
                    break;
                }
               
                int currentObjectiveFuncion = t.r; 
                
                if (objectiveFunction[0][i] < currentObjectiveFuncion){
                    objectiveFunction[0][i] = currentObjectiveFuncion;
                    
                    optimalSolutions[0].get(i)[0] = t;
                }
            }
        }
        
        for (int n = 1; n < this.I.N; n++){

            // initialize the n-hashmap
            optimalSolutions[n] = new HashMap<>();

            for (int i = 0; i<=this.I.P; i++){
                objectiveFunction[n][i] = 0;
                optimalSolutions[n].put(i, new Triplet[n+1]);

                ListIterator<Triplet> listIt = this.I.tripletsList[n].listIterator(0);
                while (listIt.hasNext()){
                    Triplet t = listIt.next();
                    if (t.p>i){ 
                        // the following ones are even bigger along p-axis. So we break.
                        break;
                    }
                   
                    int currentObjectiveFuncion = objectiveFunction[n-1][i-t.p] + t.r; 
                    
                    if (objectiveFunction[n][i] < currentObjectiveFuncion){
                        objectiveFunction[n][i] = currentObjectiveFuncion;
                        
                        for (int chan = 0; chan < n; chan++){
                            //System.out.println( optimalSolutions[n-1].get(i-t.p)[chan]);
                            optimalSolutions[n].get(i)[chan] = optimalSolutions[n-1].get(i-t.p)[chan];
                        }
                        optimalSolutions[n].get(i)[n] = t;

                        // now we have watched for all of them. We just need to select the right solution which whould be : max (R(N,p)) for all p 
                        // (by construction, we use all channels even if some can be filled by fake triplet) 
                    }
                }
            }
        }

        this.dpSolution = optimalSolutions[this.I.N-1].get(this.bestUserRateIndice(objectiveFunction[this.I.N-1]));
    }
       
public int bestUserRateIndice(int[] userRates){
    int bestUserRateIndice = 0;
    int maxUserRate = 0;
    for (int p = 0; p<userRates.length; p++){
        if (userRates[p]>maxUserRate){
            maxUserRate = userRates[p];
            bestUserRateIndice = p;
        }
    }
    return bestUserRateIndice;
}


    // -------------------------- QP) BB - algorithm ---------------------------------- // 
    public class BBInstance implements Comparable<BBInstance>{
        Triplet currentTriplet; 
        int userRateSum;
        int lowerBound;
        int upperBound;
        int currentChannel;
        int requiredPower;

        public BBInstance(Triplet currentTriplet) {
            this.currentTriplet = currentTriplet;
            this.userRateSum = 0;
            this.upperBound = Integer.MAX_VALUE;
            this.currentChannel = 0;
            this.requiredPower = 0;
        }

        public BBInstance(Triplet current, int currentChannel, int requiredPower, int userRateSum) {
            this.currentTriplet = current;
            this.userRateSum = userRateSum;
            this.upperBound = Integer.MAX_VALUE;
            this.currentChannel = currentChannel;
            this.requiredPower = requiredPower;
        }

        public void setBound(int bound){
         
            this.upperBound = bound;
        }

        @Override
        public int compareTo(BBInstance arg0) {
            return (this.upperBound - arg0.upperBound);
        }
    }

    public Stack<BBInstance> branch(BBInstance bbInstance){
        // we suppose that there still are channels left to explore.
        Stack<BBInstance> newBranches= new Stack();
        ListIterator<Triplet> listIt = this.I.tripletsList[bbInstance.currentChannel].listIterator(this.I.tripletsList[bbInstance.currentChannel].size());
        
        // we suppose it's already sorted along r axis.
        
        LinkedList<BBInstance> bbInstanceList = new LinkedList<>();
        while (listIt.hasPrevious()){
            Triplet t = listIt.previous();
            BBInstance newBBInstance = new BBInstance(t, bbInstance.currentChannel+1,bbInstance.requiredPower+t.p, bbInstance.userRateSum + t.r);
            newBBInstance.setBound(this.bound(newBBInstance));
            bbInstanceList.add(newBBInstance);
        }
        Collections.sort(bbInstanceList);
        ListIterator<BBInstance> listItBBInstance = bbInstanceList.listIterator(0);
        while (listItBBInstance.hasNext()){
            newBranches.push(listItBBInstance.next());
        }
        return newBranches;
    }

    public int bound(BBInstance bbInstance){

        // we suppose I is already sorted along the r axis.
        int upperBound = bbInstance.userRateSum + this.greedyBound(bbInstance.currentChannel-1); 
        return upperBound;
    }

    // this algorithms is a modified version of the previous one. 
    private int greedyBound(int currentChannel) {
        ListIterator<Triplet> listItAll = this.allTriplets.listIterator(0);

        int requiredPower = 0;
        int bound = 0;
        Triplet[] boundSol = new Triplet[this.I.N - currentChannel];
        while (listItAll.hasNext() && requiredPower<this.I.P){
;
            Triplet t = listItAll.next();
            if (t.n >= currentChannel){
                if (boundSol[t.n-currentChannel]==null){
                    if (requiredPower + t.p <= this.I.P){
                        t.x = 1;
                        requiredPower += t.p * t.x;
                    }
                    else{
                        t.x = ((float) (this.I.P - requiredPower))/t.p;
                        requiredPower = this.I.P;
                    }
                    boundSol[t.n-currentChannel]=t;
                    bound += t.r;
                }
                else{
                    if (requiredPower + t.p -boundSol[t.n-currentChannel].p <= this.I.P){
                        t.x = 1;
                        requiredPower += t.p * t.x - boundSol[t.n-currentChannel].p ;
                        boundSol[t.n-currentChannel].x=0;
                        bound += t.r - boundSol[t.n-currentChannel].r;
                        boundSol[t.n-currentChannel]=t;
                    }
                    else{
                        float x = ((float)this.I.P - requiredPower + (boundSol[t.n-currentChannel].x -1) * boundSol[t.n-currentChannel].p) / (t.p - boundSol[t.n-currentChannel].p);
                        boundSol[t.n-currentChannel].x=1-x;
                        t.x = x;
                        bound += t.r * x - boundSol[t.n-currentChannel].r *(1-x);
                        boundSol[this.I.N-currentChannel-1] = t;
                        requiredPower = this.I.P;
                    }
                }
            }
        }
        return bound;
    }
    

                
    

    public boolean solution(BBInstance bbInstance) {
    
        //System.out.println(bbInstance.currentChannel);
        if (bbInstance.requiredPower > this.I.P || bbInstance.upperBound <= this.maxUpperBound) {
            return false; 
        }
        else{
            if (bbInstance.upperBound > this.maxUpperBound && bbInstance.currentChannel==this.I.N){ 
                // we reach the last channel and found a solution.
                this.bbSolution[bbInstance.currentChannel-1]=bbInstance.currentTriplet;
                this.maxUpperBound = bbInstance.upperBound;
                return true; // solution founds
            }
            else {
                if (bbInstance.currentChannel==this.I.N){
                    // we already reach the last one. We won't find a solution.
                    return false;
                }
                Stack<BBInstance> children = this.branch(bbInstance);
                boolean solutionFound = false;
                while (! children.isEmpty()){
                    BBInstance child = children.pop();
                    
                    if(solution(child)){
                        if (bbInstance.currentChannel == 0){
                            return true;
                        }
                        solutionFound = true;
                        this.bbSolution[bbInstance.currentChannel-1]=bbInstance.currentTriplet;
                    }
                }
                return solutionFound;
            }
        }
    }

    public void bbProcessing(){

        Collections.sort(this.allTriplets);
        this.I.sortAllInstances(new Comparator<Triplet>(){
            public int compare(Triplet t1, Triplet t2){
                return t1.r - t2.r;
            }
        });

        BBInstance root = new BBInstance(null); // all are free.

        this.maxUpperBound=0;

        // we have to set the initials lower and upper bounds so we can calculate the next ones.
        int upperBound = 0;

        for (int n = 0; n<this.I.N; n++){
            upperBound += this.I.tripletsList[n].getLast().r;
        }
        root.setBound(upperBound);

        solution(root);
        }
}

