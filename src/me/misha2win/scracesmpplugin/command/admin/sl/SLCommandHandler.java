package me.misha2win.scracesmpplugin.command.admin.sl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
		} else if (args[0].equals("tp")) {
			World w = Bukkit.getWorld(args[1]);
			if (w != null) {
				((Player) sender).teleport(w.getSpawnLocation());
				CommandUtil.logCommand(sender, "Teleported to " + w.getName());
			} else {
				String str = "";
				for (World world : Bukkit.getWorlds())
					str += world.getName() + " ";
				sender.sendMessage("Worlds are: " + str);
			}
		}
		else {
			return false;
		}

		return true;
	}

}
