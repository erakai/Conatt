import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnatDisplay extends JPanel {

    public static JFrame frame;
    public static ConnatDisplay mainPanel;

    public List<String> sendQueue;

    private static ConnatClient myClient;

    private JTextArea chat, serverChat;
    private JTextField textBar;
    private JButton send;

    public ConnatDisplay(LayoutManager layout) {
        super(layout);
        sendQueue = new ArrayList<>();

    }

    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();

        chat = new JTextArea(18,25);
        c.insets = new Insets(10, 10, 10, 10);
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(chat);
        chat.setEditable(false);
        areaScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        c.gridwidth = 2;
        add(areaScrollPane, c);

        textBar = new JTextField(30);
        c.insets = new Insets(2, 6, 2, 6);
        textBar.setPreferredSize(new Dimension(textBar.getWidth(), 30));
        c.gridy=1;
        c.gridwidth=1;
        textBar.setFont(new Font(textBar.getFont().getName(), textBar.getFont().getStyle(), (int)(textBar.getFont().getSize()*0.6)));
        add(textBar, c);

        send = new JButton("Send");
        send.addActionListener(e -> clickedSend());
        c.gridx=1;
        add(send, c);

        serverChat = new JTextArea(8, 32);
        c.insets = new Insets(10, 5, 5, 5);
        serverChat.setLineWrap(false);
        serverChat.setWrapStyleWord(true);
        serverChat.setForeground(new Color(240, 84, 109));
        serverChat.setFont(new Font(serverChat.getFont().getName(), serverChat.getFont().getStyle(), (int)(serverChat.getFont().getSize()*0.8)));
        JScrollPane asp = new JScrollPane(serverChat);
        serverChat.setEditable(false);
        asp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        c.gridwidth = 2;
        c.gridx=0;
        c.gridy=2;
        add(asp, c);
    }

    public void appendText(String text) {
        chat.append(text + "\n");
    }

    public void appendServerText(String text) { serverChat.append(text + "\n"); }

    private void clickedSend() {
        String toSend = textBar.getText();
        if (!toSend.equals("")) {
            sendQueue.add(toSend);
            textBar.setText("");
        }
    }

    public static ConnatDisplay init(ConnatClient client) {
        myClient = client;
        frame = new JFrame("Connat");
        mainPanel = new ConnatDisplay(new GridBagLayout());
        mainPanel.addComponents();
        frame.add(mainPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?", "Close Window", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                 == JOptionPane.YES_OPTION) {
                    try {
                        myClient.closeAll();
                    } catch (IOException ex) { ex.printStackTrace(); }
                    System.exit(0);
                }
            }
        });
        frame.setVisible(true);
        return mainPanel;
    }

    public List<String> getSendQueue() {
        return sendQueue;
    }

    public void clearSendQueue() {
        sendQueue.clear();
    }

}
