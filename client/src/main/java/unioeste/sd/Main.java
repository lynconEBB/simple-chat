package unioeste.sd;

import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private ImBoolean test = new ImBoolean(true);
    private List<String> list = new ArrayList<>();
    private ImString text = new ImString();
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
            if (!ImGui.begin("Chat messages")) {
                ImGui.end();
                return;
            }

            ImGui.inputText("Mensagem", text);
            ImGui.sameLine();
            if (ImGui.button("Enviar")) {
                if (text.isNotEmpty()) {
                    list.add(text.get());
                    text.clear();
                }
            }

            ImGui.separator();

            if (ImGui.beginChild("scrolling2", 0,0,false, ImGuiWindowFlags.HorizontalScrollbar)) {
                ImVec2 fdsf = ImGui.calcTextSize("fdsf");
                ImGui.dummy(0,ImGui.getWindowHeight() - (fdsf.y + 10));
                for (String a : list) {
                    ImGui.text(a);
                }
            }
            if (ImGui.getScrollY() >= ImGui.getScrollMaxY())
                ImGui.setScrollHereY(1);

            ImGui.endChild();
            ImGui.end();

            ImGui.setWindowSize(1000,0);
            ImGui.begin("users");
            ImGui.end();
            ImGui.setWindowSize(300,0);
            ImGui.begin("Files");
            ImGui.end();
        }
    }

    public static void main(String[] args) {
        launch(new Main());
    }

}