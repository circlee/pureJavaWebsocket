package test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha1Digest {

	
	public static String makeSha1(String origin){
		
		String returnStr = "";
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			
			md.update(origin.getBytes());
			
			byte[] digest = md.digest();
			
			returnStr = Base64.getEncoder().encodeToString(digest);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnStr;
	}
	
	public static void main(String[] atgs) {
		System.out.println(Sha1Digest.makeSha1("test"));
	}
}


