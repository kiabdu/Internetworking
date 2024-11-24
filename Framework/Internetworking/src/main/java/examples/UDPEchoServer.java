package examples;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoServer {
    public final static int ECHOSERVERPORT = 9876;

    public static void main(String[] args) throws Exception {
        // Open socket on UDP port 9876
        try (DatagramSocket serverSocket = new DatagramSocket(ECHOSERVERPORT)) {
            String sentence = "";

            // The server should run indefinitely
            while (!sentence.equals("eof")) {
                // Socket expects byte arrays for sending and receiving data
                byte[] receiveData = new byte[1024];
                byte[] sendData;

                // object that will contain the incoming message
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                // server process blocks on receive method until a message comes in
                serverSocket.receive(receivePacket);

                // convert byte array to String object for easier manipulation
                sentence = new String(receivePacket.getData()).trim();
                System.out.println("RECEIVED: " + sentence);

                // Get sender's meta data from the object (IP address and UDP port number)
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                // Manipulate message and convert back to byte array
                String capitalizedSentence = sentence.toUpperCase();
                sendData = capitalizedSentence.getBytes();

                // create object for sending modified message back to sender
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }
        }
    }
}
