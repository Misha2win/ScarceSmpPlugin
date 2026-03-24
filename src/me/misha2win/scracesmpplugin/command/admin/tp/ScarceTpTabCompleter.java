package me.misha2win.scracesmpplugin.command.admin.tp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class ScarceTpTabCompleter implements TabCompleter {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public ScarceTpTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();

		if(sender.isOp()) {
			if (args.length == 1) {
				String[] names = Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new);
				suggestions.addAll(CommandUtil.getAllStartingWith(args[0], names));
			}
		}

		return suggestions;
	}

}
