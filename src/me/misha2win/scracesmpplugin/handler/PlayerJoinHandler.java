package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.EnchantingTable;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class PlayerJoinHandler implements Listener {

	private ScarceLife plugin;

	public PlayerJoinHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		if (plugin.getConfig().getBoolean("developer.op-only.enabled") && !player.isOp()) {
			player.kickPlayer("The server is not allowing unopped players to join right now!");
		}

		if (!player.hasPlayedBefore() || LifeManager.getLivesScoreboard(player) == null) {
			// Player is logging in for first time or the player does not have a lives scoreboard
			int lives = plugin.getConfig().getInt("lives.initial");
			LifeManager.getLivesScoreboard(player).setScore(lives);
			Bukkit.getLogger().info(player.getName() + " joined for the first time with " + lives + " lives");
		} else {
			// The player has logged on before and has a lives scoreboard
			Bukkit.getLogger().info(player.getName() + " joined with " + LifeManager.getLivesScoreboard(player).getScore() + " lives");
		}

		LifeManager.updateTeam(player);

		if (LifeManager.getLives(player) <= 0 && !this.plugin.getConfig().getBoolean("ghost.enabled")) {
			player.kickPlayer("You have lost your last life!");
		}

		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			if (LifeManager.getLives(otherPlayer) <= 0) {
				PacketSender.sendTeamJoinPacket(player, otherPlayer);
			}
		}

		EnchantingTable.setFooter(plugin);
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.sendMessage(ChatColor.GREEN + "You can use the tab list to see where the enchanting table is!");
		});
	}

}