import de.uniba.wiai.lspi.chord.service.*;
import de.uniba.wiai.lspi.chord.data.URL;
import java.io.*;
import java.util.*;
import java.net.*;

public class TestOpenChord {
    
    public static void main ( String [] args ) {
	de.uniba.wiai.lspi.chord.service.PropertiesLoader.loadPropertyFile();
	String protocol = URL.KNOWN_PROTOCOLS.get (URL.SOCKET_PROTOCOL);
	URL bootstrapURL = null ;
	URL localURL = null;
	try {
	    bootstrapURL = new URL(protocol + "://localhost:9081/");
	    localURL = new URL(protocol + "://localhost:9080/");
	} catch (MalformedURLException e) {
	    throw new RuntimeException ( e ) ;
	}

	Chord chord = new de.uniba.wiai.lspi.chord.service.impl.ChordImpl() ;
	Chord chord2 = new de.uniba.wiai.lspi.chord.service.impl.ChordImpl() ;
	StringKey key = new StringKey("hello");
	try {
	    chord.create(bootstrapURL) ;
	    chord2.join(localURL, bootstrapURL);
	    chord2.insert(key, "world");
	    Set<Serializable> val = chord.retrieve(key);
	    for (Serializable s : val) {
		System.out.println(s);
	    }
	} catch (ServiceException e) {
	    throw new RuntimeException ( " Could not create DHT ! " , e ) ;
	}
	
    }
}
