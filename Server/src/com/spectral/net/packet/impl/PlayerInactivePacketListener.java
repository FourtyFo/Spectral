package com.spectral.net.packet.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

public class PlayerInactivePacketListener implements PacketListener {

	//CALLED EVERY 3 MINUTES OF INACTIVITY
	
	@Override
	public void handleMessage(Player player, Packet packet) {
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
	}
}
