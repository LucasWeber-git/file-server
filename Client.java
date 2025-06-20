import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client implements Runnable {

    private static final Path PATH = Paths.get("C:/Projects/file-server/files/client");

    Thread thread;
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.setup();
        client.waitForUserInput();
    }

    private void setup() {
        try {
            socket = new Socket("localhost", 8084);

            System.out.println("Conectado ao servidor");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForUserInput() {
        try {
            BufferedReader kbIn = new BufferedReader(new InputStreamReader(System.in));
            String kbMsg = "";
            while (true) {
                kbMsg = kbIn.readLine();
                out.println(kbMsg);
                out.flush();
            }
        } catch (Exception e) {
            e.addSuppressed(e);
        }
    }

    // TODO: implementar método (ver Server)
    private void sendFile(int fileIndex) {
    }

    // TODO: implementar método (ver Server)
    private void receiveFile(String fileName) {
    }

    @Override
    public void run() {
        waitForMessages();
    }

    private void waitForMessages() {
        String msg = "";
        try {
            while (true) {
                msg = in.readLine();
                processMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: interpretar a mensage recebido. Obs: tem que receber também o nome do arquivo
    private void processMessage(String msg) {
        System.out.println(msg);
    }
}