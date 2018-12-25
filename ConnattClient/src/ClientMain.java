public class ClientMain {
    public static final int SERVER_PORT = 8701;
    public static final String SERVER_NAME = "localhost";


    public static void main(String[] args) {
        ConnatClient c = new ConnatClient(SERVER_NAME, SERVER_PORT);
        c.connect();
    }

}
