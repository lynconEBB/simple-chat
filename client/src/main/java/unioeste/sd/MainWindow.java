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
import unioeste.sd.dialogs.SecretDialog;
import unioeste.sd.structs.*;
import unioeste.sd.widgets.MessageWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends Application {

    private ImString currentText = new ImString();
    private ImBoolean showLoginWindow = new ImBoolean(true);
    private ImBoolean showSecretWindow = new ImBoolean(false);
    private final LoginDialog loginDialog;
    private final FilesDialog filesDialog;
    public final SecretDialog secretDialog;
    private final List<MessageWidget> messageWidgets;
    private List<User> usersOnline;
    private Client client;

    public MainWindow() {
        usersOnline = new ArrayList<>();
        messageWidgets = new ArrayList<>();
        client = new Client(this);
        loginDialog = new LoginDialog();
        filesDialog = new FilesDialog(client);
        secretDialog = new SecretDialog();
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
                if (currentText.isNotEmpty() && !currentText.get().isBlank() ) {

                    ChatMessage chatMessage = client.createMessageFromString(currentText.get());
                    if (chatMessage != null) {
                        client.outManager.sendMessage(chatMessage);

                        messageWidgets.add(new MessageWidget(new ChatMessage(currentText.get())));
                        currentText.clear();
                    }
                }
            }
            ImGui.end();

            filesDialog.draw();

            ImGui.begin("Online Users", ImGuiWindowFlags.HorizontalScrollbar);
            {
                for (User user : usersOnline) {
                    float topY = ImGui.getCursorPosY();
                    ImGui.text("Username: " + user.username + (user.equals(client.getConnection().user) ? " (you)" : "") );
                    ImGui.text("Name: " + user.name);
                    ImGui.separator();

                    ImVec2 cursorPos = ImGui.getCursorPos();
                    ImGui.getWindowWidth();
                    ImGui.setCursorPos(ImGui.getWindowWidth() - (ImGui.calcTextSize("secret").x + 20), topY);
                    if (ImGui.button("secret##"+user.username)){
                        secretDialog.setCurrentUser(user);
                        showSecretWindow.set(true);
                    }
                    ImGui.setCursorPos(cursorPos.x, cursorPos.y);
                }
            }
            ImGui.end();

            if (showSecretWindow.get()) {
                secretDialog.draw(showSecretWindow, client);
            }
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
        launch(new MainWindow());
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