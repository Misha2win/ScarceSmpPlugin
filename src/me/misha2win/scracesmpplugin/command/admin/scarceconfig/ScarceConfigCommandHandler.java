package me.misha2win.scracesmpplugin.command.admin.scarceconfig;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class ScarceConfigCommandHandler implements CommandExecutor {

	private ScarceLife plugin;

	public ScarceConfigCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(CommandUtil.Warnings.NO_PERMISSION);
			return true;
		}

		if (args.length < 2) return false;

		FileConfiguration config = this.plugin.getConfig();

		if (args[0].equals("get")) {
			if (!config.contains(args[1])) {
				sender.sendMessage(ChatColor.RED + "The config does not contain '" + args[1] + "'");
				return true;
			}

			sender.sendMessage(String.format("%s: %s", args[1], config.get(args[1])));
		} else if (args[0].equals("set")) {
			onSet(sender, args, config);
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid subcommand.");
			return true;
		}

		return true;
	}

	private boolean onSet(CommandSender sender, String[] args, FileConfiguration config) {
		if (args.length < 3) return false;

		if (!config.contains(args[1])) {
			sender.sendMessage(ChatColor.RED + "The config does not contain '" + args[1] + "'");
			return true;
		}

		if (config.isBoolean(args[1])) {
			if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
				sender.sendMessage(ChatColor.RED + "You must provide true/false for '" + args[1] + "'");
				return true;
			}

			config.set(args[1], Boolean.parseBoolean(args[2]));
		} else if (config.isInt(args[1])) {
			try {
				config.set(args[1], Integer.parseInt(args[2]));
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "You must provide an integer for '" + args[1] + "'");
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Unsupported type for '" + args[1] + "'.");
			return true;
		}

		plugin.saveConfig();
		CommandUtil.logCommand(sender, String.format("Set config '%s' to '%s'", args[1], args[2]));

		return true;
	}

}
