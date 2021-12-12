package me.devtec.theapi.bossbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

/**
 * 1.7.10 - 1.8.8
 * 
 * Updated by StraikerinaCZ 12.12. 2021
 */
public class BossBar {
	private static final Class<?> c = Ref.nms("EntityWither");
	private static final Constructor<?> tpC=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityTeleport","PacketPlayOutEntityTeleport"));
	private static final Constructor<?> barOld=Ref.constructor(c, Ref.nmsOrOld("world.level.World","World"));
	private static final Method mLoc=Ref.method(Ref.nmsOrOld("world.entity.Entity","Entity"), "setLocation", double.class, double.class, double.class, float.class, float.class);
	
	private final Player holder;
	private boolean hidden;
	
	private String title;
	private double progress;
	
	private Object entityBar;
	private int entityId;

	public BossBar(Player holder, String text, double progres) {
		this.holder = holder;
		Validator.validate(!TheAPI.isNewerThan(8), "This class is not supported for versions higher than 1.8.8");
		if(!TheAPI.isNewerThan(8))return;
		set(text, progress);
		LoaderClass.plugin.bars.add(this);
	}

	public void move() {
		if (!holder.isOnline())return;
		if (entityBar == null)return;
		Location loc = holder.getLocation();
		Object packet = Ref.newInstance(tpC);
		Ref.set(packet,"a", entityId);
		Ref.set(packet,"b", (int) ((loc.getX()-30) * 32D));
		Ref.set(packet,"c", (int) ((loc.getY()-100) * 32D));
		Ref.set(packet,"d", (int) (loc.getZ() * 32D));
		Ref.set(packet,"e", (byte) 0);
		Ref.set(packet,"f", (byte) 0);
		Ref.sendPacket(holder, packet);
	}

	public boolean isHidden() {
		return hidden;
	}

	public double getProgress() {
		return progress;
	}
	
	public String getTitle() {
		return title;
	}

	public void hide() {
		if (hidden)
			return;
		LoaderClass.plugin.bars.remove(this);
		if (!holder.isOnline())return;
		hidden = true;
		Ref.sendPacket(holder, LoaderClass.nmsProvider.packetEntityDestroy(entityId));
	}

	public void show() {
		if (!hidden)
			return;
		if (!holder.isOnline())return;
		hidden = false;
		Location loc = holder.getLocation();
		Ref.invoke(entityBar,mLoc,loc.getX()-25, loc.getY()-100, loc.getZ(), 0, 0);
		Ref.invoke(entityBar, "setInvisible", true);
		Ref.invoke(entityBar, "setCustomName", this.title);
		Ref.invoke(entityBar, "setHealth", (float)this.progress);
		Ref.sendPacket(holder,LoaderClass.nmsProvider.packetSpawnEntityLiving(entityBar));
		Ref.sendPacket(holder, LoaderClass.nmsProvider.packetEntityMetadata(entityId, LoaderClass.nmsProvider.getDataWatcher(entityBar)));
		LoaderClass.plugin.bars.add(this);
	}

	private void set(String text, double progress) {
		if (!holder.isOnline())return;
		if (progress != -1)
			this.progress = progress;
		if (text != null)
			title = TheAPI.colorize(text);
		boolean cr = false;
		if (entityBar == null) {
			Location loc = holder.getLocation();
			entityBar = Ref.newInstance(barOld, Ref.world(loc.getWorld()));
			Ref.invoke(entityBar,mLoc,loc.getX()-25, loc.getY()-100, loc.getZ(), 0, 0);
			entityId = LoaderClass.nmsProvider.getEntityId(entityBar);
			cr = true;
		}
		Ref.invoke(entityBar, "setInvisible", true);
		Ref.invoke(entityBar, "setCustomName", this.title);
		Ref.invoke(entityBar, "setHealth", (float)this.progress);
		if(cr)
			Ref.sendPacket(holder,LoaderClass.nmsProvider.packetSpawnEntityLiving(entityBar));
		Ref.sendPacket(holder, LoaderClass.nmsProvider.packetEntityMetadata(entityId, LoaderClass.nmsProvider.getDataWatcher(entityBar)));
	}

	public void remove() {
		hide();
		hidden = false;
		entityBar = null;
		entityId = 0;
	}

	public void setTitle(String text) {
		set(text, -1);
	}

	public void setProgress(double progress) {
		set(null, progress);
	}
}