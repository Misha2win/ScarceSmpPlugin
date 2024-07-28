package me.misha2win.scracesmpplugin.recipe;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.PacketSender;

public class ItemManager {
	
	public static final NamespacedKey EDEN_APPLE_NAMESPACE = new NamespacedKey(Main.getInstance(), "eden_apple");
	
	public static final ItemStack EDEN_APPLE = createEdenApple();
	
	public static final ItemStack MOLTEN_GOLD = createMoltenGold();
	public static final ItemStack BUCKET_OF_MOLTEN_GOLD = createBucketOfMoltenGold();
	public static final ItemStack MOLTEN_EDEN_APPLE = createMoltenEdenApple();
	public static final ItemStack NEW_EDEN_APPLE = createEdenApple();
	
	public static final ItemStack CREEPER_SOUND_PLAYER = createCreeperSoundPlayer();
	public static final ItemStack GHOST_TOGGLE_HIDE = createCreateGhostToggle1();
	public static final ItemStack GHOST_TOGGLE_SHOW = createCreateGhostToggle2();
	
	public static final ItemStack POSSESS_ITEM = createPossessItem();
	
	public static final ItemStack EMPTY_EXP_VIAL = createEXPVial();
	
	public static final ItemStack FULL_EXP_VIAL = createFullEXPVial();
	
	public static ItemStack createPossessItem() {
		ItemStack ghostToggle = new ItemStack(Material.PLAYER_HEAD, 1);
		
		ItemMeta togglerMeta = ghostToggle.getItemMeta();
		togglerMeta.setMaxStackSize(1);
		
		togglerMeta.setDisplayName(ChatColor.GOLD + "Possess Mob");
		
		ArrayList<String> togglerLore = new ArrayList<>();
		togglerLore.add("Right click a mob to");
		togglerLore.add("possess it.");
		togglerMeta.setLore(togglerLore);
		ghostToggle.setItemMeta(togglerMeta);
		
		return ghostToggle;
	}
	
	public static ItemStack createCreateGhostToggle1() {
		ItemStack ghostToggle = new ItemStack(Material.GLASS, 1);
		
		ItemMeta togglerMeta = ghostToggle.getItemMeta();
		togglerMeta.setMaxStackSize(1);
		
		togglerMeta.setDisplayName(ChatColor.GOLD + "Toggle ghost visibility");
		
		ArrayList<String> togglerLore = new ArrayList<>();
		togglerLore.add("Hide yourself from people");
		togglerLore.add("for a short period.");
		togglerMeta.setLore(togglerLore);
		ghostToggle.setItemMeta(togglerMeta);
		
		return ghostToggle;
	}
	
	public static ItemStack createCreateGhostToggle2() {
		ItemStack ghostToggle = new ItemStack(Material.TINTED_GLASS, 1);
		
		ItemMeta togglerMeta = ghostToggle.getItemMeta();
		togglerMeta.setMaxStackSize(1);
		
		togglerMeta.setDisplayName(ChatColor.GOLD + "Toggle ghost visibility");
		
		ArrayList<String> togglerLore = new ArrayList<>();
		togglerLore.add("Show yourself to people.");
		togglerMeta.setLore(togglerLore);
		ghostToggle.setItemMeta(togglerMeta);
		
		return ghostToggle;
	}
	
	public static ItemStack createCreeperSoundPlayer() {
		ItemStack creeperPlayer = new ItemStack(Material.CREEPER_HEAD, 1);
		
		ItemMeta headMeta = creeperPlayer.getItemMeta();
		headMeta.setMaxStackSize(1);
		
		headMeta.setDisplayName(ChatColor.GOLD + "Play Creeper Sound");
		
		ArrayList<String> headLore = new ArrayList<>();
		headMeta.setLore(headLore);
		creeperPlayer.setItemMeta(headMeta);
		
		return creeperPlayer;
	}
	
	public static ItemStack createMoltenEdenApple() {
		ItemStack moltenEden = new ItemStack(Material.GOLDEN_APPLE, 1);
		
		ItemMeta moltenEdenMeta = moltenEden.getItemMeta();
		moltenEdenMeta.setMaxStackSize(1);
		
		moltenEdenMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Molten Eden Apple");
		
		ArrayList<String> moltenGoldLore = new ArrayList<>();
		moltenGoldLore.add(ChatColor.RED + "So hot keeping it");
		moltenGoldLore.add(ChatColor.RED + "in your inventory");
		moltenGoldLore.add(ChatColor.RED + "will burn you!");
		moltenEdenMeta.setLore(moltenGoldLore);
		
		moltenEdenMeta.setCustomModelData(1);
		
		moltenEden.setItemMeta(moltenEdenMeta);
		
		return moltenEden;
	}
	
	public static ItemStack createBucketOfMoltenGold() {
		ItemStack bmg = new ItemStack(Material.LAVA_BUCKET, 1);
		
		ItemMeta bmgMeta = bmg.getItemMeta();
		
		bmgMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Bucket of Molten Gold");
		bmgMeta.setCustomModelData(1);
		bmg.setItemMeta(bmgMeta);
		
		return bmg;
	}
	
	public static ItemStack createMoltenGold() {
		ItemStack moltenGold = new ItemStack(Material.HONEYCOMB, 1);
		
		ItemMeta moltenGoldMeta = moltenGold.getItemMeta();
		moltenGoldMeta.setMaxStackSize(1);
		
		moltenGoldMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Molten Gold");
		
		ArrayList<String> moltenGoldLore = new ArrayList<>();
		moltenGoldLore.add(ChatColor.RED + "So hot keeping it");
		moltenGoldLore.add(ChatColor.RED + "in your inventory");
		moltenGoldLore.add(ChatColor.RED + "will burn you!");
		moltenGoldMeta.setLore(moltenGoldLore);
		
		moltenGoldMeta.setCustomModelData(1);
		
		moltenGold.setItemMeta(moltenGoldMeta);
		
		return moltenGold;
	}
	
	public static ItemStack createEdenApple() {
		ItemStack edenApple = new ItemStack(Material.APPLE, 1);
		
		ItemMeta edenMeta = edenApple.getItemMeta();
		
		edenMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Eden Apple");
		
		ArrayList<String> edenLore = new ArrayList<>();
		edenLore.add("Rumor has is that the");
		edenLore.add("ancients themselves have");
		edenLore.add("blessed this apple.");
		edenLore.add(ChatColor.RED + "You can only ever eat 3!");
		edenMeta.setLore(edenLore);
		
		//edenMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		edenMeta.addEnchant(Enchantment.MENDING, 1, false);
		
		edenMeta.setCustomModelData(1);
		
		edenApple.setItemMeta(edenMeta);
		
		return edenApple;
	}
	
	public static ItemStack createEXPVial() {
		ItemStack EXPVial = new ItemStack(Material.POTION, 1);
		
		ItemMeta vialMeta = EXPVial.getItemMeta();
		
		vialMeta.setDisplayName(ChatColor.WHITE + "Empty Experience Vial");
		
		ArrayList<String> vialLore = new ArrayList<>();
		vialLore.add("Single use item to store");
		vialLore.add("experience temporarily.");
		vialMeta.setLore(vialLore);
		
		vialMeta.setCustomModelData(1);
		
		EXPVial.setItemMeta(vialMeta);
		 
		return EXPVial;
	}
	
	public static ItemStack createFullEXPVial() {
		ItemStack EXPVial = new ItemStack(Material.POTION, 1);
		
		ItemMeta vialMeta = EXPVial.getItemMeta();
		
		vialMeta.setDisplayName(ChatColor.WHITE + "Full Experience Vial");
		
		ArrayList<String> vialLore = new ArrayList<>();
		vialLore.add("Stores experience temporarily.");
		vialMeta.setLore(vialLore);
		
		vialMeta.setCustomModelData(1);
		
		EXPVial.setItemMeta(vialMeta);
		 
		return EXPVial;
	}
	
	public static void onUse(Main plugin, Player player, ItemStack itemStack) {
		if (itemStack.isSimilar(ItemManager.CREEPER_SOUND_PLAYER)) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1, 1);
			player.setCooldown(Material.CREEPER_HEAD, 20 * 60 * 5);
		} else if (itemStack.isSimilar(ItemManager.GHOST_TOGGLE_SHOW)) {
			PacketSender.tellEveryonePlayerJoinedTheirTeam(player);
			player.sendMessage(ChatColor.RED + "You are now visible again!");
			player.setCooldown(Material.GLASS, 20 * 60 * 5);
			
			if (player.getInventory().getItemInMainHand().equals(itemStack)) {
				player.getInventory().setItemInMainHand(ItemManager.GHOST_TOGGLE_HIDE);
			} else if (player.getInventory().getItemInOffHand().equals(itemStack)) {
				player.getInventory().setItemInOffHand(ItemManager.GHOST_TOGGLE_HIDE);
			}
		} else if (itemStack.isSimilar(ItemManager.GHOST_TOGGLE_HIDE)) {
			PacketSender.tellEveryonePlayerJoinedOwnTeam(player);
			player.sendMessage(ChatColor.GREEN + "You are now invisible for the next five minutes!");
			player.setCooldown(Material.TINTED_GLASS, 20 * 3);
			
			if (player.getInventory().getItemInMainHand().equals(itemStack)) {
				player.getInventory().setItemInMainHand(ItemManager.GHOST_TOGGLE_SHOW);
			} else if (player.getInventory().getItemInOffHand().equals(itemStack)) {
				player.getInventory().setItemInOffHand(ItemManager.GHOST_TOGGLE_SHOW);
			}
			
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
				boolean found = false;
				if (player.getItemOnCursor().isSimilar(ItemManager.GHOST_TOGGLE_SHOW)) {
					player.setItemOnCursor(ItemManager.GHOST_TOGGLE_HIDE);
					found = true;
				} else {
					ItemStack[] items = player.getInventory().getContents();
					for (int i = 0; i < items.length; i++) {
						if (items[i] != null && items[i].isSimilar(ItemManager.GHOST_TOGGLE_SHOW)) {
							player.getInventory().setItem(i, ItemManager.GHOST_TOGGLE_HIDE);
							found = true;
							break;
						}
					}
				}
				
				if (found) {
					PacketSender.tellEveryonePlayerJoinedTheirTeam(player);
					player.sendMessage(ChatColor.RED + "You are now visible again!");
					player.setCooldown(Material.GLASS, 20 * 60 * 5);
				}
				
			}, 20 * 5);
		}
	}
	
}
