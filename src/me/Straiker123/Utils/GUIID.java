package me.Straiker123.Utils;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.Straiker123.LoaderClass;

public class GUIID {
	Inventory i;
	public GUIID(Player s) {
		p=s;
		for(int i = 0; i > -1; ++i) {
			if(LoaderClass.data.getConfig().getString("guis."+s.getName()+"."+i)==null) {
				id=i+"";
			break;
			}
		}
	}
	public void setInv(Inventory iv) {
		i=iv;
	}
	
	Player p;
	public Player getPlayer() {
		return p;
	}
	String id = "1";
	public String getID() {
		return id;
	}
	public static enum GRunnable{
		RUNNABLE_ON_INV_CLOSE,
		RUNNABLE,
		RUNNABLE_RIGHT_CLICK,
		RUNNABLE_LEFT_CLICK,
		RUNNABLE_SHIFT_WITH_RIGHT_CLICK,
		RUNNABLE_SHIFT_WITH_LEFT_CLICK,
		RUNNABLE_MIDDLE_CLICK;
	}
	public void setRunnable(GRunnable r, int slot, Runnable e) {
		if(GRunnable.RUNNABLE_ON_INV_CLOSE == r) {
			close=e;
		}else {
		HashMap<Integer, Runnable> d = run.get(r);
		if(d==null)d=new HashMap<Integer, Runnable>();
		d.put(slot, e);
		if(run.get(r)!=null)
		run.replace(r, d);
		else
			run.put(r, d);
	}}

	HashMap<GRunnable, HashMap<Integer,Runnable>> run = new HashMap<GRunnable, HashMap<Integer,Runnable>>();
	Runnable close;
	public Runnable getRunnable(GRunnable r, int slot) {
		if(GRunnable.RUNNABLE_ON_INV_CLOSE == r)
			return close;
		else
			if(run.containsKey(r) && run.get(r).containsKey(slot))
		return run.get(r).get(slot);
		return null;
	}
	public void runRunnable(GRunnable r, int slot) {
		if(GRunnable.RUNNABLE_ON_INV_CLOSE == r) {
			if(close != null)
			close.run();
		}else
		if(run.containsKey(r) && run.get(r).containsKey(slot))run.get(r).get(slot).run();
	}

	public void clear() {
		run.clear();
		close=null;
		LoaderClass.data.getConfig().set("guis."+p.getName()+"."+id, null);
		LoaderClass.data.save();
	}
	public void closeAndClear() {
		clear();
		p.getOpenInventory().close();
	}
	public void close() {
		p.getOpenInventory().close();
	}
}
