package me.misha2win.scracesmpplugin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import me.misha2win.scracesmpplugin.LifeManager;

public class CommandUtil {

	public static class Warnings {
		public static final String MUST_BE_PLAYER = ChatColor.RED + "You must be a player to use this command!";
		public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command!";
		public static final String DISABLED = ChatColor.RED + "This command has been disabled in the config!";
	}

	public static String getRandom(String... strings) {
		return strings[(int) (Math.random() * strings.length)];
	}

	public static String[] getSelectors() {
		return new String[] { "@a", "@p", "@r", "@s", "@dead", "@alive" };
	}

	public static List<Player> getPlayersFromSelector(CommandSender sender, String selector) {
		List<Player> players = new LinkedList<Player>();

		if (selector.equals("@a")) {
			for (Player player : Bukkit.getOnlinePlayers()) players.add(player);
		} else if (selector.equals("@p")) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else if (sender instanceof BlockCommandSender) {
				Location origin = ((BlockCommandSender) sender).getBlock().getLocation();

				Player closest = null;
				double closestDistSq = Double.MAX_VALUE;

				for (Player player : origin.getWorld().getPlayers()) {
					double distSq = player.getLocation().distanceSquared(origin);
					if (distSq < closestDistSq) {
						closestDistSq = distSq;
						closest = player;
					}
				}

				if (closest != null) players.add(closest);
			}
		} else if (selector.equals("@r")) {
			players.add(getRandomPlayer());
		} else if (selector.equals("@s")) {
			players.add((Player) sender);
		} else if (selector.equals("@dead")) {
			for (Player player : getDeadPlayers()) players.add(player);
		} else if (selector.equals("@alive")) {
			for (Player player : getAlivePlayers()) players.add(player);
		}

		return players;
	}

	public static Player getRandomPlayer() {
		List<Player> players = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			players.add(player);
		}
		Collections.shuffle(players);

		if (players.size() > 0)
			return players.get(0);
		else
			return null;
	}

	public static Player[] getDeadPlayers() {
		LinkedList<Player> playerList = new LinkedList<Player>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (LifeManager.getLives(player) <= 0) {
				playerList.add(player);
			}
		}

		return playerList.toArray(new Player[playerList.size()]);
	}

	public static Player[] getAlivePlayers() {
		LinkedList<Player> playerList = new LinkedList<Player>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (LifeManager.getLives(player) > 0) {
				playerList.add(player);
			}
		}

		return playerList.toArray(new Player[playerList.size()]);
	}

	public static Player getRandomPlayerLifeWeighted(Player... exclude) {
		List<Player> players = new ArrayList<>();

		outer:
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Player excludedPlayer : exclude) {
				if (player == excludedPlayer) {
					continue outer;
				}
			}

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

	public static ArrayList<String> getAllPlayersStartingWithExcludingSender(CommandSender sender, String name) {
		ArrayList<String> names = new ArrayList<>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().toLowerCase().startsWith(name.toLowerCase())) {
				if (!p.getName().equals(sender.getName())) {
					names.add(p.getName());
				}
			}
		}
		Collections.sort(names);

		return names;
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

	public static void messageAllPlayers(String message, Player... exclude) {
		outer:
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player excluded : exclude) {
				if (p == excluded) {
					continue outer;
				}
			}
			p.sendMessage(message);
		}
	}

	public static void logCommand(CommandSender sender, String message) {
		String grayedMessage = String.format(message, ChatColor.GRAY);
		String senderName = (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
		String formatted = String.format("%1$s[%2$s%1$s: %3$s%1$s]", ChatColor.GRAY, senderName, grayedMessage);

		sender.sendMessage(String.format(message, ChatColor.WHITE));
		messageAllOpedPlayers(sender, formatted);
		Bukkit.getLogger().info(ChatColor.stripColor(formatted));
	}

	public static void messageAllOpedPlayers(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.isOp()) p.sendMessage(message);
		}
	}

	public static void messageAllOpedPlayers(CommandSender sender, String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p == sender) continue;
			if (p.isOp()) p.sendMessage(message);
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

	public static Location randomPointInsideWorldBorder(World world) {
		return randomPointInsideWorldBorder(world, 0, 0);
	}

	public static Location randomPointInsideWorldBorder(World world, int xPadding, int zPadding) {
		WorldBorder border = world.getWorldBorder();
		Location center = border.getCenter();

		double radius = border.getSize() / 2.0;

		//Keep a small margin so you don't land exactly on the border edge
		double margin = 2.0;
		double minX = center.getX() - radius + margin + xPadding;
		double maxX = center.getX() + radius - margin - xPadding;
		double minZ = center.getZ() - radius + margin + zPadding;
		double maxZ = center.getZ() + radius - margin - zPadding;

		Random random = new Random();
		double x = minX + (random.nextDouble() * (maxX - minX));
		double z = minZ + (random.nextDouble() * (maxZ - minZ));

		// Pick a safe-ish Y on the surface
		int highestY = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z));
		double y = highestY + 1.0;

		return new Location(world, x + 0.5, y, z + 0.5);
	}

	public static String getWorldName(Location location) {
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

	public static String locationToString(int x, int y, int z, String name) {
		return String.format(
				"%5$s%1$d %2$d %3$d %6$sin the %5$s%4$s%6$s",
				x, y, z, name,
				ChatColor.GREEN, ChatColor.WHITE
			);
	}

	public static String locationToString(Location location) {
		return locationToString(
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ(),
				getWorldName(location)
		);
	}

	public static void setFooter(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setPlayerListFooter(message);
		}
	}

	public static void setHeader(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setPlayerListHeader(message);
		}
	}

}