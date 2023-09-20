package unioeste.sd;

import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.widgets.FileProgressWidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SendFileTask implements Runnable{
    private static final int MAX_PACKET_SIZE = 1024;
    private final File file;
    private final Client client;
    private final FileProgressWidget widget;

    public static void initTask(File file, Client client, FileProgressWidget widget) {
        Thread thread = new Thread(new SendFileTask(file, client,widget));
        thread.start();
    }

    public SendFileTask(File file, Client client, FileProgressWidget widget) {
        this.widget = widget;
        this.file = file;
        this.client = client;
    }

    @Override
    public void run() {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[MAX_PACKET_SIZE];

            while (true){
                int bytesRead = fileInputStream.read(bytes);

                if (bytesRead == -1) {
                    FilePacketMessage message = new FilePacketMessage(client.getConnection().user, file.getName(), new byte[0]);
                    client.outManager.sendMessage(message);
                    widget.setStatus(FileProgressWidget.Status.SUCCESS);
                    break;
                }

                byte[] bytesToSend = new byte[bytesRead];
                System.arraycopy(bytes,0, bytesToSend, 0,bytesRead);

                widget.incrementBytesReceived(bytesRead);

                FilePacketMessage message = new FilePacketMessage(client.getConnection().user, file.getName(), bytesToSend);
                client.outManager.sendMessage(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
