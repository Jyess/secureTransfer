package secureTransfer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		while (true) {
			try {
				Socket socketClient = new Socket("localhost", 1234);
				
				String pathClient = System.getProperty("user.dir") + File.separator;
				String pathToFiles = pathClient + "files" + File.separator;
				File dossierFiles = new File(pathClient + "files");

				if (!dossierFiles.exists()) {
					dossierFiles.mkdir();
				}
				
				String commande = "", filename = "";
				String request = IOSocket.readInput(); //wait here
				String[] splitRequest = request.split(" ");

				if (splitRequest.length >= 1) {
					commande = splitRequest[0];
				}
				
				if (splitRequest.length == 2) {
					filename = splitRequest[1];
				}

				switch (commande) {
					case "GET":
						if (!filename.isEmpty()) {
							IOSocket.writeSocket(socketClient, request); //1
							String response = IOSocket.readSocket(socketClient); //4
							
							//à décrypter

							if (!response.equals("error")) {
								File fileClient = new File(pathToFiles + filename); // ./files/abc.txt
								IOSocket.writeFile(fileClient, response);				
	
								System.out.println("Le fichier a bien été reçu.");
							} else {
								System.out.println("Le fichier \"" + filename + "\" n'existe pas.");
							}
						} else {
							System.out.println("Le chemin du fichier est manquant.");
						}
			
						break;

					case "PUT":
						IOSocket.writeSocket(socketClient, request);
						
						if (!filename.isEmpty()) {							
							File fileClient = new File(dossierFiles + File.separator + filename);

							if (fileClient.exists()) {
								String fileContent = IOSocket.readFile(fileClient);
								//à encrypter
								IOSocket.writeSocket(socketClient, fileContent);
								
								System.out.println("Le fichier a bien été envoyé au serveur.");
							} else {
								System.out.println("Le fichier n'existe pas. Vérifiez qu'il existe bien dans le dossier 'files'.");
							}
						} else {
							System.out.println("Le chemin du fichier est manquant.");
						}

						break;

					case "QUIT":
						IOSocket.writeSocket(socketClient, request);
						socketClient.close();
						System.exit(0);
						break;

					default:
						System.out.println("Commande non valide");
						break;
				}

				//ferme
				socketClient.close();
			} catch(IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
