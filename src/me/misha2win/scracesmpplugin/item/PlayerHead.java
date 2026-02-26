package me.misha2win.scracesmpplugin.item;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class PlayerHead {

	public static final String TYPE = "player_head";

	public static final NamespacedKey USED_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "used");
	public static final NamespacedKey PLAYER_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "player");
	public static final NamespacedKey DEATH_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "death");
	public static final NamespacedKey LIVES_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "lives");

	public static void register() {
		ItemRegistry.register(TYPE, null);
		ItemEventRouter.on(PlayerHead.TYPE, ItemDespawnEvent.class, PlayerHead::onItemDespawn);
		ItemEventRouter.on(PlayerHead.TYPE, PlayerDeathEvent.class, PlayerHead::onPlayerDeath);
		ItemEventRouter.on(PlayerHead.TYPE, BlockPlaceEvent.class, PlayerHead::onHeadPlace);
		ItemEventRouter.on(PlayerHead.TYPE, BlockBreakEvent.class, PlayerHead::onHeadBreak);
		ItemEventRouter.on(PlayerHead.TYPE, PrepareSmithingEvent.class, PlayerHead::onPrepareSmithing);
		ItemEventRouter.on(PlayerHead.TYPE, SmithItemEvent.class, PlayerHead::onSmithing);
	}

	private static ItemStack createItem(Player player, String death) {
		return PlayerHead.createItem(player.getName(), LifeManager.getLives(player), death, false);
	}

	private static ItemStack createItem(String name, int livesBefore, String death, boolean used) {
		ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);

		SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();
		meta.setMaxStackSize(1);

		ItemUtil.setType(meta, PlayerHead.TYPE);
		ItemUtil.setBoolean(meta, PlayerHead.USED_KEY, used);

		ChatColor livesColor = LifeManager.getChatColor(livesBefore);

		meta.setDisplayName(livesColor + name  + ChatColor.WHITE + "'s Head");

		ArrayList<String> itemLore = new ArrayList<>();
		itemLore.add(death);
		itemLore.add(livesColor + "Lives before death: " + livesBefore);
		if (!used) {
			itemLore.add(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET + "" + ChatColor.GOLD +  " Eden Apple Ingredient " + ChatColor.MAGIC + "X");
		} else {
			itemLore.add(ChatColor.MAGIC + "X" + ChatColor.DARK_PURPLE + " Ingredient Spent " + ChatColor.RESET + ChatColor.MAGIC + "X");
		}
		meta.setLore(itemLore);

		ItemUtil.setString(meta, PlayerHead.PLAYER_KEY, name);
		ItemUtil.setInteger(meta, PlayerHead.LIVES_KEY, livesBefore);
		ItemUtil.setString(meta, PlayerHead.DEATH_KEY, death);

		meta.setOwningPlayer(Bukkit.getPlayer(name));
		playerSkull.setItemMeta(meta);

		return playerSkull;
	}

	public static void onHeadPlace(ScarceLife plugin, BlockPlaceEvent e) {
		ItemMeta itemMeta = e.getItemInHand().getItemMeta();
		TileState tileState = (TileState) e.getBlockPlaced().getState();

		ItemUtil.setType(tileState, ItemUtil.getType(itemMeta));
		ItemUtil.setBoolean(tileState, PlayerHead.USED_KEY, ItemUtil.getBoolean(itemMeta, PlayerHead.USED_KEY));
		ItemUtil.setString(tileState, PlayerHead.PLAYER_KEY, ItemUtil.getString(itemMeta, PlayerHead.PLAYER_KEY));
		ItemUtil.setInteger(tileState, PlayerHead.LIVES_KEY, ItemUtil.getInteger(itemMeta, PlayerHead.LIVES_KEY));
		ItemUtil.setString(tileState, PlayerHead.DEATH_KEY, ItemUtil.getString(itemMeta, PlayerHead.DEATH_KEY));

		tileState.update();
	}

	public static void onHeadBreak(ScarceLife plugin, BlockBreakEvent e) {
		e.setDropItems(false);
		Block block = e.getBlock();

		TileState tileState = (TileState) block.getState();
		boolean used = ItemUtil.getBoolean(tileState, PlayerHead.USED_KEY);
		String name = ItemUtil.getString(tileState, PlayerHead.PLAYER_KEY);
		int lives = ItemUtil.getInteger(tileState, PlayerHead.LIVES_KEY);
		String death = ItemUtil.getString(tileState, PlayerHead.DEATH_KEY);

		ItemStack dropItem = PlayerHead.createItem(name, lives, death, used);

		block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), dropItem);
	}

	public static void onPrepareSmithing(ScarceLife plugin, PrepareSmithingEvent e) {
		if (e.getResult() == null) return;

	    ItemStack baseItem = e.getInventory().getItem(1);
	    if (!TYPE.equals(ItemUtil.getType(baseItem)) || ItemUtil.getBoolean(baseItem.getItemMeta(), PlayerHead.USED_KEY)) {
	    	e.setResult(null);
	    }
	}

	public static void onSmithing(ScarceLife plugin, SmithItemEvent e) {
		if (e.getResult() == null) return;

	    SmithingInventory inventory = e.getInventory();
	    ItemMeta oldMeta = inventory.getItem(1).getItemMeta();

	    String name = ItemUtil.getString(oldMeta, PlayerHead.PLAYER_KEY);
		int lives = ItemUtil.getInteger(oldMeta, PlayerHead.LIVES_KEY);
		String death = ItemUtil.getString(oldMeta, PlayerHead.DEATH_KEY);

		ItemStack replacement = PlayerHead.createItem(name, lives, death, true);

    	Bukkit.getScheduler().runTask(plugin, () -> {
    		inventory.setItem(1, replacement);
    	});
	}

	public static void onItemDespawn(ScarceLife plugin, ItemDespawnEvent e) {
		e.setCancelled(true);
	}

	public static void onPlayerDeath(ScarceLife plugin, PlayerDeathEvent e) {
		Player victim = e.getEntity();
		ItemStack head = PlayerHead.createItem(victim, ChatColor.DARK_RED + e.getDeathMessage());

		Player killer = victim.getKiller();
		if (killer != null) {
			killer.sendMessage(ChatColor.GREEN + "You got the kill credit for " + victim.getDisplayName() + ChatColor.GREEN + "'s death!");

			Inventory killerInventory = killer.getInventory();
			int killerInentorySlot = killerInventory.firstEmpty();
			if (killerInentorySlot != -1) {
				killer.sendMessage(ChatColor.GREEN + "Their head has been placed into your inventory.");
				killerInventory.setItem(killerInentorySlot, head);
			} else {
				killer.sendMessage(ChatColor.RED + "There was no empty inventory slot to place their head into.");
				killer.sendMessage(ChatColor.WHITE + "Their head was dropped at your feet.");
				Item item = killer.getWorld().dropItem(killer.getLocation(), head);
				item.setInvulnerable(true);
			}

			return;
		}

		Item item = victim.getWorld().dropItemNaturally(victim.getLocation(), head);
		item.setInvulnerable(true);
	}

}
