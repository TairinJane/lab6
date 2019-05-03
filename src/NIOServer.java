import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NIOServer implements Runnable {
    private final int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(256);
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

                    if (key.isAcceptable()) acceptClient(key);
                    if (key.isReadable()) readInput(key);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
            e.printStackTrace();
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        String address = (new StringBuilder(sc.socket().getInetAddress().toString())).append(":").append(sc.socket().getPort()).toString();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ, address);
        sc.write(welcomeBuf);
        welcomeBuf.rewind();
        System.out.println("accepted connection from: " + address);
    }

    private void readInput(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        DataInputStream in = new DataInputStream(Channels.newInputStream(ch));
        String input = in.readUTF();
        /*try {
            collection.Cloud cloud = (collection.Cloud) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("Class of given object not found");
        }*/


//        StringBuilder sb = new StringBuilder();
//
//        buf.clear();
//        int read = 0;
//        while( (read = ch.read(buf)) > 0 ) {
//            buf.flip();
//            byte[] bytes = new byte[buf.limit()];
//            buf.get(bytes);
//            sb.append(new String(bytes));
//            buf.clear();
//        }
//        String msg;
//        if(read<0) {
//            msg = key.attachment()+" left the chat.\n";
//            ch.close();
//        }
//        else {
//            msg = key.attachment()+": "+sb.toString();
//        }
//
//        System.out.println(msg);
//        broadcast(msg);
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
        NIOServer server = new NIOServer(10523);
        (new Thread(server)).start();
    }

}