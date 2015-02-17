package mcsn.pad;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import mcsn.pad.rmi.RemoteNode2Node;
import mcsn.pad.storage.Storage;

import org.junit.Test;

public class RemoteN2NTest {
	
	private Storage s;
	public RemoteN2NTest() {
		s=new Storage("/tmp/junit");
	}

	@Test
	public void testRemoteNode2Node() {
		RemoteNode2Node r= new RemoteNode2Node(0, 0, 0, s);
		assertTrue(r != null);
	}


	@Test
	public void testPutGetinStorage() {
		RemoteNode2Node r= new RemoteNode2Node(24, 54, 3, s);
		String message= "hello pad";
		String name= "pippo";
		
		try {
			r.put(name, message, "0v0v0v");
			r.get(name); //IM here!!!!!!!!!!!!!!!!!!!!!
			//assertEquals(,message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
