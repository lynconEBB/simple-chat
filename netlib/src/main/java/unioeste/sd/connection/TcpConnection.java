package unioeste.sd.connection;

import unioeste.sd.structs.User;

import java.io.IOException;
import java.net.Socket;

public class TcpConnection extends Connection{
    public TcpConnection(Socket socket, User user) throws IOException {
        super(socket, user);
    }

    public TcpConnection(Socket socket) throws IOException {
        super(socket);
    }
}
