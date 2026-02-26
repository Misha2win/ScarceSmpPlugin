package me.misha2win.scracesmpplugin.item.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

public class ItemRecipeRegistry {

	private static final Map<String, Recipe> RECIPE_BY_TYPE = new HashMap<>();

	public static void registerAll() {
		for (String key : RECIPE_BY_TYPE.keySet()) {
			Bukkit.addRecipe(RECIPE_BY_TYPE.get(key));
		}
	}

	public static void register(String type, Recipe recipe) {
		RECIPE_BY_TYPE.put(type, recipe);
		Bukkit.getLogger().info("Registered recipe for '" + type + "'");
	}

	public static Recipe get(String type) {
		return RECIPE_BY_TYPE.get(type);
	}

	public static List<String> getRegisteredRecipes() {
		return new ArrayList<>(RECIPE_BY_TYPE.keySet());
	}

}
