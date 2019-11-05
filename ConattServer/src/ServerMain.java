import javax.swing.*;

public class ServerMain {
    public static final int SERVER_PORT = 8701;

    public static void main(String[] args) {
        JTextField portField = new JTextField(5);
        portField.setText(String.valueOf(SERVER_PORT));

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Port: "));
        myPanel.add(portField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter port number.", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ServerThread server = new ServerThread(Integer.parseInt(portField.getText()));
            server.start();
        }

    }


}
