package me.misha2win.scracesmpplugin.item;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.ScarceLife;

public class GhostDiamond {
	
	public static final String TYPE = "ghost_diamond";
	
	public static void register() {
		ItemRegistry.register(TYPE, GhostDiamond::createItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, GhostDiamond::onPlayerInteract);
	}

	private static ItemStack createItem() {
		return null;
	}
	
	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
	}
	
}
