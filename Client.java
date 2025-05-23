import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {

    private static final String HOST = "localhost";
    private static final int    PORT = 1234;

    public static void main(String[] args) {
        System.out.println("Connecting to " + HOST + ":" + PORT + " …");

        try (Socket socket = new Socket(HOST, PORT)) {

            BufferedReader  in  = new BufferedReader(
                                   new InputStreamReader(socket.getInputStream()));
            PrintWriter     out = new PrintWriter(
                                   socket.getOutputStream(), true);

 
            Thread receiver = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("[SERVER] " + msg);
                    }
                } catch (IOException ex) {
                    System.out.println("Receiver stopped: " + ex.getMessage());
                }
            });
            receiver.setDaemon(true);
            receiver.start();

           
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                out.println(line);
                if (line.trim().equalsIgnoreCase("BYE")) break;
            }

        } catch (IOException ex) {
            System.err.println("Client error: " + ex.getMessage());
        }
    }
}
