package me.misha2win.scracesmpplugin.command.all.banishghost;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;

public class BanishGhostCommandHandler implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public BanishGhostCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
		
		if (LifeManager.getLives(p2) > 0) {
			p.sendMessage(ChatColor.RED + "The player you wish to banish must be a ghost!");
			return true;
		}

		if (p.getLocation().distance(p2.getLocation()) > 20) {
			p.sendMessage(ChatColor.RED + "The ghost must be within 20 blocks in order to banish them!");
			return true;
		}
		
		p2.teleport(new Location(p2.getWorld(), 0, 500, 0));
		p.sendMessage(ChatColor.GREEN + "You have banished " + p2.getDisplayName() + ChatColor.GREEN + "!");
		p2.sendMessage(ChatColor.RED + "You have been banished by " + p.getDisplayName() + ChatColor.RED + "! You will fall back onto the ground soon...");
		
		return true;
	}

}
