package unioeste.sd.connection;

import unioeste.sd.structs.User;

import java.io.IOException;
import java.net.Socket;

public class UdpConnection extends Connection{
    public UdpConnection(Socket socket, User user) throws IOException {
        super(socket, user);
    }

    public UdpConnection(Socket socket) throws IOException {
        super(socket);
    }
}
