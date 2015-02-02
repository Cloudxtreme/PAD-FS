package mcsn.pad.parsing;


import java.util.HashMap;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxConfigParser extends DefaultHandler{
	
	private HashMap<String,String> peers;
	private String[] info;
	private String myName;
	
	public SaxConfigParser(HashMap<String,String> table, String[] _info, String _myName) {
		peers=table; //taking the hash to populate with the neighbors
		info=_info;
		myName=_myName;
	}

    public void startDocument() throws SAXException {
        
    }

    
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts) throws SAXException {
    	
    	if(localName == "allNodes") 
    		return;
    	if(localName == myName ) {
    		info[0]=atts.getValue(1); //path archive
    		info[1]=atts.getValue(2); //cache dimension
    	}
    	
    	//if name is new
    	if (peers.get(localName) == null ) {
    		
    		String address=atts.getValue(0);
    		if (address == null) {
    			
					throw new SAXException("Null URl in node " + localName + " " + address );
			}
    			
    	peers.put(localName, address);
    		
    		
    	}
    	
    }
    
   
    
    
    }

	

