package unioeste.sd;

import unioeste.sd.connection.Connection;
import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class OutgoinMessageManager implements Runnable{
    private final Server server;
    private final Connection connection;
    private Queue<Message> messageQueue;

    public OutgoinMessageManager(Server server, Connection connection) {
        this.server = server;
        this.connection = connection;
        this.messageQueue = new PriorityBlockingQueue<>(50, (Message m1, Message m2) -> {
            if (m1 instanceof FilePacketMessage && m2 instanceof FilePacketMessage) {
                return 0;
            } else if (m1 instanceof FilePacketMessage) {
                return -1;
            } else if (m2 instanceof FilePacketMessage){
                return 1;
            } else {
                return 0;
            }
        });

    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            while (connection.isConnected()) {
                if (!messageQueue.isEmpty()) {
                    Message msg = messageQueue.remove();
                    connection.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Message message) {
        messageQueue.add(message);
    }
}
