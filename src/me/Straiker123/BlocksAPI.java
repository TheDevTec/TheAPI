package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlocksAPI {
	
	public static enum Shape{
		Sphere,
		Square
	}
	
	public BlockSave getBlockSave(Block b) {
		return new BlockSave(b);
	}
	
	//return List<Block>
	  public List<Block> getBlocks(Shape form,Location where, int radius){
		  List<Block> blocks = new ArrayList<Block>();
		  switch(form) {
		  case Square:
		     for(double x = where.getX() - radius; x <= where.getX() + radius; x++){
		       for(double y = where.getY() - radius; y <= where.getY() + radius; y++){
		         for(double z = where.getZ() - radius; z <= where.getZ() + radius; z++){
		           blocks.add(new Location(where.getWorld(), x, y, z).getBlock());
		      }
		      }
		     }
		     break;
		  case Sphere:
			for (int Y = -radius; Y < radius; Y++)
				for (int X = -radius; X < radius; X++)
				   for (int Z = -radius; Z < radius; Z++)
				    if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
				     blocks.add(where.getWorld().getBlockAt(X + where.getBlockX(), Y + where.getBlockY(), Z + where.getBlockZ()));
				 }
		  }
		     return blocks;
		     }

	  public void setBlockSave(BlockSave s) {
		  Block b = s.getWorld().getBlockAt(s.getLocation());
		  b.setType(s.getMaterial());
		  b.setBlockData(s.getBlockData());
	  }
	  public void setBlockSaves(List<BlockSave> list) { //like //undo command
		  for(BlockSave s : list) {
		  Block b = s.getWorld().getBlockAt(s.getLocation());
		  b.setType(s.getMaterial());
		  b.setBlockData(s.getBlockData());
		  }
	  }
	  
	  public List<Block> getBlocks(Shape form,Location where, int radius, Material ignore){
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
	  
	  public void replaceBlocks(Shape form,Location where, int radius, Material block){
		  for(Block c : getBlocks(form,where,radius))
			  c.setType(block);
	     }
	  public void replaceBlocksWithBlock(Shape form,Location where, int radius, Block block){
		  for(Block c : getBlocks(form,where,radius))
			  c.setBlockData(block.getBlockData());
	     }
	  //Material
	  public void replaceBlocks(Shape form,Location where, int radius, Material block, List<Material> ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore.contains(c.getType()))continue;
			  c.setType(block);
		  }}
	  public void replaceBlocksWithBlock(Shape form,Location where, int radius, Block block, List<Material> ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore.contains(c.getType()))continue;
			  c.setBlockData(block.getBlockData());
		  }}
	  //Material
	  public void replaceBlocks(Shape form,Location where, int radius, Material block, Material ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setType(block);
		  }}
	  public void replaceBlocksWithBlock(Shape form,Location where, int radius, Block block, Material ignore){
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setBlockData(block.getBlockData());
		  }}
	  //List<Material>
	  public void replaceBlocks(Shape form,Location where, int radius, List<Material> block){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  public void replaceBlocksWithBlocks(Shape form,Location where, int radius, List<Block> block){
	     	 List<Object> s = new ArrayList<Object>();
	     	 for(Block d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  c.setBlockData(((Block)TheAPI.getRandomFromList(s)).getBlockData());
		  }}
	  //List<Material>
	  public void replaceBlocks(Shape form,Location where, int radius, List<Material> block, List<Material> ignore){
		  List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore.contains(c.getType()))continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //List<Material>
	  public void replaceBlocks(Shape form,Location where, int radius, List<Material> block, Material ignore){
		  List<Object> s = new ArrayList<Object>();
	     	 for(Material d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //List<Material>
	  public void replaceBlocksWithBlocks(Shape form,Location where, int radius, List<Block> block, Material ignore){
		  List<Object> s = new ArrayList<Object>();
	     	 for(Block d : block)s.add(d);
		  for(Block c : getBlocks(form,where,radius)) {
			  if(ignore==c.getType())continue;
			  c.setBlockData(((Block)TheAPI.getRandomFromList(s)).getBlockData());
		  }}
	  //HashMap<Material, ChanceToSet>
	  public void replaceBlocks(Shape form,Location where, int radius, HashMap<Material, Integer> block){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Material d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
     		 for(Block c : getBlocks(form,where,radius)) {
   			  c.setType((Material)TheAPI.getRandomFromList(s));
   		  }}
	  //HashMap<Material, ChanceToSet>
	  public void replaceBlocksWithBlocks(Shape form,Location where, int radius, HashMap<Block, Integer> block){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Block d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
     		 for(Block c : getBlocks(form,where,radius)) {
   			  c.setBlockData(((Block)TheAPI.getRandomFromList(s)).getBlockData());
   		  }}
	  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
	  public void replaceBlocks(Shape form,Location where, int radius, HashMap<Material, Integer> block, List<Material> ignore){
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
	  public void replaceBlocksWithBlocks(Shape form,Location where, int radius, HashMap<Block, Integer> block, List<Material> ignore){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Block d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
 		 for(Block c : getBlocks(form,where,radius)) {
 			 if(ignore.contains(c.getType()))continue;
			  c.setBlockData(((Block)TheAPI.getRandomFromList(s)).getBlockData());
		  }}
	  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
	  public void replaceBlocks(Shape form,Location where, int radius, HashMap<Material, Integer> block, Material ignore){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Material d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
 		 for(Block c : getBlocks(form,where,radius)) {
 			 if(ignore==c.getType())continue;
			  c.setType((Material)TheAPI.getRandomFromList(s));
		  }}
	  //HashMap<Material, ChanceToSet>, List<Material> (List with blocks these ignore - do not replace these blocks)
	  public void replaceBlocksWithBlocks(Shape form,Location where, int radius, HashMap<Block, Integer> block, Material ignore){
     	 List<Object> s = new ArrayList<Object>();
     	 for(Block d : block.keySet())if(s.contains(d) == false) {
     		 for(int i = 0; i < block.get(d); ++i)
     		 s.add(d);
     	 }
 		 for(Block c : getBlocks(form,where,radius)) {
 			 if(ignore==c.getType())continue;
			  c.setBlockData(((Block)TheAPI.getRandomFromList(s)).getBlockData());
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
}
