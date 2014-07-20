package io.github.netdex.DeathSwap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

public class PlayerInterface {
	
	public static void help(Player player) {
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "=== Death Swap Help ===");
		player.sendMessage(ChatColor.GOLD + "/ds join : Joins the DeathSwap queue");
		player.sendMessage(ChatColor.GOLD + "/ds leave : Leaves the DeathSwap queue, or leaves during the game");
		player.sendMessage(ChatColor.GOLD + "/ds list : Lists all players in the DeathSwap queue");
		player.sendMessage(ChatColor.GOLD + "==================ADMIN COMMANDS=================");
		player.sendMessage(ChatColor.GOLD + "/dsa start : Starts the game [OP]");
		player.sendMessage(ChatColor.GOLD + "/dsa stop : Stops the game [OP]");
		player.sendMessage(ChatColor.GOLD + "v2.9 Created by Netdex");
		
	}
	
	public static void sendMessage(Player player, String s){
		player.sendMessage(ChatColor.BOLD + "[DeathSwap] " + ChatColor.GOLD + s);
	}
	
	public static void playerBroadcast(String s){
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for(Player player : players){
			if(DeathSwap.playerQueue.contains(player.getName())){
				sendMessage(player, s);
			}
		}
	}
	
	public static void allBroadcast(String s){
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		for(Player player : players){
			sendMessage(player, s);
		}
	}
	
	public static void checkWinner(){
		if(DeathSwap.playerQueue.size() == 1){
			String winner = DeathSwap.playerQueue.get(0);
			DeathSwap.playerQueue.clear();
			PlayerInterface.allBroadcast(winner + " has won DeathSwap!");
			Bukkit.getServer().getPlayer(winner).teleport(Bukkit.getWorld("world").getSpawnLocation());
			DeathSwap.gameRunning = false;
			DeathSwap.ps.kill();
		}
	}
	
	public static void loadWorld(){
		DeathSwap.world = Bukkit.getWorld("deathswap"); // Try to find world
		if(DeathSwap.world == null){ // If world does not exist, make one
			DeathSwap.world = new WorldCreator("deathswap").environment(World.Environment.NORMAL) // Parameters
					.generateStructures(true).type(WorldType.NORMAL).createWorld();
		}
	}
}
