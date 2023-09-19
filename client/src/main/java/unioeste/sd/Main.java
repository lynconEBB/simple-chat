package unioeste.sd;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import unioeste.sd.structs.User;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private List<String> messages = new ArrayList<>();
    private ImString currentText = new ImString();
    private final LoginWindow loginWindow;
    private ImBoolean showLoginWindow = new ImBoolean(true);

    private Client client = new Client();

    public Main() {
        loginWindow = new LoginWindow();
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Simple App");
        config.setFullScreen(true);
    }

    @Override
    public void process() {
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());

        if (showLoginWindow.get()) {
            if (loginWindow.draw(client)) {

                Thread thread = new Thread(client);
                thread.setDaemon(true);
                thread.start();

                showLoginWindow.set(false);
            }
        }

        if (client.isRunning()) {
            ImGui.begin("Chat messages");

            if (ImGui.beginChild("scrolling", 0, -(ImGui.getFrameHeightWithSpacing()),false, ImGuiWindowFlags.HorizontalScrollbar)) {
                int count = 1;
                ImGui.setCursorPos(0,ImGui.getContentRegionAvailY() - 20);
                ImGui.textWrapped("AAAAAAAA");
                ImGui.setCursorPos(0,ImGui.getContentRegionAvailY() - 20);
                ImGui.textWrapped("AAAAAAAA");
/*
                for (int i = 0; i < 100; i++) {
                    ImGui.getContentRegionAvailY();
                    count++;
                }
*/
            }
            if (ImGui.getScrollY() >= ImGui.getScrollMaxY())
                ImGui.setScrollHereY(1);
            ImGui.endChild();


            ImGui.beginGroup();

            ImGui.inputText("Mensagem", currentText);
            ImGui.sameLine();
            if (ImGui.button("Enviar")) {
                if (currentText.isNotEmpty()) {
                    messages.add(currentText.get());
                    currentText.clear();
                }
            }
            ImGui.endGroup();
            ImGui.end();

            ImGui.begin("users", ImGuiWindowFlags.HorizontalScrollbar);
            if (client.getChatUsers() != null) {
                for (User user : client.getChatUsers()) {
                    ImGui.text("Nome: " + user.name);
                    ImGui.text("Username: " + user.username);
                    ImGui.separator();
                }
            }
            ImGui.end();

            ImGui.begin("Files");
            ImGui.end();
        }
    }

    public static void main(String[] args) {
        launch(new Main());
    }

}