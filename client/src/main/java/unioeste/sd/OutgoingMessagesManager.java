package unioeste.sd;

import unioeste.sd.structs.Connection;
import unioeste.sd.structs.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutgoingMessagesManager implements Runnable{
    private Client client;
    private Main mainWindow;
    private Queue<Message> messageQueue;

    public OutgoingMessagesManager(Client client, Main mainWindow) {
        this.client = client;
        this.mainWindow = mainWindow;
        this.messageQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                if (!messageQueue.isEmpty()) {
                    Message msg = messageQueue.remove();
                    client.getConnection().sendMessage(msg);
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
