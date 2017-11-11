package com.spectral.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.spectral.game.GameConstants;
import com.spectral.game.World;
import com.spectral.game.definition.NpcSpawnDefinition;
import com.spectral.game.definition.loader.DefinitionLoader;
import com.spectral.game.entity.impl.npc.NPC;

public class NpcSpawnDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		NpcSpawnDefinition[] defs = new Gson().fromJson(reader, NpcSpawnDefinition[].class);
		//List<NpcSpawnDefinition> list = new ArrayList<NpcSpawnDefinition>();
		for(NpcSpawnDefinition def : defs) {
			/*if(!list.contains(def)) {
				list.add(def);
			} else {
				System.out.println("Double spawn for npc: "+def.getId()+", pos: "+def.getPosition());
			}*/
			NPC npc = new NPC(def.getId(), def.getPosition());
			npc.getMovementCoordinator().setRadius(def.getRadius());
			npc.setFace(def.getFacing());
			World.getAddNPCQueue().add(npc);
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "npc_spawns.json";
	}
	
}
