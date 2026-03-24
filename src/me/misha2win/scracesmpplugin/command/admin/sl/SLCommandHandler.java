package me.misha2win.scracesmpplugin.command.admin.sl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class SLCommandHandler implements CommandExecutor {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public SLCommandHandler(ScarceLife plugin) {
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

		if (args[0].equals("resetcooldowns")) {
			if (sender instanceof Player) {
				for (Material mat : Material.values()) {
					if (mat.isItem())
						((Player) sender).setCooldown(mat, 0);
				}
				CommandUtil.logCommand(sender, "Reset their item cooldowns");
			}
		} else if (args[0].equals("placestronghold")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
				return true;
			}
			Player player = (Player) sender;

			FileConfiguration config = plugin.getConfig();

			if (!config.getBoolean("stronghold.enabled")) {
				sender.sendMessage(ChatColor.RED + "Custom strongholds are disabled!");
				return true;
			}

			if (config.getBoolean("stronghold.placed")) {
				sender.sendMessage(ChatColor.RED + "The custom stronghold has already been placed!");
				return true;
			}

			Location strongholdLocation = CommandUtil.randomPointInsideWorldBorder(Bukkit.getWorlds().get(0), 100, 100);
			strongholdLocation.setY(strongholdLocation.getY() / 2);

			GameMode prevGameMode = player.getGameMode();
			Location prevLocation = player.getLocation().clone();

			player.setGameMode(GameMode.CREATIVE);
			player.teleport(strongholdLocation);

			String strongholdString = getString(strongholdLocation);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				Bukkit.dispatchCommand(sender, "place structure minecraft:stronghold " + strongholdString);

				player.setGameMode(prevGameMode);
				player.teleport(prevLocation);

				sender.sendMessage(ChatColor.RED + "Stronghold location was set to " + strongholdString + " but you should still change the config to reflect a better stronghold location!");
			}, 20 * 5);

			config.set("stronghold.placed", true);
			config.set("stronghold.location.x", strongholdLocation.getBlockX());
			config.set("stronghold.location.y", strongholdLocation.getBlockY());
			config.set("stronghold.location.z", strongholdLocation.getBlockZ());
			plugin.saveConfig();
		}
		else {
			return false;
		}

		return true;
	}

	public String getString(Location location) {
		return location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
	}

}
