package unioeste.sd.structs;

import java.time.LocalDateTime;

public class ChatMessage extends Message{
    public String text;
    public Boolean isWhisper;
    public LocalDateTime time;

    public ChatMessage(String text) {
        this.time = LocalDateTime.now();
        this.isWhisper = false;
        this.text = text;
    }
}
