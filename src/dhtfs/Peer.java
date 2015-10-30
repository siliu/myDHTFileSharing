package dhtfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import dhtfs.peer.PeerClient;
import dhtfs.peer.PeerServer;
import dhtfs.utils.Address;

public class Peer {
	
	private int port;
	private long peerIndex;
	private String config;
	private PeerServer pServer;
	private PeerClient pClient;
	
	Peer(long peerIndex, String config) throws FileNotFoundException{
		
		this.peerIndex = peerIndex;
		this.config = config;
		pClient = new PeerClient(peerIndex,config);
		this.port = pClient.getPeerAddress(peerIndex).getPort();
		pServer = new PeerServer(port,peerIndex);
	}
	
	public PeerServer getpServer() {
		return pServer;
	}

	public void setpServer(PeerServer pServer) {
		this.pServer = pServer;
	}

	public PeerClient getpClient() {
		return pClient;
	}

	public void setpClient(PeerClient pClient) {
		this.pClient = pClient;
	}
	
	public void startup(){
		this.pServer.start();
	}
	
	public void exit(){
		this.pClient.close();
		this.pServer.close();
	}
	
	 public static void printUsage() {
	    	System.out.println("* * * * * * * * * * * * * * * * * * * * *");
	        System.out.println("*  CS550 PA3: DHT File Sharing System   *");
	        System.out.println("*                                       *");
	        System.out.println("*             Name: Si Liu              *");
	        System.out.println("*             CWID: A20334820           *");
	        System.out.println("* * * * * * * * * * * * * * * * * * * * *");
	        System.out.println("Commands: REGISTER, SEARCH, LOOKUP, OBTAIN, DELETE, EXIT ");
	        System.out.println("[REGISTER]: Register all files on this peer to the decentralized index server.");
	        System.out.println("  [SEARCH]: Search the locations (peer indexes) of a file from the distributed hash table.");
	        System.out.println("  [LOOKUP]: Look up the IP address and port number of a peer through its peer index.");
	        System.out.println("  [OBTAIN]: Download a file to the current peer from a remote peer.");
	        System.out.println("  [DELETE]: Delete a file registration from the index server.");
	        System.out.println("  [EXIT]: Exit this client.");
	        System.out.println("Usage: Input the command or parameter as each promot says.");
	    }
	 

	 
	public static void main(String[] args) {
		
		printUsage();
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please input the index for this peer: ");
    	Scanner inputScanner = new Scanner(System.in);
    	String inputRaw = inputScanner.nextLine();
    	String configPath = System.getProperty("user.dir") + "/src/dhtfs/utils/config.txt" ;
    	
		try {
			Peer peer = new Peer(Long.parseLong(inputRaw),configPath);
			peer.startup();
			Thread.sleep(10);
			PeerClient pc = peer.getpClient();
			
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	String userInput;
	    	System.out.println("Please input command:  ");
	    	
	    	while((userInput = br.readLine()) != null){
				
				if(userInput.equalsIgnoreCase("REGISTER")){
					
					System.out.println("Start registering files on this peer ... ");
				    pc.registry(pc.getSharedFiles());
				    System.out.println("Registration is done.");
				    
				    System.out.println("Please input next command:  ");
					
				}else if(userInput.equalsIgnoreCase("SEARCH")){
					
					System.out.println("Please input the filename to search: ");
					String dhtKey = br.readLine();
					while(dhtKey == null){
						System.out.println("Invalid input! The filename cannot be NULL.");
						System.out.println("Please input a valid filename: ");
						dhtKey = br.readLine();
					}
					pc.search(dhtKey);
					
					System.out.println("Please input next command:  ");
					
				}else if(userInput.equalsIgnoreCase("LOOKUP")){
					
					System.out.println("Please input the peer index to lookup: ");
					String indexStr = br.readLine();
					
					while(indexStr == null){
						System.out.println("Invalid input! The filename cannot be NULL.");
						System.out.println("Please input a valid peer index: ");
						indexStr = br.readLine();
					}
					
					long peerIndex = Long.parseLong(indexStr);

					Address  peerAddress = pc.getPeerAddress(peerIndex);
					System.out.println("The address of this peer is: " + peerAddress.getHostname() + "/" + peerAddress.getPort());
					System.out.println("Please input next command:  ");
					
				}else if(userInput.equalsIgnoreCase("OBTAIN")){
					
					System.out.println("Please input the filename to obtain: ");
					String dhtKey = br.readLine();
					while(dhtKey == null){
						System.out.println("Invalid input! The filename cannot be NULL.");
						System.out.println("Please input a valid filename: ");
						dhtKey = br.readLine();
					}
					pc.search(dhtKey);
					System.out.println("Please pick up one peer index from above to obtain the file: ");
					
					String indexStr = br.readLine();
					
					while(indexStr == null){
						System.out.println("Invalid input! The filename cannot be NULL.");
						System.out.println("Please input a valid peer index: ");
						indexStr = br.readLine();
					}
					
					long remotePeerIndex = Long.parseLong(indexStr);
					pc.obtain(dhtKey,remotePeerIndex);
					System.out.println("Please input next command:  ");

				}else if(userInput.equalsIgnoreCase("EXIT")){
					
					System.out.println("Exit this client.");
					peer.exit();
					break;
					
				}else{
					
					System.out.println("This command is not supported yet! ");
					System.out.println("Commands supported:  REGISTER, SEARCH, LOOKUP, OBTAIN, DELETE, EXIT");
					System.out.println("Please input command:  ");
				}

			}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}


	
}
