package unioeste.sd.dialogs;

import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import unioeste.sd.Client;
import unioeste.sd.SendFileTask;
import unioeste.sd.structs.AvailableFileMessage;
import unioeste.sd.structs.FilePacketMessage;
import unioeste.sd.widgets.FileProgressWidget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilesDialog {
    String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
    private static ImGuiFileDialogPaneFun callback = new ImGuiFileDialogPaneFun() {
        @Override
        public void paneFun(String filter, long userDatas, boolean canContinue) { }
    };

    private String currentPath;
    private final List<FileProgressWidget> uploadedWidgets;
    private final Map<String, FileProgressWidget> downloadedWidgets;
    private final Map<String, FileOutputStream> downloadedFiles;
    private final Client client;

    public FilesDialog(Client client) {
        this.client = client;
        this.currentPath = "...";
        uploadedWidgets = new ArrayList<>();
        downloadedWidgets = new ConcurrentHashMap<>();
        downloadedFiles = new ConcurrentHashMap<>();
    }

    public void draw() {
        ImGui.begin("Files");
        {
            if (ImGui.button("Select file")) {
                ImGuiFileDialog.openModal("browse-key", "Choose File", ".*", ".", callback, 250, 1, 42, ImGuiFileDialogFlags.None);
            }
            ImGui.sameLine();
            ImGui.text(currentPath);

            if (ImGui.button("Send file")) {
                File file = new File(currentPath);
                if (file.exists() && !file.isDirectory()) {
                    FileProgressWidget fileWidget = new FileProgressWidget(client.getConnection().user, file.getName(),client);
                    uploadedWidgets.add(fileWidget);
                    SendFileTask.initTask(file, client, fileWidget);
                }
                currentPath = "...";
            }

            ImGui.separator();

            ImGui.beginChild("upload",ImGui.getWindowWidth() * 0.5f,0);
                ImGui.text("Uploads:");
                for (FileProgressWidget widget : uploadedWidgets) {
                    widget.draw(true);
                }
            ImGui.endChild();
            ImGui.sameLine();
            ImGui.beginChild("download",ImGui.getWindowWidth() * 0.5f,0);
                ImGui.text("Downloads:");
                for (FileProgressWidget widget: downloadedWidgets.values()) {
                    widget.draw(false);
                }
            ImGui.endChild();

            if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk() && !ImGuiFileDialog.getSelection().isEmpty()) {
                    currentPath = ImGuiFileDialog.getSelection().values().stream().findFirst().get();
                }
                ImGuiFileDialog.close();
            }
        }
        ImGui.end();
    }

    public void addAvailableFile(AvailableFileMessage message) {
        FileProgressWidget newWidget = new FileProgressWidget(message.user, message.filename,client);
        downloadedWidgets.put(message.filename, newWidget);
    }

    public void processPacket(FilePacketMessage message) throws IOException {
        FileProgressWidget widget = downloadedWidgets.get(message.fileName);
        FileOutputStream fileOutputStream;

        if (!downloadedFiles.containsKey(message.fileName)) {
             fileOutputStream = new FileOutputStream(desktopPath + message.fileName);
             downloadedFiles.put(message.fileName, fileOutputStream);
        } else {
            fileOutputStream = downloadedFiles.get(message.fileName);
        }

        if (message.bytes.length == 0) {
            widget.setStatus(FileProgressWidget.Status.SUCCESS);
            fileOutputStream.close();
            downloadedFiles.remove(message.fileName);
        } else {
            widget.incrementBytesReceived(message.bytes.length);
            fileOutputStream.write(message.bytes);
        }
    }
}
