import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This example was taken from the Chapter "Netzwerkprogrammierung" of the Book
 * "Programmieren mit Java II", Pearson. http://sol.cs.hm.edu/4129/
 * http://sol.cs.hm.edu/4129/html/384-minimalerwebserver.xhtml
 *
 * And then radically refactored.
 *
 * @author Reinhard Schiedermeier
 * @author Barne Kleinen
 *
 *         Starting to actually hand out files...
 */
public class ExtendedChatterbox {
    private ServerSocket serverSocket;
    private List<PrintWriter> clientWriters;
    private boolean running;

    public static void main(String[] args) {
        int port = 8005;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        ExtendedChatterbox server = new ExtendedChatterbox();
        server.listen(port);
    }

    public void listen(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            running = true;
            clientWriters = new ArrayList<>();

            new Thread(() -> {
                try (Scanner scanner = new Scanner(System.in)) {
                    while (running) {
                        String adminMessage = scanner.nextLine();
                        broadcastAdminMessage(adminMessage);
                    }
                }
            }).start();

            while (running) {
                Socket socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        } finally {
            closeServer();
        }
    }

    private void handleClient(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Add client writer to the list
            clientWriters.add(writer);

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                // Handle client message
                System.out.println("Message from client: " + clientMessage);
            }

            // Remove client writer from the list
            clientWriters.remove(writer);
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }

    private void broadcastAdminMessage(String message) {
        System.out.println("[Admin] " + message);
        for (PrintWriter writer : clientWriters) {
            writer.println("[Admin] " + message);
        }
    }

    private void closeServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server closed.");
            }
        } catch (IOException e) {
            System.err.println("Error while closing server: " + e.getMessage());
        }
    }
}

