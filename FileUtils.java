import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void download(Path destination, Socket socket) {
        try (InputStream in = socket.getInputStream();
                OutputStream out = Files.newOutputStream(destination)) {

            byte buffer[] = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upload(Path filePath, Socket socket) {
        try {
            FileInputStream fr = new FileInputStream(filePath.toFile());
            byte buffer[] = new byte[4096];

            while (fr.read(buffer) > 0) {
                socket.getOutputStream().write(buffer);
                socket.getOutputStream().flush();
            }

            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
