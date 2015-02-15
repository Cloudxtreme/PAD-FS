package mcsn.pad;

import java.io.Serializable;
import java.rmi.RemoteException;

/*
 * Modeling method for node 2 node communication
 * 
 */
public interface Node2Node {
	/*like client api, plus info on the vector clock*/
	void put(String key, Serializable value, String clocks) throws RemoteException;
	Pair<Serializable,String> get(String key) throws RemoteException;
	
}
