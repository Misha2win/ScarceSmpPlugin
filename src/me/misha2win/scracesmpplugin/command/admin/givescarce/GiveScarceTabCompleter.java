package me.misha2win.scracesmpplugin.command.admin.givescarce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
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

		if(!sender.isOp()) return suggestions;

		Bukkit.getLogger().info(Arrays.toString(args));

		if (args.length == 1) { // Arg 1
			suggestions.addAll(CommandUtil.getAllStartingWith(args[0], CommandUtil.getSelectors()));
			suggestions.addAll(CommandUtil.getAllPlayersStartingWith(args[0]));
		} else if (args.length == 2) { // Arg 2
			List<String> items = ItemRegistry.getRegisteredItems();
			suggestions = CommandUtil.getAllStartingWith(args[1], items.toArray(new String[items.size()]));
		}

		return suggestions;
	}

}
