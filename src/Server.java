import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.PriorityBlockingQueue;

public class Server implements Runnable {
    private final int PORT;

    Server(int port) {
        this.PORT = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            if (Files.exists(Paths.get("outputCollection.txt"))) {
                Type queueType = new TypeToken<PriorityBlockingQueue<Cloud>>() {
                }.getType();
                PriorityBlockingQueue<Cloud> queue = (new Gson()).fromJson(String.join(" ", Files.readAllLines(Paths.get("outputCollection.txt"))), queueType);
                CollectionCommander.importCollection(queue);
            }
            System.out.println("Server created, waiting for clients...");
            while (!serverSocket.isClosed()) {
                new Thread(new Receiver(serverSocket.accept())).start();
                System.out.println("New client connected.");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Could not listen on port " + PORT);
        }
    }

    public static void main(String[] args) {
        new Thread(new Server(1600)).start();
    }
}
