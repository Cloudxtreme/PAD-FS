/**
 * 
 */
package mcsn.pad;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;

import mcsn.pad.rmi.*;
import mcsn.pad.storage.*;

import org.junit.Test;

/**
 * @author dariobalinzo
 *
 */
public class TestRemoteFS {

	/**
	 * Test method for {@link mcsn.pad.rmi.RemoteFS#RemoteFS(int, int, int, mcsn.pad.storage.Storage, java.util.HashMap, java.util.HashMap)}.
	 */
	@Test
	public void testRemoteFS() {
		
		try {
			Storage s= new Storage("/tmp/junit");
			Hashtable<Integer,Node2Node> t= new Hashtable<Integer,Node2Node>();
			t.put(24, new RemoteNode2Node(24, 54, 3, s));
			RemoteFS rfs;
			rfs = new RemoteFS(24, 54, 3, s, t, null);
			assertTrue(rfs!=null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Test method for {@link mcsn.pad.rmi.RemoteFS#put(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testPut() {
		try {
			Storage s= new Storage("/tmp/junit");
			Hashtable<Integer,Node2Node> t= new Hashtable<Integer,Node2Node>();
			t.put(24, new RemoteNode2Node(24, 54, 3, s));
			RemoteFS rfs= new RemoteFS(24, 54, 3, s, t, null);
			assertTrue(rfs!=null);
			String message= "hello pad";
			String name= "pippo";
		
			rfs.put(name, message);
			assertTrue(s.getAllProcessing().length == 1 && s.getAllProcessing()[0].equals(name));
			String x=s.getAllProcessing()[0];
			assertEquals(s.readProcessing(x), message);
			s.deleteInProcessing(name);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Test method for {@link mcsn.pad.rmi.RemoteFS#get(java.lang.String)}.
	 */
	@Test
	public void testGetFromStorage() {
		
		try {
			Storage s= new Storage("/tmp/junit");
			Hashtable<Integer,Node2Node> t= new Hashtable<Integer,Node2Node>();
			t.put(24, new RemoteNode2Node(24, 54, 3, s));
			RemoteFS rfs= new RemoteFS(24, 54, 3, s, t, null);
			assertTrue(rfs!=null);
			String message= "hello pad";
			String name= "pippo";
		
		
			//emulating deamon
			s.writeStorage(name + "1v0v0v0v", message);
			
			Serializable[] out=  rfs.get(name);
			System.out.println(out.length);
			String[] todelete =s.findAllinStorage(name);
			for (String deleting :todelete)
				s.deleteInStorage(deleting);
			
			
			assertTrue(out.length==1);
			assertEquals((String )out[0], message);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
@Test
public void testGetFromReplica() {
		
	try {
			Storage s= new Storage("/tmp/junit");
			Hashtable<Integer,Node2Node> t= new Hashtable<Integer,Node2Node>();
			t.put(25, new RemoteNode2Node(25, 54, 3, s));
			RemoteFS rfs= new RemoteFS(25, 54, 3, s, t, null);
			assertTrue(rfs!=null);
			String message= "hello pad";
			String name= "pippo";
		
		
			//emulating deamon
			s.writeReplica(name, message);
			
			Serializable[] out=  rfs.get(name);
			System.out.println(out.length);
			String[] todelete =s.findAllinReplica(name);
			for (String deleting :todelete)
				s.deleteInReplica(deleting);
			
			
			assertTrue(out.length==1);
			assertEquals((String )out[0], message);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
