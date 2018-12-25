import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnatClient {

    private final int serverPort;
    private final String serverName;

    private ConnatDisplay display;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ConnatClient(String name, int port) {
        this.serverName = name;
        this.serverPort = port;

        display = ConnatDisplay.init(this);
    }

    public boolean connect() {
        try  {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected to server " + serverName + " on port " + serverPort + ".");
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            startLoops();

            return true;
        } catch (IOException ex) { ex.printStackTrace(); }
        return false;
    }

    private void handleMessages() throws IOException{
        String currentReceived = "";
        while ((currentReceived = in.readLine()) != null) {
            System.out.println(currentReceived);

            if (currentReceived.split(" ")[0].equals("/SERVERMESSAGE")) {
                display.appendServerText(currentReceived.substring(15));
            } else {
                display.appendText(currentReceived);
            }

            if (currentReceived.toUpperCase().equals("/QUIT")) {
                break;
            }
        }
        System.out.println("Closing.");
        closeAll();
    }

    private void handleSending() {

        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!display.getSendQueue().isEmpty()) {
                checkForSendQueue();
            }
        }
    }

    private void checkForSendQueue() {
        for (String s: display.getSendQueue()) {
            sendToServer(s);
        }
        display.clearSendQueue();
    }

    private void sendToServer(String content) {
        out.println(content);
    }

    private void startLoops() {
        Thread receiving = new Thread() {
            @Override
            public void run() {
                try {
                    handleMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread sending = new Thread() {
            @Override
            public void run() {
                handleSending();
            }
        };
        receiving.start();
        sending.start();
    }

    public void closeAll() throws IOException {
        socket.close();
        in.close();
        out.close();
        System.exit(0);
    }

}
