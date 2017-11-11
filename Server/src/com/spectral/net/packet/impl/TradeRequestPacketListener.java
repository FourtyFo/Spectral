package com.spectral.net.packet.impl;

import com.spectral.game.World;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.PlayerStatus;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;


public class TradeRequestPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index > World.getPlayers().capacity() || index < 0) {
			return;
		}
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		if(player.busy()) {
			player.getPacketSender().sendMessage("You cannot do that right now.");
			return;
		}
				
		Player p_ = World.getPlayers().get(index);
		
		if(p_.busy()) {
			String msg = "That player is currently busy.";
			
			if(p_.getStatus() == PlayerStatus.TRADING) {
				msg = "That player is currently trading with someone else.";
			}
			
			player.getPacketSender().sendMessage(msg);
			return;
		}
		
		if(p_ != null && player.getLocalPlayers().contains(p_)) {
			player.getTrading().requestTrade(p_);
		}
	}
}
