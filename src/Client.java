import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client implements Runnable {
    private DataInputStream in;
    private ObjectOutputStream out;
    private static Gson gson = new Gson();
    private Socket socket;
    private Scanner console;
    private final int PORT;
    private final String HOST;

    Client(String host, int port) {
        this.PORT = port;
        this.HOST = host;
    }

    public void run() {
        if (!tryReconnect()) return;
        String command, obj, answer;
        int commandType;
        try {
            System.out.println(in.readUTF());
        } catch (SocketException e) {
            tryReconnect();
        } catch (IOException e) {
            System.out.println("Exception while reading help. HELP");
        }
        try {
            do {
                System.out.println("Print your request");
                command = console.next();
                if (command.equalsIgnoreCase("quit")) {
                    closeClient();
                    break;
                }
                obj = console.nextLine().trim();
                if (obj.equals("")) {
                    commandType = 0;
                    writeRequest(commandType, command);
                } else if (command.equalsIgnoreCase("import")) {
                    commandType = 1;
                    Path filePath = Paths.get(obj).toAbsolutePath();
                    System.out.println("File path: " + filePath);
                    if (!Files.exists(filePath)) {
                        System.out.println("File doesn't exist!");
                        continue;
                    } else if (!Files.isReadable(filePath)) {
                        System.out.println("File isn't readable!");
                        continue;
                    } else {
                        try {
                            String fileLines = String.join("\n", Files.readAllLines(filePath));
                            writeRequest(commandType, command, fileLines);
                            out.flush();
                        } catch (IOException e) {
                            System.out.println("IO Exception while reading file");
                            continue;
                        } catch (JsonSyntaxException e) {
                            System.out.println("Invalid json file." +
                                    "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                                    "color\": \"white\"}" +
                                    "\nYou can use only nickname or speed if you want.\n");
                            continue;
                        }
                    }
                } else {
                    commandType = 2;
                    try {
                        Cloud cloud = gson.fromJson(obj, Cloud.class);
                        writeRequest(commandType, command, cloud);
                    } catch (JsonSyntaxException e) {
                        System.out.println("Invalid json." +
                                "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                                "color\": \"white\"}" +
                                "\nYou can use only nickname or speed if you want.\n");
                        continue;
                    }
                }
                try {
                    answer = in.readUTF();
                    System.out.println(answer);
                } catch (SocketException e) {
                    tryReconnect();
                } catch (IOException e) {
                    System.out.println("IO Exception while receiving answer from server");
                }
            } while (true);
        } catch (Exception e) {
            closeClient();
        }
    }

    public static void main(String[] args) {
        new Thread(new Client("localhost", 1600)).start();
    }

    private void writeRequest(int commandType, String command, Cloud cloud) {
        try {
            out.writeInt(commandType);
            out.writeUTF(command);
            out.writeObject(cloud);
            out.flush();
        } catch (SocketException e) {
            tryReconnect();
        } catch (IOException e) {
            System.out.println("IO Exception while writing to server");
        }
    }

    private void writeRequest(int commandType, String command) {
        try {
            out.writeInt(commandType);
            out.writeUTF(command);
            out.flush();
        } catch (SocketException e) {
            tryReconnect();
        } catch (IOException e) {
            System.out.println("IO Exception while writing to server");
        }
    }

    private void writeRequest(int commandType, String command, String fileLines) {
        try {
            out.writeInt(commandType);
            out.writeUTF(command);
            out.writeUTF(fileLines);
            out.flush();
        } catch (SocketException e) {
            tryReconnect();
        } catch (IOException e) {
            System.out.println("IO Exception while writing to server");
        }
    }

    private boolean tryReconnect() {
        boolean connected = false;
        while (!connected) {
            try {
                this.socket = new Socket(HOST, PORT);
                connected = true;
                System.out.println("Connected to server on port " + PORT);
                this.out = new ObjectOutputStream(socket.getOutputStream());
                this.in = new DataInputStream(socket.getInputStream());
                this.console = new Scanner(System.in);
            } catch (UnknownHostException e) {
                System.out.println("Host doesn't exist");
                return false;
            } catch (IOException e) {
                System.out.println("Server is not available");
                System.out.println("Trying to connect again in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    System.out.println("Exception in sleeping");
                }
            }
        }
        return true;
    }

    private void closeClient() {
        try {
            writeRequest(0, "quit");
            System.out.println("Closing client...");
            socket.close();
        } catch (IOException e) {
            System.out.println("IO Exception while closing socket");
        }
    }
}