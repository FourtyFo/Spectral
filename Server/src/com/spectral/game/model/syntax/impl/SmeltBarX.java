package com.spectral.game.model.syntax.impl;

import com.spectral.game.content.skill.skillable.impl.Smithing.Bar;
import com.spectral.game.content.skill.skillable.impl.Smithing.Smelting;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.syntax.EnterSyntax;

public class SmeltBarX implements EnterSyntax {
	
	/**
	 * The bar to smelt.
	 */
	private final Bar bar;
	
	public SmeltBarX(Bar bar) {
		this.bar = bar;
	}
	
	@Override
	public void handleSyntax(Player player, String input) {
	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(input <= 0 || input > Integer.MAX_VALUE) {
			return;
		}
		player.getSkillManager().startSkillable(new Smelting(bar,input));
	}
}
