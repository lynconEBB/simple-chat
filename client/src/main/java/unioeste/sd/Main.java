package unioeste.sd;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.nfd.NativeFileDialog;
import unioeste.sd.dialogs.LoginDialog;
import unioeste.sd.structs.ChatMessage;
import unioeste.sd.structs.ClientsListMessage;
import unioeste.sd.structs.User;
import unioeste.sd.widgets.MessageWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private ImString currentText = new ImString();
    private final LoginDialog loginDialog;
    private ImBoolean showLoginWindow = new ImBoolean(true);

    private final List<MessageWidget> messageWidgets;
    private List<User> usersOnline;

    private Client client;

    public Main() {
        client = new Client(this);
        loginDialog = new LoginDialog();
        usersOnline = new ArrayList<>();
        messageWidgets = new ArrayList<>();
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

    @Override
    public void process() {
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());

        if (showLoginWindow.get()) {
            if (loginDialog.draw(client)) {

                Thread clientThread = new Thread(client);
                clientThread.setDaemon(true);
                clientThread.start();

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

                    if (chatMessage.text.charAt(0) != '/')
                        messageWidgets.add(new MessageWidget(chatMessage));

                    currentText.clear();
                }
            }
            ImGui.end();

            ImGui.begin("users", ImGuiWindowFlags.HorizontalScrollbar);
            {
                for (User user : usersOnline) {
                    ImGui.text("Name: " + user.name);
                    ImGui.text("Username: " + user.username);
                    ImGui.separator();
                }
            }
            ImGui.end();

            ImGui.begin("Files");
            {
                if (ImGui.button("open file")) {
                    PointerBuffer a = PointerBuffer.allocateDirect(10);
                    NativeFileDialog.NFD_OpenDialog(new StringBuffer(), new StringBuffer(),a);
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

    public static void main(String[] args) {
        launch(new Main());
    }

}