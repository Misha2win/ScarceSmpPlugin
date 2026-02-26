package me.misha2win.scracesmpplugin.command.all.tpa.tpdeny;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaCommandHandler;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class TpdenyCommandHandler implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public TpdenyCommandHandler(ScarceLife plugin) {
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
		
		if (!TpaCommandHandler.REQUESTS.containsValue(p)) {
			p.sendMessage(ChatColor.RED + "You do not have any pending teleport requests!");
			return true;
		}
		
		int numRequests = 0;
		Player p2 = null;
		for (Player value : TpaCommandHandler.REQUESTS.keySet()) {
			if (TpaCommandHandler.REQUESTS.get(value).equals(p)) {
				if (p2 != null) {
					if (args.length != 1) {
						p.sendMessage(ChatColor.RED + "You have too many requests!");
						p.sendMessage(ChatColor.RED + "You need to specify whose request to deny!");
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
		
		p2.sendMessage(ChatColor.RED + p.getName() + " has denied your request!");
		p.sendMessage(ChatColor.GREEN + "You denied " + p2.getName() + "'s teleport request!");
		TpaCommandHandler.REQUESTS.remove(p2);
		
		CommandUtil.logCommand(sender, "denied " + p2.getDisplayName() + ChatColor.GRAY + "'s teleport request");
		
		return true;
	}

}
