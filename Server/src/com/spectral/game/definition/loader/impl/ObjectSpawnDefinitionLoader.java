package com.spectral.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.spectral.game.GameConstants;
import com.spectral.game.definition.ObjectSpawnDefinition;
import com.spectral.game.definition.loader.DefinitionLoader;
import com.spectral.game.entity.impl.object.GameObject;
import com.spectral.game.entity.impl.object.ObjectManager;

public class ObjectSpawnDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		ObjectSpawnDefinition[] defs = new Gson().fromJson(reader, ObjectSpawnDefinition[].class);
		for(ObjectSpawnDefinition def : defs) {
			ObjectManager.register(new GameObject(def.getId(), def.getPosition(), def.getType(), def.getFace()), true);
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "object_spawns.json";
	}	
}