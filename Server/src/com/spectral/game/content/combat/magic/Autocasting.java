package com.spectral.game.content.combat.magic;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Skill;
import com.spectral.game.model.equipment.BonusManager;

public class Autocasting {

	public static boolean toggleAutocast(final Player player, int actionButtonId) {
		CombatSpell cbSpell = CombatSpells.getCombatSpell(actionButtonId);
		if(cbSpell == null) {
			return false;
		}
		if(cbSpell.levelRequired() > player.getSkillManager().getCurrentLevel(Skill.MAGIC)) {
			player.getPacketSender().sendMessage("You need a Magic level of at least "+cbSpell.levelRequired()+" to cast this spell.");
			setAutocast(player, null);
			return true;
		}
		
		
		if(player.getCombat().getAutocastSpell() != null && player.getCombat().getAutocastSpell() == cbSpell) {
			
			//Player is already autocasting this spell. Turn it off.
			setAutocast(player, null);
			
		} else {
			
			//Set the new autocast spell
			setAutocast(player, cbSpell);
			
		}
		return true;
	}

	public static void setAutocast(Player player, CombatSpell spell) {
		if(spell == null) {
			player.getPacketSender().sendAutocastId(-1).sendConfig(108, 3);
		} else {
			player.getPacketSender().sendAutocastId(spell.spellId()).sendConfig(108, 1);
		}
		player.getCombat().setAutocastSpell(spell);
		
		BonusManager.update(player);
	}
}
