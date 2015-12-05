package com.nicholasnassar.thespookydungeon.client;

import com.nicholasnassar.thespookydungeon.server.DungeonServer;

import javax.swing.*;

/**
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * To play this game, create an instance of this class and call the "play"
 * method.
 * <p>
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser, the GUI, and starts the game. It also evaluates
 * and executes the commands that the GUI calls.
 */

public class Game {
    private GUI gui;
    private DungeonServer server;

    /**
     * Create the game
     */
    public Game() {
        server = null;
    }

    /**
     * This method starts the game.
     * The GUI is created, the client
     * is connected, and then the GUI
     * is displayed
     */
    public void play(DungeonServer server, String name, String ip, int port) {
        this.server = server;
        gui = new GUI(this);
        DungeonClient client = new DungeonClient(this, gui, name, ip, port, server != null);
        gui.setClient(client);
        gui.setVisible(true);
    }

    /**
     * Method called when the game
     * is exiting
     */
    public void closing() {
        if (server != null) {
            server.disconnect();
        }
    }

    /**
     * Prints a string and a new line to the GUI
     */
    public void printText(String text) {
        JTextArea textArea = gui.getTextArea();
        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
