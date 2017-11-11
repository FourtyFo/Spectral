package com.spectral.net.packet.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

/**
 * This packet listener is called when a player's region has been loaded.
 * 
 * @author relex lawl
 */

public class FinalizedMapRegionChangePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
	}
}