package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.recipe.ItemManager;

public class EdenAppleConsumeHandler implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public EdenAppleConsumeHandler(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerEat(PlayerItemConsumeEvent e) {
		if (e.getItem().isSimilar(ItemManager.EDEN_APPLE)) {
			
			int playerLives = LifeManager.getLivesScoreboard(e.getPlayer()).getScore();
			int edenApplesEaten = LifeManager.getEdenScoreboard(e.getPlayer()).getScore();
			if (playerLives < 4 && edenApplesEaten < 3) {
				
				LifeManager.addLife(e.getPlayer());
				LifeManager.getEdenScoreboard(e.getPlayer()).setScore(edenApplesEaten + 1);
				
				e.getPlayer().sendMessage(ChatColor.GREEN + "The Eden Apple fills you with determination.");
				e.getPlayer().sendMessage(ChatColor.GREEN + "You have received a life!");
				
				Bukkit.getLogger().info(e.getPlayer().getName() + " has eaten an Eden Apple!");
			} else {
				e.setCancelled(true);
				
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot eat this Eden Apple!");
				
				if (edenApplesEaten >= 3) {
					e.getPlayer().sendMessage(ChatColor.RED + "You have already eaten 3 eden apples!");
				} else if (playerLives >= 4) {
					e.getPlayer().sendMessage(ChatColor.RED + "You cannot have more than 4 lives at once!");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer() instanceof Player) {
			if (e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getItem() != null && e.getItem().isSimilar(ItemManager.MOLTEN_EDEN_APPLE)) {
				Levelled cauldron = ((Levelled)e.getClickedBlock());
				if (cauldron.getLevel() == cauldron.getMaximumLevel()) {
					if (e.getItem().isSimilar(ItemManager.MOLTEN_EDEN_APPLE)) {
						e.getPlayer().getInventory().setItemInMainHand(ItemManager.NEW_EDEN_APPLE);
						cauldron.setLevel(0);
					}
				}
			}
			
		}
	}
	
}
