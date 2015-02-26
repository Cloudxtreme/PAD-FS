package mcsn.pad;

public class Utility {
	
	
	
	public static boolean isToDelete(String namefile) {
		int separator=namefile.indexOf('.');
		if (separator != -1)  {
			return namefile.substring(separator +1, namefile.length()).equals("deleting");
		}
		return false;
	}
	
	
	/* returns true only if vc is a new compatible version of local_vc*/
	public static boolean compatibleClocks(int[] vc_local, int[] vc, int k) {
		boolean find=false;
	
		for (int i=0; i<k+1; i++) {
			if (vc_local[i] > vc[i] )
				return false;
		}
		
		for (int i=0; i<k+1; i++) {
			if (vc_local[i] != vc[i] ) {
				find=true;
				break;
			}	
		}
		
		if(!find)
			return false;
		
		return true;
	}
	
	
	public static int[] getClock(String clocks, int k) {
		
		
		
		int[] vc = new int[k+1];
		for(int i=0; i<k+1; i++) {
			
			vc[i]= Integer.parseInt(clocks.substring(0, clocks.indexOf('v')));
			
			clocks=clocks.substring(clocks.indexOf('v')+1, clocks.length());
		}
		return vc;
	}

	public static int getHash(String filename, int n) {
		int hash=filename.hashCode() % n;
		if (hash < 0)
		    hash += n;
		return hash;
	}

	
	public static boolean hasClock(String namefile) {
		int dot=namefile.indexOf('.');
		return (dot != -1);
	}
	
	
	public static String ClockToString(int[] vc) {
		String c="";
		for (int i : vc) {
			c+= new Integer(i).toString() + "v"; //BAD: LOT OF GARBAGE
		}
		return c;
	}
	
	public static boolean isReplica(int hash, int n, int k, int myid) {
		int h1;
		for(int i=0; i<k; i++ ) {
			h1=(hash + 1 + i) % n;
			if (h1==myid)
				return true;
		}
		return false;
	}
}
