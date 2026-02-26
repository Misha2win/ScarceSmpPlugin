package me.misha2win.scracesmpplugin.command.admin.givescarce;

import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.WorldBackupManager;
import me.misha2win.scracesmpplugin.item.ItemRegistry;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class GiveScarceCommandHandler implements CommandExecutor {
	
	private ScarceLife plugin;
	
	public GiveScarceCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	} 

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		
		if (args.length < 1)
			return false;
		
		Player player = Bukkit.getPlayer(args[1]);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "The player " + args[0] + " does not exist!");
			return true;
		}
		
		Supplier<ItemStack> customItem = ItemRegistry.get(args[1]);
		if (customItem != null) {
			player.getInventory().addItem(customItem.get());
			CommandUtil.logCommand(sender, "gave " + args[1] + " item to " + player.getDisplayName());
			return true;
		}
		
		sender.sendMessage(ChatColor.RED + "Unknown item!");
		return true;
		
		
	}

}
