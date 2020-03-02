package secureTransfer;

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
            String pathServer = System.getProperty("user.dir") + File.separator;
            String pathToFiles = pathServer + "files" + File.separator;
            String pathToKeys = pathServer + "Keys" + File.separator;
            		
    		Crypt c = new Crypt("SERVER"); //génère la paire de clés
			
    		PublicKey publicKey = c.getPublicKey();
			PrivateKey privateKey = c.getPrivateKey();
			
			File publicKeyFile = new File(pathToKeys + "public_key.txt");
			File privateKeyFile = new File(pathToKeys + "private_key.txt");
			IOSocket.writeFile(publicKeyFile, publicKey); //doit donner un string
			IOSocket.writeFile(privateKeyFile, privateKey); //doit donner un string

            String request = IOSocket.readSocket(this.socketClient); //2
            
            String commande = "";
            String filename = "";
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
                        //à crypter
                        IOSocket.writeSocket(this.socketClient, fileContent); //3
                    } else {
                        IOSocket.writeSocket(this.socketClient, "error"); //3
                    }

                    break;
                case "PUT":
                    String fileContent = IOSocket.readSocket(socketClient);
                    //à décrypter
                    
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
            
            //ferme
            this.socketClient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }
}
