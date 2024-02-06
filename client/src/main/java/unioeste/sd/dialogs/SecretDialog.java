package unioeste.sd.dialogs;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import unioeste.sd.Client;
import unioeste.sd.structs.User;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretDialog {
    private boolean showError = false;
    private final SecretKeyFactory keyFactory;
    public Map<User, ImString> secrets = new ConcurrentHashMap();
    private User currentUser;
    private String lastValidKey;

    public SecretDialog() {
        try {
            keyFactory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (!secrets.containsKey(currentUser)) {
            secrets.put(currentUser, new ImString(20));
        }
        lastValidKey = secrets.get(currentUser).get();
    }
    public SecretKey getSecretKeyByUser(User user) {
        System.out.println(secrets.get(user));
        if (secrets.containsKey(user) && !secrets.get(user).get().isBlank()) {
            try {
                return keyFactory.generateSecret(new DESKeySpec(secrets.get(user).get().getBytes()));
            } catch (InvalidKeySpecException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public void draw(ImBoolean showWindow, Client client) {
        boolean bSaved = false;
        if (ImGui.begin("Secret Configuration", showWindow, ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDocking)) {

            ImGui.inputText("Secret", secrets.get(currentUser));
            if (showError) {
                ImGui.textColored(255,0,0,255,"Secret key needs at least 8 characters");
            }
            ImGui.setCursorPosX((ImGui.getWindowWidth() / 2) - (ImGui.calcTextSize("Save").x / 2));
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
            if (ImGui.button("Save")) {

                if (secrets.get(currentUser).get().length() < 8) {
                    showError = true;
                }
                else {
                    showError = false;
                    bSaved = true;
                    showWindow.set(false);
                }
            }

            ImGui.setWindowSize(0,0);
            ImGui.setWindowPos((float)(ImGui.getIO().getDisplaySizeX() * 0.5) - ImGui.getWindowWidth()/2,
                    (float) (ImGui.getIO().getDisplaySizeY() * 0.5) - ImGui.getWindowHeight()/2);

            ImGui.end();
        }
        if(showWindow.get() == false && !bSaved) {
            secrets.get(currentUser).set(lastValidKey);
        }
    }
}
