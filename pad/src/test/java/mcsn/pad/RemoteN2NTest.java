package mcsn.pad;

import static org.junit.Assert.*;

import java.io.File;
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
	public void testPutStorage() {
		RemoteNode2Node r= new RemoteNode2Node(24, 54, 3, s);
		String message= "hello pad";
		String name= "pippo";
		File file = new File("/tmp/junit/Storage/pippo.1v0v0v0v");
		
		try {
			r.put(name, message, "1v0v0v0v");
			assertTrue(file.exists());
			r.put(name, message, "1v1v0v0v");
			assertTrue(!file.exists()); //deleted old version
			file = new File("/tmp/junit/Storage/pippo.1v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "2v1v0v0v");
			file = new File("/tmp/junit/Storage/pippo.2v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "1v1v0v1v");
			assertTrue(s.findAllinStorage(name).length == 2);
			s.deleteInStorage(name +".2v1v0v0v" );
			s.deleteInStorage(name +".1v1v0v1v" );
			
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("ho finito il test");
		
	}
	
	
	@Test
	public void testPutReplica() {
		RemoteNode2Node r= new RemoteNode2Node(25, 54, 3, s);
		String message= "hello pad";
		String name= "pippo";
		File file = new File("/tmp/junit/Replica/pippo.1v0v0v0v");
		
		try {
			r.put(name, message, "1v0v0v0v");
			assertTrue(file.exists());
			
			r.put(name, message, "1v1v0v0v");
			assertTrue(!file.exists()); //deleted old version
			
			file = new File("/tmp/junit/Replica/pippo.1v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			
			r.put(name, message, "2v1v0v0v");
			file = new File("/tmp/junit/Replica/pippo.2v1v0v0v");
			assertTrue(file.exists()); //check if new version exists

			r.put(name, message, "1v1v0v1v");
			assertTrue(s.findAllinReplica(name).length == 2);
			s.deleteInReplica(name +".2v1v0v0v" );
			s.deleteInReplica(name +".1v1v0v1v" );
			
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("ho finito il test");
	}
	
	
	@Test
	public void testGetStorage() {
		RemoteNode2Node r= new RemoteNode2Node(24, 54, 3, s);
		String message= "hello pad";
		String name= "pippo";
		File file = new File("/tmp/junit/Storage/pippo.1v0v0v0v");
		
		try {
			r.put(name, message, "1v0v0v0v");
			assertTrue(file.exists());
			Pair[] all=r.get(name);
			assertTrue(all.length == 1);
			r.put(name, message, "1v1v0v0v");
			assertTrue(!file.exists()); //deleted old version
			file = new File("/tmp/junit/Storage/pippo.1v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "2v1v0v0v");
			file = new File("/tmp/junit/Storage/pippo.2v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "1v1v0v1v");
			assertTrue(s.findAllinStorage(name).length == 2);
			
			all=r.get(name);
			for(int i=0; i<2; i++) {
				System.out.println(all[i].getLeft());
				System.out.println(all[i].getRight());
			}
			assertTrue(all.length == 2);
			
			s.deleteInStorage(name +".2v1v0v0v" );
			s.deleteInStorage(name +".1v1v0v1v" );
			
			
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("ho finito il test");
	}
	
	
	@Test
	public void testGetReplica() {
		RemoteNode2Node r= new RemoteNode2Node(26, 54, 3, s);
		String message= "hello pad";
		String name= "pippo";
		File file = new File("/tmp/junit/Replica/pippo.1v0v0v0v");
		
		try {
			r.put(name, message, "1v0v0v0v");
			assertTrue(file.exists());
			Pair[] all=r.get(name);
			assertTrue(all.length == 1);
			r.put(name, message, "1v1v0v0v");
			assertTrue(!file.exists()); //deleted old version
			file = new File("/tmp/junit/Replica/pippo.1v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "2v1v0v0v");
			file = new File("/tmp/junit/Replica/pippo.2v1v0v0v");
			assertTrue(file.exists()); //check if new version exists
			r.put(name, message, "1v1v0v1v");
			assertTrue(s.findAllinReplica(name).length == 2);
			
			all=r.get(name);
			for(int i=0; i<2; i++) {
				System.out.println(all[i].getLeft());
				System.out.println(all[i].getRight());
			}
			assertTrue(all.length == 2);
			
			s.deleteInReplica(name +".2v1v0v0v" );
			s.deleteInReplica(name +".1v1v0v1v" );
			
			
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("ho finito il test");
	}

}
