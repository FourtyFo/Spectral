package com.spectral.game.model.syntax.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Skill;
import com.spectral.game.model.syntax.EnterSyntax;

public class SetLevel implements EnterSyntax {
	
	private Skill skill;
	public SetLevel(Skill skill) {
		this.skill = skill;
	}

	@Override
	public void handleSyntax(Player player, String input) {
		
	}

	@Override
	public void handleSyntax(Player player, int input) {
		if(input <= 0 || input > 99) {
			player.getPacketSender().sendMessage("Invalid syntax. Please enter a level in the range of 1-99.");
			return;
		}
		player.getSkillManager().setLevel(skill, input);
	}
}
