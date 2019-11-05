import javax.swing.*;

public class ClientMain {
    public static final int SERVER_PORT = 8701;
    public static final String SERVER_NAME = "10.200.24.88";

    public static void main(String[] args) {
        JTextField portField = new JTextField(6);
        portField.setText(String.valueOf(SERVER_PORT));
        JTextField ipField = new JTextField(6);
        ipField.setText(SERVER_NAME);

        JTextField widthField = new JTextField(6);
        widthField.setText(String.valueOf(ConnatDisplay.WIDTH));
        JTextField heightField = new JTextField(6);
        heightField.setText(String.valueOf(ConnatDisplay.HEIGHT));

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));

        JPanel serverInfo = new JPanel();
        serverInfo.setLayout(new BoxLayout(serverInfo, BoxLayout.LINE_AXIS));
        serverInfo.add(new JLabel("Port:"));
        serverInfo.add(portField);
        serverInfo.add(Box.createHorizontalStrut(5)); // a spacer
        serverInfo.add(new JLabel("IP:"));
        serverInfo.add(ipField);
        myPanel.add(serverInfo);

        JPanel settingInfo = new JPanel();
        settingInfo.setLayout(new BoxLayout(settingInfo, BoxLayout.LINE_AXIS));
        settingInfo.add(new JLabel("Width:"));
        settingInfo.add(widthField);
        settingInfo.add(Box.createHorizontalStrut(5)); // a spacer
        settingInfo.add(new JLabel("Height: "));
        settingInfo.add(heightField);
        myPanel.add(settingInfo);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter client settings.", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ConnatDisplay.WIDTH = Integer.parseInt(widthField.getText());
            ConnatDisplay.HEIGHT = Integer.parseInt(heightField.getText());
            ConnatClient c = new ConnatClient(ipField.getText(), Integer.parseInt(portField.getText()));
            c.connect();
        }


    }

}
