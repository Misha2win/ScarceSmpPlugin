package me.misha2win.scracesmpplugin.item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.misha2win.scracesmpplugin.ScarceLife;

public class ItemEventRouter {
	
	private static final Map<String, Map<Class<? extends Event>, BiConsumer<ScarceLife, Event>>> HANDLERS_BY_TYPE = new HashMap<>();
	
	public static <E extends Event> void on(String itemId, Class<E> eventType, BiConsumer<ScarceLife, E> handler) {
		@SuppressWarnings("unchecked")
		BiConsumer<ScarceLife, Event> castedHandler = (BiConsumer<ScarceLife, Event>) handler;
		HANDLERS_BY_TYPE.computeIfAbsent(itemId, id -> new HashMap<>()).put(eventType, castedHandler);
		Bukkit.getLogger().info("Registered event handler of '" + eventType.getSimpleName() + "' for '" + itemId + "'");
	}
	
	public static void dispatch(ScarceLife plugin, Event event, ItemStack item) {
		if (item == null) return;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return;
		
		String itemId = ItemUtil.getType(meta);
		if (itemId == null) return;
		
		ItemEventRouter.dispatch(plugin, event, itemId);
	}
	
	public static void dispatch(ScarceLife plugin, Event event, String itemId) {
		if (itemId == null) return;
		
		Map<Class<? extends Event>, BiConsumer<ScarceLife, Event>> handlers = HANDLERS_BY_TYPE.get(itemId);
		if (handlers == null) {
			Bukkit.getLogger().info(String.format("Could not find event handlers for '%s'", itemId));
			return;
		}
		
		BiConsumer<ScarceLife, Event> handler = findHandler(handlers, event.getClass());
		if (handler == null) {
			Bukkit.getLogger().info(String.format("Could not find handler for '%s' in '%s'", event.getEventName(), itemId));
			return;
		}
		
		handler.accept(plugin, event);
	}
	
	private static BiConsumer<ScarceLife, Event> findHandler(Map<Class<? extends Event>, BiConsumer<ScarceLife, Event>> handlers, Class<?> eventClass) {
		Class<?> current = eventClass;
		while (current != null) {
			@SuppressWarnings("unchecked")
			BiConsumer<ScarceLife, Event> handler = handlers.get((Class<? extends Event>) current);
			if (handler != null) return handler;
			current = current.getSuperclass();
		}
		return null;
	}
	
}
