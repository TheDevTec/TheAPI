package me.devtec.theapi.bukkit.game.particles;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;

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
			Particle.paramRed = Ref
					.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamRedstone", "ParticleParamRedstone"))[0];
			Particle.paramBlock = Ref
					.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamBlock", "ParticleParamBlock"))[0];
			Particle.paramItem = Ref.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamItem", "ParticleParamItem"))[0];
		}
		if (Ref.isNewerThan(16))
			Particle.paramDust = Ref.getConstructors(
					Ref.nmsOrOld("core.particles.DustColorTransitionOptions", "DustColorTransitionOptions"))[0];
	}

	public static Set<String> getParticles() {
		return Particle.identifier.keySet();
	}

	private static Object toNMS(String particle) {
		return Particle.identifier.getOrDefault(particle.toLowerCase(), Particle.identifier.get("minecraft:" + particle.toLowerCase()));
	}

	public Particle(String particle) {
		this(particle, null);
	}

	public Particle(String particle, ParticleData data) {
		name = particle.toLowerCase();
		if (!Ref.isOlderThan(8))
			this.particle = Particle.toNMS(particle);
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

	public Object createPacket(Location pos) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), 1, 1);
	}

	public Object createPacket(double x, double y, double z) {
		return createPacket(x, y, z, 1, 1);
	}

	public Object createPacket(Position pos, float speed, int amount) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), speed, amount);
	}

	public Object createPacket(Location pos, float speed, int amount) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), speed, amount);
	}

	public Object createPacket(double x, double y, double z, float speed, int amount) {
		Object packet;
		try {
			packet = Particle.unsafe.allocateInstance(Particle.part);
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
			Object jValue = particle;
			if (data != null) {
				if (data instanceof RedstoneOptions || data instanceof NoteOptions) {
					Ref.set(packet, "d", data.getValueX());
					Ref.set(packet, "e", data.getValueY());
					Ref.set(packet, "f", data.getValueZ());
				}
				if (data instanceof RedstoneOptions) {
					RedstoneOptions d = (RedstoneOptions) data;
					jValue=Ref.isNewerThan(16)?name.equalsIgnoreCase("dust_color_transition")?Ref.newInstance(Particle.paramDust, Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()), Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize()):Ref.newInstance(Particle.paramRed, Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize()):Ref.newInstance(Particle.paramRed, d.getValueX(), d.getValueY(), d.getValueZ(), d.getSize());
				}else
					if (data instanceof BlockOptions) {
						BlockOptions a = (BlockOptions) data;
						jValue=Ref.newInstance(Particle.paramBlock, particle, a.getType().getIBlockData());
					}else
						if (data instanceof ItemOptions) {
							ItemOptions a = (ItemOptions) data;
							jValue=Ref.newInstance(Particle.paramItem, particle, BukkitLoader.getNmsProvider().asNMSItem(a.getItem()));
						}
			}
			Ref.set(packet, "j", jValue);
			return packet;
		}
		Ref.set(packet, "b", (float) x);
		Ref.set(packet, "c", (float) y);
		Ref.set(packet, "d", (float) z);
		Ref.set(packet, "h", speed);
		Ref.set(packet, "i", amount);
		if (Ref.isOlderThan(8)) { // 1.7.10
			Ref.set(packet, "a", name);
			if (data != null)
				if (data instanceof NoteOptions || data instanceof RedstoneOptions) {
					Ref.set(packet, "e", data.getValueX());
					Ref.set(packet, "f", data.getValueY());
					Ref.set(packet, "g", data.getValueZ());
				} else {
					int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData() : ((ItemOptions) data).getPacketData();
					Ref.set(packet, "a", name + "_" + packetData[0] + "_" + packetData[1]);
				}
			return packet;
		}
		// 1.8 - 1.12.2
		Ref.set(packet, "a", particle);
		Ref.set(packet, "j", true);
		if (data != null) {
			if (data instanceof NoteOptions || data instanceof RedstoneOptions) {
				Ref.set(packet, "e", data.getValueX());
				Ref.set(packet, "f", data.getValueY());
				Ref.set(packet, "g", data.getValueZ());
			} else {
				int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData() : ((ItemOptions) data).getPacketData();
				Ref.set(packet, "k", name.equalsIgnoreCase("CRACK_ITEM") || name.equalsIgnoreCase("ITEM_CRACK")
						|| name.equalsIgnoreCase("ITEM") || name.equalsIgnoreCase("ITEM_TAKE") ? packetData
								: new int[] { packetData[0] | packetData[1] << 12 });
			}
		}else
			Ref.set(packet, "k", new int[0]);
		return packet;
	}
}
