package com.spectral.game.task.impl;

import java.util.Optional;

import com.spectral.game.World;
import com.spectral.game.entity.impl.npc.NPC;
import com.spectral.game.entity.impl.npc.NPCDropGenerator;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.movement.MovementStatus;
import com.spectral.game.task.Task;
import com.spectral.game.task.TaskManager;

/**
 * Represents an npc's death task, which handles everything
 * an npc does before and after their death animation (including it), 
 * such as dropping their drop table items.
 * 
 * @author relex lawl
 */

public class NPCDeathTask extends Task {

	/**
	 * The NPCDeathTask constructor.
	 * @param npc	The npc being killed.
	 */
	public NPCDeathTask(NPC npc) {
		super(2);
		this.npc = npc;
		this.ticks = 2;
	}

	/**
	 * The npc setting off the death task.
	 */
	private final NPC npc;

	/**
	 * The amount of ticks on the task.
	 */
	private int ticks = 1;

	/**
	 * The player who killed the NPC
	 */
	private Optional<Player> killer = Optional.empty();

	@Override
	public void execute() {
		try {

			switch (ticks) {
			case 1:
				//Reset and disable movement queue.. 
				npc.getMovementQueue().setMovementStatus(MovementStatus.DISABLED).reset();

				//Get the {@link Player} who killed us..
				killer = npc.getCombat().getKiller(true);

				//Start death animation..
				npc.performAnimation(new Animation(npc.getDefinition().getDeathAnim()));

				//Reset combat..
				npc.getCombat().reset();

				//Reset interacting entity..
				npc.setEntityInteraction(null);
				break;
			case 0:
				if(killer.isPresent()) {
					Player player = killer.get();
					
					//Drop kills for the player..
					NPCDropGenerator.start(player, npc);
				}
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			e.printStackTrace();
			stop();
		}
	}

	@Override
	public void stop() {
		//Stop event.
		setEventRunning(false);

		//Flag that we are no longer dying.
		npc.setDying(false);

		//Handle respawn..
		if(npc.getDefinition().getRespawn() > 0) {
			TaskManager.submit(new NPCRespawnTask(npc, npc.getDefinition().getRespawn()));
		}

		//Add us to the global remove list.
		World.getRemoveNPCQueue().add(npc);
	}
}