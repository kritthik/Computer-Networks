import java.io.*;
import java.net.*;

public class ChatClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try {

            // Connect to server
            Socket socket = new Socket(SERVER_ADDRESS, PORT);

            System.out.println("Connected to chat server.");

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);

            BufferedReader keyboard = new BufferedReader(
                    new InputStreamReader(System.in));

            // Enter username
            System.out.print("Enter your name: ");
            String username = keyboard.readLine();

            // Send username to server
            output.println(username);

            // Thread for receiving messages
            Thread receiveThread = new Thread(() -> {

                try {

                    String message;

                    while ((message = input.readLine()) != null) {
                        System.out.println(message);
                    }

                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            receiveThread.start();

            // Send messages to server
            String message;

            while ((message = keyboard.readLine()) != null) {

                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }

                output.println(message);
            }

            socket.close();

        } catch (IOException e) {

            System.out.println("Could not connect to server.");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
