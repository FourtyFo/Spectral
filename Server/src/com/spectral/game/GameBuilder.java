package com.spectral.game;

import java.util.ArrayDeque;
import java.util.Queue;

import com.spectral.game.collision.RegionManager;
import com.spectral.game.content.clan.ClanChatManager;
import com.spectral.game.definition.loader.impl.DialogueDefinitionLoader;
import com.spectral.game.definition.loader.impl.ItemDefinitionLoader;
import com.spectral.game.definition.loader.impl.NpcDefinitionLoader;
import com.spectral.game.definition.loader.impl.NpcDropDefinitionLoader;
import com.spectral.game.definition.loader.impl.NpcSpawnDefinitionLoader;
import com.spectral.game.definition.loader.impl.ObjectSpawnDefinitionLoader;
import com.spectral.game.definition.loader.impl.ShopDefinitionLoader;
import com.spectral.game.definition.loader.impl.TestLoader;
import com.spectral.game.task.TaskManager;
import com.spectral.game.task.impl.GroundItemSequenceTask;
import com.spectral.game.task.impl.CombatPoisonEffect.CombatPoisonData;
import com.spectral.util.BackgroundLoader;
import com.spectral.util.PlayerPunishment;

/**
 * Loads all required necessities and starts processes required
 * for the game to work.
 * 
 * @author Lare96
 */
public class GameBuilder {

	/**
	 * The background loader that will load various utilities in the background
	 * while the bootstrap is preparing the server.
	 */
	private final BackgroundLoader backgroundLoader = new BackgroundLoader();

	/**
	 * Initializes this game builder effectively preparing the background
	 * startup tasks and game processing.
	 *
	 * @throws Exception
	 *             if any issues occur while starting the network.
	 */
	public void initialize() throws Exception {
		//Start prioritized tasks...
		RegionManager.init();
		
		//Start background tasks..
		backgroundLoader.init(createBackgroundTasks());

		//Start global tasks..
		TaskManager.submit(new GroundItemSequenceTask());
		
		//Start game engine..
		new GameEngine().init();

		//Make sure the background tasks loaded properly..
		if (!backgroundLoader.awaitCompletion())
			throw new IllegalStateException("Background load did not complete normally!");
	}

	/**
	 * Returns a queue containing all of the background tasks that will be
	 * executed by the background loader. Please note that the loader may use
	 * multiple threads to load the utilities concurrently, so utilities that
	 * depend on each other <b>must</b> be executed in the same task to ensure
	 * thread safety.
	 *
	 * @return the queue of background tasks.
	 */
	public Queue<Runnable> createBackgroundTasks() {
		Queue<Runnable> tasks = new ArrayDeque<>();
		tasks.add(ClanChatManager::init);
		tasks.add(CombatPoisonData::init);
		tasks.add(PlayerPunishment::init);
		
		//Load definitions..
		tasks.add(new ObjectSpawnDefinitionLoader());
		tasks.add(new ItemDefinitionLoader());
		tasks.add(new ShopDefinitionLoader());
		tasks.add(new DialogueDefinitionLoader());
		tasks.add(new NpcDefinitionLoader());
		tasks.add(new NpcDropDefinitionLoader());
		tasks.add(new NpcSpawnDefinitionLoader());
		
		/*tasks.add(new NpcDefinitionLoader());*/
		
		return tasks;
	}
}
