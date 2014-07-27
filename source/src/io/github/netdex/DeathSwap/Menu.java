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
		inv = Bukkit.getServer().createInventory(null, 9, ChatColor.DARK_RED + "DeathSwap Menu");
		
		ArrayList<String> joinLore = new ArrayList<String>();
		ArrayList<String> leaveLore = new ArrayList<String>();
		ArrayList<String> listLore = new ArrayList<String>();
		joinLore.add(ChatColor.GRAY + "Adds you to the DeathSwap queue.");
		leaveLore.add(ChatColor.GRAY + "Removes you from the DeathSwap queue.");
		leaveLore.add(ChatColor.GRAY + "If you are in a game, then you leave the game.");
		listLore.add(ChatColor.GRAY + "Lists all players in the DeathSwap queue.");
		
		join = createItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Join", joinLore, (short) 0);
		leave = createItem(Material.REDSTONE_BLOCK, ChatColor.RED + "Leave", leaveLore, (short) 0);
		list = createItem(Material.PAPER, ChatColor.GOLD + "List Players", listLore, (short) 0);
		
		inv.setItem(3, join);
		inv.setItem(4, leave);
		inv.setItem(5, list);
		
		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}
	
	private ItemStack createItem(Material m, String name, ArrayList<String> lore, short damage){
		ItemStack is = new ItemStack(m, 1, damage);
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
		try{
			if(!(event.getInventory().getName().equalsIgnoreCase(inv.getName())))
				return;
			if(event.getCurrentItem().getType().equals(Material.AIR))
				return;
			Player clicker = (Player) event.getWhoClicked();
			if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Join")){
				GameHandler.joinGame(clicker);
				clicker.closeInventory();
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Leave")){
				GameHandler.leaveGame(clicker);
				clicker.closeInventory();
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("List Players")){
				GameHandler.listPlayers(clicker);
				clicker.closeInventory();
			}
			event.setCancelled(true);
		}catch(Exception e){
			event.setCancelled(true);
		}
	}
}
