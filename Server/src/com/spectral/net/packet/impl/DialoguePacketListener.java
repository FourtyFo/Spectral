package com.spectral.net.packet.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.dialogue.DialogueManager;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketConstants;
import com.spectral.net.packet.PacketListener;

/**
 * Represents a packet used for handling dialogues.
 * This specific packet currently handles the action
 * for clicking the "next" option during a dialogue.
 * 
 * @author Professor Oak
 */

public class DialoguePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		switch (packet.getOpcode()) {
		case PacketConstants.DIALOGUE_OPCODE:
			DialogueManager.next(player);
			break;
		}
	}
}
