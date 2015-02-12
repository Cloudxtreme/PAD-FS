package mcsn.pad.parsing;


import java.util.HashMap;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxConfigParser extends DefaultHandler{
	
	private HashMap<String,String> peers;
	private String[] info;
	private String myName;
	
	public SaxConfigParser(HashMap<String,String> table, String[] _info, String _myName) throws SAXException {
		peers=table; //taking the hash to populate with the neighbors
		info=_info;
		myName=_myName;
		if (info.length < 2) {
				throw new SAXException("Info String Array Passed in the constructor is too small");
		}
	}

    public void startDocument() throws SAXException {
        
    }

    
    public void startElement(String namespaceURI,
            String localName,
            String qName, 
            Attributes atts) throws SAXException {
    	
    	//root element, nothing to do
    	if(localName == "allNodes") 
    		return;
    	
    	//element relative to this Node, getting al the info
    	if(localName == myName ) {
    		for (int i=0; i<atts.getLength(); i++) {
    			if (atts.getLocalName(i)=="url") 
    				info[0]=atts.getValue(i); //path archive
    			if (atts.getLocalName(i)=="path_archive") 
    				info[1]=atts.getValue(i); //path archive
    		}
    	}
    	
    	//if name is new, add in the table of peers (storing only url)
    	if (peers.get(localName) == null ) {
    		
    		String address=atts.getValue(0);
    		if (address == null) {
    			
					throw new SAXException("Null URl in node " + localName + " " + address );
			}
    			
    	peers.put(localName, address);
    		
    		
    	}
    	
    }
    
   
    
    
    }

	

