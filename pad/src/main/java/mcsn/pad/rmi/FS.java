package mcsn.pad.rmi;

import java.rmi.*;
import java.io.*;
		
public interface FS extends java.rmi.Remote {
			/* CLIENT API  */
			void put(String key, Serializable value) throws RemoteException;
			
			/*	Recursive version of get:
			 *	if the file is not found locally
			 * 	other nodes are recursively called
			 * */
			Serializable[] get(String key) throws RemoteException;
			
			
			
			public void deleteAllVersion(String key) throws RemoteException;
			
			public boolean deletingAllCompleted(String key) throws RemoteException;
			
}