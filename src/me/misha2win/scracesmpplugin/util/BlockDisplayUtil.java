package me.misha2win.scracesmpplugin.util;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import me.misha2win.scracesmpplugin.ScarceLife;

public class BlockDisplayUtil {

	public static final String TYPE = "Display";

	public static BlockDisplay createGlowingBlockDisplay(Block block) {
		return createGlowingBlockDisplay(block.getLocation());
	}

	public static BlockDisplay createGlowingBlockDisplay(Location location) {
		BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
		display.setBlock(location.getBlock().getBlockData());
		ItemUtil.setType(display, TYPE);
		display.setGlowing(true);

		return display;
	}

	public static Collection<Entity> getBlockDisplays(Location location) {
		if (location == null) return Collections.emptyList();

		Chunk chunk = location.getChunk();
		if (!chunk.isLoaded()) chunk.load();

		Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 2, 2, 2);

		entities.removeIf((entity) -> {
			return !TYPE.equals(ItemUtil.getType(entity));
		});

		return entities;
	}

	public static void removeBlockDisplays(ScarceLife plugin, Location location) {
		for (Entity entity : getBlockDisplays(location)) {
			if (!(entity instanceof BlockDisplay)) continue;
			BlockDisplay display = (BlockDisplay) entity;

			if (!TYPE.equals(ItemUtil.getType(display))) continue;

			attemptRemove(plugin, display); // Overkill
		}
	}

	private static void attemptRemove(ScarceLife plugin, BlockDisplay display) {
		if (display == null) return;

		Chunk chunk = display.getLocation().getChunk();
		if (!chunk.isLoaded()) chunk.load();
		display.remove();

		AtomicReference<BukkitTask> removeTask = new AtomicReference<>();
		AtomicInteger attempts = new AtomicInteger();
		int maxAttempts = 10;

		removeTask.set(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (display.isValid() && attempts.incrementAndGet() < maxAttempts) {
				display.remove();
				return;
			}

			BukkitTask task = removeTask.getAndSet(null);
			if (task != null) task.cancel();
		}, 1L, 10L));
	}

}