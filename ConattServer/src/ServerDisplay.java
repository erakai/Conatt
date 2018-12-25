import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

public class ServerDisplay extends JPanel {

    public static JFrame frame;
    public static ServerDisplay mainPanel;
    public static ServerThread myServer;

    private JTextArea loggingDisplay;


    public ServerDisplay(LayoutManager layout) {
        super(layout);
    }


    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();

        loggingDisplay = new JTextArea(25,30);
        c.insets = new Insets(10, 10, 10, 10);
        loggingDisplay.setLineWrap(false);
        loggingDisplay.setWrapStyleWord(true);
        loggingDisplay.setFont(new Font(loggingDisplay.getFont().getName(), loggingDisplay.getFont().getStyle(), (int)(loggingDisplay.getFont().getSize()*0.6)));
        JScrollPane areaScrollPane = new JScrollPane(loggingDisplay);
        loggingDisplay.setEditable(false);
        areaScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        c.gridwidth = 2;
        add(areaScrollPane, c);
    }

    public void log(String toLog) {
        loggingDisplay.append(toLog + "\n");
    }

    public static ServerDisplay init(ServerThread myServer) {
        ServerDisplay.myServer = myServer;
        frame = new JFrame("ServerDisplay");
        mainPanel = new ServerDisplay(new GridBagLayout());
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
                        ServerDisplay.myServer.closeAllClients();
                    } catch (IOException ex) { ex.printStackTrace(); }
                    System.exit(0);
                }
            }
        });
        frame.setVisible(true);
        return mainPanel;
    }

}
