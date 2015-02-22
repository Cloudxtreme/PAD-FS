package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;

import mcsn.pad.Deamon;
import mcsn.pad.DeamonThread;
import mcsn.pad.Pair;
import mcsn.pad.storage.Storage;

public class RemoteFS extends UnicastRemoteObject implements FS {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	private Hashtable<Integer,Node2Node> cacheN2N; //cache of remote object
	private HashMap<Integer,String> peers; //mapping id to registry Url
	private Node2Node myN2N;
	private Deamon d;
	
	public RemoteFS(int _myid, int _n, int _k, Storage _s, Hashtable<Integer,Node2Node> _n2n, HashMap<Integer,String> p
			,Deamon _d) throws RemoteException {
		super();
		d=_d;
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
		peers=p;
		cacheN2N=_n2n; //FIXME if is used only here, it is useless to pass as argument into constructor
		myN2N=cacheN2N.get(myid);
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
	
	public void put(String key, Serializable value) throws RemoteException {
		
		try {
			s.writeProcessing(key, value); //can overwrite but it is ok!
			new DeamonThread(d, false).start();
		} catch (IOException e) {
			throw new RemoteException("cannot write into processing");
		}
		
		
		
	}

	public Serializable[] get(String key) throws RemoteException {
		int hash = key.hashCode() % n;
		
		if (hash== myid || isReplica(hash)) {
			// i will find the info in my node
			
			Pair[] found = myN2N.get(key);
			
			Serializable[] output= new Serializable[found.length];
			for (int i=0; i< found.length; i++) {
				output[i]=found[i].getLeft();
			}
			
			return output;
		} else {
			for (int i=hash; i<=hash+k; i++ ) {
				Node2Node remote=cacheN2N.get(i);
				Pair[] found;
				Serializable[] output;
				
				// i will  try to ask find the info in my node
				try {
					//i will try to reuse the object if the connection is up
					found = remote.get(key);
					output= new Serializable[found.length];
					for (int j=0; j< found.length; j++) {
						output[j]=found[j].getLeft();
					}
					return output;
					
				} catch (RemoteException e)  {
					//like cache fault...
					//TODO get the new object from rmi registry
					peers.get(5);
				}
				
				
				try {
					//i will try to recall the method on the new retrieved object
					found = remote.get(key);
					output= new Serializable[found.length];
					for (int j=0; j< found.length; j++) {
						output[j]=found[j].getLeft();
					}
					return output;
				} catch (RemoteException e)  {
					
					//no thing to do, we will try to the next candidate
				}
				
			}
		}
		
		
		return null;
		
	}

}
