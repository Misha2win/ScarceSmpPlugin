package me.misha2win.scracesmpplugin.item;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.ScarceLife;

public class UnstableMix {
	
	public static final String TYPE = "unstable_mix";
	
	public static final NamespacedKey RECIPE_KEY = new NamespacedKey(ScarceLife.NAMESPACE, TYPE);
	
	public static void register() {
		ItemRegistry.register(TYPE, UnstableMix::createItem);
		ItemRecipeRegistry.register(TYPE, getRecipe());
		ItemEventRouter.on(TYPE, PrepareItemCraftEvent.class, UnstableMix::onPrepareCraft);
		ItemEventRouter.on(TYPE, CraftItemEvent.class, UnstableMix::onCraft);
		ItemEventRouter.on(TYPE, PrepareSmithingEvent.class, UnstableMix::onPrepareSmithing);
		ItemEventRouter.on(TYPE, SmithItemEvent.class, UnstableMix::onSmithing);
	}

	private static ItemStack createItem() {
		ItemStack item = new ItemStack(Material.DRAGON_BREATH, 1);
		
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Otherworldly Concoction");
		
		meta.setMaxStackSize(1);
		
		meta.setLore(Arrays.asList(ChatColor.GOLD + "" + ChatColor.MAGIC + "X" + ChatColor.RESET + "" + ChatColor.GOLD +  " Eden Apple Ingredient " + ChatColor.MAGIC + "X"));
		
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.MENDING, 1, false);
		
		ItemUtil.setType(meta, TYPE);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	private static Recipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(RECIPE_KEY, createItem());

	    recipe.shape(
	        " H ",
	        "ABA",
	        " A "
	    );

	    recipe.setIngredient('A', Material.AMETHYST_CLUSTER);
	    recipe.setIngredient('B', Material.DRAGON_BREATH);
	    recipe.setIngredient('H', Material.WITHER_SKELETON_SKULL);
	    
	    return recipe;
	}
	
	public static void onPrepareCraft(ScarceLife plugin, PrepareItemCraftEvent e) {
		if (ItemRegistry.getScarceTypeByItem(e.getInventory().getItem(5)) != null) {
			e.getView().getPlayer().sendMessage(TYPE);
			e.getInventory().setResult(null);
		}
	}
	
	public static void onCraft(ScarceLife plugin, CraftItemEvent e) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			e.getInventory().setItem(5, null);
		});
	}
	
	public static void onPrepareSmithing(ScarceLife plugin, PrepareSmithingEvent e) {
		ItemStack baseItem = e.getInventory().getItem(0);
	    if (!TYPE.equals(ItemRegistry.getScarceTypeByItem(baseItem))) {
	    	e.setResult(null);
	    }
	}
	
	public static void onSmithing(ScarceLife plugin, SmithItemEvent e) {
	}
	
}
