package com.nicholasnassar.thespookydungeon.server;

import com.esotericsoftware.kryonet.Connection;
import com.nicholasnassar.thespookydungeon.Creature;
import com.nicholasnassar.thespookydungeon.Item;
import com.nicholasnassar.thespookydungeon.Room;
import com.nicholasnassar.thespookydungeon.packets.PacketMessage;
import com.nicholasnassar.thespookydungeon.packets.PacketPlayClip;
import com.nicholasnassar.thespookydungeon.packets.PacketUpdateUI;

import java.util.ArrayList;
import java.util.List;

public class Player extends Creature {
    private final DungeonServer server;

    private final Connection connection;

    private final List<Room> previousRooms;

    private final List<Room> searchedRooms;

    private final List<Item> inventory;

    public Player(DungeonServer server, Connection connection, String name) {
        super(name, 20, 1, 3);

        this.server = server;

        this.connection = connection;

        previousRooms = new ArrayList<>();

        searchedRooms = new ArrayList<>();

        inventory = new ArrayList<>();
    }

    public Connection getConnection() {
        return connection;
    }

    public void sendTCP(Object object) {
        connection.sendTCP(object);
    }

    public void sendMessage() {
        sendMessage("");
    }

    public void sendMessage(String message) {
        PacketMessage packet = new PacketMessage();

        packet.message = message;

        connection.sendTCP(packet);
    }

    public void updateUI() {
        updateUI(false);
    }

    public void updateUI(boolean sendSlash) {
        PacketUpdateUI packet = new PacketUpdateUI();
        Room room = getRoom();
        packet.roomImage = room.getImage();
        packet.northEnabled = room.getExit("north") != null;
        packet.eastEnabled = room.getExit("east") != null;
        packet.southEnabled = room.getExit("south") != null;
        packet.westEnabled = room.getExit("west") != null;
        packet.backEnabled = !previousRooms.isEmpty();
        if (hasSearchedRoom(room)) {
            packet.roomItems = room.getItems().toArray(new Item[room.getItems().size()]);
        }
        packet.inventory = inventory.isEmpty() ? null : inventory.toArray(new Item[inventory.size()]);
        List<Creature> creatures = server.getCreatures(room);
        packet.enemies = creatures.isEmpty() ? null : creatures.toArray(new Creature[creatures.size()]);
        packet.startSlash = sendSlash;
        if (sendSlash) {
            playSoundEffect("attack");
        }
        packet.health = getHealth();
        packet.maxHealth = getMaxHealth();
        packet.minDamage = getMinDamage();
        packet.maxDamage = getMaxDamage();
        packet.minDamageBlock = getMinDamageBlock();
        packet.maxDamageBlock = getMaxDamageBlock();
        sendTCP(packet);
    }

    @Override
    public void setRoom(Room room) {
        if (getRoom() != null) {
            server.broadcastMessage("$1 left the room!", server.getPlayers(getRoom()), this);
        }

        sendMessage(room.getLongDescription(this));

        server.broadcastMessage("$1 has entered the room!", server.getPlayers(room), this);

        for (Player player : server.getPlayers(room)) {
            sendMessage(player.getName() + " is in this room!");
        }

        super.setRoom(room);

        updateUI();
    }

    public void addPreviousRoom(Room room) {
        previousRooms.add(room);
    }

    public List<Room> getPreviousRooms() {
        return previousRooms;
    }

    public void addSearchedRoom(Room room) {
        searchedRooms.add(room);
    }

    public boolean hasSearchedRoom(Room room) {
        return searchedRooms.contains(room);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                return true;
            }
        }

        return false;
    }

    public void playSoundEffect(String sound) {
        PacketPlayClip packet = new PacketPlayClip();

        packet.clip = sound;

        sendTCP(packet);
    }

    public void kill() {
        sendMessage("You died! Thanks for playing!");
    }
}
