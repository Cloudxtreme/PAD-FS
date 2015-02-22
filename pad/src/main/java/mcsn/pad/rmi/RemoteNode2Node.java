package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import mcsn.pad.Pair;
import mcsn.pad.storage.Storage;

public class RemoteNode2Node extends UnicastRemoteObject implements Node2Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	
	public RemoteNode2Node(int _myid, int _n, int _k, Storage _s) throws RemoteException {
		super();
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
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
	
	/*
	 *	
	 * 
	 */
	
	public void put(String key, Serializable value, String clocks)
			throws RemoteException {
		
		final int hash = key.hashCode() % n;
		
		//check if the file is really for me
		if ((hash != myid)&&(!isReplica(hash)))
			throw new RemoteException("Requested key " + key + " is not relative on this node"); 
		
		
		int[] vc=getClock(clocks);
		
		
		if (myid== hash) {
			
			String[] localclocks=s.findAllinStorage(key);
			System.out.println("#found " + localclocks.length);
			if(localclocks.length==0)  {
				
				try {
					System.out.println("PAD-FS: first time writing" + key);
					s.writeStorage(key + "." + clocks, value);
				} catch (IOException e) {
					throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
				}
				return;
			}
			
			
			for (String local: localclocks) {
				int[] vc_local = getClock(local.substring(local.indexOf('.') +1, local.length()));
				System.out.println(compatibleClocks(vc_local, vc));
				if (compatibleClocks(vc_local, vc)) {
					try {
						System.out.println("PAD-FS: Updated Value of " + key);
						s.writeStorage(key + "." + clocks, value);
						s.deleteInStorage(local);
					} catch (IOException e) {
						throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
					}
					return;
				} 
			}
			
			//insert also if the clock are not compatibles, but not deleting old versions
			try {
				System.out.println("PAD-FS: Added Conflicting Value of" + key);
				s.writeStorage(key + "." + clocks, value);
			} catch (IOException e) {
				throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
			}
			
		}
		
		
		if (isReplica(hash)) {
			String[] localclocks=s.findAllinReplica(key);
			
			if(localclocks.length==0)  {
				try {
					System.out.println("PAD-FS: Replica first time writing" + key);
					s.writeReplica(key + "." + clocks, value);
				} catch (IOException e) {
					throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
				}
				return;
			}
			
			for (String local: localclocks) {
				int[] vc_local = getClock(local.substring(local.indexOf('.') +1, local.length()));
				if (compatibleClocks(vc_local, vc)) {
					try {
						System.out.println("PAD-FS: Replica  update value of " + key);
						s.writeReplica(key + "." + clocks, value);
						s.deleteInReplica(local);
					} catch (IOException e) {
						throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
					}
					return;
				}
			}
			
			
			//insert also if the clock are not compatibles, but not deleting old versions
			try {
				System.out.println("PAD-FS:Replica added Conflicting Value of" + key);
				s.writeReplica(key + "." + clocks, value);
			} catch (IOException e) {
				throw new RemoteException("cannot update file " + key + " with new clock " + clocks );
			}
			
		}
	}

	/* returns true only if vc is a new compatible version of local_vc*/
	private boolean compatibleClocks(int[] vc_local, int[] vc) {
	
		for (int i=0; i<k+1; i++) {
			if (vc_local[i] > vc[i] )
				return false;
		}
		return true;
	}

	/*
	 * 	get(key) will return all versions of the file indexed by key, if it is present in this node 
	 * 	otherwise return null;
	 * */
	public Pair[] get(String key) throws RemoteException {
		
		int hash = key.hashCode() % n;
		
		if (hash == myid) {
			String[] allVersion = s.findAllinStorage(key);
			Pair[] output= new Pair[allVersion.length];
			int i=0;
			
			for (String v : allVersion ) {
				try {
					//v is the name of the file with in append the vector clock
					Serializable obj=s.readStorage(v);
					String clock=v.substring(v.indexOf('.') +1, v.length());
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
					String clock=v.substring(v.indexOf('.') +1, v.length());
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
