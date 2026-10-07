package me.misha2win.scracesmpplugin.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import me.misha2win.scracesmpplugin.ScarceLife;

public class AdvancementUtil {

	private static final String PACK_MCMETA = """
		{
			"pack": {
				"min_format": 82,
				"max_format": 121,
				"description": "Scarce SMP advancements"
			}
		}
	""";

	private static final HashMap<String, String> ADVANCEMENT_QUEUE = new HashMap<>();
	private static boolean created = false;

	public static void addAdvancement(String advancementName, String advancementJson) {
		if (created) {
			throw new IllegalStateException("Advancements have already been created.");
		}

		ADVANCEMENT_QUEUE.put(advancementName, advancementJson);
	}

	public static void createAdvancements(ScarceLife plugin) {
		if (created) {
			throw new IllegalStateException("Advancements have already been created.");
		}

		ADVANCEMENT_QUEUE.put("root", """
			{
				"display": {
					"icon": {
						"id": "minecraft:apple"
					},
					"title": {
						"text": "Scarce Life SMP"
					},
					"description": {
						"text": "Scarce Life SMP advancements."
					},
					"frame": "task",
					"background": "minecraft:block/glowstone",
					"show_toast": false,
					"announce_to_chat": false,
					"hidden": false
				},
				"criteria": {
					"tick": {
						"trigger": "minecraft:tick"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("one_life", """
			{
				"parent": "scarcelife:first_death",
				"display": {
					"icon": {
						"id": "minecraft:skeleton_skull"
					},
					"title": {
						"text": "Death's Door"
					},
					"description": {
						"text": "Have one life left."
					},
					"frame": "task",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"one_life": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("otherworldly_concoction", """
			{
				"parent": "scarcelife:pick_up_player_head",
				"display": {
					"icon": {
						"id": "minecraft:dragon_breath",
						"components": {
							"minecraft:enchantment_glint_override": true,
							"minecraft:custom_model_data": {
								"floats": [1.0]
							}
						}
					},
					"title": {
						"text": "Mad Scientist"
					},
					"description": {
						"text": "Create an otherworldly concoction."
					},
					"frame": "task",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"otherworldly_concoction": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("nine_lives", """
			{
				"parent": "scarcelife:craft_eden_apple",
				"display": {
					"icon": {
						"id": "minecraft:leather_boots",
						"components": {
							"minecraft:dyed_color": 0
						}
					},
					"title": {
						"text": "Fear Me, If You Dare!"
					},
					"description": {
						"text": "Have nine or more lives."
					},
					"frame": "challenge",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"nine_lives": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("ghost_revive", """
			{
				"parent": "scarcelife:one_life",
				"display": {
					"icon": {
						"id": "minecraft:totem_of_undying"
					},
					"title": {
						"text": "Hell and Back"
					},
					"description": {
						"text": "Eat an Eden Apple with no lives."
					},
					"frame": "challenge",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"ghost_revive": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("first_death", """
			{
				"parent": "scarcelife:root",
				"display": {
					"icon": {
						"id": "minecraft:wither_rose"
					},
					"title": {
						"text": "And Taxes"
					},
					"description": {
						"text": "Die for the first time."
					},
					"frame": "task",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"first_death": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");

		ADVANCEMENT_QUEUE.put("craft_knowledge_book", """
			{
				"parent": "scarcelife:root",
				"display": {
					"icon": {
						"id": "minecraft:knowledge_book",
						"components": {
							"minecraft:custom_model_data": {
								"floats": [1.0]
							}
						}
					},
					"title": {
						"text": "Knowledge is Power"
					},
					"description": {
						"text": "Craft a Tome of Knowledge."
					},
					"frame": "task",
					"show_toast": true,
					"announce_to_chat": true,
					"hidden": false
				},
				"criteria": {
					"craft_knowledge_book": {
						"trigger": "minecraft:impossible"
					}
				}
			}
		""");


		File worldFolder = Bukkit.getWorlds().getFirst().getWorldFolder();
		Path packFolder = worldFolder.toPath().resolve("datapacks").resolve("scarce-advancements");

		Path packMcmeta = packFolder.resolve("pack.mcmeta");
		Path advancementFolder = packFolder.resolve("data/scarcelife/advancement");

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			boolean advancementsChange = false;
			try {
				Files.createDirectories(advancementFolder);
				Files.writeString(packMcmeta, PACK_MCMETA);

				for (String advancementName : ADVANCEMENT_QUEUE.keySet()) {
					String advancementJson = ADVANCEMENT_QUEUE.get(advancementName);

					Path advancementFile = advancementFolder.resolve(advancementName + ".json");
					try {
						if (!Files.exists(advancementFile) || !Files.readString(advancementFile).equals(advancementJson)) {
							advancementsChange = true;
							Files.writeString(advancementFile, advancementJson);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				ADVANCEMENT_QUEUE.clear();
				created = true;

				if (advancementsChange) {
					Bukkit.getScheduler().runTask(plugin, () -> {
						CommandUtil.messageAllOpedPlayers(ChatColor.RED + "Advancements have been updated and the server needs to be restarted or reloaded using /reload to apply changes.");
					});
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static void grantAdvancement(Player player, String advancementName) {
		grantAdvancement(player, advancementName, advancementName);

	}

	public static void grantAdvancement(Player player,String advancementName, String criterion) {
		NamespacedKey advancementKey = new NamespacedKey(ScarceLife.NAMESPACE, advancementName);
		Advancement advancement = Bukkit.getServer().getAdvancement(advancementKey);
		if (advancement == null) {
			Bukkit.getLogger().info("Advancement not found: " + advancementKey);
			return;
		}

		AdvancementProgress progress = player.getAdvancementProgress(advancement);
		if (!progress.isDone()) {
			progress.awardCriteria(criterion);
		}
	}

}