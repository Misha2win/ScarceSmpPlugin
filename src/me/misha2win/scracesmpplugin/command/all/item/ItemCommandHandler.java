package me.misha2win.scracesmpplugin.command.all.item;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class ItemCommandHandler implements CommandExecutor {

	@SuppressWarnings("unused")
	private ScarceLife plugin;

	public ItemCommandHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Sender must be a Player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only a player can use this command!");

			return true;
		}

		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item == null || item.getType().isAir()) {
			player.sendMessage(ChatColor.RED + "You must be holding an item in your main hand!");
			return true;
		}

		PacketSender.sendItemTooltipChat(player);

		return true;
	}

}
