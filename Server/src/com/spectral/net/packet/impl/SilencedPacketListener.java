package com.spectral.net.packet.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

/**
 * This packet listener is called when a packet should not execute
 * any particular action or event, but will also not print out
 * any debug information.
 * 
 * @author relex lawl
 */

public class SilencedPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
	
	}
}
