package com.spectral.game.entity.impl.npc;

import com.spectral.game.collision.RegionManager;
import com.spectral.game.content.combat.CombatFactory;
import com.spectral.game.model.Position;
import com.spectral.game.model.movement.path.RS317PathFinder;
import com.spectral.util.Misc;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 * 
 * @author lare96
 */
public class NPCMovementCoordinator {

	/** The npc we are coordinating movement for. */
	private NPC npc;

	/** The coordinate state this npc is in. */
	private CoordinateState coordinateState;
	
	/** The total radius a npc can move from spawn location before retreating **/
	private int radius;

	public enum CoordinateState {
		HOME,
		AWAY,
		RETREATING;
	}

	public NPCMovementCoordinator(NPC npc) {
		this.npc = npc;
		this.coordinateState = CoordinateState.HOME;
	}

	public void sequence() {

		//If walk radius is 0, that means the npc shouldn't walk around.
		//HOWEVER: Only if npc is home. Because the npc might be retreating 
		//from a fight.
		if(radius == 0) {
			if (coordinateState == CoordinateState.HOME) {
				return;
			}
		}

		updateCoordinator();

		switch (coordinateState) {
		case HOME:

			if(CombatFactory.inCombat(npc)) {
				return;
			}

			if (npc.getMovementQueue().isMovementDone()) {
				if (Misc.getRandom(10) <= 1) {
					Position pos = generateLocalPosition();
					if(pos != null) {
						npc.getMovementQueue().walkStep(pos.getX(), pos.getY());
					}
				}
			}

			break;
		case RETREATING:
		case AWAY:
			RS317PathFinder.findPath(npc, npc.getSpawnPosition().getX(), npc.getSpawnPosition().getY(), true, 1, 1);
			break;
		}
	}

	public void updateCoordinator() {

		/**
		 * Handle retreating from combat.
		 */

		if(CombatFactory.inCombat(npc)) {
			if(coordinateState == CoordinateState.AWAY) {
				coordinateState = CoordinateState.RETREATING;
			}
			if(coordinateState == CoordinateState.RETREATING) {
				if(npc.getPosition().equals(npc.getSpawnPosition())) {
					coordinateState = CoordinateState.HOME;
				}
				npc.getCombat().reset();
			}
			return;
		}

		int deltaX;
		int deltaY;

		if(npc.getSpawnPosition().getX() > npc.getPosition().getX()) {
			deltaX = npc.getSpawnPosition().getX() - npc.getPosition().getX();
		} else {
			deltaX = npc.getPosition().getX() - npc.getSpawnPosition().getX();
		}

		if(npc.getSpawnPosition().getY() > npc.getPosition().getY()) {
			deltaY = npc.getSpawnPosition().getY() - npc.getPosition().getY();
		} else {
			deltaY = npc.getPosition().getY() - npc.getSpawnPosition().getY();
		}

		if((deltaX > radius) || (deltaY > radius)) {
			coordinateState = CoordinateState.AWAY;
		} else { 
			coordinateState = CoordinateState.HOME;
		}
	}

	private Position generateLocalPosition() {
		int dir = -1;
		int x = 0, y = 0;
		if (!RegionManager.blockedNorth(npc.getPosition()))
		{
			dir = 0;
		}
		else if (!RegionManager.blockedEast(npc.getPosition()))
		{
			dir = 4;
		}
		else if (!RegionManager.blockedSouth(npc.getPosition()))
		{
			dir = 8;
		}
		else if (!RegionManager.blockedWest(npc.getPosition()))
		{
			dir = 12;
		}
		int random = Misc.getRandom(3);

		boolean found = false;

		if (random == 0)
		{
			if (!RegionManager.blockedNorth(npc.getPosition()))
			{
				y = 1;
				found = true;
			}
		}
		else if (random == 1)
		{
			if (!RegionManager.blockedEast(npc.getPosition()))
			{
				x = 1;
				found = true;
			}
		}
		else if (random == 2)
		{
			if (!RegionManager.blockedSouth(npc.getPosition()))
			{
				y = -1;
				found = true;
			}
		}
		else if (random == 3)
		{
			if (!RegionManager.blockedWest(npc.getPosition()))
			{
				x = -1;
				found = true;
			}
		}
		if (!found)
		{
			if (dir == 0)
			{
				y = 1;
			}
			else if (dir == 4)
			{
				x = 1;
			}
			else if (dir == 8)
			{
				y = -1;
			}
			else if (dir == 12)
			{
				x = -1;
			}
		}
		if(x == 0 && y == 0)
			return null;
		int spawnX = npc.getSpawnPosition().getX();
		int spawnY = npc.getSpawnPosition().getY();
		if (x == 1) {
			if (npc.getPosition().getX() + x > spawnX + 1)
				return null;
		}
		if (x == -1) {
			if (npc.getPosition().getX() - x < spawnX - 1)
				return null;
		}
		if (y == 1) {
			if (npc.getPosition().getY() + y > spawnY + 1)
				return null;
		}
		if (y == -1) {
			if (npc.getPosition().getY() - y < spawnY - 1)
				return null;
		}
		return new Position(x, y);
	}

	public void setCoordinateState(CoordinateState coordinateState) {
		this.coordinateState = coordinateState;
	}

	public CoordinateState getCoordinateState() {
		return coordinateState;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}