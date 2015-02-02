package mcsn.pad.parsing;


import java.util.HashMap;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxConfigParser extends DefaultHandler{
	
	private HashMap<String,String> peers;
	
	public SaxConfigParser(HashMap<String,String> table) {
		peers=table; //taking the hash to populate with the neighbors
	}

    public void startDocument() throws SAXException {
        
    }

    
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts) throws SAXException {
    	
    	if(localName == "allNodes") 
    		return;
    	
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

	

