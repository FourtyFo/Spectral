package com.spectral.game.content.skill.skillable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.AnimationLoop;
import com.spectral.game.model.Item;
import com.spectral.game.model.RequiredItem;
import com.spectral.game.model.Skill;
import com.spectral.game.model.menu.CreationMenu.CreationMenuAction;
import com.spectral.game.model.menu.impl.SingleItemCreationMenu;
import com.spectral.util.ItemIdentifiers;

/**
 * Represents the Crafting skill, handling
 * all related tasks.
 * 
 * @author Professor Oak
 *
 */
public class Crafting extends ItemIdentifiers {

	/**
	 * Attempts to craft a gem.
	 * @param player
	 * @param itemUsed
	 * @param itemUsedWith
	 * @return
	 */
	public static boolean craftGem(Player player, int itemUsed, int itemUsedWith) {
		if(itemUsed == CHISEL || itemUsedWith == CHISEL) {
			CraftableGem gem = CraftableGem.map.get(itemUsed == CHISEL ? itemUsedWith : itemUsed);
			if(gem != null) {
				player.setCreationMenu(Optional.of(new SingleItemCreationMenu(player, gem.getCut().getId(), "How many would you like to cut?", new CreationMenuAction() {
					@Override
					public void execute(int item, int amount) {
						player.getSkillManager().startSkillable(new ItemCreationSkillable(Arrays.asList(new RequiredItem(new Item(CHISEL), false), new RequiredItem(gem.getUncut(), true)), gem.getCut(), amount, Optional.of(gem.getAnimationLoop()), gem.getLevel(), gem.getExp(), Skill.CRAFTING));
					}
				}).open()));
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Handles Gem crafting.
	 * @author Professor Oak
	 */
	public enum CraftableGem {
		G1(new Item(OPAL), new Item(UNCUT_OPAL), 1, 15, new AnimationLoop(new Animation(890), 3)),
		G2(new Item(JADE), new Item(UNCUT_JADE), 13, 20, new AnimationLoop(new Animation(891), 3)),
		G3(new Item(RED_TOPAZ), new Item(UNCUT_RED_TOPAZ), 16, 25, new AnimationLoop(new Animation(892), 3)),
		G4(new Item(SAPPHIRE), new Item(UNCUT_SAPPHIRE), 20, 50, new AnimationLoop(new Animation(888), 3)),
		G5(new Item(EMERALD), new Item(UNCUT_EMERALD), 27, 68, new AnimationLoop(new Animation(889), 3)),
		G6(new Item(RUBY), new Item(UNCUT_RUBY), 34, 85, new AnimationLoop(new Animation(887), 3)),
		G7(new Item(DIAMOND), new Item(UNCUT_DIAMOND), 43, 108, new AnimationLoop(new Animation(886), 3)),
		G8(new Item(DRAGONSTONE), new Item(UNCUT_DRAGONSTONE), 55, 138, new AnimationLoop(new Animation(885), 3)),
		G9(new Item(ONYX), new Item(UNCUT_ONYX), 67, 168, new AnimationLoop(new Animation(885), 3)),
		G10(new Item(ZENYTE), new Item(UNCUT_ZENYTE), 89, 200, new AnimationLoop(new Animation(885), 3)),
		;
		
		private final Item cut;
		private final Item uncut;
		private final int level;
		private final int exp;
		private final AnimationLoop animLoop;
		
		CraftableGem(Item cut, Item uncut, int level, int exp, AnimationLoop animLoop) {
			this.cut = cut;
			this.uncut = uncut;
			this.level = level;
			this.exp = exp;
			this.animLoop = animLoop;
		}
		
		public Item getCut() {
			return cut;
		}
		
		public Item getUncut() {
			return uncut;
		}

		public int getLevel() {
			return level;
		}

		public int getExp() {
			return exp;
		}

		public AnimationLoop getAnimationLoop() {
			return animLoop;
		}
		
		static Map<Integer, CraftableGem> map = new HashMap<Integer, CraftableGem>();
		static {
			for(CraftableGem c : CraftableGem.values()) {
				map.put(c.getUncut().getId(), c);
			}
		}
	}
}
