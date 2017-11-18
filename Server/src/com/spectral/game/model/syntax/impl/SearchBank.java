package com.spectral.game.model.syntax.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.container.impl.Bank;
import com.spectral.game.model.syntax.EnterSyntax;

public class SearchBank implements EnterSyntax {
	
	@Override
	public void handleSyntax(Player player, String input) {
		Bank.search(player, input);
	}

	@Override
	public void handleSyntax(Player player, int input) {
		
	}

}
