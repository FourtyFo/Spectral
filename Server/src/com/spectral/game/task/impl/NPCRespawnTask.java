package com.spectral.game.task.impl;

import com.spectral.game.World;
import com.spectral.game.entity.impl.npc.NPC;
import com.spectral.game.task.Task;

/**
 * A {@link Task} implementation which handles the respawn
 * of an npc.
 * 
 * @author Professor Oak
 */
public class NPCRespawnTask extends Task {

	public NPCRespawnTask(NPC npc, int ticks) {
		super(ticks);
		this.npc = npc;
	}

	/**
	 * The {@link NPC} which is going to respawn.
	 */
	private final NPC npc;

	@Override
	public void execute() {
		//Register the new entity..
		World.getAddNPCQueue().add(npc.clone());
		
		//Stop the task
		stop();
	}
}
