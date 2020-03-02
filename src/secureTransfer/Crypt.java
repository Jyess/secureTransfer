package secureTransfer;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * Classe pour gérer l'encodage et le décodage des données.
 * @author Axel ighir
 *
 */
public class Crypt {
	
	private Key key;
	
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public static final String DES = "DES";
	public static final String RSA = "RSA";
	
	public Crypt(String role) {
		if (role.equals("CLIENT")) generateKey();
		if (role.equals("SERVER")) generateKeyPair();
	}
	
	private void generateKey() {		
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance(Crypt.DES);
			keyGen.init(56);
			key = keyGen.generateKey();
			setKey(key);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private void generateKeyPair() {		
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Crypt.RSA);
			keyGen.initialize(1024);
			
			KeyPair keyPair = keyGen.genKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			setPublicKey(publicKey);
			setPrivateKey(privateKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] encode(String method, String input, Key key) {
		byte[] encodedMessage = null;
		
		try {
			Cipher c = Cipher.getInstance(method);
			c.init(Cipher.ENCRYPT_MODE, key);
			encodedMessage = c.doFinal(input.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		return encodedMessage;
	}
	
	public String decode(String method, byte[] input, Key key) {
		String decodedMessage = null;
		
		try {
			Cipher c = Cipher.getInstance(method);
			c.init(Cipher.DECRYPT_MODE, key);
			decodedMessage = new String(c.doFinal(input));
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		return decodedMessage;
	}
	
	public String bytesToHexa(byte[] res) {
        StringBuilder sb = new StringBuilder();
        
        for (byte b : res) {
            sb.append(String.format("%02x", b));
        }
        
		return sb.toString();
	}
	
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	public static void main(String[] args) {		
//		System.out.println("A coder : ");
//		Scanner sc = new Scanner(System.in);
//		String input = sc.nextLine();
//		sc.close();
//		
//		Crypt c = new Crypt();
//		
//		byte[] res = c.encode(Crypt.DES, input, c.getKey());
//		System.out.println("Encodé : " + c.bytesToHexa(res));
//		
//		String res2 = c.decode(Crypt.DES, res, c.getKey());
//		System.out.println("Décodé : " + res2);
	}
}
