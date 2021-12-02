package me.devtec.theapi.bossbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

/**
 * 1.8 - 1.17+
 */
public class BossBar {
	private static final boolean ww = !TheAPI.isOlderThan(9);
	private static final Class<?> c = Ref.nms("EntityWither");
	private static final Constructor<?> barC= Ref.constructor(Ref.nmsOrOld("server.level.BossBattleServer","BossBattleServer"), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"),
			Ref.nmsOrOld("world.BossBattle$BarColor","BossBattle$BarColor"), Ref.nmsOrOld("world.BossBattle$BarStyle","BossBattle$BarStyle"));
	private static final Constructor<?> tpC=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutEntityTeleport","PacketPlayOutEntityTeleport"));
	private static final Constructor<?> barOld=Ref.constructor(c, Ref.nmsOrOld("world.level.World","World"));
	private static final Method mSend = Ref.method(Ref.nmsOrOld("server.level.BossBattleServer","BossBattleServer"), "sendUpdate", TheAPI.isNewerThan(16)?Function.class:Ref.nmsOrOld("network.protocol.game.PacketPlayOutBoss$Action","PacketPlayOutBoss$Action"));
	private static final Method mAdd=Ref.method(Ref.nmsOrOld("server.level.BossBattleServer","BossBattleServer"), "addPlayer", Ref.nmsOrOld("server.level.EntityPlayer","EntityPlayer"));
	private static final Method idM=Ref.method(Ref.nmsOrOld("world.entity.Entity","Entity"), "getId");
	private static final Method mVis=Ref.method(Ref.nmsOrOld("server.level.BossBattleServer","BossBattleServer"), "setVisible", boolean.class);
	private static final Method mLoc=Ref.method(Ref.nmsOrOld("world.entity.Entity","Entity"), "setLocation", double.class, double.class, double.class, float.class, float.class);
	
	private final Player p;
	private String title;
	private double progress;
	private Object bar;
	private int id;
	private boolean hide;
	private BarColor color;
	private BarStyle style;

	public BossBar(Player holder, String text, double progress, BarColor color, BarStyle style) {
		p = holder;
		set(text, progress, color, style);
		if (!ww)
			LoaderClass.plugin.bars.add(this);
	}

	public void move() {
		if (!p.isOnline())return;
		if (ww || bar == null)return;
		Location loc = p.getLocation();
		Object packet = Ref.newInstance(tpC);
		Ref.set(packet,"a", id);
		Ref.set(packet,"b", (int) ((loc.getX()-32) * 32D));
		Ref.set(packet,"c", (int) ((loc.getY()-32) * 32D));
		Ref.set(packet,"d", (int) (loc.getZ() * 32D));
		Ref.set(packet,"e", (byte) 0);
		Ref.set(packet,"f", (byte) 0);
		Ref.sendPacket(p, packet);
	}

	public boolean isHidden() {
		return hide;
	}

	public double getProgress() {
		return progress;
	}

	public BarColor getColor() {
		return color;
	}

	public BarStyle getStyle() {
		return style;
	}

	public String getTitle() {
		return title;
	}

	public void hide() {
		if (hide)
			return;
		hide = true;
		if (ww) {
			Ref.invoke(bar, mVis, false);
			return;
		}
		if (!p.isOnline())return;
		Ref.sendPacket(p, LoaderClass.nmsProvider.packetEntityDestroy(id));
	}

	public void show() {
		if (!hide)
			return;
		hide = false;
		if (ww) {
			Ref.invoke(bar, mVis, true);
			return;
		}
		if (!p.isOnline())return;
		set(title, progress, null, null);
	}
	
	private void update(String a, Object... objs) {
		if (!ww || bar == null)return;
		if(TheAPI.isNewerThan(16)){
			if (!p.isOnline())return;
			Ref.sendPacket(p, Ref.invokeStatic(Ref.findMethod(Ref.nmsOrOld("network.protocol.game.PacketPlayOutBoss", null), findCorrect(a)),objs));
			return;
		}
		if(a.equals("UPDATE_PROGRESS"))a="UPDATE_PCT";
		Ref.invoke(bar, mSend, Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutBoss$Action"), a.toUpperCase())));
	}

	private String findCorrect(String a) {
		switch(a) {
		case "ADD":
			return "createAddPacket";
		case "REMOVE":
			return "createRemovePacket";
		case "UPDATE_NAME":
			return "createUpdateNamePacket";
		case "UPDATE_PROGRESS":
			return "createUpdateProgressPacket";
		case "UPDATE_PCT":
			return "createUpdatePropertiesPacket";
		case "UPDATE_STYLE":
			return "createUpdateStylePacket";
		}
		return null;
	}

	private void set(String text, double progress, BarColor color, BarStyle style) {
		if (!p.isOnline())return;
		if (progress != -1)
			this.progress = progress;
		if (text != null)
			title = TheAPI.colorize(text);
		if (ww) {
			if (bar == null) {
				if (color == null)color=this.color;
				if(color==null)
					color = BarColor.PURPLE;
				this.color=color;
				if (style == null)style=this.style;
				if(style==null)
					style = BarStyle.PROGRESS;
				this.style=style;
				bar = Ref.newInstance(barC, ComponentAPI.toIChatBaseComponent(title, true), color.toMojang(), style.toMojang());
				Ref.set(bar, "b", (float) progress > -1 ? progress : 0);
				Ref.invoke(bar, mAdd,Ref.player(p));
				if(TheAPI.isOlderThan(17))
				update("ADD",bar);
				return;
			}
			if (text != null) {
				Ref.set(bar, TheAPI.isNewerThan(16)?"a":"title", ComponentAPI.toIChatBaseComponent(title, true));
				update("UPDATE_NAME",bar);
			}
			if (progress != -1) {
				Ref.set(bar, "b", (float) progress);
				update("UPDATE_PROGRESS",bar);
			}
			if (color != null) {
				Ref.set(bar, TheAPI.isNewerThan(16)?"c":"color", color.toMojang());
				update("UPDATE_PCT",bar);
			}
			if (style != null) {
				Ref.set(bar, TheAPI.isNewerThan(16)?"d":"style", style.toMojang());
				update("UPDATE_STYLE",bar);
			}
			return;
		}
		boolean cr = false;
		if (bar == null) {
			Location loc = p.getLocation();
			bar = Ref.newInstance(barOld, Ref.world(loc.getWorld()));
			Ref.invoke(bar,mLoc,loc.getX()-32, loc.getY()-32, loc.getZ(), 0, 0);
			id = (int) Ref.invoke(bar, idM);
			cr = true;
		}
		Ref.invoke(bar, "setInvisible", true);
		Ref.invoke(bar, "setCustomName", text);
		Ref.invoke(bar, "setHealth", (float)progress);
		if(cr)
			Ref.sendPacket(p,LoaderClass.nmsProvider.packetSpawnEntityLiving(bar));
		else
			Ref.sendPacket(p, LoaderClass.nmsProvider.packetEntityMetadata(id, LoaderClass.nmsProvider.getDataWatcher(bar)));
	}

	public void remove() {
		hide();
		hide = false;
		bar = null;
		id = 0;
	}

	public void setTitle(String text) {
		set(text, -1, null, null);
	}

	public void setProgress(double progress) {
		set(null, progress, null, null);
	}

	public void setColor(BarColor color) {
		set(null, -1, color, null);
	}

	public void setStyle(BarStyle style) {
		set(null, -1, null, style);
	}
}