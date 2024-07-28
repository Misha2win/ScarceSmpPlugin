package me.misha2win.scracesmpplugin.handler;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.recipe.ItemManager;

public class DeadPlayerHandler implements Listener {
	
	public static final ItemStack[] allowedItems = new ItemStack[] {
			ItemManager.CREEPER_SOUND_PLAYER,
			ItemManager.GHOST_TOGGLE_HIDE,
			ItemManager.GHOST_TOGGLE_SHOW
	};
	
	private static final HashMap<Player, Mob> POSSESSIONS = new HashMap<>();
	
	private Main plugin;
	
	public DeadPlayerHandler(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent e) {
		if (shouldIgnoreEvents(e.getAttacker()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent e) {
		if (shouldIgnoreEvents(e.getEntity()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent e) {
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setAmount(0);
	}
	
	@EventHandler
	public void onPlayerInteract(EntityInteractEvent e) {
		if (shouldIgnoreEvents(e.getEntity()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getItem().isSimilar(ItemManager.CREEPER_SOUND_PLAYER)) {
				if (e.getPlayer().getCooldown(Material.CREEPER_HEAD) == 0) {
					ItemManager.onUse(plugin, e.getPlayer(), e.getItem());
				}
			} else if (e.getItem().isSimilar(ItemManager.GHOST_TOGGLE_SHOW)) {
				if (e.getPlayer().getCooldown(Material.TINTED_GLASS) == 0) {
					ItemManager.onUse(plugin, e.getPlayer(), e.getItem());
				}
			} else if (e.getItem().isSimilar(ItemManager.GHOST_TOGGLE_HIDE)) {
				if (e.getPlayer().getCooldown(Material.GLASS) == 0) {
					ItemManager.onUse(plugin, e.getPlayer(), e.getItem());
				}
			} else if (e.getItem().isSimilar(ItemManager.POSSESS_ITEM)) {
				if (e.getPlayer().getCooldown(ItemManager.POSSESS_ITEM.getType()) == 0) {
					if (POSSESSIONS.containsKey(e.getPlayer())) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.showEntity(plugin, e.getPlayer());
						}
						e.getPlayer().setCooldown(ItemManager.POSSESS_ITEM.getType(), 20 * 60 * 5);
						e.getPlayer().setWalkSpeed(0.2f);
						e.getPlayer().showEntity(plugin, POSSESSIONS.get(e.getPlayer()));
						POSSESSIONS.get(e.getPlayer()).setAware(true);
						e.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer possessing " + POSSESSIONS.get(e.getPlayer()).getName() + ".");
						POSSESSIONS.remove(e.getPlayer());
					}
				}
			}
		}
		
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setCancelled(true);
	}
	
//	@EventHandler
//	public void onPlayerMove(PlayerRotateEvent e) {
//		if (possessions.containsKey(e.getPlayer())) {
//			Mob mob = possessions.get(e.getPlayer());
//			mob.setVelocity(e.getVelocity());
//		}
//	}
	
//	@EventHandler
//	public void onPlayerMove(PlayerVelocityEvent e) {
//		if (possessions.containsKey(e.getPlayer())) {
//			Mob mob = possessions.get(e.getPlayer());
//			mob.setVelocity(e.getVelocity());
//		}
//	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (POSSESSIONS.containsKey(e.getPlayer())) {
			Mob mob = POSSESSIONS.get(e.getPlayer());
			mob.teleport(e.getPlayer());
			mob.setFallDistance(e.getPlayer().getFallDistance());
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		ItemStack holdingItem = e.getPlayer().getInventory().getItemInMainHand();
		
		if (holdingItem != null) {
			if (holdingItem.isSimilar(ItemManager.POSSESS_ITEM)) {
				if (e.getRightClicked() instanceof Mob) {
					if (e.getPlayer().getCooldown(holdingItem.getType()) == 0) {
						if (!POSSESSIONS.containsKey(e.getPlayer())) {
							e.getPlayer().sendMessage(ChatColor.GREEN + "You are now possessing " + e.getRightClicked().getName() + " for the next 5 minutes!");
							e.getPlayer().setCooldown(holdingItem.getType(), 20 * 5);
							Mob mob = (Mob) e.getRightClicked();
							e.getPlayer().setWalkSpeed((float) mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
							e.getPlayer().hideEntity(plugin, mob);
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.hideEntity(plugin, e.getPlayer());
							}
							mob.setAware(false);
							e.getPlayer().teleport(mob);
							POSSESSIONS.put(e.getPlayer(), mob);
							
							Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
								if (POSSESSIONS.containsKey(e.getPlayer())) {
									for (Player p : Bukkit.getOnlinePlayers()) {
										p.showEntity(plugin, e.getPlayer());
									}
									
									e.getPlayer().setCooldown(ItemManager.POSSESS_ITEM.getType(), 20 * 60 * 5);
									e.getPlayer().setWalkSpeed(0.2f);
									e.getPlayer().showEntity(plugin, mob);
									
									POSSESSIONS.get(e.getPlayer()).setAware(true);
									e.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer possessing " + POSSESSIONS.get(e.getPlayer()).getName() + ".");
									POSSESSIONS.remove(e.getPlayer());
								}
								
							}, 20 * 60);
						}
					}
				}
			}
		}
		
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onArmorDispense(BlockDispenseArmorEvent e) {
		if (shouldIgnoreEvents(e.getTargetEntity()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockReceiveGameEvent(BlockReceiveGameEvent e) {
		if (shouldIgnoreEvents(e.getEntity()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		if (shouldIgnoreEvents(e.getTarget()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		if (shouldIgnoreEvents(e.getTarget()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (shouldIgnoreEvents(e.getEntity(), e.getCause() != DamageCause.KILL && e.getCause() != DamageCause.VOID && e.getCause() != DamageCause.SUICIDE))
			e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (shouldIgnoreEvents(e.getDamager(), e.getCause() != DamageCause.KILL && e.getCause() != DamageCause.VOID && e.getCause() != DamageCause.SUICIDE))
			e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onEntityItemPickup(EntityPickupItemEvent e) {
		if (shouldIgnoreEvents(e.getEntity()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (shouldIgnoreEvents(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent e) {
		if (shouldIgnoreEvents(e.getEntity()))
			e.setCancelled(true);
	}
	
	public static boolean shouldIgnoreEvents(Entity e) {
		return shouldIgnoreEvents(e, true);
	}
	
	public static boolean shouldIgnoreEvents(Entity e, boolean extraCondition) {
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.getGameMode() != GameMode.CREATIVE && LifeManager.getLives(p) <= 0)
				if (extraCondition)	
					return true;
		}
			
		
		return false;
	}
	
}
