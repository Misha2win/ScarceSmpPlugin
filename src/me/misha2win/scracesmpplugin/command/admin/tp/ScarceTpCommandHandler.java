package me.misha2win.scracesmpplugin.command.admin.tp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.World.Environment;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class ScarceTpCommandHandler implements CommandExecutor {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public ScarceTpCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Sender must have op
		if (!sender.isOp()) {
			sender.sendMessage(CommandUtil.Warnings.NO_PERMISSION);
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
			return true;
		}
		Player player = (Player) sender;

		// Command must be valid
		if (args.length != 1) {
			return false;
		}

		Location tpLocation = player.getLocation().clone();
		double yPosition = tpLocation.getY();

		World toWorld = Bukkit.getWorld(args[0]);
		if (toWorld == null) {
			sender.sendMessage(String.format("%sThe world '%s' does not exist!", ChatColor.RED + args[0]));
			return true;
		}
		tpLocation.setWorld(toWorld);

		World fromWorld = player.getWorld();
		if (fromWorld.getEnvironment() == Environment.NORMAL && toWorld.getEnvironment() == Environment.NETHER) {
			tpLocation.multiply(0.125);
			tpLocation.setY(yPosition);
		} else if (fromWorld.getEnvironment() == Environment.NETHER && toWorld.getEnvironment() == Environment.NORMAL) {
			tpLocation.multiply(8);
			tpLocation.setY(yPosition);
		}

		player.teleport(tpLocation);
		CommandUtil.logCommand(sender, String.format("Teleported %s%s%%s to '%s'", player.getDisplayName(), ChatColor.GRAY, args[0]));

		return true;
	}

}
