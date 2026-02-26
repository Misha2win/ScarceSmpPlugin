package me.misha2win.scracesmpplugin.command.all.tpa.tpcancel;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.misha2win.scracesmpplugin.ScarceLife;

public class TpcancelTabCompleter implements TabCompleter {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public TpcancelTabCompleter(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> suggestions = new ArrayList<>();
		
		return suggestions;
	}

}
