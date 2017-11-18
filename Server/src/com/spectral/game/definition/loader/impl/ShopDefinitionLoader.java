package com.spectral.game.definition.loader.impl;

import java.io.FileReader;

import com.google.gson.Gson;
import com.spectral.game.GameConstants;
import com.spectral.game.definition.ShopDefinition;
import com.spectral.game.definition.loader.DefinitionLoader;
import com.spectral.game.model.container.impl.shop.Shop;
import com.spectral.game.model.container.impl.shop.ShopManager;

public class ShopDefinitionLoader extends DefinitionLoader {

	@Override
	public void load() throws Throwable {
		FileReader reader = new FileReader(file());
		ShopDefinition[] defs = new Gson().fromJson(reader, ShopDefinition[].class);
		for(ShopDefinition def : defs) {
			ShopManager.shops.put(def.getId(), new Shop(def.getId(), def.getName(), def.getOriginalStock()));
		}
		reader.close();
	}

	@Override
	public String file() {
		return GameConstants.DEFINITIONS_DIRECTORY + "shops.json";
	}
}
