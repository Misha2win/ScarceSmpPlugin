package me.misha2win.scracesmpplugin.recipe;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import me.misha2win.scracesmpplugin.Main;

public class RecipeMaker {
	
	private Main plugin;
	
	public RecipeMaker(Main plugin) {
		this.plugin = plugin;
	}
	
	public void createAppleOfEdenRecipe() {
		ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "eden_apple"), ItemManager.EDEN_APPLE);
		
		recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.GOLDEN_APPLE));
		recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT));
		
		Bukkit.addRecipe(recipe);
	}
	
	public void createNameTagRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "name_tag"), new ItemStack(Material.NAME_TAG, 1));
		
		recipe.shape("s", "n", "p");
		recipe.setIngredient('s', new RecipeChoice.MaterialChoice(Material.STRING));
		recipe.setIngredient('n', new RecipeChoice.MaterialChoice(Material.IRON_NUGGET));
		recipe.setIngredient('p', new RecipeChoice.MaterialChoice(Material.PAPER));
		
		Bukkit.addRecipe(recipe);
	}
	
	public void createSaddleRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "saddle"), new ItemStack(Material.SADDLE, 1));
		
		recipe.shape("lll", "s s", "i i");
		recipe.setIngredient('l', new RecipeChoice.MaterialChoice(Material.LEATHER));
		recipe.setIngredient('s', new RecipeChoice.MaterialChoice(Material.STRING));
		recipe.setIngredient('i', new RecipeChoice.MaterialChoice(Material.IRON_INGOT));
		
		Bukkit.addRecipe(recipe);
	}
	
	public void createCheaperTNTRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "tnt"), new ItemStack(Material.TNT, 1));
		
		recipe.shape("gsg", "sps", "gsg");
		recipe.setIngredient('g', new RecipeChoice.MaterialChoice(Material.GUNPOWDER));
		recipe.setIngredient('s', new RecipeChoice.MaterialChoice(Material.SAND));
		recipe.setIngredient('p', new RecipeChoice.MaterialChoice(Material.PAPER));
		
		Bukkit.addRecipe(recipe);
	}
	
	public void removeOldTNTRecipe() {
		Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
		
		while (recipes.hasNext()) {
			Recipe recipe = recipes.next();
			
			if (recipe.getResult().getType().equals(Material.TNT)) {
				recipes.remove();
				break;
			}
		}
	}
	
	public void removeEnchantmentTableRecipe() {
		Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
		
		while (recipes.hasNext()) {
			Recipe recipe = recipes.next();
			
			if (recipe.getResult().getType().equals(Material.ENCHANTING_TABLE)) {
				recipes.remove();
				break;
			}
		}
	}
	
	public void createMoltenGoldRecipe() {
		int smeltTime = 20 * 60 * 30;
		
		FurnaceRecipe recipe1 = new FurnaceRecipe(new NamespacedKey(plugin, "recipe_molten_gold_1"), ItemManager.MOLTEN_GOLD, Material.GOLD_BLOCK, 0f, smeltTime);
		Bukkit.addRecipe(recipe1);
		
		FurnaceRecipe recipe2 = new FurnaceRecipe(new NamespacedKey(plugin, "recipe_molten_gold_2"), ItemManager.MOLTEN_GOLD, Material.RAW_GOLD_BLOCK, 0f, smeltTime);
		Bukkit.addRecipe(recipe2);
	}
	
	public void createMoltenGoldBucketRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "molten_gold_bucket"), ItemManager.BUCKET_OF_MOLTEN_GOLD);
		
		recipe.shape("   ", "igi", " b ");
		recipe.setIngredient('i', new RecipeChoice.MaterialChoice(Material.IRON_INGOT));
		recipe.setIngredient('g', new RecipeChoice.ExactChoice(ItemManager.MOLTEN_GOLD));
		recipe.setIngredient('b', new RecipeChoice.MaterialChoice(Material.IRON_BLOCK));
		
		Bukkit.addRecipe(recipe);
	}
	
}
