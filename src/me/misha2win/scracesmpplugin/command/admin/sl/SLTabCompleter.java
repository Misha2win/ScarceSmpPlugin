package me.misha2win.scracesmpplugin.command.admin.sl;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class SLTabCompleter implements TabCompleter {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public SLTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();

		if(sender.isOp())
			if (args.length == 1) { // Arg 1
				suggestions = CommandUtil.getAllStartingWith(args[0], "resetcooldowns", "tp");
			} else if (args.length == 2) { // Arg 2
				if (args[0].equals("tp")) {
					String[] names = Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new);
					suggestions.addAll(CommandUtil.getAllStartingWith(args[1], names));
				}
			}

		return suggestions;
	}

}
