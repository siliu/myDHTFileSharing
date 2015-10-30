package dhtfs;

import java.io.FileNotFoundException;
import java.util.Scanner;

import dhtfs.peer.PeerClient;

/**
 * @author siliu
 * Evaluation2 class is to only start the server 
 */

public class Evaluation2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		System.out.println("Please input the index for this peer: ");
    	Scanner inputScanner = new Scanner(System.in);
    	String inputRaw = inputScanner.nextLine();
    	String configPath =  System.getProperty("user.dir") + "/src/dhtfs/utils/config.txt";
    	
		try {
	
			Peer peer = new Peer(Integer.parseInt(inputRaw),configPath);
			peer.startup();
			Thread.sleep(10);
			PeerClient pc = peer.getpClient();
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

//		System.out.println("Working Directory = " + System.getProperty("user.dir") + "/src/dhtp2p/utils/config.txt" );

	}
}
