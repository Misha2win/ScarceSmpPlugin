package me.misha2win.scracesmpplugin.command.admin.scarceconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class ScarceConfigTabCompleter implements TabCompleter {

	private ScarceLife plugin;

	public ScarceConfigTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();

		if(!sender.isOp()) {
			return suggestions;
		}

		if (args.length == 1) { // Arg 1
			suggestions.addAll(CommandUtil.getAllStartingWith(args[0], "get", "set"));
		} else if (args.length == 2) { // Arg 2
			if (args[0].equals("get") || args[0].equals("set")) {
				Set<String> keys = plugin.getConfig().getKeys(true);
				suggestions.addAll(CommandUtil.getAllStartingWith(args[1], keys.toArray(new String[keys.size()])));
			}
		}

		return suggestions;
	}

}
