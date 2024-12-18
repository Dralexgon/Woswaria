package fr.firedragonalex.areaplugin.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.firedragonalex.areaplugin.AreaEvent;
import fr.firedragonalex.areaplugin.MainAreaPlugin;
import fr.firedragonalex.areaplugin.Selection;
import fr.firedragonalex.areaplugin.area.Area;

public class ListenerSelection implements Listener {
	private MainAreaPlugin mainAreaPlugin;

	public ListenerSelection(MainAreaPlugin mainAreaPlugin) {
		this.mainAreaPlugin = mainAreaPlugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action==Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			ItemStack it = event.getItem();
			if(it == null) return;
			if(it.getType()==Material.TNT_MINECART && !mainAreaPlugin.getPlayerCanPlaceMinecartWithTnt() && action==Action.RIGHT_CLICK_BLOCK) {
				player.sendMessage("�cSorry, TNT_MINECART are disabled !");
				event.setCancelled(true);
				return;
			}
			if(it.getType()==Material.END_CRYSTAL && !mainAreaPlugin.getPlayerCanPlaceEndCrystal() && action==Action.RIGHT_CLICK_BLOCK) {
				player.sendMessage("�cSorry, END_CRYSTAL are disabled !");
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteractSelection(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action==Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();
			ItemStack it = event.getItem();
			if(it == null) return;
			if(!it.hasItemMeta()) return;
			if(it.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"AreaSelector") || it.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"AreaSelector(with height)")) {
				for (Selection selection : mainAreaPlugin.getAllSelections()) {
					if (selection.getOwner() == player) {
						if (player.getWorld() == selection.getLocation().getWorld()) {
							System.out.println("[AreaPlugin-FDA] New selection : "+block.getLocation().getBlockX()+" "+block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ()+" !");
							player.sendMessage(ChatColor.BLUE+"Nouvelle s�l�ction !");
							event.setCancelled(true);
							int nbArea = 0;
							for (Area area : mainAreaPlugin.getAllAreas()) {
								if (area.getOwner().equals(player.getUniqueId())) {
									nbArea++;
								}
							}
							Area areaCreated;
							if (it.getItemMeta().getDisplayName().equals(ChatColor.BLUE+"AreaSelector")) {
								areaCreated = new Area(new Location(selection.getLocation().getWorld(), selection.getLocation().getBlockX(), 1, selection.getLocation().getBlockZ()), new Location(block.getLocation().getWorld(), block.getLocation().getBlockX(), 255, block.getLocation().getBlockZ()), player.getUniqueId(), new ArrayList<UUID>(), mainAreaPlugin.getDefaultNameArea()+nbArea, null, mainAreaPlugin);
							}else {
								areaCreated = new Area(selection.getLocation(), block.getLocation(), player.getUniqueId(), new ArrayList<UUID>(), mainAreaPlugin.getDefaultNameArea()+nbArea, null, mainAreaPlugin);
							}
							mainAreaPlugin.getAllSelections().remove(selection);
							if (mainAreaPlugin.getConfig().getBoolean("default_settings.must_be_distinct")) {
								for (Area area : mainAreaPlugin.getAllAreas()) {
									if (!areaCreated.isDistinct(area)) {
										player.sendMessage("�cTu ne peux pas cr�er une zone dans une zone d�j� existante !");
										return;
									}
								}
							}
							if (!AreaEvent.callCreateAreaEvent(areaCreated)) {
								mainAreaPlugin.addArea(areaCreated);
								player.sendMessage(ChatColor.BLUE+"La zone a bien �t� cr��e !");
							} else {
								AreaEvent.sendMessageCancel(player);
							}
							return;
						}else {
							mainAreaPlugin.getAllSelections().remove(selection);
							player.sendMessage("�c[Erreur] Les deux s�l�ctions doivent �tre dans le m�me monde !");
							player.sendMessage("�cLes deux s�l�ctions ont �t� supprim�es !");
							return;
						}

					}
				}
				player.sendMessage(ChatColor.BLUE+"Nouvelle s�l�ction !");
				System.out.println("[AreaPlugin-FDA] New selection : "+block.getLocation().getBlockX()+" "+block.getLocation().getBlockY()+" "+block.getLocation().getBlockZ()+" !");
				mainAreaPlugin.getAllSelections().add(new Selection(block.getLocation(), player));
				event.setCancelled(true);
			}
		}
	}
}
