package mcsn.pad.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/*
 * Storage class
 */

public class Storage {
	private String PATH;
	
	public Storage(String PathArchive) {
		PATH=PathArchive;
		File file = new File(PATH);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("PAD-FS: Archive " + PATH + " is created!");
			} else {
				System.out.println("PAD-FS: Failed to create directory Archive!");
			}
		}
		
	}
	
	public void writeFile(int key, Serializable obj) throws IOException {
		OutputStream file = new FileOutputStream(new Integer(key).toString());
	    OutputStream buffer = new BufferedOutputStream(file);
	    ObjectOutput output = new ObjectOutputStream(buffer);
	    output.writeObject(obj);
	    output.close();
	}

	
	public Serializable readFile(int key) throws IOException, ClassNotFoundException {
		InputStream file = new FileInputStream(new Integer(key).toString());
	    InputStream buffer = new BufferedInputStream(file);
	    ObjectInput input = new ObjectInputStream (buffer);
	    
	    Serializable retrieved= (Serializable) input.readObject();
	    input.close();
	    return retrieved;
		
	}
}
