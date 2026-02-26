package me.misha2win.scracesmpplugin.command.admin.givescarce;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.ItemRegistry;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class GiveScarceTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public GiveScarceTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override 
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		if(sender.isOp())
			if (args.length == 1) { // Arg 1
				suggestions = CommandUtil.getAllStartingWith(args[0], "gamemode", "give", "resetcooldowns", "tp", "createbackup", "gc");
				
			} else if (args.length == 2) { // Arg 2
				if (args[0].equals("gamemode")) {
					suggestions = CommandUtil.getAllStartingWith(args[1], "3rdlife", "lastlife", "doublelife", "limitedlife", "secretlife");
				} else if (args[0].equals("give")) {
					suggestions = CommandUtil.getAllPlayersStartingWith(args[1]);
				} else if (args[0].equals("tp")) {
					for (World w : Bukkit.getWorlds()) {
						suggestions.add(w.getName());
					}
				}
			} else if (args.length == 3) { // Arg 3
				if (args[0].equals("give")) {
					List<String> items = ItemRegistry.getRegisteredItems();
					suggestions = CommandUtil.getAllStartingWith(args[2], items.toArray(new String[items.size()]));
				}
			}
		
		return suggestions;
	}

}
