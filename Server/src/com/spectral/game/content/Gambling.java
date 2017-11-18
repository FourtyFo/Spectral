package com.spectral.game.content;

import java.util.Optional;

import com.spectral.game.entity.impl.npc.NPC;
import com.spectral.game.entity.impl.object.GameObject;
import com.spectral.game.entity.impl.object.ObjectManager;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.movement.MovementQueue;
import com.spectral.game.task.TaskManager;
import com.spectral.game.task.impl.TimedObjectSpawnTask;
import com.spectral.util.Misc;

public class Gambling {
	
	/**
	 * The item id of mithril seeds.
	 * Used for planting flowers.
	 */
	public static final int MITHRIL_SEEDS = 299;

	public enum FlowersData {
		PASTEL_FLOWERS(2980, 2460),
		RED_FLOWERS(2981, 2462),
		BLUE_FLOWERS(2982, 2464),
		YELLOW_FLOWERS(2983, 2466),
		PURPLE_FLOWERS(2984, 2468),
		ORANGE_FLOWERS(2985, 2470),
		RAINBOW_FLOWERS(2986, 2472),

		WHITE_FLOWERS(2987, 2474),
		BLACK_FLOWERS(2988, 2476);
		FlowersData(int objectId, int itemId) {
			this.objectId = objectId;
			this.itemId = itemId;
		}

		public int objectId;
		public int itemId;
		
		public static FlowersData forObject(int object) {
			for(FlowersData data : FlowersData.values()) {
				if(data.objectId == object)
					return data;
			}
			return null;
		}
		
		public static FlowersData generate() {
			double RANDOM = (java.lang.Math.random() * 100);
			if(RANDOM >= 1) {
				return values()[Misc.getRandom(6)];
			} else {
				return Misc.getRandom(3) == 1 ? WHITE_FLOWERS : BLACK_FLOWERS;
			}
		}
	}
	
	/**
	 * Plants flowers using mithril seeds.
	 * @param player
	 */
	public static void plantFlower(Player player) {
		if(!player.getClickDelay().elapsed(3000)) {
			return;
		}
		for(NPC npc : player.getLocalNpcs()) {
			if(npc != null && npc.getPosition().equals(player.getPosition())) {
				player.getPacketSender().sendMessage("You cannot plant a seed right here.");
				return;
			}
		}
		if(ObjectManager.exists(player.getPosition())) {
			player.getPacketSender().sendMessage("You cannot plant a seed right here.");
			return;
		}	
		final FlowersData flowers = FlowersData.generate();
		final GameObject flowerObject = new GameObject(flowers.objectId, player.getPosition().copy());
		
		//Stop skilling..
		player.getSkillManager().stopSkillable();
		
		player.getMovementQueue().reset();
		player.getInventory().delete(MITHRIL_SEEDS, 1);
		player.performAnimation(new Animation(827));
		player.getPacketSender().sendMessage("You plant the seed and suddenly some flowers appear..");
		MovementQueue.clippedStep(player);
		//Start a task which will spawn and then delete them after a period of time.
		TaskManager.submit(new TimedObjectSpawnTask(flowerObject, 60, Optional.empty()));
		player.setPositionToFace(flowerObject.getPosition());
		player.getClickDelay().reset();
	}	
}
