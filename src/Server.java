import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 1600;
    //private static int clientCount = 0;

    public static void main(String[] args) {
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server created, waiting for clients...");
            while (listening) {
                new ClientHandler(serverSocket.accept()).start();
                System.out.println("New client connected.");
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }

    }

}
