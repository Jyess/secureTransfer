package secureTransfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		try {
			ServerSocket socketServer = new ServerSocket(1234);
			
			while (true) {
				Socket socketClient = socketServer.accept();
				System.out.println("---Client connecté---");
				
				ServerClientThread thread = new ServerClientThread(socketClient);
				thread.start();
			}		
		} catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
}
