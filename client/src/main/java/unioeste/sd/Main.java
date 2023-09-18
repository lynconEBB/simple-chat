package unioeste.sd;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;

public class Main extends Application {
    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
        ImGui.text("Hello, World!");
    }

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    protected void initImGui(Configuration config) {
        super.initImGui(config);
        ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
    }
}

/*
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",54000);
        User clientUser = new User("test1");
        clientUser.name = "OSORIO";

        Connection connection = new Connection(socket);
        connection.user = clientUser;

        connection.sendMessage(new ClientInfoMessage(clientUser));
        ClientsListMessage listMsg = connection.readMessage();

        while (true) {
            Message msg = connection.readMessage();
            if (msg instanceof ChatMessage) {
                ChatMessage chatmessage = (ChatMessage) msg;
                System.out.println("[" + chatmessage.user.username + "]: " + chatmessage.text);
            }
        }
    }
}*/
