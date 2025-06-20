import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Client implements Runnable {

    Thread thread;
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.setup();
        client.waitForUserInput();
    }

    public void setup() {
        try {
            socket = new Socket("localhost", 8084);

            System.out.println("Connected to server");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForUserInput() {
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

    public static void download() {
        try {
            int ct = 0;
            ServerSocket ss = new ServerSocket(8084);
            Socket s = ss.accept();

            byte buffer[] = new byte[4096];
            while (s.getInputStream().read(buffer) > 0) {
                System.out.println("R " + ++ct); //TODO: adaptar ao contexto do trabalho
            }

            ss.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void upload() { //TODO: adaptar ao contexto do trabalho
        try {
            Socket s = new Socket("localhost", 8084);
            FileInputStream fr = new FileInputStream("/home/gabriel/Downloads/slides.pdf");
            byte buffer[] = new byte[4096];
            int ct = 0;

            while (fr.read(buffer) > 0) {
                System.out.println("R " + ++ct);
                s.getOutputStream().write(buffer);
                s.getOutputStream().flush();
            }

            s.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        waitForMessages();
    }

    public void waitForMessages() {
        try {
            while (true) {
                System.out.println(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}