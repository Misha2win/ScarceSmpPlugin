package me.misha2win.scracesmpplugin.item.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.item.CreeperMimic;
import me.misha2win.scracesmpplugin.item.EdenApple;
import me.misha2win.scracesmpplugin.item.EnchantingTable;
import me.misha2win.scracesmpplugin.item.GhostBlink;
import me.misha2win.scracesmpplugin.item.GhostRespawn;
import me.misha2win.scracesmpplugin.item.GhostVisibilityToggle;
import me.misha2win.scracesmpplugin.item.PlayerHead;
import me.misha2win.scracesmpplugin.item.UnstableMix;

public class ItemRegistry {

	private static final Map<String, Supplier<ItemStack>> ITEM_BY_TYPE = new HashMap<>();

	public static void registerItems() {
		EdenApple.register();
		CreeperMimic.register();
		GhostVisibilityToggle.register();
		PlayerHead.register();
		GhostBlink.register();
		GhostRespawn.register();
		UnstableMix.register();
		EnchantingTable.register();
	}

	public static void register(String type, Supplier<ItemStack> fatory) {
		ITEM_BY_TYPE.put(type, fatory);
		Bukkit.getLogger().info("Registered item '" + type + "'");
	}

	public static Supplier<ItemStack> get(String type) {
		return ITEM_BY_TYPE.get(type);
	}

	public static List<String> getRegisteredItems() {
		return new ArrayList<>(ITEM_BY_TYPE.keySet());
	}

	public static boolean has(String type) {
		return ITEM_BY_TYPE.containsKey(type);
	}

}
