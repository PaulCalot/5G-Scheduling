

public class Triplet implements Comparable<Triplet> {
    // position in the matrix :
    int n ;
    int k ;
    int m ;

    float x; // 0<= x <= 1 (if choosen)
    // values : 
    int p ; 
    int r ;

    public Triplet(int n, int k, int m, int p, int r) {
        this.n = n;
        this.k = k;
        this.m = m;
        this.p = p;
        this.r = r;
        this.x = 1;
    }


    public void resetX(){
        this.x = 1;
    }

    @Override
    public int compareTo(Triplet t) {
        return this.p - t.p;
    }

    public boolean sameCoord(Triplet t){
        if (this.n == t.n && this.k == t.k && this.m == t.m){
            return true;
        }
        return false;
    }

	@Override
	public String toString() {
		return ("Triplet coord (n,k,m) : " + this.n + ", " + this.k + ", " + this.m + "  ;  Power / user rate : " + this.p + " / " + this.r + "; x = " + this.x);
	}   
}