package io.github.netdex.DeathSwap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInterface {
	
	public static void help(Player player) {
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "=== Death Swap Help ===");
		player.sendMessage(ChatColor.GOLD + "/ds join : Joins the DeathSwap queue");
		player.sendMessage(ChatColor.GOLD + "/ds leave : Leaves the DeathSwap queue, or leaves during the game");
		player.sendMessage(ChatColor.GOLD + "/ds list : Lists all players in the DeathSwap queue");
		player.sendMessage(ChatColor.GOLD + "==================ADMIN COMMANDS=================");
		player.sendMessage(ChatColor.GOLD + "/dsa start : Starts the game [OP]");
		player.sendMessage(ChatColor.GOLD + "/dsa stop : Stops the game [OP]");
		player.sendMessage(ChatColor.GOLD + "/dsa config setDefaultWorld : Sets the default world [OP]");
		player.sendMessage(ChatColor.GOLD + "v3.4.3 Created by Netdex");
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
	
	@SuppressWarnings("deprecation")
	public static void checkWinner(){
		if(DeathSwap.playerQueue.size() == 1){
			String winner = DeathSwap.playerQueue.get(0);
			DeathSwap.playerQueue.clear();
			PlayerInterface.allBroadcast(winner + " has won DeathSwap!");
			Bukkit.getServer().getPlayer(winner).teleport(DeathSwap.defaultWorld);
			PlayerInventory inv = Bukkit.getServer().getPlayer(winner).getInventory();
			inv.clear();
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
	
	public static void setConfig(){
		// Check if config contains necessary values
		if(!DeathSwap.config.contains("invincibleTicks"))
			DeathSwap.config.set("invincibleTicks", 600);
		if(!DeathSwap.config.contains("graceTime"))
			DeathSwap.config.set("graceTime", 60);
		if(!DeathSwap.config.contains("minSwapTime"))
			DeathSwap.config.set("minSwapTime", 60);
		if(!DeathSwap.config.contains("maxSwapTime"))
			DeathSwap.config.set("maxSwapTime", 120);
		if(!DeathSwap.config.contains("maxPlayers"))
			DeathSwap.config.set("maxPlayers", 4);
		if(!DeathSwap.config.contains("defaultWorld")){
			Location loc = Bukkit.getWorld("world").getSpawnLocation();
			String location = (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
			DeathSwap.config.set("defaultWorld", location);
		}
	}
	
	public static Location parseLocation(String location){
		String[] loc = location.split("\\|");
		 
		World world = Bukkit.getWorld(loc[0]);
		Double x = Double.parseDouble(loc[1]);
		Double y = Double.parseDouble(loc[2]);
		Double z = Double.parseDouble(loc[3]);
		 
		Location finalloc = new Location(world, x, y, z);
		return finalloc;
	}
	
}
