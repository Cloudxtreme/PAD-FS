package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import mcsn.pad.Pair;
import mcsn.pad.Utility;
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
	
	
	
	
	
	
	/*
	 *	
	 * 
	 */
	
	public void put(String key, Serializable value, String clocks)
			throws RemoteException {
		System.out.println("PAD-FS: received N2N.put of " + key + "." + clocks);
		int hash = Utility.getHash(key, n);
		
		//check if the file is really for me
		if ((hash != myid)&&(! Utility.isReplica(hash,n,k,myid)))
			throw new RemoteException("Requested key " + key + " is not relative on this node"); 
		
		
		int[] vc=Utility.getClock(clocks,k);
		
		
		if (myid== hash) {
			
			String[] localclocks=s.findAllinStorage(key);
			//System.out.println("#found " + localclocks.length);
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
				int[] vc_local = Utility.getClock(local.substring(local.indexOf('.') +1, local.length()),k);
				//System.out.println(Utility.compatibleClocks(vc_local, vc,k));
				
				if ( Utility.compatibleClocks(vc_local, vc,k)) {
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
		
		
		if (Utility.isReplica(hash,n,k,myid)) {
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
				int[] vc_local = Utility.getClock(local.substring(local.indexOf('.') +1, local.length()),k);
				if (Utility.compatibleClocks(vc_local, vc, k)) {
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

	

	/*
	 * 	get(key) will return all versions of the file indexed by key, if it is present in this node 
	 * 	otherwise return null;
	 * */
	public Pair[] get(String key) throws RemoteException {
		
		System.out.println("PAD-FS: received N2N.get of " + key);
		
		int hash = Utility.getHash(key, n);
		
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
			System.out.println("PAD-FS: N2N.get: return "+ output.length + " values for " + key );
			return output;
			
		} else if(Utility.isReplica(hash,n,k,myid)) {
			
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
			System.out.println("PAD-FS: N2N.get: (from replica) return "+ output.length + " values for " + key );
			return output;
			
		}
		
		return null;
	}






	@Override
	public void deleteAllVersion(String filename) throws RemoteException {
		String[] alltodelete = s.findAllinProcessing(filename);
		for (String delete : alltodelete) {
			if (!Utility.isToDelete(delete)) {
				s.deleteInProcessing(delete);
			}
		}
		
		alltodelete = s.findAllinStorage(filename);
		for (String delete : alltodelete) {
	
			s.deleteInStorage(delete);
			
		}
		
		alltodelete = s.findAllinReplica(filename);
		for (String delete : alltodelete) {
	
			s.deleteInReplica(delete);
			
		}
		
	}



}
