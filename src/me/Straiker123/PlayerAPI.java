package me.Straiker123;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import me.Straiker123.Utils.Error;

public class PlayerAPI {
	Player s;
	public PlayerAPI(Player a) {
		s=a;
	}
	public void teleport(Location loc) {
		teleport(loc,TeleportCause.PLUGIN);
	}
	public void teleport(Entity entity) {
		teleport(entity,TeleportCause.PLUGIN);
	}
	public void teleport(Location loc,TeleportCause cause) {
		if(loc.getWorld()!=null && loc!=null) {
			if(cause!=null)
		s.teleport(loc,cause);
			else {
				Error.err("teleporting "+s.getName(), "TeleportCause is null");
			}
		}else {
			Error.err("teleporting "+s.getName(), "Location is null");
		}
	}
	public void teleport(Entity entity,TeleportCause cause) {
		if(entity!=null) {
			if(cause!=null)
		s.teleport(entity,cause);
			else {
				Error.err("teleporting "+s.getName(), "TeleportCause is null");
			}
		}else {
			Error.err("teleporting "+s.getName(), "Entity is null");
		}
	}

	public void safeTeleport(Location loc) {
		if(loc.getBlock().getType().name().contains("AIR")||loc.getBlock().getType().name().contains("LAVA")) {
			teleport(searchLocation(loc));
		}else
			teleport(loc); //is safe
	}
	public void safeTeleport(Location loc, TeleportCause cause) {
		if(loc.getBlock().getType().name().contains("AIR")||loc.getBlock().getType().name().contains("LAVA")) {
			teleport(searchLocation(loc),cause);
		}else
			teleport(loc,cause); //is safe
	}
	
	private Location searchLocation(Location loc) {
		return new Location(loc.getWorld(),getX(loc),getY(loc),getZ(loc));
	}
	
	private int getY(Location b) {
				int a=b.getBlockY();
				for(int i = 1; i>0; ++i) {
					 Location l = new Location(b.getWorld(),b.getBlockX(),b.getBlockY()-i,b.getBlockZ());
					 if(!l.getBlock().getType().name().contains("AIR")
							 &&!l.getBlock().getType().name().contains("LAVA")) {
						 a=i;
						 break;
					 }
				}
				return a;
	}
	
	private int getZ(Location b) {
				int a=b.getBlockZ();
				boolean random = Boolean.getBoolean(TheAPI.getRandomFromList(Arrays.asList(true,false)).toString());
				for(int i = 1; i>0; ++i) {
					int x = b.getBlockY();
					if(random)x=x+i;
					else
						x=x-i;
					 Location l = new Location(b.getWorld(),b.getBlockX(),b.getBlockY(),x);
					 if(!l.getBlock().getType().name().contains("AIR")
							 &&!l.getBlock().getType().name().contains("LAVA")) {
						 a=i;
						 break;
					 }
				}
				return a;
	}
	
	private int getX(Location b) {
		int a=b.getBlockX();
		boolean random = Boolean.getBoolean(TheAPI.getRandomFromList(Arrays.asList(true,false)).toString());
		for(int i = 1; i>0; ++i) {
			int x = b.getBlockX();
			if(random)x=x+i;
			else
				x=x-i;
			 Location l = new Location(b.getWorld(),x,b.getBlockY(),b.getBlockZ());
			 if(!l.getBlock().getType().name().contains("AIR")
					 &&!l.getBlock().getType().name().contains("LAVA")) {
				 a=i;
				 break;
			 }
		}
		return a;
	}

	public void setFreeze(boolean freeze) {
		LoaderClass.data.getConfig().set("data."+s.getName()+".freeze",true);
		LoaderClass.data.save();
	}
	public boolean isFreezen() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".freeze");
	}
	
	public static enum InvseeType {
		INVENTORY,
		ENDERCHEST
	}
	public void invsee(Player target, InvseeType type) {
		if(target==null) {
			Error.err("opening inventory to "+s.getName(), "Target is null");
			return;
		}
		if(type==null) {
			Error.err("opening inventory to "+s.getName(), "InvseeType is null");
			return;
		}
		try {
		switch(type) {
		case INVENTORY:
			s.openInventory(target.getInventory()); 
			break;
		case ENDERCHEST:
			s.openInventory(target.getEnderChest());
			break;
		}
		}catch(Exception e) {
			Error.err("opening inventory to "+s.getName(), "Uknown");
		}
	}
	public void msg(String message) {
		try {
		s.sendMessage(TheAPI.colorize(message));
		}catch(Exception e) {
			Error.err("sending message to "+s.getName(), "Message is null");
		}
	}
	@SuppressWarnings("deprecation")
	public void setHealth(double health) {
		try {
		if(0<health) {
			s.setMaxHealth(health);
		}
		s.setHealth(health);
		}catch(Exception e) {
			Error.err("setting health on "+s.getName(), "Health limit reached");
		}
	}
	
	public void setFood(int food) {
		try {
		s.setFoodLevel(food);
		}catch(Exception e) {
			Error.err("setting food on "+s.getName(), "Food limit reached");
		}
	}

	public void setFly(boolean allowFlying, boolean enableFlying) {
		if(allowFlying) {
			LoaderClass.data.getConfig().set("data."+s.getName()+".fly",true);
			LoaderClass.data.save();
		s.setAllowFlight(true);
		s.setFlying(enableFlying);
		}else {
			LoaderClass.data.getConfig().set("data."+s.getName()+".fly",false);
			LoaderClass.data.save();
			s.setFlying(enableFlying);
			s.setAllowFlight(false);
		}
	}
	public boolean allowedFly() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".fly");
	}

	public void takeExp(int exp) {
		int take = (int)s.getExp();
		if(take-exp < 0) {
			s.setExp(take);
		}else
			s.setExp(take-exp);
	}
	
	@SuppressWarnings("deprecation")
	public void resetMaxHealth() {
		s.setMaxHealth(20);
	}

	public void resetExp() {
		s.setExp(0);
	}
	public void resetLevel() {
		s.setLevel(0);
	}
	
	@SuppressWarnings("deprecation")
	public void sendTitle(String firstLine, String nextLine) {
		try {
		s.sendTitle(TheAPI.colorize(firstLine), TheAPI.colorize(nextLine));
		}catch(Exception e) {
			Error.err("sending title to "+s.getName(), "Line is null");
		}
	}
	
	public void giveLevel(int level) {
		s.setLevel(s.getLevel()+level);
	}
	
	public List<Entity> getNearbyEntities(double x, double y, double z){
		return s.getNearbyEntities(x, y, z);
	}

	public void closeOpenInventory() {
		s.getOpenInventory().close();
	}
	
	public String getAddress() {
	try {
		return TheAPI.getPunishmentAPI().getIP(s.getName());
	}catch(Exception e) {
		Error.err("getting ip address of "+s.getName(), "Address is null");
		return null;
	}
	}
	
	public void setScoreboard(Scoreboard sb) {
		try {
		if(sb!=null)
		s.setScoreboard(sb);
		else
			s.setScoreboard(s.getServer().getScoreboardManager().getNewScoreboard());
		}catch(Exception e) {
			Error.err("setting scoreboard on "+s.getName(), "Scoreboard is null");
		}
	}
	/**
	 * int from -10 to 10
	 * @param speed
	 */
	public void setFlySpeed(int speed) {
		if(speed<-10)speed=-10;
		if(speed>10)speed=10;
		s.setFlySpeed(speed/10);
	}

	/**
	 * int from -10 to 10
	 * @param speed
	 */
	public void setWalkSpeed(int speed) {
		if(speed<-10)speed=-10;
		if(speed>10)speed=10;
		s.setWalkSpeed(speed/10);
	}
	public void setMaxAir() {
		 s.setRemainingAir(s.getMaximumAir());
	}
	public void setAir(int air) {
		 s.setRemainingAir(air);
	}
	public void setGodOnTime(int time) {
		 s.setNoDamageTicks(time*20);
	}
	@SuppressWarnings("deprecation")
	public ItemStack getItemInHand() {
		return s.getItemInHand();
	}
	/**
	 * Kick player from serveer with reason
	 */
	public void kick(String reason) {
		if(reason==null)reason="Uknown";
		 s.kickPlayer(TheAPI.colorize(reason));
	}
	
	public void setGod(boolean enable) {
			LoaderClass.data.getConfig().set("data."+s.getName()+".god",enable);
			LoaderClass.data.save();
	}
	public boolean allowedGod() {
		return LoaderClass.data.getConfig().getBoolean("data."+s.getName()+".god");
	}
	
}
