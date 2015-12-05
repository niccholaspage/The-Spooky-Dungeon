package com.nicholasnassar.thespookydungeon;

import com.esotericsoftware.kryo.Kryo;
import com.nicholasnassar.thespookydungeon.packets.*;

public class Shared {
    public Shared(Kryo kryo) {
        kryo.register(PacketJoin.class);
        kryo.register(PacketMessage.class);
        kryo.register(PacketUpdateUI.class);
        kryo.register(PacketGo.class);
        kryo.register(PacketPlayClip.class);
        kryo.register(PacketHelp.class);
        kryo.register(Item.class);
        kryo.register(Item[].class);
        kryo.register(PacketSearch.class);
        kryo.register(PacketGrabItem.class);
        kryo.register(PacketUseItem.class);
        kryo.register(PacketAttack.class);
        kryo.register(PacketChat.class);
        kryo.register(Creature.class);
        kryo.register(Creature[].class);
    }
}
