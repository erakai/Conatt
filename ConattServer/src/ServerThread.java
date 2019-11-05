import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.Server;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {
    public final int SERVER_PORT;

    private ServerDisplay display;

    public List<ClientHandler> clients;

    public ServerThread(int port) {
        this.SERVER_PORT = port;
        clients = new ArrayList<>();

        display = ServerDisplay.init(this);
    }

    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            log("Server started on port " + SERVER_PORT + ".");
            while(true) {
                Socket acceptedSocket = serverSocket.accept();
                ClientHandler newHandler = new ClientHandler(acceptedSocket);
                clients.add(newHandler);
                new Thread(newHandler).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    private void cleanClients() {
        List<ClientHandler> toRemove = new ArrayList<>();
        for (ClientHandler c: clients) {
            if (c.client.isClosed() || c == null) {
                toRemove.add(c);
            }
        }
        clients.removeAll(toRemove);
    }

    private void sendToAll(String message) throws IOException {
        for (ClientHandler loopedClient: clients) {
            if (loopedClient != null) {
                loopedClient.sendMessage(message);
            }
        }
    }

    private void log(String toLog) {
        System.out.println(toLog);
        display.log(toLog);
        //write to file
    }

    public void closeAllClients() throws IOException {
        cleanClients();

        for (ClientHandler loopedClient: clients) {
            if (loopedClient != null) {
                loopedClient.closeAll();
            }
        }
    }

    public ClientHandler[] getAllClients() {
        cleanClients();

        ClientHandler[] arrayClients = new ClientHandler[clients.size()];
        int i = 0;
        for (ClientHandler loopedClient: clients) {
            if (loopedClient != null) {
                arrayClients[i] = loopedClient;
                i++;
            }
        }
        return arrayClients;
    }

    private class ClientHandler implements Runnable {
        private Socket client;
        private String clientUsername;


        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket client) {
            this.client = client;
            log("ClientHandler created for new client at " + client.getInetAddress());
        }

        @Override
        public void run() {
            try {
                handleClient();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void handleClient() throws IOException {
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String currentLine;

            receiveLoop: while((currentLine = in.readLine()) != null) {
                if (Character.toString(currentLine.charAt(0)).equals("/")) {
                    String[] tokens = currentLine.split(" ");
                    switch (tokens[0].toUpperCase()) {
                        case "/QUIT":
                            clientCalledQuit();
                            break receiveLoop;
                        case "/USERNAME":
                            clientCalledUsername(currentLine);
                            break;
                        case "/ONLINE":
                            clientCalledOnline();
                            break;
                        case "/HELP":
                            clientCalledHelp();
                            break;
                        default:
                            log(clientUsername + " used unrecognized command " + currentLine + ".");
                            sendServerMessage("Unrecognized command.");
                    }
                } else {
                    log(getClientUsername() + ": " + currentLine);

                    String userMessage = getAnonymousUsername();
                    for (int i = getAnonymousUsername().length(); i <= 10; i++) {
                        userMessage += "-";
                    }
                    sendToAll(userMessage + "> " + currentLine);
                }

            }

            closeAll();
        }

        private void clientCalledQuit() throws IOException{
            log(getClientUsername() + " has quit.");
            sendToAll(getAnonymousUsername() + " has quit.");
            sendMessage("/QUIT");
        }

        private void clientCalledHelp() throws IOException {
            log(getAnonymousUsername() + " called help.");
            sendServerMessage("Existing commands include /username, /online, and /quit.");
        }

        private void clientCalledUsername(String currentLine) throws IOException {
            if (clientUsername != null) {
                sendToAll(getClientUsername() + " has quit.");
            }
            if (currentLine.length() < 11) {
                sendServerMessage("Please enter a valid username - \"/username [name]\".");
            } else {
                String userAtt = currentLine.substring(10);

                if (userAtt.length() > 10) {
                    sendServerMessage("Usernames must be less than 10 characters!");
                } else {
                    this.clientUsername = currentLine.substring(10);
                    log("Client at " + client.getInetAddress() + " has set username to " + getClientUsername());
                    sendToAll(getClientUsername() + " has joined.");
                    sendServerMessage("You have set your username to \"" + getClientUsername() + "\".");
                }
            }
        }

        private void clientCalledOnline() throws IOException {
            String onlineMessage = "Online: ";
            ClientHandler[] arrayClients = getAllClients();
            for (int i = 0; i<arrayClients.length; i++) {
                if (arrayClients[i] == this) {
                    onlineMessage += (getAnonymousUsername() + " (You)");
                } else {
                    onlineMessage += (arrayClients[i].getAnonymousUsername());
                }
                if (i != arrayClients.length-1) {
                    onlineMessage += ", ";
                }
            }
            sendServerMessage(onlineMessage);
            log(getClientUsername() + " called /online.");
        }

        private void closeAll()  throws IOException {
            in.close();
            out.close();
            client.close();
        }

        private void sendMessage(String content) throws IOException {
            out.println(content);
        }

        private void sendServerMessage(String content) throws IOException {
            out.println("/SERVERMESSAGE " + content);
        }

        public String getClientUsername() {
            if (clientUsername == null) {
                return String.valueOf(client.getInetAddress()).replaceAll("/", "");
            }
            return clientUsername;
        }

        private String getAnonymousUsername() {
            if (clientUsername == null) {
                return "Anonymous";
            }
            return getClientUsername();
        }


    }

}
