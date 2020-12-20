package net.debreczeni.remoteclient.ui;

import net.debreczeni.remoteclient.controller.ClientHandler;
import net.debreczeni.remoteclient.model.Client;
import net.debreczeni.remotecommon.util.InetAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

@Component
public class Main extends JFrame {

    private Client client;
    private ClientHandler clientHandler;
    private JPanel[][] content;

    private JButton startConnection;
    private JTextField inputAddress;

    private JTextField inputPassword;
    private JTextField inputPort;

    private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

    private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);

    private static final String PASSWORD_REGEX = "^[a-zA-Z]{6}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private static final String PORT_REGEX = "^[0-9]{4,5}$";

    private static final Pattern PORT_PATTERN = Pattern.compile(PORT_REGEX);

    Main() {
        init();
    }

    private void init() {

        initClientHandler();
        initGridLayout();

        createElements();
        addElements();

        connectionButton();

        pack();
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void connectionButton() {
        startConnection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String inputAddressText = inputAddress.getText();
                String inputPasswordText = inputPassword.getText();
                String inputPortText = inputPort.getText();

                if (!checkIp(inputAddressText)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Wrong ip Address", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!checkPassword(inputPasswordText)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!checkPort(inputPortText)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Wrong Port", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //TO DO: what happens after pressing the "Start Connection" Button
            }
        });
    }

    private boolean checkPassword(String password) {
        if (password == null) {
            return false;
        } else {
            return (PASSWORD_PATTERN.matcher(password).matches());
        }
    }

    private boolean checkPort(String port) {
        if (port == null) {
            return false;
        } else {
            return (PORT_PATTERN.matcher(port).matches());
        }
    }

    private boolean checkIp(String address) {
        if (address == null) {
            return false;
        } else {
            return IP_PATTERN.matcher(address).matches();
        }
    }

    private void initClientHandler() {
        client = new Client("Hunor");
        clientHandler = new ClientHandler(client);
    }

    private void initGridLayout() {
        int i = 7; //rows
        int j = 3; //columns
        content = new JPanel[i][j];
        setLayout(new GridLayout(i, j));

        for (int m = 0; m < i; m++) {
            for (int n = 0; n < j; n++) {
                content[m][n] = new JPanel();
                add(content[m][n]);
            }
        }
    }

    private void createElements() {
        startConnection = new JButton("Start Connection");
        inputAddress = new JTextField(16);
        inputPassword = new JTextField(16);
        inputPort = new JTextField(16);
    }

    private void addElements() {
        content[1][0].add(new JLabel("The local IP address is: " + InetAddress.getLocalAddress()));
        content[2][0].add(new JLabel("The host name is: " + InetAddress.getHostName()));
        content[3][0].add(new JLabel("The public IP address is: " + InetAddress.getPublicAddress()));
        content[4][0].add(new JLabel("The login token: " + clientHandler.getLoginToken()));

        content[2][1].add(startConnection);

        content[1][2].add(new JLabel("Address: "));
        content[3][2].add(new JLabel("Password: "));
        content[5][2].add(new JLabel("Port: "));

        content[2][2].add(inputAddress);
        content[4][2].add(inputPassword);
        content[6][2].add(inputPort);

        content[0][1].add(new JLabel("Remote control app"));
    }
}
