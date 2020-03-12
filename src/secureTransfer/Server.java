package securetransfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket socketServer;

	public static void main(String[] args) {
		try {
			socketServer = new ServerSocket(1234);

			while (true) {
				Socket socketClient = socketServer.accept();
				System.out.println("---Client connecté---");

				ServerClientThread thread = new ServerClientThread(socketClient);
				thread.start();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		} finally {
			try {
				socketServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
