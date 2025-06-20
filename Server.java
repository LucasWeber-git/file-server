import java.io.BufferedReader;
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
                System.out.println("Aguardando conexão...");
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

    private void processMessage(String msg) {
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
                receiveFile(parameters.get(0));
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
            sb.append(String.format("%d - %s\n", i + 1, fileList.get(i).getFileName()));
        }

        sendMessage(sb.toString());
    }

    private void sendFile(int fileIndex) {
        System.out.println("Enviando arquivo: " + fileList.get(fileIndex).getFileName());

        FileUtils.upload(fileList.get(fileIndex), socket);

        sendMessage(String.format("arquivo %s salvo com sucesso!", fileList.get(fileIndex).getFileName()));
    }

    private void receiveFile(String fileName) {
        System.out.println("Recebendo arquivo: " + fileName);

        Path filePath = Paths.get(SERVER_PATH, fileName);
        FileUtils.download(filePath, socket);

        updateFileList();

        sendMessage(String.format("arquivo %s transferido!", fileName));
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
