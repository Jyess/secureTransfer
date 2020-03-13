package securetransfer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.spec.SecretKeySpec;

public class ServerClientThread extends Thread {
    private Socket socketClient;

    public ServerClientThread(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public void run() {
        try {
            String pathServer = System.getProperty("user.dir") + File.separator;
            String pathToFiles = pathServer + "files" + File.separator;
            // String pathToKeys = pathServer + "Keys" + File.separator;

            Crypt c = new Crypt("SERVER"); // génère la paire de clés

            PublicKey publicKey = c.getPublicKey();
            PrivateKey privateKey = c.getPrivateKey();

            // envoie la clé publique
            IOSocket.writeSocket(this.socketClient, publicKey.getEncoded());

            // récupère la clé secrète du client et la décode
            byte[] encodedSecretKeyFromClient = IOSocket.readByteSocket(this.socketClient);
            byte[] secretKeyClientByte = c.decode(Crypt.RSA, encodedSecretKeyFromClient, privateKey);
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyClientByte, "DES");

            byte[] encodedRequest = IOSocket.readByteSocket(this.socketClient);
            byte[] decodedRequest = c.decode(Crypt.DES, encodedRequest, secretKey);
            String request = new String(decodedRequest);

            String commande = "", filename = "";
            if (!request.equals("error")) {
                System.out.println("Requête client -> " + request + "\r\n");

                String[] splitRequest = request.split(" ");

                if (splitRequest.length >= 1) {
                    commande = splitRequest[0];
                }

                if (splitRequest.length == 2) {
                    filename = splitRequest[1];
                }
            }

            switch (commande) {
                case "GET":
                    File fileServer = new File(pathToFiles + filename);

                    if (fileServer.exists()) {
                        String fileContent = IOSocket.readFile(fileServer);
                        byte[] encodedFileContent = c.encode(Crypt.DES, fileContent.getBytes(), secretKey);

                        IOSocket.writeSocket(this.socketClient, encodedFileContent); // 3
                    } else {
                        IOSocket.writeSocket(this.socketClient, "error"); // 3
                    }

                    break;
                case "PUT":
                    byte[] encodedFileContent = IOSocket.readByteSocket(socketClient);
                    byte[] decodedFileContent = c.decode(Crypt.DES, encodedFileContent, secretKey);
                    String fileContent = new String(decodedFileContent);

                    if (!fileContent.equals("error")) {
                        File fileClient = new File(pathToFiles + filename);
                        fileClient.createNewFile();

                        IOSocket.writeFile(fileClient, fileContent);
                    }

                    break;
                case "QUIT":
                    this.socketClient.close();
                    System.out.println("---Le client s'est déconnecté---");
                    break;
                default:
                    System.out.println("Wrong request");
                    break;
            }

            // ferme
            this.socketClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Enregistre la paire de clés dans des fichiers
     * 
     * @param pathToKeys chemin vers le repertoire des clés
     * @param publicKey  clé publique
     * @param privateKey clé privée
     */
    // private void storeKeys(final String pathToKeys, PublicKey publicKey,
    // PrivateKey privateKey) {
    // boolean keysFolderCreated = new File(pathToKeys).mkdir();

    // if (keysFolderCreated) {
    // File publicKeyFile = new File(pathToKeys + "public_key");
    // File privateKeyFile = new File(pathToKeys + "private_key");
    // IOSocket.writeFile(publicKeyFile, publicKey.getEncoded());
    // IOSocket.writeFile(privateKeyFile, privateKey.getEncoded());
    // }
    // }
}
