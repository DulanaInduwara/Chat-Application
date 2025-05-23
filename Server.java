import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;



public class Server {

   
    private static final Instant startInstant = Instant.now();

   
    private static final int PORT = 1234;

    public static void main(String[] args) {
        System.out.println("Server starting on port " + PORT + " …");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client: " + client);

              
                new Thread(new ClientHandler(client)).start();
            }

        } catch (IOException ex) {
            System.err.println("Server error: " + ex.getMessage());
        }
    }

  
    private static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) { this.socket = socket; }

        @Override public void run() {
            try (
                BufferedReader  in  = new BufferedReader(
                                         new InputStreamReader(socket.getInputStream()));
                PrintWriter     out = new PrintWriter(
                                         socket.getOutputStream(), true)
            ) {
                out.println("Hello! Type TIME, DATE, UPTIME or BYE");

                String line;
                while ((line = in.readLine()) != null) {
                    String cmd = line.trim().toUpperCase();

                    switch (cmd) {
                        case "TIME"   -> out.println(LocalTime.now()
                                                     .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                        case "DATE"   -> out.println(LocalDate.now());
                        case "UPTIME" -> out.println(Duration.between(startInstant, Instant.now())
                                                         .toSeconds() + " seconds");
                        case "BYE"    -> {
                                            out.println("Goodbye!");
                                            return;               
                                        }
                        default       -> out.println("Unknown command");
                    }
                }
            } catch (IOException ex) {
                System.out.println("Client lost: " + ex.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}
