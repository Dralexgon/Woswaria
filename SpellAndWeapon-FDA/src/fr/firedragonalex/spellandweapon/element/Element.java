package fr.firedragonalex.spellandweapon.element;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import fr.firedragonalex.spellandweapon.Main;

public class Element {
	
	private ElementType elementType;
	private int power;
	private int duration;
	private WaitBeforeLightning lightningStriker;
	
	public Element(ElementType elementType, int power) {
		this.elementType = elementType;
		this.power = power;
		this.duration = 0;
		
	}
	
	public ElementType getType() {
		return this.elementType;
	}
	
	public int getPower() {
		return this.power;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public Element clone() {
		return new Element(this.elementType, this.power);
	}
	
	public WaitBeforeLightning getLightningStriker() {
		return this.lightningStriker;
	}
	
	public void setLightningStriker(LivingEntity entity, Element element) {
		for (int i = 0; i < power; i++) {
			if (Math.random() <= (1.0/power)) {
				this.lightningStriker = new WaitBeforeLightning(entity,element);
				this.lightningStriker.runTaskLater(Main.getInstance(), i);
				return;
			}
		}
		this.lightningStriker = new WaitBeforeLightning(entity,element);
		this.lightningStriker.runTaskLater(Main.getInstance(), power);
	}
	
//	public void freeze(Entity target, int seconds) {
//		if (!target.isFrozen() && target.getType()!=EntityType.ARMOR_STAND) {
//			FreezeAllTime freezeAllTime = new FreezeAllTime(Main.getInstance(),20*seconds,target,0);
//			freezeAllTime.runTaskTimer(Main.getInstance(), 0, 1);
//		}
//	}
//	
//	public void freeze(Entity target, int seconds, double damagePerSnowTick) {
//		if (!target.isFrozen() && target.getType()!=EntityType.ARMOR_STAND) {
//			FreezeAllTime freezeAllTime = new FreezeAllTime(Main.getInstance(),20*seconds,target,damagePerSnowTick);
//			freezeAllTime.runTaskTimer(Main.getInstance(), 0, 1);
//		}
//	}
	
}
