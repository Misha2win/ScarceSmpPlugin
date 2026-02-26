package me.misha2win.scracesmpplugin.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.ItemUtil;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class GhostVisibilityToggle{

	public static final String TYPE = "ghost_visibility_toggle";

	private static final NamespacedKey STATE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "state");

	private static boolean permanentToggle = false;
	private static int cooldown = 20;
	private static int hideDuration = 20 * 60 * 5;

	public static void register() {
		ItemRegistry.register(TYPE, GhostVisibilityToggle::createItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, GhostVisibilityToggle::onPlayerInteract);
	}

	public void loadConfigSettings(ScarceLife plugin) {
		permanentToggle = plugin.getConfig().getBoolean("ghostTogglePermanent");
		cooldown = plugin.getConfig().getInt("ghostToggleCooldown");
		hideDuration = plugin.getConfig().getInt("ghostToggleDuration");
	}

	private static ItemStack createItem() {
		ItemStack ghostToggle = new ItemStack(Material.GLASS, 1);

		ItemMeta togglerMeta = ghostToggle.getItemMeta();
		togglerMeta.setMaxStackSize(1);

		togglerMeta.setDisplayName(ChatColor.GOLD + "Toggle ghost visibility");

		ItemUtil.setType(togglerMeta, TYPE);
		ItemUtil.setBoolean(togglerMeta, STATE_KEY, false);

		ghostToggle.setItemMeta(togglerMeta);

		return ghostToggle;
	}

	public static ItemStack createTint() {
		ItemStack ghostToggle = new ItemStack(Material.TINTED_GLASS, 1);

		ItemMeta togglerMeta = ghostToggle.getItemMeta();
		togglerMeta.setMaxStackSize(1);

		togglerMeta.setDisplayName(ChatColor.GOLD + "Toggle ghost visibility");

		ItemUtil.setType(togglerMeta, TYPE);
		ItemUtil.setBoolean(togglerMeta, STATE_KEY, true);

		ghostToggle.setItemMeta(togglerMeta);

		return ghostToggle;
	}

	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		if (LifeManager.getLives(e.getPlayer()) > 0) return;

//		boolean playerHidden = e.getItem().getItemMeta().getPersistentDataContainer().get(
//			GhostVisibilityToggle.STATE_KEY,
//			PersistentDataType.BOOLEAN
//		);
//
//		if (playerHidden) {
//			if (e.getPlayer().getCooldown(Material.TINTED_GLASS) != 0) {
//				return;
//			}
//
//			setItemsTo(e.getPlayer(), thiz.createClear());
//			revealPlayer(e.getPlayer());
//		} else {
//			if (e.getPlayer().getCooldown(Material.GLASS) != 0) {
//				return;
//			}
//
//			setItemsTo(e.getPlayer(), thiz.createTint());
//			hidePlayer(e.getPlayer());
//
//			if (!permanentToggle) {
//				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
//					boolean hadItem = setItemsTo(e.getPlayer(), thiz.createClear());
//					if (hadItem) {
//						revealPlayer(e.getPlayer());
//					}
//				}, hideDuration);
//			}
//		}
	}

	public static void revealPlayer(Player player) {
		PacketSender.tellEveryonePlayerJoinedTheirTeam(player);
		player.sendMessage(ChatColor.RED + "You are now visible again!");
		player.setCooldown(Material.GLASS, cooldown);
	}

	public static void hidePlayer(Player player) {
		PacketSender.tellEveryonePlayerJoinedOwnTeam(player, CommandUtil.getDeadPlayers());
		if (!permanentToggle) {
			player.sendMessage(ChatColor.GREEN + "You are now invisible to alive players for the next " + (hideDuration / 20 / 60) + " minutes and " + (hideDuration / 20 % 60) + " seconds!");
		} else {
			player.sendMessage(ChatColor.GREEN + "You are now invisible to alive players!");
		}
		player.setCooldown(Material.TINTED_GLASS, 20 * 5);
	}

	public static boolean setItemsTo(Player player, ItemStack to) {
		boolean hadItem = false;

		if ("ghost_visibility_toggle".equals(ItemUtil.getType(player.getItemOnCursor()))) {
			player.setItemOnCursor(to);
		}

		if ("ghost_visibility_toggle".equals(ItemUtil.getType(player.getInventory().getItemInMainHand()))) {
			player.getInventory().setItemInMainHand(to);
		} else if ("ghost_visibility_toggle".equals(ItemUtil.getType(player.getInventory().getItemInOffHand()))) {
			player.getInventory().setItemInOffHand(to);
		}

		ItemStack[] inv = player.getInventory().getContents();
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && "ghost_visibility_toggle".equals(ItemUtil.getType(inv[i]))) {
				player.getInventory().setItem(i, to);
			}
		}

		return hadItem;
	}

}
