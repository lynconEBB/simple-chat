package unioeste.sd;

public class App {
    public void init() {
        Thread serverThread = new Thread(new Server());
        serverThread.start();
    }
}
