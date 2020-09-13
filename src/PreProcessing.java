import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class PreProcessing  {

    Instance I; // easier to pass it to the Processing class later on.

    public PreProcessing(Instance i) {
        I = i;
    }



    
    // we are not using a field to know if an instance is sorted and if sorted, along which variable it's sorted : p, r etc. 
    // So we have to keep track ourselves of which sorting is actually being used. 
    //TODO:  implement this.


    // ---------------------------- Q2 ) Naive PreProcessing -------------------------------- //
    
    public void naivePreProcessing() {

        // we begin by sorting along p-axis
        this.I.sortAllInstances(new Comparator<Triplet>(){
            public int compare(Triplet t1, Triplet t2){
                if (t1.p == t2.p){
                    return t1.r - t2.r;
                }
                return t1.p - t2.p;
            }
        });
        this.naiveRemoveP_ExceedingTriplet();
        this.naiveRemoveSamePorR_Triplet();
    }

            
    // we remove the triplets that are too big so they the minimum sum is higher than the max power.
    public void naiveRemoveP_ExceedingTriplet(){
        for (int n = 0; n < this.I.N; n++) {
            ListIterator<Triplet> listIt = this.I.tripletsList[n].listIterator(this.I.tripletsList[n].size());
            while (listIt.hasPrevious()) {

                // Max sum
                Triplet t = listIt.previous();
                int pSum = t.p;
                for (int i = 0; i < this.I.N; i++) {
                    if (i != n) {
                        if (!this.I.tripletsList[i].isEmpty()){
                            pSum += this.I.tripletsList[i].getFirst().p;
                        }
                    }
                }
                if (pSum > this.I.P) {
                    listIt.remove();
                } else {
                    break;
                }
            }
        }
    }

    // we keep only one triplet with the power p (the one with the higher user rate)
    public void naiveRemoveSamePorR_Triplet() {

        for (int n = 0; n < this.I.N; n++) {
            
            // remove same p
                // sorting by increasing p and then decreasing r.
            this.I.sortInstance(new Comparator<Triplet>() {
                public int compare(Triplet t1, Triplet t2) {
                    if (t1.p == t2.p){
                        return -t1.r+t2.r;
                    }
                    return (t1.p - t2.p);
            }}, n);

            ListIterator<Triplet> listIt = this.I.tripletsList[n].listIterator(0);
            this.remove(listIt, "p");     
            
            // remove same r
                // sorting by increasing r and then increasing p.
                this.I.sortInstance(new Comparator<Triplet>() {
                    public int compare(Triplet t1, Triplet t2) {
                        if (t1.r == t2.r){
                            return t1.p-t2.p;
                        }
                        return (t1.r - t2.r);
                }}, n);

            listIt = this.I.tripletsList[n].listIterator(0);
            this.remove(listIt, "r");
     
        }
    }
    
    public void remove(ListIterator<Triplet> listIt, String axis){
        if (listIt.hasNext()){
            if (axis == "p"){
                Triplet tOld = listIt.next();
                while (listIt.hasNext()) {
                    Triplet t = listIt.next();
                    if (t.p == tOld.p){
                        listIt.remove(); // sorted by increasing p and then decreasing r.
                    }
                    tOld = t;
                }
            }
            else if (axis == "r"){
                Triplet tOld = listIt.next();
                while (listIt.hasNext()) {
                    Triplet t = listIt.next();
                    if (t.r == tOld.r){
                        listIt.remove(); // sorted by increasing r and then increasing p.
                    }
                    tOld = t;
                }   
            }
        }
    }

    // Q3 ) 

    public void removeIPDominated(){
        for (int n = 0; n< this.I.N; n++){
            int lenght = this.I.tripletsList[n].size();
            int oldLenght = 0;
            // In practice I notices that doing the algorithms several times was useful to remove every triplets. 
            // Only once remove most of the IP-dominated triplets but not all of them
            while (lenght - oldLenght >0) {
                // sorting while comparing to check if the given triplet is IP-dominated.
                this.I.sortInstance(new Comparator<Triplet>() {
                    public int compare(Triplet t1, Triplet t2){
                        if(t1.r > t2.r){
                            if (t1.p <= t2.p){
                                t2.x = 0;
                            }
                            return 1;
                        }
                        else {
                            // by previous preprocessing we can't have t1.r = t2.r (neither t1.p = t2.p)
                            if (t1.p>= t2.p){
                                t1.x = 0;
                            }
                            return -1;
                        }
                    }
                }, n);

                this.removeIPDominated(this.I.tripletsList[n]);

                this.I.sortInstance(new Comparator<Triplet>() {
                    public int compare(Triplet t1, Triplet t2){
                        if(t1.p > t2.p){
                            if (t1.r <= t2.r){
                                t1.x=0;
                            }
                            return 1;
                        }
                        else {
                            // by previous preprocessing we can't have t1.r = t2.r (neither t1.p = t2.p)
                            if (t1.r>= t2.r){
                                t2.x=0;
                            }
                            return -1;
                        }
                    }
                }, n);

                this.removeIPDominated(this.I.tripletsList[n]);

                // we resert the x for all remaining triplets.
                this.I.resetAllX(this.I.tripletsList[n]);

                oldLenght = lenght;
                lenght = this.I.tripletsList[n].size();
            }
        }
    }

    public void removeIPDominated(LinkedList<Triplet> tripletsList){
        ListIterator<Triplet> listIt = tripletsList.listIterator(0);
        while (listIt.hasNext()){
            Triplet t = listIt.next();
            if (t.x == 0){
                listIt.remove();
            }
        }
    }

    // Q4 ) 

    public static float cross(Triplet A, Triplet B, Triplet C){
        // tells if there is a counter-clockwise turn
        return (B.p - A.p) * (float) (C.r - A.r) - (B.r - A.r) * (float) (C.p - A.p);
    }
    public void removeLPDominated(){
        for (int n = 0; n < this.I.N; n++){
            this.I.sortInstance(null, n);
            ListIterator<Triplet> listIt = this.I.tripletsList[n].listIterator(0);
            
            
            int lenght = this.I.tripletsList[n].size();
            Triplet[] L = new Triplet[lenght];
            int k = 0;
            while (listIt.hasNext()){
                Triplet t = listIt.next();
                while (k >=2 && cross(L[k-2], L[k-1], t)>=0){
                    k--;
                }
                L[k++] = t;
            }


            // remove non hull Triplets

            for (int i = k-1; i >= 0; i--){
                while (listIt.hasPrevious()) {
                    Triplet t = listIt.previous();
                    if (!t.sameCoord(L[i])){
                        // t is not in the hull. Else it is.
                        listIt.remove();
                    }
                    else {
                        break;
                    }
                }
            }
        }
    }
}
