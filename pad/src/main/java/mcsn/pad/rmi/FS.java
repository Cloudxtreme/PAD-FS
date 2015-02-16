package mcsn.pad.rmi;

import java.rmi.*;
import java.io.*;
		
public interface FS extends java.rmi.Remote {
			/* CLIENT API  */
			void put(String key, Serializable value) throws RemoteException;
			Serializable[] get(String key) throws RemoteException;
}