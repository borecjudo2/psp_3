package by.bsuir;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        new EchoServer().start();
        new EchoClient().start();
    }
}

class EchoServer extends Thread {

    private final DatagramSocket socket;
    private final byte[] buf = new byte[1080];

    public EchoServer() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received
                = new String(packet.getData(), 0, packet.getLength());

            byte[] answer = checkOnPalindrome(received).getBytes(StandardCharsets.UTF_8);
//            byte[] answer = checkOnPalindrome(received).getBytes();

            DatagramPacket answerPacket = new DatagramPacket(answer, answer.length, address, port);
            try {
                socket.send(answerPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    private String checkOnPalindrome(String word) {
        String palindrome = "Word is palindrome";
        String notPalindrome = "Word isn't palindrome";

        return word.equalsIgnoreCase(new StringBuffer(word).reverse().toString()) ? palindrome : notPalindrome;
    }

}

class EchoClient {
    private final DatagramSocket socket;
    private final InetAddress address;

    public EchoClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public void start() throws IOException {
        while (true) {
            byte[] buf = getWord();
            DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                packet.getData(), 0, packet.getLength());
            System.out.println(received);
        }
    }

    private byte[] getWord() {
        System.out.println("Print word: ");

        Scanner in = new Scanner(System.in);
        return in.nextLine().getBytes();
    }
}
