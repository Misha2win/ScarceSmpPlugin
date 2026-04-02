package me.misha2win.scracesmpplugin.item;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRecipeRegistry;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.BlockDisplayUtil;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.ItemUtil;
import me.misha2win.scracesmpplugin.util.TimeUtil;

public class EnchantingTable {

	public static final String TYPE = "enchanting_table";

	public static final NamespacedKey RECIPE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, TYPE);

	private static BukkitTask hideGlowTask;
	private static BukkitTask cleanupTask;
	private static Long cooldown;

	public static void register() {
		Bukkit.removeRecipe(NamespacedKey.minecraft("enchanting_table"));
		ItemRegistry.register(TYPE, EnchantingTable::createItem);
		ItemRecipeRegistry.register(TYPE, getRecipe());
		ItemEventRouter.on(TYPE, BlockBreakEvent.class, EnchantingTable::onBreak);
		ItemEventRouter.on(TYPE, BlockPlaceEvent.class, EnchantingTable::onPlace);
		ItemEventRouter.on(TYPE, PlayerDropItemEvent.class, EnchantingTable::onPlayerDropItem);
		ItemEventRouter.on(TYPE, InventoryOpenEvent.class, EnchantingTable::onPlayerOpenInventory);
		ItemEventRouter.on(TYPE, PlayerItemConsumeEvent.class, EnchantingTable::onPlayerConsumeItem);
		ItemEventRouter.on(TYPE, PlayerDeathEvent.class, EnchantingTable::onPlayerDeath);
		ItemEventRouter.on(TYPE, PlayerQuitEvent.class, EnchantingTable::onPlayerQuit);
		ItemEventRouter.on(TYPE, PrepareItemCraftEvent.class, EnchantingTable::onPrepareCraft);
		ItemEventRouter.on(TYPE, CraftItemEvent.class, EnchantingTable::onCraft);
		ItemEventRouter.on(TYPE, PrepareItemEnchantEvent.class, EnchantingTable::onEnchantPrepare);
		ItemEventRouter.on(TYPE, EnchantItemEvent.class, EnchantingTable::onEnchant);
	}

	private static ItemStack createItem() {
		ItemStack item = new ItemStack(Material.ENCHANTING_TABLE, 1);

		ItemMeta meta = item.getItemMeta();
		meta.setMaxStackSize(1);

		meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
		meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);

		ItemUtil.setType(meta, EnchantingTable.TYPE);

		item.setItemMeta(meta);

		return item;
	}

	private static Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(RECIPE_KEY, createItem());

		recipe.shape(
			" B ",
			"DOD",
			"OOO"
		);

		recipe.setIngredient('B', Material.BOOK);
		recipe.setIngredient('O', Material.OBSIDIAN);
		recipe.setIngredient('D', Material.DIAMOND);

		return recipe;
	}

	private static void addEffects(ScarceLife plugin, Player player) {
		player.sendMessage(ChatColor.RED + "You have been cursed until you place the enchanting table back down!");
		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 255, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 255, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 1, false, false));

		FileConfiguration config = plugin.getConfig();
		config.set("items.enchanting-table.holder", player.getName());
		plugin.saveConfig();

		setFooter(plugin);
	}

	private static void removeEffects(ScarceLife plugin, Player player) {
		player.sendMessage(ChatColor.GREEN + "Your curse has been lifted!");
		player.removePotionEffect(PotionEffectType.GLOWING);
		player.removePotionEffect(PotionEffectType.WEAKNESS);
		player.removePotionEffect(PotionEffectType.SLOWNESS);
		player.removePotionEffect(PotionEffectType.MINING_FATIGUE);

		FileConfiguration config = plugin.getConfig();
		config.set("items.enchanting-table.holder", "no-one");
		plugin.saveConfig();
	}

	private static boolean inventoryContainsType(PlayerInventory playerInv) {
		if (playerInv.getHolder().getGameMode() != GameMode.SURVIVAL) return false;

		for (ItemStack item : playerInv.getStorageContents()) {
			if (TYPE.equals(ItemUtil.getType(item))) {
				return true;
			}
		}

		return TYPE.equals(ItemUtil.getType(playerInv.getItemInOffHand()));
	}

	private static Location placeNewTable(ScarceLife plugin) {
		Location location = CommandUtil.randomPointInsideWorldBorder(Bukkit.getWorlds().get(0)).getBlock().getLocation();

		location.getChunk().load();

		Block block = location.getBlock();
		block.setType(Material.ENCHANTING_TABLE);
		TileState tileState = (TileState) block.getState();
		ItemUtil.setType(tileState, TYPE);
		tileState.update();

		Bukkit.broadcastMessage(String.format("%1$sA new enchanting table has been placed at %2$s%1$s!", ChatColor.WHITE, CommandUtil.locationToString(location)));

		removeGlow(plugin);

		FileConfiguration config = plugin.getConfig();
		config.set("items.enchanting-table.location", location);
		plugin.saveConfig();

		startGlow(plugin);

		return location;
	}

	private static void cancelGlowTasks() {
		if (hideGlowTask != null) {
			hideGlowTask.cancel();
			hideGlowTask = null;
		}
		if (cleanupTask != null) {
			cleanupTask.cancel();
			cleanupTask = null;
		}
	}

	private static void removeGlow(ScarceLife plugin) {
		cancelGlowTasks();
		FileConfiguration config = plugin.getConfig();
		Location location = config.getLocation("items.enchanting-table.location");
		if (location != null) BlockDisplayUtil.removeBlockDisplays(plugin, location);
	}

	private static void startGlow(ScarceLife plugin) {
		FileConfiguration config = plugin.getConfig();
		Location location = config.getLocation("items.enchanting-table.location");
		if (location == null) return;

		BlockDisplayUtil.createGlowingBlockDisplay(location);

		hideGlowTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Entity blockDisplay : BlockDisplayUtil.getBlockDisplays(location)) {
				for (Entity entity : blockDisplay.getNearbyEntities(10, 10, 10)) {
					if (!(entity instanceof Player)) continue;
					Player p = (Player) entity;
					if (!p.canSee(blockDisplay)) continue;
					p.hideEntity(plugin, blockDisplay);
				}
			}
		}, 20 * 3, 20);

		int glowTicks = plugin.getConfig().getInt("items.enchanting-table.glow-ticks");
		if (glowTicks > 0) {
			cleanupTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (hideGlowTask != null) {
					hideGlowTask.cancel();
					hideGlowTask = null;
				}
				BlockDisplayUtil.removeBlockDisplays(plugin, location);
			}, glowTicks);
		}
	}

	public static void setFooter(ScarceLife plugin) {
		FileConfiguration config = plugin.getConfig();

		String holder = config.getString("items.enchanting-table.holder");
		if (!holder.equals("no-one")) {
			String player = Bukkit.getPlayer(holder).getDisplayName(); // If this throws an exception, code is broken anyways. Holder should never be offline!
			CommandUtil.setFooter(String.format("%s %shas the enchanting table!", player, ChatColor.WHITE));
			return;
		}

		Location location = config.getLocation("items.enchanting-table.location");
		if (location == null) return;
		String coords = CommandUtil.locationToString(location);
		String pickupable = cooldown !=  null ? ChatColor.RED + "It can't be picked up!" : ChatColor.GREEN + "It can be picked up!";
		CommandUtil.setFooter(String.format("%sEnchanting table:\n%s%s.\n%s", ChatColor.WHITE, coords, ChatColor.WHITE, pickupable));
	}

	private static void queueNotifyPickupable(ScarceLife plugin, long ticks) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			cooldown = null;
			plugin.getConfig().set("items.enchanting-table.current-cooldown", cooldown);
			plugin.saveConfig();
			Bukkit.broadcastMessage(String.format("%sThe enchanting table can be picked up!", ChatColor.GREEN));
			setFooter(plugin);
		}, ticks);
	}

	public static void onPlace(ScarceLife plugin, BlockPlaceEvent e) {
		ItemMeta itemMeta = e.getItemInHand().getItemMeta();
		TileState tileState = (TileState) e.getBlockPlaced().getState();

		ItemUtil.setType(tileState, ItemUtil.getType(itemMeta));

		tileState.update();

		Player player = e.getPlayer();
		Block block = e.getBlockPlaced();

		String locationString = CommandUtil.locationToString(block.getLocation());

		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " has placed down the enchanting table!");
		Bukkit.broadcastMessage(ChatColor.WHITE + "The enchanting table is at " + locationString + ChatColor.WHITE + ".");

		removeEffects(plugin, player);

		FileConfiguration config = plugin.getConfig();

		removeGlow(plugin);

		// Set the pickup cooldown of this enchanting table
		int cooldownTicks = plugin.getConfig().getInt("items.enchanting-table.pickup-cooldown-ticks");
		cooldown =  TimeUtil.getFutureTimeFromTicks(cooldownTicks);
		config.set("items.enchanting-table.current-cooldown", cooldown);
		config.set("items.enchanting-table.location", block.getLocation());
		plugin.saveConfig();

		queueNotifyPickupable(plugin, cooldownTicks);
		startGlow(plugin);
		setFooter(plugin);
	}

	public static void onBreak(ScarceLife plugin, BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (player.getInventory().firstEmpty() == -1) {
			player.sendMessage(ChatColor.RED + "You must have at least one empty inventory slot in order to pick up the enchanting table!");
			e.setCancelled(true);
			return;
		}

		if (cooldown != null) {
			player.sendMessage(String.format(
					"%sThe enchanting table cannot be picked up for %.0f seconds!",
					ChatColor.RED, TimeUtil.getDeltaSeconds(cooldown)
				));
			e.setCancelled(true);
			return;
		}

		e.setDropItems(false);

		FileConfiguration config = plugin.getConfig();
		Location location = config.getLocation("items.enchanting-table.location");
		if (location != null) BlockDisplayUtil.removeBlockDisplays(plugin, location);

		player.getInventory().addItem(EnchantingTable.createItem());

		String locationString = CommandUtil.locationToString(e.getPlayer().getLocation());

		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RED + " has picked up the enchanting table!");
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " is at " + locationString + ChatColor.WHITE + ".");

		addEffects(plugin, player);
	}

	public static void onPlayerDropItem(ScarceLife plugin, PlayerDropItemEvent e) {
		e.getPlayer().sendMessage(ChatColor.RED + "You cannot drop the enchanting table! It must be placed!");
		e.setCancelled(true);

		Player player = e.getPlayer();
		if (player.getInventory().firstEmpty() == -1) {
			Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RED + " has lost the enchanting table!");
			placeNewTable(plugin);
			removeEffects(plugin, player);
		}
	}

	public static void onPlayerOpenInventory(ScarceLife plugin, InventoryOpenEvent e) {
		Player player = (Player) e.getPlayer();

		if (inventoryContainsType(player.getInventory())) {
			if (e.getInventory().getType() != InventoryType.PLAYER) {
				player.sendMessage(ChatColor.RED + "You cannot open other inventories until you are not longer cursed!");
				e.setCancelled(true);
				return;
			}
		}
	}

	public static void onPlayerConsumeItem(ScarceLife plugin, PlayerItemConsumeEvent e) {
		if (inventoryContainsType(e.getPlayer().getInventory())) {
			if (e.getItem().getType() == Material.MILK_BUCKET) {
				Bukkit.getScheduler().runTask(plugin, () -> {
					addEffects(plugin, e.getPlayer());
				});
			}
		}
	}

	public static void onPlayerDeath(ScarceLife plugin, PlayerDeathEvent e) {
		if (inventoryContainsType(e.getEntity().getInventory())) {
			Player victim = e.getEntity();

			PlayerInventory victimInv = victim.getInventory();
			for (int slot = 0; slot < victimInv.getSize(); slot++) {
				if (TYPE.equals(ItemUtil.getType(victimInv.getItem(slot)))) {
					victimInv.setItem(slot, null);
				}
			}

			List<ItemStack> drops = e.getDrops();
			for (int i = drops.size() - 1; i >= 0; i--) {
				if (TYPE.equals(ItemUtil.getType(drops.get(i)))) {
					drops.remove(i);
				}
			}

			Player killer = victim.getKiller();

			Bukkit.getScheduler().runTask(plugin, () -> {
				victim.sendMessage(ChatColor.RED + "You have lost the enchanting table!");

				if (killer != null) {
					Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.RED + " has picked up the enchanting table!");
					Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.WHITE + " is at " + CommandUtil.locationToString(killer.getLocation()) + ChatColor.WHITE + ".");
					return;
				} else {
					placeNewTable(plugin);
				}
			});
		}
	}

	public static void onPlayerQuit(ScarceLife plugin, PlayerQuitEvent e) {
		if (inventoryContainsType(e.getPlayer().getInventory())) {
			Player player = e.getPlayer();
			PlayerInventory playerInv = player.getInventory();
			for (int slot = 0; slot < playerInv.getSize(); slot++) {
				if (TYPE.equals(ItemUtil.getType(playerInv.getItem(slot)))) {
					playerInv.setItem(slot, null);
					removeEffects(plugin, player);
				}
			}

			Bukkit.getScheduler().runTask(plugin, () -> {
				placeNewTable(plugin);
			});
		}
	}

	public static void onPrepareCraft(ScarceLife plugin, PrepareItemCraftEvent e) {
		ItemStack result = e.getInventory().getResult();
		ItemMeta meta = result.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(ItemUtil.makeList(ChatColor.RED, "Warning: Crafting this will curse you and reveal your location!"));
		result.setItemMeta(meta);
		e.getInventory().setResult(result);
	}

	public static void onCraft(ScarceLife plugin, CraftItemEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}

		ItemStack result = e.getCurrentItem();
		ItemMeta meta = result.getItemMeta();
		meta.setLore(null);
		meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		result.setItemMeta(meta);
		result.setAmount(1);
		e.setCurrentItem(result);

		Player player = (Player) e.getWhoClicked();
		String locationString = CommandUtil.locationToString(player.getLocation());

		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RED + " has crafted the enchanting table!");
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " is at " + locationString + ChatColor.WHITE + ".");

		addEffects(plugin, player);

		Bukkit.removeRecipe(RECIPE_KEY);
	}

	public static void onEnchantPrepare(ScarceLife plugin, PrepareItemEnchantEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchanting-table.weak-enchants")) return;

		for (EnchantmentOffer offer : e.getOffers()) {
			offer.setEnchantmentLevel(1);
		}
	}

	public static void onEnchant(ScarceLife plugin, EnchantItemEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchanting-table.weak-enchants")) return;

		Map<Enchantment, Integer> enchants = e.getEnchantsToAdd();
		for (Enchantment enchant : e.getEnchantsToAdd().keySet()) {
			enchants.put(enchant, 1);
		}
	}

	public static void onEnable(ScarceLife plugin) {
		FileConfiguration config = plugin.getConfig();
		boolean craftable = config.getBoolean("items.enchanting-table.craftable");
		if (!craftable) {
			Bukkit.removeRecipe(RECIPE_KEY);
		}

		Location location = config.getLocation("items.enchanting-table.location");
		if (location == null) return;
		BlockDisplayUtil.removeBlockDisplays(plugin, location);

		if (config.contains("items.enchanting-table.current-cooldown")) {
			cooldown = config.getLong("items.enchanting-table.current-cooldown");
			long ticks = TimeUtil.getDeltaMilliseconds(cooldown) / 50;
			if (ticks > 0) {
				queueNotifyPickupable(plugin, ticks);
			} else {
				config.set("items.enchanting-table.current-cooldown", null);
				plugin.saveConfig();
			}
		}

		startGlow(plugin);
	}

}
