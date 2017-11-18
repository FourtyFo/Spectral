package com.spectral.net.packet.impl;

import com.spectral.game.GameConstants;
import com.spectral.game.content.Food;
import com.spectral.game.content.Gambling;
import com.spectral.game.content.PotionConsumable;
import com.spectral.game.content.PrayerHandler;
import com.spectral.game.content.TeleportTablets;
import com.spectral.game.content.PrayerHandler.PrayerData;
import com.spectral.game.content.combat.CombatSpecial;
import com.spectral.game.content.skill.skillable.impl.Herblore;
import com.spectral.game.content.skill.skillable.impl.Prayer;
import com.spectral.game.content.skill.skillable.impl.Runecrafting;
import com.spectral.game.definition.ItemDefinition;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.BarrowsSet;
import com.spectral.game.model.Position;
import com.spectral.game.model.Locations.Location;
import com.spectral.game.model.dialogue.DialogueManager;
import com.spectral.game.model.dialogue.DialogueOptions;
import com.spectral.game.model.teleportation.TeleportHandler;
import com.spectral.game.model.teleportation.TeleportType;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketConstants;
import com.spectral.net.packet.PacketListener;
import com.spectral.util.Misc;


public class ItemActionPacketListener implements PacketListener {

	private static void firstAction(final Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceId = packet.readUnsignedShort();
		int itemId = packet.readShort();
		int slot = packet.readShort();

		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		
		//Herblore
		if(Herblore.cleanHerb(player, itemId)) {
			return;
		}
		
		//Prayer
		if(Prayer.buryBone(player, itemId)) {
			return;
		}
		
		//Eating food..
		if (Food.consume(player, itemId, slot)) {
			return;
		}
		
		//Drinking potions..
		if (PotionConsumable.drink(player, itemId, slot)) {
			return;
		}
		
		//Runecrafting pouches..
		if(Runecrafting.handlePouch(player, itemId, 1)) {
			return;
		}
		
		//Teleport tablets..
		if(TeleportTablets.init(player, itemId)) {
			return;
		}

		switch(itemId) {
		case Gambling.MITHRIL_SEEDS:
			Gambling.plantFlower(player);
			break;
		case 9520:
			if(player.getLocation() != Location.WILDERNESS) {
				if(player.getSpecialPercentage() < 100) {
					player.getPacketSender().sendInterfaceRemoval();
					player.performAnimation(new Animation(829));
					player.getInventory().delete(9520, 1);
					player.setSpecialPercentage(100);
					CombatSpecial.updateBar(player);
					player.getPacketSender().sendMessage("You now have 100% special attack energy.");
				} else {
					player.getPacketSender().sendMessage("You already have full special attack energy!");
				}
			} else {
				player.getPacketSender().sendMessage("You cannot use this in the Wilderness!");
			}
			break;
		case 8013:
			Position targetLocation = GameConstants.DEFAULT_POSITION.copy().add(Misc.getRandom(1), Misc.getRandom(4));
			if(TeleportHandler.checkReqs(player, targetLocation)) {
				TeleportHandler.teleport(player, targetLocation, TeleportType.TELE_TAB);
				player.getInventory().delete(8013, 1);
			}
			break;
		case 2542:
		case 2543:
		case 2544:
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot do that right now.");
				return;
			}
			if(itemId == 2542 && player.isPreserveUnlocked() || itemId == 2543 && player.isRigourUnlocked() || 
					itemId == 2544 && player.isAuguryUnlocked()) {
				player.getPacketSender().sendMessage("You have already unlocked that prayer.");
				return;
			}
			DialogueManager.start(player, 9);
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					if(option == 1) {
						player.getInventory().delete(itemId, 1);

						if(itemId == 2542)
							player.setPreserveUnlocked(true);
						else if(itemId == 2543)
							player.setRigourUnlocked(true);
						else if(itemId == 2544)
							player.setAuguryUnlocked(true);
						player.getPacketSender().sendConfig(709, PrayerHandler.canUse(player, PrayerData.PRESERVE, false) ? 1 : 0);
						player.getPacketSender().sendConfig(711, PrayerHandler.canUse(player, PrayerData.RIGOUR, false) ? 1 : 0);
						player.getPacketSender().sendConfig(713, PrayerHandler.canUse(player, PrayerData.AUGURY, false) ? 1 : 0);
						player.getPacketSender().sendMessage("You have unlocked a new prayer.");
					}
					player.getPacketSender().sendInterfaceRemoval();
				}
			});
			break;
		case 2545:
			if(player.busy()) {
				player.getPacketSender().sendMessage("You cannot do that right now.");
				return;
			}
			if(player.isTargetTeleportUnlocked()) {
				player.getPacketSender().sendMessage("You have already unlocked that teleport.");
				return;
			}
			DialogueManager.start(player, 12);
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					if(option == 1) {
						player.getInventory().delete(itemId, 1);
						player.setTargetTeleportUnlocked(true);
						player.getPacketSender().sendMessage("You have unlocked a new teleport.");
					}
					player.getPacketSender().sendInterfaceRemoval();
				}
			});
			break;
		case 12873:
		case 12875:
		case 12879:
		case 12881:
		case 12883:
		case 12877:
			BarrowsSet set = BarrowsSet.get(itemId);
			if(set != null) {
				if(!player.getInventory().contains(set.getSetId())) {
					return;
				}
				if((player.getInventory().getFreeSlots() - 1) < set.getItems().length) {
					player.getPacketSender().sendMessage("You need at least "+set.getItems().length+" free inventory slots to do that.");
					return;
				}
				player.getInventory().delete(set.getSetId(), 1);
				for(int item : set.getItems()) {
					player.getInventory().add(item, 1);
				}
				player.getPacketSender().sendMessage("You've opened your "+ItemDefinition.forId(itemId).getName()+".");
			}
			break;
		}
	}

	public static void secondAction(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		
		if(Runecrafting.handleTalisman(player, itemId)) {
			return;
		}
		if(Runecrafting.handlePouch(player, itemId, 2)) {
			return;
		}
		
		switch(itemId) {
		case 2550:
			player.setDialogueOptions(new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					player.getPacketSender().sendInterfaceRemoval();
					if(option == 1) {
						if(player.getInventory().contains(2550)) {
							player.getInventory().delete(2550, 1);
							player.setRecoilDamage(0);
							player.getPacketSender().sendMessage("Your Ring of recoil has degraded.");
						}
					}
				}
			});
			player.setDialogue(DialogueManager.getDialogues().get(10)); //Yes / no option
			DialogueManager.sendStatement(player, "You still have "+(40 - player.getRecoilDamage())+" damage before it breaks. Continue?");
			break;
		}
	}

	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		@SuppressWarnings("unused")
		int interfaceId = packet.readLEShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;

		if(BarrowsSet.pack(player, itemId)) {
			return;
		}
		if(Runecrafting.handlePouch(player, itemId, 3)) {
			return;
		}

		switch(itemId) {
		case 12926:
			player.getPacketSender().sendMessage("Your Toxic blowpipe has "+player.getBlowpipeScales()+" Zulrah scales left.");
			break;
		}
	}

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player == null || player.getHitpoints() <= 0)
			return;
		switch (packet.getOpcode()) {
		case PacketConstants.SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case PacketConstants.FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case PacketConstants.THIRD_ITEM_ACTION_OPCODE:
			thirdClickAction(player, packet);
			break;
		}
	}
}