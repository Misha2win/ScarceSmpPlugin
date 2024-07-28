package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.Main;

public class WetSeasonHandler  implements Listener {
	
	private static final int MOON_CYCLE_TICK_DURATION = 192000;
	
	private boolean wetSeasonActive;
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public WetSeasonHandler(Main plugin) {
		this.plugin = plugin;
	}
	
	public void startSeason() {
		World overworld = Bukkit.getWorlds().get(0);
		Bukkit.broadcastMessage(ChatColor.GREEN + "The wet season has begun!");
		overworld.setStorm(true);
		overworld.setWeatherDuration(MOON_CYCLE_TICK_DURATION);
		wetSeasonActive = true;
	}
	
	public boolean getWetSeasonActive() {
		return wetSeasonActive;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (wetSeasonActive) {
			if (e.getBlock().getBlockData() instanceof Ageable) {
				e.setDropItems(false);
				for (ItemStack item : e.getBlock().getDrops()) {
					item.setAmount(item.getAmount() + 1);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockGrow(BlockGrowEvent e) {
		if (wetSeasonActive) {
			if (e.getBlock().getBlockData() instanceof Ageable) {
				Ageable crop = (Ageable) e.getNewState().getBlockData();
				
				if (crop.getAge() < crop.getMaximumAge()) {
					crop.setAge(crop.getAge() + 1);
					
					e.getNewState().setBlockData(crop);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (wetSeasonActive) {
			if (e.getEntity() instanceof Monster && e.getEntityType() != EntityType.SLIME) {
				if (Math.random() * 100 < 69) {
					e.setCancelled(true);
					e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.SLIME);
				}
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if (wetSeasonActive) {
			if (e.getWorld() == Bukkit.getWorlds().get(0)) {
				if (!e.toWeatherState()) {
					wetSeasonActive = false;
					Bukkit.broadcastMessage(ChatColor.RED + "The wet season has ended!");
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (wetSeasonActive) {
			if (e.getEntity() instanceof Animals) {
				for (ItemStack item : e.getDrops()) {
					item.setAmount(item.getAmount() * 2);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerFish(PlayerFishEvent e) {
		if (wetSeasonActive) {
			Bukkit.broadcastMessage("Event: " + e.getState());
			
			e.setExpToDrop(3 * e.getExpToDrop());
			
			if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
				boolean gotFish = Math.random() * 10 < 2;
				
				if (gotFish) {
					// Do nothing
				} else {
					((Item) e.getCaught()).setItemStack(new ItemStack(Material.EMERALD));
				}
				
			}
		}
	}
	
}