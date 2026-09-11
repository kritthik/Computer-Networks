import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {

    private static final int PORT = 5000;

    // Stores all connected clients
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("Starting chat server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (true) {

                // Wait for a client to connect
                Socket socket = serverSocket.accept();

                System.out.println("New client connected: "
                        + socket.getInetAddress());

                // Create a handler for the client
                ClientHandler clientHandler = new ClientHandler(socket);

                // Add client to the list
                synchronized (clients) {
                    clients.add(clientHandler);
                }

                // Start a separate thread
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Send a message to all clients except the sender
    public static void broadcast(String message, ClientHandler sender) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Remove a client from the list
    public static void removeClient(ClientHandler client) {

        synchronized (clients) {
            clients.remove(client);
        }
    }

    // Client Handler
    static class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;

            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                output = new PrintWriter(
                        socket.getOutputStream(), true);

            } catch (IOException e) {
                System.out.println("Error creating client handler: "
                        + e.getMessage());
            }
        }

        @Override
        public void run() {

            try {

                // Receive username
                username = input.readLine();

                if (username == null || username.trim().isEmpty()) {
                    return;
                }

                System.out.println(username + " joined the chat.");

                // Notify other clients
                broadcast(username + " joined the chat.", this);

                String message;

                // Continuously receive messages
                while ((message = input.readLine()) != null) {

                    if (!message.trim().isEmpty()) {

                        System.out.println(username + ": " + message);

                        // Send message to all other clients
                        broadcast(username + ": " + message, this);
                    }
                }

            } catch (IOException e) {

                System.out.println(username + " disconnected.");

            } finally {

                // Remove client
                removeClient(this);

                // Notify remaining clients
                if (username != null) {
                    broadcast(username + " left the chat.", this);
                }

                // Close socket
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket.");
                }

                System.out.println("Client removed: " + username);
            }
        }

        // Send a message to this client
        public void sendMessage(String message) {

            if (output != null) {
                output.println(message);
            }
        }
    }
}
