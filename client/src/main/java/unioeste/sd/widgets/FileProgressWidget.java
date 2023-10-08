package unioeste.sd.widgets;

import imgui.ImGui;
import unioeste.sd.Client;
import unioeste.sd.structs.RequestFileMessage;
import unioeste.sd.structs.User;

import java.io.IOException;

public class FileProgressWidget {
    public enum Status {
        IDLE,
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
    private final Client client;
    private final User sourceUser;
    private final String fileName;
    private long bytesReceived;
    private Status status;

    public FileProgressWidget(User sourceUser, String fileName, Client client) {
        this.sourceUser = sourceUser;
        this.client = client;
        this.fileName = fileName;
        this.bytesReceived = 0;
        this.status = Status.IDLE;
    }

    public void draw(boolean isUpload) {
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() + 10);
        ImGui.text("User: " + sourceUser.username);
        ImGui.text("File name: " + fileName);
        switch (status) {
            case SUCCESS -> {
                ImGui.textColored(0,255,0,255, (isUpload ? "Upload" : "Download") + " success!");
            }
            case FAILED -> {
                ImGui.textColored(255,0,0,255,(isUpload ? "Upload" : "Download") + " failed!");
            }
            case IN_PROGRESS -> {
                ImGui.text("Size:");
                ImGui.sameLine();
                ImGui.text(getBytesFormated());
            }
        }
        if (!isUpload && status == Status.IDLE) {
           if (ImGui.button("Download")) {
                status = Status.IN_PROGRESS;
               RequestFileMessage requestMessage = new RequestFileMessage(client.getConnection().user, fileName);
               client.outManager.sendMessage(requestMessage);
           }
        }
        ImGui.separator();
    }

    private String getBytesFormated() {
        if (bytesReceived < 1024 * 1024) {
            double kb = bytesReceived / 1024.0;
            return String.format("%.2f KB", kb);
        } else if (bytesReceived < 1024 * 1024 * 1024) {
            double mb = bytesReceived / (1024.0 * 1024.0);
            return String.format("%.2f MB", mb);
        } else {
            double gb = bytesReceived / (1024.0 * 1024.0 * 1024.0);
            return String.format("%.2f GB", gb);
        }
    }
    public void incrementBytesReceived(long newBytes) {
        bytesReceived += newBytes;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
