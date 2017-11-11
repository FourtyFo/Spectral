package com.spectral.game.task.impl;

import java.util.Iterator;

import com.spectral.game.World;
import com.spectral.game.entity.impl.grounditem.ItemOnGround;
import com.spectral.game.task.Task;
import com.spectral.game.task.TaskManager;

/**
 * A {@link Task} implementation that handles the
 * processing of all active {@link ItemOnGround}.
 * 
 * @author Professor Oak
 */
public class GroundItemSequenceTask extends Task {

	public GroundItemSequenceTask() {
		super(1);
	}

	@Override
	protected void execute() {
		Iterator<ItemOnGround> iterator = World.getItems().iterator();
		while (iterator.hasNext()) {
			ItemOnGround i = iterator.next();
			
			//Process item..
			i.sequence();
			
			//Check if the item needs to be removed..
			if(i.isPendingRemoval()) {
				
				//If it respawns, make sure we fire off a respawn task before
				//we remove it..
				if(i.respawns()) {
					TaskManager.submit(new GroundItemRespawnTask(i, i.getRespawnTimer()));
				}
				
				//Remove!
				iterator.remove();
			}
		}
	}
}
