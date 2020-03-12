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
            final String pathServer = System.getProperty("user.dir") + File.separator;
            final String pathToFiles = pathServer + "files" + File.separator;
            // final String pathToKeys = pathServer + "Keys" + File.separator;

            final Crypt c = new Crypt("SERVER"); // génère la paire de clés

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
            if (!request.equals("ERROR")) {
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
                        // TODO à crypter
                        IOSocket.writeSocket(this.socketClient, fileContent); // 3
                    } else {
                        IOSocket.writeSocket(this.socketClient, "error"); // 3
                    }

                    break;
                case "PUT":
                    String fileContent = IOSocket.readSocket(socketClient);
                    // TODO à décrypter

                    File fileClient = new File(pathToFiles + filename);
                    fileClient.createNewFile();

                    System.out.println(fileContent);
                    IOSocket.writeFile(fileClient, fileContent);
                    break;
                case "QUIT":
                    this.socketClient.close();
                    System.out.println("---Le client s'est déconnecté---");
                    break;
                default:
                    // IOSocket.writeSocket(socketClient, "command not valid");
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
