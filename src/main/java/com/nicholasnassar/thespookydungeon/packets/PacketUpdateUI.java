package com.nicholasnassar.thespookydungeon.packets;

import com.nicholasnassar.thespookydungeon.Creature;
import com.nicholasnassar.thespookydungeon.Item;

public class PacketUpdateUI {
    public String roomImage;
    public boolean northEnabled, eastEnabled, southEnabled, westEnabled, backEnabled;
    public Item[] roomItems, inventory;
    public Creature[] enemies;
    public boolean startSlash;
    public int health, maxHealth, minDamage, maxDamage, minDamageBlock, maxDamageBlock;
}
