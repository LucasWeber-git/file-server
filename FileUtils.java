import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static final int BUFFER_SIZE = 4096;

    public static void download(String directory, String filename, long size, Socket socket) {
        System.out.println("Recebendo arquivo: " + filename);

        Path filePath = Paths.get(directory, filename);

        try (OutputStream out = Files.newOutputStream(filePath)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int totalRead = 0;

            while (totalRead < size) {
                int bytesRead = socket.getInputStream().read(buffer);

                if (bytesRead == -1) {
                    break;
                }
                totalRead += bytesRead;

                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upload(String directory, String filename, Socket socket) {
        System.out.println("Enviando arquivo: " + filename);

        Path filePath = Paths.get(directory, filename);

        try (FileInputStream fr = new FileInputStream(filePath.toFile())) {
            byte buffer[];

            while ((buffer = fr.readNBytes(BUFFER_SIZE)).length > 0) {
                socket.getOutputStream().write(buffer);
                socket.getOutputStream().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
