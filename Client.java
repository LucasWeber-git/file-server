import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client implements Runnable {

    private static final String CLIENT_PATH = "C:/Projects/file-server/files/client/";

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
                processRequest(kbMsg);
            }
        } catch (Exception e) {
            e.addSuppressed(e);
        }
    }

    private void processRequest(String msg) throws IOException {
        List<String> parameters = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String command = parameters.remove(0).toLowerCase();

        if ("up".equals(command)) {
            Path filePath = Paths.get(CLIENT_PATH, parameters.get(0));
            String request = msg + " " + Files.size(filePath);

            out.println(request);
            out.flush();
        } else {
            out.println(msg);
            out.flush();
        }
    }

    @Override
    public void run() {
        waitForResponses();
    }

    private void waitForResponses() {
        String msg = "";
        try {
            while (true) {
                msg = in.readLine();
                processResponse(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processResponse(String msg) throws IOException {
        if (msg == null || msg.isEmpty()) {
            System.out.println("Response vazio");
            return;
        }

        List<String> parameters = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String command = parameters.remove(0).toLowerCase();

        if ("down".equals(command)) {
            down(parameters.get(0), Long.parseLong(parameters.get(1)));
        } else if ("up".equals(command)) {
            up(parameters.get(0));
        } else {
            System.out.println(msg);
        }
    }

    private void down(String filename, long size) {
        FileUtils.download(CLIENT_PATH, filename, size, socket);
        System.out.println(String.format("arquivo %s salvo com sucesso!", filename));
    }

    private void up(String filename) {
        FileUtils.upload(CLIENT_PATH, filename, socket);
        System.out.println(String.format("arquivo %s transferido!", filename));
    }

}