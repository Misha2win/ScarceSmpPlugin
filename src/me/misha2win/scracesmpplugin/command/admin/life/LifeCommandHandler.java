package me.misha2win.scracesmpplugin.command.admin.life;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

import me.misha2win.scracesmpplugin.LifeManager;
import me.misha2win.scracesmpplugin.Main;
import me.misha2win.scracesmpplugin.util.CommandUtil;

public class LifeCommandHandler implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public LifeCommandHandler(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Sender must have op
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		
		// Command must be valid
		if (args.length != 3) {
			return false;
		}
		
		// Third argument must be a valid integer!
		int lives = 0;
		
		try {
			lives = Integer.valueOf(args[2]);
			
			if (lives < 0) {
				sender.sendMessage(ChatColor.RED + "You cannot give less than 0 lives!");
				return true;
			}
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + "The third argument must be a valid integer!");
			return true;
		}
		
		// Receiving player must exist or sender uses all
		if (args[1].equals("all")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				onCommand(sender, cmd, label, new String[] { args[0], p.getName(), args[2] });
			}
			return true;
		} else if (Bukkit.getPlayer(args[1]) == null) {
			sender.sendMessage(ChatColor.RED + "The second argument must be an online player! Use 'all' to give lives to everyone online!");
			return true;
		}
		Player player = Bukkit.getPlayer(args[1]);
		
		// The first argument must be give, remove, or set
		if (args[0].equals("add")) {
			addLives(player, lives);
			CommandUtil.logCommand(sender, "gave " + lives + " lives to " + player.getDisplayName());
		} else if (args[0].equals("remove")) {
			removeLives(player, lives);
			CommandUtil.logCommand(sender, "removed " + lives + " lives from " + player.getDisplayName());
		} else if (args[0].equals("set")) {
			setLives(player, lives);
			player.sendMessage(ChatColor.GOLD + "Your lives have been set to " + lives + "!");
			CommandUtil.logCommand(sender, "set " + player.getDisplayName() + ChatColor.GRAY + "'s lives to " + lives);
		} else {
			sender.sendMessage(ChatColor.RED + "The first arguemnt must be 'give', 'remove', or 'set'!");
			return true;
		}
	
		
		return true;
	}
	
	public void addLives(Player player, int numOfLives) {
		setLives(player, LifeManager.getLivesScoreboard(player).getScore() + numOfLives);

		if (numOfLives == 1)
			player.sendMessage(ChatColor.GREEN + "You have received a live!");
		else
			player.sendMessage(ChatColor.GREEN + "You have received " + numOfLives + " lives!");
		
	}
	
	public void removeLives(Player player, int numOfLives) {
		setLives(player, LifeManager.getLivesScoreboard(player).getScore() - numOfLives);

		if (numOfLives == 1)
			player.sendMessage(ChatColor.RED + "One life has been taking from you!");
		else
			player.sendMessage(ChatColor.RED + (numOfLives + " lives have been taken from you!"));
		
	}
	
	public void setLives(Player player, int numOfLives) {
		Score playerScore = LifeManager.getLivesScoreboard(player);
		
		if (playerScore.getScore() > numOfLives) {
			playerScore.setScore(numOfLives + 1);
			LifeManager.removeLife(player);
		} else if (playerScore.getScore() < numOfLives) {
			playerScore.setScore(numOfLives - 1);
			LifeManager.addLife(player);
		} else {
			// Nothing changed!
		}
	}

}
