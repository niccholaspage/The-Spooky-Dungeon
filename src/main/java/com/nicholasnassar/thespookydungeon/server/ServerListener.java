package com.nicholasnassar.thespookydungeon.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nicholasnassar.thespookydungeon.Creature;
import com.nicholasnassar.thespookydungeon.Item;
import com.nicholasnassar.thespookydungeon.Room;
import com.nicholasnassar.thespookydungeon.packets.*;

import java.net.InetSocketAddress;
import java.util.List;

public class ServerListener extends Listener {
    private final DungeonServer server;

    public ServerListener(DungeonServer server) {
        this.server = server;
    }

    @Override
    public void connected(Connection connection) {
        InetSocketAddress address = connection.getRemoteAddressTCP();

        System.out.println("Connection from " + address.getAddress() + ":" + address.getPort() + "!");
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object == null) {
            //Sending a null object? Cut their connection.
            connection.close();

            return;
        }

        if (object instanceof PacketJoin) {
            PacketJoin packet = (PacketJoin) object;

            String name = packet.name;

            if (name == null) {
                return;
            }

            name = name.trim();

            name = name.replaceAll("[^A-Za-z0-9 ]", "");

            if (name.length() > 16) {
                name = name.substring(0, 16).trim();
            }

            if (name.isEmpty()) {
                //This client is screwing with us..
                connection.close();

                return;
            }

            Player player = server.getPlayer(connection);

            if (player != null) {
                //This client is screwing with us..
                connection.close();

                return;
            }

            server.broadcastMessage(name + " has joined!");

            server.addPlayer(player = new Player(server, connection, name));

            player.setRoom(server.getStartingRoom());

            player.sendMessage("Welcome to the spooky dungeon!");
            player.sendMessage("You wake up in a dungeon. You have never been here before. You need to make it out of this dungeon! You have 20 health. You can attack enemies, search for items, grab items, and use items. You need to escape the dungeon to win.");
            player.sendMessage("All your actions are available to the right! Good luck!");
            player.sendMessage();

            System.out.println(name + " has connected.");
        }

        Player player = server.getPlayer(connection);

        if (player == null) {
            //This client is screwing with us. How can player be null after they have joined?
            connection.close();

            return;
        }

        if (object instanceof PacketGo) {
            PacketGo packet = (PacketGo) object;

            Room currentRoom = player.getRoom();
            // Try to leave current room.
            Room nextRoom = currentRoom.getExit(packet.direction);

            if (packet.direction.equals("back")) {
                List<Room> previousRooms = player.getPreviousRooms();
                if (previousRooms.isEmpty()) {
                    player.sendMessage("You can't go back!");
                } else {
                    int lastElement = previousRooms.size() - 1;
                    player.setRoom(previousRooms.get(lastElement));
                    player.playSoundEffect("walk");
                    previousRooms.remove(lastElement);
                    player.updateUI();
                }
            } else {
                List<Item> inventory = player.getInventory();
                if (nextRoom != null && nextRoom.getRequiredItem() != null) {
                    Item requiredItem = nextRoom.getRequiredItem();
                    if (inventory.contains(requiredItem)) {
                        player.sendMessage("You used your " + requiredItem.getName().toLowerCase() + " to open the door!");
                        player.removeItem(requiredItem);
                        nextRoom.setRequiredItem(null);
                    } else {
                        player.sendMessage("You need a " + requiredItem.getName().toLowerCase() + " to enter this room.");
                        return;
                    }
                }
                player.addPreviousRoom(currentRoom);
                player.setRoom(nextRoom);
                player.playSoundEffect("walk");
                //TODO: FIGURE OUT WINNING FOR OTHER PLAYERS?
                if (nextRoom == server.getWinningRoom()) {
                    //The player won! Play a sound clip, print a victory message and disable the buttons
                    player.sendMessage("You've won! Thanks for playing!");
                    player.sendMessage("Credits:");
                    player.sendMessage("Nicholas for making this sweet game");
                    player.sendMessage("Mr. Blochowski for the help and teaching an awesome class");
                    player.sendMessage("Luke for ideas and testing");
                    player.sendMessage("Skyrim for the awesome images");
                    player.sendMessage("freesound.org for the free sound effects");
                    player.playSoundEffect("victory");
                    //gui.disableUI();
                }
            }
        } else if (object instanceof PacketHelp) {
            player.sendMessage("All actions are available to the right!");
            player.sendMessage(player.getRoom().getLongDescription(player));
        } else if (object instanceof PacketSearch) {
            Room room = player.getRoom();

            if (player.hasSearchedRoom(room)) {
                //Room has already been searched, lets end their session.
                connection.close();

                return;
            }

            player.addSearchedRoom(room);

            if (!room.getItems().isEmpty()) {
                player.sendMessage(room.getItemsString());
            }

            player.updateUI();
        } else if (object instanceof PacketGrabItem) {
            PacketGrabItem packet = (PacketGrabItem) object;
            Room room = player.getRoom();
            Item item = room.getItems().get(packet.itemID);
            room.removeItem(item);
            if (item.getName().equals("Epic Sword")) {
                for (int i = 0; i < player.getInventory().size(); i++) {
                    if (player.getInventory().get(i).getName().equals("Sword")) {
                        player.getInventory().remove(i);
                    }
                }
            }
            player.addItem(item);
            if (item.getName().equals("Armor")) {
                player.setMaxHealth(40);
                player.setHealth(40);
                player.sendMessage("You equipped armor! You now have 40 health!");
            } else if (item.getName().equals("Shield")) {
                player.setMinDamageBlock(5);
                player.setMaxDamageBlock(7);
                player.sendMessage("You equipped a shield! You now block 5 to 7 damage on each attack!");
            } else {
                player.sendMessage("You picked up a " + item.getName().toLowerCase() + "!");
            }
            if (player.hasItem("Epic Sword")) {
                player.setMinDamage(10);
                player.setMaxDamage(13);
            } else if (player.hasItem("Sword")) {
                player.setMinDamage(5);
                player.setMaxDamage(7);
            }
            List<Player> playersInRoom = server.getPlayers(room);
            String message = player.getName() + " picked up a " + item.getName().toLowerCase() + "!";
            for (Player loopPlayer : playersInRoom) {
                if (loopPlayer != player) {
                    loopPlayer.sendMessage(message);
                }
                loopPlayer.updateUI();
            }
        } else if (object instanceof PacketUseItem) {
            PacketUseItem packet = (PacketUseItem) object;
            Item item = player.getInventory().get(packet.itemID);
            if (player.getHealth() >= player.getMaxHealth()) {
                player.sendMessage("You are already at full health!");
                return;
            }
            String itemName = item.getName();
            int healthGain = 0;
            if (itemName.equals("Health Potion")) {
                healthGain = 5;
            } else if (itemName.equals("Super Health Potion")) {
                healthGain = 10;
            } else if (itemName.equals("Mega Health Potion")) {
                healthGain = 20;
            }
            player.gainHealth(healthGain);
            player.removeItem(item);
            player.sendMessage("You drank a " + item.getName().toLowerCase() + " and gained " + healthGain +
                    " health! You are now at " + player.getHealth() + " health.");
            String message = player.getName() + " used a " + item.getName().toLowerCase() + "!";
            for (Player loopPlayer : server.getPlayers(player.getRoom())) {
                if (loopPlayer != player) {
                    loopPlayer.sendMessage(message);
                }
                loopPlayer.updateUI();
            }
        } else if (object instanceof PacketAttack) {
            PacketAttack packet = (PacketAttack) object;
            Room room = player.getRoom();
            Creature creature;
            try {
                creature = room.getEnemies().get(packet.enemyID);
            } catch (Exception e) {
                return;
            }
            int damage = calculateDamage(player, creature);
            player.sendMessage("You attacked the " + creature.getName().toLowerCase() + " and dealt " + damage + " damage!");
            creature.setHealth(creature.getHealth() - damage);
            if (creature.getHealth() <= 0) {
                room.removeEnemy(creature);
                player.sendMessage("The " + creature.getName().toLowerCase() + " is dead!");
                Item drop = creature.getDrop();
                if (drop != null) {
                    room.addItem(drop);
                    player.sendMessage("The " + creature.getName().toLowerCase() + " dropped a " + drop.getName().toLowerCase() + "!");
                }
            } else {
                int enemyDamage = calculateDamage(creature, player);
                player.setHealth(player.getHealth() - enemyDamage);
                player.sendMessage("The " + creature.getName().toLowerCase() + " dealt " + enemyDamage + " damage! You have " + player.getHealth() + " HP left!");
            }

            String message = player.getName() + " attacked " + creature.getName().toLowerCase() + "!";
            for (Player loopPlayer : server.getPlayers(player.getRoom())) {
                if (loopPlayer != player) {
                    loopPlayer.sendMessage(message);
                }
                loopPlayer.updateUI(true);
            }
            //gui.playAttackClip();
            if (player.getHealth() <= 0) {
                player.kill();
            }
        } else if (object instanceof PacketChat) {
            String message = ((PacketChat) object).message;

            message = message.trim();

            if (!message.isEmpty()) {
                server.broadcastMessage(player.getName() + ": " + message);
            }
        }
    }

    /**
     * Calculates damage done by a creature, factoring
     * in random damage. If the creature dealing the
     * damage is the player, critical strike will also
     * be taken into effect. If the player is being hit,
     * their damage will be negated if they have a shield.
     */
    public int calculateDamage(Creature damager, Creature victim) {
        int minDamage = damager.getMinDamage();
        int maxDamage = damager.getMaxDamage();
        int damage = (int) (Math.random() * (maxDamage - minDamage + 1)) + minDamage;
        if (damager instanceof Player) {
            if (Math.random() <= 0.3) {
                damage *= 2;
            }
        } else if (victim instanceof Player) {
            Player player = (Player) victim;
            damage -= (int) (Math.random() * (player.getMaxDamageBlock() - player.getMinDamageBlock() + 1))
                    + player.getMinDamageBlock();

            if (damage <= 0) {
                damage = 1;
            }
        }
        return damage;
    }

    @Override
    public void disconnected(Connection connection) {
        Player player = server.getPlayer(connection);

        if (player != null) {
            server.removePlayer(player);

            server.broadcastMessage(player.getName() + " has disconnected.");
        }
    }
}
