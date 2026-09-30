package me.misha2win.scracesmpplugin.command.all.item;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class ItemTabCompleter implements TabCompleter {

	public ItemTabCompleter(ScarceLife plugin) {
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