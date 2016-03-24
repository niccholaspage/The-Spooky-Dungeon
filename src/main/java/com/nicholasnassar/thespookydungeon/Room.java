package com.nicholasnassar.thespookydungeon;

import com.nicholasnassar.thespookydungeon.server.Player;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

/**
 * Class Room - a room in an adventure game.
 * <p>
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * A "Room" represents one location in the scenery of the game.  It is
 * connected to other rooms via exits.  For each existing exit, the room
 * stores a reference to the neighboring room. The room also can store
 * items and require a certain item to allow entrance into the room.
 */

public class Room {
    private final String description;

    private final String image;

    private final HashMap<String, Room> exits;

    private final ArrayList<Creature> enemies;

    private final ArrayList<Item> items;

    private Item requiredItem;

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard". The room has a random chance of
     * having different types of potions added to it.
     *
     * @param description The room's description.
     */
    public Room(String description, String image) {
        this.description = description;

        this.image = image;

        exits = new HashMap<>();

        enemies = new ArrayList<>();

        items = new ArrayList<>();

        double chance = Math.random();

        if (chance < 0.5) {
            addItem(new Item("Health Potion"));
        } else if (chance < 0.8) {
            addItem(new Item("Super Health Potion"));
        } else if (chance < 0.9) {
            addItem(new Item("Mega Health Potion"));
        }

        requiredItem = null;
    }

    /**
     * Define an exit from this room.
     *
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * Returns the image used in the GUI for this room
     */
    public String getImage() {
        return image;
    }

    /**
     * Return a description of the room in the form:
     * You are in the kitchen.
     * Exits: north west
     * You see a (listofitemshere) on the ground.
     * There is a (enemyhere) with (health) in this room!
     *
     * @return A long description of this room
     */
    public String getLongDescription(Player player) {
        String longDescription = "You are " + description + ".\n" + getExitString();

        if (!items.isEmpty() && player.hasSearchedRoom(this)) {
            longDescription += "\n" + getItemsString();
        }

        for (Creature enemy : enemies) {
            longDescription += "\n" + "There is a " + enemy.getName().toLowerCase() + " with " + enemy.getHealth() + " health in this room!";

        }

        return longDescription;
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     *
     * @return Details of the room's exits.
     */
    private String getExitString() {
        String returnString = "Exits:";

        Set<String> keys = exits.keySet();

        for (String exit : keys) {
            returnString += " " + exit;
        }

        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     *
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * Returns the enemies in the room
     */
    public ArrayList<Creature> getEnemies() {
        return enemies;
    }

    /**
     * Adds an enemy to the room
     */
    public void addEnemy(Creature creature) {
        enemies.add(creature);
    }

    /**
     * Removes an enemy from the room
     */
    public void removeEnemy(Creature creature) {
        enemies.remove(creature);
    }

    /**
     * Returns an ArrayList of the items in the room
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Adds an item to the room's array of items
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Removes an item from the room's array of items
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Returns a String with a nicely
     * formatted list of items available
     * in this room
     */
    public String getItemsString() {
        String itemsFound = "You see a " + items.get(0).getName().toLowerCase();

        for (int i = 1; i < items.size(); i++) {
            String itemName = items.get(i).getName().toLowerCase();

            if (i != 0 && i == items.size() - 1) {
                if (items.size() > 2) {
                    itemsFound += ",";
                }
                itemsFound += " and a " + itemName;
            } else {
                itemsFound += ", a " + itemName;
            }
        }

        return itemsFound + " on the ground.";
    }

    /**
     * Sets the required item
     * needed to enter the room
     */
    public void setRequiredItem(Item requiredItem) {
        this.requiredItem = requiredItem;
    }

    /**
     * Returns the required item
     * needed to enter the room
     */
    public Item getRequiredItem() {
        return requiredItem;
    }
}

