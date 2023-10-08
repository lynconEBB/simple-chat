package unioeste.sd;

import unioeste.sd.structs.AvailableFileMessage;
import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.structs.RequestFileMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileManager implements Runnable {
    private final Server server;
    private final Map<String, FileOutputStream> fileStreams;
    private final BlockingQueue<FilePacketMessage> messages;
    private final File filesFolder;

    public FileManager(Server server) {
        this.server = server;
        this.fileStreams = new HashMap<>();
        this.messages = new LinkedBlockingQueue<>();
        this.filesFolder = new File("server-files");

        if (!filesFolder.exists() || !filesFolder.isDirectory())
            filesFolder.mkdir();
    }

    public static FileManager start(Server server) {
        FileManager fileManager = new FileManager(server);
        Thread thread = new Thread(fileManager);
        thread.start();

        return fileManager;
    }

    @Override
    public void run() {

        do {
            if (messages.isEmpty())
                continue;

            FilePacketMessage message = messages.remove();
            FileOutputStream fileOutputStream;

            try {
                if (!fileStreams.containsKey(message.fileName)) {
                    fileOutputStream = new FileOutputStream(filesFolder.getPath() + File.separator + message.fileName);
                    fileStreams.put(message.fileName, fileOutputStream);
                } else {
                    fileOutputStream = fileStreams.get(message.fileName);
                }

                if (message.bytes.length == 0) {
                    fileOutputStream.close();
                    fileStreams.remove(message.fileName);
                    AvailableFileMessage newFileMessage = new AvailableFileMessage(message.user, message.fileName);
                    server.sendToAll(message.user, newFileMessage);
                } else {
                    fileOutputStream.write(message.bytes);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (true);
    }

    public void addMessage(FilePacketMessage message) {
        messages.add(message);
    }

    public void startFileSend(RequestFileMessage message) {
        SendFileTask sendFileTask = new SendFileTask(message.filename, message.user, this);
        new Thread(sendFileTask).start();
    }

    public Server getServer() {
        return server;
    }

    public File getFilesFolder() {
        return filesFolder;
    }
}
