import java.net.*;
import java.util.Scanner;

public class UDPClient {

    public static void main(String[] args) throws Exception {

        DatagramSocket clientSocket = new DatagramSocket();

        Scanner scanner = new Scanner(System.in);

        // Server address
        InetAddress serverAddress =
                InetAddress.getByName("localhost");

        // Input sentence
        System.out.print("Enter a new-generation English sentence: ");
        String sentence = scanner.nextLine();

        // Convert sentence to bytes
        byte[] sendBuffer = sentence.getBytes();

        // Create packet and send to server
        DatagramPacket sendPacket =
                new DatagramPacket(
                        sendBuffer,
                        sendBuffer.length,
                        serverAddress,
                        12345
                );

        clientSocket.send(sendPacket);

        // Receive translated sentence
        byte[] receiveBuffer = new byte[4096];

        DatagramPacket receivePacket =
                new DatagramPacket(
                        receiveBuffer,
                        receiveBuffer.length
                );

        clientSocket.receive(receivePacket);

        String translatedSentence =
                new String(
                        receivePacket.getData(),
                        0,
                        receivePacket.getLength()
                );

        // Display result
        System.out.println("\nFormal English:");
        System.out.println(translatedSentence);

        clientSocket.close();
        scanner.close();
    }
}
