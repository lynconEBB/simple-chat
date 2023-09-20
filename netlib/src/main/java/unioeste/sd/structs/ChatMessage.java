package unioeste.sd.structs;

public class ChatMessage extends Message{
    public String text;

    public Boolean isWhisper;

    public ChatMessage(String text) {
        this.isWhisper = false;
        this.text = text;
    }
}
