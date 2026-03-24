package me.misha2win.scracesmpplugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.misha2win.scracesmpplugin.util.CommandUtil;

public class AutoReloadManager {

	private static File jarFile;
	private static long lastModified;
	private static boolean hasBeenModified;
	private static volatile boolean halt;

	public static void start(ScarceLife plugin) {
		if (jarFile != null) return; // Guard against multiple calls

		if (!plugin.getConfig().getBoolean("developer.auto-reload.enabled")) {
			return; // Silent return so non-devs won't see this
		}

		CommandUtil.messageAllOpedPlayers(ChatColor.RED + "Auto reload is enabled! Disable by typing '/scarceconfig set developer.auto-reload.enabled false'");

		try {
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			jarFile = (File) getFileMethod.invoke(plugin);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		lastModified = jarFile.lastModified();

		AtomicReference<BukkitTask> reloadTaskRef = new AtomicReference<>();
		AtomicReference<BukkitTask> configTaskRef = new AtomicReference<>();

		configTaskRef.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			halt = !plugin.getConfig().getBoolean("developer.auto-reload.enabled");
		}, 20, 20));

		reloadTaskRef.set(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			if (halt) {
				BukkitTask reloadTask = reloadTaskRef.getAndSet(null);
				if (reloadTask != null) reloadTask.cancel();
				BukkitTask configTask = configTaskRef.getAndSet(null);
				if (configTask != null) configTask.cancel();

				Bukkit.getScheduler().runTask(plugin, () -> {
					CommandUtil.messageAllOpedPlayers(ChatColor.RED + "Auto reload has been disabled!");
				});
				return;
			}

			long currentModified = jarFile.lastModified();
			if (lastModified != currentModified) {
				Bukkit.getLogger().info("The jarfile has been modified!");
				lastModified = currentModified;
				hasBeenModified = true;
				return;
			}

			if (!hasBeenModified) return;
			// hasBeenMoified never needs to be set to false, since the plugin reloads this which defaults it to false

			BukkitTask reloadTask = reloadTaskRef.getAndSet(null);
			if (reloadTask != null) reloadTask.cancel();
			BukkitTask configTask = configTaskRef.getAndSet(null);
			if (configTask != null) configTask.cancel();

			Bukkit.getLogger().info("The jarfile is done being modified! Reloading!");
			Bukkit.getScheduler().runTask(plugin, () -> {
				Bukkit.broadcastMessage(ChatColor.RED + "Restarting server plugin... Expect lag!");
				Bukkit.reload();
			});
		}, 20, 20));
	}

}
