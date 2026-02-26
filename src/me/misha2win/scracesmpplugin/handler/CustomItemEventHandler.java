package me.misha2win.scracesmpplugin.handler;

import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.PlayerHead;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.util.ItemUtil;
import me.misha2win.scracesmpplugin.item.EnchantingTable;

public class CustomItemEventHandler implements Listener {

	private ScarceLife plugin;

	public CustomItemEventHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerEat(PlayerItemConsumeEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getItem());
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getItem());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getItemInHand());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (!(e.getBlock().getState() instanceof TileState)) return;
		TileState tileEntity = (TileState) e.getBlock().getState();
		ItemEventRouter.dispatch(plugin, e, ItemUtil.getType(tileEntity));
	}

	@EventHandler
	public void onPrepareSmithing(PrepareSmithingEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getResult());
	}

	@EventHandler
	public void onSmith(SmithItemEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getInventory().getResult());
	}

	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getInventory().getResult());
	}

	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getInventory().getResult());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		ItemEventRouter.dispatch(plugin, e, PlayerHead.TYPE);
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

	@EventHandler
	public void onItemDespawn(ItemDespawnEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getEntity().getItemStack());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		ItemEventRouter.dispatch(plugin, e, e.getItemDrop().getItemStack());
	}

	@EventHandler
	public void onPlayerMoveItem(InventoryOpenEvent e) {
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

}
