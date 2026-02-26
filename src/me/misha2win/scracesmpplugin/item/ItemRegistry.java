package me.misha2win.scracesmpplugin.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
	
	public static String getScarceTypeByItem(ItemStack item) {
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		return ItemUtil.getType(meta);
	}
	
}
