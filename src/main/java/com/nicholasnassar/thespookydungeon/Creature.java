package com.nicholasnassar.thespookydungeon;

/**
 * This class is part of the "The Spooky Dungeon" application.
 * "The Spooky Dungeon" is an advanced, graphical based RPG
 * game.
 * <p>
 * This class represents a creature. It provides
 * a creature's name, health, maximum health, minimum
 * and maximum damage values, minimum and maximum
 * damage block values, and an item drop
 */
public class Creature {
    private String name;
    private Room room;
    private int health;
    private int maxHealth;
    private int minDamage;
    private int maxDamage;
    private int minDamageBlock;
    private int maxDamageBlock;
    private Item drop;

    public Creature() {

    }

    /**
     * Constructs an item with the
     * given name, maximum health,
     * minimum damage, and maximum
     * damage. The creature's health
     * will be set to their maximum
     * health automatically.
     */
    public Creature(String name, int maxHealth, int minDamage, int maxDamage) {
        this.name = name;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        minDamageBlock = 0;
        maxDamageBlock = 0;
        drop = null;
    }

    /**
     * Returns the name of the creature
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the room the creature is in
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room the creature is in
     *
     * @param room
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Sets the max health of the creature
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Returns the max health of the creature
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the health of the creature
     * <p>
     * This method to make sure that the
     * health parameter is valid so that
     * the health will not be set higher
     * than the maximum health or lower
     * than zero
     */
    public void setHealth(int health) {
        if (health < 0) {
            health = 0;
        }
        if (health > maxHealth) {
            health = maxHealth;
        }
        this.health = health;
    }

    /**
     * Add the given health on to
     * the current health of the
     * creature
     */
    public void gainHealth(int health) {
        setHealth(getHealth() + health);
    }

    /**
     * Returns the health
     * of the creature
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the minimum damage
     * of the creature
     */
    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    /**
     * Returns the minimum damage
     * of the creature
     */
    public int getMinDamage() {
        return minDamage;
    }

    /**
     * Sets the maximum damage
     * of the creature
     */
    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    /**
     * Returns the maximum damage
     * of the creature
     */
    public int getMaxDamage() {
        return maxDamage;
    }

    /**
     * Sets the minimum damage
     * block of the creature
     */
    public void setMinDamageBlock(int minDamageBlock) {
        this.minDamageBlock = minDamageBlock;
    }

    /**
     * Returns the minimum damage
     * block of the creature
     */
    public int getMinDamageBlock() {
        return minDamageBlock;
    }

    /**
     * Sets the maximum damage
     * block of the creature
     */
    public void setMaxDamageBlock(int maxDamageBlock) {
        this.maxDamageBlock = maxDamageBlock;
    }

    /**
     * Returns the max damage
     * block of the creature
     */
    public int getMaxDamageBlock() {
        return maxDamageBlock;
    }

    /**
     * Sets the item drop of
     * this creature. This item
     * will be dropped in the room
     * when the creature dies
     */
    public void setDrop(Item drop) {
        this.drop = drop;
    }

    /**
     * Returns the item drop
     * of the creature
     */
    public Item getDrop() {
        return drop;
    }
}
