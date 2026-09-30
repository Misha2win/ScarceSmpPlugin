package me.misha2win.scracesmpplugin.item;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class PlayerHead {

	public static final String TYPE = "player_head";

	public static final NamespacedKey USED_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "used");
	public static final NamespacedKey DEATH_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "death");
	public static final NamespacedKey LIVES_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "lives");
	public static final NamespacedKey UUID_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "uuid");

	public static void register() {
		ItemRegistry.register(TYPE, null);
		ItemEventRouter.on(PlayerHead.TYPE, ItemDespawnEvent.class, PlayerHead::onItemDespawn);
		ItemEventRouter.on(PlayerHead.TYPE, PlayerDeathEvent.class, PlayerHead::onPlayerDeath);
		ItemEventRouter.on(PlayerHead.TYPE, BlockPlaceEvent.class, PlayerHead::onHeadPlace);
		ItemEventRouter.on(PlayerHead.TYPE, BlockBreakEvent.class, PlayerHead::onHeadBreak);
		ItemEventRouter.on(PlayerHead.TYPE, PrepareSmithingEvent.class, PlayerHead::onPrepareSmithing);
		ItemEventRouter.on(PlayerHead.TYPE, SmithItemEvent.class, PlayerHead::onSmithing);
		ItemEventRouter.on(PlayerHead.TYPE, PlayerItemConsumeEvent.class, PlayerHead::onPlayerItemConsume);
	}

	private static ItemStack createItem(ScarceLife plugin, Player player, String death) {
		return PlayerHead.createItem(plugin, player.getUniqueId(), LifeManager.getLives(player), death, false);
	}

	private static ItemStack createItem(ScarceLife plugin, UUID uuid, int livesBefore, String death, boolean used) {
		FileConfiguration config = plugin.getConfig();
		boolean isConsumable = config.getBoolean("items.player-head.consumable");
		boolean isIngredient = config.getBoolean("items.eden-apple.enabled");

		ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);

		SkullMeta meta = (SkullMeta) playerSkull.getItemMeta();

		if (isConsumable && !used) {
			FoodComponent food = meta.getFood();
			food.setNutrition(20);
			food.setSaturation(20);
			food.setCanAlwaysEat(true);

			ConsumableComponent consumable = meta.getConsumable();
			consumable.setConsumeSeconds(10);

			meta.setFood(food);
			meta.setConsumable(consumable);
		}

		ItemUtil.setType(meta, PlayerHead.TYPE);
		ItemUtil.setBoolean(meta, PlayerHead.USED_KEY, used);

		ChatColor livesColor = LifeManager.getChatColor(livesBefore);

		String name = Bukkit.getOfflinePlayer(uuid).getName();
		meta.setDisplayName(livesColor + name  + ChatColor.WHITE + "'s Head");

		ArrayList<String> itemLore = new ArrayList<>();
		itemLore.add(death);
		itemLore.add(livesColor + "Lives before death: " + livesBefore);
		if ((isConsumable || isIngredient) && !used) {
			String text = isConsumable ? " Consumable " : " Eden Apple Ingredient ";
			itemLore.add(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET + "" + ChatColor.GOLD +  text + ChatColor.MAGIC + "X");
		}
		meta.setLore(itemLore);

		ItemUtil.setInteger(meta, PlayerHead.LIVES_KEY, livesBefore);
		ItemUtil.setString(meta, PlayerHead.DEATH_KEY, death);
		ItemUtil.setString(meta, PlayerHead.UUID_KEY, uuid.toString());

		meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

		playerSkull.setItemMeta(meta);

		return playerSkull;
	}

	public static void onHeadPlace(ScarceLife plugin, BlockPlaceEvent e) {
		ItemMeta itemMeta = e.getItemInHand().getItemMeta();
		TileState tileState = (TileState) e.getBlockPlaced().getState();

		// Backwards compatibility for older heads that only have the player name stored
		if (ItemUtil.getString(itemMeta, PlayerHead.UUID_KEY) == null) {
			String name = ItemUtil.getString(itemMeta, new NamespacedKey(ScarceLife.NAMESPACE, "player"));
			@SuppressWarnings("deprecation")
			String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
			ItemUtil.setString(itemMeta, PlayerHead.UUID_KEY, uuid);
		}

		ItemUtil.setType(tileState, ItemUtil.getType(itemMeta));
		ItemUtil.setBoolean(tileState, PlayerHead.USED_KEY, ItemUtil.getBoolean(itemMeta, PlayerHead.USED_KEY));
		ItemUtil.setString(tileState, PlayerHead.UUID_KEY, ItemUtil.getString(itemMeta, PlayerHead.UUID_KEY));
		ItemUtil.setInteger(tileState, PlayerHead.LIVES_KEY, ItemUtil.getInteger(itemMeta, PlayerHead.LIVES_KEY));
		ItemUtil.setString(tileState, PlayerHead.DEATH_KEY, ItemUtil.getString(itemMeta, PlayerHead.DEATH_KEY));

		tileState.update();
	}

	public static void onHeadBreak(ScarceLife plugin, BlockBreakEvent e) {
		e.setDropItems(false);
		Block block = e.getBlock();

		TileState tileState = (TileState) block.getState();
		boolean used = ItemUtil.getBoolean(tileState, PlayerHead.USED_KEY);
		String uuid = ItemUtil.getString(tileState, PlayerHead.UUID_KEY);
		int lives = ItemUtil.getInteger(tileState, PlayerHead.LIVES_KEY);
		String death = ItemUtil.getString(tileState, PlayerHead.DEATH_KEY);

		// This will only drop the item once the player's profile has been updated (to get skin)
		UUID playerUuid = UUID.fromString(uuid);
		Bukkit.getOfflinePlayer(playerUuid).getPlayerProfile().update().thenAcceptAsync(profile -> {
			ItemStack dropItem = PlayerHead.createItem(plugin, playerUuid, lives, death, used);
			SkullMeta dropMeta = (SkullMeta) dropItem.getItemMeta();
			dropMeta.setOwnerProfile(profile);
			dropItem.setItemMeta(dropMeta);

			block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), dropItem);
		}, task -> Bukkit.getScheduler().runTask(plugin, task)).exceptionally(ex -> {
			ItemStack dropItem = PlayerHead.createItem(plugin, playerUuid, lives, death, used);
			block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), dropItem);
			return null;
		});
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

		String uuid = ItemUtil.getString(oldMeta, PlayerHead.UUID_KEY);
		int lives = ItemUtil.getInteger(oldMeta, PlayerHead.LIVES_KEY);
		String death = ItemUtil.getString(oldMeta, PlayerHead.DEATH_KEY);

		ItemStack replacement = PlayerHead.createItem(plugin, UUID.fromString(uuid), lives, death, true);

		Bukkit.getScheduler().runTask(plugin, () -> {
			inventory.setItem(1, replacement);
		});
	}

	public static void onItemDespawn(ScarceLife plugin, ItemDespawnEvent e) {
		e.setCancelled(true);
		e.getEntity().setGlowing(true);
	}

	public static void onPlayerDeath(ScarceLife plugin, PlayerDeathEvent e) {
		if (!plugin.getConfig().getBoolean("death.drop-head")) {
			return;
		}

		Player victim = e.getEntity();
		ItemStack head = PlayerHead.createItem(plugin, victim, ChatColor.DARK_RED + e.getDeathMessage());

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

	public static void onPlayerItemConsume(ScarceLife plugin, PlayerItemConsumeEvent e) {
		FileConfiguration config = plugin.getConfig();
		boolean isConsumable = config.getBoolean("items.player-head.consumable");

		ItemMeta itemMeta = e.getItem().getItemMeta();

		boolean used =ItemUtil.getBoolean(itemMeta, PlayerHead.USED_KEY);
		String uuid = ItemUtil.getString(itemMeta, PlayerHead.UUID_KEY);
		int lifes = ItemUtil.getInteger(itemMeta, PlayerHead.LIVES_KEY);
		String death = ItemUtil.getString(itemMeta, PlayerHead.DEATH_KEY);

		if (used || !isConsumable) {
			e.setCancelled(true);
			return;
		}

		e.getPlayer().sendMessage(ChatColor.GREEN + "The god of cannibalism gives you a life!");
		LifeManager.addLife(e.getPlayer());

		ItemStack usedHead = PlayerHead.createItem(plugin, UUID.fromString(uuid), lifes, death, true);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			e.getPlayer().getInventory().addItem(usedHead);
		}, 1L);
	}

}
