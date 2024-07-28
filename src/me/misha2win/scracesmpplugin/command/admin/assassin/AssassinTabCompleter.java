package me.misha2win.scracesmpplugin.command.admin.assassin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class AssassinTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public AssassinTabCompleter(Main plugin) {
		this.plugin = plugin;
	} 

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		if(sender.isOp()) {
			if (args.length == 1) {
				suggestions.addAll(CommandUtil.getAllStartingWith(args[0], "select", "multi"));
			} else if (args[0].equals("select")) {
				if (args.length == 2) {
					suggestions.addAll(CommandUtil.getAllStartingWith(args[1], "random"));
					suggestions.addAll(CommandUtil.getAllPlayersStartingWith(args[1]));
					Collections.sort(suggestions);
				} else if (args.length == 3) {
					suggestions.addAll(CommandUtil.getAllStartingWith(args[2], "random"));
					suggestions.addAll(CommandUtil.getAllPlayersStartingWith(args[2]));
					Collections.sort(suggestions);
				}
			}
		}
		
		return suggestions;
	}

}
