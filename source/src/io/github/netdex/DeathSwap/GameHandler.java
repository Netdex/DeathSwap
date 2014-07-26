package io.github.netdex.DeathSwap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameHandler {
	public static void joinGame(Player player){
		if(DeathSwap.gameRunning){ // Check if game running
			FunctionManager.sendMessage(player, "The game is currently in progress.");
			return;
		}
		if(DeathSwap.playerQueue.contains(player.getName())){ // Check if player in queue
			FunctionManager.sendMessage(player, "You are already in the queue.");
			return;
		}
		if(DeathSwap.playerQueue.size() == DeathSwap.maxPlayers){ // Check if queue full
			FunctionManager.sendMessage(player, "The DeathSwap lobby is currently full.");
			return;
		}
		DeathSwap.playerQueue.add(player.getName()); // Normal action
		FunctionManager.sendMessage(player, "You are " + DeathSwap.playerQueue.size() + "/" + DeathSwap.maxPlayers + " in the queue.");
	}
	
	public static void leaveGame(Player player){
		if(DeathSwap.gameRunning){ // Check if game running, if so, then register kill event
			if(!DeathSwap.playerQueue.contains(player.getName())){ // Check if player is in the queue
				FunctionManager.sendMessage(player, "You are not in the queue.");
				return;
			}
			// Remove player from queue, tell everyone they have left, teleport them back, check winner
			DeathSwap.playerQueue.remove(player.getName());
			FunctionManager.playerBroadcast(player.getName() + " has left DeathSwap.");
			FunctionManager.sendMessage(player, "You have left the game.");
			player.teleport(DeathSwap.defaultWorld);
			FunctionManager.checkWinner();
			return;
		}
		if(DeathSwap.playerQueue.contains(player.getName())){ // Check if player in queue
			DeathSwap.playerQueue.remove(player.getName());
			FunctionManager.sendMessage(player, "You have been removed from the queue.");
			return;
		}
		// Normal action
		FunctionManager.sendMessage(player, "You are not in the queue.");
	}
	
	public static void listPlayers(Player player){
		FunctionManager.sendMessage(player, "Player List : " + DeathSwap.playerQueue.size() + "/" + DeathSwap.maxPlayers);
		if(DeathSwap.playerQueue.size() == 0){
			player.sendMessage(ChatColor.GOLD + "There are no players in queue.");
			return;
		}
		for(String p : DeathSwap.playerQueue){
			player.sendMessage(ChatColor.GOLD + p);
		}
		return;
	}
}
