package com.spectral.net.packet.impl;

import com.spectral.game.definition.NpcDefinition;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

public class ExamineNpcPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int npc = packet.readShort();
		if(npc <= 0) {
			return;
		}

		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		NpcDefinition npcDef = NpcDefinition.forId(npc);
		if(npcDef != null) {
			player.getPacketSender().sendMessage(npcDef.getExamine());
		}
	}

}
