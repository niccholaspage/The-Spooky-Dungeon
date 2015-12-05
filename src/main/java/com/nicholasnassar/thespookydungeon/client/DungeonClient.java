package com.nicholasnassar.thespookydungeon.client;

import com.esotericsoftware.kryonet.Client;
import com.nicholasnassar.thespookydungeon.Shared;
import com.nicholasnassar.thespookydungeon.packets.*;

import javax.swing.*;

public class DungeonClient {
    private final Game game;

    private final GUI gui;

    private final Client client;

    private final boolean server;

    public DungeonClient(Game game, GUI gui, String name, String ip, int port, boolean isServer) {
        this.game = game;

        this.gui = gui;

        this.client = new Client();

        this.server = isServer;

        new Shared(client.getKryo());

        client.addListener(new ClientListener(this));

        client.start();

        try {
            client.connect(5000, ip, port, port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to server! Exiting.", "Error!", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }

        PacketJoin join = new PacketJoin();

        join.name = name;

        client.sendTCP(join);
    }

    public Game getGame() {
        return game;
    }

    public GUI getGUI() {
        return gui;
    }

    public void goRoom(String direction) {
        PacketGo packet = new PacketGo();

        packet.direction = direction;

        client.sendTCP(packet);
    }

    public void askForHelp() {
        client.sendTCP(new PacketHelp());
    }

    public void search() {
        client.sendTCP(new PacketSearch());
    }

    public void grabItem(int itemID) {
        PacketGrabItem packet = new PacketGrabItem();

        packet.itemID = itemID;

        client.sendTCP(packet);
    }

    public void useItem(int itemID) {
        PacketUseItem packet = new PacketUseItem();

        packet.itemID = itemID;

        client.sendTCP(packet);
    }

    public void attack(int enemyID) {
        PacketAttack packet = new PacketAttack();

        packet.enemyID = enemyID;

        client.sendTCP(packet);
    }

    public void chat(String message) {
        PacketChat packet = new PacketChat();

        packet.message = message;

        client.sendTCP(packet);
    }

    public boolean isServer() {
        return server;
    }
}
