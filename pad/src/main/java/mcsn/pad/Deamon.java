package mcsn.pad;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

import mcsn.pad.rmi.FS;
import mcsn.pad.rmi.Node2Node;
import mcsn.pad.storage.Storage;

public class Deamon {
	
	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	private Hashtable<Integer,Node2Node> cacheN2N; //cache of remote object
	private Hashtable<Integer,FS> cacheFS; //cache of remote object
	private HashMap<Integer,String> peers; //mapping id to registry Url
	private Node2Node myN2N;
	
	public Deamon(int _myid, int _n, int _k, Storage _s, Hashtable<Integer,Node2Node> _n2n, Hashtable<Integer, FS> _fs, 
			HashMap<Integer,String> p) {
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
		peers=p;
		cacheN2N=_n2n; 
		myN2N=cacheN2N.get(myid);
		cacheFS=_fs;
	}

	private boolean isReplica(int hash) {
		int h1;
		for(int i=0; i<k; i++ ) {
			h1=(hash + 1 + i) % n;
			if (h1==myid)
				return true;
		}
		return false;
	}
	
	private int[] getClock(String clocks) {
		
		int[] vc = new int[k+1];
		for(int i=0; i<k+1; i++) {
			
			vc[i]= Integer.parseInt(clocks.substring(0, clocks.indexOf('v')));
			
			clocks=clocks.substring(clocks.indexOf('v')+1, clocks.length());
		}
		return vc;
	}
	
	public synchronized void FetchProcessing() {
		String[] toProc = s.getAllProcessing();
		// process all file
		for (String filename : toProc) {
			int hash=filename.hashCode() % n;
			if (hash < 0)
			    hash += n;
			Serializable obj;
			
			try {
				obj= s.readProcessing(filename);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			} 
			
			if( (hash != myid) && !isReplica(hash)) {
			
				//ask to all node that can store the object
				for (int i=hash; i!=(hash+k+1)%n; i=(i+1) %n ) {
					FS remote=cacheFS.get(i); //FIXME if is null download a new object
					
					// i will  try to ask find the info in my node 
					if (remote != null)
						try {
							//i will try to reuse the object if the connection is up
							remote.put(filename, obj);
							s.deleteInProcessing(filename);
							return;
						} catch (RemoteException e)  {
							//like cache fault...
							//get the new object from rmi registry
							try {
								remote = (FS) Naming.lookup(peers.get(i)+"/FS");
								cacheFS.put(new Integer(i),remote);
							} catch (MalformedURLException e1) {
								
								e1.printStackTrace();
							} catch (RemoteException e1) {
								
								e1.printStackTrace();
							} catch (NotBoundException e1) {
								
								e1.printStackTrace();
							}
							
							//TODO replica the update!!
						}
					else //get the new object from rmi registry
						try {
							System.out.println("asking peers for " + i + " getting "+ peers.get(i));
							remote = (FS) Naming.lookup(peers.get(i)+"/FS");
							cacheFS.put(new Integer(i),remote);
						} catch (MalformedURLException e1) {
							
							e1.printStackTrace();
						} catch (RemoteException e1) {
							
							e1.printStackTrace();
						} catch (NotBoundException e1) {
							
							e1.printStackTrace();
						}
					
					
					try {
						//i will try to recall the method on the new retrieved object
						remote.put(filename, obj);
						s.deleteInProcessing(filename);
						return;
					} catch (RemoteException e)  {
						
						//no thing to do, we will try to the next candidate
					}
					
				}
				
			} else if (hash ==myid) {
				try {
					String[] all=s.findAllinStorage(filename);
					if (all.length == 0) {
						//case new insertion;
						String c = "1v";
						for (int z=0; z<k; z++ )
							c+="0v";   //NOTE: will create a lot of garbage for large k, but normally k is small
						myN2N.put(filename, obj, c);
						
					} else {
						//FIXME update only the first clock...
						int[] vc=getClock(all[0].substring(all[0].indexOf('.') +1, all[0].length()));
						vc[0]++;
						myN2N.put(filename, obj, ClockToString(vc));
						s.deleteInStorage(all[0]);
						all=null;
						vc=null;
					}
					
					s.deleteInProcessing(filename);
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} 
				
				
			} else {
				//replica case
				try {
					String[] all=s.findAllinReplica(filename);
					if (all.length == 0) {
						//case new insertion;
						//TODO fix as the isReplica Function!!!
						String c="";
						for (int z=hash; z<k; z++ )
							c+="0v";
						for (int z=0; z<k; z++ )
							c+="0v";   //NOTE: will create a lot of garbage for large k, but normally k is small
						myN2N.put(filename, obj, c);
						
					} else {
						//FIXME update only the first clock...
						int[] vc=getClock(all[0].substring(all[0].indexOf('.') +1, all[0].length()));
						int idx=1;
						for (int x=(hash+1) %n; x!=myid; x=(x+1)%n) {
							idx++;
						}
						vc[idx]++;
						myN2N.put(filename, obj, ClockToString(vc));
						s.deleteInReplica(all[0]);
						all=null;
						vc=null;
					}
					
					s.deleteInProcessing(filename);
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} 
				
			}
			
			
		}
	}

	private String ClockToString(int[] vc) {
		String c="";
		for (int i : vc) {
			c+= new Integer(i).toString() + "v"; //BAD: LOT OF GARBAGE
		}
		return c;
	}
	
}
