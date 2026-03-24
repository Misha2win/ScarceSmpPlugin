package me.misha2win.scracesmpplugin.item;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderSignal;
import org.bukkit.event.entity.EntitySpawnEvent;

import me.misha2win.scracesmpplugin.ScarceLife;
import me.misha2win.scracesmpplugin.item.registry.ItemEventRouter;

public class EnderEye {

	public static final String TYPE = "ender_eye";

	public static void register() {
		ItemEventRouter.on(TYPE, EntitySpawnEvent.class, EnderEye::onEyeSpawn);
	}

	public static void onEyeSpawn(ScarceLife plugin, EntitySpawnEvent e) {
		FileConfiguration config = plugin.getConfig();

		if (!config.getBoolean("stronghold.enabled")) return;
		if (!config.getBoolean("stronghold.placed")) return;

		EnderSignal eye = (EnderSignal) e.getEntity();
		Location stronghold = new Location(
			e.getEntity().getWorld(),
			config.getInt("stronghold.location.x"),
			config.getInt("stronghold.location.y"),
			config.getInt("stronghold.location.z")
		);
		eye.setTargetLocation(stronghold);
	}

}
