package mcsn.pad;



import java.io.*;
import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mcsn.pad.parsing.SaxConfigParser;
import mcsn.pad.storage.Storage;
import mcsn.pad.storage.Value;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Node extends UnicastRemoteObject implements FS 
{ 
    /**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private HashMap<String,String> peers; 

	public Node() throws RemoteException {}


    public static void main(String args[]) 
    { 
    	
    	try {
    		/*Node node= new Node();
    		node.StartUp("name1", "src/config.xml");*/
    		
    		
    		Storage store= new Storage("Archive");
    		Value v= new Value();
    		v.x=2;
    		v.y=3;
    		store.writeFile(555,v);
    		v=(Value) store.readFile(555);
    		v=(Value) store.readFile(555);
    		System.out.println(v.x + " " + v.y);
    		
    	} catch (Exception e ) {
    		e.printStackTrace();
    	}
    	
        /*try 
        { 
        	System.out.println("trying to create the registry");
        	//LocateRegistry.createRegistry(1999);
        	LocateRegistry.createRegistry(1998);
        	System.out.println("registry UP :)");
            Node obj = new Node(); 
            // Bind this object instance to the name "HelloServer" 
            Naming.rebind("//127.0.0.1:1999/HelloServer", obj); 
        } 
        catch (Exception e) 
        { 
            System.out.println("HelloImpl err: " + e.getMessage()); 
            e.printStackTrace(); 
        } */
    }

	
	public void put(long key, Serializable value) throws RemoteException {
		// TODO Auto-generated method stub
		
		
	}

	
	public Serializable get(long key) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	} 
	
	/*
	 * StartUp configuration file parsing, discover address
	 * of all nodes
	 */
	public void StartUp(String myId, String ConfigPath) throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    spf.setNamespaceAware(true);
	    SAXParser saxParser = spf.newSAXParser();
	    XMLReader xmlReader = saxParser.getXMLReader();
	    peers=new HashMap<String,String>();
	    xmlReader.setContentHandler(new SaxConfigParser(peers) );
	    xmlReader.parse(ConfigPath);	
	    
	}
	
	
}

