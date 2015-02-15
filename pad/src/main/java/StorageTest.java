
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
		Storage s= new Storage("/Users/dariobalinzo/git/junit");
		File f= new File("/Users/dariobalinzo/git/junit");
		String[] l=f.list();
		int i=3;
		for (String s1: l) {
			if (s1.equals("Storage") || s1.equals("Processing")  || s1.equals("Replica"))
				i--;
		}
		assertEquals(i,0);
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeStorage(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteStorage() {
		Storage s= new Storage("/Users/dariobalinzo/git/junit");
		String name= "123.12v12v45";
		File f= new File("/Users/dariobalinzo/git/junit/Storage/" + name);
		String message = "Hello world";
		try {
			s.writeStorage(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		if (f.exists())
			f.delete();
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeReplica(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteReplica() {
		Storage s= new Storage("/Users/dariobalinzo/git/junit");
		String name= "123.12v12v45";
		File f= new File("/Users/dariobalinzo/git/junit/Replica/" + name);
		String message = "Hello world";
		try {
			s.writeStorage(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		if (f.exists())
			f.delete();
	
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#writeProcessing(java.lang.String, java.io.Serializable)}.
	 */
	@Test
	public void testWriteProcessing() {
		Storage s= new Storage("/Users/dariobalinzo/git/junit");
		String name= "123.12v12v45";
		File f= new File("/Users/dariobalinzo/git/junit/Processing/" + name);
		String message = "Hello world";
		try {
			s.writeStorage(name, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(f.exists());
		if (f.exists())
			f.delete();
	
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readProcessing(java.lang.String)}.
	 */
	@Test
	public void testReadProcessing() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readReplica(java.lang.String)}.
	 */
	@Test
	public void testReadReplica() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#readStorage(java.lang.String)}.
	 */
	@Test
	public void testReadStorage() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#getAllProcessing()}.
	 */
	@Test
	public void testGetAllProcessing() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#findAllinStorage(java.lang.String)}.
	 */
	@Test
	public void testFindAllinStorage() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link mcsn.pad.storage.Storage#findAllinReplica(java.lang.String)}.
	 */
	@Test
	public void testFindAllinReplica() {
		fail("Not yet implemented");
	}

}
