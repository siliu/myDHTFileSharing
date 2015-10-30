package dhtfs;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dhtfs.peer.PeerClient;

/**
 * 
 * @author siliu
 * Evaluation class is to start server and send requests
 */

public class Evaluation1 {
	
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
			
			Timer timer = new Timer();
			Date startdate = new Date(115,9,29,23,10,00);
			
			System.out.println("Task start time: " + startdate);
		
			timer.schedule(new TimerTask() {
			
				public void run(){
					
					//Send 10K register requests
					long regstart = System.nanoTime();
					
					ArrayList<String> fileList = new ArrayList<String>();
					fileList.add("file1");
					fileList.add("file2");
					fileList.add("file3");
					for(int i=1 ; i <= 10000 ; i++){
						pc.registry(fileList);
					}
					long regend = System.nanoTime();
					
					long regduration = TimeUnit.MILLISECONDS.convert((regend - regstart), TimeUnit.NANOSECONDS);
					System.out.println("Sending 10K REGISTER requests takes : " + regduration + " ms." );
					
					
					//Send 10K register requests
					long searchStart = System.nanoTime();
					
					for(int i=1 ; i <= 10000 ; i++){
						pc.search("file1");
					}
					long searchEnd = System.nanoTime();
					
					long searchDuration = TimeUnit.MILLISECONDS.convert((searchEnd - searchStart), TimeUnit.NANOSECONDS);
					System.out.println("Sending 10K REGISTER requests takes : " + searchDuration + " ms." );
					
					
					
					//Send 10K obtain requests
					long obtainStart = System.nanoTime();
					
					for(int i=1 ; i <= 10000 ; i++){
						String filename = "blue1";
						long peerIndex = 8;
						pc.obtain(filename, peerIndex);

					}
					long obtainEnd = System.nanoTime();
					
					long obtainDuration = TimeUnit.MILLISECONDS.convert((obtainEnd - obtainStart), TimeUnit.NANOSECONDS);
					System.out.println("Sending 10K OBTAIN requests takes : " + obtainDuration + " ms." );
					
				}

			}, startdate);
		

			
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

	}

}
