package me.misha2win.scracesmpplugin.command.all.tpa.tpa;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class TpaCommandHandler implements CommandExecutor {

	public static final HashMap<Player, Player> REQUESTS = new HashMap<>(); // teleporter, reciever

	private ScarceLife plugin;

	public TpaCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!this.plugin.getConfig().getBoolean("commands.tpa.enabled")) {
			sender.sendMessage(CommandUtil.Warnings.DISABLED);
			return true;
		}

		// Command must be valid
		if (args.length != 1) {
			return false;
		}

		// Sender must be a Player
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
			return true;
		}

		Player p = (Player) sender;

		if (this.plugin.getConfig().getBoolean("commands.tpa.only-ghosts") && LifeManager.getLives(p) > 0) {
			p.sendMessage(ChatColor.RED + "You must be dead to use this command!");
			return true;
		}

		if (REQUESTS.containsKey(p)) {
			p.sendMessage(ChatColor.RED + "You cannot send multiple teleport requests at once!");
			return true;
		}

		Player p2 = Bukkit.getPlayer(args[0]);
		if (p2 == null) {
			p.sendMessage(ChatColor.RED + "The first argument must be an online player!");
			return true;
		}

		if (p == p2) {
			p.sendMessage(ChatColor.RED + "You cannot send a request to yourself!");
			return true;
		}

		p.sendMessage(ChatColor.GREEN + "Teleport request sent to " + p2.getDisplayName() + ChatColor.GREEN + ".");
		p.sendMessage(ChatColor.GREEN + "They have 60 seconds to accept! Or you can cancel your request with /tpcancel");
		p2.sendMessage(ChatColor.GREEN + p.getName() + " has requested to teleport to you!");
		p2.sendMessage(ChatColor.GREEN + "Type '/tpaccept' to accept or '/tpdeny' to deny their teleport request. If you have multiple requests then specify the name of the person you want to accept or deny.");

		REQUESTS.put(p, p2);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (REQUESTS.containsKey(p)) {
				REQUESTS.remove(p);
				p.sendMessage(ChatColor.RED + "Your teleport request to " + p2.getName() + " has expired!");
				p2.sendMessage(ChatColor.RED + p.getName() + "'s teleport request to you has expired!");
			}
		}, 60 * 20);

		return true;
	}

}
