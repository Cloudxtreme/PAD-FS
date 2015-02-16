package mcsn.pad.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import mcsn.pad.Pair;
import mcsn.pad.storage.Storage;

public class RemoteNode2Node implements Node2Node {

	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	public RemoteNode2Node(int _myid, int _n, int _k, Storage _s) {
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
	}
	
	private boolean isReplica(int hash) {
		for(int i=hash+1; i<=hash+k; i=(i+1)%n )
			if (i==myid)
				return true;
		
		return false;
	}
	
	
	public void put(String key, Serializable value, String clocks, int from)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public Pair[] get(String key) throws RemoteException {
		
		int hash = key.hashCode() % n;
		if (hash == myid) {
			String[] allVersion = s.findAllinStorage(key);
			Pair[] output= new Pair[allVersion.length];
			int i=0;
			for (String v : allVersion ) {
				try {
					Serializable obj=s.readStorage(v);
					String clock=v.substring(v.indexOf('.'), v.length() -1);
					output[i]=new Pair(obj,clock);
					i++;
				} catch (Exception e) {
					throw new RemoteException("cannot read " + v + " into Storage");
				} 
			}
			return output;
		} else if(isReplica(hash)) {
			String[] allVersion = s.findAllinReplica(key);
			Pair[] output= new Pair[allVersion.length];
			int i=0;
			for (String v : allVersion ) {
				try {
					Serializable obj=s.readReplica(v);
					String clock=v.substring(v.indexOf('.'), v.length() -1);
					output[i]=new Pair(obj,clock);
					i++;
				} catch (Exception e) {
					throw new RemoteException("cannot read " + v + " into Storage");
				} 
			}
			return output;
			
		}
		
		return null;
	}

}
