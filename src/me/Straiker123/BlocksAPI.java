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

import com.google.common.collect.Lists;

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
    	return getNearbyEntities(new Position(l),radius);
    }

    public List<Entity> getNearbyEntities(Position l, int radius){
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
                        if (l.distance(e.getLocation()) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                    }
                }
            }
        return radiusEntities;
    }

    public List<Entity> getNearbyEntities(Entity ed, int radius){
    	return getNearbyEntities(ed.getLocation(),radius);
    }

    public List<Entity> getNearbyEntities(World world, double x, double y, double z, int radius){
    	return getNearbyEntities(new Location(world,x,y,z),radius);
    }
	
	public BlockSave getBlockSave(Position b) {
		return new BlockSave(b);
	}
	
	public List<Position> get(Position from, Position to, TheMaterial ignore){
		List<Position> blocks = new ArrayList<Position>();
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
                	Position s = new Position(w,x,y,z);
    	        	if(ignore != s.getType())
                   blocks.add(s);
                }
            }
        }
        return blocks;
	    }
	   
	public List<Position> get(Position from, Position to, List<TheMaterial> ignore){
	        	List<Position> blocks = new ArrayList<Position>();
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
		                	Position s = new Position(w,x,y,z);
		    	        	if(!ignore.contains(s.getType()))
				                   blocks.add(s);
		                }
		            }
		        }
		        return blocks;
	    }
	
	public List<Position> get(Shape form,Position where, int radius){
			  List<Position> blocks = new ArrayList<Position>();
		        World w = where.getWorld();
		        int Xx = where.getBlockX();
		        int Yy = where.getBlockY();
		        int Zz = where.getBlockZ();
			  switch(form) {
			  case Square:
			     for(int x =Xx - radius; x <= Xx + radius; x++)
			       for(int y = Yy - radius; y <= Yy + radius; y++)
			         for(int z = Zz - radius; z <= Zz + radius; z++)
			           blocks.add(new Position(w,x,y,z));
			     break;
			  case Sphere:
				for (int Y = -radius; Y < radius; Y++)
					for (int X = -radius; X < radius; X++)
					   for (int Z = -radius; Z < radius; Z++)
					    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) 
					     blocks.add(new Position(w,X +Xx, Y +Yy, Z + Zz));
				}
			     return blocks;
			 }

	public void set(Position loc, Material material) {
		if(!material.isBlock())return;
		TheAPI.getNMSAPI().setBlock(loc.toLocation(), material, 0, true);
	}

	public void set(Block loc, Material material) {
		if(!material.isBlock())return;
		TheAPI.getNMSAPI().setBlock(loc.getLocation(), material, 0, true);
	}

	public void loadBlockSaves(List<BlockSave> list) { //like //undo command
		  loadBlockSaves(list);

	  }
	  
	@SuppressWarnings("deprecation")
	public void loadBlockSave(BlockSave s) { //like //undo command
		Block b = s.getWorld().getBlockAt(s.getLocation().toLocation());
		  String n = b.getType().name();
		  BlockState state = b.getState();
		  state.setType(s.getMaterial().getType());
		  state.setRawData((byte)s.getMaterial().getData());
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
	  
	public List<Position> get(Shape form,Position where, int radius, TheMaterial ignore){
		  List<Position> blocks = new ArrayList<Position>();
	        World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(ignore!=s.getType())
		           blocks.add(s);
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(ignore!=s.getType())
				     blocks.add(s);
				    }
			}
		     return blocks;
	  }
	
	public List<Position> get(Shape form,Position where, int radius, List<TheMaterial> ignore){
		  List<Position> blocks = new ArrayList<Position>();
	        World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(!ignore.contains(s.getType()))
		           blocks.add(s);
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(!ignore.contains(s.getType()))
				     blocks.add(s);
				    }
			}
		     return blocks;
	  }

	public void replace(Position from,Position to, TheMaterial block, TheMaterial with) {
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
	                	Position s = new Position(w,x,y,z);
	    	        	if(s.getType()==block)
			                s.setType(with);
	                }
	            }
	        }
	  }
	  
	public void replace(Shape form,Position where, int radius, TheMaterial block, TheMaterial with) {
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(block==s.getType())
		        		 s.setType(with);
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(block==s.getType())
			        		 s.setType(with);
				    }
			}
	  }

	public void replace(Position from,Position to, TheMaterial block, List<TheMaterial> with) {
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
	                	Position s = new Position(w,x,y,z);
	    	        	if(s.getType()==block)
			                s.setType((TheMaterial)TheAPI.getRandomFromList(with));
	                }
	            }
	        }
	  }
	
	public void replace(Position from,Position to, TheMaterial block, HashMap<TheMaterial, Double> with) {
		  List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : with.keySet())
			  for(int i = -1; i > with.get(m); ++i)
				  c.add(m);
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
	                	Position s = new Position(w,x,y,z);
	    	        	if(s.getType()==block)
			                s.setType((TheMaterial)TheAPI.getRandomFromList(c));
	                }
	            }
	        }
	  }

	public void replace(Shape form,Position where, int radius, TheMaterial block, HashMap<TheMaterial, Double> with) {
		  List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : with.keySet())
			  for(int i = -1; i > with.get(m); ++i)
				  c.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(block==s.getType())
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(block==s.getType())
			        		 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
				    }
			}
	  }
	  
	public void replace(Shape form,Position where, int radius, HashMap<TheMaterial, Double> block, TheMaterial with) {
		  List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
			        		 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
				    }
			}
	  }
	 
    public void replace(Position from,Position to, HashMap<TheMaterial, Double> block, TheMaterial with) {
		  List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
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
	                	Position s = new Position(w,x,y,z);
			        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
			                s.setType((TheMaterial)TheAPI.getRandomFromList(c));
	                }
	            }
	        }
	  }
	  
    public void replace(Shape form,Position where, int radius, HashMap<TheMaterial, Double> block, HashMap<TheMaterial, Double> with) {
		  List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
		  List<TheMaterial> d = Lists.newArrayList();
		  for(TheMaterial m : with.keySet())
			  for(int i = -1; i > with.get(m); ++i)
				  d.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(d));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
			        		 s.setType((TheMaterial)TheAPI.getRandomFromList(d));
				    }
			}
	  }
	  
    public void replace(Position from,Position to, HashMap<TheMaterial, Double> block, HashMap<TheMaterial, Double> with) {
   	 List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
	    	 List<TheMaterial> d = Lists.newArrayList();
			  for(TheMaterial m : with.keySet())
				  for(int i = -1; i > with.get(m); ++i)
					  d.add(m);
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
	                	Position s = new Position(w,x,y,z);
			        	 if(block.containsKey(s.getType()) && TheAPI.generateChance(block.get(s.getType())))
			                s.setType((TheMaterial)TheAPI.getRandomFromList(c));
	                }
	            }
	        }
	  }
  
    public void replace(Shape form,Position where, int radius, List<TheMaterial> block,TheMaterial with) {
    	World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(block.contains(s.getType()))
	        		 s.setType(with);
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(block.contains(s.getType()))
		        		 s.setType(with);
			    }
		}
	}
	  
    public void replace(Shape form,Position where, int radius, List<TheMaterial> block, List<TheMaterial> with) {
    	World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(block.contains(s.getType()))
	        		 s.setType((TheMaterial)TheAPI.getRandomFromList(with));
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(block.contains(s.getType()))
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(with));
			    }
		}
	}
	  
	public void replace(Position from,Position to, List<TheMaterial> block,TheMaterial with) {
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
                	Position s = new Position(w,x,y,z);
		        	 if(block.contains(s.getType()))
		                s.setType(with);
                }
            }
        }
	}
	  
	public void replace(Position from,Position to, List<TheMaterial> block, List<TheMaterial> with) {
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
                	Position s = new Position(w,x,y,z);
		        	 if(block.contains(s.getType()))
		                s.setType((TheMaterial)TheAPI.getRandomFromList(with));
                }
            }
        }
	}
	  
	public void set(Shape form,Position where, int radius, TheMaterial block){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        		 s.setType(block);
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        		 s.setType(block);
			    }
		}
	 }
	  
	public void set(Shape form,Position where, int radius, TheMaterial block, List<TheMaterial> ignore){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(!ignore.contains(s.getType()))
	        		 s.setType(block);
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(!ignore.contains(s.getType()))
		        		 s.setType(block);
			    }
		}
	}
	  
	public void set(Shape form,Position where, int radius, TheMaterial block, TheMaterial ignore){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(ignore!=s.getType())
	        		 s.setType(block);
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(ignore!=s.getType())
		        		 s.setType(block);
			    }
		}
	}
	  
	public void set(Shape form,Position where, int radius, List<TheMaterial> block){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
			    }
		}
	}
	  
	public void set(Shape form,Position where, int radius, List<TheMaterial> block, List<TheMaterial> ignore){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(!ignore.contains(s.getType()))
	        		 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(!ignore.contains(s.getType()))
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
			    }
		}
	}
	  
	public void set(Shape form,Position where, int radius, List<TheMaterial> block, TheMaterial ignore){
		World w = where.getWorld();
        int Xx = where.getBlockX();
        int Yy = where.getBlockY();
        int Zz = where.getBlockZ();
	  switch(form) {
	  case Square:
	     for(int x =Xx - radius; x <= Xx + radius; x++)
	       for(int y = Yy - radius; y <= Yy + radius; y++)
	         for(int z = Zz - radius; z <= Zz + radius; z++) {
	        	 Position s = new Position(w,x,y,z);
	        	 if(ignore!=s.getType())
	        		 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
	         }
	     break;
	  case Sphere:
		for (int Y = -radius; Y < radius; Y++)
			for (int X = -radius; X < radius; X++)
			   for (int Z = -radius; Z < radius; Z++)
			    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
		        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
		        	 if(ignore!=s.getType())
		        		 s.setType((TheMaterial)TheAPI.getRandomFromList(block));
			    }
		}
	}
	  
	public void set(Shape form,Position where, int radius, HashMap<TheMaterial, Integer> block){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
				    }
			}
	}
	  
	public void set(Shape form,Position where, int radius, HashMap<TheMaterial, Integer> block, List<TheMaterial> ignore){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(!ignore.contains(s.getType()))
		        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(!ignore.contains(s.getType()))
			        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
				    }
			}
	}
	  
	public void set(Shape form,Position where, int radius, HashMap<TheMaterial, Integer> block, TheMaterial ignore){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
		  World w = where.getWorld();
	        int Xx = where.getBlockX();
	        int Yy = where.getBlockY();
	        int Zz = where.getBlockZ();
		  switch(form) {
		  case Square:
		     for(int x =Xx - radius; x <= Xx + radius; x++)
		       for(int y = Yy - radius; y <= Yy + radius; y++)
		         for(int z = Zz - radius; z <= Zz + radius; z++) {
		        	 Position s = new Position(w,x,y,z);
		        	 if(ignore!=s.getType())
		        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
		         }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
			        	 Position s = new Position(w,X +Xx, Y +Yy, Z + Zz);
			        	 if(ignore!=s.getType())
			        	 s.setType((TheMaterial)TheAPI.getRandomFromList(c));
				    }
			}
	}
	
	public void set(Position from,Position to, TheMaterial block){
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
                	Position s = new Position(w,x,y,z);
		                s.setType(block);
                }
            }
        }
	}
		  
	public void set(Position from,Position to, TheMaterial block, List<TheMaterial> ignore){
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
                	Position s = new Position(w,x,y,z);
                	if(!ignore.contains(s.getType()))
		                s.setType(block);
                }
            }
        }
	}
		  
	public void set(Position from,Position to, TheMaterial block, TheMaterial ignore){
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
                	Position s = new Position(w,x,y,z);
                	if(ignore!=s.getType())
		                s.setType(block);
                }
            }
        }
	}
	
	public void set(Position from,Position to, List<TheMaterial> block){
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
                	Position s = new Position(w,x,y,z);
		            s.setType((TheMaterial)TheAPI.getRandomFromList(block));
                }
            }
        }
	}
		  
	public void set(Position from,Position to, List<TheMaterial> block, List<TheMaterial> ignore){
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
                	Position s = new Position(w,x,y,z);
                	if(!ignore.contains(s.getType()))
		            s.setType((TheMaterial)TheAPI.getRandomFromList(block));
                }
            }
        }
	}
		  
	public void set(Position from,Position to, List<TheMaterial> block, TheMaterial ignore){
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
                	Position s = new Position(w,x,y,z);
                	if(ignore!=s.getType())
		            s.setType((TheMaterial)TheAPI.getRandomFromList(block));
                }
            }
        }
	}
		  
	public void set(Position from,Position to, HashMap<TheMaterial, Integer> block){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
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
                	Position s = new Position(w,x,y,z);
		            s.setType((TheMaterial)TheAPI.getRandomFromList(c));
                }
            }
        }
	}
		  
	public void set(Position from,Position to, HashMap<TheMaterial, Integer> block, List<TheMaterial> ignore){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
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
                	Position s = new Position(w,x,y,z);
                	if(!ignore.contains(s.getType()))
		            s.setType((TheMaterial)TheAPI.getRandomFromList(c));
                }
            }
        }
	}

	public void set(Position from,Position to, HashMap<TheMaterial, Integer> block, TheMaterial ignore){
		List<TheMaterial> c = Lists.newArrayList();
		  for(TheMaterial m : block.keySet())
			  for(int i = -1; i > block.get(m); ++i)
				  c.add(m);
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
              	Position s = new Position(w,x,y,z);
              	if(ignore!=s.getType())
		            s.setType((TheMaterial)TheAPI.getRandomFromList(c));
              }
          }
      }
	}

	public boolean isInside(Entity entity, Position a, Position b){
		return isInside(new Position(entity.getLocation()),a,b);
	}
			
	public boolean isInside(Position location, Position a, Position b){
		return location.getWorld()==a.getWorld() && location.getWorld()==b.getWorld() && a.getWorld()==b.getWorld() 
		     && new IntRange(a.getX(), b.getX()).containsDouble(location.getX())
		     && new IntRange(a.getY(), b.getY()).containsDouble(location.getY())
		     &&  new IntRange(a.getZ(), b.getZ()).containsDouble(location.getZ());
	}

	public boolean isInside(Entity entity, Location a, Location b){
		return isInside(entity.getLocation(),a,b);
	}
			
	public boolean isInside(Location location, Location a, Location b){
		return location.getWorld()==a.getWorld() && location.getWorld()==b.getWorld() && a.getWorld()==b.getWorld() 
		     && new IntRange(a.getX(), b.getX()).containsDouble(location.getX())
		     && new IntRange(a.getY(), b.getY()).containsDouble(location.getY())
		     &&  new IntRange(a.getZ(), b.getZ()).containsDouble(location.getZ());
	}
}
