package mcsn.pad;



import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mcsn.pad.parsing.SaxConfigParser;
import mcsn.pad.rmi.RemoteFS;
import mcsn.pad.rmi.RemoteNode2Node;
import mcsn.pad.storage.Storage;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class Node { 
    /**
	 * 
	 */
	
	
	private HashMap<Integer,String> peers; //table with urls of all the other peers
	private String[] storing_settings; //my setting (my ulr, my path of archive)
	

	public Node() throws RemoteException {}


    public static void main(String args[]) 
    { 
    	/*ARGS NEEDED: PATH OF CONFIG.XML and MYNAME */
    	String configFilePath;
    	String MyName;
    	
    	if ( args.length < 2) {
    		System.out.println("Parameter needed: PATH OF CONFIG.XML and MYID");
    		return;
    	}
    	
    	MyName=args[1];
    	configFilePath=args[0];
    	
    	try {
    		Node node= new Node();
    		node.StartUp(MyName, configFilePath);
    		int myid=Integer.parseInt(args[2]);
    		node.setupRMI(myid);
    		
    		
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

    
    public void setupRMI(int myid) throws RemoteException, MalformedURLException {
    	String myUrl=storing_settings[0];
		int port= Integer.parseInt(myUrl.substring(myUrl.indexOf(':') + 1, myUrl.length()));
		System.out.println("PAD-FS: trying to create the registry");
    	LocateRegistry.createRegistry(port);
    	System.out.println("registry UP :)");
    	Storage s= new Storage(storing_settings[1]);
    	System.out.println("PAD-FS: storage is ready");
    	
    	Hashtable<Integer,RemoteFS> cacheFS=new Hashtable<Integer,RemoteFS>();
    	
    	
    	//FIXME: first time can be setted also to null directly
    	//populating cache of remote object
    	for (Integer i : peers.keySet()) {
    		try {
				RemoteFS fs = (RemoteFS) Naming.lookup(peers.get(i)+"/FS");
				cacheFS.put(new Integer(i), fs);
			} catch (NotBoundException e) {
				
			} catch (RemoteException e ) {
				
			}
    	}
    	
    	Hashtable<Integer,RemoteNode2Node> cacheN2N=new Hashtable<Integer,RemoteNode2Node>();
    	
    	//populating cache of remote object
    	for (Integer i : peers.keySet()) {
    		try {
				RemoteNode2Node fs = (RemoteNode2Node) Naming.lookup(peers.get(i)+"/N2N");
				cacheN2N.put(new Integer(i), fs);
			} catch (NotBoundException e) {
				
			} catch (RemoteException e ) {
				
			}
    	}
    	
    	//needed my id, where i find?
    	
    	RemoteFS myFS = new RemoteFS(myid, 2,  1, s, cacheN2N, peers);
    	RemoteNode2Node myN2N = new RemoteNode2Node(myid, 2,  1, s);
        // Bind this object instance to the name "HelloServer" 
        Naming.rebind(myUrl+"/FS", myFS); 
        Naming.rebind(myUrl+"/N2N", myN2N); 

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
	    peers=new HashMap<Integer,String>();
	    storing_settings= new String[2];
	    xmlReader.setContentHandler(new SaxConfigParser(peers,storing_settings, myId ) );
	    xmlReader.parse(ConfigPath);
	    
	    //debug
	    //System.out.println(storing_settings[0] +"---"+ storing_settings[1]);
	    
	}
	
	public void clientGet(String registry, String key ) throws MalformedURLException, RemoteException, NotBoundException {
		RemoteFS fs = (RemoteFS) Naming.lookup( registry + "/FS"); 
		Serializable[] all=fs.get(key);
		for (Serializable s : all)
			System.out.println("PAD-CLIENT: get " + key + " = " + (String) s );
	}
	
	public void clientPut(String registry, String key, String value) throws MalformedURLException, RemoteException, NotBoundException {
		RemoteFS fs = (RemoteFS) Naming.lookup( registry + "/FS");
		fs.put(key, value);
		System.out.println("PAD-CLIENT: put " + key + " , " + value );
	}
	
	
}

