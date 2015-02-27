package mcsn.pad;


import java.io.IOException;
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
import mcsn.pad.Utility;

public class Daemon {
	
	private int myid; // my id
	private int n; // number of peers
	private int k; // number of replicas
	private Storage s; //local storage
	private Hashtable<Integer,Node2Node> cacheN2N; //cache of remote object
	private Hashtable<Integer,FS> cacheFS; //cache of remote object
	private HashMap<Integer,String> peers; //mapping id to registry Url
	private Node2Node myN2N;
	
	
	public Daemon(int _myid, int _n, int _k, Storage _s, Hashtable<Integer,Node2Node> _n2n, Hashtable<Integer, FS> _fs, 
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

	
	
	
	
	
	
	public synchronized void FetchProcessing() {
		
		String[] toProc = s.getAllProcessing();
		// process all file
		
		for (String filename : toProc) {
			
			
			if( s.existInProcessing(filename)) {
				System.out.println("PAD-FS: DEAMON: processing"  +filename);
				
				if (Utility.isToDelete(filename)) {
					tryToDeleteAll(filename);
					return;
				}
				
				if (! Utility.hasClock(filename) )
					firstTimeProcessing(filename);
				else
					finishProcessing(filename);
			}
		}
	}

	
	
	
	private void tryToDeleteAll(String filename) {
		//ask to all node that can store the object
		String onlyName= filename.substring(0, filename.indexOf('.'));
		boolean synch_all=true;
		final int hash = Utility.getHash(onlyName, n);
		for (int i=hash; i!=(hash+k+1)%n; i=(i+1) %n ) {
			
			if (i == myid ) {
				
				String[] alltodelete = s.findAllinProcessing( onlyName);
				for (String delete : alltodelete) {
					if (!Utility.isToDelete(delete)) {
						s.deleteInProcessing(delete);
					}
				}
				
				alltodelete = s.findAllinStorage( onlyName);
				for (String delete : alltodelete) {
			
					s.deleteInStorage(delete);
					
				}
				
				alltodelete = s.findAllinReplica(onlyName);
				for (String delete : alltodelete) {
			
					s.deleteInReplica(delete);
					
				}
			}
			
			if (i != myid) {
				Node2Node remote=cacheN2N.get(i); 
				// i will  try to ask find the info in my node 
				if (remote != null )
					try {
						//i will try to reuse the object if the connection is up
						remote.deleteAllVersion(onlyName);
					} catch (RemoteException e)  {
						
					}
				
				
				//get the new object from rmi registry
				try {
						System.out.println("asking peers for " + i + " getting "+ peers.get(i));
						remote = (Node2Node) Naming.lookup(peers.get(i)+"/N2N");
						cacheN2N.put(new Integer(i),remote);
						remote.deleteAllVersion(onlyName);
				} catch (MalformedURLException e1) {
						synch_all=false;
						e1.printStackTrace();
				} catch (RemoteException e1) {
						synch_all=false;
						e1.printStackTrace();
				} catch (NotBoundException e1) {
						synch_all=false;
						e1.printStackTrace();
					}
			
			
			}
		}
		
		// i will delete on processing only if i finish the synch
		if (synch_all)
			s.deleteInProcessing(filename);
			
	
	
	}
		
	







	private void finishProcessing(String filename) {
		System.out.println("PAD-FS: DEAMON: finish processing"  +filename);
		String onlyName= filename.substring(0, filename.indexOf('.'));
		String clock=filename.substring( filename.indexOf('.')+ 1 , filename.length());
		int hash = Utility.getHash(onlyName,n);
		Serializable obj;
		try {
			obj= s.readProcessing(filename);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		} 
		
		
		synch(onlyName, clock, hash, obj);
		return;
		
		
	}



	private void firstTimeProcessing(String filename) {
		System.out.println("PAD-FS: DEAMON: first time processing"  +filename);
		int hash = Utility.getHash(filename,n);
		Serializable obj;
		try {
			obj= s.readProcessing(filename);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} 
		
		
		if( (hash != myid) && ! Utility.isReplica(hash,n, k, myid)) {
			// CASE1: IS NOT FOR ME
			tryToSendAway(filename, hash, obj);
		} else if (hash ==myid) {
			//CASE2: IS FOR MY STORAGE
			putInMyStorage(filename, obj); 
			
		} else {
			//CASE3: IS FOR MY  REPLICA
			putInMyReplica(filename, hash, obj); 
			
		}
		return;
	}

	
	private void synch(String onlyname, String clock, int hash,Serializable obj) {
		
			//ask to all node that can store the object
			boolean synch_all=true;
			for (int i=hash; i!=(hash+k+1)%n; i=(i+1) %n ) {
				if (i != myid) {
					Node2Node remote=cacheN2N.get(i); 
					// i will  try to ask find the info in my node 
					if (remote != null )
						try {
							//i will try to reuse the object if the connection is up
							remote.put(onlyname, obj, clock);
						} catch (RemoteException e)  {
							//like cache fault...
							//get the new object from rmi registry
							/*try {
								remote = (Node2Node) Naming.lookup(peers.get(i)+"/N2N");
								cacheN2N.put(new Integer(i),remote);
							} catch (MalformedURLException e1) {
								synch_all=false;
								e1.printStackTrace();
							} catch (RemoteException e1) {
								synch_all=false;
								e1.printStackTrace();
							} catch (NotBoundException e1) {
								synch_all=false;
								e1.printStackTrace();
							}*/
						
							//TODO replica the update!!
						}
					
					
					//get the new object from rmi registry
					try {
							System.out.println("asking peers for " + i + " getting "+ peers.get(i));
							remote = (Node2Node) Naming.lookup(peers.get(i)+"/N2N");
							cacheN2N.put(new Integer(i),remote);
							remote.put(onlyname, obj,clock);
					} catch (MalformedURLException e1) {
							synch_all=false;
							e1.printStackTrace();
					} catch (RemoteException e1) {
							synch_all=false;
							e1.printStackTrace();
					} catch (NotBoundException e1) {
							synch_all=false;
							e1.printStackTrace();
						}
					
				
				
					/*try {
						//i will try to recall the method on the new retrieved object
						remote.put(onlyname, obj,clock);
					} catch (RemoteException e)  {
						synch_all=false;
					//no thing to do, we will try to the next candidate
					}*/
				
				}
			}
			
			// i will delete on processing only if i finish the synch
			if (synch_all)
				s.deleteInProcessing(onlyname + "." + clock);
				
		
		
	}
	
	
	
	private void putInMyReplica(String filename, int hash, Serializable obj) {
		try {
			//FIXME select max of my id
			String[] all=s.findAllinReplica(filename);
			String newClocks;
			if (all.length == 0) {
				//case new insertion;
				
				
				int idx=1;
				for (int x=(hash+1) %n; x!=myid; x=(x+1)%n) {
					idx++;
				}
				String c="";
				for (int z=0; z<idx; z++ )
					c+="0v";
				c+="1v";
				for (int z=idx; z<k; z++ )
					c+="0v";   //NOTE: will create a lot of garbage for large k, but normally k is small
				myN2N.put(filename, obj, c);
				newClocks=c;
				
			} else {
				//FIXME update only the first clock...
				int[] vc=Utility.getClock(all[0].substring(all[0].indexOf('.') +1, all[0].length()),k);
				int idx=1;
				for (int x=(hash+1) %n; x!=myid; x=(x+1)%n) {
					idx++;
				}
				vc[idx]++;
				newClocks=Utility.ClockToString(vc);
				myN2N.put(filename, obj, newClocks);
				s.deleteInReplica(all[0]);
				all=null;
				vc=null;
			}
			//TODO IF THE FILE IS PRESENT IN PROCESSING WITH A CLOCK, DELETE THE FILE WITH CLOCK, IF COMPATIBLE
			
			String[] tosynchall = s.findAllinProcessing(filename);
			s.writeProcessing(filename + "." + newClocks,  obj);
			for ( String deleting : tosynchall ) 
				s.deleteInProcessing(deleting);
			
			synch(filename, newClocks, hash, obj);
			
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void putInMyStorage(String filename, Serializable obj) {
		try {
			String[] all=s.findAllinStorage(filename);
			String newClocks;
			if (all.length == 0) {
				//case new insertion;
				String c = "1v";
				for (int z=0; z<k; z++ )
					c+="0v";   //NOTE: will create a lot of garbage for large k, but normally k is small
				myN2N.put(filename, obj, c);
				newClocks=c;
				
			} else {
				//FIXME update only the first clock...
				int[] vc=Utility.getClock(all[0].substring(all[0].indexOf('.') +1, all[0].length()),k);
				vc[0]++;
				newClocks=Utility.ClockToString(vc);
				myN2N.put(filename, obj,newClocks );
				s.deleteInStorage(all[0]);
				
			}
			//TODO IF THE FILE IS PRESENT IN PROCESSING WITH A CLOCK, DELETE THE FILE WITH CLOCK, IF COMPATIBLE
			String[] tosynchall = s.findAllinProcessing(filename);
			
			s.writeProcessing(filename + "." + newClocks,  obj); //uptade processing with clocks, will be removed after all replica update
			//String[] tosynchall = s.findAllinProcessing(filename);
			for ( String deleting : tosynchall ) 
				s.deleteInProcessing(deleting);
			
			synch(filename, newClocks, Utility.getHash(filename, n), obj);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void tryToSendAway(String filename, int hash,
			Serializable obj) {
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
					/*try {
						remote = (FS) Naming.lookup(peers.get(i)+"/FS");
						cacheFS.put(new Integer(i),remote);
					} catch (MalformedURLException e1) {
						
						e1.printStackTrace();
					} catch (RemoteException e1) {
						
						e1.printStackTrace();
					} catch (NotBoundException e1) {
						
						e1.printStackTrace();
					}*/
					
					//TODO replica the update!!
				}
			 //get the new object from rmi registry
				try {
					System.out.println("asking peers for " + i + " getting "+ peers.get(i));
					remote = (FS) Naming.lookup(peers.get(i)+"/FS");
					cacheFS.put(new Integer(i),remote);
					remote.put(filename, obj);
					s.deleteInProcessing(filename);
					return;
				} catch (MalformedURLException e1) {
					
					e1.printStackTrace();
				} catch (RemoteException e1) {
					
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					
					e1.printStackTrace();
				}
			
			
			/*try {
				//i will try to recall the method on the new retrieved object
				remote.put(filename, obj);
				s.deleteInProcessing(filename);
				return;
			} catch (RemoteException e)  {
				
				//no thing to do, we will try to the next candidate
			}*/
			
		}
	}

	
	
}
