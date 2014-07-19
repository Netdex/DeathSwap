package io.github.netdex.DeathSwap;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class CommandManager implements CommandExecutor {
	private final DeathSwap plugin;
 
	public CommandManager(DeathSwap plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Normal player command
		if(cmd.getName().equalsIgnoreCase("ds")){
			if(!(sender instanceof Player)){ // Player check
				sender.sendMessage("You must be a player to use this command.");
				return true;
			}
			Player player = (Player) sender;
			if(args.length == 0){ // Incorrect command
				PlayerInterface.help(player);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("join")){ // Join queue
				if(DeathSwap.gameRunning){ // Check if game running
					PlayerInterface.sendMessage(player, "The game is currently in progress.");
					return true;
				}
				if(DeathSwap.playerQueue.contains(player.getName())){ // Check if player in queue
					PlayerInterface.sendMessage(player, "You are already in the queue.");
					return true;
				}
				if(DeathSwap.playerQueue.size() == 4){ // Check if queue full
					PlayerInterface.sendMessage(player, "The DeathSwap lobby is currently full.");
					return true;
				}
				DeathSwap.playerQueue.add(player.getName()); // Normal action
				PlayerInterface.sendMessage(player, "You are " + DeathSwap.playerQueue.size() + "/4 in the queue.");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("leave")){ // Leave queue
				if(DeathSwap.gameRunning){ // Check if game running, if so, then register kill event
					if(!DeathSwap.playerQueue.contains(player.getName())){
						PlayerInterface.sendMessage(player, "You are not in the queue.");
						return true;
					}
					DeathSwap.playerQueue.remove(player.getName());
					PlayerInterface.playerBroadcast(player.getName() + " has left DeathSwap.");
					PlayerInterface.sendMessage(player, "You have left the game.");
					PlayerInterface.checkWinner();
					return true;
				}
				if(DeathSwap.playerQueue.contains(player.getName())){ // Check if player in queue
					DeathSwap.playerQueue.remove(player.getName());
					PlayerInterface.sendMessage(player, "You have been removed from the queue.");
					return true;
				}
				// Normal action
				PlayerInterface.sendMessage(player, "You are not in the queue.");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list")){ // List players
				PlayerInterface.sendMessage(player, "Player List : " + DeathSwap.playerQueue.size() + "/4");
				Player[] players = Bukkit.getServer().getOnlinePlayers();
				if(DeathSwap.playerQueue.size() == 0){
					player.sendMessage(ChatColor.GOLD + "There are no players in queue.");
					return true;
				}
				for(String p : DeathSwap.playerQueue){
					player.sendMessage(ChatColor.GOLD + p);
				}
				return true;
			}
			return false;
		}
		
		
		if(cmd.getName().equalsIgnoreCase("dsa")){ // Admin Commands
			if(!(sender instanceof Player)){ // Player check
				sender.sendMessage("You must be a player to use this command.");
				return true;
			}
			Player player = (Player) sender;
			if(args.length == 0){ // Check args length
				PlayerInterface.help(player);
				return true;
			}
			if(args[0].equalsIgnoreCase("start")){ // Starts game
				if(DeathSwap.playerQueue.size() < 2){ // Check enough players
					PlayerInterface.sendMessage(player, "Not enough players to start game.");
					return true;
				}
				if(DeathSwap.gameRunning){ // Check if game is already started
					PlayerInterface.sendMessage(player, "Game is already started.");
					return true;
				}
				DeathSwap.gameRunning = true; // Set game to running mode
				Random r = new Random();
				for(String ply : DeathSwap.playerQueue){ // Teleports all players into world
					Player p = Bukkit.getServer().getPlayer(ply);
					p.setGameMode(GameMode.SURVIVAL);
					int x = r.nextInt(10000);
					int z = r.nextInt(10000);
					
					while(Bukkit.getWorld("deathswap").getBiome(x, z) == Biome.OCEAN || Bukkit.getWorld("deathswap").getBiome(x, z) == Biome.DEEP_OCEAN){
						x = r.nextInt(10000);
						z = r.nextInt(10000);
					}
					
					p.teleport(new Location(Bukkit.getWorld("deathswap"), x, 128, z));
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 7)); // Resistance
					PlayerInterface.sendMessage(p, "You have a 15 second grace period. Enjoy falling.");
					
					// Reset stats
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setExp(0);
					PlayerInventory inv = p.getInventory();
					inv.clear();
				}
				DeathSwap.ps.revive();
				DeathSwap.world.setTime(6000L); // Daytime
				DeathSwap.t = new Thread(DeathSwap.ps); // Swap thread
				DeathSwap.t.start();
				return true;
			}
			
			if(args[0].equalsIgnoreCase("stop")){ // Stops game
				DeathSwap.gameRunning = false;
				PlayerInterface.playerBroadcast("Game has been stopped.");
				for(Player p : Bukkit.getServer().getOnlinePlayers()){
					if(DeathSwap.playerQueue.contains(p.getName())){
						p.teleport(Bukkit.getServer().getWorld(DeathSwap.defaultWorld).getSpawnLocation());
					}
				}
				DeathSwap.playerQueue.clear();
				DeathSwap.ps.kill();
				return true;
			}
			return false;
		}
		return false;
	}
}