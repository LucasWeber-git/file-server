import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) {
        Server server = new Server();
        server.waitForConnection();
    }

    public void waitForConnection() {
        try (ServerSocket serverSocket = new ServerSocket(8084)) {
            while (true) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());

                waitForMessages();
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForMessages() {
        String msg = "";
        try {
            while (true) {
                msg = in.readLine();
                processMessage(msg);
            }
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processMessage(String msg) {
        if (msg == null || msg.isEmpty()) {
            System.out.println("Received empty message, ignoring.");
            return;
        }

        String command = msg.split(" ")[0].toLowerCase();

        switch (command) {
            case "list":
                list();
                break;
            case "down":
                down();
                break;
            case "up":
                up();
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    public void list() { //TODO: implementar
        System.out.println("Listing files...");
    }

    public void down() { //TODO: implementar
        System.out.println("Downloading file...");
    }

    public void up() { //TODO: implementar
        System.out.println("Uploading file...");
    }

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

}
