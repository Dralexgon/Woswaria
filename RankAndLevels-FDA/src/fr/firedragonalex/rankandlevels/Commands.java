package fr.firedragonalex.rankandlevels;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.firedragonalex.rankandlevels.gui.Gui;
import fr.firedragonalex.rankandlevels.rank.Rank;

public class Commands implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] arguments) {
		if (!(sender instanceof Player)) return false;
		Player player  = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("levelrewards")) {
			Gui.GuiLevelRewards(player,1,Rank.CITOYEN);
		}
		if (cmd.getName().equalsIgnoreCase("feed")) {
			//if player a grade
			player.setFoodLevel(20);
			player.setSaturation(player.getSaturation()+2);
		}
		if (cmd.getName().equalsIgnoreCase("craft")) {
			//if player a grade
			player.openWorkbench(null, true);
		}
		if (cmd.getName().equalsIgnoreCase("enderchest")) {
			//if player a grade
			player.openInventory(player.getEnderChest());
		}
		return true;
	}

}
