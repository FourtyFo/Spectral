package com.spectral.game.content.clan;

import com.spectral.game.model.SecondsTimer;

public class BannedMember {

	private SecondsTimer timer;
	private String name;
	
	public BannedMember(String name, int seconds) {
		this.setName(name);
		this.setTimer(new SecondsTimer(seconds));
	}

	public SecondsTimer getTimer() {
		return timer;
	}

	public void setTimer(SecondsTimer timer) {
		this.timer = timer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
