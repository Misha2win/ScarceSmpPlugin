package me.misha2win.scracesmpplugin.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class GhostRespawn {

	public static final String TYPE = "ghost_respawn";

	public static void register() {
		ItemRegistry.register(TYPE, GhostRespawn::createItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, GhostRespawn::onPlayerInteract);
	}

	private static ItemStack createItem() {
		ItemStack item = new ItemStack(Material.COMPASS, 1);

		ItemMeta meta = item.getItemMeta();
		meta.setMaxStackSize(1);

		meta.setDisplayName(ChatColor.GOLD + "Respawn");

		ItemUtil.setType(meta, TYPE);

		item.setItemMeta(meta);

		return item;
	}

	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (LifeManager.getLives(player) > 0) {
			e.getPlayer().getInventory().setItemInMainHand(null);
			e.getPlayer().sendMessage(ChatColor.RED + "You shouldn't have this item!");
			return;
		}

		if (e.getPlayer().getCooldown(Material.COMPASS) != 0) return;

		player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		player.setCooldown(Material.COMPASS, plugin.getConfig().getInt("items.respawn.cooldown-ticks"));
	}

}
