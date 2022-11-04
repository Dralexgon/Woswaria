package fr.firedragonalex.spellandweapon.islandgenerator;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IslandGenereator extends BukkitRunnable {
	
	private static IslandGenereator lastIsland = null;
	
	private int timeoutCounter;
	private Location location;
	private int size;
	private int height;
	private Player player;
	private List<Block> allBlocks;
	private static boolean BEAUTIFUL_SPAWN;
	
	public IslandGenereator(Player player, Location location, int size, int height) {
		this.timeoutCounter = 0;
		this.location = location;
		this.size = size;
		this.height = height;
		this.player = player;
		this.allBlocks = new ArrayList<>();
		IslandGenereator.BEAUTIFUL_SPAWN = false;
		IslandGenereator.lastIsland = this;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public List<Block> getAllBlocks() {
		return this.allBlocks;
	}
	
	public static IslandGenereator getLastIsland() {
		return IslandGenereator.lastIsland;
	}
	
	@Override
	public void run() {
		if (timeoutCounter >= 30) {
			player.sendMessage(ChatColor.LIGHT_PURPLE+"[SpellAndWeapon-FDA] Timeout");
			this.cancel();
			return;
		}
		
		File file = new File("plugins/SpellAndWeapon-FDA/PerlinNoise.png");
		player.sendMessage(ChatColor.LIGHT_PURPLE+"[SpellAndWeapon-FDA] Creating island...");
		if (file.exists()) {
			BufferedImage image;
			player.sendMessage(ChatColor.LIGHT_PURPLE+"[SpellAndWeapon-FDA] Charging island...");
			try {
				image = ImageIO.read(file);
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			if (BEAUTIFUL_SPAWN) {
				new IslandCreator(image,this);
			} else {
				this.createIsland(image);
			}
			this.cancel();
			return;
		}
		timeoutCounter++;
	}
	
	private void createIsland(BufferedImage image) {
		int[][] pixels = IslandGenereator.convertTo2DWithoutUsingGetRGB(image);
		int minValue = pixels[0][0];
		int maxValue = 0;
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				int onePixel = pixels[i][j];
				if (onePixel < minValue) {
					minValue = onePixel;
				}
				if (onePixel > maxValue) {
					maxValue = onePixel;
				}
			}
		}
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				int onePixel = pixels[i][j];
				double newValue = onePixel + (0 - minValue);
				newValue = newValue*(1/(double)(maxValue-minValue));
				int finalValue = (int)Math.round(newValue*height);
				Block block;
				
				for (int k = 0; k < (finalValue-3); k++) {
					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
					this.tryChangeTypeBlock(block, Material.STONE);
				}
				for (int k = (finalValue-3); k < finalValue; k++) {
					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
					this.tryChangeTypeBlock(block, Material.DIRT);
				}
				block = location.getWorld().getBlockAt(location.getBlockX()+i, finalValue+location.getBlockY(), location.getBlockZ()+j);
				this.tryChangeTypeBlock(block, Material.GRASS_BLOCK);
				
//				for (int k = 0; k <= finalValue; k++) {
//					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
//					if (Math.random() > 0.5) {
//						this.tryChangeTypeBlock(block, Material.COBBLED_DEEPSLATE);
//					} else {
//						this.tryChangeTypeBlock(block, Material.DEEPSLATE);
//					}
//					
//				}
//				for (int k = 0; k < (finalValue-12); k++) {
//					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
//					this.tryChangeTypeBlock(block, Material.BLUE_ICE);
//				}
//				for (int k = (finalValue-12); k < (finalValue-2); k++) {
//					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
//					this.tryChangeTypeBlock(block, Material.PACKED_ICE);
//				}
//				for (int k = (finalValue-2); k < finalValue; k++) {
//					block = location.getWorld().getBlockAt(location.getBlockX()+i, k+location.getBlockY(), location.getBlockZ()+j);
//					this.tryChangeTypeBlock(block, Material.SNOW_BLOCK);
//				}
			}
		}
		player.sendMessage(ChatColor.LIGHT_PURPLE+"[SpellAndWeapon-FDA] Island created !");
	}
	
	public void tryChangeTypeBlock(Block block, Material type) {
		if ((block.getType() == Material.AIR || block.getType() == Material.WATER) && block.getType() != type) {
			block.setType(type);
			this.allBlocks.add(block);
		}
	}
	
	public static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

	      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      final int width = image.getWidth();
	      final int height = image.getHeight();
	      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

	      int[][] result = new int[height][width];
	      if (hasAlphaChannel) {
	         final int pixelLength = 4;
	         for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff); // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff); // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return result;
	}
}
