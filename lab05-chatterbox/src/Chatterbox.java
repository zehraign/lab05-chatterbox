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
public class Chatterbox {


    public static void main(String[] args) throws IOException {
        int port = 8005;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        Chatterbox server = new Chatterbox();
        server.listen(port);
    }

    public void listen(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Server listening on port " + port);
                //Scanner scanner = new Scanner(System.in);
                /*if(scanner.hasNextLine()) {
                    System.out.println("Message received: " + scanner.nextLine());
                }*/
                try (Socket socket = serverSocket.accept();
                     //InputStream input = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(socket.getInputStream()));
                     OutputStream output = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(
                              socket.getOutputStream(), true)

                ) {
                    System.out.println("-------------------- request from "
                            + socket.getRemoteSocketAddress());
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("Message received: " + message);
                        writer.println(message.toUpperCase());
                    }
                } catch (IOException e) {
                    System.out.println("Error handling client connection: " + e.getMessage());
                }
                }

            }
        }
    }
