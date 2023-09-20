package unioeste.sd;

import unioeste.sd.structs.ChatMessage;
import unioeste.sd.structs.Connection;
import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.Message;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class OutgoingMessagesManager implements Runnable{
    private Client client;
    private Main mainWindow;
    private Queue<Message> messageQueue;

    public OutgoingMessagesManager(Client client, Main mainWindow) {
        this.client = client;
        this.mainWindow = mainWindow;
        this.messageQueue = new PriorityBlockingQueue<>(50, (m1, m2) -> {
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

    @Override
    public void run() {
        try {
            System.out.println(client.isRunning());
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
