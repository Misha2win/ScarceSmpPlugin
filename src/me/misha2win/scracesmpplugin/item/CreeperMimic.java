package me.misha2win.scracesmpplugin.item;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class CreeperMimic {

	public static final String TYPE = "creeper_mimic";

	public static void register() {
		ItemRegistry.register(TYPE, CreeperMimic::createItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, CreeperMimic::onPlayerInteract);
	}

	private static ItemStack createItem() {
		ItemStack creeperPlayer = new ItemStack(Material.CREEPER_HEAD, 1);

		ItemMeta headMeta = creeperPlayer.getItemMeta();
		headMeta.setMaxStackSize(1);

		headMeta.setDisplayName(ChatColor.GOLD + "Mimic Creeper");

		ArrayList<String> headLore = new ArrayList<>();
		headMeta.setLore(headLore);

		ItemUtil.setType(headMeta, TYPE);

		creeperPlayer.setItemMeta(headMeta);

		return creeperPlayer;
	}

	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (LifeManager.getLives(player) > 0) {
			player.getInventory().setItemInMainHand(null);
			player.sendMessage(ChatColor.RED + "You shouldn't have this item!");
			return;
		}

		if (player.getCooldown(Material.CREEPER_HEAD) != 0) return;

		Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.CREEPER);
		creeper.setInvulnerable(true);
		creeper.setInvisible(true);
		creeper.setGravity(false);
		creeper.setAI(false);
		creeper.setCollidable(false);
		creeper.setExplosionRadius(0);
		creeper.ignite();

		ItemStack creeperHead = new ItemStack(Material.CREEPER_HEAD);
		creeperHead.addEnchantment(Enchantment.BINDING_CURSE, 1);
		player.getEquipment().setHelmet(creeperHead);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			player.getEquipment().setHelmet(null);
		}, 20 * 5);

		player.setCooldown(Material.CREEPER_HEAD, plugin.getConfig().getInt("items.creeper-mimic.cooldown-ticks"));
	}

}
