import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private int port;
    private String host;
    private static BufferedReader bufferedReader;
    private PrintWriter printwriter;
    private Socket socket;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8005);
        client.start();
        //System.out.println("Please enter message: ");

        while(true){
            try
                    (Scanner s = new Scanner(System. in)) {
                if(s.hasNext()) {
                    String m = s.nextLine();
                    client.writeMessage(m);
                    client.readMessage();
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        }

    private void start() {
        connectSocket();
        setupStreams();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Please enter message (or type 'exit' to quit): ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                writeMessage(message);
                readMessage();
            }
        } finally {
            closeConnection();
        }
    }
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void connectSocket() {
        try {
            System.out.println("Connecting to " + host + ":" + port);
            socket = new Socket(host, port);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            System.err.println("Could not connect to server.");
            e.printStackTrace();
        }
    }
    private void setupStreams() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printwriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error setting up streams.");
            e.printStackTrace();
        }
    }

    private void writeMessage(String message) {
        printwriter.println(message);
    }

    private void readMessage() {
        try {
            String receivedMessage = bufferedReader.readLine();
            System.out.println("Received message: " + receivedMessage);
        } catch (IOException e) {
            System.err.println("Error reading message.");
            e.printStackTrace();
        }
    }
}

