package com.nicholasnassar.thespookydungeon.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.nicholasnassar.thespookydungeon.Creature;
import com.nicholasnassar.thespookydungeon.Item;
import com.nicholasnassar.thespookydungeon.Room;
import com.nicholasnassar.thespookydungeon.Shared;
import com.nicholasnassar.thespookydungeon.packets.PacketMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DungeonServer {
    private Server server;

    private final List<Player> players;

    private final List<Creature> creatures;

    private Room startingRoom, winningRoom;

    public DungeonServer() {
        players = new ArrayList<>();

        creatures = new ArrayList<>();

        createRooms();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room storage, barracks, northBridge, eastBridge, lair, tavern, stairway, library, cellar, prison, outdoors;

        // create the rooms
        storage = new Room("in the storage room. You see many barrels and crates", "storage");
        barracks = new Room("in the barracks.\nYou see a warning sign", "barracks");
        northBridge = new Room("on a long bridge", "north_bridge");
        eastBridge = new Room("on a short bridge", "east_bridge");
        lair = new Room("in a lair.\nYou see a table and chairs", "lair");
        tavern = new Room("in the tavern. The fire is lit.", "tavern");
        stairway = new Room("on a stairway", "stairway");
        library = new Room("in a library. You see a huge amount of books", "library");
        cellar = new Room("in a wine cellar. You see a few wine bottles and bags on the shelves", "cellar");
        prison = new Room("in a prison.\nYou see a dead man lying on the floor.", "prison");
        outdoors = new Room("outside of the dungeon", "outdoors");

        // initialise room exits
        storage.setExit("north", northBridge);
        storage.setExit("west", barracks);
        storage.setExit("east", eastBridge);
        storage.setExit("south", library);
        storage.addItem(new Item("Sword"));

        barracks.setExit("west", tavern);
        barracks.setExit("east", storage);
        barracks.addItem(new Item("Shield"));

        northBridge.setExit("north", cellar);
        northBridge.setExit("south", storage);

        eastBridge.setExit("west", storage);
        eastBridge.setExit("east", lair);
        eastBridge.addEnemy(new Creature("Rat", 2, 1, 3));

        lair.setExit("west", eastBridge);
        Creature skeleton = new Creature("Skeleton", 20, 8, 10);
        skeleton.setDrop(new Item("Epic Sword"));
        lair.addEnemy(skeleton);

        tavern.setExit("north", stairway);
        tavern.setExit("east", barracks);
        tavern.addEnemy(new Creature("Mega Skeleton", 35, 15, 18));

        stairway.setExit("north", outdoors);
        stairway.setExit("south", tavern);

        library.setExit("north", storage);
        library.setExit("south", prison);
        library.addEnemy(new Creature("Zombie", 10, 5, 8));

        Item silverKey = new Item("Silver Key");

        cellar.setExit("south", northBridge);
        Creature giant = new Creature("Giant", 100, 10, 15);
        Item goldenKey = new Item("Golden Key");
        giant.setDrop(goldenKey);
        cellar.addEnemy(giant);
        cellar.setRequiredItem(silverKey);

        prison.setExit("north", library);
        prison.addItem(silverKey);
        prison.addItem(new Item("Armor"));

        outdoors.setExit("south", stairway);
        //outdoors.setRequiredItem(goldenKey);

        startingRoom = storage;  // start game in the storage room
        winningRoom = outdoors;
    }

    public boolean create(boolean console) {
        server = new Server();

        new Shared(server.getKryo());

        server.addListener(new ServerListener(this));

        server.start();

        try {
            server.bind(7333, 7333);
        } catch (Exception e) {
            System.out.println("Couldn't bind port 7333!");

            return false;
        }

        if (console) {
            System.out.println("Type quit to quit.");

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String inputLine = scanner.nextLine();

                if (inputLine.equalsIgnoreCase("who")) {
                    System.out.println("Players Online: ");
                    players.forEach(player -> System.out.println(player.getName()));
                } else if (inputLine.equalsIgnoreCase("quit")) {
                    break;
                }
            }

            disconnect();

            System.exit(0);
        }

        return true;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Player getPlayer(Connection connection) {
        for (Player player : players) {
            if (player.getConnection() == connection) {
                return player;
            }
        }

        return null;
    }

    public List<Player> getPlayers(Room room) {
        List<Player> players = new ArrayList<>();

        for (Player player : this.players) {
            if (player.getRoom() == room) {
                players.add(player);
            }
        }

        return players;
    }

    public List<Creature> getCreatures(Room room) {
        return room.getEnemies();
    }

    public void broadcastMessage(String message) {
        broadcastMessage(message, players, null);
    }

    public void broadcastMessage(String message, List<Player> players, Player mainPlayer) {
        PacketMessage packet = new PacketMessage();

        packet.message = message;

        for (Player player : players) {
            if (mainPlayer != null) {
                if (player == mainPlayer) {
                    packet.message = message.replace("$1", "You");
                } else {
                    packet.message = message.replace("$1", mainPlayer.getName());
                }
            }

            player.sendTCP(packet);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void disconnect() {
        System.out.println("Shutting down!");
        server.close();
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public Room getWinningRoom() {
        return winningRoom;
    }
}
