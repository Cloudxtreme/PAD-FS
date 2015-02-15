package mcsn.pad.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
	private String ARCHIVE_PATH;
	
	private class nameFilter implements FilenameFilter {
		
		private final String starting;
		
		public nameFilter(String start) {
			starting=start;
		}
		
		public boolean accept(File dir, String name) {
			if (name.startsWith(starting))
				return true;
			else
				return false;
		}
	}
	
	public Storage(String PathArchive) {
		ARCHIVE_PATH=PathArchive;
		File file = new File(ARCHIVE_PATH);
		/*folder of node database */
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("PAD-FS: Archive " + ARCHIVE_PATH + " is created!");
			} else {
				System.out.println("PAD-FS: Failed to create directory Archive!");
			}
		}
		
		file= new File(ARCHIVE_PATH+ "/Storage");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("PAD-FS: Storage " + ARCHIVE_PATH + " is created!");
			} else {
				System.out.println("PAD-FS: Failed to create directory of Storage!");
			}
		}
		
		file= new File(ARCHIVE_PATH+ "/Replica");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("PAD-FS: Replica " + ARCHIVE_PATH + " is created!");
			} else {
				System.out.println("PAD-FS: Failed to create directory of Replicas!");
			}
		}
		
		file= new File(ARCHIVE_PATH+ "/Processing");
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("PAD-FS: Replica " + ARCHIVE_PATH + " is created!");
			} else {
				System.out.println("PAD-FS: Failed to create directory of Replicas!");
			}
		}
		
		
	}
	
	public void writeStorage(String filename, Serializable o) throws IOException {
		writeFile(filename,o,ARCHIVE_PATH+"/Storage/");
		System.out.println("PAD-FS: writed " +filename + "in Storage");
	}
	
	public void writeReplica(String filename, Serializable o) throws IOException {
		writeFile(filename,o,ARCHIVE_PATH+"/Replica/");
		System.out.println("PAD-FS: writed " +filename + "in Replica");
	}
	
	public void writeProcessing(String filename, Serializable o) throws IOException {
		writeFile(filename,o,ARCHIVE_PATH+"/Processing/");
		System.out.println("PAD-FS: writed " +filename + "in Processing");
	}
	
	public Serializable readProcessing(String filename) throws IOException, ClassNotFoundException {
		Serializable out =readFile(filename,ARCHIVE_PATH+"/Processing/");
		System.out.println("PAD-FS: readed " +filename + "in Processing");
		return out;
	}
	
	public Serializable readReplica(String filename) throws IOException, ClassNotFoundException {
		Serializable out =readFile(filename,ARCHIVE_PATH+"/Replica/");
		System.out.println("PAD-FS: readed " +filename + "in Replica");
		return out;
	}
	
	public Serializable readStorage(String filename) throws IOException, ClassNotFoundException {
		Serializable out =readFile(filename,ARCHIVE_PATH+"/Storage/");
		System.out.println("PAD-FS: readed " +filename + "in Storage");
		return out;
	}
	
	public String[] getAllProcessing() {
	return getAllFile(ARCHIVE_PATH + "/Processing/");
	}
	
	public String[] findAllinStorage(String key) {
		return getAllVersion(key, ARCHIVE_PATH + "/Storage/");
	}
	
	public String[] findAllinReplica(String key) {
		return getAllVersion(key, ARCHIVE_PATH + "/Replica/");
	}
	
	
	private String[] getAllFile(String path) {
		File f= new File(path);
		return f.list();
	}
	
	private String[] getAllVersion(String name_prefix, String path) {
		File file = new File(path);
		nameFilter f= new nameFilter(name_prefix);
		File[] list = file.listFiles(f);
		String[] out= new String[list.length];
		int i=0;
		for (File matched : list) {
			out[i]=matched.getName();
			i++;
		}
		return out;
	};

	
	
	private void writeFile(String filename, Serializable obj, String path) throws IOException {
		OutputStream file = new FileOutputStream(path + filename);
	    OutputStream buffer = new BufferedOutputStream(file);
	    ObjectOutput output = new ObjectOutputStream(buffer);
	    output.writeObject(obj);
	    output.close();
	}

	
	private Serializable readFile(String filename, String path) throws IOException, ClassNotFoundException {
		InputStream file = new FileInputStream(path + filename);
	    InputStream buffer = new BufferedInputStream(file);
	    ObjectInput input = new ObjectInputStream (buffer);
	    
	    Serializable retrieved= (Serializable) input.readObject();
	    input.close();
	    return retrieved;
		
	}
}
