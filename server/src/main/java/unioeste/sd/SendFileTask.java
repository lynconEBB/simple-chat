package unioeste.sd;

import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SendFileTask implements Runnable {
    private static final int MAX_PACKET_SIZE = 1024;
    private final String filename;
    private final FileManager fileManager;
    private final OutgoingMessageManager outManager;

    public SendFileTask(String filename, User sourceUser, FileManager fileManager) {
        this.filename = filename;
        this.fileManager = fileManager;
        this.outManager = fileManager.getServer().getOutManagers().get(sourceUser);
    }

    @Override
    public void run() {
        try (FileInputStream fileInputStream = new FileInputStream(fileManager.getFilesFolder().getPath() + File.separator + filename)) {
            byte[] bytes = new byte[MAX_PACKET_SIZE];

            while (true){
                int bytesRead = fileInputStream.read(bytes);

                if (bytesRead == -1) {
                    FilePacketMessage message = new FilePacketMessage(fileManager.getServer().getServerUser(), filename, new byte[0]);
                    outManager.sendMessage(message);
                    break;
                }

                byte[] bytesToSend = new byte[bytesRead];
                System.arraycopy(bytes,0, bytesToSend, 0,bytesRead);


                FilePacketMessage message = new FilePacketMessage(fileManager.getServer().getServerUser(), filename, bytesToSend);
                outManager.sendMessage(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
