package com.nicholasnassar.thespookydungeon;

/**
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * This class represents an item by its name.
 * This class is used as part of a collection
 * of items to represent an inventory or items
 * on the ground.
 */
public class Item {
    private String name;

    public Item() {

    }

    /**
     * Constructs an item with the
     * given name
     */
    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
