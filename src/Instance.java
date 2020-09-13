import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Instance {

    // triplets list per channel.
    LinkedList<Triplet>[] tripletsList; // initially not sorted

    // Test file name (.txt)
    String textFileName;
    // constants
    int N;
    int K;
    int M;
    int P; // max power available.

    public Instance(String textFileName) throws FileNotFoundException {
		super();
		this.textFileName = textFileName;
        
        // we read the .txt
        BufferedReader f = new BufferedReader(new FileReader(textFileName));
        
		int count = 1;
		while (true) {
			try {
				String s = f.readLine();
				if (s == null) {
					break; // we reach the end of the text file
				}
				if (count == 1) {
					this.N = (int) Float.parseFloat(s);
				} else if (count == 2) {
					this.M = (int) Float.parseFloat(s);
				} else if (count == 3) {
					this.K = (int) Float.parseFloat(s);
				} else if (count == 4) {
					this.P = (int) Float.parseFloat(s);
					break;
				}
				count += 1;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// it's easier to read the files in 3D matrices before using LinkedList<Triplet>
        int[][][] pMatrices = new int[this.N][this.K][this.M];
        int[][][] rMatrices = new int[this.N][this.K][this.M];

        for (int i = 0; i < this.N; i += 1) {
			for (int j = 0; j < this.K; j += 1) {
				String s;
				try {
					s = f.readLine();
					String[] s2 = s.split("   ", 0); // 3 spaces in the text file.
					int m = 0;
					for (int a = 0; a < s2.length; a += 1) {
						if (!s2[a].isEmpty()) {
							pMatrices[i][j][m] = (int) Float.parseFloat(s2[a]);
							m += 1;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < this.N; i += 1) {
			for (int j = 0; j < this.K; j += 1) {
				String s;
				try {
					s = f.readLine();
					String[] s2 = s.split("   ", 0); // 3 spaces in the text file.
					int m = 0;
					for (int a = 0; a < s2.length; a += 1) {
						if (!s2[a].isEmpty()) {
							rMatrices[i][j][m] = (int) Float.parseFloat(s2[a]);
							m += 1;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// LinkedList 
        this.tripletsList = new LinkedList[this.N];
        for (int n=0; n<this.N; n++){
            this.tripletsList[n] = new LinkedList<Triplet>();
        }
        
        for (int n = 0; n<this.N; n++){
            for (int k = 0; k<this.K; k++){
                for (int m = 0; m<this.M; m++){
                    this.tripletsList[n].add(new Triplet(n,k,m,pMatrices[n][k][m],rMatrices[n][k][m]));
                }
            }
        }
		// close the file
		try {
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Instance(LinkedList<Triplet>[] tripletsList, String textFileName, int N, int K, int M, int P) {
		this.textFileName = textFileName;
		this.N = N;
		this.K = K;
		this.M = M;
		this.P = P;

		this.tripletsList = new LinkedList[this.N];
        for (int n=0; n<this.N; n++){
			this.copyTripletsList(n, tripletsList);
        }
    }

	public void copyTripletsList(int canal, LinkedList<Triplet>[] tripletsList){
	
		this.tripletsList[canal] = new LinkedList<Triplet>();
    
		ListIterator<Triplet> listIt = tripletsList[canal].listIterator(0);
		while (listIt.hasNext()){
			Triplet t = listIt.next();
			this.tripletsList[canal].add(t);
		}
	}
	
	public void resetAllX(){
		for (int n = 0; n<this.N; n++){
			this.resetAllX(this.tripletsList[n]);
		}
	}
	
	public void resetAllX(LinkedList<Triplet> tripletsList){
		ListIterator<Triplet> listIt = tripletsList.listIterator(0);
		while (listIt.hasNext()){
			Triplet t = listIt.next();
			t.resetX();
		}
	}

	public void sortAllInstances(Comparator<Triplet> comparator){
        for (int n = 0; n<this.N; n++){
            this.sortInstance(comparator, n);
        }
    }

    public void sortInstance(Comparator<Triplet> comparator, int canal){
		if (comparator == null){
			Collections.sort(this.tripletsList[canal]);
		}
		else{
			Collections.sort(this.tripletsList[canal], comparator);
		}
	}

    public int remainingTripletsNumber() {
        int number = 0;
        for (int n = 0; n<this.N; n++){
            number+= this.tripletsList[n].size();
        }
        return number;
	}

    public void printInstance() {
		System.out.println("N :  " + Integer.toString(this.N));
		System.out.println("M :  " + Integer.toString(this.M));
		System.out.println("K :  " + Integer.toString(this.K));
		System.out.println("P :  " + Integer.toString(this.P));

		for (int n = 0; n < this.N; n += 1) {
            ListIterator<Triplet> listIt = this.tripletsList[n].listIterator(0);
            while (listIt.hasNext()){
                Triplet t = listIt.next();
                System.out.println(t.toString());
            }
        }
    }

    public void toTxtFormat(String name) {
		// this will create a text.txt folder with the name given in argument.
		// only a matrix for a canal. (which format ??)
		PrintWriter writer;
		try {
			writer = new PrintWriter(name, "UTF-8");
            for (int n = 0; n<this.N; n++){
                writer.println("new channel"); // to differentiate between two channels.
                ListIterator<Triplet> listIt = this.tripletsList[n].listIterator(0);
                while (listIt.hasNext()){
                    Triplet t = listIt.next();
                    writer.println(t.p + " " + t.r );
                }
            }
            writer.close();
        }
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
    public static void main(String[] args) throws FileNotFoundException {
        Instance I1 = new Instance("Tests/test1.txt");
		I1.printInstance();
    }

	
	
}