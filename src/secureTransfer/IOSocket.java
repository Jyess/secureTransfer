package securetransfer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class IOSocket {
	/**
	 * Retourne ce qu'un utilisateur écrit dans la console.
	 * 
	 * @return String une chaîne de caractères.
	 */
	static String readInput() {
		try {
			InputStreamReader in = new InputStreamReader(System.in); // récup l'input
			BufferedReader bf = new BufferedReader(in); // lit l'input
			return bf.readLine(); // affiche l'input
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "";
		}
	}

	/**
	 * Retourne ce qui a été écrit dans un socket.
	 * 
	 * @param s un socket.
	 * @return String une chaîne de caractères.
	 */
	static String readSocket(Socket s) {
		try {
			InputStreamReader in = new InputStreamReader(s.getInputStream()); // récup l'input
			BufferedReader bf = new BufferedReader(in); // lit l'input

			return bf.readLine(); // affiche l'input
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "";
		}
	}

	/**
	 * Retourne ce qui a été écrit dans un socket.
	 * 
	 * @param s un socket.
	 * @return une chaîne d'octets.
	 */
	static byte[] readByteSocket(Socket s) {
		byte[] buffer = new byte[0];

		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
			buffer = new byte[1024];
			in.read(buffer, 0, buffer.length);
			s.shutdownInput();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer;
	}

	/**
	 * Ecrit une chaîne de caractères dans un socket.
	 * 
	 * @param s   un socket.
	 * @param msg la chaîne de caractères à écrire.
	 */
	static void writeSocket(Socket s, String msg) {
		try (PrintWriter pr = new PrintWriter(s.getOutputStream())) {
			pr.println(msg);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Ecrit une chaîne d'octets dans un socket.
	 * 
	 * @param s   un socket.
	 * @param msg la chaîne d'octets à écrire.
	 */
	static void writeSocket(Socket s, byte[] msg) {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.write(msg);
			s.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le contenu d'un fichier.
	 * 
	 * @param filePath le chemin d'un fichier
	 * @return String le contenu d'un fichier
	 */
	static String readFile(File filePath) {
		BufferedReader reader;
		String ligne = "", fileContent = "";

		try {
			reader = new BufferedReader(new FileReader(filePath));
			while ((ligne = reader.readLine()) != null) {
				fileContent += (ligne + "\r\n");
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return fileContent;
	}

	/**
	 * Ecrit dans un fichier le contenu souhaité.
	 * 
	 * @param filePath    le chemin du fichier à écrire
	 * @param fileContent le contenu du fichier
	 */
	static void writeFile(File file, String fileContent) {
		try (PrintWriter writeInFile = new PrintWriter(new FileWriter(file))) {
			writeInFile.println(fileContent);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Ecrit dans un fichier le contenu souhaité.
	 * 
	 * @param filePath    le chemin du fichier à écrire
	 * @param fileContent le contenu du fichier
	 */
	static void writeFile(File file, byte[] fileContent) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(fileContent);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	static Key getPublicKey(byte[] publicKeyByte) {
		Key publicKey = null;

		try {
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyByte));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return publicKey;
	}
}

// PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new
// PKCS8EncodedKeySpec(privateKeyBytes));

// PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new
// X509EncodedKeySpec(bytes));