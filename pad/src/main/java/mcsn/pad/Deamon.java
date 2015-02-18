package mcsn.pad;

import java.io.IOException;
import java.io.Serializable;
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

	//FIXME not working well
	private boolean isReplica(int hash) {
		for(int i=hash+1; i<= hash+k; i=(i+1)%n )
				if (i==myid)
					return true;
			
			return false;
		}
	
	
	public synchronized void FetchProcessing() {
		String[] toProc = s.getAllProcessing();
		for (String filename : toProc) {
			int hash=filename.hashCode() % n;
			Serializable obj;
			if( (hash != myid) && !isReplica(hash)) {
			
				for (int i=hash; i<=hash+k; i++ ) {
					FS remote=cacheFS.get(i);
					try {
						obj= s.readProcessing(filename);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					} 
					// i will  try to ask find the info in my node
					try {
						//i will try to reuse the object if the connection is up
						remote.put(filename, obj);
					} catch (RemoteException e)  {
						//like cache fault...
						//TODO get the new object from rmi registry
						peers.get(5);
					}
					
					
					try {
						//i will try to recall the method on the new retrieved object
						remote.put(filename, obj);
					} catch (RemoteException e)  {
						
						//no thing to do, we will try to the next candidate
					}
					
				}
				
			}
		}
	}
	
}
