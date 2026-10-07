package me.misha2win.scracesmpplugin.command.admin.scarcemodify;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class ModifyScarceCommandHandler implements CommandExecutor {

	public ModifyScarceCommandHandler(ScarceLife plugin) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(CommandUtil.Warnings.NO_PERMISSION);
			return true;
		}

		// modify get key value

		if (args.length < 1)
			return false;

		if (!(sender instanceof Player)) {
			sender.sendMessage(CommandUtil.Warnings.MUST_BE_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item == null || item.getType().isAir()) {
			sender.sendMessage(ChatColor.RED + "You must be holding an item!");
			return true;
		}

		if (!args[0].equals("get") && !args[0].equals("set") && !args[0].equals("list")) {
			sender.sendMessage(ChatColor.RED + "Unknown subcommand! Use 'get', 'set' or 'list'.");
			return true;
		}

		if (args[0].equals("get")) {
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "You must provide a key to get the value for!");
				return true;
			}

			NamespacedKey key = NamespacedKey.fromString(args[1]);
			if (!ItemUtil.getScarceKeys(item.getItemMeta()).contains(key)) {
				sender.sendMessage(ChatColor.RED + "The item does not have the specified key!");
				return true;
			}

			String value = getKeyValue(item, key);
			if (value == null) {
				sender.sendMessage(ChatColor.RED + "The key '" + args[1] + "' has an unsupported data type.");
				return true;
			}

			sender.sendMessage("The value of the key " + ChatColor.GREEN  + "'" + args[1] + "'" + ChatColor.WHITE + " is " + ChatColor.GREEN + "'" + value + "'");
		} else if (args[0].equals("list")) {
			for (NamespacedKey scarceKey : ItemUtil.getScarceKeys(item.getItemMeta())) {
				sender.sendMessage(ChatColor.GREEN + scarceKey.toString() + ChatColor.WHITE + ": " + ChatColor.GREEN + "'" + getKeyValue(item, scarceKey) + ChatColor.GREEN + "'");
			}
		} else if (args[0].equals("set")) {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "You must provide a value to set for the key!");
				return true;
			}

			NamespacedKey key = NamespacedKey.fromString(args[1]);
			if (!ItemUtil.getScarceKeys(item.getItemMeta()).contains(key)) {
				sender.sendMessage(ChatColor.RED + "The item does not have the specified key!");
				return true;
			}

			ItemMeta meta = item.getItemMeta();

			String newValue = args[2];
			PersistentDataType<?, ?> type = ItemUtil.hasKey(item.getItemMeta(), key);
			if (type == PersistentDataType.STRING) {
				ItemUtil.setString(meta, key, newValue);
			} else if (type == PersistentDataType.INTEGER) {
				try {
					int intValue = Integer.parseInt(newValue);
					ItemUtil.setInteger(meta, key, intValue);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "The value must be an integer!");
					return true;
				}
			} else if (type == PersistentDataType.BOOLEAN) {
				if (newValue.equalsIgnoreCase("true") || newValue.equalsIgnoreCase("false")) {
					ItemUtil.setBoolean(meta, key, Boolean.parseBoolean(newValue));
				} else {
					sender.sendMessage(ChatColor.RED + "The value must be 'true' or 'false'!");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "The key '" + args[1] + "' has an unsupported data type.");
				return true;
			}

			item.setItemMeta(meta);
			CommandUtil.logCommand(sender, "Set the value of the key " + ChatColor.GREEN + "'" + args[1] + "'%1$s to " + ChatColor.GREEN + "'" + newValue + "'%1$s on item " + ChatColor.GREEN + "'" + ScarceLife.NAMESPACE + ":" + ItemUtil.getType(item) + "'");
		}
		return true;
	}

	private String getKeyValue(ItemStack item, NamespacedKey key) {
		PersistentDataType<?, ?> type = ItemUtil.hasKey(item.getItemMeta(), key);
		if (type == PersistentDataType.STRING) {
			return ItemUtil.getString(item.getItemMeta(), key);
		} else if (type == PersistentDataType.INTEGER) {
			return String.valueOf(ItemUtil.getInteger(item.getItemMeta(), key));
		} else if (type == PersistentDataType.BOOLEAN) {
			return String.valueOf(ItemUtil.getBoolean(item.getItemMeta(), key));
		}
		return null;
	}

}
