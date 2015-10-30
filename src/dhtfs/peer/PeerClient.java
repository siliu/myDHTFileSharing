package dhtfs.peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import dhtfs.utils.Address;
import dhtfs.utils.Command;
import dhtfs.utils.Configuration;
import dhtfs.utils.KeyHash;
import dhtfs.utils.Message;
import dhtfs.utils.Receiver;
import dhtfs.utils.Transfer;

public class PeerClient {
	
	private long peerIndex;
	private Socket socket;
	private String config;
	private int peerNum;
	private static Map<Long, Address> peerList = new ConcurrentHashMap<Long, Address>();
	private String peerDir;
	private final int repFactor = 3; // Replication factor value should be less than peerNum.
	
	//Cache the old socket used for communication with other servers for reuse in the future communication
	private Map<Address, Socket> socketCache = new ConcurrentHashMap<Address, Socket>(); 
	
	public PeerClient(long peerIndex,String config) throws FileNotFoundException{
		this.peerIndex = peerIndex;
		this.config = config;
		this.peerList = Configuration.load(config);
		this.peerNum = peerList.size();
		this.peerDir = Long.toString(peerIndex);      //Use the peerIndex as the name of local directory for peer
		File shared = new File(peerDir);
		shared.mkdir();
	
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * LOOKUP command
	 * 
	 */
	public synchronized Address getPeerAddress(long peerIndex){
		
		Address peerAddress = null;
		if(peerList.containsKey(peerIndex))
			peerAddress = peerList.get(peerIndex);
		return peerAddress;
	}
	
	private void getSocket(Address peerAddress) throws IOException{
		
		if(socketCache.containsKey(peerAddress)){
			this.socket = socketCache.get(peerAddress);
			
		}else{
			try {
				this.socket = new Socket(peerAddress.getHostname(),peerAddress.getPort());
				socketCache.put(peerAddress,this.socket);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
	}
	/**
	 * Get the file list in the folder of this peer. The folder name is the same as the peer port.
	 * @return
			OutputStream os = socket.getOutputStream();
	 */
	
	public ArrayList<String> getSharedFiles(){
		
		ArrayList<String> sharedFileList = new ArrayList<String>();
		
		File sharedDir = new File(peerDir);
		
		Iterator<File> iter = FileUtils.iterateFiles(sharedDir, null, false);
		
		while(iter.hasNext()){
			sharedFileList.add(iter.next().getName());
		}
		
		return sharedFileList;
	}
	
	/**
	 * checkMessage function is to check if the IndexServer returned OK command to the peer.
	 * @param msgIn : The input message to check
	 * @return
	 */
	private boolean checkMessage(Message msgIn){
		
		if(msgIn != null && msgIn.getCmd() != null && msgIn.getCmd().equals(Command.OK))
			return true;
		
		return false;
	}
	
	public void registry(ArrayList<String> fileList){
		
		try {
			for(String filename : fileList){
				
				String hashKey = KeyHash.genHash(filename);
				int regIndex = KeyHash.getIndex(hashKey, peerNum);
				
				// Register file at multiple peers for resilence.
				int replicaIndex = 0;
				for(int i=0; i < repFactor ;  i++){
					
					replicaIndex = (regIndex + i) % peerNum + 1;
					System.out.println("The " + (i+1) + "th replica of this file index goes to peer: " + replicaIndex);
					Address peerAddress = getPeerAddress(replicaIndex);
					System.out.println("The address of the registration peer is: " + peerAddress.getHostname() + "/" + peerAddress.getPort());
					
					this.getSocket(peerAddress);
					
					Map<String, Object> regMap = new HashMap<String,Object>();
					regMap.put("dhtKey",hashKey);
					//regMap.put("dhtKey",filename); // TODO: change to hashKey after test
					regMap.put("peerIndex", peerIndex);
					

					Message msgOut = new Message(Command.REGISTER,regMap);
					Transfer.sendMessage(msgOut, this.socket.getOutputStream());

					Message msgIn = Transfer.receiveMessage(this.socket.getInputStream());

					if(checkMessage(msgIn)){
						System.out.println(msgIn.getContent());
					}else{
						System.out.println(msgIn.getContent());
					}	
				}
							
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public boolean doSearch(String filename, Address pa){
		
		boolean result = false;
		
		String hashKey = KeyHash.genHash(filename);		
		
		try {
			this.getSocket(pa);
			Map<String, Object> getMap = new HashMap<String,Object>();
			getMap.put("dhtKey", hashKey);
			
			//getMap.put("dhtKey", filename); //TODO change to hashKey later
		
			Message msgOut = new Message(Command.SEARCH, getMap);
			Transfer.sendMessage(msgOut, this.socket.getOutputStream());
			
			Message msgIn = Transfer.receiveMessage(this.socket.getInputStream());
			if(checkMessage(msgIn)){
				System.out.println("The locations of this file are: " + msgIn.getContent());
			}else{
				System.out.println("Fail to get the locations of this file!");
			}
			
			result = true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			try {
				this.socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		}
			
		return result;
	}
	
	public void search(String filename){
		
		String hashKey = KeyHash.genHash(filename);
		int regIndex = KeyHash.getIndex(hashKey, peerNum);

		int delta = 0;

		while(delta < repFactor){
			
		    int nextPeer = (regIndex + delta) % peerNum + 1;
			Address peerAddress = getPeerAddress(nextPeer);
			System.out.println("Number " + delta + " index of this file on peer: " + nextPeer);
			System.out.println("The address of this peer is: " + peerAddress.getHostname() + "/" + peerAddress.getPort());
			
			if(doSearch(filename, peerAddress))  break;
			
			delta = delta + 1;
		}
		
		if(delta == repFactor)
			
			System.out.println("No index available for this file.");
		
	}
	
	
	/**
	 * obtain function is to download a file from other peer by providing the remotePeerIndex and filename
	 * 1. Get the remote peer address from index server using peerId
	 * 2. Download the file from the remote peer
	 */
	public void obtain(String filename, long remotePeerIndex){

		Address remoteAddress = this.getPeerAddress(remotePeerIndex);
		if(remoteAddress != null){
			try {
				this.getSocket(remoteAddress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				try {
					this.socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			Receiver receiver = new Receiver(socket,filename, peerDir);
			receiver.download();
//			receiver.start();
			
		}
	}
	
	public void close() {
         try {
        	 
			this.socket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
    }

}
