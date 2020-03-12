package securetransfer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerClientThread extends Thread {
    private Socket socketClient;

    public ServerClientThread(Socket socketClient) {
        this.socketClient = socketClient;
    }

    public void run() {
        try {
            final Crypt c = new Crypt("SERVER"); // génère la paire de clés

            final String pathServer = System.getProperty("user.dir") + File.separator;
            final String pathToFiles = pathServer + "files" + File.separator;
            final String pathToKeys = pathServer + "Keys" + File.separator;

            PublicKey publicKey = c.getPublicKey();
            PrivateKey privateKey = c.getPrivateKey();
            storeKeys(pathToKeys, publicKey, privateKey);

            // envoie la clé publique
            IOSocket.writeSocket(this.socketClient, publicKey.getEncoded());

            // récupère la clé secrète du client et la décode
            byte[] encodedSecretKeyFromClient = IOSocket.readByteSocket(this.socketClient);
            System.out.println(c.bytesToHexa(encodedSecretKeyFromClient));
            byte[] secretKeyClient = c.decode(Crypt.RSA, encodedSecretKeyFromClient, privateKey);
            System.out.println("La clé secrète du client est : " + secretKeyClient);

            // récupère la requete du client
            String request = IOSocket.readSocket(this.socketClient);

            String commande = "", filename = "";
            if (request != null) {
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
                    IOSocket.writeSocket(socketClient, "command not valid");
                    break;
            }

            // ferme
            this.socketClient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
    private void storeKeys(final String pathToKeys, PublicKey publicKey, PrivateKey privateKey) {
        boolean keysFolderCreated = new File(pathToKeys).mkdir();

        if (keysFolderCreated) {
            File publicKeyFile = new File(pathToKeys + "public_key");
            File privateKeyFile = new File(pathToKeys + "private_key");
            IOSocket.writeFile(publicKeyFile, publicKey.getEncoded());
            IOSocket.writeFile(privateKeyFile, privateKey.getEncoded());
        }
    }
}
