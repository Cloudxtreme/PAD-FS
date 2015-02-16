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
	private Node2Node n2n;
	private HashMap<Integer,String> peers;
	
	public RemoteFS(int _myid, int _n, int _k, Storage _s, Node2Node _n2n, HashMap<Integer,String> p) {
		n=_n;
		k=_k;
		myid=_myid;
		n2n=_n2n;
		s=_s;
		peers=p;
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