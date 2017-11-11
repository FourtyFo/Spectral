package com.spectral.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.spectral.game.GameConstants;
import com.spectral.game.definition.NpcDefinition;
import com.spectral.game.definition.loader.DefinitionLoader;

public class NpcDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		NpcDefinition[] defs = new Gson().fromJson(reader, NpcDefinition[].class);
		for(NpcDefinition def : defs) {
			NpcDefinition.definitions.put(def.getId(), def);
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "npc_defs.json";
	}

}
