/**
 * 
 */
package dhtfs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author siliu Transfer class is to deal with the message transfer Message is
 *         composed by two parts: command and content
 */
public class Transfer {

	// Receive message from ObjectInputStream
	public static Message receiveMessage(InputStream is) throws IOException {

		Message msg = new Message();

		ObjectInputStream ois;
		try {

			ois = new ObjectInputStream(is);
			msg = (Message) ois.readObject();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;

	}

	// Send message by converting it to ObjectOutputStream
	public static void sendMessage(Message msg, OutputStream os) throws IOException {

		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(msg);
		oos.flush();

	}
}
