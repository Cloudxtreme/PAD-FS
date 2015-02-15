package mcsn.pad;



import java.io.*;
import java.rmi.RemoteException; 
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mcsn.pad.parsing.SaxConfigParser;
import mcsn.pad.storage.Storage;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class Node { 
    /**
	 * 
	 */
	
	
	private HashMap<String,String> peers; //table with urls of all the other peers
	private String[] storing_settings; //my setting (my ulr, my path of archive)

	public Node() throws RemoteException {}


    public static void main(String args[]) 
    { 
    	/*ARGS NEEDED: PATH OF CONFIG.XML and MYNAME */
    	String configFilePath;
    	String MyName;
    	
    	if ( args.length < 2) {
    		System.out.println("Parameter needed: PATH OF CONFIG.XML and MYNAME");
    		return;
    	}
    	
    	MyName=args[1];
    	configFilePath=args[0];
    	
    	try {
    		Node node= new Node();
    		node.StartUp(MyName, configFilePath);
    		
    		
    		
    		
    		Storage store= new Storage("Archive");
    		
    		
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
	    storing_settings= new String[2];
	    xmlReader.setContentHandler(new SaxConfigParser(peers,storing_settings, myId ) );
	    xmlReader.parse(ConfigPath);
	    
	    //debug
	    System.out.println(storing_settings[0] + storing_settings[1]);
	    
	}
	
	
}

