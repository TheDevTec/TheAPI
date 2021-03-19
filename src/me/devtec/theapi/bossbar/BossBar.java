package me.devtec.theapi.bossbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.ChatMessage;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

/**
 * 1.8 - 1.16+
 */
public class BossBar {
	private static boolean ww = !TheAPI.isOlderThan(9);
	private static Class<?> c = Ref.nms("EntityWither");
	private static Constructor<?> barC= Ref.constructor(Ref.nms("BossBattleServer"), Ref.nms("IChatBaseComponent"),
			Ref.nms("BossBattle$BarColor"), Ref.nms("BossBattle$BarStyle")), tpC=Ref.constructor(Ref.nms("PacketPlayOutEntityTeleport")),barOld=Ref.constructor(c, Ref.nms("World"));
	private static Method mSend = Ref.method(Ref.nms("BossBattleServer"), "sendUpdate", Ref.nms("PacketPlayOutBoss$Action")),mAdd=Ref.method(Ref.nms("BossBattleServer"), "addPlayer", Ref.nms("EntityPlayer")),idM=Ref.method(Ref.nms("Entity"), "getId"),
			mVis=Ref.method(Ref.nms("BossBattleServer"), "setVisible", boolean.class),mLoc=Ref.method(Ref.nms("Entity"), "setLocation", double.class, double.class, double.class, float.class, float.class);
	
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
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutEntityDestroy(id));
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
				bar = Ref.newInstance(barC, NMSAPI.getIChatBaseComponentJson(new ChatMessage(title).getJson()), color.toMojang(), style.toMojang());
				Ref.set(bar, "b", (float) progress > -1 ? progress : 0);
				Ref.invoke(bar, mAdd,Ref.player(p));
				update("ADD");
				return;
			}
			if (text != null) {
				Ref.set(bar, "title", NMSAPI.getIChatBaseComponentJson(new ChatMessage(title).getJson()));
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
			Ref.sendPacket(p, NMSAPI.getPacketPlayOutSpawnEntityLiving(bar));
		else
			Ref.sendPacket(p, NMSAPI.getPacketPlayOutEntityMetadata(id, Ref.get(bar, "datawatcher")));
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