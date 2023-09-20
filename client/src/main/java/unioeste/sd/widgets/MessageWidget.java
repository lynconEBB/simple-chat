package unioeste.sd.widgets;

import imgui.ImGui;
import imgui.ImVec2;
import unioeste.sd.structs.ChatMessage;
import unioeste.sd.utils.ImGuiUtils;

public class MessageWidget {
    private ChatMessage chatMessage;

    public MessageWidget(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void draw() {
        ImGui.setCursorPosY(ImGui.getCursorPosY() + 15);
        ImGui.text(chatMessage.user.username);
        ImGui.sameLine();
        ImGui.text("18:09:43");
        ImGui.sameLine();
        ImGui.text(chatMessage.isWhisper ? "whispers:": "says:");

        ImGui.setCursorPos(ImGui.getCursorPosX() + 20, ImGui.getCursorPosY() + 5);
        ImGui.pushTextWrapPos(ImGui.getWindowWidth()-10);
        ImGui.text(chatMessage.text);
        ImGui.popTextWrapPos();

        ImGuiUtils.paddedRect(10,5,ImGui.colorConvertFloat4ToU32(1,0,0,1),5);
    }

    public float getWidgetHeight() {
        ImVec2 imVec2 = ImGui.calcTextSize(chatMessage.text, true, ImGui.getWindowWidth() - 10);
        return ImGui.getTextLineHeightWithSpacing() + 15 + 10 + imVec2.y;
    }
}
