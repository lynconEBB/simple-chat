package unioeste.sd;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import unioeste.sd.dialogs.FilesDialog;
import unioeste.sd.dialogs.LoginDialog;
import unioeste.sd.structs.*;
import unioeste.sd.widgets.MessageWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private ImString currentText = new ImString();
    private ImBoolean showLoginWindow = new ImBoolean(true);
    private final LoginDialog loginDialog;
    private final FilesDialog filesDialog;
    private final List<MessageWidget> messageWidgets;
    private List<User> usersOnline;
    private Client client;

    public Main() {
        client = new Client(this);
        loginDialog = new LoginDialog();
        usersOnline = new ArrayList<>();
        messageWidgets = new ArrayList<>();
        filesDialog = new FilesDialog(client);
    }

    @Override
    public void process() {
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());

        if (showLoginWindow.get()) {
            if (loginDialog.draw(client)) {
                showLoginWindow.set(false);
            }
        }

        if (client.isRunning()) {
            ImGui.begin("Chat messages", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar);

            if (ImGui.beginChild("scrolling", 0, -(ImGui.getFrameHeightWithSpacing()),false, ImGuiWindowFlags.HorizontalScrollbar)) {

                float accum = 10;
                for (MessageWidget widget: messageWidgets) {
                    accum += widget.getWidgetHeight();
                }
                if (accum < ImGui.getContentRegionAvailY()) {
                    ImGui.dummy(0, ImGui.getContentRegionAvailY() - accum);
                }

                for (MessageWidget widget : messageWidgets) {
                    widget.draw();
                }
            }

            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
            ImGui.dummy(0,1);

            if (ImGui.getScrollY() >= ImGui.getScrollMaxY())
                ImGui.setScrollHereY(1f);

            ImGui.endChild();

            ImGui.inputText("Message", currentText);
            ImGui.sameLine();
            if (ImGui.button("Send")) {
                if (currentText.isNotEmpty()) {
                    ChatMessage chatMessage = new ChatMessage(currentText.get());
                    chatMessage.user = client.getConnection().user;
                    client.outManager.sendMessage(chatMessage);

                    messageWidgets.add(new MessageWidget(chatMessage));
                    currentText.clear();
                }
            }
            ImGui.end();

            filesDialog.draw();

            ImGui.begin("Online Users", ImGuiWindowFlags.HorizontalScrollbar);
            {
                for (User user : usersOnline) {
                    ImGui.text("Username: " + user.username + (user.equals(client.getConnection().user) ? " (you)" : "") );
                    ImGui.text("Name: " + user.name);
                    ImGui.separator();
                }
            }
            ImGui.end();
        }

    }

    public void handleNewChatMessage(ChatMessage message) {
        this.messageWidgets.add(new MessageWidget(message));
    }
    public void handleNewClientsListMessage(ClientsListMessage message) {
        usersOnline = message.users;
    }
    public void handleFilePacketMessage(FilePacketMessage message) {
        try {
            filesDialog.processPacket(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void handleAvailableFileMessage(AvailableFileMessage msg) {
       filesDialog.addAvailableFile(msg);
    }

    public static void main(String[] args) {
        launch(new Main());
    }
    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Simple Chat");
        config.setFullScreen(true);
    }
}