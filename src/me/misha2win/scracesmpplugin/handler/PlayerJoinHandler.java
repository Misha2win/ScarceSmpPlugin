package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class PlayerJoinHandler implements Listener {

	private ScarceLife plugin;

	public PlayerJoinHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (LifeManager.getLives(e.getPlayer()) <= 0 && !this.plugin.getConfig().getBoolean("ghost.enabled")) {
			e.getPlayer().kickPlayer("You have lost your last life!");
		}

		if (LifeManager.getLivesScoreboard(e.getPlayer()) == null) {
			// Player is logging in for first time or the player does not have a lives scoreboard
			LifeManager.getLivesScoreboard(e.getPlayer()).setScore(3);
			Bukkit.getLogger().info(e.getPlayer().getName() + " joined for the first time with 3 lives");
		} else {
			// The player has logged on before and has a lives scoreboard
			Bukkit.getLogger().info(e.getPlayer().getName() + " joined with " + LifeManager.getLivesScoreboard(e.getPlayer()).getScore() + " lives");
		}

		LifeManager.updateTeam(e.getPlayer());

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (LifeManager.getLives(player) <= 0) {
				PacketSender.sendTeamJoinPacket(e.getPlayer(), player);
			}
		}
	}

}