package mcsn.pad;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import mcsn.pad.storage.Storage;

import org.junit.Test;

/**
 * 
 */

/**
 * @author dariobalinzo
 *
 */
public class StorageTest {

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#Storage(java.lang.String)}.
	 */
	@Test
	public void testStorage() {
		Storage s= new Storage("/tmp/junit");
		File f= new File("/tmp/junit");
		String[] l=f.list();
		int i=3;
		for (String s1: l) {
			if (s1.equals("Storage") || s1.equals("Processing")  || s1.equals("Replica"))
				i--;
		}
		assertEquals(i,0);
		assertTrue(s!=null);
		s=null;
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeStorage(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteStorage() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		File f= new File("/tmp/junit/Storage/" + name);
		String message = "Hello world";
		try {
			s.writeStorage(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		s.deleteInStorage(name);
		assertTrue(!f.exists());
		
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeReplica(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteReplica() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		File f= new File("/tmp/junit/Replica/" + name);
		String message = "Hello world";
		try {
			s.writeReplica(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		s.deleteInReplica(name);
		assertTrue(!f.exists());
	
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeProcessing(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteProcessing() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		File f= new File("/tmp/junit/Processing/" + name);
		String message = "Hello world";
		try {
			s.writeProcessing(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		s.deleteInProcessing(name);
		assertTrue(!f.exists());
	
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readProcessing(java.lang.String)}.
	 */
	@Test
	public void testReadProcessing() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String out="";
		File f= new File("/tmp/junit/Processing/" + name);
		String message = "Hello world";
		try {
			s.writeProcessing(name, message);
			out=(String)s.readProcessing(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(out.equals(message));
		assertTrue(f.exists());
		s.deleteInProcessing(name);
		assertTrue(!f.exists());
		
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readReplica(java.lang.String)}.
	 */
	@Test
	public void testReadReplica() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String out="";
		File f= new File("/tmp/junit/Replica/" + name);
		String message = "Hello world";
		try {
			s.writeReplica(name, message);
			out=(String)s.readReplica(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(out.equals(message));
		assertTrue(f.exists());
		s.deleteInReplica(name);
		assertTrue(!f.exists());
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readStorage(java.lang.String)}.
	 */
	@Test
	public void testReadStorage() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String out="";
		File f= new File("/tmp/junit/Storage/" + name);
		String message = "Hello world";
		try {
			s.writeStorage(name, message);
			out=(String)s.readStorage(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(out.equals(message));
		assertTrue(f.exists());
		s.deleteInStorage(name);
		assertTrue(!f.exists());
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#getAllProcessing()}.
	 */
	@Test
	public void testGetAllProcessing() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String[] out=null;
		
		String message = "Hello world";
		try {
			
			s.writeProcessing("a"+name, message);
			s.writeProcessing("b"+name, message);
			s.writeProcessing("c"+name, message);
			s.writeProcessing("d"+name, message);
			s.writeProcessing("e"+name, message);
			out=s.getAllProcessing();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i=5;
		for (String s1: out) {
			if (s1.equals("e"+name) || s1.equals("d"+name)  || s1.equals("c"+name) ||
					s1.equals("b" + name) || s1.equals("a" + name));
				i--;
		}
		assertEquals(i,0);
		
		s.deleteInProcessing("a"+name);
		s.deleteInProcessing("b"+name);
		s.deleteInProcessing("c"+name);
		s.deleteInProcessing("d"+name);
		s.deleteInProcessing("e"+name);
		
		out=s.getAllProcessing();
		assertEquals(out.length,0);
		
		
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#findAllinStorage(java.lang.String)}.
	 */
	@Test
	public void testFindAllinStorage() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String[] out=null;
		
		String message = "Hello world";
		try {
			
			s.writeStorage(name + "a", message);
			s.writeStorage(name + "a1", message);
			s.writeStorage(name + "a2", message);
			s.writeStorage(name + "a3", message);
			s.writeStorage("d"+name, message);
			s.writeStorage("e"+name, message);
			out=s.findAllinStorage(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i=4;
		for (String s1: out) {
			if (s1.equals(name + "a") || s1.equals(name + "a1")  || s1.equals(name + "a2") ||
					s1.equals( name + "a3") );
				i--;
		}
		assertEquals(i,0);
		s.deleteInStorage(name + "a");
		s.deleteInStorage(name + "a1");
		s.deleteInStorage(name + "a2");
		s.deleteInStorage(name + "a3");
		s.deleteInStorage("d"+name);
		s.deleteInStorage("e"+name);
		out=s.findAllinStorage(name);
		assertEquals(out.length,0);
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#findAllinReplica(java.lang.String)}.
	 */
	@Test
	public void testFindAllinReplica() {
		Storage s= new Storage("/tmp/junit");
		String name= "123.12v12v45";
		String[] out=null;
		
		String message = "Hello world";
		try {
			
			s.writeReplica(name + "a", message);
			s.writeReplica(name + "a1", message);
			s.writeReplica(name + "a2", message);
			s.writeReplica(name + "a3", message);
			s.writeReplica("d"+name, message);
			s.writeReplica("e"+name, message);
			out=s.findAllinReplica(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i=4;
		for (String s1: out) {
			if (s1.equals(name + "a") || s1.equals(name + "a1")  || s1.equals(name + "a2") ||
					s1.equals( name + "a3") );
				i--;
		}
		assertEquals(i,0);
		s.deleteInReplica(name + "a");
		s.deleteInReplica(name + "a1");
		s.deleteInReplica(name + "a2");
		s.deleteInReplica(name + "a3");
		s.deleteInReplica("d"+name);
		s.deleteInReplica("e"+name);
		out=s.findAllinStorage(name);
		assertEquals(out.length,0);
		
	}

	
	
	
	
}
