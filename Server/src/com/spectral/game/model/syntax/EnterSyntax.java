package com.spectral.game.model.syntax;

import com.spectral.game.entity.impl.player.Player;

public interface EnterSyntax {

	public abstract void handleSyntax(Player player, String input);
	public abstract void handleSyntax(Player player, int input);
}
