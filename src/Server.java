import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Socket socket;
    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        // code for server
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242); // Port for connections 4242
        // creates new thread for every connection
        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            new Thread(server).start();
        }
    }
}
