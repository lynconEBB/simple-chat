package dev.lyncon;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Color;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;

public class Main extends Application {

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }

    @Override
    protected void configure(Configuration config) {
        config.setFullScreen(true);
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
        ImGui.begin("AA");
        ImGui.textColored(0,0,1,1,"AAAAA");
        ImGui.end();
    }

    public static void main(String[] args) {
        launch(new Main());
    }
}
