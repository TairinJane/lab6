package collection;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Receiver implements Runnable {
    private SelectionKey key;
    private SocketChannel channel;
    private DataInputStream in;

    private static Gson gson = new Gson();

    public Receiver(SelectionKey key) {
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.in = new DataInputStream(Channels.newInputStream(channel));
    }

    @Override
    public void run() {
        try {
            String line = in.readUTF().trim();
            String command = line.split(" ", 2)[0];
            String object;
            if (line.contains(" ")) {
                object = line.split(" ", 2)[1];
                try {
                    Cloud cloud = gson.fromJson(object, Cloud.class);
                    switch (command) {
                        case "remove_all":
                            writeAnswer(CollectionCommander.removeAll(cloud));
                            break;
                        case "remove":
                            writeAnswer(CollectionCommander.removeElement(cloud));
                            break;
                        //TODO: import: придется формировать объекты на стороне клиента????
                        //case "import": writeAnswer(CollectionCommander.importCollection()); break;
                        case "add":
                            writeAnswer(CollectionCommander.addElement(cloud));
                            break;
                        default:
                            writeAnswer("No such command. Write 'help' to see commands.\n");
                    }
                } catch (JsonSyntaxException e) {
                    writeAnswer("Invalid json!" +
                            "\nJson must look like: {\"nickname\": \"Cloud0\", \"speed\": 10, " +
                            "color\": \"white\"}" +
                            "\nYou can use only nickname or speed if you want.\n");
                }
            } else
                switch (command) {
                    case "show":
                        writeAnswer(CollectionCommander.show());
                        break;
                    case "info":
                        writeAnswer(CollectionCommander.info());
                        break;
                    case "remove_first":
                        writeAnswer(CollectionCommander.removeFirst());
                        break;
                    case "help":
                        writeAnswer(CollectionCommander.help());
                        break;
                    default:
                        writeAnswer("No such command. Write 'help' to see commands.\n");
                }

        } catch (IOException e) {
            System.out.println("IO Exception while reading channel");
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
