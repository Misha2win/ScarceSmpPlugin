package me.misha2win.scracesmpplugin.command.all.givelife;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class GiveLifeCommandHandler implements CommandExecutor {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public GiveLifeCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!this.plugin.getConfig().getBoolean("commands.givelife.enabled")) {
			sender.sendMessage(CommandUtil.Warnings.DISABLED);
			return true;
		}

		// Command must be valid
		if (args.length != 1) {
			return false;
		}

		// Sender must be a Player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only a player can use this command!");

			return true;
		}
		Player p = (Player)sender;

		// Receiving player must exist
		if (Bukkit.getPlayer(args[0]) == null) {
			p.sendMessage(ChatColor.RED + "The first argument must be an online player!");
			return true;
		}

		Player p2 = Bukkit.getPlayer(args[0]);

		// If sender is in creative then give life to revceiver without subtracting from sender
		if (p.getGameMode() == GameMode.CREATIVE) {
			p.sendMessage(ChatColor.GREEN + "You have given a life to " + p2.getName() + "!");

			LifeManager.addLife(p2);
			p2.sendMessage(ChatColor.GREEN + "You have received a live!");

			return true;
		}

		// Receiving player must not be sender
		if (p == p2) {
			p.sendMessage(ChatColor.RED + "You cannot give yourself a life!");

			return true;
		}

		// Player must be in survival
		if (p.getGameMode() != GameMode.SURVIVAL) {
			p.sendMessage(ChatColor.RED + "You must be in survival to give one of your lives!");

			return true;
		}

		// Sending player must have at least one life to give
		if (LifeManager.getLivesScoreboard(p).getScore() <= 1) {
			p.sendMessage(ChatColor.RED + "You must have at least one life to give!");

			return true;
		}

		// Sending player must not have 2 lives
		if (LifeManager.getLivesScoreboard(p).getScore() == 2) {
			p.sendMessage(ChatColor.RED + "You cannot make yourself have 1 life with this command!");

			return true;
		}

		// Receiving player must not have be given more lives than the max
		int maxLives = this.plugin.getConfig().getInt("lives.max");
		if (maxLives > 0 && LifeManager.getLivesScoreboard(p2).getScore() >= maxLives) {
			p.sendMessage(ChatColor.RED + "You cannot give a life to this player! They already have " + maxLives + " or more lives!");

			return true;
		}


		// Receiving player must be alive
		if (LifeManager.getLivesScoreboard(p2).getScore() <= 0) {
			p.sendMessage(ChatColor.RED + "The player must be alive!");

			return true;
		}

		LifeManager.removeLife(p, true);
		LifeManager.addLife(p2);

		p.sendMessage(ChatColor.GREEN + "You have gave one of your lives to " + p2.getName() + "!");
		p2.sendMessage(ChatColor.GREEN + "You have received a live from " + p.getName() + "!");

		Bukkit.getLogger().info(p.getName() + " gave one of their lives to " + p2.getName() + "!");
		Bukkit.getLogger().info(p.getName() + " now has " + LifeManager.getLivesScoreboard(p).getScore() + " lives!");
		Bukkit.getLogger().info(p2.getName() + " now has " + LifeManager.getLivesScoreboard(p2).getScore() + " lives!");

		return true;
	}

}
