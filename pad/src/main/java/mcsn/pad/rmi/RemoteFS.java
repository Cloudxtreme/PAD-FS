package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

import mcsn.pad.storage.Storage;

public class RemoteFS implements FS {

	int myid; // my id
	int n; // number of peers
	int k; // number of replicas
	Storage s; //local storage
	Node2Node n2n;
	public RemoteFS(int _myid, int _n, int _k, Storage _s, Node2Node _n2n) {
		n=_n;
		k=_k;
		myid=_myid;
		n2n=_n2n;
		s=_s;
	}
	
	private boolean isReplica(int hash) {
		for(int i=hash+1; i<=hash+k; i=(i+1)%n )
			if (i==myid)
				return true;
		
		return false;
	}
	
	public void put(String key, Serializable value) throws RemoteException {
		int hash = key.hashCode() % n;
		
		
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
		
		
		try {
			s.writeProcessing(key, value);
			//TODO signal to DEAMON
		} catch (IOException e) {
			throw new RemoteException("cannot write into processing");
		}
		
		
		
	}

	public Serializable[] get(String key) throws RemoteException {
		int hash = key.hashCode() % n;
		if (hash == myid) {
			String[] allVersion = s.findAllinStorage(key);
			Serializable[] output= new Serializable[allVersion.length];
			int i=0;
			for (String v : allVersion ) {
				try {
					output[i]=s.readStorage(v);
					i++;
				} catch (Exception e) {
					throw new RemoteException("cannot read " + v + " into Storage");
				} 
			}
			return output;
		} else if(isReplica(hash)) {
			
		}
			
		return null;
		
	}

}
