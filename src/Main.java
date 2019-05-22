public class Main {
    public static void main(String[] args) {
        if (args[0].equalsIgnoreCase("server")) {
            try {
                int port = Integer.parseInt(args[1]);
                new Thread(new Server(port)).start();
            } catch (NumberFormatException e) {
                System.out.println("Usage: (server | client <host>) <port>");
            }
        } else if (args[0].equalsIgnoreCase("client")) {
            try {
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                new Thread(new Client(host, port)).start();
            } catch (NumberFormatException e) {
                System.out.println("Usage: (server | client <host>) <port>");
            }
        } else {
            System.out.println("Usage: (server | client <host>) <port>");
        }
    }
}
