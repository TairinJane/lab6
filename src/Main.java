public class Main {
    public static void main(String[] args) {
        if (args[0].equalsIgnoreCase("server")) {
            try {
                int port = Integer.parseInt(args[1]);
                new Thread(new Server(port)).start();
            } catch (NumberFormatException e) {
                System.out.println("Usage: (server | client) <port>");
            }
        } else if (args[0].equalsIgnoreCase("client")) {
            try {
                int port = Integer.parseInt(args[1]);
                new Thread(new Client(port)).start();
            } catch (NumberFormatException e) {
                System.out.println("Usage: (server | client) <port>");
            }
        } else {
            System.out.println("Usage: (server | client) <port>");
        }
    }
}
