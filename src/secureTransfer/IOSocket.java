package secureTransfer;

import java.io.BufferedReader;
import java.io.File;
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
			System.out.println(e.getMessage());
			return "";
		}
	}

	/**
	 * Retourne ce qui a été écrit dans un socket.
	 * 
	 * @param s 		un socket.
	 * @return String 	une chaîne de caractères.
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
	 * Ecrit une chaîne de caractères dans un socket.
	 * 
	 * @param s   un socket.
	 * @param msg la chaîne de caractères à écrire.
	 */
	static void writeSocket(Socket s, String msg) {
		try {
			PrintWriter pr = new PrintWriter(s.getOutputStream()); // pour écrire dans l'output
			pr.println(msg);
			pr.flush();
			// pr.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Retourne le contenu d'un fichier.
	 * @param filePath 	le chemin d'un fichier
	 * @return String 	le contenu d'un fichier
	 */
	static String readFile(File filePath) {
		BufferedReader reader;
		String ligne, fileContent = "";

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
	 * @param filePath 		le chemin du fichier à écrire
	 * @param fileContent 	le contenu du fichier
	 */
	static void writeFile(File file, String fileContent) {
		try {
			PrintWriter writeInFile = new PrintWriter(new FileWriter(file));
			writeInFile.println(fileContent);
			writeInFile.flush();
			// writeInFile.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}