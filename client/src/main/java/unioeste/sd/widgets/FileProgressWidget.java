package unioeste.sd.widgets;

import imgui.ImGui;
import unioeste.sd.structs.User;

public class FileProgressWidget {
    private User sourceUser;
    private String fileName;
    private long bytesReceived;
    public enum Status {
        SUCCESS,
        FAILED,
        IN_PROGRESS
    }
    private Status status;

    public FileProgressWidget(User sourceUser, String fileName) {
        this.sourceUser = sourceUser;
        this.fileName = fileName;
        this.bytesReceived = 0;
        this.status = Status.IN_PROGRESS;
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
