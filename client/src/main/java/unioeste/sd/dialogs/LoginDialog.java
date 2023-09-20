package unioeste.sd.dialogs;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import unioeste.sd.Client;
import unioeste.sd.structs.User;

public class LoginDialog {
    private ImString username = new ImString();
    private ImString name = new ImString();
    private ImString serverIp = new ImString("127.0.0.1", 200);
    private ImInt serverPort = new ImInt(54000);
    private boolean showErrorMsg;

    public boolean draw(Client client) {
        boolean success = false;

        ImGui.begin("Login", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDocking);
        {
            ImGui.inputText("Server IP", serverIp);
            ImGui.inputInt("Server Port", serverPort);
            ImGui.separator();
            ImGui.inputText("Username", username);
            ImGui.inputText("Name", name);

            ImGui.setWindowSize(0,0);
            ImGui.setWindowPos((float)(ImGui.getIO().getDisplaySizeX() * 0.5) - ImGui.getWindowWidth()/2,
                    (float) (ImGui.getIO().getDisplaySizeY() * 0.5) - ImGui.getWindowHeight()/2);

            if (ImGui.button("Login")) {
                success = client.tryInitConnection(serverIp.get(), serverPort.get(), new User(username.get(), name.get()));

                if (!success)
                    showErrorMsg = true;
            }

            if (showErrorMsg) {
                ImGui.sameLine();
                ImGui.textColored(255,0,0,255, "Erro ao tentar conectar!");
            }
        }
        ImGui.end();

        return success;
    }
}
