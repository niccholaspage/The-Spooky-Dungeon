package com.nicholasnassar.thespookydungeon.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nicholasnassar.thespookydungeon.packets.PacketMessage;
import com.nicholasnassar.thespookydungeon.packets.PacketPlayClip;
import com.nicholasnassar.thespookydungeon.packets.PacketUpdateUI;

import javax.swing.*;

public class ClientListener extends Listener {
    private final DungeonClient client;

    public ClientListener(DungeonClient client) {
        this.client = client;
    }

    @Override
    public void connected(Connection connection) {

    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PacketMessage) {
            PacketMessage packet = (PacketMessage) object;

            client.getGame().printText(packet.message);
        } else if (object instanceof PacketUpdateUI) {
            GUI gui = client.getGUI();

            gui.updateUI((PacketUpdateUI) object);
        } else if (object instanceof PacketPlayClip) {
            PacketPlayClip packet = (PacketPlayClip) object;

            client.getGUI().playClip(packet.clip);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        if (!client.isServer()) {
            JOptionPane.showMessageDialog(null, "The server shut down! Exiting!", "Error!", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }
}
