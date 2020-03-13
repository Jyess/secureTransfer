package securetransfer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;

public class Client {
	public static void main(String[] args) {

		Crypt c = new Crypt("CLIENT");

		while (true) {
			try {
				Socket socketClient = new Socket("localhost", 1234);

				String commande = "", filename = "";
				String pathClient = System.getProperty("user.dir") + File.separator;
				String pathToFiles = pathClient + "files" + File.separator;
				File dossierFiles = new File(pathClient + "files");

				if (!dossierFiles.exists())
					dossierFiles.mkdir();

				// recoit la clé publique
				byte[] publicKey = IOSocket.readByteSocket(socketClient);
				Key publicKeyFromServer = c.getPublicKey(publicKey);

				// encode la clé secrète et l'envoie
				Key secretKey = c.getKey();
				byte[] encodedSecretKey = c.encode(Crypt.RSA, secretKey.getEncoded(), publicKeyFromServer);
				IOSocket.writeSocket(socketClient, encodedSecretKey); // envoie la clé secrète

				String request = IOSocket.readInput(); // attend qu'on écrive la requete
				String[] splitRequest = request.split(" "); // split la requete

				if (splitRequest.length > 0) {
					commande = splitRequest[0];
				}

				if (splitRequest.length == 2) {
					filename = splitRequest[1];
				}

				byte[] encodedRequest = c.encode(Crypt.DES, request.getBytes(), secretKey);

				switch (commande) {
					case "GET":
						if (!filename.isEmpty()) {
							IOSocket.writeSocket(socketClient, encodedRequest); // 1
							byte[] encodedResponse = IOSocket.readByteSocket(socketClient); // 4

							byte[] decodedReponse = c.decode(Crypt.DES, encodedResponse, secretKey);
							String response = new String(decodedReponse);

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
						IOSocket.writeSocket(socketClient, encodedRequest);

						if (!filename.isEmpty()) {
							File fileClient = new File(dossierFiles + File.separator + filename);

							if (fileClient.exists()) {
								String fileContent = IOSocket.readFile(fileClient);
								byte[] encodedFileContent = c.encode(Crypt.DES, fileContent.getBytes(), secretKey);
								IOSocket.writeSocket(socketClient, encodedFileContent);

								System.out.println("Le fichier a bien été envoyé au serveur.");
							} else {
								byte[] encodedError = c.encode(Crypt.DES, new String("error").getBytes(), secretKey);
								IOSocket.writeSocket(socketClient, encodedError);
								System.out.println(
										"Le fichier n'existe pas. Vérifiez qu'il existe bien dans le dossier 'files'.");
							}
						} else {
							System.out.println("Le chemin du fichier est manquant.");
						}

						break;

					case "QUIT":
						IOSocket.writeSocket(socketClient, encodedRequest); // 1
						socketClient.close();
						System.exit(0);
						break;

					default:
						byte[] error = c.encode(Crypt.DES, new String("error").getBytes(), secretKey);
						IOSocket.writeSocket(socketClient, error);
						System.out.println("Commande non valide");
						break;
				}

				// ferme
				socketClient.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
