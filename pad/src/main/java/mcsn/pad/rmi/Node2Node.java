package mcsn.pad.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import mcsn.pad.Pair;

/*
 * Modeling method for node 2 node communication
 * 
 */
public interface Node2Node extends java.rmi.Remote {
	/*like client api, plus info on the vector clock*/
	public void put(String key, Serializable value, String clocks) throws RemoteException;
	public Pair[] get(String key) throws RemoteException;
	public void deleteAllVersion(String filename) throws RemoteException;
	

}
