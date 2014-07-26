package io.github.netdex.DeathSwap;

import java.util.Random;

import org.bukkit.Bukkit;
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
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Normal player command
		if(cmd.getName().equalsIgnoreCase("ds")){
			if(!(sender instanceof Player)){ // Player check
				sender.sendMessage("You must be a player to use this command.");
				return true;
			}
			Player player = (Player) sender;
			if(args.length == 0){ // Open menu
				DeathSwap.menu.show(player);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("join")){ // Join queue
				GameHandler.joinGame(player);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("leave")){ // Leave queue
				GameHandler.leaveGame(player);
			}
			
			if(args[0].equalsIgnoreCase("list")){ // List players
				GameHandler.listPlayers(player);
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
				FunctionManager.help(player);
				return true;
			}
			if(args[0].equalsIgnoreCase("start")){ // Starts game
				if(DeathSwap.playerQueue.size() < 2){ // Check enough players
					FunctionManager.sendMessage(player, "Not enough players to start game.");
					return true;
				}
				if(DeathSwap.gameRunning){ // Check if game is already started
					FunctionManager.sendMessage(player, "Game is already started.");
					return true;
				}
				
				if(DeathSwap.t != null){
					if(DeathSwap.t.isAlive()){
						DeathSwap.t.stop();
					}
				}
				Random r = new Random();
				for(String ply : DeathSwap.playerQueue){ // Teleports all players into world
					Player p = Bukkit.getServer().getPlayer(ply);
					p.setGameMode(GameMode.SURVIVAL);
					int x = r.nextInt(10000);
					int z = r.nextInt(10000);
					
					// Avoid these biomes
					while(Bukkit.getWorld("deathswap").getBiome(x, z) == Biome.OCEAN || Bukkit.getWorld("deathswap").getBiome(x, z) == Biome.DEEP_OCEAN){
						x = r.nextInt(10000);
						z = r.nextInt(10000);
					}
					
					p.teleport(new Location(Bukkit.getWorld("deathswap"), x, 128, z));
					int invincibility = DeathSwap.config.getInt("invincibleTicks");
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, invincibility, 7)); // Resistance
					FunctionManager.sendMessage(p, "You have a " + (invincibility/20) + " second invulnerability period. Enjoy falling.");
					
					// Reset stats
					p.setHealth(20);
					p.setFoodLevel(20);
					p.setExp(0);
					PlayerInventory inv = p.getInventory();
					inv.clear();
				}
				DeathSwap.gameRunning = true; // Set game to running mode
				DeathSwap.ps.revive();
				DeathSwap.world.setTime(6000L); // Daytime
				DeathSwap.t = new Thread(DeathSwap.ps); // Swap thread
				DeathSwap.t.start();
				return true;
			}
			
			if(args[0].equalsIgnoreCase("stop")){ // Stops game
				if(DeathSwap.gameRunning){
					DeathSwap.gameRunning = false;
					FunctionManager.playerBroadcast("Game has been stopped.");
					for(Player p : Bukkit.getServer().getOnlinePlayers()){
						if(DeathSwap.playerQueue.contains(p.getName())){
							p.teleport(DeathSwap.defaultWorld);
						}
					}
					DeathSwap.playerQueue.clear();
					DeathSwap.ps.kill();
					DeathSwap.ps.interrupt();
					DeathSwap.t.interrupt();
				}
				else{
					FunctionManager.sendMessage(player, "DeathSwap is not running.");
				}
				
				return true;
			}
			
			if(args[0].equalsIgnoreCase("config")){
				if(args.length > 1){
					if(args[1].equalsIgnoreCase("setDefaultWorld")){
						Location loc = player.getLocation();
						String location = (loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
						DeathSwap.config.set("defaultWorld", location);
						FunctionManager.sendMessage(player, "Default world set to player location.");
						plugin.saveConfig();
						return true;
					}
					return false;
				}
				else{
					FunctionManager.help(player);
					return true;
				}
			}
			return false;
		}
		return false;
	}
}