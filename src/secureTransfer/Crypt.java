package securetransfer;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * Classe pour gérer l'encodage et le décodage des données.
 */
public class Crypt {

	private Key key;

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public static final String DES = "DES";
	public static final String RSA = "RSA";

	/**
	 * Génère un clé
	 * 
	 * @param role CLIENT si côté client, SERVER si côté serveur
	 */
	public Crypt(String role) {
		if (role.equals("CLIENT"))
			generateKey();
		if (role.equals("SERVER"))
			generateKeyPair();
	}

	/**
	 * Génère une clé DES
	 */
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

	/**
	 * Génère une paire de clé (publique et privée)
	 */
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

	/**
	 * Encode un message avec une methode et une clé
	 * 
	 * @param method DES ou RSA
	 * @param input  le message à encoder
	 * @param key    la clé d'encodage
	 * @return le message encodé
	 */
	public byte[] encode(String method, byte[] input, Key key) {
		byte[] encodedMessage = null;

		try {
			Cipher c = Cipher.getInstance(method);
			c.init(Cipher.ENCRYPT_MODE, key);
			encodedMessage = c.doFinal(input);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			e.printStackTrace();
		}

		return encodedMessage;
	}

	/**
	 * Décode le message avec une méthode et une clé
	 * 
	 * @param method DES ou RSA
	 * @param input  le message à décoder en octets
	 * @param key    la clé de décodage
	 * @return le message décodé
	 */
	public byte[] decode(String method, byte[] input, Key key) {
		byte[] decodedMessage = null;

		try {
			Cipher c = Cipher.getInstance(method);
			c.init(Cipher.DECRYPT_MODE, key);
			System.out.println(input.length);
			decodedMessage = c.doFinal(input);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			e.printStackTrace();
		}

		return decodedMessage;
	}

	/**
	 * Convertit une chaîne d'octets en hexadécimal.
	 * 
	 * @param res une chaîne d'octets
	 * @return une chaîne de caractères
	 */
	public String bytesToHexa(byte[] res) {
		StringBuilder sb = new StringBuilder();

		for (byte b : res) {
			sb.append(String.format("%02x", b));
		}

		return sb.toString();
	}

	/**
	 * Récupère la clé secrète
	 * 
	 * @return clé secrète DES
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * Définit la clé secrète
	 * 
	 * @param key clé secrète DES
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * Récupère la clé publique
	 * 
	 * @return clé publique
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}

	/**
	 * Définit la clé publique
	 * 
	 * @param publicKey clé publique
	 */
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * Récupère la clé privée
	 * 
	 * @return clé privée
	 */
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * Définit la clé privée
	 * 
	 * @param privateKey clé privée
	 */
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public static void main(String[] args) {
		// String input = "text";

		// Crypt c = new Crypt("CLIENT");

		// byte[] res = c.encode(Crypt.DES, input.getBytes(), c.getKey());
		// System.out.println("Encodé : " + c.bytesToHexa(res));

		// byte[] res2 = c.decode(Crypt.DES, res, c.getKey());
		// System.out.println("Décodé : " + new String(res2));
	}
}
