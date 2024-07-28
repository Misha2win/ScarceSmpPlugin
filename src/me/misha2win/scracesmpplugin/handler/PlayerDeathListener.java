package me.misha2win.scracesmpplugin.handler;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;

public class PlayerDeathListener implements Listener {
	
	private Main plugin;
	
	public PlayerDeathListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		LifeManager.onDeath(e.getEntity());
		
		if (LifeManager.getLives(e.getEntity()) <= 0) {// TODO Remove this if u don't want players to keep inventory!
			e.setKeepInventory(false);
		} else {
			e.setKeepInventory(true);
			e.getDrops().clear();
		} 
		
		e.getDrops().add(getHead(e.getEntity(), ChatColor.DARK_RED + e.getDeathMessage()));
		
		// For some reason cannot be done immediately after death
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (LifeManager.getLives(e.getEntity()) <= 0) {
				e.getEntity().spigot().respawn();
				e.getEntity().setAllowFlight(true);
				e.getEntity().sendTitle(ChatColor.RED + "You lost your last life!", ChatColor.RED + "You are now a ghost!", 20, 20 * 5, 20);
				e.getEntity().sendMessage(ChatColor.GREEN + "You may now fly around!");
			}
		}, 1);
	}
	
	public ItemStack getHead(Player player, String... lore) {
		ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);
		
		ItemMeta meta = playerSkull.getItemMeta();
		
		meta.setDisplayName(ChatColor.WHITE + player.getName() + "'s Head");
		
		int livesBefore = LifeManager.getLives(player) + 1;
		
		ArrayList<String> itemLore = new ArrayList<>();
		if (lore.length > 0)
			for (int i = 0; i < lore.length; i++)
				itemLore.add(lore[i]);
		itemLore.add(LifeManager.getChatColor(livesBefore) + "Lives before death: " + livesBefore);
		meta.setLore(itemLore);
		playerSkull.setItemMeta(meta);
		
		SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
		skullMeta.setOwnerProfile(player.getPlayerProfile());
		playerSkull.setItemMeta(skullMeta);
		
		return playerSkull;
	}
	
}
