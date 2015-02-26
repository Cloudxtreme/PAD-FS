package mcsn.pad.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;

import mcsn.pad.Deamon;
import mcsn.pad.DeamonThread;
import mcsn.pad.Pair;
import mcsn.pad.Utility;
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
		super(6000+_myid);
		d=_d;
		n=_n;
		k=_k;
		myid=_myid;
		s=_s;
		peers=p;
		cacheN2N=_n2n; //FIXME if is used only here, it is useless to pass as argument into constructor
		myN2N=cacheN2N.get(myid);
	}
	
	
	
	
	public void put(String key, Serializable value) throws RemoteException {
		
		System.out.println("PAD-FS: received FS.put of " + key);
		
		try {
			s.writeProcessing(key, value); //can overwrite but it is ok!
			new DeamonThread(d, false).start();
		} catch (IOException e) {
			throw new RemoteException("cannot write into processing");
		}
		
		
		
	}

	public Serializable[] get(String key) throws RemoteException {
		System.out.println("PAD-FS: received FS.get of " + key);
		
		int hash = Utility.getHash(key, n);
		
		if (hash== myid || Utility.isReplica(hash,n,k,myid)) {
			// i will find the info in my node
			System.out.println("PAD-FS: asking " + key + " to local N2N");
			Pair[] found = myN2N.get(key);
			
			Serializable[] output= new Serializable[found.length];
			for (int i=0; i< found.length; i++) {
				output[i]=found[i].getLeft();
			}
			
			System.out.println("PAD-FS: FS.get: return "+ output.length + " values for " + key);
			return output;
		} else {
			//error!!!
			for (int i=hash; i!= (hash+k +1) %n; i=(i+1) %n ) {
				System.out.println("PAD-FS: FS.get: trying to ask to "+ i + " responsable for " + key);
				Node2Node remote=cacheN2N.get(i);
				Pair[] found;
				Serializable[] output;
				
				if (remote != null)// i will  try to ask find the info in my node
					try {
						System.out.println("PAD-FS: remote object is in cache, trying to reuse");
						//i will try to reuse the object if the connection is up
						found = remote.get(key);
						output= new Serializable[found.length];
						for (int j=0; j< found.length; j++) {
							output[j]=found[j].getLeft();
						}
						System.out.println("PAD-FS: FS.get: return "+ output.length + " values for " + key + ", info by " + i);
						return output;
					
					} catch (RemoteException e)  {
					//like cache fault...
					// get the new object from rmi registry 
					}
				
				System.out.println("PAD-FS: remote object is not in cache");
				try {
					System.out.println("PAD-FS: getting new remote object from " + peers.get(i) + "/N2N");
					remote = (Node2Node) Naming.lookup(peers.get(i)+"/N2N");
					cacheN2N.put(new Integer(i),remote);
					found = remote.get(key);
					output= new Serializable[found.length];
					for (int j=0; j< found.length; j++) {
						output[j]=found[j].getLeft();
					}
					System.out.println("PAD-FS: FS.get: return "+ output.length + " values for " + key + ", info by " + i);
					return output;
				} catch (RemoteException e1) {
					e1.printStackTrace();
						//return null;
				}	catch (MalformedURLException e1) {
					
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//return null;
				} catch (NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//return null;
				}
				
				
				
				/*try {
					//i will try to recall the method on the new retrieved object
					found = remote.get(key);
					output= new Serializable[found.length];
					for (int j=0; j< found.length; j++) {
						output[j]=found[j].getLeft();
					}
					System.out.println("PAD-FS: FS.get: return "+ output.length + " values for " + key + ", info by " + i);
					return output;
				} catch (RemoteException e)  {
					System.out.println("PAD-FS: cannot use remote object of node " + i);
					//no thing to do, we will try to the next candidate
				}*/
				
			}
		}
		
		
		return null;
		
	}

}
