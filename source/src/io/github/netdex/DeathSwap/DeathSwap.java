package io.github.netdex.DeathSwap;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
	public static Thread t;
	public static PlayerSwapper ps = new PlayerSwapper();
	public static Location defaultWorld;
	public Plugin plugin;
	public static FileConfiguration config;
	public static int maxPlayers;
	

	public void onEnable() {
		// Load and create configuration
		try{
			config = getConfig();
			File DeathSwap = new File("plugins" + File.separator + "DeathSwap" + File.separator + "config.yml");
			DeathSwap.mkdir();

			FunctionManager.setConfig();
			saveConfig();
		}catch(Exception e){
			e.printStackTrace();
		}

		this.plugin = this;
		getServer().getPluginManager().registerEvents(this, this); // Register listeners
		FunctionManager.loadWorld(); // Load world
		this.getCommand("ds").setExecutor(new CommandManager(this));
		this.getCommand("dsa").setExecutor(new CommandManager(this));

		defaultWorld = FunctionManager.parseLocation(config.getString("defaultWorld"));
		maxPlayers = DeathSwap.config.getInt("maxPlayers");
	}

	public void onDisable() {
		getLogger().info("Removing all players...");
		if(gameRunning){
			FunctionManager.playerBroadcast("Game ended prematurely.");
			for(Player p : Bukkit.getServer().getOnlinePlayers()){
				if(playerQueue.contains(p.getName())){
					p.teleport(defaultWorld);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent event){ // Check dying players
		String name = event.getEntity().getName();
		Player player = Bukkit.getServer().getPlayer(name);
		if(playerQueue.contains(name) && gameRunning){ // Check if player is playing
			FunctionManager.playerBroadcast(name + " has died. " + playerQueue.size() + " remain.");
			playerQueue.remove(name);
			player.teleport(defaultWorld);
			FunctionManager.checkWinner();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	/**
	 * When a player quits and the game is running, remove the players name from the queue, 
	 * then tell everyone and themselves that they have left, then teleport the player back to the spawn.
	 * After that, check for a winner.
	 */
	public void onPlayerQuit(PlayerQuitEvent event) {
		String name = event.getPlayer().getName();
		if(playerQueue.contains(name)){
			playerQueue.remove(name);
			if(gameRunning){
				FunctionManager.playerBroadcast(name + " has left DeathSwap.");
				Bukkit.getPlayer(name).teleport(defaultWorld);
				FunctionManager.checkWinner();
			}
			else{
				FunctionManager.playerBroadcast(name + " has been kicked out of the DeathSwap queue.");
			}
		}
	}

	@EventHandler
	public void onVehicleRide(org.bukkit.event.vehicle.VehicleEnterEvent event){
		String name = ((Player) event.getEntered()).getName();
		if(playerQueue.contains(name) && gameRunning){
			event.setCancelled(true);
			FunctionManager.sendMessage((Player) event.getEntered(), "You cannot ride anything during DeathSwap.");
		}
	}

	@EventHandler
	public void onPortalTeleport(org.bukkit.event.player.PlayerPortalEvent event){
		String name = event.getPlayer().getName();
		if(playerQueue.contains(name) && gameRunning){
			event.setCancelled(true);
			FunctionManager.sendMessage(event.getPlayer(), "You may not portal during DeathSwap.");
		}
	}

}
