package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class PlayerJoinHandler implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public PlayerJoinHandler(Main plugin) {
		this.plugin = plugin;
	} 

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!e.getPlayer().hasPlayedBefore() || LifeManager.getLivesScoreboard(e.getPlayer()) == null) {
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
		
		// Unfreeze server
		Bukkit.getLogger().info("Unfreezing game");
		Bukkit.getServerTickManager().setFrozen(false);
		Bukkit.getLogger().info("Setting tick rate to 20");
		Bukkit.getServerTickManager().setTickRate(20);
	}
	
	@EventHandler
	public void onDisconnectJoin(PlayerQuitEvent e) {
		Bukkit.getWorlds().get(0).setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, CommandUtil.getAlivePlayersPercentage());
		Bukkit.getLogger().info("New sleep percentage is: " + CommandUtil.getAlivePlayersPercentage());
		
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			Bukkit.getLogger().info("This was the last player online! Now server is empty!");
			Bukkit.getLogger().info("Freezing game");
			Bukkit.getServerTickManager().setFrozen(true);
			Bukkit.getLogger().info("Setting tick rate to 1");
			Bukkit.getServerTickManager().setTickRate(1);
		}
	}
	
}