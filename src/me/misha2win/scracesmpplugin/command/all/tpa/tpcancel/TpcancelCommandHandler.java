package me.misha2win.scracesmpplugin.command.all.tpa.tpcancel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaCommandHandler;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class TpcancelCommandHandler implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public TpcancelCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 0) {
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
			return true;
		}
		
		Player p = (Player) sender;
		
		if (LifeManager.getLives(p) > 0) {
			p.sendMessage(ChatColor.RED + "You must be dead to use this command!");
			return true;
		}
		
		if (!TpaCommandHandler.REQUESTS.containsKey(p)) {
			p.sendMessage(ChatColor.RED + "You do not have any pending teleport requests!");
			return true;
		}
		
		Player p2 = TpaCommandHandler.REQUESTS.get(p);
		p.sendMessage(ChatColor.GREEN + "You have cancelled your teleport request to " + p2.getName() + "!");
		p2.sendMessage(ChatColor.RED + p.getName() + " has cancelled their teleport request to you!");
		TpaCommandHandler.REQUESTS.remove(p);
		
		CommandUtil.logCommand(sender, "cancelled thier teleport request to " + p2.getDisplayName());
		
		return true;
	}

}
