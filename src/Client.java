import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    Client() {
        try {
            this.socket = new Socket("localhost", 1600);
            System.out.println("Connected to server on port 1600");
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            System.out.println(in.readUTF());
            this.console = new Scanner(System.in);
        } catch (IOException e) {
            System.out.println("Exception while trying to connect");
        }
    }

    public void run() {
        String command, obj, answer;
        int commandType;
        do {
            System.out.println("Print your request");
            command = console.next();
            if (command.equalsIgnoreCase("quit")) {
                try {
                    socket.close();
                    break;
                } catch (IOException e) {
                    System.out.println("IO Exception while closing socket");
                }
            }
            obj = console.nextLine().trim();
            if (obj.equals("")) {
                commandType = 0;
                try {
                    out.writeInt(commandType);
                    out.writeUTF(command);
                } catch (IOException e) {
                    System.out.println("IO Exception while writing to server");
                }
            } else if (command.equalsIgnoreCase("import")) {
                commandType = 1;
                Path filePath = Paths.get(obj);
                if (Files.notExists(filePath)) {
                    System.out.println("File doesn't exist!");
                } else if (!Files.isReadable(filePath)) {
                    System.out.println("File isn't readable!");
                } else {
                    try {
                        String fileLines = String.join("\n", Files.readAllLines(filePath));
                        out.writeInt(commandType);
                        out.writeUTF(command);
                        out.writeUTF(fileLines);
                    } catch (IOException e) {
                        System.out.println("IO Exception while reading file");
                    } catch (JsonSyntaxException e) {
                        System.out.println("Invalid json file." +
                                "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                                "color\": \"white\"}" +
                                "\nYou can use only nickname or speed if you want.\n");
                    }
                }
            } else {
                commandType = 2;
                try {
                    Cloud cloud = gson.fromJson(obj, Cloud.class);
                    out.writeInt(commandType);
                    out.writeUTF(command);
                    out.writeObject(cloud);
                } catch (IOException e) {
                    System.out.println("IO Exception while writing to server");
                } catch (JsonSyntaxException e) {
                    System.out.println("Invalid json." +
                            "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                            "color\": \"white\"}" +
                            "\nYou can use only nickname or speed if you want.\n");
                }
            }
            try {
                answer = in.readUTF();
                System.out.println(answer);
            } catch (IOException e) {
                System.out.println("IO Exception while receiving answer from server");
            }
            System.out.println(command + "*" + obj + "*");

        } while (true);
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
}