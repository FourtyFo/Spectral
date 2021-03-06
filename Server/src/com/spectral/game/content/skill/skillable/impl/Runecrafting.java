package com.spectral.game.content.skill.skillable.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.spectral.game.content.PetHandler;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.Graphic;
import com.spectral.game.model.Position;
import com.spectral.game.model.Skill;
import com.spectral.game.model.teleportation.TeleportHandler;
import com.spectral.util.ItemIdentifiers;
import com.spectral.util.Misc;

/**
 * Handles the Runecrafting skill ingame.
 * It currently consists of clicking on altars to craft runes.
 * 
 * It has support for:
 * Talismans, right clicking on them to get teleported to the altars. 
 * Pouches, can be used to store essence.
 * 
 * @author Professor Oak
 *
 */
public class Runecrafting {
	
	/**
	 * The {@link Graphic} which will be used when the player is crafting runes.
	 */
	private static final Graphic CRAFT_RUNES_GRAPHIC = new Graphic(186);
	
	/**
	 * The {@link Animation} which will be used when the player is crafting runes.
	 */
	private static final Animation CRAFT_RUNES_ANIMATION = new Animation(791);

	/**
	 * Attempts to craft runes when clicking an object ingame.
	 * @param player
	 * @param objectId
	 * @return
	 */
	public static boolean initialize(Player player, int objectId) {
		Optional<Rune> rune = Rune.forId(objectId);
		if(rune.isPresent()) {
			if(player.getSkillManager().getCurrentLevel(Skill.RUNECRAFTING) < rune.get().getLevelRequirement()) {
				player.getPacketSender().sendMessage("You need a Runecrafting level of at least " +rune.get().getLevelRequirement() + " to craft this.");
				return false;
			}
			int essence;
			if(rune.get().isPureRequired()) {
				if(!player.getInventory().contains(ItemIdentifiers.PURE_ESSENCE)) {
					player.getPacketSender().sendMessage("You need Pure essence to craft runes using this altar.");
					return true;
				}
				essence = ItemIdentifiers.PURE_ESSENCE;
			} else {
				if(player.getInventory().contains(ItemIdentifiers.RUNE_ESSENCE)) {
					essence = ItemIdentifiers.RUNE_ESSENCE;
				} else if(player.getInventory().contains(ItemIdentifiers.PURE_ESSENCE)) {
					essence = ItemIdentifiers.PURE_ESSENCE;
				} else {
					player.getPacketSender().sendMessage("You don't have any essence in your inventory.");
					return true;
				}
			}
			player.performGraphic(CRAFT_RUNES_GRAPHIC);
			player.performAnimation(CRAFT_RUNES_ANIMATION);			
			int craftAmount = craftAmount(rune.get(), player);
			int xpGain = 0;
			for(int i = 0; i < 28; i++) {
				if(!player.getInventory().contains(essence)) {
					break;
				}
				player.getInventory().delete(essence, 1);
				player.getInventory().add(rune.get().getRuneID(), craftAmount);
				xpGain += rune.get().getXP();
			}
			
			//Finally add the total experience they gained..
			player.getSkillManager().addExperience(Skill.RUNECRAFTING, xpGain);
			
			//Pets..
			PetHandler.onSkill(player, Skill.RUNECRAFTING);
		}
		return false;
	}
	
	/**
	 * Attempts to handle the teleport for a {@link Talisman} ingame.
	 * The talismans teleport players to the Runecrafting altars.
	 * 
	 * @param player
	 * @param itemId
	 * @return
	 */
	public static boolean handleTalisman(Player player, int itemId) {
		Optional<Talisman> talisman = Talisman.forId(itemId);
		if(talisman.isPresent()) {
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < talisman.get().getLevelRequirement()) {
				player.getPacketSender().sendMessage("You need a Runecrafting level of at least " +talisman.get().getLevelRequirement()+ " to use this Talisman's teleport function.");
			} else {
				TeleportHandler.teleport(player, talisman.get().getPosition(), player.getSpellbook().getTeleportType());
			}			
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to handle a Runecrafting {@link Pouch}.
	 * The pouch allows players to store essence.
	 * 
	 * @param player
	 * @param itemId
	 * @param actionType
	 * @return
	 */
	public static boolean handlePouch(Player player, int itemId, int actionType) {
		Optional<Pouch> pouch = Pouch.forItemId(itemId);
		if(pouch.isPresent()) {
			//PouchContainer container = player.getPouchContainers()[pouch.get().ordinal()];
			Optional<PouchContainer> container = Optional.empty();
			for(PouchContainer pC : player.getPouches()) {
				if(pC.getPouch() == pouch.get()) {
					container = Optional.of(pC);
					break;
				}
			}
			if(container.isPresent()) {
				switch(actionType) {
				case 1:
					container.get().store(player);
					break;
				case 2:
					container.get().check(player);
					break;
				case 3:
					container.get().withdraw(player);
					break;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Represents a rune which can be crafted from essence using 
	 * the Runecrafting skill.
	 * @author Professor Oak
	 */
	public enum Rune {
		AIR_RUNE(556, 1, 5, 14897, false),
		MIND_RUNE(558, 2, 6, 14898, false),
		WATER_RUNE(555, 5, 7, 14899, false),
		EARTH_RUNE(557, 9, 8, 14900, false),
		FIRE_RUNE(554, 14, 9, 14901, false),
		BODY_RUNE(559, 20, 10, 14902, false),
		COSMIC_RUNE(564, 27, 11, 14903, true),
		CHAOS_RUNE(562, 35, 12, 14906, true),
		ASTRAL_RUNE(9075, 40, 13, 14911, true),
		NATURE_RUNE(561, 44, 14, 14905, true),
		LAW_RUNE(563, 54, 15, 14904, true),
		DEATH_RUNE(560, 65, 16, 14907, true),
		BLOOD_RUNE(565, 75, 27, 27978, true);

		Rune(int rune, int levelReq, int xpReward, int altarObjectID, boolean pureRequired) {
			this.runeID = rune;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.objectId = altarObjectID;
			this.pureRequired = pureRequired;
		}

		private int runeID;
		private int levelReq;
		private int xpReward;
		private int objectId;
		private boolean pureRequired;

		public int getRuneID() {
			return runeID;
		}

		public int getLevelRequirement() {
			return levelReq;
		}

		public int getXP() {
			return xpReward;
		}

		public int getObjectId() {
			return objectId;
		}

		public boolean isPureRequired() {
			return pureRequired;
		}

		private static final Map<Integer, Rune> runes = new HashMap<Integer, Rune>();

		static {
			for(Rune rune : Rune.values()) {
				runes.put(rune.getObjectId(), rune);
			}
		}

		public static Optional<Rune> forId(int objectId) {
			Rune rune = runes.get(objectId);
			if(rune != null) {
				return Optional.of(rune);
			}
			return Optional.empty();
		}
	}

	/**
	 * Represents a talisman which can be used to
	 * teleport to the Runecrafting altars.
	 * @author Professor Oak
	 */
	public enum Talisman {
		AIR_TALISMAN(1438, 1, new Position(2841, 4828)),
		MIND_TALISMAN(1448, 2, new Position(2793, 4827)),
		WATER_TALISMAN(1444, 5, new Position(2720, 4831)),
		EARTH_TALISMAN(1440, 9, new Position(2655, 4829)),
		FIRE_TALISMAN(1442, 14, new Position(2576, 4846)),
		BODY_TALISMAN(1446, 20, new Position(2522, 4833)),
		COSMIC_TALISMAN(1454, 27, new Position(2163, 4833)),
		CHAOS_TALISMAN(1452, 35, new Position(2282, 4837)),
		NATURE_TALISMAN(1462, 44, new Position(2400, 4834)),
		LAW_TALISMAN(1458, 54, new Position(2464, 4817)),
		DEATH_TALISMAN(1456, 65, new Position(2208, 4829)),
		BLOOD_TALISMAN(1450, 77, new Position(1722, 3826));

		Talisman(int talismanId, int levelReq, Position location) {
			this.talismanId = talismanId;
			this.levelReq = levelReq;
			this.location = location;
		}

		private int talismanId;
		private int levelReq;
		private Position location;

		public int getItemId() {
			return talismanId;
		}

		public int getLevelRequirement() {
			return levelReq;
		}

		public Position getPosition() {
			return location.copy();
		}

		private static final Map<Integer, Talisman> talismans = new HashMap<Integer, Talisman>();

		static {
			for(Talisman t : Talisman.values()) {
				talismans.put(t.getItemId(), t);
			}
		}

		public static Optional<Talisman> forId(int itemId) {
			Talisman talisman = talismans.get(itemId);
			if(talisman != null) {
				return Optional.of(talisman);
			}
			return Optional.empty();
		}
	}
	
	/**
	 * Represents a container for a {@link Pouch}.
	 * @author Professor Oak
	 */
	public static class PouchContainer {
		/**
		 * The pouch which belongs
		 * to this container.
		 */
		private final Pouch pouch;
		
		/**
		 * The amount of regular Rune essence
		 * stored in this container.
		 */
		private int runeEssenceAmt;
		
		/**
		 * The amount of Pure essence
		 * stored in this container.
		 */
		private int pureEssenceAmt;
		
		/**
		 * Creates this container.
		 * @param player
		 * @param pouch
		 */
		public PouchContainer(Pouch pouch) {
			this.pouch = pouch;
		}
		
		/**
		 * Attempts to store essence into the container.
		 */
		public void store(Player player) {
			if(getStoredAmount() >= pouch.getCapacity()) {
				player.getPacketSender().sendMessage("Your pouch is already full.");
				return;
			}
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < pouch.getRequiredLevel()) {
				player.getPacketSender().sendMessage("You need a Runecrafting level of at least "+Integer.toString(pouch.getRequiredLevel())+" to use this.");
				return;
			}
			for(int i = getStoredAmount(); i < pouch.getCapacity(); i++) {
				if(player.getInventory().contains(ItemIdentifiers.PURE_ESSENCE)) {
					player.getInventory().delete(ItemIdentifiers.PURE_ESSENCE, 1);
					pureEssenceAmt++;
				} else if(player.getInventory().contains(ItemIdentifiers.RUNE_ESSENCE)) {
					player.getInventory().delete(ItemIdentifiers.RUNE_ESSENCE, 1);
					runeEssenceAmt++;
				} else {
					player.getPacketSender().sendMessage("You don't have any more essence to store.");
					break;
				}
			}
		}
		
		/**
		 * Attempts to withdraw essence from the container.
		 */
		public void withdraw(Player player) {
			int total = getStoredAmount();
			if(total == 0) {
				player.getPacketSender().sendMessage("Your pouch is already empty.");
				return;
			}
			for(int i = 0; i < total; i++) {
				if(player.getInventory().isFull()) {
					player.getInventory().full();
					break;
				}
				if(pureEssenceAmt > 0) {
					player.getInventory().add(ItemIdentifiers.PURE_ESSENCE, 1);
					pureEssenceAmt--;
				} else if(runeEssenceAmt > 0) {
					player.getInventory().add(ItemIdentifiers.RUNE_ESSENCE, 1);
					runeEssenceAmt--;
				} else {
					player.getPacketSender().sendMessage("You don't have any more essence to withdraw.");
					break;
				}
			}
		}
		
		/**
		 * Checks the amount of essence in the container.
		 */
		public void check(Player player) {
			player.getPacketSender().sendMessage("Your "+Misc.capitalize(pouch.toString().toLowerCase().replace("_", " "))+" contains "+Integer.toString(runeEssenceAmt)+" Rune essence and "+Integer.toString(pureEssenceAmt)+" Pure essence.");
		}
		
		/**
		 * Returns the total amount of stored essence.
		 * @return
		 */
		public int getStoredAmount() {
			return runeEssenceAmt + pureEssenceAmt;
		}
		
		/**
		 * Returns the pouch.
		 * @return
		 */
		public Pouch getPouch() {
			return pouch;
		}
	}
	
	/**
	 * Represents a pouch used for the Runecrafting
	 * skill to hold essence for the player.
	 * @author Professor Oak
	 */
	public enum Pouch {
		SMALL_POUCH(5509, 1, 3, -1),
		MEDIUM_POUCH(5510, 25, 6, 45),
		LARGE_POUCH(5512, 50, 9, 29),
		GIANT_POUCH(5514, 75, 12, 10),
		;
		
		Pouch(int itemId, int requiredLevel, int capacity, int decayChance) {
			this.itemId = itemId;
			this.requiredLevel = requiredLevel;
			this.capacity = capacity;
			this.decayChance = decayChance;
		}

		/**
		 * The pouch's item identifier.
		 */
		private final int itemId;
		
		/**
		 * The level required to use this pouch.
		 */
		private final int requiredLevel;
		
		/**
		 * The pouch's capacity.
		 */
		private final int capacity;
		
		/**
		 * The pouch's decay chance.
		 */
		private final int decayChance;
		
		public int getItemId() {
			return itemId;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public int getCapacity() {
			return capacity;
		}
		
		public int getDecayChance() {
			return decayChance;
		}
		
		private static final Map<Integer, Pouch> pouches = new HashMap<Integer, Pouch>();
		static {
			for(Pouch p : Pouch.values()) {
				pouches.put(p.getItemId(), p);
			}
		}
		
		public static Optional<Pouch> forItemId(int itemId) {
			Pouch pouch = pouches.get(itemId);
			if(pouch != null) {
				return Optional.of(pouch);
			}
			return Optional.empty();
		}
	}
		
	/**
	 * Gets the amount of runes a player can craft based
	 * on their level in the Runecrafting skill.
	 * {@author Ruse} - Credits to Ruse for this method.
	 * 
	 * @param rune
	 * @param player
	 * @return
	 */
	private static int craftAmount(Rune rune, Player player) {
		int amount = 1;
		switch(rune) {
		case AIR_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 11)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 22)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 33)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 44)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 55)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 66)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 77)
				amount = 8;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 88)
				amount = 9;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 99)
				amount = 10;
			break;
		case ASTRAL_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 82)
				amount = 2;
			break;
		case BLOOD_RUNE:
			break;
		case BODY_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 46)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 92)
				amount = 3;
			break;
		case CHAOS_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 74)
				amount = 2;
			break;
		case COSMIC_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 59)
				amount = 2;
			break;
		case DEATH_RUNE:
			break;
		case EARTH_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 26)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 52)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 78)
				amount = 4;
			break;
		case FIRE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 35)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 3;
			break;
		case LAW_RUNE:
			break;
		case MIND_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 14)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 28)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 42)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 56)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 70)
				amount = 6;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 84)
				amount = 7;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 98)
				amount = 8;
			break;
		case NATURE_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 91)
				amount = 2;
			break;
		case WATER_RUNE:
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 19)
				amount = 2;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 38)
				amount = 3;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 57)
				amount = 4;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 76)
				amount = 5;
			if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 95)
				amount = 6;
			break;
		default:
			break;
		}
		return amount;
	}
}
