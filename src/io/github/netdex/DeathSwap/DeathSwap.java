package io.github.netdex.DeathSwap;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathSwap extends JavaPlugin implements Listener {
	public static ArrayList<String> playerQueue = new ArrayList<String>();
	public static World world;
	public static boolean gameRunning = false;
	public static Thread t = new PlayerSwapper();
	public static String defaultWorld = "world";
	public static Plugin plugin;
	
	public void onEnable() {
		this.plugin = this;
		getServer().getPluginManager().registerEvents(this, this); // Register listeners
		PlayerInterface.loadWorld(); // Load world
		this.getCommand("ds").setExecutor(new CommandManager(this));
		this.getCommand("dsa").setExecutor(new CommandManager(this));
	}
	
	public void onDisable() {
		getLogger().info("Removing all players...");
		PlayerInterface.playerBroadcast("Game ended prematurely.");
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(playerQueue.contains(p.getName())){
				p.teleport(Bukkit.getServer().getWorld(defaultWorld).getSpawnLocation());
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event){ // Check dying players
		String name = event.getEntity().getName();
		Player player = Bukkit.getServer().getPlayer(name);
		if(playerQueue.contains(name) && gameRunning){ // Check if player is playing
			playerQueue.remove(name);
			PlayerInterface.checkWinner();
			PlayerInterface.playerBroadcast(name + " has died. " + playerQueue.size() + " remain.");
			player.teleport(Bukkit.getWorld(defaultWorld).getSpawnLocation());
		}
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if(playerQueue.contains(name) && gameRunning){
        	playerQueue.remove(name);
        	PlayerInterface.playerBroadcast(name + " has left DeathSwap.");
        }
        PlayerInterface.checkWinner();
    }
	
	@EventHandler
	public void onVehicleRide(org.bukkit.event.vehicle.VehicleEnterEvent event){
		String name = ((Player) event.getEntered()).getName();
		if(playerQueue.contains(name)){
			event.setCancelled(true);
			PlayerInterface.sendMessage((Player) event.getEntered(), "You cannot ride anything during DeathSwap.");
		}
	}
	
}
