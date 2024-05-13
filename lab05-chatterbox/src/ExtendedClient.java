

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ExtendedClient {
    private int port;
    private String host;
    private BufferedReader bufferedReader;
    private PrintWriter printwriter;
    private Socket socket;

    public ExtendedClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        ExtendedClient client = new ExtendedClient("localhost", 8005);
        client.start();
    }

    private void start() {
        connectSocket();
        setupStreams();

        // Start a thread to continuously read messages from the server
        new Thread(this::readAdminMessages).start();

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
        } catch (Exception e) {
            System.err.println("Error while sending/receiving messages: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void connectSocket() {
        try {
            System.out.println("Connecting to " + host + ":" + port);
            socket = new Socket(host, port);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupStreams() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printwriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error setting up streams: " + e.getMessage());
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
            System.err.println("Error reading message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readAdminMessages() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String adminMessage = reader.readLine();
                if (adminMessage != null) {
                    System.out.println("[Admin] " + adminMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading admin messages: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}