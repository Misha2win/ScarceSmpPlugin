package me.misha2win.scracesmpplugin.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.misha2win.scracesmpplugin.ScarceLife;

public final class ItemUtil {

	public static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "custom_item");

	public static List<String> makeList(ChatColor color, String lore) {
		List<String> lines = new ArrayList<>();
		if (lore == null) return lines;

		String text = lore.trim();
		int maxWidth = 30;

		while (!text.isEmpty()) {
			if (text.length() <= maxWidth) {
					lines.add(color + text);
					break;
			}

			int breakIndex = text.lastIndexOf(' ', maxWidth);
			if (breakIndex <= 0) breakIndex = maxWidth; //no space found, hard break

			String line = text.substring(0, breakIndex).trim();
			if (!line.isEmpty()) lines.add(color + line);

			text = text.substring(breakIndex).trim();
		}

		return lines;
	}

	public static void setType(ItemMeta meta, String type) {
		ItemUtil.setString(meta, ItemUtil.ITEM_ID_KEY, type);
	}

	public static void setString(ItemMeta meta, NamespacedKey key, String value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.STRING,
				value
			);
	}

	public static void setInteger(ItemMeta meta, NamespacedKey key, int value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.INTEGER,
				value
			);
	}

	public static void setBoolean(ItemMeta meta, NamespacedKey key, boolean value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.BOOLEAN,
				value
			);
	}

	public static String getType(ItemStack item) {
		if (item == null) return null;

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		return meta.getPersistentDataContainer().get(ItemUtil.ITEM_ID_KEY, PersistentDataType.STRING);
	}

	public static String getType(ItemMeta meta) {
		return meta.getPersistentDataContainer().get(ItemUtil.ITEM_ID_KEY, PersistentDataType.STRING);
	}
	public static String getString(ItemMeta meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}

	public static int getInteger(ItemMeta meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	}

	public static boolean getBoolean(ItemMeta meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
	}

	public static void setType(TileState meta, String type) {
		ItemUtil.setString(meta, ItemUtil.ITEM_ID_KEY, type);
	}

	public static void setString(TileState meta, NamespacedKey key, String value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.STRING,
				value
			);
	}

	public static void setInteger(TileState meta, NamespacedKey key, int value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.INTEGER,
				value
			);
	}

	public static void setBoolean(TileState meta, NamespacedKey key, boolean value) {
		meta.getPersistentDataContainer().set(
				key,
				PersistentDataType.BOOLEAN,
				value
			);
	}

	public static String getType(TileState meta) {
		return meta.getPersistentDataContainer().get(ItemUtil.ITEM_ID_KEY, PersistentDataType.STRING);
	}
	public static String getString(TileState meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}

	public static int getInteger(TileState meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	}

	public static boolean getBoolean(TileState meta, NamespacedKey key) {
		return meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
	}

}
