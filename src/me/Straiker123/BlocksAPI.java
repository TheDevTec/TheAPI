package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;

import me.Straiker123.Utils.Error;

public class BlocksAPI {
	
	public static enum Shape{
		Sphere,
		Square
	}
	public String getLocationAsString(Location loc) {
		return TheAPI.getStringUtils().getLocationAsString(loc);
	}
	
	public Location getLocationFromString(String saved) {
		return TheAPI.getStringUtils().getLocationFromString(saved);
		}
    public List<Entity> getNearbyEntities(Location l, int radius){
    	if(radius > 256) {
    		Error.err("getting nearby entities", "The radius cannot be greater than 256");
    		return new ArrayList<Entity>();
    	}
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<Entity> radiusEntities = new ArrayList<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities;
    }

    public List<Entity> getNearbyEntities(Entity ed, int radius){
    	if(radius > 256) {
    		Error.err("getting nearby entities", "The radius cannot be greater than 256");
    		return new ArrayList<Entity>();
    	}
    	Location l = ed.getLocation();
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<Entity> radiusEntities = new ArrayList<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities;
    }

    public List<Entity> getNearbyEntities(World world, double x, double y, double z, int radius){
    	if(radius > 256) {
    		Error.err("getting nearby entities", "The radius cannot be greater than 256");
    		return new ArrayList<Entity>();
    	}
    	Location l = new Location(world,x,y,z);
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        List<Entity> radiusEntities = new ArrayList<Entity>();
            for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
                for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                    int xs=(int) l.getX(),ys=(int) l.getY(),zs=(int) l.getZ();
                    for (Entity e : new Location(l.getWorld(),xs+(chX*16),ys,zs+(chZ*16)).getChunk().getEntities()){
                        if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities;
    }
	
	public BlockSave getBlockSave(Block b) {
		return new BlockSave(b);
	}
	   public List<Block> getBlocks(Location from, Location to){
	        List<Block> blocks = new ArrayList<Block>();
	        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        World w = from.getWorld();
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                   blocks.add(w.getBlockAt(x, y, z));
	                }
	            }
	        }
	        return blocks;
	    }
	   public List<Location> getBlockLocations(Location from, Location to){
	        List<Location> blocks = new ArrayList<Location>();
	        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        World w = from.getWorld();
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                   blocks.add(new Location(w,x, y, z));
	                }
	            }
	        }
	        return blocks;
	    }
		public List<Location> getBlockLocations(Location from, Location to, Material ignore){
			List<Location> blocks = new ArrayList<Location>();
	        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        World w = from.getWorld();
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                	if(new Location(from.getWorld(),x, y, z).getBlock().getType()!=ignore)
	                   blocks.add(new Location(w,x, y, z));
	                }
	            }
	        }
	        return blocks;
	    }
	   public List<Location> getBlockLocations(Location from, Location to, List<Material> ignore){
		   List<Location> blocks = new ArrayList<Location>();
	        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
	        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
	        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
	        World w = from.getWorld();
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                	if(!ignore.contains(new Location(w,x, y, z).getBlock().getType()))
	                   blocks.add(new Location(w,x, y, z));
	                }
	            }
	        }
	        return blocks;
	    }
	
	//return List<Block>
		  public List<Location> getBlocksLocation(Shape form,Location where, int radius){
			  return getBlockLocations(form, where, radius);
		  }
		  public List<Location> getBlockLocations(Shape form,Location where, int radius){
			  List<Location> blocks = new ArrayList<Location>();
		        World w = where.getWorld();
		        int Xx = where.getBlockX();
		        int Yy = where.getBlockY();
		        int Zz = where.getBlockZ();
			  switch(form) {
			  case Square:
			     for(int x =Xx - radius; x <= Xx + radius; x++)
			       for(int y = Yy - radius; y <= Yy + radius; y++)
			         for(int z = Zz - radius; z <= Zz + radius; z++)
			           blocks.add(new Location(w, x, y, z));
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) 
					     blocks.add(new Location(w,X +Xx, Y +Yy, Z + Zz));
				}
			     return blocks;
			 }
	   
	public List<Block> getBlocks(Location from, Location to, Material ignore){
		List<Block> blocks = new ArrayList<Block>();
        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
        World w = from.getWorld();
        for(int x = bottomBlockX; x <= topBlockX; x++){
            for(int z = bottomBlockZ; z <= topBlockZ; z++){
                for(int y = bottomBlockY; y <= topBlockY; y++){
    	        	if(ignore != w.getBlockAt(x, y, z).getType())
                   blocks.add(w.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
	    }
	   public List<Block> getBlocks(Location from, Location to, List<Material> ignore){
	        	List<Block> blocks = new ArrayList<Block>();
		        int topBlockX = (from.getBlockX() < to.getBlockX() ? to.getBlockX() : from.getBlockX());
		        int bottomBlockX = (from.getBlockX() > to.getBlockX() ? to.getBlockX() : from.getBlockX());
		        int topBlockY = (from.getBlockY() < to.getBlockY() ? to.getBlockY() : from.getBlockY());
		        int bottomBlockY = (from.getBlockY() > to.getBlockY() ? to.getBlockY() : from.getBlockY());
		        int topBlockZ = (from.getBlockZ() < to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		        int bottomBlockZ = (from.getBlockZ() > to.getBlockZ() ? to.getBlockZ() : from.getBlockZ());
		        World w = from.getWorld();
		        for(int x = bottomBlockX; x <= topBlockX; x++){
		            for(int z = bottomBlockZ; z <= topBlockZ; z++){
		                for(int y = bottomBlockY; y <= topBlockY; y++){
		    	        	if(!ignore.contains(w.getBlockAt(x, y, z).getType()))
				                   blocks.add(w.getBlockAt(x, y, z));
		                }
		            }
		        }
		        return blocks;
	    }
	
	//return List<Block>
		  public List<Block> getBlocks(Shape form,Location where, int radius){
			  List<Block> blocks = new ArrayList<Block>();
		        World w = where.getWorld();
		        int Xx = where.getBlockX();
		        int Yy = where.getBlockY();
		        int Zz = where.getBlockZ();
			  switch(form) {
			  case Square:
			     for(int x =Xx - radius; x <= Xx + radius; x++)
			       for(int y = Yy - radius; y <= Yy + radius; y++)
			         for(int z = Zz - radius; z <= Zz + radius; z++)
			           blocks.add(w.getBlockAt(x, y, z));
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) 
					     blocks.add(w.getBlockAt(X +Xx, Y +Yy, Z + Zz));
				}
			     return blocks;
			 }

			public void setBlock(Location loc, Material material) {
				  if(!material.isBlock())return;
				  TheAPI.getNMSAPI().setBlock(loc, material, 0, true);
			  }
			public void setBlock(Block loc, Material material) {
				  if(!material.isBlock())return;
				  TheAPI.getNMSAPI().setBlock(loc.getLocation(), material, 0, true);
			  }
	  
	  public void setBlockSave(BlockSave s) {
		  Block b = s.getWorld().getBlockAt(s.getLocation());
		  String n = b.getType().name();
		  BlockState state = b.getState();
		  state.setType(s.getMaterial());
		  state.setData(s.getMaterialData());
			if(n.contains("SIGN")) {
				Sign w = (Sign)state;
				int i = 0;
				if(s.getSignLines() != null && s.getSignLines().length > 0)
				for(String line : s.getSignLines()) {
				w.setLine(i, line);
				++i;
				}
				try {
					if(s.getColor()!=null)
					w.setColor(s.getColor());
				}catch(Exception|NoSuchFieldError er) {
					//old version
				}
			}
			if(n.contains("CHEST")) {
				Chest w = (Chest)state;
				if(s.getBlockInventory() != null)
				w.getBlockInventory().setContents(s.getBlockInventory());
			}
			  if(n.equals("DROPPER")) {
				  Dropper w = (Dropper)state;
					if(s.getBlockInventory() != null)
					w.getInventory().setContents(s.getBlockInventory());
			  }
			  if(n.equals("DISPENSER")) {
				  Dispenser w = (Dispenser)state;
					if(s.getBlockInventory() != null)
					w.getInventory().setContents(s.getBlockInventory());
			  }
			  if(n.equals("HOPPER")) {
				  Hopper w = (Hopper)state;
					if(s.getBlockInventory() != null)
					w.getInventory().setContents(s.getBlockInventory());
			  }
			if(n.contains("SHULKER_BOX")) {
				ShulkerBox w = (ShulkerBox)state;
				if(s.getBlockInventory() != null)
				w.getInventory().setContents(s.getBlockInventory());
			}
			if(n.contains("COMMAND")) {
				CommandBlock w = (CommandBlock)state;
				if(s.getCommand() != null)
				w.setCommand(s.getCommand());
				if(s.getCommandBlockName() != null)
				w.setName(s.getCommandBlockName());
			}
			state.update(true, true);
	  }
	  public void setBlockSaves(List<BlockSave> list) { //like //undo command
		  for(BlockSave s : list) {
			  setBlockSave(s);
		  }
	  }
	  public void loadBlockSaves(List<BlockSave> list) { //like //undo command
		  loadBlockSaves(list);
	  }
	  public void loadBlockSave(BlockSave save) { //like //undo command
		  setBlockSave(save);
	  }
	  
	  public List<Block> getBlocks(Shape form,Location where, int radius, Material ignore){
		  if(!ignore.isBlock())return new ArrayList<Block>();
		  List<Block> blocks = new ArrayList<Block>();
		  for(Block b : getBlocks(form, where, radius)) {
			  if(b.getType()!=ignore)blocks.add(b);
		  }
		  return blocks;
	  }
	  public List<Block> getBlocks(Shape form,Location where, int radius, List<Material> ignore){
		  List<Block> blocks = new ArrayList<Block>();
		  for(Block b : getBlocks(form, where, radius)) {
			  if(!ignore.contains(b.getType()))blocks.add(b);
		  }
		  return blocks;
	  }

	  public void replace(Location from,Location to, Material block, Material with) {
		  for(Block c : getBlocks(from,to))
			  if(c.getType()==block)
			  c.setType(with);
	  }
	  public void replace(Shape form,Location where, int radius, Material block, Material with) {
		  for(Block c : getBlocks(form,where,radius))
			  if(c.getType()==block)
			  c.setType(with);
	  }

	  public void replace(Location from,Location to, Material block, List<Material> with) {
		  for(Block c : getBlocks(from,to))
			  if(c.getType()==block)
			  c.setType((Material)TheAPI.getRandomFromList(with));
	  }
	  public void replace(Shape form,Location where, int radius, Material block, List<Material> with) {
		  for(Block c : getBlocks(form,where,radius))
			  if(c.getType()==block)
			  c.setType((Material)TheAPI.getRandomFromList(with));
	  }
	  public void replace(Location from,Location to, Material block, HashMap<Material, Integer> with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : with.keySet()) {
			  for(int i = -1; i > with.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  for(Block cs : getBlocks(from,to))
			  if(cs.getType()==block)
			  cs.setType((Material)TheAPI.getRandomFromList(c));
	  }

	  public void replace(Shape form,Location where, int radius, Material block, HashMap<Material, Integer> with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : with.keySet()) {
			  for(int i = -1; i > with.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  for(Block cs : getBlocks(form,where,radius))
			  if(cs.getType()==block)
			  cs.setType((Material)TheAPI.getRandomFromList(c));
	  }
	  public void replace(Shape form,Location where, int radius, HashMap<Material, Integer> block, Material with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : block.keySet()) {
			  for(int i = -1; i > block.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  for(Block cs : getBlocks(form,where,radius))
			  if(c.contains(cs.getType()))
			  cs.setType((Material)TheAPI.getRandomFromList(c));
	  }
	  public void replace(Location from,Location to, HashMap<Material, Integer> block, Material with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : block.keySet()) {
			  for(int i = -1; i > block.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  for(Block cs : getBlocks(from,to))
			  if(c.contains(cs.getType()))
			  cs.setType((Material)TheAPI.getRandomFromList(c));
	  }
	  public void replace(Shape form,Location where, int radius, HashMap<Material, Integer> block, HashMap<Material, Integer> with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : block.keySet()) {
			  for(int i = -1; i > block.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  List<Material> d = new ArrayList<Material>();
		  for(Material m : with.keySet()) {
			  for(int i = -1; i > with.get(m); ++i) {
				  d.add(m);
			  }
		  }
		  for(Block cs : getBlocks(form,where,radius))
			  if(c.contains(cs.getType()))
			  cs.setType((Material)TheAPI.getRandomFromList(d));
	  }
	  public void replace(Location from,Location to, HashMap<Material, Integer> block, HashMap<Material, Integer> with) {
		  List<Material> c = new ArrayList<Material>();
		  for(Material m : block.keySet()) {
			  for(int i = -1; i > block.get(m); ++i) {
				  c.add(m);
			  }
		  }
		  List<Material> d = new ArrayList<Material>();
		  for(Material m : with.keySet()) {
			  for(int i = -1; i > with.get(m); ++i) {
				  d.add(m);
			  }
		  }
		  for(Block cs : getBlocks(from,to))
			  if(c.contains(cs.getType()))
			  cs.setType((Material)TheAPI.getRandomFromList(d));
	  }

	  public void replace(Shape form,Location where, int radius, List<Material> block,Material with) {
		  for(Block c : getBlocks(form,where,radius))
			  if(block.contains(c.getType()))
			  c.setType(with);
	  }
	  public void replace(Shape form,Location where, int radius, List<Material> block, List<Material> with) {
		  for(Block c : getBlocks(form,where,radius))
			  if(block.contains(c.getType()))
			  c.setType((Material)TheAPI.getRandomFromList(with));
	  }
	  
	  public void replace(Location from,Location to, List<Material> block,Material with) {
		  for(Block c : getBlocks(from,to))
			  if(block.contains(c.getType()))
			  c.setType(with);
	  }
	  public void replace(Location from,Location to, List<Material> block, List<Material> with) {
		  for(Block c : getBlocks(from,to))
			  if(block.contains(c.getType()))
			  c.setType((Material)TheAPI.getRandomFromList(with));
	  }
	  
	  public void set(Shape form,Location where, int radius, Material block){
		  for(Block c : getBlocks(form,where,radius))
			  c.setType(block);
	     }
	  //Material
	  public void set(Shape form,Location where, int radius, Material block, List<Material> ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore.contains(c.getType()))continue;
			  c.setType(block);
		  }}
	  //Material
	  public void set(Shape form,Location where, int radius, Material block, Material ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setType(block);
		  }}
	  //List<Material>
	  public void set(Shape form,Location where, int radius, List<Material> block){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //List<Material>
	  public void set(Shape form,Location where, int radius, List<Material> block, List<Material> ignore){
		  List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore.contains(c.getType()))continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //List<Material>
	  public void set(Shape form,Location where, int radius, List<Material> block, Material ignore){
		  List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //HashMap<Material, ChanceToSet>
	  public void set(Shape form,Location where, int radius, HashMap<Material, Integer> block){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Material d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
     		 for(Block c : getBlocks(form,where,radius)) {
   			  c.setType((Material)TheAPI.getRandomFromList(s));
   		  }}
	  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
	  public void set(Shape form,Location where, int radius, HashMap<Material, Integer> block, List<Material> ignore){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Material d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
 		 for(Block c : getBlocks(form,where,radius)) {
 			 if(ignore.contains(c.getType()))continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}

	  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
	  public void set(Shape form,Location where, int radius, HashMap<Material, Integer> block, Material ignore){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Material d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
 		 for(Block c : getBlocks(form,where,radius)) {
 			 if(ignore==c.getType())continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  

	  //locations
		  public void set(Location from,Location to, Material block){
			  for(Block c : getBlocks(from,to))
				  c.setType(block);
		     }
		  //Material
		  public void set(Location from,Location to, Material block, List<Material> ignore){
			  for(Block c : getBlocks(from,to)) {
				  if(ignore.contains(c.getType()))continue;
				  c.setType(block);
			  }}
		  //Material
		  public void set(Location from,Location to, Material block, Material ignore){
			  for(Block c : getBlocks(from,to)) {
				  if(ignore==c.getType())continue;
				  c.setType(block);
			  }}
		  //List<Material>
		  public void set(Location from,Location to, List<Material> block){
		     	 List<Object> s = new ArrayList<Object>();
		     	 for(Material d : block)s.add(d);
			  for(Block c : getBlocks(from,to)) {
				  c.setType((Material)TheAPI.getRandomFromList(s));
			  }}
		  //List<Material>
		  public void set(Location from,Location to, List<Material> block, List<Material> ignore){
			  List<Object> s = new ArrayList<Object>();
		     	 for(Material d : block)s.add(d);
			  for(Block c : getBlocks(from,to)) {
				  if(ignore.contains(c.getType()))continue;
				  c.setType((Material)TheAPI.getRandomFromList(s));
			  }}
		  //List<Material>
		  public void set(Location from,Location to, List<Material> block, Material ignore){
			  List<Object> s = new ArrayList<Object>();
		     	 for(Material d : block)s.add(d);
			  for(Block c : getBlocks(from,to)) {
				  if(ignore==c.getType())continue;
				  c.setType((Material)TheAPI.getRandomFromList(s));
			  }}
		  //HashMap<Material, ChanceToSet>
		  public void set(Location from,Location to, HashMap<Material, Integer> block){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block.keySet())if(s.contains(d) == false) {
	     		 for(int i = 0; i < block.get(d); ++i)
	     		 s.add(d);
	     	 }
	     		 for(Block c : getBlocks(from,to)) {
	   			  c.setType((Material)TheAPI.getRandomFromList(s));
	   		  }}
		  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
		  public void set(Location from,Location to, HashMap<Material, Integer> block, List<Material> ignore){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block.keySet())if(s.contains(d) == false) {
	     		 for(int i = 0; i < block.get(d); ++i)
	     		 s.add(d);
	     	 }
	 		 for(Block c : getBlocks(from,to)) {
	 			 if(ignore.contains(c.getType()))continue;
				  c.setType((Material)TheAPI.getRandomFromList(s));
			  }}

		  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
		  public void set(Location from,Location to, HashMap<Material, Integer> block, Material ignore){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block.keySet())if(s.contains(d) == false) {
	     		 for(int i = 0; i < block.get(d); ++i)
	     		 s.add(d);
	     	 }
	 		 for(Block c : getBlocks(from,to)) {
	 			 if(ignore==c.getType())continue;
				  c.setType((Material)TheAPI.getRandomFromList(s));
			  }}

			//square
			public boolean isInside(Entity entity, Location a, Location b){
				return isInside(entity.getLocation(),a,b);
			}
			//square
			public boolean isInside(Location location, Location a, Location b){
		        return new IntRange(a.getX(), b.getX()).containsDouble(location.getX())
		                && new IntRange(a.getY(), b.getY()).containsDouble(location.getY())
		                &&  new IntRange(a.getZ(), b.getZ()).containsDouble(location.getZ());
		    }
}
