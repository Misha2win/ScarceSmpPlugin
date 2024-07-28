package me.misha2win.scracesmpplugin.command.all.tpa.tpa;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class TpaTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public TpaTabCompleter(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		if (args.length == 1) {
			suggestions = CommandUtil.getAllPlayersStartingWith(args[0]);
		}
		
		return suggestions;
	}

}
