package me.misha2win.scracesmpplugin.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
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

	public static String getType(ItemStack item) {
		if (item == null) return null;

		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		return meta.getPersistentDataContainer().get(ItemUtil.ITEM_ID_KEY, PersistentDataType.STRING);
	}

	public static void setType(PersistentDataHolder holder, String type) {
		ItemUtil.setString(holder, ItemUtil.ITEM_ID_KEY, type);
	}

	public static void setString(PersistentDataHolder holder, NamespacedKey key, String value) {
		holder.getPersistentDataContainer().set(
				key,
				PersistentDataType.STRING,
				value
			);
	}

	public static void setInteger(PersistentDataHolder holder, NamespacedKey key, int value) {
		holder.getPersistentDataContainer().set(
				key,
				PersistentDataType.INTEGER,
				value
			);
	}

	public static void setBoolean(PersistentDataHolder holder, NamespacedKey key, boolean value) {
		holder.getPersistentDataContainer().set(
				key,
				PersistentDataType.BOOLEAN,
				value
			);
	}

	public static String getType(PersistentDataHolder holder) {
		return holder.getPersistentDataContainer().get(ItemUtil.ITEM_ID_KEY, PersistentDataType.STRING);
	}
	public static String getString(PersistentDataHolder holder, NamespacedKey key) {
		return holder.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}

	public static int getInteger(PersistentDataHolder holder, NamespacedKey key) {
		return holder.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	}

	public static boolean getBoolean(PersistentDataHolder holder, NamespacedKey key) {
		return holder.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
	}

}
