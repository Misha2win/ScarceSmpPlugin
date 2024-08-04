package me.misha2win.scracesmpplugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.misha2win.scracesmpplugin.LifeManager;

public class CommandUtil {
	
	public static class Warnings {
		public static final String MUST_BE_PLAYER = ChatColor.RED + "You must be a player to use this command!";
	}
	
	public static Player getRandomPlayer(Player... exclude) {
		List<Player> players = new ArrayList<>();
		
		for (Player player : players) {
			if (LifeManager.getLives(player) >= 1) {
				players.add(player);
			}
		}
		Collections.shuffle(players);
		
		if (players.size() > 0)
			return players.get(0);
		else
			return null;
	}
	
	public static Player getRandomPlayerWeighted(Player... exclude) {
		List<Player> players = new ArrayList<>();
		
		for (Player player : players) {
			for (int i = 0; i < LifeManager.getLives(player); i++) {
				players.add(player);
			}
		}
		Collections.shuffle(players);
		
		if (players.size() > 0)
			return players.get(0);
		else
			return null;
	}
	
	public static ArrayList<String> getAllPlayersStartingWith(String name) {
		ArrayList<String> names = new ArrayList<>();
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().toLowerCase().startsWith(name.toLowerCase())) {
				names.add(p.getName());
			}
		}
		Collections.sort(names);
		
		return names;
	}
	
	public static ArrayList<String> getAllStartingWith(String args, String... commands) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		for (String command : commands) {
			if (command.startsWith(args)) {
				suggestions.add(command);
			}
		}
		
		Collections.sort(suggestions);
		
		return suggestions;
	}
	
	public static void messageAllPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(message);
		}
	}
	
	public static void logCommand(CommandSender sender, String message) {
		if (sender instanceof Player) {
			messageAllOpedPlayers(sender, ChatColor.GRAY + "[" + ((Player) sender).getDisplayName() + ChatColor.GRAY + ": " + message + ChatColor.GRAY + "]");
		} else {
			messageAllOpedPlayers(sender, ChatColor.GRAY + "[" + sender.getName() + ChatColor.GRAY + ": " + message + ChatColor.GRAY + "]");
		}
	}
	
	public static void messageAllOpedPlayers(CommandSender sender, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p == sender) {
				continue;
			}
			
			if (p.isOp()) {
				p.sendMessage(message);
			}
		}
	}
	
	public static Team getTeamOfPlayer(Player player) {
		for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
			if (team.hasEntry(player.getName())) {
				return team;
			}
		}
		
		return null;
	}
	
	public static int getAlivePlayersPercentage() {
		int alivePlayers = 0;
		int onlinePlayers = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			onlinePlayers++;
			if (LifeManager.getLives(p) > 0)  {
				alivePlayers++;
			}
		}
		
		return (int)(((double) alivePlayers / onlinePlayers) * 33);
	}

}