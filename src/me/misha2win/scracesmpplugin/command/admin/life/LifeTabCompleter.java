package me.misha2win.scracesmpplugin.command.admin.life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class LifeTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public LifeTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	} 

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		if(sender.isOp()) {
			if (args.length == 1) {
				suggestions.addAll(CommandUtil.getAllStartingWith(args[0], "add", "remove", "set"));
			} else if (args.length == 2) {
				suggestions.addAll(CommandUtil.getAllStartingWith(args[1], "all"));
				suggestions.addAll(CommandUtil.getAllPlayersStartingWith(args[1]));
				Collections.sort(suggestions);
			}
		}
		
		return suggestions;
	}

}
