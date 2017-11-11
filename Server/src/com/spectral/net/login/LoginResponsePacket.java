package com.spectral.net.login;

import com.spectral.game.model.PlayerRights;

/**
 * The packet that contains information about a players login attempt.
 * 
 * @author Vult-R
 */
public final class LoginResponsePacket {

	/**
	 * The login response that was indicated.
	 */
	private final int response;

	/**
	 * The rights of the player logging in.
	 */
	private final PlayerRights rights;

	/**
	 * Creates a new {@link LoginResponsePacket}.
	 * 
	 * @param response The response that was indicated.
	 * 
	 * @param rights The rights of the player logging in.
	 * 
	 * @param flagged The flag that indicates a player was flagged.
	 */
	public LoginResponsePacket(int response, PlayerRights rights) {
		this.response = response;
		this.rights = rights;
	}

	public LoginResponsePacket(int response) {
		this.response = response;
		this.rights = PlayerRights.PLAYER;
	}

	public int getResponse() {
		return response;
	}

	public PlayerRights getRights() {
		return rights;
	}
}

