import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer implements Runnable {
    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Successfully connected to Server\n".getBytes());

    NIOServer(int port) throws IOException {
        this.port = port;
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(port));
        this.ssc.configureBlocking(false);
        this.selector = Selector.open();
        this.ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server starting on port " + this.port);

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (ssc.isOpen()) {
                selector.select();
                iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();
                    if (!key.isValid()) key.cancel();
                    if (key.isAcceptable()) {
                        System.out.println("Accepting new client");
                        acceptClient(key);
                    }
                    if (key.isReadable()) {
                        System.out.println("Reading input");
                        readInput(key);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
            e.printStackTrace();
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        // Runnable acceptClient = () -> {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            sc.write(welcomeBuf);
            welcomeBuf.rewind();
            System.out.println("Accepted new client");
        } catch (IOException e) {
            System.out.println("IO Exception while connecting new Client");
        }
        //};
        // new Thread(acceptClient).start();
    }

    private void readInput(SelectionKey key) throws IOException {
        new Thread(new Receiver(key)).start();

    }

    private void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NIOServer server = new NIOServer(1600);
        (new Thread(server)).start();
    }

}