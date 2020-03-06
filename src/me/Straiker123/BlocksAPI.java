package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
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
	 
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                   blocks.add(from.getWorld().getBlockAt(x, y, z));
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
	 
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                   blocks.add(new Location(from.getWorld(),x, y, z));
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
	 
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                	if(new Location(from.getWorld(),x, y, z).getBlock().getType()!=ignore)
	                   blocks.add(new Location(from.getWorld(),x, y, z));
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
	 
	        for(int x = bottomBlockX; x <= topBlockX; x++){
	            for(int z = bottomBlockZ; z <= topBlockZ; z++){
	                for(int y = bottomBlockY; y <= topBlockY; y++){
	                	if(!ignore.contains(new Location(from.getWorld(),x, y, z).getBlock().getType()))
	                   blocks.add(new Location(from.getWorld(),x, y, z));
	                }
	            }
	        }
	        return blocks;
	    }
	
	//return List<Block>
		  public List<Location> getBlockLocations(Shape form,Location where, int radius){
			  List<Location> blocks = new ArrayList<Location>();
			  switch(form) {
			  case Square:
			     for(double x = where.getX() - radius; x <= where.getX() + radius; x++)
			       for(double y = where.getY() - radius; y <= where.getY() + radius; y++)
			         for(double z = where.getZ() - radius; z <= where.getZ() + radius; z++)
			           blocks.add(new Location(where.getWorld(), x, y, z));
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) 
					     blocks.add(new Location(where.getWorld(),X + where.getBlockX(), Y + where.getBlockY(), Z + where.getBlockZ()));
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
 
        for(int x = bottomBlockX; x <= topBlockX; x++){
            for(int z = bottomBlockZ; z <= topBlockZ; z++){
                for(int y = bottomBlockY; y <= topBlockY; y++){
    	        	if(ignore != from.getWorld().getBlockAt(x, y, z).getType())
                   blocks.add(from.getWorld().getBlockAt(x, y, z));
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
		 
		        for(int x = bottomBlockX; x <= topBlockX; x++){
		            for(int z = bottomBlockZ; z <= topBlockZ; z++){
		                for(int y = bottomBlockY; y <= topBlockY; y++){
		    	        	if(!ignore.contains(from.getWorld().getBlockAt(x, y, z).getType()))
				                   blocks.add(from.getWorld().getBlockAt(x, y, z));
		                }
		            }
		        }
		        return blocks;
	    }
	
	//return List<Block>
		  public List<Block> getBlocks(Shape form,Location where, int radius){
			  List<Block> blocks = new ArrayList<Block>();
			  switch(form) {
			  case Square:
			     for(int x = where.getBlockX() - radius; x <= where.getBlockX() + radius; x++)
			       for(int y = where.getBlockY() - radius; y <= where.getBlockY() + radius; y++)
			         for(int z = where.getBlockZ() - radius; z <= where.getBlockZ() + radius; z++)
			           blocks.add(where.getWorld().getBlockAt(x, y, z));
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) 
					     blocks.add(where.getWorld().getBlockAt(X + where.getBlockX(), Y + where.getBlockY(), Z + where.getBlockZ()));
			 }
			     return blocks;
			     }

			public void setBlock(Location loc, Material material) {
				  if(!material.isBlock())return;
				  BlockState s = loc.getBlock().getState();
				  s.setType(material);
				  s.update(true,true);
			  }
			public void setBlock(Block loc, Material material) {
				  if(!material.isBlock())return;
				  BlockState s = loc.getState();
				  s.setType(material);
				  s.update(true,true);
			  }
	  
	  public void setBlockSave(BlockSave s) {
		  Block b = s.getWorld().getBlockAt(s.getLocation());
		  BlockState state = b.getState();
		  state.setType(s.getMaterial());

			try {
		  state.setBlockData(s.getBlockData());
			}catch(Exception|NoSuchMethodError|NoSuchFieldError er) {
				//old version
			}
		  state.setData(s.getMaterialData());
			if(b.getType().name().contains("SIGN")) {
				Sign w = (Sign)b.getState();
				int i = 0;
				if(s.getSignLines() != null && s.getSignLines().length > 0)
				for(String line : s.getSignLines()) {
				w.setLine(i, line);
				++i;
				}
				try {
					if(s.getColor()!=null)
					w.setColor(s.getColor());
				}catch(Exception er) {
					//old version
				}
			}
			if(b.getType().name().contains("CHEST")) {
				Chest w = (Chest)b.getState();
				if(s.getBlockInventory() != null)
				w.getBlockInventory().setContents(s.getBlockInventory());
			}
			try {
			if(b.getType().name().contains("SHULKER_BOX")) {
				ShulkerBox w = (ShulkerBox)b.getState();
				if(s.getBlockInventory() != null)
				w.getInventory().setContents(s.getBlockInventory());
			}

			  }catch(Exception |NoSuchFieldError e) {
				  
			  }
			if(b.getType().name().contains("COMMAND")) {
				CommandBlock w = (CommandBlock)b.getState();
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
	  
		  public List<Location> getBlocksLocation(Shape form,Location where, int radius){
			  List<Location> blocks = new ArrayList<Location>();
			  switch(form) {
			  case Square:
			     for(double x = where.getX() - radius; x <= where.getX() + radius; x++){
			       for(double y = where.getY() - radius; y <= where.getY() + radius; y++){
			         for(double z = where.getZ() - radius; z <= where.getZ() + radius; z++){
			           blocks.add(new Location(where.getWorld(), x, y, z));
			      }
			      }
			     }
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
					     blocks.add(new Location (where.getWorld(),X + where.getBlockX(), Y + where.getBlockY(), Z + where.getBlockZ()));
					 }
			  }
			     return blocks;
			     }
		  
		  
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
		  
}
