package me.misha2win.scracesmpplugin.command.all.givelife;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class GiveLifeTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public GiveLifeTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		if (args.length == 1) { // FIXME
			suggestions = CommandUtil.getAllPlayersStartingWith(args[0]);
		} 
		
		return suggestions;
	}

}
