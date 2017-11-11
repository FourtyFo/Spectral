package com.spectral.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spectral.game.content.clan.ClanChatManager;
import com.spectral.util.flood.Flooder;

/**
 * The engine which processes the game.
 * 
 * @author Professor Oak
 */
public final class GameEngine implements Runnable {

	/**
	 * The {@link ScheduledExecutorService} which will be used for 
	 * this engine.
	 */
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("GameThread").build());

	/**
	 * Initializes this {@link GameEngine}.
	 */
	public void init() {
		executorService.scheduleAtFixedRate(this, 0, GameConstants.GAME_ENGINE_PROCESSING_CYCLE_RATE, 
				TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run() {
		try {
			World.sequence();
		} catch (Throwable e) {
			e.printStackTrace();
			World.savePlayers();
			ClanChatManager.save();
		}
	}
}