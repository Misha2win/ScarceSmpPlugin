package me.misha2win.scracesmpplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.misha2win.scracesmpplugin.handler.DeadPlayerHandler;
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
		removeLife(player, false);
		
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
	
	public static void removeLife(Player player, boolean doTotem) {
		Score playerScore = getLivesScoreboard(player);
		playerScore.setScore(playerScore.getScore() - 1);
		
		if (doTotem)
			player.playEffect(EntityEffect.TOTEM_RESURRECT);

		ParticleMaker.createDome(Particle.ENCHANT, player.getLocation(), 1, 50, 1);
		
		updateTeam(player);
		
		Bukkit.getLogger().info("Removing a life from " + player.getName() + "!");
		Bukkit.getLogger().info(player.getName() + " now has " + playerScore.getScore() + " lives!");
	}
	
	public static void addLife(Player player) {
		Score playerScore = getLivesScoreboard(player);
		playerScore.setScore(playerScore.getScore() + 1);
		
		ParticleMaker.createCylinder(Particle.HAPPY_VILLAGER, player.getLocation(), 0.8, 2);
		
		updateTeam(player);
		
		Bukkit.getLogger().info("Giving a life to " + player.getName() + "!");
		Bukkit.getLogger().info(player.getName() + " now has " + playerScore.getScore() + " lives!");
	}
 	
	public static void updateTeam(Player player) {
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
				player.setSleepingIgnored(false);
				player.setAllowFlight(false);
				player.setSilent(false);
				player.setInvulnerable(false);
				
				// Make sure no wardens are allied with the player
				for (World world : Bukkit.getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof Warden) {
							CommandUtil.getTeamOfPlayer(player).removeEntry(entity.getUniqueId().toString());
						}
					}
				}
			} else {
				player.setGameMode(GameMode.ADVENTURE);
				player.setInvisible(true);
				player.setSleepingIgnored(true);
				player.setAllowFlight(true);
				player.setSilent(true);
				
				// Make sure all wardens are allied with the player so that wardens won't attack ghosts
				for (World world : Bukkit.getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof Warden) {
							DeadPlayerHandler.clearWardenAnger((Warden) entity, player);
						}
					}
				}
			}
		}
		
		playerTeam.setColor(getChatColor(getLives(player)));
		
		player.setCustomNameVisible(true);
		player.setCustomName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.RESET);
		player.setDisplayName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.RESET);
		player.setPlayerListName(LifeManager.getChatColor(LifeManager.getLives(player)) + player.getName() + ChatColor.RESET);
		
		if (LifeManager.getLives(player) > 0) { // still alive
			PacketSender.tellEveryonePlayerJoinedOwnTeam(player);
		} else { // dead
			PacketSender.tellEveryonePlayerJoinedTheirTeam(player);
		}
		
		PacketSender.sendTeamColorPacket(player, ChatColor.GRAY);
	}

}
