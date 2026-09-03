import java.net.*;
import java.util.*;

public class UDPServer {

    public static void main(String[] args) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(12345);

        System.out.println("Server is running...");
        System.out.println("Waiting for client message...");

        // Abbreviation dictionary
        Map<String, String> abbreviations = new HashMap<>();

        abbreviations.put("tbh", "to be honest");
        abbreviations.put("ig", "I guess");
        abbreviations.put("tbf", "to be fair");
        abbreviations.put("atm", "at the moment");
        abbreviations.put("irl", "in real life");
        abbreviations.put("lol", "laughing out loud");
        abbreviations.put("asap", "as soon as possible");
        abbreviations.put("omg", "oh my god");
        abbreviations.put("ttyl", "talk to you later");
        abbreviations.put("idk", "I don't know");
        abbreviations.put("nvm", "never mind");
        abbreviations.put("idc", "I don't care");

        while (true) {

            // Receive data
            byte[] receiveBuffer = new byte[4096];

            DatagramPacket receivePacket =
                    new DatagramPacket(receiveBuffer, receiveBuffer.length);

            serverSocket.receive(receivePacket);

            String sentence = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength()
            );

            System.out.println("Received: " + sentence);

            // Translate sentence
            String[] words = sentence.split(" ");
            StringBuilder translated = new StringBuilder();

            for (String word : words) {

                String punctuation = "";

                // Separate punctuation from the word
                while (!word.isEmpty() &&
                       ".,!?;:".indexOf(word.charAt(word.length() - 1)) != -1) {

                    punctuation =
                            word.charAt(word.length() - 1) + punctuation;

                    word = word.substring(0, word.length() - 1);
                }

                String lowerWord = word.toLowerCase();

                if (abbreviations.containsKey(lowerWord)) {

                    String replacement = abbreviations.get(lowerWord);

                    // Preserve capitalization
                    if (!word.isEmpty() &&
                        Character.isUpperCase(word.charAt(0))) {

                        replacement =
                                Character.toUpperCase(replacement.charAt(0))
                                + replacement.substring(1);
                    }

                    translated.append(replacement);
                    translated.append(punctuation);

                } else {
                    translated.append(word);
                    translated.append(punctuation);
                }

                translated.append(" ");
            }

            String result = translated.toString().trim();

            System.out.println("Translated: " + result);

            // Send translated sentence back to client
            byte[] sendBuffer = result.getBytes();

            DatagramPacket sendPacket =
                    new DatagramPacket(
                            sendBuffer,
                            sendBuffer.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );

            serverSocket.send(sendPacket);

            System.out.println("Translation sent to client.\n");
        }
    }
}
