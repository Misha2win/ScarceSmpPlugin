package me.misha2win.scracesmpplugin.handler;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaCommandHandler;

public class PlayerQuitHandler implements Listener {

	private ScarceLife plugin;

	public PlayerQuitHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (!plugin.getConfig().getBoolean("commands.tpa.enabled")) return;

		HashMap<Player, Player> requests = TpaCommandHandler.REQUESTS;
		requests.keySet().removeIf(player -> {
			if (player.equals(e.getPlayer())) {
				requests.get(player).sendMessage(ChatColor.RED + "The teleport request from " + player.getName() + " has been cancelled.");
				return true;
			} else if (requests.get(player).equals(e.getPlayer())) {
				player.sendMessage(ChatColor.RED + "Your teleport request has been cancelled.");
				return true;
			}
			return false;
		});
	}

}