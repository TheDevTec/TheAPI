package me.DevTec.TheAPI.BossBar;

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
	private String s;
	private String title;
	private double progress;
	private Object bar;
	private int id;
	private boolean hide;
	private static Class<?> c = Ref.nms("EntityEnderDragon");

	public BossBar(Player holder, String text, double progress, BarColor color, BarStyle style) {
		s = holder.getName();
		set(text, progress, color, style);
		if (TheAPI.isOlder1_9())
			LoaderClass.plugin.bars.add(this);
	}

	public void move() {
		Player p = TheAPI.getPlayer(s);
		if (!p.getName().equals(s))
			return;
		if (!TheAPI.isOlder1_9() || bar == null)
			return;
		Location loc = p.getLocation();
		Object packet = Ref.newInstance(
				Ref.constructor(Ref.nms("PacketPlayOutEntityTeleport"), int.class, int.class, int.class, int.class,
						byte.class, byte.class, boolean.class),
				getId(), (int) loc.getX() * 32, (int) (loc.getY() - 100) * 32, (int) loc.getZ() * 32,
				(byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
		NMSAPI.sendPacket(p, packet);
	}

	public boolean isHidden() {
		return hide;
	}

	public int getId() {
		return id;
	}

	public double getProgress() {
		return progress;
	}

	public String getTitle() {
		return title;
	}

	public void hide() {
		if (hide)
			return;
		hide = true;
		Player p = TheAPI.getPlayer(s);
		if (p==null ||!p.getName().equals(s))
			return;
		if (!TheAPI.isOlder1_9()) {
			Ref.invoke(bar, Ref.method(Ref.nms("BossBattleServer"), "setVisible", boolean.class), false);
			return;
		}
		NMSAPI.sendPacket(p, NMSAPI.getPacketPlayOutEntityDestroy(getId()));
	}

	public void show() {
		if (!hide)
			return;
		hide = false;
		if (!TheAPI.isOlder1_9()) {
			Ref.invoke(bar, Ref.method(Ref.nms("BossBattleServer"), "setVisible", boolean.class), true);
			return;
		}
		set(title, progress, null, null);
	}

	private void update(String a) {
		if (bar == null)
			return;
		Ref.invoke(bar, Ref.method(bar.getClass(), "sendUpdate", Ref.nms("PacketPlayOutBoss$Action")),
				Ref.getNulled(Ref.field(Ref.nms("PacketPlayOutBoss$Action"), a.toUpperCase())));
	}

	private void set(String text, double progress, BarColor color, BarStyle style) {
		Player p = TheAPI.getPlayer(s);
		if (p==null||!p.getName().equals(s))
			return;
		if (progress != -1)
			this.progress = progress;
		if (text != null)
			title = TheAPI.colorize(text);
		if (!TheAPI.isOlder1_9()) {
			if (bar == null) {
				if (color == null)
					color = BarColor.GREEN;
				if (style == null)
					style = BarStyle.NOTCHED_20;
				bar = Ref.newInstance(
						Ref.constructor(Ref.nms("BossBattleServer"), Ref.nms("IChatBaseComponent"),
								Ref.nms("BossBattle$BarColor"), Ref.nms("BossBattle$BarStyle")),
						NMSAPI.getIChatBaseComponentFromCraftBukkit(title), color.toMojang(), style.toMojang());
				Ref.invoke(bar, Ref.method(bar.getClass(), "setProgress", float.class),
						(float) progress != -1 ? progress : 1);
				Ref.invoke(bar, Ref.method(Ref.nms("BossBattleServer"), "addPlayer", Ref.nms("EntityPlayer")),
						Ref.player(p));
				return;
			}
			if (text != null) {
				Ref.set(bar, "title", NMSAPI.getIChatBaseComponentFromCraftBukkit(title));
			}
			if (progress != -1) {
				Ref.invoke(bar, Ref.method(bar.getClass(), "setProgress", float.class), (float) progress);
			}
			if (color != null) {
				Ref.set(bar, "color", color.toMojang());
			}
			if (style != null) {
				Ref.set(bar, "style", style.toMojang());
			}
			update("UPDATE_PROPERTIES");
			update("UPDATE_STYLE");
			update("UPDATE_NAME");
			update("UPDATE_PCT");
			return;
		}

		Location loc = p.getLocation();
		Object packet = null;
		boolean cr = false;
		if (bar == null) {
			bar = Ref.newInstance(Ref.constructor(c, Ref.nms("World")), Ref.world(loc.getWorld()));
			Ref.invoke(bar,
					Ref.method(c, "setLocation", double.class, double.class, double.class, float.class, float.class),
					loc.getX(), loc.getY() - 100, loc.getZ(), 0, 0);
			id = (int) Ref.invoke(bar, Ref.method(c, "getId"));
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
			packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutEntityMetadata"), int.class,
					Ref.nms("DataWatcher"), boolean.class), getId(), watcher.getDataWatcher(), true);
		if (cr)
			Ref.set(packet, "l", watcher);
		NMSAPI.sendPacket(p, packet);
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