package me.misha2win.scracesmpplugin.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.EnchantingTable;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;

public class EnchantmentsHandler implements Listener {

	private ScarceLife plugin;

	public EnchantmentsHandler(ScarceLife plugin) {
		this.plugin = plugin;
	}

	public static ItemStack sanitizeItem(ItemStack item) {
		if (item == null) return null;

		ItemStack clone = null;

		for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
			if (entry.getValue() > 1) {
				if (clone == null) clone = item.clone();
				clone.removeEnchantment(entry.getKey());
				clone.addUnsafeEnchantment(entry.getKey(), 1);
			}
		}

		ItemMeta meta = item.getItemMeta();
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
			boolean changed = false;
			for (Map.Entry<Enchantment, Integer> entry : storageMeta.getStoredEnchants().entrySet()) {
				if (entry.getValue() > 1) {
					changed = true;
					break;
				}
			}

			if (changed) {
				if (clone == null) clone = item.clone();

				EnchantmentStorageMeta cloneMeta = (EnchantmentStorageMeta) clone.getItemMeta();
				for (Map.Entry<Enchantment, Integer> entry : new HashMap<>(cloneMeta.getStoredEnchants()).entrySet()) {
					if (entry.getValue() > 1) {
						cloneMeta.removeStoredEnchant(entry.getKey());
						cloneMeta.addStoredEnchant(entry.getKey(), 1, true);
					}
				}
				clone.setItemMeta(cloneMeta);
			}
		}

		return clone != null ? clone : item;
	}

	public static void sanitizeInventory(Inventory inventory) {
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack original = inventory.getItem(i);
			ItemStack sanitized = sanitizeItem(original);

			if (sanitized != original) {
				inventory.setItem(i, sanitized);
			}
		}
	}

	@EventHandler
	public void onVillagerAcquireTrade(VillagerAcquireTradeEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		MerchantRecipe oldRecipe = e.getRecipe();

		MerchantRecipe newRecipe = new MerchantRecipe(
			sanitizeItem(oldRecipe.getResult()),
			oldRecipe.getUses(),
			oldRecipe.getMaxUses(),
			oldRecipe.hasExperienceReward(),
			oldRecipe.getVillagerExperience(),
			oldRecipe.getPriceMultiplier(),
			oldRecipe.getDemand(),
			oldRecipe.getSpecialPrice()
		);
		newRecipe.setIngredients(oldRecipe.getIngredients());

		e.setRecipe(newRecipe);
	}

	@EventHandler
	public void onLootGenerate(LootGenerateEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		List<ItemStack> loot = e.getLoot();
		for (int i = 0; i < loot.size(); i++) {
			loot.set(i, sanitizeItem(loot.get(i)));
		}
	}

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
		if (!(e.getCaught() instanceof Item)) return;
		Item itemEntity = (Item) e.getCaught();

		ItemStack item = itemEntity.getItemStack();
		itemEntity.setItemStack(sanitizeItem(item));
	}

	@EventHandler
	public void onBarter(PiglinBarterEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		List<ItemStack> outcome = e.getOutcome();
		for (int i = 0; i < outcome.size(); i++) {
			outcome.set(i, sanitizeItem(outcome.get(i)));
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		if (!(e.getEntity() instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) e.getEntity();

		EntityEquipment equipment = livingEntity.getEquipment();
		if (equipment == null) return;

		equipment.setHelmet(sanitizeItem(equipment.getHelmet()));
		equipment.setChestplate(sanitizeItem(equipment.getChestplate()));
		equipment.setLeggings(sanitizeItem(equipment.getLeggings()));
		equipment.setBoots(sanitizeItem(equipment.getBoots()));
		equipment.setItemInMainHand(sanitizeItem(equipment.getItemInMainHand()));
		equipment.setItemInOffHand(sanitizeItem(equipment.getItemInOffHand()));
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		List<ItemStack> drops = event.getDrops();
		for (int i = 0; i < drops.size(); i++) {
			drops.set(i, sanitizeItem(drops.get(i)));
		}
	}

	@EventHandler
	public void onPrepareEnchant(PrepareItemEnchantEvent e) {
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		ItemEventRouter.dispatch(plugin, e, EnchantingTable.TYPE);
	}

	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		e.setResult(sanitizeItem(e.getResult()));
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (!plugin.getConfig().getBoolean("items.enchantments.weak-only")) return;

		Inventory inventory = e.getInventory();
		sanitizeInventory(inventory);

		if (inventory.getType() != InventoryType.PLAYER) {
			sanitizeInventory(e.getPlayer().getInventory());
		}
	}

}