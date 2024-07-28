package me.misha2win.scracesmpplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;

public class PacketSender {
	
	public static void tellEveryonePlayerJoinedOwnTeam(Player player) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {  // Make all online dead players join alive players team
			if (otherPlayer.equals(player)) {
				continue;
			}
			
			PacketSender.sendTeamJoinPacket(otherPlayer, player, CommandUtil.getTeamOfPlayer(player));
		}
	}
	
	public static void tellEveryonePlayerJoinedTheirTeam(Player player) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {  // Make all online dead players join alive players team
			if (otherPlayer.equals(player)) {
				continue;
			}
			
			PacketSender.sendTeamJoinPacket(otherPlayer, player, CommandUtil.getTeamOfPlayer(otherPlayer));
		}
	}
	
	public static void sendTeamColorPacket(Player player, ChatColor color) {
		Team playerTeam = CommandUtil.getTeamOfPlayer(player);
		
		if (playerTeam == null)
			return;
		
		ScoreboardTeam fakeCopyTeam = new ScoreboardTeam(new Scoreboard(), playerTeam.getName());
		fakeCopyTeam.a(CraftChatMessage.getColor(color)); // This changes the color of the team
		((CraftPlayer) player).getHandle().c.sendPacket(PacketPlayOutScoreboardTeam.a(fakeCopyTeam, true));
	}
	
	public static void sendTeamJoinPacket(Player player, Player joiningPlayer, Team team) {
		sendTeamPacket(player, joiningPlayer, team, 0);
	}
	
	public static void sendTeamLeavePacket(Player player, Player leavingPlayer, Team team) {
		sendTeamPacket(player, leavingPlayer, team, 1);
	}
	
	public static void sendTeamJoinPacket(Player player, Player joiningPlayer) {
		sendTeamPacket(player, joiningPlayer, 0);
	}
	
	public static void sendTeamLeavePacket(Player player, Player leavingPlayer) {
		sendTeamPacket(player, leavingPlayer, 1);
	}
	
	private static void sendTeamPacket(Player player, Player otherPlayer, int actionNum) {
		sendTeamPacket(player, otherPlayer, CommandUtil.getTeamOfPlayer(player), actionNum);
	}
	
	private static void sendTeamPacket(Player player, Player otherPlayer, Team team, int actionNum) {
		// Since the Enum im trying to access has the same obfustcated name as a static field, reflection must be used to get it.
		PacketPlayOutScoreboardTeam.a action = null;
		try {
			for (Class<?> clazz : PacketPlayOutScoreboardTeam.class.getDeclaredClasses()) {
				if (clazz.isEnum()) {
					action = (PacketPlayOutScoreboardTeam.a) clazz.getEnumConstants()[actionNum % 2];
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ScoreboardTeam fakeCopyTeam = new ScoreboardTeam(new Scoreboard(), team.getName());
		PacketPlayOutScoreboardTeam teamPacket = PacketPlayOutScoreboardTeam.a(fakeCopyTeam, otherPlayer.getName(), action);
		((CraftPlayer) player).getHandle().c.sendPacket(teamPacket);
	}

}