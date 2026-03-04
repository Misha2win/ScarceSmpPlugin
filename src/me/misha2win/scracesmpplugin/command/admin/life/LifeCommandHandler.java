package me.misha2win.scracesmpplugin.command.admin.life;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class LifeCommandHandler implements CommandExecutor {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public LifeCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Sender must have op
		if (!sender.isOp()) {
			sender.sendMessage(CommandUtil.Warnings.NO_PERMISSION);
			return true;
		}

		// Command must be valid
		if (args.length != 2 && args.length != 3) {
			return false;
		}

		int lives = 0;
		try {
			if (args.length == 2) lives = Integer.valueOf(args[1]);
			else lives = Integer.valueOf(args[2]);
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + "You must provide a valid integer!");
			return true;
		}

		if (lives < 1) {
			sender.sendMessage(ChatColor.RED + "You cannot give less than 1 life!");
			return true;
		}

		LinkedList<Player> players = new LinkedList<>();
		if (args.length == 2 && sender instanceof Player) {
			players.add((Player) sender);
		} else if (args.length == 3) { // Selector provided
			if (args[1].startsWith("@")) {
				players.addAll(CommandUtil.getPlayersFromSelector(sender, args[1]));
			} else {
				players.add(Bukkit.getPlayer(args[1]));
			}
		}

		if (players.size() <= 0) {
			sender.sendMessage(ChatColor.RED + "You must provide an online player or selector!");
			return true;
		}

		for (Player player : players) {
			applyLifeAction(args[0], player, lives);
		}

		if (players.size() == 1) {
			CommandUtil.logCommand(sender, String.format("Set %s%s%%s's lives to %d", players.get(0).getDisplayName(), ChatColor.GRAY, lives));
		} else {
			CommandUtil.logCommand(sender, String.format("Set %s player's lives to %d", players.size(), lives));
		}

		return true;
	}

	public void applyLifeAction(String action, Player player, int lives) {
		if (action.equals("add")) {
			addLives(player, lives);
		} else if (action.equals("remove")) {
			removeLives(player, lives);
		} else if (action.equals("set")) {
			setLives(player, lives);
		}
	}

	public void addLives(Player player, int numOfLives) {
		setLives(player, LifeManager.getLivesScoreboard(player).getScore() + numOfLives);

		if (numOfLives == 1)
			player.sendMessage(ChatColor.GREEN + "You have received a live!");
		else
			player.sendMessage(ChatColor.GREEN + "You have received " + numOfLives + " lives!");

	}

	public void removeLives(Player player, int numOfLives) {
		setLives(player, LifeManager.getLivesScoreboard(player).getScore() - numOfLives);

		if (numOfLives == 1)
			player.sendMessage(ChatColor.RED + "One life has been taking from you!");
		else
			player.sendMessage(ChatColor.RED + (numOfLives + " lives have been taken from you!"));

	}

	public void setLives(Player player, int numOfLives) {
		Score playerScore = LifeManager.getLivesScoreboard(player);

		if (playerScore.getScore() > numOfLives) {
			playerScore.setScore(numOfLives + 1);
			LifeManager.removeLife(player, playerScore.getScore() > 1);
		} else if (playerScore.getScore() < numOfLives) {
			playerScore.setScore(numOfLives - 1);
			LifeManager.addLife(player);
		} else {
			// Nothing changed!
		}
	}

}
