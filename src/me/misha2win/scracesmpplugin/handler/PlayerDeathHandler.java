package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.GhostBlink;
import me.misha2win.scracesmpplugin.item.GhostRespawn;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;

public class PlayerDeathHandler implements Listener {

	private ScarceLife plugin;

	public PlayerDeathHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (this.plugin.getConfig().getBoolean("ghost.keep-inventory")) {
			if (LifeManager.getLives(e.getEntity()) <= 1) { // Last life (before processing)
				e.setKeepInventory(false);
			} else {
				e.setKeepInventory(true);
				e.getDrops().clear();
			}
		}

		Bukkit.getScheduler().runTask(plugin, () -> {
			LifeManager.onDeath(e.getEntity());
			if (!this.plugin.getConfig().getBoolean("ghost.enabled")) {
				e.getEntity().kickPlayer("You lost your last life!");
			}
		});
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (LifeManager.getLives(e.getPlayer()) == 0) {
			Bukkit.getScheduler().runTask(plugin, () -> {
				FileConfiguration config = this.plugin.getConfig();

				e.getPlayer().setAllowFlight(true);
				e.getPlayer().teleport(e.getPlayer().getLastDeathLocation());
				LifeManager.updateTeam(e.getPlayer());

				if (config.getBoolean("ghost.give-items")) {
					if (config.getBoolean("items.blink.enabled")) {
						e.getPlayer().getInventory().addItem(ItemRegistry.get(GhostBlink.TYPE).get());
					}

					if (config.getBoolean("items.respawn.enabled")) {
						e.getPlayer().getInventory().addItem(ItemRegistry.get(GhostRespawn.TYPE).get());
					}
				}

				e.getPlayer().sendTitle(ChatColor.RED + "You lost your last life!", ChatColor.RED + "You are now a ghost!", 20, 20 * 5, 20);
				if (config.getBoolean("commands.tpa.enabled") && config.getBoolean("commands.tpa.only-ghosts")) {
					e.getPlayer().sendMessage(ChatColor.GREEN + "You may now fly around and use the /tpa command to teleport to other players!");
				} else {
					e.getPlayer().sendMessage(ChatColor.GREEN + "You may now fly around!");
				}
			});
		}
	}

}
