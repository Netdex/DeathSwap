package io.github.netdex.DeathSwap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerSwapper extends Thread {
	private static volatile boolean isRunning = true;
	private static Random r = new Random();
	
	public void run(){
		// Get values from config
		int graceTime = DeathSwap.config.getInt("graceTime");
		int minSwap = DeathSwap.config.getInt("minSwapTime");
		int maxSwap = DeathSwap.config.getInt("maxSwapTime");
		
		try {Thread.sleep(graceTime * 1000);} catch (InterruptedException e1) {}
		while(isRunning){
			FunctionManager.playerBroadcast("Swap!");
			
			swap();
			
			long sleep = (r.nextInt(maxSwap - minSwap) + minSwap) * 1000;
			try {Thread.sleep(sleep);} catch (InterruptedException e) {}
		}
	}
	
	public void kill(){
		isRunning = false;
	}
	
	public void revive(){
		isRunning = true;
	}
	
	@SuppressWarnings("deprecation")
	public static void swap(){
		ArrayList<String> randomNames = new ArrayList<String>();
		for(String name : DeathSwap.playerQueue){ // Copy the list
			randomNames.add(name);
		}
		Collections.shuffle(randomNames); // Shuffle
		Location firstPlayer = Bukkit.getServer().getPlayer(randomNames.get(0)).getLocation();
		for(int i = 0; i < randomNames.size(); i++){
			Player p = Bukkit.getServer().getPlayer(randomNames.get(i));
			if(i == randomNames.size() - 1){
				p.teleport(firstPlayer);
			}
			else{
				p.teleport(Bukkit.getServer().getPlayer(randomNames.get(i+1)).getLocation());
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 175 ,10));
		}
	}
	
	public static void giveGift(Player player){
		int giveChance = r.nextInt(3);
		if(giveChance == 0){
			PlayerInventory inv = player.getInventory();
			int rand = r.nextInt(13);
			if(rand >= 0 && rand < 4){
				FunctionManager.sendMessage(player, "You have been gifted some redstone!");
				ItemStack redstone = new ItemStack(Material.REDSTONE, 6);
				inv.addItem(redstone);
			}
			else if(rand >= 4 && rand < 8){
				FunctionManager.sendMessage(player, "You have been gifted some obsidian!");
				ItemStack obsidian = new ItemStack(Material.OBSIDIAN, 5);
				inv.addItem(obsidian);
			}
			else if(rand >=8 && rand < 10){
				FunctionManager.sendMessage(player, "You have been gifted a nether wart!");
				ItemStack netherwart = new ItemStack(Material.NETHER_WARTS, 1);
				inv.addItem(netherwart);
			}
			else if(rand == 10){
				FunctionManager.sendMessage(player, "You have been gifted some TNT!");
				ItemStack tnt = new ItemStack(Material.TNT, 1);
				inv.addItem(tnt);
			}
			else if(rand == 11){
				FunctionManager.sendMessage(player, "You have been gifted a blaze rod!");
				ItemStack blazerod = new ItemStack(Material.BLAZE_ROD, 1);
				inv.addItem(blazerod);
			}
			else if(rand == 12){
				FunctionManager.sendMessage(player, "You have been gifted a piston!");
				ItemStack piston = new ItemStack(Material.PISTON_STICKY_BASE, 1);
				inv.addItem(piston);
			}
			player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 10, 1);
		}
		
	}
}
