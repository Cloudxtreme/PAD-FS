package mcsn.pad;

import java.rmi.*;
import java.io.*;
		
public interface FS extends java.rmi.Remote {
			/* CLIENT API  */
			void put(long key, Serializable value) throws RemoteException;
			Serializable get(long key) throws RemoteException;
}