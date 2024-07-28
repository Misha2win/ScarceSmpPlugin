package me.misha2win.scracesmpplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.PacketSender;
import me.misha2win.scracesmpplugin.util.ParticleMaker;

public final class LifeManager {
	
	public static final String LIVES_SCOREBOARD_NAME = "lives";
	public static final String EDEN_APPLES_SCOREBOARD_NAME = "edenapples";
	
	public enum LifeMode {
		THREELIVES,
		RANDOMLIFES,
		SOULMATES,
		TIMER
	}
	
	public LifeMode lifemode;
	public boolean edenAppleAllowed;
	
	public LifeManager(LifeMode lifemode, boolean edenAppleAllowed) {
		this.lifemode = lifemode;
		this.edenAppleAllowed = edenAppleAllowed;
	}
	
	public static void onDeath(Player player) {
		removeLife(player);
		
		if (getLives(player) == 0) {
			player.getWorld().strikeLightningEffect(player.getLocation());
		}
		
		Bukkit.getLogger().info(player.getName() + " has died!");
	}
	
	public static Score getLivesScoreboard(Player player) {
		return Bukkit.getScoreboardManager().getMainScoreboard().getObjective(LIVES_SCOREBOARD_NAME).getScore(player.getName());
	}
	
	public static Score getEdenScoreboard(Player player) {
		return Bukkit.getScoreboardManager().getMainScoreboard().getObjective(EDEN_APPLES_SCOREBOARD_NAME).getScore(player.getName());
	}
	
	public static void setEdenApplesEaten(Player player, int value) {
		getEdenScoreboard(player).setScore(value);
	}
	
	public static int getLives(Player player) {
		return getLivesScoreboard(player).getScore();
	}
	
	public static ChatColor getChatColor(int lives) {
		if (lives >= 4) {
			return ChatColor.DARK_GREEN;
		} else if (lives == 3) {
			return ChatColor.GREEN;
		} else if (lives == 2) {
			return ChatColor.YELLOW;
		} else if (lives == 1) {
			return ChatColor.RED;
		} else if (lives <= 0) {
			return ChatColor.GRAY;
		}
		
		return ChatColor.RESET;
	}
	
	public static void removeLife(Player player) {
		Score playerScore = getLivesScoreboard(player);
		playerScore.setScore(playerScore.getScore() - 1);
		
		player.playEffect(EntityEffect.TOTEM_RESURRECT);

		ParticleMaker.createCylinder(Particle.ENCHANT, player.getLocation(), 0.8, 2);
		
		updateTeam(player);
		
		Bukkit.getLogger().info("Removing a life from " + player.getName() + "!");
		Bukkit.getLogger().info(player.getName() + " now has " + playerScore.getScore() + " lives!");
	}
	
	public static void addLife(Player player) {
		Score playerScore = getLivesScoreboard(player);
		playerScore.setScore(playerScore.getScore() + 1);
		
		ParticleMaker.createCylinder(Particle.HAPPY_VILLAGER, player.getLocation(), 0.8, 2);
		
		updateTeam(player, playerScore.getScore() - 1 <= 0);
		
		Bukkit.getLogger().info("Giving a life to " + player.getName() + "!");
		Bukkit.getLogger().info(player.getName() + " now has " + playerScore.getScore() + " lives!");
	}
	
	public static void updateTeam(Player player) {
		updateTeam(player, false);
	}
 	
	public static void updateTeam(Player player, boolean wasDeadFlag) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team playerTeam = scoreboard.getTeam(player.getUniqueId().toString());
		if (playerTeam == null) {
			playerTeam = scoreboard.registerNewTeam(player.getUniqueId().toString());
		}
		
		if (!playerTeam.hasEntry(player.getName())) {
			playerTeam.addEntry(player.getName());
		}
		
		if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
			if (getLives(player) > 0) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setInvisible(false);
				player.setCollidable(true);
				player.setAllowFlight(false);
				playerTeam.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
			} else {
				player.setGameMode(GameMode.ADVENTURE);
				player.setInvisible(true);
				player.setCollidable(false);
				player.setAllowFlight(true);
				playerTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
				Bukkit.getWorlds().get(0).setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, CommandUtil.getAlivePlayersPercentage());
				Bukkit.getLogger().info("New sleep percentage is: " + CommandUtil.getAlivePlayersPercentage());
			}
		}
		
		playerTeam.setColor(getChatColor(getLives(player)));
		
		player.setCustomNameVisible(true);
		player.setCustomName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.WHITE);
		player.setDisplayName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.WHITE);
		player.setPlayerListName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.WHITE);
		
		if (LifeManager.getLives(player) > 0) { // still alive
			PacketSender.sendTeamColorPacket(player, ChatColor.GRAY);
			
			PacketSender.tellEveryonePlayerJoinedOwnTeam(player);
		} else { // dead
			PacketSender.tellEveryonePlayerJoinedTheirTeam(player);
		}
	}

}
