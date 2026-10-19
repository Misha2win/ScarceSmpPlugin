package me.misha2win.scracesmpplugin.item;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRecipeRegistry;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.AdvancementUtil;
import me.misha2win.scracesmpplugin.util.ItemUtil;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class ExpCollector {

	public static final String TYPE = "experience_collector";
	public static final NamespacedKey EXP_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "stored_experience");
	public static final NamespacedKey MODE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "import_mode");
	public static final NamespacedKey RECIPE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, TYPE);
	public static final NamespacedKey COOLDOWN_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "use_cooldown");

	private static final String IMPORT_TEXT = "Import into book";
	private static final String EXPORT_TEXT = "Export from book";

	public static void register() {
		ItemRegistry.register(TYPE, ExpCollector::createItem);
		ItemRecipeRegistry.register(TYPE, getRecipe());
		ItemEventRouter.on(TYPE, PlayerItemConsumeEvent.class, ExpCollector::onPlayerConsume);
		ItemEventRouter.on(TYPE, CraftItemEvent.class, ExpCollector::onCraftItem);
		ItemEventRouter.on(TYPE, PlayerInteractEvent.class, ExpCollector::onPlayerInteract);
		ItemEventRouter.on(TYPE, PlayerItemHeldEvent.class, ExpCollector::onPlayerItemHeld);
	}

	private static ItemStack createItem() {
		return createItem(0, true);
	}

	private static ItemStack createItem(int exp, boolean importMode) {
		ItemStack item = new ItemStack(Material.BOOK);

		ItemMeta meta = item.getItemMeta();

		UseCooldownComponent useCooldown = meta.getUseCooldown();
		useCooldown.setCooldownGroup(COOLDOWN_KEY);
		meta.setUseCooldown(useCooldown);

		ConsumableComponent consumable = meta.getConsumable();
		consumable.setConsumeParticles(false);
		consumable.setConsumeSeconds(1.6f);
		meta.setConsumable(consumable);

		boolean hasExp = exp > 0;
		importMode = hasExp ? importMode : true;

		applyModel(meta, hasExp, importMode);

		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Tome of Knowledge");

		meta.setMaxStackSize(1);

		ExpInfo totalExp = getLevels(exp);
		meta.setLore(Arrays.asList(
			ChatColor.GRAY + "Worth " + totalExp.level + " Levels",
			ChatColor.DARK_GRAY + "(" + exp + " Experience)"
		));

		meta.setEnchantmentGlintOverride(true);

		ItemUtil.setType(meta, TYPE);
		ItemUtil.setInteger(meta, EXP_KEY, exp);
		ItemUtil.setBoolean(meta, MODE_KEY, importMode);

		item.setItemMeta(meta);

		return item;
	}

	private static Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(RECIPE_KEY, createItem());

		recipe.shape(
			" C ",
			"AMA",
			" L "
		);

		recipe.setIngredient('A', Material.AMETHYST_SHARD);
		recipe.setIngredient('M', Material.ENCHANTED_BOOK);
		recipe.setIngredient('C', Material.SCULK_CATALYST);
		recipe.setIngredient('L', Material.LAPIS_LAZULI);

		return recipe;
	}

	public static void onPlayerConsume(ScarceLife plugin, PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		int playerExp = getCumulativeExp(player.getLevel(), Math.round(player.getExp() * player.getExpToLevel()));
		int slot = e.getPlayer().getInventory().getHeldItemSlot();

		int bookExp = ItemUtil.getInteger(e.getItem().getItemMeta(), EXP_KEY);

		// Add support for books that did not previously have a mode set (made before update)
		if (ItemUtil.hasKey(e.getItem().getItemMeta(), MODE_KEY) == null) {
			ItemUtil.setBoolean(e.getItem().getItemMeta(), MODE_KEY, true);
		}

		int rawMaxBookLevels = plugin.getConfig().getInt("items.experience-collector.max-levels");
		int maxBookLevels = rawMaxBookLevels < 0 ? Integer.MAX_VALUE : rawMaxBookLevels;
		int maxBookExp = getCumulativeExp(maxBookLevels, 0);

		boolean importMode = ItemUtil.getBoolean(e.getItem().getItemMeta(), MODE_KEY);
		if (importMode) {
			if (playerExp <= 0) {
				e.getPlayer().sendMessage(ChatColor.RED + "You have no experience to collect.");
				e.setCancelled(true);
				return;
			}

			if (player.isSneaking()) {
				ExpInfo bookLevel = getLevels(bookExp);
				int expNeededToNextTen = (getCumulativeExp((bookLevel.level / 10 + 1) * 10, 0) - bookExp);
				int expTransferred = Math.min(playerExp, Math.min(expNeededToNextTen, maxBookExp - bookExp));
				ExpInfo remainingExpInfo = getLevels(playerExp - expTransferred);

				player.setLevel(remainingExpInfo.level);
				player.setExp((float) remainingExpInfo.expForLevel / remainingExpInfo.expToNext);

				ItemStack newItem = createItem(bookExp + expTransferred, importMode);
				Bukkit.getScheduler().runTask(plugin, () -> {
					ItemMeta meta = newItem.getItemMeta();
					if (rawMaxBookLevels > 0 && meta instanceof Damageable dmg) {
						dmg.setMaxDamage(maxBookExp + 1);
						dmg.setDamage(maxBookExp - (bookExp + expTransferred) + 1);
						newItem.setItemMeta(meta);
					}
					e.getPlayer().getInventory().setItem(slot, newItem);
				});
			} else {
				int expTransferred = Math.min(playerExp, maxBookExp - bookExp);
				ExpInfo remainingExpInfo = getLevels(playerExp - expTransferred);

				player.setLevel(remainingExpInfo.level);
				player.setExp((float) remainingExpInfo.expForLevel / remainingExpInfo.expToNext);

				ItemStack newItem = createItem(bookExp + expTransferred, false);
				Bukkit.getScheduler().runTask(plugin, () -> {
					ItemMeta meta = newItem.getItemMeta();
					if (rawMaxBookLevels > 0 && meta instanceof Damageable dmg) {
						dmg.setMaxDamage(maxBookExp + 1);
						dmg.setDamage(maxBookExp - (bookExp + expTransferred) + 1);
						newItem.setItemMeta(meta);
					}
					e.getPlayer().getInventory().setItem(slot, newItem);
				});
			}
			player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
		} else {
			if (bookExp <= 0) {
				e.getPlayer().sendMessage(ChatColor.RED + "The book has no experience to give you.");
				e.setCancelled(true);
				return;
			}

			if (player.isSneaking()) {
				int expNeededToNextTen = (getCumulativeExp((player.getLevel() / 10 + 1) * 10, 0) - playerExp);
				int expTransferred = Math.min(bookExp, expNeededToNextTen);
				ExpInfo expInfo = getLevels(playerExp + expTransferred);

				player.setLevel(expInfo.level);
				player.setExp((float) expInfo.expForLevel / expInfo.expToNext);

				ItemStack newItem = createItem(bookExp - expTransferred, importMode);
				Bukkit.getScheduler().runTask(plugin, () -> {
					ItemMeta meta = newItem.getItemMeta();
					if (rawMaxBookLevels > 0 && meta instanceof Damageable dmg) {
						dmg.setMaxDamage(maxBookExp + 1);
						dmg.setDamage(maxBookExp - (bookExp - expTransferred) + 1);
						newItem.setItemMeta(meta);
					}
					e.getPlayer().getInventory().setItem(slot, newItem);
				});
			} else {
				ExpInfo expInfo = getLevels(playerExp + bookExp);
				player.setLevel(expInfo.level);
				player.setExp((float) expInfo.expForLevel / expInfo.expToNext);

				ItemStack newItem = createItem(0, importMode);
				Bukkit.getScheduler().runTask(plugin, () -> {
					ItemMeta meta = newItem.getItemMeta();
					if (rawMaxBookLevels > 0 && meta instanceof Damageable dmg) {
						dmg.setMaxDamage(maxBookExp + 1);
						dmg.setDamage(maxBookExp + 1);
						newItem.setItemMeta(meta);
					}
					e.getPlayer().getInventory().setItem(slot, newItem);
				});
			}
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		}
	}

	public static void onPlayerInteract(ScarceLife plugin, PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			if (!e.getPlayer().hasCooldown(item)) {
				ItemMeta itemMeta = item.getItemMeta();

				boolean bookHasExp = ItemUtil.getInteger(itemMeta, EXP_KEY) > 0;
				boolean playerHasExp = e.getPlayer().getLevel() > 0 || e.getPlayer().getExp() > 0;
				boolean newMode = bookHasExp ? (playerHasExp ? !ItemUtil.getBoolean(itemMeta, MODE_KEY) : false) : true;

				applyModel(itemMeta, bookHasExp, newMode);

				ItemUtil.setBoolean(itemMeta, MODE_KEY, newMode);
				item.setItemMeta(itemMeta);

				e.getPlayer().setCooldown(item, 3);
				PacketSender.sendActionBar(e.getPlayer(), newMode ? IMPORT_TEXT : EXPORT_TEXT);
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			ItemMeta itemMeta = item.getItemMeta();
			boolean mode = ItemUtil.getBoolean(itemMeta, MODE_KEY);
			PacketSender.sendActionBar(e.getPlayer(), mode ? IMPORT_TEXT : EXPORT_TEXT);
		}
	}

	public static void onCraftItem(ScarceLife plugin, CraftItemEvent e) {
		AdvancementUtil.grantAdvancement((Player) e.getWhoClicked(), "craft_knowledge_book");
	}

	public static void onPlayerItemHeld(ScarceLife plugin, PlayerItemHeldEvent e) {
		ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
		ItemMeta itemMeta = item.getItemMeta();
		boolean mode = ItemUtil.getBoolean(itemMeta, MODE_KEY);
		PacketSender.sendActionBar(e.getPlayer(), mode ? IMPORT_TEXT : EXPORT_TEXT);
	}

	private static void applyModel(ItemMeta meta, boolean bookHasExp, boolean importMode) {
		meta.setCustomModelDataComponent(null);

		if (bookHasExp && importMode) {
			CustomModelDataComponent modelData = meta.getCustomModelDataComponent();
			modelData.setFloats(List.of(1f));
			meta.setCustomModelDataComponent(modelData);
			meta.setItemModel(NamespacedKey.minecraft("knowledge_book"));
		} else if (bookHasExp) {
			meta.setItemModel(NamespacedKey.minecraft("knowledge_book"));
		} else if (importMode) {
			meta.setItemModel(NamespacedKey.minecraft("writable_book"));
		} else {
			meta.setItemModel(null);
		}
	}

	public static int getCumulativeExp(int level, int levelExp) {
		int baseExp;

		if (level <= 16) {
			baseExp = level * level + 6 * level;
		} else if (level <= 31) {
			baseExp = (int) (2.5 * level * level - 40.5 * level + 360);
		} else {
			baseExp = (int) (4.5 * level * level - 162.5 * level + 2220);
		}

		return baseExp + levelExp;
	}

	private static final class ExpInfo {
		public final int level;
		public final int expForLevel;
		public final int expToNext;
		public ExpInfo(int level, int expForLevel, int expToNext) {
			this.level = level;
			this.expForLevel = expForLevel;
			this.expToNext = expToNext;
		}
	}

	public static ExpInfo getLevels(int totalExp) {
		int level = 0;
		int remaining = totalExp;

		while (true) {
			int expToNext;

			if (level <= 15) {
				expToNext = 2 * level + 7;
			} else if (level <= 30) {
				expToNext = 5 * level - 38;
			} else {
				expToNext = 9 * level - 158;
			}

			if (remaining < expToNext) {
				return new ExpInfo(level, remaining, expToNext);
			}

			remaining -= expToNext;
			level++;
		}
	}

}
