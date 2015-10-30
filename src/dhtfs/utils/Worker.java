package dhtfs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author siliu
 * Worker class is too implement the function of PeerServer
 */
public class Worker extends Thread {

	private String peerDir;
	private Socket socket;
	private Map<String, Object> localTable = new ConcurrentHashMap<String, Object>();
	
	
	public Worker( String peerDir, Socket socket,Map<String, Object> localTable){
	
		this.peerDir = peerDir;
		this.socket = socket;
		this.localTable = localTable;
		
	}
	
	//Get the peer address with corresponding peer index

		
	// register the file in the local hash table of the decentralized index Server
	// key: filename; hash value of filename
	// value : file location peer index
	public synchronized boolean registry(String dhtKey , long peerIndex){
	
		boolean result = false;
		Set<Long> peerSet = new HashSet<Long>();
		//System.out.println("Worker dhtKey 1: " + dhtKey);
		if(! localTable.containsKey(dhtKey)){
			peerSet.add(peerIndex);
			localTable.put(dhtKey, peerSet);
			result = true;
		}else{
			peerSet = (Set<Long>) localTable.get(dhtKey);
			if(! peerSet.contains(peerIndex))
				peerSet.add(peerIndex);
			localTable.put(dhtKey, peerSet);
			result = true;
		}
		System.out.println("The current table content on this peer is: ");
		MapOperator.printMap(localTable);
		return result;
	}
	
	//Search the location of the file in the decentralized index server using filename
	public synchronized Set<Long> search(String dhtKey){
		
		return (Set<Long>) localTable.get(dhtKey);
	}
	
	public synchronized void obtain(String filename){
		
	    Sender sender = new Sender(socket,peerDir,filename);
        sender.start();
	}
	
	
	public void run(){
		
		try {

			while(socket.isConnected()){
				
				InputStream is = socket.getInputStream();
				Message msg = Transfer.receiveMessage(is);
				Command cmd = msg.getCmd(); 
				Map<String, Object> receivedMap = (Map<String, Object>) msg.getContent();
				//TODO while(cmd != exit)

				if(cmd == Command.REGISTER) {
					
					String dhtKey = (String) receivedMap.get("dhtKey");
					long peerIndex = (long) receivedMap.get("peerIndex");
					
					Message returnMsg;
					if(this.registry(dhtKey,peerIndex)){
						returnMsg = new Message(Command.OK, "Register success!");
						
					}else{
						returnMsg = new Message(Command.ERROR, "Register failed!");
					}
					
					Transfer.sendMessage(returnMsg,this.socket.getOutputStream());
					
				}if(cmd == Command.SEARCH){
			
					String dhtKey = (String) receivedMap.get("dhtKey");

					Set<Long> peerSet  = this.search(dhtKey);
					Message returnMsg = new Message(Command.OK, peerSet);
					Transfer.sendMessage(returnMsg, this.socket.getOutputStream());
					
				}if(cmd == Command.OBTAIN){
					
					String filename = (String) receivedMap.get("filename");
					this.obtain(filename);
					
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			try {
				System.out.println("Closing socket at server.");
				this.socket.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
	}

}
