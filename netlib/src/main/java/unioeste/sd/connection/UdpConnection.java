package unioeste.sd.connection;

import unioeste.sd.structs.Message;
import unioeste.sd.structs.User;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class UdpConnection extends Connection{ private final DatagramSocket socket;
    private final SocketAddress socketAddress;
    private final BlockingQueue<Message> incommingMessages;
    private boolean isConnected;

    public UdpConnection(SocketAddress socketAddress, DatagramSocket socket) throws IOException {
        this.incommingMessages = new LinkedBlockingDeque<>();
        this.socket = socket;
        this.socketAddress = socketAddress;
        this.isConnected = true;
    }

    @Override
    public <T extends Message> T readMessage() throws IOException, ClassNotFoundException {
        while (incommingMessages.isEmpty()) { }
        return (T) incommingMessages.remove();
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(message);
        objectStream.close();

        DatagramPacket packet = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, socketAddress);
        socket.send(packet);
    }

    @Override
    public void close() throws IOException {
        isConnected = false;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void addMessage(Message message) {
        this.incommingMessages.add(message);
    }
}
