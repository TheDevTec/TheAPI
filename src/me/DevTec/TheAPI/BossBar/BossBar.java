package me.DevTec.TheAPI.BossBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.DataWatcher.DataWatcher;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

/**
 * 1.8 - 1.16+
 */
public class BossBar {
	private static boolean ww = !TheAPI.isOlder1_9();
	private static Class<?> c = Ref.nms("EntityEnderDragon");
	private static Constructor<?> barC= Ref.constructor(Ref.nms("BossBattleServer"), Ref.nms("IChatBaseComponent"),
			Ref.nms("BossBattle$BarColor"), Ref.nms("BossBattle$BarStyle")), tpC=Ref.constructor(Ref.nms("PacketPlayOutEntityTeleport"), int.class, int.class, int.class, int.class,
					byte.class, byte.class, boolean.class),barOld=Ref.constructor(c, Ref.nms("World"));
	private static Method mSend = Ref.method(Ref.nms("BossBattleServer"), "sendUpdate", Ref.nms("PacketPlayOutBoss$Action")),mAdd=Ref.method(Ref.nms("BossBattleServer"), "addPlayer", Ref.nms("EntityPlayer")),idM=Ref.method(c, "getId"),
			mVis=Ref.method(Ref.nms("BossBattleServer"), "setVisible", boolean.class),mLoc=Ref.method(c, "setLocation", double.class, double.class, double.class, float.class, float.class);
	
	private Player p;
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
		Object packet = Ref.newInstance(tpC,
				id, (int) loc.getX() * 32, (int) (loc.getY() - 100) * 32, (int) loc.getZ() * 32,
				(byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
		NMSAPI.sendPacket(p, packet);
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
		NMSAPI.sendPacket(p, NMSAPI.getPacketPlayOutEntityDestroy(id));
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

	private void update(String a) {
		if (!ww || bar == null)return;
		Ref.invoke(bar, mSend, Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutBoss$Action"), a.toUpperCase())));
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
				this.color=color;
				if(color==null)
					color = BarColor.PURPLE;
				if (style == null)style=this.style;
				this.style=style;
				if(style==null)
					style = BarStyle.PROGRESS;
				bar = Ref.newInstance(barC, NMSAPI.getIChatBaseComponentFromCraftBukkit(title), color.toMojang(), style.toMojang());
				Ref.set(bar, "b", (float) progress > -1 ? progress : 0);
				Ref.invoke(bar, mAdd,Ref.player(p));
				update("ADD");
				return;
			}
			if (text != null) {
				Ref.set(bar, "title", NMSAPI.getIChatBaseComponentFromCraftBukkit(title));
				update("UPDATE_NAME");
			}
			if (progress != -1) {
				Ref.set(bar, "b", (float) progress);
				update("UPDATE_PCT");
			}
			if (color != null) {
				Ref.set(bar, "color", color.toMojang());
				update("UPDATE_STYLE");
			}
			if (style != null) {
				Ref.set(bar, "style", style.toMojang());
				update("UPDATE_STYLE");
			}
			return;
		}
		Location loc = p.getLocation();
		Object packet = null;
		boolean cr = false;
		if (bar == null) {
			bar = Ref.newInstance(barOld, Ref.world(loc.getWorld()));
			Ref.invoke(bar,mLoc,loc.getX(), loc.getY() - 100, loc.getZ(), 0, 0);
			id = (int) Ref.invoke(bar, idM);
			packet = NMSAPI.getPacketPlayOutSpawnEntityLiving(bar);
			cr = true;
		}
		DataWatcher watcher = new DataWatcher(bar);
		watcher.set(0, (byte) 0x20);
		if (progress != -1)
			watcher.set(6, (float) (progress * 200) / 100);
		if (text != null) {
			watcher.set(10, text);
			watcher.set(2, text);
		}
		watcher.set(11, (byte) 1);
		watcher.set(3, (byte) 1);
		if (packet == null)
			packet = NMSAPI.getPacketPlayOutEntityMetadata(id, watcher);
		if (cr)
			Ref.set(packet, "l", watcher);
		Ref.sendPacket(p, packet);
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