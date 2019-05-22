import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

public class Receiver implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private DataOutputStream out;

    private static Gson gson = new Gson();

    Receiver(Socket socket) {
        this.socket = socket;
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            writeAnswer("IO Exception while creating in or out");
        }
        writeAnswer(CollectionCommander.help());
    }

    @Override
    public void run() {
        int commandType;
        Commands command;
        try {
            while (!socket.isClosed()) {
                try {
                    commandType = in.readInt();
                    command = Commands.valueOf(in.readUTF().trim().toUpperCase());
                    if (commandType == 0) {
                        switch (command) {
                            case HELP:
                                writeAnswer(CollectionCommander.help());
                                break;
                            case REMOVE_FIRST:
                                writeAnswer(CollectionCommander.removeFirst());
                                break;
                            case SHOW:
                                writeAnswer(CollectionCommander.show());
                                break;
                            case INFO:
                                writeAnswer(CollectionCommander.info());
                                break;
                            case QUIT:
                                System.out.println("Client disconnected");
                                socket.close();
                                break;
                            default:
                                writeAnswer("No such command");
                        }
                    } else if (commandType == 2) {
                        Cloud cloud;
                        try {
                            cloud = (Cloud) in.readObject();
                            switch (command) {
                                case REMOVE:
                                    writeAnswer(CollectionCommander.removeElement(cloud));
                                    break;
                                case ADD:
                                    writeAnswer(CollectionCommander.addElement(cloud));
                                    break;
                                case REMOVE_ALL:
                                    writeAnswer(CollectionCommander.removeAll(cloud));
                                    break;
                                default:
                                    writeAnswer("No such command");
                            }
                        } catch (ClassNotFoundException e) {
                            writeAnswer("Class not found");
                        }
                    } else {
                        String fileLines;
                        try {
                            fileLines = in.readUTF();
                            Type queueType = new TypeToken<PriorityBlockingQueue<Cloud>>() {
                            }.getType();
                            PriorityBlockingQueue<Cloud> queue = gson.fromJson(fileLines, queueType);
                            writeAnswer(CollectionCommander.importCollection(queue));
                        } catch (JsonSyntaxException e) {
                            writeAnswer("Invalid JSON");
                        } catch (IOException e) {
                            writeAnswer("IO Exception");
                        }

                    }
                } catch (IllegalArgumentException e) {
                    writeAnswer("No such command");
                } catch (IOException e) {
                    writeAnswer("IO Exception");
                }
            }
        } catch (Exception e) {
            System.out.println("Client closed");
        }
    }

    private void writeAnswer(String answer) {
        try {
            out.writeUTF(answer);
            out.flush();
        } catch (IOException ex) {
            try {
                socket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                System.out.println("Exception closing socket");
            }
        }
    }
}
