package main.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tomcat.util.codec.binary.Base64;

public class CustomBasicDataSource extends BasicDataSource {
	
	public synchronized void setPassword(String encryptedPassword){	
		super.setPassword(base64Decode(encryptedPassword));		
   }
	
	/**
	 * @param token
	 * @return encoded
	 */
	public static String base64Encode(String token) {
		
		byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
		return new String(encodedBytes);
	}

	/**
	 * @param token
	 * @return
	 */
	public static String base64Decode(String token) {
		byte[] decodedBytes = Base64.decodeBase64(token);
	    return new String(decodedBytes);
	}	 	
}
