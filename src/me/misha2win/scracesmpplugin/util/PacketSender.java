package me.misha2win.scracesmpplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class PacketSender {

	/**
	 * Lies to everyone who is online that a specific player is in no one else's team.
	 * Used to make dead or alive players invisible to everyone.
	 *
	 * @param player the player to tell everyone else about.
	 */
	public static void tellEveryonePlayerJoinedOwnTeam(Player player, Player... excludedPlayers) {
		outer:
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			for (Player excluded : excludedPlayers) {
				if (otherPlayer.equals(excluded)) {
					continue outer;
				}
			}

			if (otherPlayer.equals(player)) {
				continue;
			}

			PacketSender.sendTeamJoinPacket(otherPlayer, player, CommandUtil.getTeamOfPlayer(player));
		}
	}

	/**
	 * Lies to everyone who is online that a specific player joined their team.
	 * Used to make dead players visible as ghost to dead or alive players.
	 *
	 * @param player the player to tell everyone else about.
	 */
	public static void tellEveryonePlayerJoinedTheirTeam(Player player, Player... excludedPlayers) {
		ChatColor playerColor = CommandUtil.getTeamOfPlayer(player).getColor();

		outer:
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {  // Make all online dead players join alive players team
			for (Player excluded : excludedPlayers) {
				if (otherPlayer.equals(excluded)) {
					continue outer;
				}
			}

			if (otherPlayer.equals(player)) {
				continue;
			}

			PacketSender.sendTeamJoinPacket(otherPlayer, player, CommandUtil.getTeamOfPlayer(otherPlayer));
			PacketSender.sendTeamColorPacket(otherPlayer, playerColor);
		}
	}

	/**
	 * Lies to a player that their team color changed.
	 *
	 * @param player the player to change the team color of.
	 * @param color the new color the player's team should be.
	 */
	public static void sendTeamColorPacket(Player player, ChatColor color) {
		Team playerTeam = CommandUtil.getTeamOfPlayer(player);

		if (playerTeam == null)
			return;

		PlayerTeam fakeCopyTeam = new PlayerTeam(new Scoreboard(), playerTeam.getName());
		fakeCopyTeam.setColor(CraftChatMessage.getColor(color));
		((CraftPlayer) player).getHandle().connection.sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(fakeCopyTeam, true));
	}

	/**
	 * Lies to a player that another player joined a specific team.
	 *
	 * @param player the player to lie to.
	 * @param joiningPlayer the player to lie about.
	 * @param team the team to lie to <code>player</code> about.
	 */
	public static void sendTeamJoinPacket(Player player, Player joiningPlayer, Team team) {
		sendTeamPacket(player, joiningPlayer, team, 0);
	}

	/**
	 * Lies to a player that another player left a specific team.
	 *
	 * @param player the player to lie to.
	 * @param joiningPlayer the player to lie about.
	 * @param team the team to lie to <code>player</code> about.
	 */
	public static void sendTeamLeavePacket(Player player, Player leavingPlayer, Team team) {
		sendTeamPacket(player, leavingPlayer, team, 1);
	}

	/**
	 * Lies to a player that another player joined their team.
	 *
	 * @param player the player to lie to
	 * @param joiningPlayer the player to lie about
	 */
	public static void sendTeamJoinPacket(Player player, Player joiningPlayer) {
		sendTeamPacket(player, joiningPlayer, 0);
	}

	/**
	 * Lies to a player that another player left their team.
	 *
	 * @param player the player to lie to
	 * @param joiningPlayer the player to lie about
	 */
	public static void sendTeamLeavePacket(Player player, Player leavingPlayer) {
		sendTeamPacket(player, leavingPlayer, 1);
	}

	private static void sendTeamPacket(Player player, Player otherPlayer, int actionNum) {
		sendTeamPacket(player, otherPlayer, CommandUtil.getTeamOfPlayer(player), actionNum);
	}

	private static void sendTeamPacket(Player player, Player otherPlayer, Team team, int actionNum) {
		ClientboundSetPlayerTeamPacket.Action action = ClientboundSetPlayerTeamPacket.Action.values()[actionNum];

		PlayerTeam fakeCopyTeam = new PlayerTeam(new Scoreboard(), team.getName());
		ClientboundSetPlayerTeamPacket teamPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(fakeCopyTeam, otherPlayer.getName(), action);
		((CraftPlayer) player).getHandle().connection.sendPacket(teamPacket);
	}

}