package unioeste.sd.structs;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

public class ChatMessage extends Message{
    public String text;
    public LocalDateTime time;
    public boolean isWhisper;

    public ChatMessage(String text, User user) {
        this.time = LocalDateTime.now();
        this.text = text;
        this.user = user;
        this.isWhisper = false;
    }

    public ChatMessage(String text) {
        this.time = LocalDateTime.now();
        this.text = text;
        this.isWhisper = false;
    }
}
