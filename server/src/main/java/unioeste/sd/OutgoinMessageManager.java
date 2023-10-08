package unioeste.sd;

import unioeste.sd.connection.Connection;
import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class OutgoinMessageManager implements Runnable{
    private final Connection connection;
    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> fileMessages = new LinkedBlockingQueue<>();

    public OutgoinMessageManager(Connection connection) {
        this.connection = connection;
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
                if (!fileMessages.isEmpty()) {
                    Message msg = fileMessages.remove();
                    connection.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Message message) {
        if (message instanceof FilePacketMessage) {
            fileMessages.add(message);
        } else {
            messageQueue.add(message);
        }
    }
}
