package dhtfs.utils;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class Sender extends Thread{

	private Socket socket;
	private String peerDir;
	private String filename;
	
	public Sender(Socket socket, String peerDir, String filename) {

		this.socket = socket;
		this.peerDir = peerDir;
		this.filename = filename;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 1. Get the request from the socket inputstream.
	 * 2. Parse the inputstream to get the filename.
	 * 3. Put the file to output stream for sending.
	 */
	public void run () {
		
		try {
			
			//1. Put the file into DataInputStream first. This "dis" is different from the input stream to receive message.
			//2. Read from DataInputStream to DataOutputStream
			    
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			System.out.println("Sending file: " + filename);
			
			File file= new File(peerDir + "/" + filename);
			
			
			dos.writeLong((long)file.length());       //Send the file length to the receiver first
			dos.flush();
			
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            
			byte[] buf=new byte[1024*1024*512];
			
			while(true)
            {
                int reader = 0;
                if(dis!= null)
                {
                    reader = dis.read(buf);
                }
                
                if(reader == -1)
                    break;                  
                dos.write(buf,0,reader);
            }
			
//			FileUtils.copyFile(new File(peerDir + "/" + filename), os);

			dos.flush();
			
			System.out.print("Done sending file: " + filename);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, e);
		}
	} 

}
