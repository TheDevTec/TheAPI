package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.Straiker123.LoaderClass;
import me.Straiker123.ParticleEffect;
import me.Straiker123.TheAPI;
import me.Straiker123.TheRunnable;
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
		if(event.canTNTInLiquidCancelEvent() && Events.around(event.getLocation())) {
			return;
		}
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
		if(event.canHitEntities()) {
			Location l = event.getLocation();
			int r = (int) (event.getPower()*1.25);
	        int chunkRadius = r < 16 ? 1 : (r - (r % 16))/16;
			for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= r && e.getLocation().getBlock() != l.getBlock()) {

            				double rd = (r-e.getLocation().distance(l));
            				double damage = r*rd/9;
            				double total = damage/3;
    			if(e.getType()==EntityType.PRIMED_TNT) {
    				String xd = TheAPI.getRandomFromList(Arrays.asList(0.1*total,0.15*total,0.2*total)).toString();
    				String yd = TheAPI.getRandomFromList(Arrays.asList(0.1*total,0.15*total,0.2*total)).toString();
    				String zd = TheAPI.getRandomFromList(Arrays.asList(0.1*total,0.15*total,0.2*total)).toString();
    				e.setVelocity(new Vector(TheAPI.getStringUtils().getDouble(xd),TheAPI.getStringUtils().getDouble(yd),TheAPI.getStringUtils().getDouble(zd)));		
    		
    			}else {
    			if(e instanceof LivingEntity) {
    				String xd = TheAPI.getRandomFromList(Arrays.asList(0.1*total,0.2*total,0.3*total)).toString();
    				String yd = TheAPI.getRandomFromList(Arrays.asList(0.2*total,0.3*total,0.4*total)).toString();
    				String zd = TheAPI.getRandomFromList(Arrays.asList(0.1*total,0.2*total,0.3*total)).toString();
    				e.setVelocity(new Vector(TheAPI.getStringUtils().getDouble(xd),TheAPI.getStringUtils().getDouble(yd),TheAPI.getStringUtils().getDouble(zd)));	
    				LivingEntity a = (LivingEntity)e;
    				try {
    				if(a.getAttribute(Attribute.GENERIC_ARMOR) != null && 
    						a.getAttribute(Attribute.GENERIC_ARMOR).getValue() > 0)
    					damage=r/(a.getAttribute(Attribute.GENERIC_ARMOR).getValue()/8);
    				a.damage(damage);
    				}catch(Exception  | NoSuchMethodError err) {
        				a.damage(damage);
    				}
    			}else
    				e.remove();
    		}}
		}}}}
		TheRunnable task = TheAPI.getTheRunnable();
		task.runRepeating(new Runnable() {
			@Override
			public void run() {
				if(action().equalsIgnoreCase("wait")) {
				for(int i = (event.isNuclearBomb() ? 2000 : 200); i > 0; --i) {
					if(a.isEmpty()) {
						task.cancel();
						break;
					}
					int p=TheAPI.generateRandomInt(7);
				Block b = a.get(a.size()-1);
				Location c = b.getLocation();
				if(event.canTNTInLiquidCancelEvent() && Events.around(c)) {
					return;
				}
		    	if(event.canDestroyBlocks())
				if(b.getType()==Material.TNT) {
							if(!f.getBoolean("Options.LagChecker.TNT.CollidingTNT.Disabled")) {
							b.setType(Material.AIR);
							if(!f.getBoolean("Options.LagChecker.TNT.SpawnTNT")) {
								Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
									@Override
									public void run() {
										Events.get(reals,b.getLocation());
									}
								}, (f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime") <= 0 ? 1: f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime")));
								}else {
									TNTPrimed tnt = (TNTPrimed)b.getWorld().spawnEntity(b.getLocation(), EntityType.PRIMED_TNT);
									tnt.setMetadata("real", new FixedMetadataValue(LoaderClass.plugin, TheAPI.getBlocksAPI().getLocationAsString(reals)));
									tnt.setFuseTicks((f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime") <= 0 ? 1: f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime")));
								}
						}else {
							Events.add(b.getLocation(),(toReal ? reals : b.getLocation()),toReal,new ArrayList<Inventory>(),(List<ItemStack>) b.getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
							b.setType(Material.AIR);
							b.getDrops().clear();
							if(p==4) {
								for(Entity e: TheAPI.getBlocksAPI().getNearbyEntities(b.getLocation(), 2)) {
									if(e instanceof LivingEntity) {
										e.setFireTicks(100);
									}
								}
								if(TheAPI.isOlder1_9()) {
									TheAPI.getParticleEffectAPI().spawnParticle(ParticleEffect.FLAME, c, 1);
								}else {
									event.getLocation().getWorld().spawnParticle(Particle.FLAME, c,1);
								}
						}
						}}else {

							Events.add(b.getLocation(),(toReal ? reals : b.getLocation()),toReal,new ArrayList<Inventory>(),(List<ItemStack>) b.getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
							b.setType(Material.AIR);
							b.getDrops().clear();
							if(p==4) {
								for(Entity e: TheAPI.getBlocksAPI().getNearbyEntities(b.getLocation(), 2)) {
									if(e instanceof LivingEntity) {
										e.setFireTicks(100);
									}
								}
									if(TheAPI.isOlder1_9()) {
										TheAPI.getParticleEffectAPI().spawnParticle(ParticleEffect.FLAME, c, 1);
									}else {
										event.getLocation().getWorld().spawnParticle(Particle.FLAME, c,1);
									}
							}
						}
				a.remove(a.size()-1);
					}
				}else {
					a.clear();
					task.cancel();
				}
			}
		}, 10);
	}
}
