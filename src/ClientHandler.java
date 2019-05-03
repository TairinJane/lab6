import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;

    ClientHandler(Socket s) {
        super("ClientHandler");
        socket = s;
    }

    private static Gson gson = new Gson();

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());)
        {
            System.out.println("Got connection on "+socket);
            String request = "", answer;
            do {
                System.out.println(out);
                out.write("Hello client");
                out.flush();
                System.out.println("Waiting for next request from "+socket);
                request = in.readLine();
                System.out.println("Got client request: "+ request);
                /*out.write(request.toUpperCase());
                System.out.println("Passed answer for request");*/
                out.write("JOPA BLYAT");
                out.flush();
            } while (!request.equalsIgnoreCase("exit"));
            System.out.println("Disconnection...");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
