package net.debreczeni.remotedesktop.model;

import io.rsocket.exceptions.RejectedSetupException;
import net.debreczeni.remotedesktop.controller.RClientController;
import net.debreczeni.remotedesktop.util.InetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

@Component
public class Main extends JFrame {

    private User user;
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

        initGridLayout();

        createElements();
        addElements();

        connectionButton();

        pack();
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Autowired
    RClientController rSocketShellClient;

    private void connectionButton() {
        startConnection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String inputAddressText = inputAddress.getText();
                String inputPasswordText = inputPassword.getText();

//                if (!checkIp(inputAddressText)) {
//                    JOptionPane.showMessageDialog(null, "Invalid ip Address", "Warning", JOptionPane.WARNING_MESSAGE);
//                    return;
//                }
                if (!checkPassword(inputPasswordText)) {
                    JOptionPane.showMessageDialog(null, "Invalid Password", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try{
                    rSocketShellClient.login("control", inputPasswordText);
                    rSocketShellClient.screenShare(0, 500, 500);
                }catch (RejectedSetupException e){
                    JOptionPane.showMessageDialog(null, "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

    private boolean checkIp(String address) {
        if (address == null) {
            return false;
        } else {
            return IP_PATTERN.matcher(address).matches();
        }
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
        inputAddress = new JTextField("localhost",16);
        inputPassword = new JPasswordField(User.getInstance().getControlToken(), 6);
    }

    private void addElements() {
        content[1][0].add(new JLabel("The local IP address is: " + InetAddress.getLocalAddress()));
        content[2][0].add(new JLabel("The host name is: " + InetAddress.getHostName()));
        content[3][0].add(new JLabel("The public IP address is: " + InetAddress.getPublicAddress()));
        content[4][0].add(new JTextField("The view token: " + User.getInstance().getViewToken()));
        content[5][0].add(new JTextField("The control token: " + User.getInstance().getControlToken()));

        content[2][1].add(startConnection);

        content[1][2].add(new JLabel("Address: "));
        content[3][2].add(new JLabel("Password: "));

        content[2][2].add(inputAddress);
        content[4][2].add(inputPassword);

        content[0][1].add(new JLabel("Remote control app"));
    }
}
