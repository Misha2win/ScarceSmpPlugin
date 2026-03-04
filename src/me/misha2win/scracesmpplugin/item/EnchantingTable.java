package me.misha2win.scracesmpplugin.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;
import me.misha2win.scracesmpplugin.util.TimeUtil;

public class EnchantingTable {

	public static final String TYPE = "enchanting_table";

	public static final HashMap<String, Long> COOLDOWNS = new HashMap<>();

	public static void register() {
		Bukkit.removeRecipe(NamespacedKey.minecraft("enchanting_table"));
		ItemRegistry.register(TYPE, EnchantingTable::createItem);
		ItemEventRouter.on(TYPE, BlockBreakEvent.class, EnchantingTable::onBreak);
		ItemEventRouter.on(TYPE, BlockPlaceEvent.class, EnchantingTable::onPlace);
		ItemEventRouter.on(TYPE, PlayerDropItemEvent.class, EnchantingTable::onPlayerDropItem);
		ItemEventRouter.on(TYPE, InventoryOpenEvent.class, EnchantingTable::onPlayerOpenInventory);
		ItemEventRouter.on(TYPE, PlayerItemConsumeEvent.class, EnchantingTable::onPlayerConsumeItem);
		ItemEventRouter.on(TYPE, PlayerDeathEvent.class, EnchantingTable::onPlayerDeath);
		ItemEventRouter.on(TYPE, PlayerQuitEvent.class, EnchantingTable::onPlayerQuit);
	}

	private static ItemStack createItem() {
		ItemStack item = new ItemStack(Material.ENCHANTING_TABLE, 1);

		ItemMeta meta = item.getItemMeta();
		meta.setMaxStackSize(1);

		meta.setDisplayName("Cursed Enchanting Table");

		ItemUtil.setType(meta, EnchantingTable.TYPE);

		item.setItemMeta(meta);

		return item;
	}

	private static void addEffects(Player player) {
		player.sendMessage(ChatColor.RED + "You have been cursed until you place the enchanting table back down!");
		player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 255, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 255, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 1, false, false));
	}

	private static void removeEffects(Player player) {
		player.sendMessage(ChatColor.GREEN + "Your curse has been lifted!");
		player.removePotionEffect(PotionEffectType.GLOWING);
		player.removePotionEffect(PotionEffectType.WEAKNESS);
		player.removePotionEffect(PotionEffectType.SLOWNESS);
		player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
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

	private static Location randomPointInsideWorldBorder(World world) {
		WorldBorder border = world.getWorldBorder();
		Location center = border.getCenter();

		double radius = border.getSize() / 2.0;

		//Keep a small margin so you don't land exactly on the border edge
		double margin = 2.0;
		double minX = center.getX() - radius + margin;
		double maxX = center.getX() + radius - margin;
		double minZ = center.getZ() - radius + margin;
		double maxZ = center.getZ() + radius - margin;

		Random random = new Random();
		double x = minX + (random.nextDouble() * (maxX - minX));
		double z = minZ + (random.nextDouble() * (maxZ - minZ));

		// Pick a safe-ish Y on the surface
		int highestY = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z));
		double y = highestY + 1.0;

		return new Location(world, x + 0.5, y, z + 0.5);
	}

	private static Location placeNewTable(ScarceLife plugin) {
		Location location = randomPointInsideWorldBorder(Bukkit.getWorlds().get(0));

		location.getBlock().setType(Material.ENCHANTING_TABLE);
		TileState tileState = (TileState) location.getBlock().getState();
		ItemUtil.setType(tileState, TYPE);
		tileState.update();

		Bukkit.broadcastMessage(String.format("%1$sA new enchanting table has been placed at %2$s%1$s!", ChatColor.WHITE, locationToString(location)));

		startGlow(plugin, location.getBlock());

		return location;
	}

	private static String getWorldName(Location location) {
		Environment env = location.getWorld().getEnvironment();
		if (env == Environment.NORMAL) {
			return "Overworld";
		} else if (env == Environment.NETHER) {
			return "Nether";
		} else if (env == Environment.THE_END) {
			return "End";
		}

		return "unknown";
	}

	private static String locationToString(Location location) {
		return String.format(
				"%5$s%1$d %2$d %3$d %6$sin the %5$s%4$s%6$s",
				location.getBlockX(), location.getBlockY(), location.getBlockZ(),
				getWorldName(location),
				ChatColor.GREEN, ChatColor.WHITE
			);
	}

	private static void startGlow(ScarceLife plugin, Block block) {
		BlockDisplay display = (BlockDisplay) block.getWorld().spawnEntity(block.getLocation(), EntityType.BLOCK_DISPLAY);
		display.setBlock(block.getBlockData());
		display.setGlowing(true);

		HashSet<UUID> hiddenPlayers = new HashSet<>();

		AtomicReference<BukkitTask> hideGlowTaskRef = new AtomicReference<>();
		AtomicReference<BukkitTask> cleanupTaskRef = new AtomicReference<>();

		hideGlowTaskRef.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			// Clean up early if block was broken/removed
			if (!display.isValid() || block.getType() != Material.ENCHANTING_TABLE) {
				BukkitTask task = hideGlowTaskRef.getAndSet(null);
				if (task != null) task.cancel();

				BukkitTask cleanup = cleanupTaskRef.getAndSet(null);
				if (cleanup != null) cleanup.cancel();

				display.remove();
				return;
			}

			for (Entity entity : display.getNearbyEntities(10, 10, 10)) {
				if (!(entity instanceof Player)) continue;
				Player p = (Player) entity;
				if (!hiddenPlayers.add(p.getUniqueId())) continue;
				p.hideEntity(plugin, display);
			}
		}, 20 * 3, 20));

		cleanupTaskRef.set(Bukkit.getScheduler().runTaskLater(plugin, () -> {
			BukkitTask task = hideGlowTaskRef.getAndSet(null);
			if (task != null) task.cancel();
			display.remove();
		}, plugin.getConfig().getInt("items.enchanting-table.glow-ticks")));
	}

	public static void onPlace(ScarceLife plugin, BlockPlaceEvent e) {
		ItemMeta itemMeta = e.getItemInHand().getItemMeta();
		TileState tileState = (TileState) e.getBlockPlaced().getState();

		ItemUtil.setType(tileState, ItemUtil.getType(itemMeta));

		tileState.update();

		Player player = e.getPlayer();
		Block block = e.getBlockPlaced();

		String locationString = locationToString(block.getLocation());

		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " has placed down the enchanting table!");
		Bukkit.broadcastMessage(ChatColor.WHITE + "The enchanting table is at " + locationString + ChatColor.WHITE + ".");

		removeEffects(player);

		// Set the pickup cooldown of this enchanting table
		COOLDOWNS.put(locationString, TimeUtil.getFutureTimeFromTicks(plugin.getConfig().getInt("items.enchanting-table.pickup-cooldown-ticks")));
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			COOLDOWNS.remove(locationString);
			Bukkit.broadcastMessage(String.format("%sThe enchanting table can be picked up!", ChatColor.GREEN));
		}, plugin.getConfig().getInt("items.enchanting-table.pickup-cooldown-ticks"));

		startGlow(plugin, block);
	}

	public static void onBreak(ScarceLife plugin, BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (player.getInventory().firstEmpty() == -1) {
			player.sendMessage(ChatColor.RED + "You must have at least one empty inventory slot in order to pick up the enchanting table!");
			e.setCancelled(true);
			return;
		}

		String locationString = locationToString(e.getBlock().getLocation());
		Long cooldown = COOLDOWNS.get(locationString);
		if (cooldown != null) {
			player.sendMessage(String.format(
					"%sThe enchanting table cannot be picked up for %.0f seconds!",
					ChatColor.RED, TimeUtil.getDeltaSeconds(cooldown)
				));
			e.setCancelled(true);
			return;
		}

		e.setDropItems(false);

		player.getInventory().addItem(EnchantingTable.createItem());

		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RED + " has picked up the enchanting table!");
		Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.WHITE + " is at " + locationString + ChatColor.WHITE + ".");

		addEffects(player);
	}

	public static void onPlayerDropItem(ScarceLife plugin, PlayerDropItemEvent e) {
		e.getPlayer().sendMessage(ChatColor.RED + "You cannot drop the enchanting table! It must be placed!");
		e.setCancelled(true);

		Player player = e.getPlayer();
		if (player.getInventory().firstEmpty() == -1) {
			Bukkit.broadcastMessage(player.getDisplayName() + ChatColor.RED + " has lost the enchanting table!");
			placeNewTable(plugin);
			removeEffects(player);
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
					addEffects(e.getPlayer());
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
					Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.WHITE + " is at " + locationToString(killer.getLocation()) + ChatColor.WHITE + ".");
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
					removeEffects(player);
				}
			}

			Bukkit.getScheduler().runTask(plugin, () -> {
				placeNewTable(plugin);
			});
		}
	}

}
