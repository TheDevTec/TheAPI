package me.devtec.theapi.bukkit.game.particles;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.game.particles.ParticleData.BlockOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.ItemOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.NoteOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.RedstoneOptions;

public class Particle {
	public static final Map<String, Object> identifier = new ConcurrentHashMap<>();

	private static Constructor<?> paramRed;
	private static Constructor<?> paramDust;
	private static Constructor<?> paramBlock;
	private static Constructor<?> paramItem;
	private static final Constructor<?> vector = Ref.constructor(Ref.getClass("com.mojang.math.Vector3fa"), float.class,
			float.class, float.class);
	private static final Class<?> part;
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));

	private final Object particle;
	private final String name;
	private final ParticleData data;

	static {
		part = Ref.nmsOrOld("network.protocol.game.PacketPlayOutWorldParticles", "PacketPlayOutWorldParticles");
		if (Ref.nmsOrOld("core.particles.ParticleParamRedstone", "ParticleParamRedstone") != null) {
			paramRed = Ref
					.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamRedstone", "ParticleParamRedstone"))[0];
			paramBlock = Ref
					.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamBlock", "ParticleParamBlock"))[0];
			paramItem = Ref.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamItem", "ParticleParamItem"))[0];
		}
		if (Ref.isNewerThan(16))
			paramDust = Ref.getConstructors(
					Ref.nmsOrOld("core.particles.DustColorTransitionOptions", "DustColorTransitionOptions"))[0];
	}

	public static Set<String> getParticles() {
		return identifier.keySet();
	}

	private static Object toNMS(String particle) {
		return identifier.getOrDefault(particle.toLowerCase(), identifier.get("minecraft:" + particle.toLowerCase()));
	}

	public Particle(String particle) {
		this(particle, null);
	}

	public Particle(String particle, ParticleData data) {
		name = particle.toLowerCase();
		if (!Ref.isOlderThan(8))
			this.particle = toNMS(particle);
		else
			this.particle = name;
		this.data = data;
	}

	public boolean isValid() {
		return particle != null;
	}

	public Object getParticle() {
		return particle;
	}

	public String getParticleName() {
		return name;
	}

	public ParticleData getParticleData() {
		return data;
	}

	public Object createPacket(Position pos) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), 1, 1);
	}

	public Object createPacket(double x, double y, double z) {
		return createPacket(x, y, z, 1, 1);
	}

	public Object createPacket(Position pos, float speed, int amount) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), speed, amount);
	}

	public Object createPacket(double x, double y, double z, float speed, int amount) {
		Object packet;
		try {
			packet = unsafe.allocateInstance(part);
		} catch (Exception e) {
			return null;
		}
		if (Ref.isNewerThan(12)) { // 1.13+
			Ref.set(packet, "i", true);
			Ref.set(packet, "a", x);
			Ref.set(packet, "b", y);
			Ref.set(packet, "c", z);
			Ref.set(packet, "g", speed);
			Ref.set(packet, "h", amount);
			if (data != null) {
				if (data instanceof RedstoneOptions) {
					RedstoneOptions d = (RedstoneOptions) data;
					Ref.set(packet, "d", d.getValueX());
					Ref.set(packet, "e", d.getValueY());
					Ref.set(packet, "f", d.getValueZ());
					if (Ref.isNewerThan(16)) {
						if (name.equalsIgnoreCase("dust_color_transition"))
							Ref.set(packet, "j", Ref.newInstance(paramDust,
									Ref.newInstance(vector, d.getValueX(), d.getValueY(), d.getValueZ()),
									Ref.newInstance(vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize()));
						else
							Ref.set(packet, "j", Ref.newInstance(paramRed,
									Ref.newInstance(vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize()));
					} else
						Ref.set(packet, "j",
								Ref.newInstance(paramRed, d.getValueX(), d.getValueY(), d.getValueZ(), d.getSize()));
					return packet;
				}
				if (data instanceof NoteOptions) {
					NoteOptions d = (NoteOptions) data;
					Ref.set(packet, "d", d.getValueX());
					Ref.set(packet, "e", d.getValueY());
					Ref.set(packet, "f", d.getValueZ());
					Ref.set(packet, "j", particle);
					return packet;
				}
				if (data instanceof BlockOptions) {
					BlockOptions a = (BlockOptions) data;
					Ref.set(packet, "j", Ref.newInstance(paramBlock, particle, a.getType().getIBlockData()));
					return packet;
				}
				ItemOptions a = (ItemOptions) data;
				Ref.set(packet, "j",
						Ref.newInstance(paramItem, particle, BukkitLoader.getNmsProvider().asNMSItem(a.getItem())));
				return packet;
			}
			Ref.set(packet, "j", particle);
			return packet;
		}
		if (Ref.isOlderThan(8)) { // 1.7.10
			Ref.set(packet, "a", name);
			Ref.set(packet, "b", (float) x);
			Ref.set(packet, "c", (float) y);
			Ref.set(packet, "d", (float) z);
			Ref.set(packet, "h", speed);
			Ref.set(packet, "i", amount);
			if (data != null) {
				if (data instanceof NoteOptions || data instanceof RedstoneOptions) {
					Ref.set(packet, "e", data instanceof NoteOptions ? ((NoteOptions) data).getValueX()
							: ((RedstoneOptions) data).getValueX());
					Ref.set(packet, "f", data instanceof NoteOptions ? ((NoteOptions) data).getValueY()
							: ((RedstoneOptions) data).getValueY());
					Ref.set(packet, "g", data instanceof NoteOptions ? ((NoteOptions) data).getValueZ()
							: ((RedstoneOptions) data).getValueZ());
				} else {
					int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData()
							: ((ItemOptions) data).getPacketData();
					Ref.set(packet, "a", name + "_" + packetData[0] + "_" + packetData[1]);
				}
			}
			return packet;
		}
		// 1.8 - 1.12.2
		Ref.set(packet, "a", particle);
		Ref.set(packet, "b", (float) x);
		Ref.set(packet, "c", (float) y);
		Ref.set(packet, "d", (float) z);
		Ref.set(packet, "h", speed);
		Ref.set(packet, "i", amount);
		Ref.set(packet, "j", true);
		Ref.set(packet, "k", new int[0]);
		if (data != null) {
			if (data instanceof NoteOptions || data instanceof RedstoneOptions) {
				Ref.set(packet, "e", data instanceof NoteOptions ? ((NoteOptions) data).getValueX()
						: ((RedstoneOptions) data).getValueX());
				Ref.set(packet, "f", data instanceof NoteOptions ? ((NoteOptions) data).getValueY()
						: ((RedstoneOptions) data).getValueY());
				Ref.set(packet, "g", data instanceof NoteOptions ? ((NoteOptions) data).getValueZ()
						: ((RedstoneOptions) data).getValueZ());
			} else {
				int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData()
						: ((ItemOptions) data).getPacketData();
				Ref.set(packet, "k",
						name.equalsIgnoreCase("CRACK_ITEM") || name.equalsIgnoreCase("ITEM_CRACK")
								|| name.equalsIgnoreCase("ITEM") || name.equalsIgnoreCase("ITEM_TAKE") ? packetData
										: new int[] { packetData[0] | (packetData[1] << 12) });
			}
		}
		return packet;
	}
}
