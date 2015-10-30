package dhtfs.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyHash {
	
	/**
	 * Generate the HashValue of dhtKey
	 * @param dthKey
	 * @return
	 */
	
	public static String genHash(String dthKey){
	
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     md.update(dthKey.getBytes());
	        
	     byte byteData[] = md.digest();
	 
	     //convert the byte to hex format
	     StringBuffer sb = new StringBuffer();
	     for (int i = 0; i < byteData.length; i++) {
	    	 
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	      }
		  
	     return sb.toString();
	}
	
	/**
	 * Get the peer index from file HashValue
	 * @param hashhex
	 * @return
	 */
	public static int getIndex(String hashhex, int peerNum){
		  
		 //TODO: use hash value to find the index
	     
	     BigInteger hashint = new BigInteger(hashhex,16);
	     BigInteger bigsize = new BigInteger(Integer.toString(peerNum),10);
	     BigInteger bigindex = hashint.mod(bigsize);
	     int index = bigindex.intValue() + 1;
	
	     return index;
	     
	}
	
	
	

}
