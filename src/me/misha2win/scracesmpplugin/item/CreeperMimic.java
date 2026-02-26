package me.misha2win.scracesmpplugin.item;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
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

		headMeta.setDisplayName(ChatColor.GOLD + "Play Creeper Sound");

		ArrayList<String> headLore = new ArrayList<>();
		headMeta.setLore(headLore);

		ItemUtil.setType(headMeta, TYPE);

		creeperPlayer.setItemMeta(headMeta);

		return creeperPlayer;
	}

	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		if (LifeManager.getLives(e.getPlayer()) > 0) return;
		if (e.getPlayer().getCooldown(Material.CREEPER_HEAD) != 0) return;

		Creeper creeper = (Creeper) e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation().add(0, 1, 0), EntityType.CREEPER);
		creeper.setInvulnerable(true);
		creeper.setInvisible(true);
		creeper.setGravity(false);
		creeper.setAI(false);
		creeper.setCollidable(false);
		creeper.setExplosionRadius(0);
		creeper.ignite();

		ItemStack creeperHead = new ItemStack(Material.CREEPER_HEAD);
		creeperHead.addEnchantment(Enchantment.BINDING_CURSE, 1);
		e.getPlayer().getEquipment().setHelmet(creeperHead);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				e.getPlayer().getEquipment().setHelmet(null);
			}
		}, 20 * 5);

		e.getPlayer().setCooldown(Material.CREEPER_HEAD, 20 * 60 * 5);
	}

}
