package me.misha2win.scracesmpplugin.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.util.ParticleMaker;

public class GhostBlink {
	
	public static String TYPE = "ghost_blink";
	
	public static void register() {
		ItemRegistry.register(TYPE, GhostBlink::createItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, GhostBlink::onPlayerInteract);
	}

	private static ItemStack createItem() {
		ItemStack echoShard = new ItemStack(Material.ECHO_SHARD, 1);
		
		ItemMeta meta = echoShard.getItemMeta();
		meta.setMaxStackSize(1);
		
		meta.setDisplayName(ChatColor.GOLD + "Blink");
		
		ItemUtil.setType(meta, TYPE);
		
		echoShard.setItemMeta(meta);
		
		return echoShard;
	}
	
	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		Player player = e.getPlayer();
		
		if (LifeManager.getLives(player) > 0) {
			e.getPlayer().getInventory().setItemInMainHand(null);
			e.getPlayer().sendMessage(ChatColor.RED + "You shouldn't have this item!");
			return;
		}
		if (player.getCooldown(Material.ECHO_SHARD) != 0) return;
		
		ParticleMaker.createDome(Particle.SCULK_SOUL,  player.getLocation(), 0.8, 3, 0);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.PLAYERS, 0.05f, 0.1f);
		player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, PotionEffect.INFINITE_DURATION, 255, false, false, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 255, false, false, false));
		
		player.setGameMode(GameMode.SPECTATOR);
		Location previousLocation = player.getLocation().clone();
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (player.getLocation().getBlock().getType() != Material.AIR) {
				player.teleport(previousLocation);
				player.sendMessage(ChatColor.RED + "You cannot be inside of a block when you rematerialize!");
			}
			player.removePotionEffect(PotionEffectType.DARKNESS);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.setGameMode(GameMode.ADVENTURE);
			player.setCooldown(Material.ECHO_SHARD, 20 * 3);
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.PLAYERS, 0.5f, 0.1f);
			ParticleMaker.createDome(Particle.SCULK_SOUL,  player.getLocation(), 0.8, 3, 0.05);
			LifeManager.updateTeam(player);
		}, 15);
	}
	
}
