package com.spectral.net.packet.impl;

import java.util.Optional;

import com.spectral.game.GameConstants;
import com.spectral.game.content.combat.magic.EffectSpells.EffectSpell;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.Graphic;
import com.spectral.game.model.GraphicHeight;
import com.spectral.game.model.Item;
import com.spectral.game.model.Skill;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketConstants;
import com.spectral.net.packet.PacketListener;

/**
 * Handles the packet for using magic spells on items ingame.
 * 
 * @author Professor Oak
 */
public class MagicOnItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		switch(packet.getOpcode()) {
		case PacketConstants.MAGIC_ON_ITEM_OPCODE:
			int slot = packet.readShort();
			int itemId = packet.readShortA();
			int childId = packet.readShort();
			int spellId = packet.readShortA();
			if(!player.getClickDelay().elapsed(1300))
				return;
			if(slot < 0 || slot > player.getInventory().capacity())
				return;
			if(player.getInventory().getItems()[slot].getId() != itemId)
				return;
			Optional<EffectSpell> spell = EffectSpell.forSpellId(spellId);
			if(!spell.isPresent()) {
				return;
			}
			Item item = player.getInventory().getItems()[slot];
			switch(spell.get()) {
			case LOW_ALCHEMY:
			case HIGH_ALCHEMY:
				if(!item.getDefinition().isTradeable() || !item.getDefinition().isSellable() || item.getId() == 995
					|| item.getDefinition().getHighAlchValue() <= 0 || item.getDefinition().getLowAlchValue() <= 0) {
					player.getPacketSender().sendMessage("This spell can not be cast on this item.");
					return;
				}
				if(!spell.get().getSpell().canCast(player, true)) {
					return;
				}
				player.getInventory().delete(itemId, 1);
				player.performAnimation(new Animation(712));
				if(spell.get() == EffectSpell.LOW_ALCHEMY) {
					player.getInventory().add(995, item.getDefinition().getLowAlchValue());
				} else {
					player.getInventory().add(995, item.getDefinition().getHighAlchValue());
				}
				player.performGraphic(new Graphic(112, GraphicHeight.HIGH));
				player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience());
				player.getPacketSender().sendTab(6);
				break;
			default:
				break;
			}
		}
	}
}