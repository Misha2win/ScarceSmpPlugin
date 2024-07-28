package me.misha2win.scracesmpplugin.command.admin.assassin;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class AssassinCommandHandler implements CommandExecutor {
	
	private Main plugin;
	
	public AssassinCommandHandler(Main plugin) {
		this.plugin = plugin;
	} 

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Sender must have op
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		
		if (args[0].equals("select")) {
			if (args.length < 2) {
				return false;
			}
			
			Player p1 = Bukkit.getPlayer(args[1]);
			if (p1 == null) {
				if (args[1].equals("random")) {
					p1 = CommandUtil.getRandomPlayer();
				} else {
					sender.sendMessage(ChatColor.RED + "Player " + args[1] + " does not exist!");
					return false;
				}
			}
			
			Player p2 = null;
			if (args.length == 3) {
				p2 = Bukkit.getPlayer(args[2]);
				
				if (p2 == null) {
					if (args[2].equals("random")) {
						p2 = CommandUtil.getRandomPlayer(p1);
					} else {
						sender.sendMessage(ChatColor.RED + "Player " + args[2] + " does not exist!");
						return false;
					}
				}
			} else if (args[1].equals("random")) {
				p2 = CommandUtil.getRandomPlayer(p1);
			} else {
				return false;
			}
			
			if (p1 == null || p2 == null) {
				sender.sendMessage(ChatColor.RED + "There aren't enough online players!");
				return true;
			}
			
			HashMap<Player, Player> assassins = new HashMap<>();
			assassins.put(p1, p2);
			plugin.getAssassinHandler().startEvent(assassins);
		} else if (args[0].equals("multi")) {
			sender.sendMessage(ChatColor.RED + "This subcommand does not work yet!");
		} else {
			return false;
		}
	
		
		return true;
	}

}
