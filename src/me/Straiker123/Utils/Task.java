package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Straiker123.LoaderClass;
import me.Straiker123.ParticleEffect;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.TNTExplosionEvent;

public class Task {
	List<Block> a;
	TNTExplosionEvent event;
	Location reals;
	boolean toReal;
	public Task(Location real,List<Block> blocks, TNTExplosionEvent result) {
		a=blocks;
		event=result;
		reals=real;
		f=LoaderClass.config.getConfig();
		toReal=f.getBoolean("Options.LagChecker.TNT.Drops.InFirstTNTLocation");
	}
	int task;
	FileConfiguration f;
	
	private String action() {
		if(TheAPI.getMemoryAPI().getFreeMemory(true) < 30) {
			TheAPI.getMemoryAPI().clearMemory();
			return f.getString("Options.LagChecker.TNT.Action.LowMememory");
		}
		if(TheAPI.getServerTPS() < 15) {
			return f.getString("Options.LagChecker.TNT.Action.LowTPS");
		}
		return "wait";
	}
	
	public void start() {
		if(!f.getBoolean("Options.LagChecker.TNT.Particles.Disable")) {
			if(TheAPI.isOlder1_9()) {
				ParticleEffect e = ParticleEffect.EXPLOSION_LARGE;
				if(ParticleEffect.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"))!=null)
					e=ParticleEffect.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"));
				TheAPI.getParticleEffectAPI().spawnParticle(e, event.getLocation(), 1);
			}else {
				Particle e = Particle.EXPLOSION_LARGE;
				if(Particle.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"))!=null)
					e=Particle.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"));
				event.getLocation().getWorld().spawnParticle(e, event.getLocation(),1);
			}
		}
    	if(event.canHitEntities())
		for(Entity e : TheAPI.getBlocksAPI().getNearbyEntities(event.getLocation(),(int) (event.getPower()*1.25))) {
			if(e instanceof LivingEntity) {
				((LivingEntity)e).damage(7);
			}else
				e.remove();
		}
		task=Bukkit.getScheduler().scheduleSyncRepeatingTask(LoaderClass.plugin, new Runnable() {
			
			@Override
			public void run() {
				//if memory usage is 75%+ or Server TPS are lower than 15, TNT will wait (only this tnt)
				if(action().equalsIgnoreCase("wait")) {
				for(int i = (event.isNuclearBomb() ? 1000 : 100); i > 0; --i) {
					if(a.isEmpty()) {
						Bukkit.getScheduler().cancelTask(task);
						break;
					}
				Block b = a.get(a.size()-1);
				Location c = b.getLocation();
				if(event.canTNTInLiquidCancelEvent() && !Events.around(c)||!event.canTNTInLiquidCancelEvent())
		    	if(event.canDestroyBlocks())
				if(b.getType()==Material.TNT) {
							if(!f.getBoolean("Options.LagChecker.TNT.CollidingTNT.Disabled")) {
							b.setType(Material.AIR);
							if(!f.getBoolean("Options.LagChecker.TNT.SpawnTNT")) {
								Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
									@Override
									public void run() {
										Events.get(reals,b.getLocation(),toReal);
									}
								}, (f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime") <= 0 ? 1: f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime")));
								}else {
									TNTPrimed tnt = (TNTPrimed)b.getWorld().spawnEntity(b.getLocation(), EntityType.PRIMED_TNT);
									tnt.setFuseTicks((f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime") <= 0 ? 1: f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime")));
								}
						}else {
							Events.add(b.getLocation(),(toReal ? reals : b.getLocation()),false,new ArrayList<Inventory>(),(List<ItemStack>) b.getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
							b.setType(Material.AIR);
							b.getDrops().clear();
						}}else {

							Events.add(b.getLocation(),(toReal ? reals : b.getLocation()),false,new ArrayList<Inventory>(),(List<ItemStack>) b.getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
							b.setType(Material.AIR);
							b.getDrops().clear();
						}
				a.remove(a.size()-1);
					}
				}else {
					a.clear();
					Bukkit.getScheduler().cancelTask(task);
				}
			
			}
		}, 10,10);
	}
}
