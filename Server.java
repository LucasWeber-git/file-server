import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Server {

    private static final String SERVER_PATH = "C:/Projects/file-server/files/server/";

    List<Path> fileList = null;

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) {
        Server server = new Server();
        server.waitForConnection();
    }

    private void waitForConnection() {
        try (ServerSocket serverSocket = new ServerSocket(8084)) {
            while (true) {
                System.out.println("Aguardando conexao...");
                socket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());

                waitForMessages();
            }
        } catch (SocketException e) {
            System.out.println("Cliente desconectado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForMessages() {
        String msg = "";
        try {
            while (true) {
                msg = in.readLine();
                processMessage(msg);
            }
        } catch (SocketException e) {
            System.out.println("Cliente desconectado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String msg) throws IOException {
        if (msg == null || msg.isEmpty()) {
            System.out.println("Mensagem vazia");
            return;
        }

        List<String> parameters = new ArrayList<>(Arrays.asList(msg.split(" ")));
        String command = parameters.remove(0).toLowerCase();

        switch (command) {
            case "list":
                list();
                break;
            case "down":
                sendFile(Integer.parseInt(parameters.get(0)) - 1);
                break;
            case "up":
                receiveFile(parameters.get(0), Long.parseLong(parameters.get(1)));
                break;
            default:
                System.out.println("Comando desconhecido: " + command);
                break;
        }
    }

    private void list() {
        System.out.println("Listando arquivos...");

        updateFileList();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileList.size(); i++) {
            sb.append(String.format("%d - %s", i + 1, fileList.get(i).getFileName()));

            if (i < fileList.size() - 1) {
                sb.append("\n");
            }
        }

        sendMessage(sb.toString());
    }

    private void sendFile(int fileIndex) throws IOException {
        if (fileList == null || fileList.isEmpty()) {
            updateFileList();
        }

        String filename = fileList.get(fileIndex).getFileName().toString();
        long size = Files.size(fileList.get(fileIndex));

        sendMessage(String.format("down %s %d", filename, size));

        FileUtils.upload(SERVER_PATH, filename, socket);
    }

    private void receiveFile(String filename, long size) {
        sendMessage(String.format("up %s", filename));

        FileUtils.download(SERVER_PATH, filename, size, socket);
        updateFileList();
    }

    private void updateFileList() {
        try (Stream<Path> stream = Files.list(Paths.get(SERVER_PATH))) {
            fileList = stream.toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

}
