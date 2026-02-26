package me.misha2win.scracesmpplugin.item;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;
import me.misha2win.scracesmpplugin.item.registry.ItemRecipeRegistry;
import me.misha2win.scracesmpplugin.item.registry.ItemRegistry;
import me.misha2win.scracesmpplugin.util.ItemUtil;

public class EdenApple {

	public static final String TYPE = "eden_apple";
	public static final NamespacedKey RECIPE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, EdenApple.TYPE);

	public static final NamespacedKey PLAYER_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "player_head_used");
	public static final NamespacedKey BLOODSTAINED_KEY = new NamespacedKey(ScarceLife.NAMESPACE, "bloodstained");

	public static void register() {
		ItemRegistry.register(TYPE, EdenApple::createItem);
		ItemRecipeRegistry.register(TYPE, EdenApple.getRecipe());
		ItemEventRouter.on(EdenApple.TYPE, PlayerItemConsumeEvent.class, EdenApple::onPlayerEat);
		ItemEventRouter.on(EdenApple.TYPE, PrepareSmithingEvent.class, EdenApple::onPrepareSmithing);
		ItemEventRouter.on(EdenApple.TYPE, SmithItemEvent.class, EdenApple::onSmithing);
	}

	public static String emphasize(String text, ChatColor color) {
		return String.format("%s%sX%s%s%s%s%sX%s%s", color, ChatColor.MAGIC, ChatColor.RESET, color, text, color, ChatColor.MAGIC, ChatColor.RESET, color);
	}

	private static ItemStack createItem() {
		ItemStack edenApple = new ItemStack(Material.APPLE, 1);

		ItemMeta edenMeta = edenApple.getItemMeta();

		FoodComponent food = edenMeta.getFood();
		food.setCanAlwaysEat(true);
		edenMeta.setFood(food);

		edenMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Forbidden Eden Apple");

		edenMeta.setLore(Arrays.asList(String.format("%sAn invaluable price paid.", ChatColor.DARK_RED)));

		edenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		edenMeta.addEnchant(Enchantment.MENDING, 1, false);

		CustomModelDataComponent cmd = edenMeta.getCustomModelDataComponent();
		cmd.setFloats(Arrays.asList(1f));
		edenMeta.setCustomModelDataComponent(cmd);

		ItemUtil.setType(edenMeta, EdenApple.TYPE);

		edenApple.setItemMeta(edenMeta);

		return edenApple;
	}

	private static Recipe getRecipe() {
		return new SmithingTransformRecipe(
				EdenApple.RECIPE_KEY,
				EdenApple.createItem(),
				new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH),
				new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD),
				new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
		);
	}


	public static void onPlayerEat(ScarceLife plugin, PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();

		int playerLives = LifeManager.getLivesScoreboard(e.getPlayer()).getScore();
		int edenApplesEaten = LifeManager.getEdenScoreboard(e.getPlayer()).getScore();
		if (playerLives >= 4) {
			player.sendMessage(ChatColor.RED + "You cannot eat this Eden Apple, you already have too many lives!");
			e.setCancelled(true);
			return;
		}

		LifeManager.addLife(player);
		LifeManager.getEdenScoreboard(player).setScore(edenApplesEaten + 1);

		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 60 * 5, 255, false, true, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 1, false, false, false));

		player.getWorld().playSound(player, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.1f, 1f);

		player.sendMessage(ChatColor.RED + "You are overwhelmed by an immense sense of power...");
		player.sendMessage(ChatColor.GREEN + "You have received a life!");

		Bukkit.getLogger().info(e.getPlayer().getName() + " has eaten an Eden Apple!");
	}

	public static void onPrepareSmithing(ScarceLife plugin, PrepareSmithingEvent e) {
		ItemEventRouter.dispatch(plugin, e, PlayerHead.TYPE);
		ItemEventRouter.dispatch(plugin, e, UnstableMix.TYPE);
	}

	public static void onSmithing(ScarceLife plugin, SmithItemEvent e) {
		ItemEventRouter.dispatch(plugin, e, PlayerHead.TYPE);

		HumanEntity crafter = e.getWhoClicked();
		e.getWhoClicked().getWorld().playSound(crafter.getLocation(), Sound.ENTITY_WARDEN_NEARBY_CLOSEST, SoundCategory.PLAYERS, 1f, 0.1f);
		e.getWhoClicked().getWorld().playSound(crafter.getLocation(), Sound.AMBIENT_CAVE, SoundCategory.PLAYERS, 1f, 0.1f);
	}

}
