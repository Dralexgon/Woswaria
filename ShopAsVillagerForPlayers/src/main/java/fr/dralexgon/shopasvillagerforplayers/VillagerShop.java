package fr.dralexgon.shopasvillagerforplayers;

import java.io.File;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerShop {

	private UUID owner;
	private Villager villager;
	private String name;
	private Inventory inventoryThingsToSell;
	private Inventory inventoryThingsObtained;
	private long lastTimeUse;
	private List<Integer> listLastMaxUses;
	private boolean hasInfiniteTrade;
	private boolean dead;
	
	public VillagerShop(UUID owner, Villager villager, boolean hasInfiniteTrade) {
		name = ChatColor.YELLOW + Main.getText("villagershop.defaultname") + Bukkit.getPlayer(owner).getName();
		setDefaultValues(owner, name, villager, hasInfiniteTrade);
	}

	public VillagerShop(UUID owner, String name, Villager villager, boolean hasInfiniteTrade) {
		/*Only used for the save*/
		setDefaultValues(owner, name, villager, hasInfiniteTrade);
	}

	public void setDefaultValues(UUID owner, String name, Villager villager, boolean hasInfiniteTrade) {
		this.owner = owner;
		this.villager = villager;
		this.villager.setAI(false);
		this.villager.setGravity(true);
		this.villager.setSilent(true);
		this.name = name;
		this.villager.setCustomName(name);
		this.villager.setCustomNameVisible(true);
		this.lastTimeUse = System.currentTimeMillis();
		this.listLastMaxUses = new ArrayList<>();
		this.inventoryThingsToSell = Bukkit.createInventory(null, 3*9, "§1InventoryThingsToSell");
		this.inventoryThingsObtained = Bukkit.createInventory(null, 3*9, "§1InventoryThingsObtained");
		this.hasInfiniteTrade = hasInfiniteTrade;
		this.dead = false;
	}

	public VillagerShop(String uuid, String owner, String name, String world, Integer x, Integer z, String inventory_obtained, String inventory_to_sell, Boolean infinite_trade) {
		/*Only used for the save*/
		this.owner = UUID.fromString(owner);
		this.name = name;
		this.hasInfiniteTrade = infinite_trade;
		Chunk chunk = Bukkit.getWorld(UUID.fromString(world)).getChunkAt(x, z);

		villager = (Villager)Bukkit.getEntity(UUID.fromString(uuid));
		if (villager == null) {
			for(Entity entity : chunk.getEntities()) {
				if (entity.getUniqueId().equals(uuid)) {
					villager = (Villager)entity;
				}
			}
		}
		if (villager == null) {
			System.out.println("[ERROR] Villager not found !");
			return;
		}

		/*
		this.villager.setAI(false);
		this.villager.setGravity(true);
		this.villager.setSilent(true);
		this.villager.setCustomName(name);
		this.villager.setCustomNameVisible(true);
		this.lastTimeUse = System.currentTimeMillis();
		this.listLastMaxUses = new ArrayList<>();
		this.dead = false;
		 */

		Main.getInstance().getListVillagersShop().add(this);

		Inventory inventoryThingsObtained = Bukkit.createInventory(null, 3*9);
		Inventory inventoryThingsToSell = Bukkit.createInventory(null, 3*9);

		File saveInventory = new File("plugins/ShopPlayerPNJ-FDA/saves/" +"saveItemstacks"+ ".yml");
		FileConfiguration saveInventoryConfig = YamlConfiguration.loadConfiguration(saveInventory);
		boolean hasFinish = false;
		int i = 0;
		while (!hasFinish) {
			try {
				inventoryThingsObtained.addItem(saveInventoryConfig.getItemStack(UUID.fromString(inventory_obtained)+"."+i));
				i++;
			} catch (Exception e) {
				hasFinish = true;
			}
		}
		hasFinish = false;
		i = 0;
		while (!hasFinish) {
			try {
				inventoryThingsToSell.addItem(saveInventoryConfig.getItemStack(UUID.fromString(inventory_to_sell)+"."+i));
				i++;
			} catch (Exception e) {
				hasFinish = true;
			}
		}

		this.setInventoryThingsObtained(inventoryThingsObtained);
		this.setInventoryThingsToSell(inventoryThingsToSell);
	}
	
	public boolean isDead() {
		return this.dead;
	}
	
	public void death() {
		this.dead = true;
		this.getVillager().setHealth(0);
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public Villager getVillager() {
		return this.villager;
	}
	
	public Inventory getInventoryThingsToSell() {
		return this.inventoryThingsToSell;
	}
	
	public Inventory getInventoryThingsObtained() {
		return this.inventoryThingsObtained;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Integer> getListLastMaxUses() {
		return this.listLastMaxUses;
	}
	
	public boolean hasInfiniteTrade() {
		return this.hasInfiniteTrade;
	}
	
	public void setListLastMaxUses(List<Integer> listLastMaxUses) {
		this.listLastMaxUses = listLastMaxUses;
	}
	
	public void setInventoryThingsToSell(Inventory inventory) {
		this.inventoryThingsToSell = inventory;
	}
	
	public void setInventoryThingsObtained(Inventory inventory) {
		this.inventoryThingsObtained = inventory;
	}
	
	public void setVillager(Villager villager) {
		this.villager = villager;
	}
	
	public void setName(String name) {
		this.name = name;
		this.getVillager().setCustomName(name);
		this.getVillager().setCustomNameVisible(true);
	}
	
	public void updateMaxUses() {
		this.updateInventories();
		this.setListLastMaxUses(new ArrayList<>());
		for (MerchantRecipe trade : this.getVillager().getRecipes()) {
			if (hasInfiniteTrade) {
				trade.setMaxUses(64*4*9);
			} else {
				int nbItem = 0;
				for (ItemStack  itemStack : this.getInventoryThingsToSell().getContents()) {
					if (itemStack!=null) {
						if (itemStack.getType()==trade.getResult().getType()) {
							nbItem+=itemStack.getAmount();
						}
					}
				}
				this.getListLastMaxUses().add(Math.floorDiv(nbItem, trade.getResult().getAmount()));
				trade.setMaxUses(Math.floorDiv(nbItem, trade.getResult().getAmount()));
				trade.setUses(0);
			}
		}
	}
	
	public void updateInventories() {
		for (MerchantRecipe trade : this.getVillager().getRecipes()) {
			try {
				int nbTrade = trade.getUses();
				for (int i = 0; i < nbTrade; i++) {
					removeInventory(this.getInventoryThingsToSell(), trade.getResult().getType(), trade.getResult().getAmount());
					this.getInventoryThingsObtained().addItem(trade.getIngredients().get(0));
					try {
						this.getInventoryThingsObtained().addItem(trade.getIngredients().get(1));
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private static boolean removeInventory(Inventory inventory, Material type, int nb) {
		int nbMissing = nb;
		for (ItemStack itemStack : inventory.getContents()) {
			if (itemStack!=null) {
				if (itemStack.getType()==type) {
					if (itemStack.getAmount()>=nbMissing) {
						itemStack.setAmount(itemStack.getAmount()-nb);
						return true;
					} else {
						nbMissing-=itemStack.getAmount();
						itemStack.setAmount(0);
					}
				}
			}
		}
		return false;
	}

	public static void addTrade(Villager villager, ItemStack inputTrade, ItemStack inputTrade2, ItemStack outputTrade) {
		List<MerchantRecipe> tempListOfRecipes = new ArrayList<>(villager.getRecipes());
		MerchantRecipe newTrade = new MerchantRecipe(outputTrade.clone(),0);
		newTrade.addIngredient(inputTrade.clone());
		if (inputTrade2 != null) {
			newTrade.addIngredient(inputTrade2.clone());
		}
		newTrade.setExperienceReward(false);
		tempListOfRecipes.add(newTrade);
		villager.setRecipes(tempListOfRecipes);
	}
}
