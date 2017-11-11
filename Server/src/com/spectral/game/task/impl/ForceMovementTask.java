package com.spectral.game.task.impl;

import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.ForceMovement;
import com.spectral.game.model.Position;
import com.spectral.game.task.Task;

/**
 * A {@link Task} implementation that handles forced movement.
 * An example of forced movement is the Wilderness ditch.
 * @author Professor Oak
 */
public class ForceMovementTask extends Task {

	private Player player;
	private Position end;
	private Position start;
	
	public ForceMovementTask(Player player, int delay, ForceMovement forceM) {
		super(delay, player, (delay == 0 ? true : false));
		this.player = player;
		this.start = forceM.getStart().copy();
		this.end = forceM.getEnd().copy();
		
		//Reset combat
		player.getCombat().reset();
		
		//Reset movement queue
		player.getMovementQueue().reset();

		//Playerupdating
		player.setForceMovement(forceM);
	}
	
	@Override
	protected void execute() {
		int x = start.getX() + end.getX();
		int y = start.getY() + end.getY();
		player.moveTo(new Position(x, y, player.getPosition().getZ()));
		player.setForceMovement(null);
		stop();
	}
}
