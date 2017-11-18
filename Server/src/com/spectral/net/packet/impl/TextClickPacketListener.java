package com.spectral.net.packet.impl;

import com.spectral.game.content.clan.ClanChatManager;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.container.impl.Bank;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

public class TextClickPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int frame = packet.readInt();
		int action = packet.readByte();
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		if(Bank.handleButton(player, frame, action)) {
			return;
		}
		if(ClanChatManager.handleButton(player, frame, action)) {
			return;
		}
	}
}
