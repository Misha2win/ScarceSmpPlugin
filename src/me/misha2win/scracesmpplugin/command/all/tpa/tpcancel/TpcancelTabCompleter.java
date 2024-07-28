package me.misha2win.scracesmpplugin.command.all.tpa.tpcancel;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.Main;

public class TpcancelTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public TpcancelTabCompleter(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		return suggestions;
	}

}
