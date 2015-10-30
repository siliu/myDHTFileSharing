package dhtfs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * @author siliu Receiver class is to send the "OBTAIN" command to the peer to
 *         ask for file, and receive the file from the sender.
 */
public class Receiver{

	private Socket socket;
	private String filename;
	private String peerDir;

	public Receiver(Socket socket, String filename, String peerDir) {
		super();
		this.socket = socket;
		this.filename = filename;
		this.peerDir = peerDir;
	}

	public void download() {

		try {
			
			OutputStream os = socket.getOutputStream();

			// Send the "OBTAIN" command to the peer having the file
			System.out.println("Ask for file: " + filename);
			Map<String, Object> obtainMap = new HashMap<String, Object>();
			obtainMap.put("filename", filename);
			Message msg = new Message(Command.OBTAIN, obtainMap);
			Transfer.sendMessage(msg, os);

			// Obtain the file from sender and save it to the local directory
			
			DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			long fileLen = dis.readLong();
//			System.out.println("file length is " + fileLen);
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(peerDir + "/" + filename)));
			
			byte[] buf = new byte[1024*1024];
	       
			long count = 0;
			while (true) {
				int reader = 0;
				if (dis != null) {
					reader = dis.read(buf);
				}
				count += reader;
//				System.out.println("reader is " + reader);

				dos.write(buf, 0, reader);
				if (count == fileLen) {
					break;
				}
			}
			
			dos.close();			
//			FileUtils.copyInputStreamToFile(is, new File(peerDir + "/" + filename));

			System.out.println("Done receiving file: " + filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.err.println("Cannot connect to the sender peer.");
		}

	}

}

