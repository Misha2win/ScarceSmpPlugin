package me.misha2win.scracesmpplugin.command.all.tpa.tpaccept;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaCommandHandler;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class TpacceptCommandHandler implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public TpacceptCommandHandler(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 1) {
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
			return true;
		}
		
		Player p = (Player) sender;
		
		if (!TpaCommandHandler.pendingRequests.containsValue(p)) {
			p.sendMessage(ChatColor.RED + "You do not have any pending teleport requests!");
			return true;
		}
		
		int numRequests = 0;
		Player p2 = null;
		for (Player value : TpaCommandHandler.pendingRequests.keySet()) {
			if (TpaCommandHandler.pendingRequests.get(value).equals(p)) {
				if (p2 != null) {
					if (args.length != 1) {
						p.sendMessage(ChatColor.RED + "You have too many requests!");
						p.sendMessage(ChatColor.RED + "You need to specify whose request to accept!");
						return true;
					}
				}
				p2 = value;
				numRequests++;
			}
		}
		
		if (numRequests > 1) {
			p2 = Bukkit.getPlayer(args[0]);
			if (p2 == null) {
				p.sendMessage(ChatColor.RED + args[0] + " is not a player!");
				return true;
			}
		}
		
		p2.sendMessage(ChatColor.GREEN + p.getName() + " has accepted your request!");
		p2.sendMessage(ChatColor.GREEN + "Teleporting you to " + p.getName() + "!");
		p.sendMessage(ChatColor.GREEN + "Teleporting " + p2.getName() + " to you!");
		TpaCommandHandler.pendingRequests.remove(p2);
		p2.teleport(p);
		
		CommandUtil.logCommand(sender, "accepted " + p2.getDisplayName() + ChatColor.GRAY + "'s teleport request");
		
		return true;
	}

}
