package com.spectral.net.packet.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Flag;
import com.spectral.game.model.ChatMessage.Message;
import com.spectral.game.model.dialogue.DialogueManager;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;
import com.spectral.util.Misc;
import com.spectral.util.PlayerPunishment;

/**
 * This packet listener manages the spoken text by a player.
 * 
 * @author Gabriel Hannason
 */

public class ChatPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int color = packet.readByte();
		int effect = packet.readByte();
		String text = packet.readString();
		if(text.length() <= 0) {
			return;
		}
		if(player == null) {
			return;
		}
		if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
			player.getPacketSender().sendMessage("You are muted and cannot chat.");
			return;
		}
		if(Misc.blockedWord(text)) {
			DialogueManager.sendStatement(player, "A word was blocked in your sentence. Please do not repeat it!");
			return;
		}
		player.getChatMessages().set(new Message(color, effect, text));
		player.getUpdateFlag().flag(Flag.CHAT);
	}

}
