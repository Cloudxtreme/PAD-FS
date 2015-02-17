package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

import mcsn.pad.storage.Storage;

public class RemoteFS implements FS {

	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	private HashMap<Integer,Node2Node> cacheN2N; //cache of remote object
	private HashMap<Integer,String> peers; //mapping id to registry Url
	
	
	public RemoteFS(int _myid, int _n, int _k, Storage _s, HashMap<Integer,Node2Node> _n2n, HashMap<Integer,String> p) {
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
		peers=p;
		cacheN2N=_n2n; //FIXME if is used only here, it is useless to pass as argument into constructor
	}
	
	private boolean isReplica(int hash) {
		for(int i=hash+1; i<=hash+k; i=(i+1)%n )
			if (i==myid)
				return true;
		
		return false;
	}
	
	public void put(String key, Serializable value) throws RemoteException {
		/*int hash = key.hashCode() % n;
		
		
		if (hash == myid) 
			try {
				s.writeStorage(key, value);
				
			} catch (IOException e) {
				
				throw new RemoteException("cannot write into storage");
				
			}
		else if (isReplica(hash)){
			
					try {
						s.writeReplica(key, value);
						
					} catch (IOException e) {
						throw new RemoteException("cannot write into replica");
					}
					return;
				
			}
		*/
		
		try {
			s.writeProcessing(key, value);
			//TODO signal to DEAMON
		} catch (IOException e) {
			throw new RemoteException("cannot write into processing");
		}
		
		
		
	}

	public Serializable[] get(String key) throws RemoteException {
		
			
		return null;
		
	}

}
