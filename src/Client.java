import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Client {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1441);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in)))
        {
            System.out.println("Connected to "+socket);
            System.out.println(in);
            System.out.println(in.readLine());
            String line, answer;
            do {
                System.out.println("Print your request");
                line = console.readLine();
                out.write(line);
                out.flush();
                System.out.println("Line was passed to server "+socket);
                answer = in.readLine();
                System.out.println("Got the answer: "+ answer);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}