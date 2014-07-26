package io.github.netdex.DeathSwap;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Menu implements Listener {
	private Inventory inv;
	private ItemStack join, leave, list;
	
	public Menu(Plugin p){
		inv = Bukkit.getServer().createInventory(null, 9, "DeathSwap Menu");
		
		ArrayList<String> joinLore = new ArrayList<String>();
		ArrayList<String> leaveLore = new ArrayList<String>();
		ArrayList<String> listLore = new ArrayList<String>();
		joinLore.add("Adds you to the DeathSwap queue.");
		leaveLore.add("Removes you from the DeathSwap queue.");
		leaveLore.add("If you are in a game, then you leave the game.");
		listLore.add("Lists all playerse in the DeathSwap queue.");
		
		join = createItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Join", joinLore);
		leave = createItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Leave", leaveLore);
		list = createItem(Material.PAPER, ChatColor.GOLD + "List Players", listLore);
		
		inv.setItem(4, join);
		inv.setItem(5, leave);
		inv.setItem(6, list);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}
	
	private ItemStack createItem(Material m, String name, ArrayList<String> lore){
		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
	
	public void show(Player p){
		p.openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(!(event.getInventory().equals(inv)))
			return;
		Player clicker = (Player) event.getWhoClicked();
		if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Join")){
			event.setCancelled(true);
			GameHandler.joinGame(clicker);
		}
		else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Leave")){
			event.setCancelled(true);
			GameHandler.leaveGame(clicker);
		}
		else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("List Players")){
			event.setCancelled(true);
			GameHandler.listPlayers(clicker);
		}
	}
}
