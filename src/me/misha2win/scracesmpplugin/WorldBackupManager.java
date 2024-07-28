package me.misha2win.scracesmpplugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class WorldBackupManager {
	
	public static void start(Plugin plugin) {
//		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
//			try {
//				Bukkit.broadcastMessage(ChatColor.GREEN + "Creating backup of worlds...");
//				for (World world : Bukkit.getWorlds()) {
//					copyDirectory(world.getWorldFolder().getAbsolutePath(), world.getWorldFolder().getAbsolutePath() + "../worldsaves/" + world.getName() + System.currentTimeMillis());
//				}
//				Bukkit.broadcastMessage(ChatColor.GREEN + "Backup complete!");
//			} catch (Exception e) {
//				Bukkit.broadcastMessage(ChatColor.RED + "There was an error creating backups!");
//				e.printStackTrace();
//			}
//		}, 20 * 60 * 30, 20 * 60 * 30);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				Bukkit.broadcastMessage(ChatColor.GREEN + "Creating backup of worlds...");
				for (World world : Bukkit.getWorlds()) {
					world.save();
					copyDirectory(world.getWorldFolder().getAbsolutePath(), world.getWorldFolder().getAbsolutePath() + "/../worldsaves/" + world.getName() + System.currentTimeMillis());
				}
				Bukkit.broadcastMessage(ChatColor.GREEN + "Backup complete!");
			} catch (Exception e) {
				Bukkit.broadcastMessage(ChatColor.RED + "There was an error creating backups!");
				e.printStackTrace();
			}
		});
	}
	
	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
		Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
			Path destination = Paths.get(destinationDirectoryLocation,
					source.toString().substring(sourceDirectoryLocation.length()));
			try {
				Files.copy(source, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
