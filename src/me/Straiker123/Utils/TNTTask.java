package me.Straiker123.Utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.Straiker123.LoaderClass;
import me.Straiker123.Particle;
import me.Straiker123.Position;
import me.Straiker123.Storage;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.TNTExplosionEvent;
import me.Straiker123.Scheduler.Tasker;

public class TNTTask {
	List<Position> a;
	TNTExplosionEvent event;
	Position reals;
	boolean toReal;
	public TNTTask(Position reals2,List<Position> list, TNTExplosionEvent result) {
		a=list;
		event=result;
		reals=reals2;
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
		return "none";
	}
	public void start() {
		Storage st = new Storage();
		if(event.canTNTInLiquidCancelEvent() && Events.around(event.getLocation())) {
			return;
		}
		if(!f.getBoolean("Options.LagChecker.TNT.Particles.Disable")) {
			if(TheAPI.isOlder1_9()) {
				Particle e = Particle.EXPLOSION_LARGE;
				if(Particle.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"))!=null)
					e=Particle.valueOf(f.getString("Options.LagChecker.TNT.Particles.Type"));
				for(Entity ds: TheAPI.getBlocksAPI().getNearbyEntities(event.getLocation(), 15))
					if(ds.getType()==EntityType.PLAYER)
				TheAPI.getNMSAPI().sendPacket((Player)ds, TheAPI.getNMSAPI().getPacketPlayOutWorldParticles(e, event.getLocation().toLocation()));
		}}
		if(event.canHitEntities()) {
			Position l = event.getLocation();
			int r = (int) (event.getPower()*1.25);
	        int chunkRadius = r < 16 ? 1 : (r - (r % 16))/16;
			for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                    	if(e.getType()==EntityType.CREEPER) {
                    		e.setFireTicks(11);
                    	}
                    	if(e.getType()==EntityType.MINECART_TNT) {
                    		e.setFireTicks(11);
                    	}
                        if (l.distance(e.getLocation()) <= r && e.getLocation().getBlock() != l.getBlock()) {

            				double rd = (r-l.distance(e.getLocation()));
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
    				a.damage(damage/7);
    				}catch(Exception | NoClassDefFoundError | NoSuchMethodError err) {
        				a.damage(damage/7);
    				}
    			}else
    				e.remove();
    		}}
		}}}}
		new Tasker() {
			int ignite = f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime") <= 0 ? 5 : f.getInt("Options.LagChecker.TNT.CollidingTNT.IgniteTime");
			boolean igniteUsed = !f.getBoolean("Options.LagChecker.TNT.CollidingTNT.Disabled");
			boolean fakeTnt = !f.getBoolean("Options.LagChecker.TNT.SpawnTNT");
			String location = reals.toString();
			public void run() {
					if(action().equalsIgnoreCase("none")) {
					for(int i = (event.isNuclearBomb() ? 2000 : 200); i > 0; --i) {
						if(a.isEmpty()) {
							for(ItemStack d:st.getItems()) {
									try {
										event.getLocation().getWorld().dropItem(event.getLocation().toLocation(), d);
									}catch(Exception err) {}
								}
							cancel();
							break;
						}
					Position b = a.get(a.size()-1);
					if(event.canTNTInLiquidCancelEvent() && Events.around(b))
						return;
			    	if(event.canDestroyBlocks())
					if(b.getBukkitType()==Material.TNT) {
								if(igniteUsed) {
								b.setType(Material.AIR);
								if(fakeTnt) {
									Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
										@Override
										public void run() {
											Events.get(reals,b);
										}
									}, ignite);
									}else {
										TNTPrimed tnt = (TNTPrimed)b.getWorld().spawnEntity(b.toLocation(), EntityType.PRIMED_TNT);
										tnt.setMetadata("real", new FixedMetadataValue(LoaderClass.plugin, location));
										tnt.setFuseTicks(ignite);
									}
							}else {
								if(event.isDropItems())
								Events.add(b,(toReal ? reals : b),toReal,st, b.getBlock().getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
								b.setType(Material.AIR);
								b.getBlock().getDrops().clear();
									if(TheAPI.generateRandomInt(25)==25) {
									for(Entity e: TheAPI.getBlocksAPI().getNearbyEntities(b, 2)) {
										if(e instanceof LivingEntity) {
											e.setFireTicks(80);
										}
									}
									for(Entity ds: TheAPI.getBlocksAPI().getNearbyEntities(event.getLocation(), 15))
										if(ds.getType()==EntityType.PLAYER)
									TheAPI.getNMSAPI().sendPacket((Player)ds, TheAPI.getNMSAPI().getPacketPlayOutWorldParticles(Particle.FLAME, event.getLocation().toLocation()));
							
									}
							}}else {
								if(event.isDropItems())
									Events.add(b,(toReal ? reals : b),toReal,st,b.getBlock().getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
								b.setType(Material.AIR);
								b.getBlock().getDrops().clear();
								if(TheAPI.generateRandomInt(25)==25) {
									for(Entity e: TheAPI.getBlocksAPI().getNearbyEntities(b, 2)) {
										if(e instanceof LivingEntity) {
											e.setFireTicks(80);
										}
									}
									for(Entity ds: TheAPI.getBlocksAPI().getNearbyEntities(event.getLocation(), 15))
										if(ds.getType()==EntityType.PLAYER)
									TheAPI.getNMSAPI().sendPacket((Player)ds, TheAPI.getNMSAPI().getPacketPlayOutWorldParticles(Particle.FLAME, event.getLocation().toLocation()));
							
								}
							}
					a.remove(a.size()-1);
						}
					}else if(action().equalsIgnoreCase("drop")) {
							for(ItemStack id :st.getItems()) {
								try {
									event.getLocation().getWorld().dropItem(event.getLocation().toLocation(), id);
								}catch(Exception err) {}
							}
						
						a.clear();
						cancel();
					}
			}
		}.repeating(0,10);
	}}
