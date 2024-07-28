package me.misha2win.scracesmpplugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;

import me.misha2win.scracesmpplugin.command.admin.assassin.AssassinCommandHandler;
import me.misha2win.scracesmpplugin.command.admin.assassin.AssassinTabCompleter;
import me.misha2win.scracesmpplugin.command.admin.life.LifeCommandHandler;
import me.misha2win.scracesmpplugin.command.admin.life.LifeTabCompleter;
import me.misha2win.scracesmpplugin.command.admin.sl.SLCommandHandler;
import me.misha2win.scracesmpplugin.command.admin.sl.SLTabCompleter;
import me.misha2win.scracesmpplugin.command.all.givelife.GiveLifeCommandHandler;
import me.misha2win.scracesmpplugin.command.all.givelife.GiveLifeTabCompleter;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaCommandHandler;
import me.misha2win.scracesmpplugin.command.all.tpa.tpa.TpaTabCompleter;
import me.misha2win.scracesmpplugin.command.all.tpa.tpaccept.TpacceptCommandHandler;
import me.misha2win.scracesmpplugin.command.all.tpa.tpaccept.TpacceptTabCompleter;
import me.misha2win.scracesmpplugin.command.all.tpa.tpcancel.TpcancelCommandHandler;
import me.misha2win.scracesmpplugin.command.all.tpa.tpcancel.TpcancelTabCompleter;
import me.misha2win.scracesmpplugin.command.all.tpa.tpdeny.TpdenyCommandHandler;
import me.misha2win.scracesmpplugin.command.all.tpa.tpdeny.TpdenyTabCompleter;
import me.misha2win.scracesmpplugin.handler.AssassinHandler;
import me.misha2win.scracesmpplugin.handler.DeadPlayerHandler;
import me.misha2win.scracesmpplugin.handler.EdenAppleConsumeHandler;
import me.misha2win.scracesmpplugin.handler.PlayerDeathListener;
import me.misha2win.scracesmpplugin.handler.PlayerJoinHandler;
import me.misha2win.scracesmpplugin.handler.WetSeasonHandler;
import me.misha2win.scracesmpplugin.recipe.RecipeMaker;

public class Main extends JavaPlugin {

	private long lastModified;
	private boolean isNoLongerModified;
	
	private WetSeasonHandler wetSeasonHandler;
	private AssassinHandler assassinHandler;

	@Override
	public void onEnable() {
		File dir = new File("plugins/" + this.getName());
        if (dir.exists()){
            getLogger().info(this.getName() + " folder already exists!");
        }
        else {
            dir.mkdir(); 
            getLogger().info(this.getName() + " folder created!");
        }
		
		String versionString = "Version 2.1";
		
		if (Bukkit.getPlayer("Milllennial") != null) {
			Bukkit.getPlayer("Milllennial").sendMessage(ChatColor.GREEN + "SL plugin is enabled! " + versionString);
		}

		if (Bukkit.getPlayer("Misha2win") != null) {
			Bukkit.getPlayer("Misha2win").sendMessage(ChatColor.GREEN + "SL plugin is enabled! " + versionString);
		}

		// Death listener setup
		Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);

		// Eden apple listener setup
		Bukkit.getPluginManager().registerEvents(new EdenAppleConsumeHandler(this), this);

		// Player join handler
		Bukkit.getPluginManager().registerEvents(new PlayerJoinHandler(this), this);

		// Wet season handler
		Bukkit.getPluginManager().registerEvents(wetSeasonHandler = new WetSeasonHandler(this), this);
		
		// Dead player handler
		Bukkit.getPluginManager().registerEvents(new DeadPlayerHandler(this), this);
		
		Bukkit.getPluginManager().registerEvents(assassinHandler = new AssassinHandler(this), this);

		
		// SL command setup
		registerCommand("scarce", new SLCommandHandler(this), new SLTabCompleter(this), "sl");

		// Life command setup
		registerCommand("life", new LifeCommandHandler(this), new LifeTabCompleter(this));

		// Givelife command setup
		registerCommand("givelife", new GiveLifeCommandHandler(this), new GiveLifeTabCompleter(this));

		// tpa command setup
		registerCommand("tpaccept", new TpacceptCommandHandler(this), new TpacceptTabCompleter(this));
		registerCommand("tpdeny", new TpdenyCommandHandler(this), new TpdenyTabCompleter(this));
		registerCommand("tpcancel", new TpcancelCommandHandler(this), new TpcancelTabCompleter(this));
		registerCommand("tpa", new TpaCommandHandler(this), new TpaTabCompleter(this));
		
		// assassin command setup
		registerCommand("assassin", new AssassinCommandHandler(this), new AssassinTabCompleter(this));
		
		// Recipes setup
		RecipeMaker rm = new RecipeMaker(this);
		rm.createAppleOfEdenRecipe();
		rm.createNameTagRecipe();
		rm.createSaddleRecipe();
		rm.createCheaperTNTRecipe();
		rm.createMoltenGoldRecipe();
		rm.createMoltenGoldBucketRecipe();

		// Setup scoreboard
		setupScoreboard(); // 
		
		try {
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			File file = (File) getFileMethod.invoke(this);
			lastModified = file.lastModified();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// TODO Remove this when no longer testing!
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			try {
				Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
				getFileMethod.setAccessible(true);
				File file = (File) getFileMethod.invoke(this);
				if (lastModified != file.lastModified()) {
					Bukkit.getLogger().info("The jarfile has been modified!");
					lastModified = file.lastModified();
					isNoLongerModified = true;
				} else {
					if (isNoLongerModified) {
						Bukkit.getLogger().info("The jarfile is done being modified! Reloading!");
						Bukkit.getScheduler().runTask(this, () -> {
							Bukkit.broadcastMessage(ChatColor.RED + "Restarting server plugin... Expect lag!");
							Bukkit.reload();
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 20, 20);
	}
	
	public WetSeasonHandler getWetSeasonHandler() {
		return wetSeasonHandler;
	}
	
	public AssassinHandler getAssassinHandler() {
		return assassinHandler;
	}

	@Override
	public void onDisable() {
		if (Bukkit.getPlayer("Milllennial") != null) {
			Bukkit.getPlayer("Milllennial").sendMessage(ChatColor.GREEN + "SL plugin is disabled!");
		}

		if (Bukkit.getPlayer("Misha2win") != null) {
			Bukkit.getPlayer("Misha2win").sendMessage(ChatColor.GREEN + "SL plugin is disabled!");
		}
		
		if (Bukkit.getPlayer("strawburby") != null) {
			Bukkit.getPlayer("strawburby").sendMessage(ChatColor.GREEN + "SL plugin is disabled!");
		}
	}

	public void registerCommand(String commandName, CommandExecutor ce, TabCompleter tc, String... aliases) {
		PluginCommand command = getCommand(commandName);
		command.setExecutor(ce);
		command.setTabCompleter(tc);
		command.setAliases(Arrays.asList(aliases));
	}

	public void setupScoreboard() {
		try {
			Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("health", Criteria.HEALTH, ChatColor.DARK_RED + "❤");
		} catch (Exception ex) {
			Bukkit.getLogger().info("health scoreboard team already exists!");
		} finally {
			Bukkit.getScoreboardManager().getMainScoreboard().getObjective("health").setDisplaySlot(DisplaySlot.BELOW_NAME);
		}

		try {
			Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("graylives");
		} catch (Exception ex) {
			Bukkit.getLogger().info("graylives scoreboard team already exists!");
		} finally {
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam("graylives").setColor(ChatColor.GRAY);
		}

		try {
			Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("lives", Criteria.DUMMY, "Lives");
		} catch (Exception ex) {
			Bukkit.getLogger().info("lives scoreboard objective already exists!");
		} finally {
			Bukkit.getScoreboardManager().getMainScoreboard().getObjective("lives").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		
		try {
			Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective("edenapples", Criteria.DUMMY, "Eden Apples Eaten");
		} catch (Exception ex) {
			Bukkit.getLogger().info("edenapples scoreboard objective already exists!");
		}
	}
	
	/**
	 * Statically gets the plugin instance of this plugin.
	 * For static use only!
	 * 
	 * @return this plugin's instance
	 */
	public static Main getInstance() {
		return JavaPlugin.getPlugin(Main.class);
	}

//	public static int[] findSwaps(int[] arr) {
//		if (arr.length != 10)
//			return null;
//
//		int[] oArr = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
//
//		int[] swaps = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1 };
//
//		for (int i = oArr.length; i > 1; --i) {
//			if (arr[i - 1] != oArr[i - 1]) {
//				for (int j = 0; j < oArr.length; j++) {
//					if (oArr[j] == arr[i - 1]) {
//						int temp = oArr[j];
//						oArr[j] = oArr[i - 1];
//						oArr[i - 1] = temp;
//						swaps[10 - i] = j;
//						break;
//					}
//				}
//			} else {
//				swaps[10 - i] = i - 1;
//			}
//		}
//		
//		return swaps;
//	}
//	
//	public static void findForkedSeed(int[] swaps) {
//		
//	}
//	
//	public static int[] findPillarsArrayFromHeights(int[] heights) {
//		if (heights.length != 10)
//			throw new IllegalArgumentException();
//		
//		int[] spikeVals = new int[10];
//		for (int i = 0; i < heights.length; i++) {
//			spikeVals[i] = (heights[i] - 76) / 3;
//		}
//		
//		return spikeVals;
//	}
//
//	public static void main(String[] args) { // STUFF FOR MINECRAFT SEED FINDING
//		int[] heights = new int[] { 85, 100, 103, 79, 82, 91, 94, 88, 76, 97 };
//		int[] pillars = findPillarsArrayFromHeights(heights);
//		int[] swaps = findSwaps(pillars);
//		
//		System.out.println("provided pillar heights: " + Arrays.toString(heights));
//		System.out.println("pillar numbers: " + Arrays.toString(pillars));
//		System.out.println("swaps to pillar order: " + Arrays.toString(swaps));
//		
//		findForkedSeed(swaps);
//	}
}
