package mcsn.pad;

import java.io.Serializable;
import java.rmi.RemoteException;

/*
 * Modeling method for node 2 node communication
 * 
 */
public interface Node2Node {
	
	void put(long key, Serializable value) throws RemoteException;
	Serializable get(long key) throws RemoteException;
	
	void store_replica(long key, Serializable value) throws RemoteException;
	Serializable get_replica(long key) throws RemoteException;
	
	boolean heartBeat() throws RemoteException;
}
