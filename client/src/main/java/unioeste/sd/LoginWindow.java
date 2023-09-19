package unioeste.sd;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import unioeste.sd.structs.User;

public class LoginWindow {
    private ImString username = new ImString();
    private ImString name = new ImString();
    private ImString serverIp = new ImString("127.0.0.1");
    private ImInt serverPort = new ImInt(54000);
    private boolean showErrorMsg;

    public boolean draw(Client client) {
        ImGui.begin("Login", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
        ImGui.inputText("Server IP", serverIp);
        ImGui.inputInt("Server Port", serverPort);
        ImGui.separator();
        ImGui.inputText("Username", username);
        ImGui.inputText("Name", name);

        ImGui.setWindowSize(0,0);
        ImGui.setWindowPos((float)(ImGui.getIO().getDisplaySizeX() * 0.5) - ImGui.getWindowWidth()/2,
                (float) (ImGui.getIO().getDisplaySizeY() * 0.5) - ImGui.getWindowHeight()/2);

        boolean success = false;

        if (ImGui.button("Login")) {
            success = client.tryInitConnection(serverIp.get(), serverPort.get(), new User(username.get(), name.get()));

            System.out.println(success);

            if (!success)
                showErrorMsg = true;
        }
        if (showErrorMsg) {
            ImGui.sameLine();
            ImGui.textColored(255,0,0,255, "Erro ao tentar conectar!");
        }

        ImGui.end();
        return success;
    }
}
