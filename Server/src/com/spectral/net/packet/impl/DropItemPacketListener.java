package com.spectral.net.packet.impl;

import com.spectral.game.content.PetHandler;
import com.spectral.game.content.skill.skillable.Skillable;
import com.spectral.game.entity.impl.grounditem.ItemOnGroundManager;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Item;
import com.spectral.game.model.container.impl.Inventory;
import com.spectral.net.packet.Packet;
import com.spectral.net.packet.PacketListener;

/**
 * This packet listener is called when a player drops an item they
 * have placed in their inventory.
 * 
 * @author relex lawl
 */

public class DropItemPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int id = packet.readUnsignedShortA();
		int interface_id = packet.readUnsignedShort();
		int itemSlot = packet.readUnsignedShortA();
		
		if(player == null || player.getHitpoints() <= 0) {
			return;
		}
		
		if(interface_id != Inventory.INTERFACE_ID) {
			return;
		}
		
		if (player.getHitpoints() <= 0 || player.getInterfaceId() > 0)
			return;
		if(itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;

		if(player.busy()) {
			player.getPacketSender().sendMessage("You cannot do this right now.");
			return;
		}

		Item item = player.getInventory().getItems()[itemSlot];
		if(item == null)
			return;
		if(item.getId() != id || item.getAmount() <= 0) {
			return;
		}
		
		player.getPacketSender().sendInterfaceRemoval();
		
		//Stop skilling..
		player.getSkillManager().stopSkillable();
		
		//Check if we're dropping a pet..
		if(PetHandler.drop(player, id, false)) {
			return;
		}
		
		if(item.getDefinition().isDropable()) {
			ItemOnGroundManager.register(player, item);
			player.getInventory().setItem(itemSlot, new Item(-1, 0)).refreshItems();
		} else {
			destroyItemInterface(player, item);
		}
	}

	public static void destroyItemInterface(Player player, Item item) {//Destroy item created by Remco
		player.setDestroyItem(item.getId());
		String[][] info = {//The info the dialogue gives
				{ "Are you sure you want to discard this item?", "14174" },
				{ "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" },
				{"This item will vanish once it hits the floor.", "14182" }, {"You cannot get it back if discarded.", "14183" },
				{ item.getDefinition().getName(), "14184" } };
		player.getPacketSender().sendItemOnInterface(14171, item.getId(), 0, item.getAmount());
		for (int i = 0; i < info.length; i++)
			player.getPacketSender().sendString(Integer.parseInt(info[i][1]), info[i][0]);
		player.getPacketSender().sendChatboxInterface(14170);
	}
}
