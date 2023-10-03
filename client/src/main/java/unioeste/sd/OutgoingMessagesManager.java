package unioeste.sd;

import unioeste.sd.structs.Message;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.*;

public class OutgoingMessagesManager implements Runnable{
    private Client client;
    private Main mainWindow;
    private Queue<Message> messageQueue;
    private Queue<Message> fileMessages;

    public OutgoingMessagesManager(Client client, Main mainWindow) {
        this.client = client;
        this.mainWindow = mainWindow;
        this.fileMessages = new LinkedBlockingQueue<>();
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        try {
            while (client.isRunning()) {
                if (!fileMessages.isEmpty()) {

                }
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
