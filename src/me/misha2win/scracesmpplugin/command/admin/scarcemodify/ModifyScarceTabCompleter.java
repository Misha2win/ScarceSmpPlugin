package me.misha2win.scracesmpplugin.command.admin.scarcemodify;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class ModifyScarceTabCompleter implements TabCompleter {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public ModifyScarceTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();

		if (!sender.isOp()) return suggestions;
		if (!(sender instanceof Player)) return suggestions;
		Player player = (Player) sender;

		if (args.length == 1) { // Arg 1
			suggestions.addAll(CommandUtil.getAllStartingWith(args[0], "get", "set", "list"));
		} else if (args.length == 2) { // Arg 2
			List<NamespacedKey> keys = ItemUtil.getScarceKeys(player.getInventory().getItemInMainHand().getItemMeta());
			for (NamespacedKey key : keys) {
				String keyString = key.toString();
				if (!keyString.contains(args[1])) continue;

				suggestions.add(keyString);
			}
		}

		return suggestions;
	}

}
