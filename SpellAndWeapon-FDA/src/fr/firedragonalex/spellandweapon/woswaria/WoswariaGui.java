package fr.firedragonalex.spellandweapon.woswaria;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.firedragonalex.spellandweapon.custom.easytoadd.Craft;

public class WoswariaGui {
	
	public static void startGui_ResourcePack(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 3*9, "�1WoswariaGUI_DownloadRessourcePack");
		
		for (int i = 0; i < 3*9; i++) {
			inventory.setItem(i,Craft.customItem(Material.BLACK_STAINED_GLASS_PANE, "�"));
		}
		
		inventory.setItem(9*1+1,Craft.customItem(Material.GREEN_CONCRETE,ChatColor.DARK_GREEN+"T�l�charger/Activer le ressource pack !",Arrays.asList(ChatColor.YELLOW+"Tu as besoin d'optifine pour faire",ChatColor.YELLOW+"fonctioner la plus part des textures !")));
		inventory.setItem(9*1+3,Craft.customItem(Material.LIME_CONCRETE,ChatColor.GREEN+"J'ai d�j� le ressource pack !",Arrays.asList()));
		inventory.setItem(9*1+5,Craft.customItem(Material.WHITE_CONCRETE,"T�l�charger optifine !",Arrays.asList(ChatColor.YELLOW+"Tu peux installer optifine via ce lien ou",ChatColor.YELLOW+"sur ton moteur de recherche pr�f�r� !")));
		inventory.setItem(9*1+7,Craft.customItem(Material.RED_CONCRETE,ChatColor.RED+"Ne pas t�l�charger le ressource pack !",Arrays.asList(ChatColor.YELLOW+"Cela r�duit l'exp�rience de jeu !",ChatColor.YELLOW+"Nous vous recommandons de l'installer !")));
		
		player.openInventory(inventory);
	}

}
