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
			e.printStackTrace();
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
			e.printStackTrace();
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
			int length = in.readInt();
			buffer = new byte[length];
			in.read(buffer, 0, buffer.length);
			// s.shutdownInput();
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
		try {
			PrintWriter pr = new PrintWriter(s.getOutputStream());
			pr.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
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
			out.writeInt(msg.length);
			out.write(msg);
			// s.shutdownOutput();
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

			int lastIndex = fileContent.lastIndexOf("\r\n");
			fileContent = fileContent.substring(0, lastIndex);

			reader.close();
		} catch (IOException e) {
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
		try {
			PrintWriter writeInFile = new PrintWriter(new FileWriter(file));
			writeInFile.println(fileContent);
			writeInFile.close();
		} catch (IOException e) {
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
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(fileContent);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}