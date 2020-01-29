package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.Straiker123.Events.EntityBreedEvent;

public class BreedEvent implements Listener {

	HashMap<Player, List<Entity>> list = new HashMap<Player, List<Entity>>();
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractAtEntityEvent e) {
		if(e.getRightClicked().getType()==EntityType.COW) {
			Cow c = (Cow)e.getRightClicked();
			if(c.canBreed() && e.getPlayer().getItemInHand().getType()==Material.WHEAT) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}
		if(e.getRightClicked().getType()==EntityType.PIG) {
			Pig c = (Pig)e.getRightClicked();
			if(c.canBreed() && e.getPlayer().getItemInHand().getType().name().contains("CARROT")) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}
		if(e.getRightClicked().getType()==EntityType.SHEEP) {
			Sheep c = (Sheep)e.getRightClicked();
			if(c.canBreed() && e.getPlayer().getItemInHand().getType()==Material.WHEAT) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}
		if(e.getRightClicked().getType()==EntityType.CHICKEN) {
			Chicken c = (Chicken)e.getRightClicked();
			if(c.canBreed() && e.getPlayer().getItemInHand().getType().name().contains("SEEDS")) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}
		if(e.getRightClicked().getType()==EntityType.HORSE) {
			Horse c = (Horse)e.getRightClicked();
			if(c.canBreed()) {
				if(e.getPlayer().getItemInHand().getType()==Material.WHEAT
						||e.getPlayer().getItemInHand().getType()==Material.APPLE
						||e.getPlayer().getItemInHand().getType()==Material.GOLDEN_APPLE
						||e.getPlayer().getItemInHand().getType()==Material.GOLDEN_CARROT) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}}
		if(e.getRightClicked().getType()==EntityType.WOLF) {
			Wolf c = (Wolf)e.getRightClicked();
			if(c.canBreed())
				if(e.getPlayer().getItemInHand().getType().name().equals("RAW_BEEF")) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
			}
		}
		if(e.getRightClicked().getType().name().equals("OCELOT")||e.getRightClicked().getType().name().equals("CAT")) {
			Ocelot c = (Ocelot)e.getRightClicked();
			if(c.canBreed()) {
				if(e.getPlayer().getItemInHand().getType().name().equals("RAW_FISH")
						|| e.getPlayer().getItemInHand().getType().name().equals("COD")
						|| e.getPlayer().getItemInHand().getType().name().equals("SALMON")) {
				List<Entity> l = list.get(e.getPlayer());
				l.add(e.getRightClicked());
				list.put(e.getPlayer(), l);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSpawn(CreatureSpawnEvent e) {
		if(e.getSpawnReason()==SpawnReason.BREEDING) {
			if(e.getEntity().getType()==EntityType.COW) {
				Cow c = (Cow)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.COW) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType()==EntityType.PIG) {
				Pig c = (Pig)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.PIG) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType()==EntityType.CHICKEN) {
				Chicken c = (Chicken)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.CHICKEN) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType()==EntityType.SHEEP) {
				Sheep c = (Sheep)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.SHEEP) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType()==EntityType.HORSE) {
				Horse c = (Horse)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.HORSE) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType()==EntityType.WOLF) {
				Wolf c = (Wolf)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType()==EntityType.WOLF) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
			if(e.getEntity().getType().name().equals("OCELOT")||e.getEntity().getType().name().equals("CAT")) {
				Ocelot c = (Ocelot)e.getEntity();
				if(!c.isAdult()) {
					List<Entity> entity = new ArrayList<Entity>();
					for(Entity d : c.getWorld().getNearbyEntities(c.getLocation(), 20, 20, 20)) {
						if(d.getType().name().equals("OCELOT")||d.getType().name().equals("CAT")) {
							entity.add(d);
						}
					}
					for(Player p : list.keySet()) {
						List<Entity> es = list.get(p);
						for(Entity f : es) {
							if(entity.contains(f)) {
								es.remove(f);
								list.remove(p);
								list.put(p, es);
								EntityBreedEvent event = new EntityBreedEvent(p,e.getLocation(),e.getEntity());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
}
