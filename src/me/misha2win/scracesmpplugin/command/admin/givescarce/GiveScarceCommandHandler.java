package me.misha2win.scracesmpplugin.command.admin.givescarce;

import java.util.HashMap;
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
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class GiveScarceCommandHandler implements CommandExecutor {

	private ScarceLife plugin;

	public GiveScarceCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(CommandUtil.Warnings.NO_PERMISSION);
			return true;
		}

		if (args.length < 2)
			return false;

		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "The player " + args[0] + " does not exist!");
			return true;
		}

		Supplier<ItemStack> customItem = ItemRegistry.get(args[1]);
		if (customItem == null) {
			if (ItemRegistry.has(args[1])) {
				sender.sendMessage(ChatColor.RED + "This item cannot be obtained with the give command!");
				return true;
			}

			sender.sendMessage(ChatColor.RED + "Unknown item!");
			return true;
		}

		int count = 1;
		if (args.length == 3) {
			try {
				count = Integer.valueOf(args[2]);
				if (count <= 0) return true;
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + args[2] + " is not a valid number!");
				return true;
			}
		}

		for (int i = 0; i < count; i++) {
			HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(customItem.get());
			for (Integer item : leftOver.keySet()) { // TODO: Finish me

			}
			CommandUtil.logCommand(sender, "Gave " + i + " " + args[1] + " item to " + player.getDisplayName());
		}
		CommandUtil.logCommand(sender, "Gave " + count + " " + args[1] + " item to " + player.getDisplayName());
		return true;
	}

}
