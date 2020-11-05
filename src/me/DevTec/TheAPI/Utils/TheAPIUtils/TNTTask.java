package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.APIs.MemoryAPI;
import me.DevTec.TheAPI.BlocksAPI.BlocksAPI;
import me.DevTec.TheAPI.ConfigAPI.Config;
import me.DevTec.TheAPI.Events.TNTExplosionEvent;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Storage;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.Particle;

public class TNTTask {
	private final List<Position> a;
	private final Particle e;
	private final TNTExplosionEvent event;
	private final Position reals;
	private final Config f = LoaderClass.config;
	private final boolean toReal = f.getBoolean("Options.Optimize.TNT.Drops.InFirstTNTLocation");

	public TNTTask(Position reals2, List<Position> list, TNTExplosionEvent result) {
		a = list;
		event = result;
		reals = reals2;
		if (Particle.valueOf(f.getString("Options.Optimize.TNT.Particles.Type")) != null)
			e = Particle.valueOf(f.getString("Options.Optimize.TNT.Particles.Type"));
		else
			e = Particle.EXPLOSION_LARGE;
	}

	private String action() {
		if (MemoryAPI.getFreeMemory(true) < 30) {
			MemoryAPI.clearMemory();
			return f.getString("Options.Optimize.TNT.Action.LowMememory");
		}
		if (TheAPI.getServerTPS() < 15) {
			return f.getString("Options.Optimize.TNT.Action.LowTPS");
		}
		return "none";
	}

	public void start() {
		Storage st = new Storage();
		if (event.canTNTInLiquidCancelEvent() && Events.around(event.getLocation())) {
			return;
		}
		if (f.getBoolean("Options.Optimize.TNT.Particles.Use")) {
			for (Entity ds : BlocksAPI.getNearbyEntities(event.getLocation(), 15))
				if (ds.getType() == EntityType.PLAYER)
					NMSAPI.sendPacket((Player) ds,
							NMSAPI.getPacketPlayOutWorldParticles(e, event.getLocation().toLocation()));
		}
		if (event.canHitEntities()) {
			Position l = event.getLocation();
			int r = (int) (event.getPower() * 1.25);
			int chunkRadius = r < 16 ? 1 : (r - (r % 16)) / 16;
			for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
				for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
					int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
					for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk()
							.getEntities()) {
						if (e.getType() == EntityType.CREEPER) {
							e.setFireTicks(11);
						}
						if (e.getType() == EntityType.MINECART_TNT) {
							e.setFireTicks(11);
						}
						if (l.distance(e.getLocation()) <= r && e.getLocation().getBlock() != l.getBlock()) {

							double rd = (r - l.distance(e.getLocation()));
							double damage = r * rd / 9;
							double total = damage / 3;
							String xd = TheAPI.getRandomFromList(Arrays.asList(0.1 * total, 0.2 * total, 0.3 * total))
									.toString();
							String yd = TheAPI.getRandomFromList(Arrays.asList(0.2 * total, 0.3 * total, 0.4 * total))
									.toString();
							String zd = TheAPI.getRandomFromList(Arrays.asList(0.1 * total, 0.2 * total, 0.3 * total))
									.toString();
							if (e.getType() == EntityType.PRIMED_TNT) {
								e.setVelocity(new Vector(StringUtils.getDouble(xd), StringUtils.getDouble(yd),
										StringUtils.getDouble(zd)));
							} else {
								if (e instanceof LivingEntity) {
									e.setVelocity(new Vector(StringUtils.getDouble(xd), StringUtils.getDouble(yd),
											StringUtils.getDouble(zd)));
									LivingEntity a = (LivingEntity) e;
									try {
										if (a.getAttribute(Attribute.GENERIC_ARMOR) != null
												&& a.getAttribute(Attribute.GENERIC_ARMOR).getValue() > 0)
											damage = r / (a.getAttribute(Attribute.GENERIC_ARMOR).getValue() / 8);
										a.damage(damage / 7);
									} catch (Exception | NoClassDefFoundError | NoSuchMethodError err) {
										a.damage(damage / 7);
									}
								} else
									e.remove();
							}
						}
					}
				}
			}
		}
		new Tasker() {
			int ignite = f.getInt("Options.Optimize.TNT.CollidingTNT.IgniteTime") <= 0 ? 5
					: f.getInt("Options.Optimize.TNT.CollidingTNT.IgniteTime");
			boolean igniteUsed = f.getBoolean("Options.Optimize.TNT.CollidingTNT.Use");
			boolean fakeTnt = !f.getBoolean("Options.Optimize.TNT.SpawnTNT");
			String location = reals.toString();

			@Override
			public void run() {
				if (action().equalsIgnoreCase("none")) {
					for (int i = (event.isNuclearBomb() ? 2000 : 200); i > 0; --i) {
						if (a.isEmpty()) {
							for (ItemStack d : st.getItems()) {
								try {
									event.getLocation().getWorld().dropItem(event.getLocation().toLocation(), d);
								} catch (Exception err) {
								}
							}
							cancel();
							break;
						}
						Position b = a.get(a.size() - 1);
						if (event.canTNTInLiquidCancelEvent() && Events.around(b))
							return;
						if (event.canDestroyBlocks())
							if (b.getBukkitType() == Material.TNT) {
								if (igniteUsed) {
									Object oldBlock = b.getType().getIBlockData();
									b.setType(Material.AIR);
									Position.updateBlockAt(b, oldBlock);
									Position.updateLightAt(b);
									if (fakeTnt) {
										new Tasker() {
											@Override
											public void run() {
												Events.get(reals, b);
											}
										}.runLater(ignite);
									} else {
										TNTPrimed tnt = (TNTPrimed) b.getWorld().spawnEntity(b.toLocation(),
												EntityType.PRIMED_TNT);
										tnt.setMetadata("real", new FixedMetadataValue(LoaderClass.plugin, location));
										tnt.setFuseTicks(ignite);
									}
								} else {
									if (event.isDropItems())
										Events.add(b, (toReal ? reals : b), toReal, st,
												b.getBlock().getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
									Object oldBlock = b.getType().getIBlockData();
									b.setType(Material.AIR);
									Position.updateBlockAt(b, oldBlock);
									Position.updateLightAt(b);
									b.getBlock().getDrops().clear();
									if (TheAPI.generateRandomInt(25) == 25) {
										for (Entity e : BlocksAPI.getNearbyEntities(b, 2)) {
											if (e instanceof LivingEntity) {
												e.setFireTicks(80);
											}
										}
										for (Entity ds : BlocksAPI.getNearbyEntities(event.getLocation(), 15))
											if (ds.getType() == EntityType.PLAYER)
												NMSAPI.sendPacket((Player) ds, NMSAPI.getPacketPlayOutWorldParticles(
														Particle.FLAME, event.getLocation().toLocation()));

									}
								}
							} else {
								if (event.isDropItems())
									Events.add(b, (toReal ? reals : b), toReal, st,
											b.getBlock().getDrops(new ItemStack(Material.DIAMOND_PICKAXE)));
								Object oldBlock = b.getType().getIBlockData();
								b.setType(Material.AIR);
								Position.updateBlockAt(b, oldBlock);
								Position.updateLightAt(b);
								b.getBlock().getDrops().clear();
								if (TheAPI.generateRandomInt(25) == 25) {
									for (Entity e : BlocksAPI.getNearbyEntities(b, 2)) {
										if (e instanceof LivingEntity) {
											e.setFireTicks(80);
										}
									}
									for (Entity ds : BlocksAPI.getNearbyEntities(event.getLocation(), 15))
										if (ds.getType() == EntityType.PLAYER)
											NMSAPI.sendPacket((Player) ds, NMSAPI.getPacketPlayOutWorldParticles(
													Particle.FLAME, event.getLocation().toLocation()));

								}
							}
						a.remove(a.size() - 1);
					}
				} else if (action().equalsIgnoreCase("drop")) {
					for (ItemStack id : st.getItems()) {
						try {
							event.getLocation().getWorld().dropItem(event.getLocation().toLocation(), id);
						} catch (Exception err) {
						}
					}
					a.clear();
					cancel();
				}
			}
		}.runRepeating(0, 10);
	}
}
