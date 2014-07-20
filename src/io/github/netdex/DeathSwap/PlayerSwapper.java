package io.github.netdex.DeathSwap;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSwapper extends Thread {
	public static boolean isRunning = true;
	
	public void run(){
		Random r = new Random();
		try {Thread.sleep(60000);} catch (InterruptedException e1) {}
		PlayerInterface.playerBroadcast("2 minutes remaining before swapping.");
		try {Thread.sleep(60000);} catch (InterruptedException e1) {}
		PlayerInterface.playerBroadcast("1 minute remaining before swapping.");
		try {Thread.sleep(60000);} catch (InterruptedException e1) {}
		while(isRunning){
			PlayerInterface.playerBroadcast("Swap!");
			
			swap();
			
			long sleep = (r.nextInt(60) + 60) * 1000;
			try {Thread.sleep(sleep);} catch (InterruptedException e) {}
		}
	}

	public static void swap(){
		Location firstPlayer = Bukkit.getServer().getPlayer(DeathSwap.playerQueue.get(0)).getLocation();
		for(int i = 0; i < DeathSwap.playerQueue.size(); i++){
			Player p = Bukkit.getServer().getPlayer(DeathSwap.playerQueue.get(i));
			if(i == DeathSwap.playerQueue.size() - 1){
				p.teleport(firstPlayer);
			}
			else{
				p.teleport(Bukkit.getServer().getPlayer(DeathSwap.playerQueue.get(i+1)).getLocation());
			}
		}
	}
}
