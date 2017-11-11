package com.spectral.net.packet.impl;

import com.spectral.game.definition.ItemDefinition;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;
import com.spectral.util.Misc;

public class ExamineItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int item = packet.readShort();
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		//Coins
		if(item == 995 || item == 13307) {
			player.getPacketSender().sendMessage("@red@" + Misc.insertCommasToNumber(""+player.getInventory().getAmount(item)+"") +"x coins.");
			return;
		}
		
		//Blowpipe
		if(item == 12926) {
			player.getPacketSender().sendMessage("Fires Dragon darts while coating them with venom. Zulrah scales left: "+player.getBlowpipeScales());
			return;
		}
		
		ItemDefinition itemDef = ItemDefinition.forId(item);
		if(itemDef != null) {
			player.getPacketSender().sendMessage(itemDef.getExamine());
		}
	}

}
