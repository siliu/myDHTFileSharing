package dhtfs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
	

    private Configuration() {
    }

    public static Map<Long, Address>  load(String filepath) throws FileNotFoundException {
    	
        FileInputStream fis = new FileInputStream(new File(filepath));
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
        Map<Long, Address> addressTable = new ConcurrentHashMap<Long, Address>();
        
        String line = null;
        String[] lineArray = new String[2];
        
    	try {
			while ((line = br.readLine()) != null) {
				lineArray = line.split(":");
				Long index = Long.parseLong(lineArray[0]);
				String peerHostname = (lineArray[1].split("/"))[0];
				int peerPort = Integer.parseInt((lineArray[1].split("/"))[1]);
				Address peerAddress = new Address();
				peerAddress.setHostname(peerHostname);
				peerAddress.setPort(peerPort);
				addressTable.put(index, peerAddress);
				
			}
		 	br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return addressTable;
    }
}
