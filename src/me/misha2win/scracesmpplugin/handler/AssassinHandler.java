package me.misha2win.scracesmpplugin.handler;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.misha2win.scracesmpplugin.Main;

public class AssassinHandler implements Listener {
	
	private static final int TIME_LIMIT = 30;
	
	private HashMap<Player, Player> assassins;
	
	private Main plugin;
	
	public AssassinHandler(Main plugin) {
		this.plugin = plugin;
	}
	
	public HashMap<Player, Player> getAssassins() {
		return assassins;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (assassins.containsKey(e.getEntity())) {
			Player target = assassins.remove(e.getEntity());
			e.getEntity().sendMessage(ChatColor.RED + "You lost! You died before you were able to kill " + target.getName() + "!");
			target.sendMessage(ChatColor.GREEN + "You won! You survived and your assassin (" + e.getEntity().getName() + ") died before they could kill you!");
		} else if (assassins.containsValue(e.getEntity())) {
			if (e.getDamageSource() instanceof Player) {
				Player killer = (Player) e.getDamageSource();
				for (Player p : assassins.keySet()) {
					if (assassins.get(p).equals(e.getEntity())) {
						if (killer.equals(p)) {
							p.sendMessage(ChatColor.GREEN + "You won! You killed " + e.getEntity().getName() + "!");
						} else {
							p.sendMessage(ChatColor.RED + "You lost! " + killer.getName() + " killed " + e.getEntity().getName() + " before you did!");
						}
					}
					assassins.remove(p);
				}
			} else {
				for (Player p : assassins.keySet()) {
					if (assassins.get(p).equals(e.getEntity())) {
						p.sendMessage(ChatColor.RED + "You lost! " + e.getEntity().getName() + " died before you could kill them!");
					}
					assassins.remove(p);
				}
			}
		}
	}
	
	public void startEvent(HashMap<Player, Player> assassins) {
		this.assassins = assassins;
		
//		Bukkit.broadcastMessage(ChatColor.RED + "The assassins will be selected in 5 minutes!");
//		
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {
//			Bukkit.broadcastMessage(ChatColor.RED + "The assassins will be selected in 2 minutes!");
//		}, 20 * 60 * 3);
//
//		Bukkit.getScheduler().runTaskLater(plugin, () -> {
//			Bukkit.broadcastMessage(ChatColor.RED + "The assassins will be selected in 1 minute!");
//		}, 20 * 60 * 4);
		
		new BukkitRunnable() {
			int counter = 5;
			
			@Override
			public void run() {
				String message = "";
				if (counter >= 5) {
					message = ChatColor.DARK_GREEN + "5";
				} else if (counter == 4) {
					message = ChatColor.GREEN + "4";
				} else if (counter == 3) {
					message = ChatColor.YELLOW + "3";
				} else if (counter == 2) {
					message = ChatColor.RED + "2";
				} else if (counter == 1) {
					message = ChatColor.DARK_RED + "1";
					cancel();
					
					new BukkitRunnable() {
						int counter = 2;
						
						@Override
						public void run() {
							String message = "";
							
							if (counter == 2) {
								message = ChatColor.YELLOW + "You are...";
							} else if (counter == 1) {
								cancel();
							}
							
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (counter == 1) {
									if (assassins.containsKey(p)) {
										message = ChatColor.RED + "...an assassin!";
										p.sendMessage(ChatColor.GREEN + "Your task is to kill " + assassins.get(p).getName() + " before they die or you die! You have " + TIME_LIMIT + " minutes to find and kill them or you lose!");
									
										Bukkit.getScheduler().runTaskLater(plugin, () -> {
											if (assassins.containsKey(p)) {
												Player target = assassins.remove(p);
												p.sendMessage(ChatColor.RED + "You lost! Time ran out before you could kill " + target.getName() + "!");
												target.sendMessage(ChatColor.GREEN + "You won! You survived long enough for " + p.getName() + " to run out of time to kill you!");
											}
										}, 20 * 60 * TIME_LIMIT);
									} else if (assassins.containsValue(p)) {
										message = ChatColor.RED + "...the target!";
										p.sendMessage(ChatColor.RED + "You are being hunted by someone on the server! Be careful and survive for the next " + TIME_LIMIT + " minutes without dying! Trust no one and good luck!");
									} else {
										message = ChatColor.GREEN + "...safe!";
									}
								}
								
								p.sendTitle(message, "", 0, 20 * 5, 0);
							}
							
							counter--;
						}
						
					}.runTaskTimer(plugin, 20, 20 * 5);
				}
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendTitle(message, "", 0, 20, 0);
				}
				
				counter--;
			}
		}.runTaskTimer(plugin, /*20 * 60 * 5*/ 5, 20);
	}
	
}
