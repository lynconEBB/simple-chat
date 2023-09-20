package unioeste.sd.utils;

import imgui.ImGui;

public class ImGuiUtils {
    public static void paddedRect(float pX, float pY, int col, float rounding) {
        ImGui.getWindowDrawList().addRect(ImGui.getItemRectMinX() - pX,ImGui.getItemRectMinY() -pY,ImGui.getItemRectMaxX() + pX, ImGui.getItemRectMaxY() + pY,
                col, rounding);
    }
}
