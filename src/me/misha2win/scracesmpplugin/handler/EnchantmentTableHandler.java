package me.misha2win.scracesmpplugin.handler;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.recipe.ItemManager;
import me.misha2win.scracesmpplugin.util.StructureHandler;

public class EnchantmentTableHandler implements Listener {
	
	private ArrayList<Location> protectedLocations;
	
	private Main plugin;
	
	private Location eTableLoc;
	
	private boolean shrineActive;
	
	//private boolean
	
	public EnchantmentTableHandler(Main plugin) {
		this.plugin = plugin;
		eTableLoc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
		setProtectedLocations();
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.ENCHANTING_TABLE)
			checkShrine(true);
		else if (e.getBlock().getType() == Material.PLAYER_HEAD) {
			checkHeads(e.getBlock());
		}
	}
	
	public void checkHeads(Block block) {
		int heads = 0;
		
		Location l1 = new Location(eTableLoc.getWorld(), -2, 1, -2);
		Location l2 = new Location(eTableLoc.getWorld(),  2, 1, -2);
		Location l3 = new Location(eTableLoc.getWorld(), -2, 1,  2);
		Location l4 = new Location(eTableLoc.getWorld(),  2, 1,  2);
		
		if ((block != null && block.getLocation().equals(l1)) || l1.getBlock().getType() == Material.PLAYER_HEAD) {
			heads++;
		}
		
		if ((block != null && block.getLocation().equals(l2)) || l2.getBlock().getType() == Material.PLAYER_HEAD) {
			heads++;
		} 
		
		if ((block != null && block.getLocation().equals(l3)) || l3.getBlock().getType() == Material.PLAYER_HEAD) {
			heads++;
		}

		if ((block != null && block.getLocation().equals(l4)) || l4.getBlock().getType() == Material.PLAYER_HEAD) {
			heads++;
		}
		
		if (heads == 4) {
			checkDrops();
		}
	}
	
	public void checkDrops() {
		new Thread(() -> {
			Location bb = new Location(eTableLoc.getWorld(), 0, 1, 0);
			while (true) {
				
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					@Override
					public void run() {
						Entity apple = null;
						Entity head = null;
						
						for (Entity e : eTableLoc.getWorld().getNearbyEntities(bb, 1, 1, 1)) {
							if (e.getType() == EntityType.ITEM) {
								ItemStack i = ((Item) e).getItemStack();
								if (i.getType() == Material.PLAYER_HEAD) {
									head = e;
								}
								
								if (i.isSimilar(ItemManager.EDEN_APPLE)) {
									apple = e;
								}
							}
							
							if (apple != null && head != null) {
								break;
							}
							
						}
						
						if (apple != null && head != null) {
							SkullMeta sm = (SkullMeta)(((Item) head).getItemStack().getItemMeta());
							
							OfflinePlayer player = sm.getOwningPlayer();
							
							if (player.isOnline()) {
								if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("revived").getScore(player.getName()).getScore() == 0) {
									doTheThing(sm.getOwningPlayer(), apple, head);
								} else {
									for (Player p : Bukkit.getOnlinePlayers()) {
										p.sendMessage(ChatColor.RED + player.getName() + " cannot be revived! They have been revived before!");
									}
									
									Bukkit.getWorlds().get(0).playSound(eTableLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 100, 0);
								}
							}
						}
						
					}
				});
				
				try { Thread.sleep(100); } catch (Exception ex) {}
			}
		}).start();
	}
	
	public void doTheThing(OfflinePlayer player, Entity apple, Entity head) {
		apple.remove();
		head.remove();
		
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, apple.getLocation(), 1);
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, head.getLocation(), 1);
		
		new Location(eTableLoc.getWorld(), -2, 1, -2).getBlock().setType(Material.AIR);
		new Location(eTableLoc.getWorld(),  2, 1, -2).getBlock().setType(Material.AIR);
		new Location(eTableLoc.getWorld(), -2, 1,  2).getBlock().setType(Material.AIR);
		new Location(eTableLoc.getWorld(),  2, 1, 2).getBlock().setType(Material.AIR);
		
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, new Location(eTableLoc.getWorld(), -2, 1, -2), 1);
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, new Location(eTableLoc.getWorld(), 2, 1, -2), 1);
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, new Location(eTableLoc.getWorld(), -2, 1, 2), 1);
		Bukkit.getWorlds().get(0).spawnParticle(Particle.CLOUD, new Location(eTableLoc.getWorld(), 2, 1, 2), 1);
		
		try { Thread.sleep(0); } catch (Exception ex) {}
		
		Bukkit.getWorlds().get(0).playSound(eTableLoc, Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 100, 0);
		
		try { Thread.sleep(8 * 50); } catch (Exception ex) {}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 5, 255, true, true));
		}
		
		try { Thread.sleep(74 * 50); } catch (Exception ex) {}
		
		int delay = 50;
		
		Bukkit.getWorlds().get(0).spawnParticle(Particle.SCULK_SOUL, 0, 0, 0, 1000, 1, 1, 1);
		Bukkit.getWorlds().get(0).playSound(eTableLoc, Sound.ITEM_TOTEM_USE, 100, 0);
		Bukkit.getWorlds().get(0).playSound(eTableLoc, Sound.ITEM_TRIDENT_THUNDER, 100, 0);
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));
		try { Thread.sleep(delay); } catch (Exception ex) {}
		Bukkit.getWorlds().get(0).strikeLightningEffect(new Location(Bukkit.getWorlds().get(0), 0.5, 1, 0.5));

		Bukkit.getScoreboardManager().getMainScoreboard().getObjective("revived").getScore(player.getName()).setScore(1);
		LifeManager.addLife((Player) player);
		((Player)player).teleport(new Location(eTableLoc.getWorld(), 0.5, 2, 0.5));
		
		try { Thread.sleep(34 * 50); } catch (Exception ex) {}
		
		Bukkit.getWorlds().get(0).spawnParticle(Particle.FLASH, 0, 0, 0, 50, 1, 1, 1);
		
		try { Thread.sleep(2 * 50); } catch (Exception ex) {}
		
		Bukkit.getWorlds().get(0).spawnParticle(Particle.ENCHANT, 0, 0, 0, 10000, 5, 5, 5);
		
		try { Thread.sleep(12 * 50); } catch (Exception ex) {}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 11, 255, true, true));
		}
		
		try { Thread.sleep(14 * 50); } catch (Exception ex) {}
		
		Bukkit.getWorlds().get(0).playSound(eTableLoc, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 100, 0);
	}
	
	public void checkShrine(boolean playSound) {
		if (!shrineActive && eTableLoc.getBlock().getType() == Material.ENCHANTING_TABLE) {
			shrineActive = true;
			if (playSound)
				eTableLoc.getWorld().playSound(eTableLoc, Sound.BLOCK_END_PORTAL_SPAWN, 100, 0);
		}
	}
	
	public void setProtectedLocations() {
		protectedLocations = new ArrayList<>();
		
		String[] bd = StructureHandler.getBlocks();
		
		int x = -6;
		int y = -18;
		int z = -6;
		
		int counter = 0;
		for (int i = 0; i <= 12; i++) {
			for (int j = 0; j <= 22; j++) {
				for (int k = 0; k <= 12; k++) {
					BlockData block = Bukkit.getServer().createBlockData(bd[counter]);
					Location l = new Location(eTableLoc.getWorld(), x + i, y + j, z + k);
				
					if (block.getMaterial() != Material.AIR && block.getMaterial() != Material.ENCHANTING_TABLE) {
						protectedLocations.add(l);
					}
					
					counter++;
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		boolean isETable = e.getBlock().getType() == Material.ENCHANTING_TABLE;
		boolean isShrine = protectedLocations.contains(e.getBlock().getLocation());
		boolean isMisplacedETable = isETable && !e.getBlock().getLocation().equals(eTableLoc);
		if ((isShrine || (shrineActive && isETable))) {
			if (!isMisplacedETable)
				cancelEvent(e);
		}
	}
	
	public void cancelEvent(BlockBreakEvent e) {
		e.setCancelled(true);
		e.getPlayer().sendMessage(ChatColor.RED + "You cannot break this!");
	}
	
	
	
}
