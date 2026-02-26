package me.misha2win.scracesmpplugin.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.potion.PotionEffectType;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.CommandUtil;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class DeadPlayerHandler implements Listener {
	
	@SuppressWarnings("unused")
	private ScarceLife plugin;
	
	public DeadPlayerHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (LifeManager.getLives(player) <= 0) {
				PacketSender.sendTeamJoinPacket(e.getPlayer(), player);
			}
		}
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
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
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
	public void onSpectatorTeleport(PlayerTeleportEvent e) {
		if (e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) return;

		Player player = e.getPlayer();

		if (LifeManager.getLives(player) <= 0) {
			player.sendMessage(ChatColor.RED + "Nice try, but no.");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player victim = (Player) e.getEntity();
			
			if (e.getDamager() instanceof Player) {
				Player damager = (Player) e.getDamager();
				
				if (LifeManager.getLives(victim) <= 0 && LifeManager.getLives(damager) >= 1) {
					Location location = victim.getLocation().add(0, 50, 0);
					while (location.getBlock().getType() != Material.AIR) {
						location = location.add(0, 50, 0);
					}
					victim.teleport(location);
				}
			}
		}
		
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
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntity() instanceof Warden) {
			for (Player player : CommandUtil.getDeadPlayers()) {
				clearWardenAnger((Warden) e.getEntity(), player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) return;
		if (LifeManager.getLives(e.getPlayer()) > 0) return;
		
		e.getPlayer().removePotionEffect(PotionEffectType.DARKNESS);
		e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
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
	
	public static void clearWardenAnger(Warden warden, Player player) {
		warden.setAnger(player, 0);
		CommandUtil.getTeamOfPlayer(player).addEntry(warden.getUniqueId().toString());
	}
	
}
