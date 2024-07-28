package me.misha2win.scracesmpplugin.command.admin.sl;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.WorldBackupManager;
import me.misha2win.scracesmpplugin.recipe.ItemManager;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class SLCommandHandler implements CommandExecutor {
	
	private Main plugin;
	
	public SLCommandHandler(Main plugin) {
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
		
		if (args[0].equals("gamemode")) {
			if ("l".startsWith("l")) {
				sender.sendMessage(ChatColor.RED + "The gamemode subcommand does not work!");
				return true;
			}
			
			if (args.length < 2)
				return false;
				
			if (args[1].equals("3rdlife")) {
				// 3 Lives
				sender.sendMessage(ChatColor.RED + "This is already the gamemode!");
				CommandUtil.logCommand(sender, "set the live mode to 3rdlife");
			} else if (args[1].equals("lastlife")) {
				// 2-6 Lives
				sender.sendMessage(ChatColor.RED + "This gamemode is not available yet!");
				CommandUtil.logCommand(sender, "set the live mode to lastlife");
				return true;
			} else if (args[1].equals("doublelife")) {
				// 3 Lives + Soulmates
				sender.sendMessage(ChatColor.RED + "This gamemode is not available yet!");
				CommandUtil.logCommand(sender, "set the live mode to doublelife");
				return true;
			} else if (args[1].equals("limitedlife")) {
				// 24 Hours + Death means lose an hour
				sender.sendMessage(ChatColor.RED + "This gamemode is not available yet!");
				CommandUtil.logCommand(sender, "set the live mode to limitedlife");
				return true;
			} else if (args[1].equals("secretlife")) {
				// 3 Lives + Secret tasks
				sender.sendMessage(ChatColor.RED + "This gamemode is not available yet!");
				CommandUtil.logCommand(sender, "set the live mode to secretlife");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid arguments!");
				return true;
			}
			
			Bukkit.broadcastMessage("Gamemode changed to " + ChatColor.GREEN + args[1] + ChatColor.WHITE + "!");
		} else if (args[0].equals("give")) {
			if (args.length < 2)
				return false;
			
			Player player = Bukkit.getPlayer(args[1]);
			if (player != null) {
				Class<?> clazz = ItemManager.class;
				for (Field field : clazz.getFields()) {
					if (field.getName().toLowerCase().equals(args[2])) {
						try {
							player.getInventory().addItem((ItemStack) field.get(ItemManager.class));
							CommandUtil.logCommand(sender, "gave " + args[2] + " item to " + player.getDisplayName());
							return true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				sender.sendMessage(ChatColor.RED + "Unknown item!");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "The player " + args[1] + " does not exist!");
				return true;
			}
		} else if (args[0].equals("resetcooldowns")) {
			if (sender instanceof Player) {
				for (Material mat : Material.values()) {
					if (mat.isItem())
						((Player) sender).setCooldown(mat, 0);
				}
				CommandUtil.logCommand(sender, "reset their item cooldowns");
			}
		}
		else if (args[0].equals("createbackup")) {
			CommandUtil.logCommand(sender, "started creating a world backup");
			WorldBackupManager.start(plugin); // XXX
		}
		else if (args[0].equals("test")) {
			plugin.getWetSeasonHandler().startSeason();
		} else if (args[0].equals("tp")) {
			World w = Bukkit.getWorld(args[1]);
			if (w != null) {
				((Player) sender).teleport(w.getSpawnLocation());
				CommandUtil.logCommand(sender, "teleported to " + w.getName());
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
