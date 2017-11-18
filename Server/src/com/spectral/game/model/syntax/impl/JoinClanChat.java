package com.spectral.game.model.syntax.impl;

import com.spectral.game.content.clan.ClanChatManager;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.syntax.EnterSyntax;

public class JoinClanChat implements EnterSyntax {
	
	@Override
	public void handleSyntax(Player player, String input) {
		ClanChatManager.join(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}
}
