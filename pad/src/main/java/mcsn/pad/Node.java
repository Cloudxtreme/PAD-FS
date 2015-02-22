package mcsn.pad;



import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Hashtable;
//import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mcsn.pad.parsing.SaxConfigParser;
import mcsn.pad.rmi.FS;
import mcsn.pad.rmi.Node2Node;
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
	private String[] MySettings; //my setting (my ulr, my path of archive)
	
	

	public Node() throws RemoteException {}


    public static void main(String args[]) 
    { 
    	
    	String configFilePath;
    	String MyName;
    	
    	
    	if ( args.length < 2) {
    		System.out.println("Parameter needed: PATH OF CONFIG.XML and MYID");
    		return;
    	}
    	
    	if (args[0].equals("get")) {
    		try {
				clientGet(args[1],args[2]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    	
    	if (args[0].equals("put")) {
    		try {
				clientPut(args[1],args[2],args[3]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    		
    	
    	MyName=args[1];
    	configFilePath=args[0];
    	
    	try {
    		Node node= new Node();
    		node.StartUp(MyName, configFilePath);
    		node.setupRMI();
    		
    		
    		
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

    
    public void setupRMI() throws RemoteException, MalformedURLException {
    	String myUrl=MySettings[0];
		int port= Integer.parseInt(myUrl.substring(myUrl.indexOf(':') + 1, myUrl.length()));
		System.out.println("PAD-FS: trying to create the registry");
    	LocateRegistry.createRegistry(port);
    	System.out.println("registry UP :)");
    	Storage s= new Storage(MySettings[1]);
    	System.out.println("PAD-FS: storage is ready");
    	
    	Hashtable<Integer,FS> cacheFS=new Hashtable<Integer,FS>();
    	
    	
    	//FIXME: first time can be setted also to null directly
    	//populating cache of remote object
    	//FIXME: is this useless?????????????????????????????
    	for (Integer i : peers.keySet()) {
    		try {
				FS fs = (FS) Naming.lookup(peers.get(i)+"/FS");
				cacheFS.put(new Integer(i), fs);
			} catch (NotBoundException e) {
				
			} catch (RemoteException e ) {
				
			}
    	}
    	
    	Hashtable<Integer,Node2Node> cacheN2N=new Hashtable<Integer,Node2Node>();
    	
    	//populating cache of remote object
    	for (Integer i : peers.keySet()) {
    		try {
				Node2Node fs = (Node2Node) Naming.lookup(peers.get(i)+"/N2N");
				cacheN2N.put(new Integer(i), fs);
			} catch (NotBoundException e) {
				
			} catch (RemoteException e ) {
				
			}
    	}
    	
    	
    	//needed my id, where i find?
    	final int myid = Integer.parseInt(MySettings[2]);
    	final int n = Integer.parseInt(MySettings[3]);
    	final int k = Integer.parseInt(MySettings[4]);
    	
    	
    	Deamon d= new Deamon(myid, n, k, s, cacheN2N, cacheFS, peers);
    	RemoteFS myFS = new RemoteFS(myid, n,  k, s, cacheN2N, peers,d);
    	RemoteNode2Node myN2N = new RemoteNode2Node(myid, n, k, s);
        // Bind this object instance to the name "HelloServer" 
        Naming.rebind(myUrl+"/FS", myFS); 
        Naming.rebind(myUrl+"/N2N", myN2N); 
        
        
		new DeamonThread(d, true).start(); 

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
	    MySettings= new String[5];
	    xmlReader.setContentHandler(new SaxConfigParser(peers,MySettings, myId ) );
	    xmlReader.parse(ConfigPath);
	    
	    //debug
	    /*System.out.println(MySettings[0] +"---"+ MySettings[1] + "---" + MySettings[2]);
	    System.out.println(MySettings[3] +"---"+ MySettings[4] );
	    Set<Integer> all = peers.keySet();
	    System.out.println("peers debugging");
	    for ( Integer j : all) {
	    	System.out.println(j + " : " + peers.get(j));
	    }*/
	}
	
	public static void clientGet(String registry, String key ) throws MalformedURLException, RemoteException, NotBoundException {
		FS fs = (FS) Naming.lookup( registry + "/FS"); 
		Serializable[] all=fs.get(key);
		for (Serializable s : all)
			System.out.println("PAD-CLIENT: get " + key + " = " + (String) s );
	}
	
	public static void clientPut(String registry, String key, String value) throws MalformedURLException, RemoteException, NotBoundException {
		FS fs = (FS) Naming.lookup( registry + "/FS");
		fs.put(key, value);
		System.out.println("PAD-CLIENT: put " + key + " , " + value );
	}
	
	
}

