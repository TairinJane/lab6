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

    private Client() {
        boolean connected = false;
        while (!connected) {
            try {
                this.socket = new Socket("localhost", 1600);
                connected = true;
                System.out.println("Connected to server on port 1600");
                this.out = new ObjectOutputStream(socket.getOutputStream());
                this.in = new DataInputStream(socket.getInputStream());
                //System.out.println(in.readUTF());
                this.console = new Scanner(System.in);
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
    }

    public void run() {
        String command, obj, answer;
        int commandType;
        try {
            System.out.println(in.readUTF());
        } catch (IOException e) {
            System.out.println("Exception while reading help. HELP");
        }
        do {
            System.out.println("Print your request");
            command = console.next();
            if (command.equalsIgnoreCase("quit")) {
                try {
                    writeRequest(0, command);
                    socket.close();
                    break;
                } catch (IOException e) {
                    System.out.println("IO Exception while closing socket");
                    continue;
                }
            }
            obj = console.nextLine().trim();
            System.out.println("Got request: " + command);
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
                        //System.out.println("Command out");
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
                    //System.out.println("Command out");
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
            } catch (IOException e) {
                System.out.println("IO Exception while receiving answer from server");
            }
            //System.out.println(command + "*" + obj + "*");

        } while (true);
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }

    private void writeRequest(int commandType, String command, Cloud cloud) {
        try {
            out.writeInt(commandType);
            out.writeUTF(command);
            out.writeObject(cloud);
            out.flush();
        } catch (IOException e) {
            System.out.println("IO Exception while writing to server");
        }
    }

    private void writeRequest(int commandType, String command) {
        try {
            out.writeInt(commandType);
            out.writeUTF(command);
            out.flush();
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
        } catch (IOException e) {
            System.out.println("IO Exception while writing to server");
        }
    }
}