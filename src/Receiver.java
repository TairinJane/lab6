import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Receiver implements Runnable {
    private SelectionKey key;
    private SocketChannel channel;
    private ObjectInputStream in;

    private static Gson gson = new Gson();

    Receiver(SelectionKey key) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        try {
            this.in = new ObjectInputStream(Channels.newInputStream(channel));
        } catch (IOException e) {
            writeAnswer("IO Exception");
        }
    }

    @Override
    public void run() {
        int commandType;
        Commands command;
        try {
            commandType = in.readInt();
            command = Commands.valueOf(in.readUTF());
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
                    }
                } catch (ClassNotFoundException e) {
                    writeAnswer("Class not found");
                }
            } else {
                String fileLines;
                try {
                    fileLines = in.readUTF();
                    Type queueType = new TypeToken<PriorityQueue<Cloud>>() {
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

    private void writeAnswer(String answer) {
        try {
            channel.write(ByteBuffer.wrap(answer.getBytes()));
        } catch (IOException ex) {
            System.out.println("IO Exception when writing to channel");
        }
    }
}
