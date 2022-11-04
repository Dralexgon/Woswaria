package fr.firedragonalex.rankandlevels.gui;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.firedragonalex.rankandlevels.Main;
import fr.firedragonalex.rankandlevels.rank.Rank;
import fr.firedragonalex.spellandweapon.custom.code.CustomPlayer;

public class Gui {
	
	public static void GuiLevelRewards(Player player,int number,Rank rank) {
		int page = Math.floorDiv(number, 7);
		
		List<Rank> listOfRanks = Arrays.asList(Rank.values());
		int rankNumber = Math.floorDiv(listOfRanks.indexOf(rank), 2);
		
		Inventory gui = Bukkit.createInventory(null, 5*9, "�1LevelRewards;page:"+String.valueOf(page)+";rank:"+rankNumber);
		
		for (int i = 0; i < 5*9; i++) {
			gui.setItem(i,Main.customItem(Material.BLACK_STAINED_GLASS_PANE, "�"));
		}
		
		if (page != 0) {
			gui.setItem(9*2,Main.customHead(ChatColor.YELLOW+"Pr�c�dent","MHF_ArrowLeft"));
		}
		gui.setItem(9*3-1,Main.customHead(ChatColor.YELLOW+"Suivant","MHF_ArrowRight"));
		if (rankNumber != 0) {
			gui.setItem(9*4+4,Main.customHead(ChatColor.YELLOW+"Grade moins haut","MHF_ArrowDown"));
		}
		if (rankNumber != Rank.values().length - 4) {
			gui.setItem(4,Main.customHead(ChatColor.YELLOW+"Grade plus haut","MHF_ArrowUp"));
		}
		
		Gui.fillItems(rankNumber, page, player, gui);
		
		player.openInventory(gui);
	}
	
	private static void fillItems(int rankNumber, int page, Player player, Inventory gui) {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 2; j++) {
				Rank rank = Rank.values()[rankNumber+j];
				ItemStack item = null;
				int number = page*7+i+1;
				if (Main.getCustomRewards().containsKey(number) && Main.getCustomRewards().get(number).containsKey(rank)) {
					item = Main.getCustomRewards().get(number).get(rank);
				} else {
					if (!(number % 3 == 0)) {
						item = Main.customItem(Material.GOLD_NUGGET, ChatColor.YELLOW+"Niveau "+number+" (Normal,"+rank.getName()+")");
					}
					else if (!(number % 6 == 0)) {
						item = Main.customItem(Material.GOLD_INGOT, ChatColor.YELLOW+"Niveau "+number+" (Rare,"+rank.getName()+")");
					}
					else if (!(number % 12 == 0)) {
						item = Main.customItem(Material.GOLD_BLOCK, ChatColor.YELLOW+"Niveau "+number+" (Tr�s rare,"+rank.getName()+")");
					}
					else if (!(number % 36 == 0)) {
						item = Main.customItem(Material.AMETHYST_SHARD, ChatColor.YELLOW+"Niveau "+number+" (Epique,"+rank.getName()+")");
					} 
					else {
						item = Main.customItem(Material.END_CRYSTAL, ChatColor.YELLOW+"Niveau "+number+" (L�gendaire,"+rank.getName()+")");
					}
				}
				Gui.enchant(number, rank, player, item);
				gui.setItem(i+28-j*18,item);
			}
		}
	}
	
	private static void enchant(int number, Rank rank, Player player, ItemStack item) {
		CustomPlayer customPlayer = fr.firedragonalex.spellandweapon.Main.getCustomPlayerByPlayer(player);
		if (customPlayer == null) {
			player.kickPlayer("Error : no customPlayer ! Contacter un administrateur !");
		}
		if (number <= customPlayer.getLevel()) {
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemMeta.setLore(Arrays.asList(ChatColor.GREEN+"D�bloqu�"));
			item.setItemMeta(itemMeta);
		}
		UUID id = player.getUniqueId();
		if (Main.getLevelRewardsAdvancement().containsKey(id) && Main.getLevelRewardsAdvancement().get(id).containsKey(rank)) {
			if (number <= Main.getLevelRewardsAdvancement().get(id).get(rank)) {
				item.setAmount(1);
				if (item.getItemMeta().getDisplayName().contains("Normal")) {
					item.setType(Material.IRON_NUGGET);
				}
				if (item.getItemMeta().getDisplayName().contains("Rare")) {
					item.setType(Material.IRON_INGOT);
				}
				if (item.getItemMeta().getDisplayName().contains("Tr�s rare")) {
					item.setType(Material.IRON_BLOCK);
				}
				if (item.getItemMeta().getDisplayName().contains("Epique")) {
					item.setType(Material.FEATHER);
				}
				if (item.getItemMeta().getDisplayName().contains("L�gendaire")) {
					item.setType(Material.NETHER_STAR);
				}
				ItemStack tempItem = new ItemStack(Material.COAL);
				ItemMeta itemMeta = tempItem.getItemMeta();
				itemMeta.setDisplayName("R�cup�r�");
				itemMeta.setLore(Arrays.asList());
				item.setItemMeta(itemMeta);
				item.removeEnchantment(Enchantment.DURABILITY);
			}
		}
	}

}