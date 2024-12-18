package fr.dralexgon.shopasvillagerforplayers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.dralexgon.shopasvillagerforplayers.database.SaveAndLoadSQLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.util.Vector;


public class Listeners implements Listener {
	
	private final Main main;

	public Listeners(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (action != Action.RIGHT_CLICK_BLOCK) return;
		ItemStack it = event.getItem();
		if (it == null || it.getType() != Material.VILLAGER_SPAWN_EGG || !it.hasItemMeta()) return;

		if (it.getItemMeta().getDisplayName().equals(ChatColor.YELLOW+"VillagerShop")) {
			event.setCancelled(true);
			Location location = new Location(block.getWorld(), block.getLocation().getBlockX()+0.5, block.getLocation().getBlockY()+1, block.getLocation().getBlockZ()+0.5);
			Vector playerDirection = player.getLocation().getDirection();
			location.setDirection(new Vector(-playerDirection.getX(), 0, -playerDirection.getZ()));
			Villager villager = (Villager)block.getWorld().spawnEntity(location, EntityType.VILLAGER);
			VillagerShop villagerShop = new VillagerShop(player.getUniqueId(), villager, false);
			main.getListVillagersShop().add(villagerShop);
			SaveAndLoadSQLite.addVillagerShop(villagerShop);
			if (player.getGameMode()!=GameMode.CREATIVE) {
				it.setAmount(it.getAmount()-1);
			}
		} else if (it.getItemMeta().getDisplayName().equals(ChatColor.YELLOW+"VillagerShopInfiniteTrade")) {
			event.setCancelled(true);
			Location location = new Location(block.getWorld(), block.getLocation().getBlockX()+0.5, block.getLocation().getBlockY()+1, block.getLocation().getBlockZ()+0.5);
			Vector playerDirection = player.getLocation().getDirection();
			location.setDirection(new Vector(-playerDirection.getX(), 0, -playerDirection.getZ()));
			Villager villager = (Villager)block.getWorld().spawnEntity(location, EntityType.VILLAGER);
			VillagerShop villagerShop = new VillagerShop(player.getUniqueId(), villager, true);
			main.getListVillagersShop().add(villagerShop);
			SaveAndLoadSQLite.addVillagerShop(villagerShop);
			if (player.getGameMode()!=GameMode.CREATIVE) {
				it.setAmount(it.getAmount()-1);
			}
		}
		/*
		if (it.getItemMeta().getLore().get(0).equals("?0VillagerShopCustom")) {
			event.setCancelled(true);
			long villagerShopUUID = Long.valueOf(it.getItemMeta().getLore().get(1).split(":")[3]);
			long villagerShopTimeOfCreation = Long.valueOf(it.getItemMeta().getLore().get(1).split(":")[4]);
			if (System.currentTimeMillis()>=villagerShopTimeOfCreation+main.getExpirationTime()) {
				player.sendMessage("Ce marchant est rest? trop longtemps sous forme d'oeuf !");
				player.getInventory().remove(it);
				player.updateInventory();
			}else {
				for (VillagerShop villagerShopInactive : main.getListVillagersShopInactive()) {
					if (villagerShopInactive.getID()==villagerShopUUID) {
						main.getListVillagersShop().add(villagerShopInactive);
						main.getListVillagersShopInactive().remove(villagerShopInactive);
						return;
					}
				}
			}
		}
		*/
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onLeftClickVillagerShop(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player player && event.getEntity() instanceof Villager villager) {
            List<List<Object>> listTempVarToRemove = new ArrayList<>();
			for (List<Object> tempVariable : main.getTempVariables()) {
				if (tempVariable.get(0)==player && (tempVariable.get(1).equals("VillagerShopSelected") || tempVariable.get(1).equals("RenameVillagerShop") || tempVariable.get(1).equals("TradeSelected"))) {
					listTempVarToRemove.add(tempVariable);
				}
			}
			for (List<Object> tempVarToRemove : listTempVarToRemove) {
				main.getTempVariables().remove(tempVarToRemove);
			}
            for (VillagerShop villagerShop : main.getListVillagersShop()) {
				if (villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
					event.setCancelled(true);
					if (villagerShop.getOwner().equals(player.getUniqueId())) {
						villagerShop.setVillager(villager);
						villagerShop.updateMaxUses();
						List<Object> tempList = new ArrayList<>();
						tempList.add(player);
						tempList.add("VillagerShopSelected");
						tempList.add(villagerShop);
						main.getTempVariables().add(tempList);
						main.getGui().startMainMenu(player);
						return;
					}else {
						for (MerchantRecipe trade : villagerShop.getVillager().getRecipes()) {
							if (villagerShop.hasInfiniteTrade()) {
								trade.setMaxUses(999);
							} else {
								int nbTradesPossible = 0;
								Inventory virtualInventoryThingsToSell = Bukkit.createInventory(null, villagerShop.getInventoryThingsToSell().getSize());
								for (ItemStack item : villagerShop.getInventoryThingsToSell().getContents()) {
									if (item != null && item.getType() != Material.AIR) {
										virtualInventoryThingsToSell.addItem(item);
									}
								}
								while (villagerShop.getInventoryThingsToSell().contains(trade.getResult())) {
									nbTradesPossible++;
									virtualInventoryThingsToSell.remove(trade.getResult());
								}
								trade.setMaxUses(nbTradesPossible);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onVillagerShopKiller(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked() instanceof Villager) {
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(ChatColor.RED + "VillagerShopKiller")) {
				Player player = event.getPlayer();
				Villager villager = (Villager)event.getRightClicked();
				for (VillagerShop villagerShop : main.getListVillagersShop()) {
					if (villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
						villagerShop.death();
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onRightClickVillagerShop(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof Villager villager)) return;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		Player player = event.getPlayer();

		if (item.getType() == Material.LEATHER_CHESTPLATE && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			for (VillagerShop villagerShop : main.getListVillagersShop()) {
				if (villagerShop.getOwner().equals(player.getUniqueId()) && villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
					event.setCancelled(true);
					player.closeInventory();
					Profession profession = Profession.valueOf(item.getItemMeta().getLore().get(0));
					if (profession != villager.getProfession()) {
						List<MerchantRecipe> recipes = villager.getRecipes();
						villager.setProfession(profession);
						villager.setRecipes(recipes);
						if (player.getGameMode() != GameMode.CREATIVE) {
							ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
							itemInMainHand.setAmount(itemInMainHand.getAmount()-1);
						}
					} else {
						player.sendMessage(ChatColor.RED + Main.getText("error.alreadyhavesameskin"));
					}
					return;
				}
			}
		}
		for (VillagerShop villagerShop : main.getListVillagersShop()) {
			if (villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
				villagerShop.updateMaxUses();
				if (villagerShop.getOwner().equals(player.getUniqueId())) {
					if (villager.getRecipes().isEmpty()) {
						player.sendMessage(ChatColor.YELLOW + Main.getText("error.notrade"));
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onDamageVillagerShop(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Villager) {
			Villager villager = (Villager)event.getEntity();
			for(VillagerShop villagerShop : main.getListVillagersShop()) {
				if(villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	private void onKillEntity(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Villager) {
			Villager villager = (Villager)event.getEntity();
			for(VillagerShop villagerShop : main.getListVillagersShop()) {
				if(villagerShop.getVillager().getUniqueId().equals(villager.getUniqueId())) {
					if (villagerShop.isDead()) {
						main.getListVillagersShop().remove(villagerShop);
					} else {
						villagerShop.getVillager().setHealth(villagerShop.getVillager().getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
//						Villager newVillager = (Villager)villagerShop.getVillager().getWorld().spawnEntity(villagerShop.getVillager().getLocation(), EntityType.VILLAGER);
//						newVillager.setAI(false);
//						newVillager.setGravity(true);
//						newVillager.setSilent(true);
//						newVillager.setCustomNameVisible(true);
//						newVillager.setCustomName(villagerShop.getName());
//						newVillager.getEyeLocation().setDirection(villagerShop.getVillager().getEyeLocation().getDirection());
					}
					return;
				}
			}
			if (Math.random()*100 <= main.getConfig().getDouble("drop_egg")) {
				ItemStack item = new ItemStack(Material.VILLAGER_SPAWN_EGG,1);
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(ChatColor.YELLOW + "VillagerShop");
				item.setItemMeta(itemMeta);
				villager.getWorld().dropItem(villager.getLocation(), item);
			}
			if (Math.random()*100 <= main.getConfig().getDouble("drop_skin")) {
				ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE,1);
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(ChatColor.BLUE + "SkinVillagerShop");
				itemMeta.setLore(Arrays.asList(villager.getProfession().name()));
				item.setItemMeta(itemMeta);
				villager.getWorld().dropItem(villager.getLocation(), item);
			}
		}
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player)event.getPlayer();
		if (event.getView().getTitle().equals(ChatColor.BLUE + Main.getText("gui.newtrade.title"))) {
			ItemStack itemstack1 = event.getView().getTopInventory().getContents()[9*2+1];
			ItemStack itemstack2 = event.getView().getTopInventory().getContents()[9*2+3];
			ItemStack itemstack3 = event.getView().getTopInventory().getContents()[9*2+7];
			if (itemstack1 != null) {
				player.getInventory().addItem(itemstack1.clone());
			}
			if (itemstack2 != null) {
				player.getInventory().addItem(itemstack2.clone());
			}
			if (itemstack3 != null) {
				player.getInventory().addItem(itemstack3.clone());
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		PermissionAttachment attachment;
		if (main.getConfig().getBoolean("everyone_permission.giveskinvillagershop")) {
			attachment = player.addAttachment(main);
			attachment.setPermission("giveskinvillagershop.use", true);
		}
		
		if (main.getConfig().getBoolean("everyone_permission.givevillagershop")) {
			attachment = player.addAttachment(main);
			attachment.setPermission("givevillagershop.use", true);
		}
		
		if (main.getConfig().getBoolean("everyone_permission.givevillagershopinfinitetrade")) {
			attachment = player.addAttachment(main);
			attachment.setPermission("givevillagershopinfinitetrade.use", true);
		}
	}

	//Edge case: VillagerShop is transformed into a witch by lightning
	@EventHandler(priority = EventPriority.NORMAL)
	public void onVillagerTransform(EntityTransformEvent event) {
		if (event.getEntity() instanceof Villager) {
			for (VillagerShop villagerShop : main.getListVillagersShop()) {
				if (villagerShop.getVillager().getUniqueId().equals(event.getEntity().getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	//Edge case: VillagerShop right-clicked with a spawn egg. Cancel baby villager creation
	/*
	@EventHandler(priority = EventPriority.LOW)
	public void onRightClickVillagerShopWithVillagerEgg(PlayerInteractAtEntityEvent event) {
		if (!(event.getRightClicked() instanceof Villager))
			return;

		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item.getType() == Material.VILLAGER_SPAWN_EGG) {
			event.setCancelled(true);
			Bukkit.broadcastMessage("Event cancelled");
		}
	}
	*/

	//Edge case: VillagerShop right-clicked with a spawn egg. Cancel baby villager creation
	@EventHandler(priority = EventPriority.LOW)
	public void onSpawnVillagerShopWithVillagerEgg(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Villager villager))
			return;

		if (!villager.isAdult() && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
			//Get the closest player to the villager
			Player closestPlayer = null;
			double closestDistance = Double.MAX_VALUE;
			for (Player player : Bukkit.getOnlinePlayers()) {
				double distance = player.getLocation().distance(villager.getLocation());
				if (distance < closestDistance) {
					closestDistance = distance;
					closestPlayer = player;
				}
			}
			if (closestPlayer != null) {
				closestPlayer.sendMessage(Main.getText("error.spawnvillagershoponvillagershop"));
			}
		}
	}
}
