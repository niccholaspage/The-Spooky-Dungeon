package com.nicholasnassar.thespookydungeon;

import com.nicholasnassar.thespookydungeon.client.ConnectDialog;
import com.nicholasnassar.thespookydungeon.server.DungeonServer;

import javax.swing.*;

/**
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * This main class provides a static main method
 * to allow a runnable JAR file to be created
 */
public class Main {
    /**
     * The main method provided allows a JAR file
     * to automatically start the game.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--server")) {
            new DungeonServer().create(true);
        } else {
            try {
                //Tries to match the system's look and feel to match other applications
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                //If it fails, we will fail without any error message since the program will still function properly
            }

            new ConnectDialog();
        }
    }
}
