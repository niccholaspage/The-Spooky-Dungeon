package com.nicholasnassar.thespookydungeon.client;

import com.nicholasnassar.thespookydungeon.server.DungeonServer;

import javax.swing.*;
import java.awt.*;

public class ConnectDialog extends JFrame {
    private DungeonServer server;
    private JTextField ipField, nameField;

    public ConnectDialog() {
        super("Connect");

        getContentPane().setBounds(5, 5, 0, 0);
        setLayout(new GridLayout(3, 2));

        JButton startServer, connect;
        add(new JLabel("IP:"));
        add(ipField = new JTextField());
        ipField.setText("niccholaspage.duckdns.org");
        add(new JLabel("Name:"));
        add(nameField = new JTextField());
        add(startServer = new JButton("Start Server"));
        add(connect = new JButton("Connect"));

        startServer.addActionListener(e -> {
            if (!(server = new DungeonServer()).create(false)) {
                JOptionPane.showMessageDialog(null, "Failed to create server.", "Error!", JOptionPane.ERROR_MESSAGE);
            } else {
                connect("localhost");
            }
        });

        connect.addActionListener(e -> connect(null));

        setSize(300, 125);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connect(String ip) {
        String name = getNickName();

        if (ip == null) {
            ip = getIP();
        }

        if (name == null || ip == null) {
            return;
        }

        dispose();

        new Game().play(server, name, ip, getPort());
    }

    private String getNickName() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You can't have an empty name!", "Error!", JOptionPane.ERROR_MESSAGE);

            return null;
        }

        return name;
    }

    private String getIP() {
        String ip = ipField.getText().trim();

        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No IP entered!", "Error!", JOptionPane.ERROR_MESSAGE);

            return null;
        }

        if (ip.contains(":")) {
            ip = ip.substring(0, ip.indexOf(":")).trim();
        }

        return ip;
    }

    private int getPort() {
        String ip = ipField.getText().trim();

        if (ip.contains(":")) {
            ip = ip.substring(ip.indexOf(":") + 1).trim();
        }

        try {
            return Integer.parseInt(ip);
        } catch (Exception e) {
            return 7333;
        }
    }
}
