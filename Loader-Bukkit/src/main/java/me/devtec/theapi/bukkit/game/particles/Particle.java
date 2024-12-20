package me.devtec.theapi.bukkit.game.particles;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;

import lombok.Getter;
import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.Position;
import me.devtec.theapi.bukkit.game.particles.ParticleData.BlockOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.ItemOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.NoteOptions;
import me.devtec.theapi.bukkit.game.particles.ParticleData.RedstoneOptions;

public class Particle {
	public static final Map<String, Object> identifier = new ConcurrentHashMap<>();

	private static final Field particleType;
	private static final Field x;
	private static final Field y;
	private static final Field z;
	private static final Field xDist;
	private static final Field yDist;
	private static final Field zDist;
	private static final Field maxSpeed;
	private static final Field count;
	private static final Field overrideLimiter;
	private static final Field particleOptions;
	private static Constructor<?> paramRed;
	private static Constructor<?> paramDust;
	private static Constructor<?> paramBlock;
	private static Constructor<?> paramItem;
	private static final Constructor<?> vector = Ref.constructor(Ref.getClass(BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "org.joml.Vector3f" : "com.mojang.math.Vector3fa"), float.class, float.class,
			float.class);
	private static final Class<?> particlePacket;

	@Getter
	private final Object particle;
	private final String name;
	private final ParticleData data;

	static {
		particlePacket = Ref.nms("network.protocol.game", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ClientboundLevelParticlesPacket" : "PacketPlayOutWorldParticles");
		if (Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ColorParticleOption" : "ParticleParamRedstone") != null) {
			Constructor<?>[] constructors = Ref.getDeclaredConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ColorParticleOption" : "ParticleParamRedstone"));
			if (constructors.length == 0) {
				constructors = Ref.getConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ColorParticleOption" : "ParticleParamRedstone"));
			}
			Particle.paramRed = constructors[0];
			constructors = Ref.getDeclaredConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "BlockParticleOption" : "ParticleParamBlock"));
			if (constructors.length == 0) {
				constructors = Ref.getConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "BlockParticleOption" : "ParticleParamBlock"));
			}
			Particle.paramBlock = constructors[0];
			constructors = Ref.getDeclaredConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ItemParticleOption" : "ParticleParamItem"));
			if (constructors.length == 0) {
				constructors = Ref.getConstructors(Ref.nms("core.particles", BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "ItemParticleOption" : "ParticleParamItem"));
			}
			Particle.paramItem = constructors[0];
		}
		if (Ref.isNewerThan(16)) {
			Particle.paramDust = Ref.getConstructors(Ref.nms("core.particles", "DustColorTransitionOptions"))[0];
		}
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE) {
			x = Ref.field(particlePacket, "x");
			y = Ref.field(particlePacket, "y");
			z = Ref.field(particlePacket, "z");
			xDist = Ref.field(particlePacket, "xDist");
			yDist = Ref.field(particlePacket, "yDist");
			zDist = Ref.field(particlePacket, "zDist");
			maxSpeed = Ref.field(particlePacket, "maxSpeed");
			count = Ref.field(particlePacket, "count");
			overrideLimiter = Ref.field(particlePacket, "overrideLimiter");
			particleOptions = Ref.field(particlePacket, "particle");
			particleType = null;
		} else if (Ref.isNewerThan(12)) {
			if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 4) {
				x = Ref.field(particlePacket, "b");
				y = Ref.field(particlePacket, "c");
				z = Ref.field(particlePacket, "d");
				xDist = Ref.field(particlePacket, "e");
				yDist = Ref.field(particlePacket, "f");
				zDist = Ref.field(particlePacket, "g");
				maxSpeed = Ref.field(particlePacket, "h");
				count = Ref.field(particlePacket, "i");
				overrideLimiter = Ref.field(particlePacket, "j");
				particleOptions = Ref.field(particlePacket, "k");
			} else {
				x = Ref.field(particlePacket, "a");
				y = Ref.field(particlePacket, "b");
				z = Ref.field(particlePacket, "c");
				xDist = Ref.field(particlePacket, "d");
				yDist = Ref.field(particlePacket, "e");
				zDist = Ref.field(particlePacket, "f");
				maxSpeed = Ref.field(particlePacket, "g");
				count = Ref.field(particlePacket, "h");
				overrideLimiter = Ref.field(particlePacket, "i");
				particleOptions = Ref.field(particlePacket, "j");
			}
			particleType = null;
		} else {
			particleType = Ref.field(particlePacket, "a");
			x = Ref.field(particlePacket, "b");
			y = Ref.field(particlePacket, "c");
			z = Ref.field(particlePacket, "d");
			xDist = Ref.field(particlePacket, "e");
			yDist = Ref.field(particlePacket, "f");
			zDist = Ref.field(particlePacket, "g");
			maxSpeed = Ref.field(particlePacket, "h");
			count = Ref.field(particlePacket, "i");
			overrideLimiter = Ref.field(particlePacket, "j");
			particleOptions = Ref.field(particlePacket, "k");
		}
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
		if (!Ref.isOlderThan(8)) {
			this.particle = Particle.toNMS(particle);
		} else {
			this.particle = name;
		}
		this.data = data;
	}

	public boolean isValid() {
		return particle != null;
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
		Object packet = Ref.newUnsafeInstance(Particle.particlePacket);
		Ref.set(packet, overrideLimiter, true);
		Ref.set(packet, Particle.x, x);
		Ref.set(packet, Particle.y, y);
		Ref.set(packet, Particle.z, z);
		Ref.set(packet, Particle.maxSpeed, speed);
		Ref.set(packet, Particle.count, amount);
		if (Ref.isOlderThan(8)) { // 1.7.10
			if (data != null) {
				if (data instanceof RedstoneOptions || data instanceof NoteOptions) {
					Ref.set(packet, Particle.xDist, data.getValueX());
					Ref.set(packet, Particle.yDist, data.getValueY());
					Ref.set(packet, Particle.zDist, data.getValueZ());
					Ref.set(packet, particleType, name);
				} else {
					int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData() : ((ItemOptions) data).getPacketData();
					Ref.set(packet, particleType, name + "_" + packetData[0] + "_" + packetData[1]);
				}
			} else {
				Ref.set(packet, particleType, name);
			}
		} else if (Ref.isOlderThan(13)) { // 1.8 - 1.12.2
			Ref.set(packet, particleType, particle);
			if (data != null) {
				if (data instanceof NoteOptions || data instanceof RedstoneOptions) {
					Ref.set(packet, Particle.xDist, data.getValueX());
					Ref.set(packet, Particle.yDist, data.getValueY());
					Ref.set(packet, Particle.zDist, data.getValueZ());
				} else {
					int[] packetData = data instanceof BlockOptions ? ((BlockOptions) data).getPacketData() : ((ItemOptions) data).getPacketData();
					Ref.set(packet, Particle.particleOptions,
							"CRACK_ITEM".equalsIgnoreCase(name) || "ITEM_CRACK".equalsIgnoreCase(name) || "ITEM".equalsIgnoreCase(name) || "ITEM_TAKE".equalsIgnoreCase(name) ? packetData
									: new int[] { packetData[0] | packetData[1] << 12 });
				}
			} else {
				Ref.set(packet, Particle.particleOptions, new int[0]);
			}
		} else {

			Object jValue = particle;
			if (data != null) {
				if (data instanceof RedstoneOptions || data instanceof NoteOptions) {
					Ref.set(packet, Particle.xDist, data.getValueX());
					Ref.set(packet, Particle.yDist, data.getValueY());
					Ref.set(packet, Particle.zDist, data.getValueZ());
				}
				if (data instanceof RedstoneOptions) {
					RedstoneOptions d = (RedstoneOptions) data;
					jValue = Ref.isNewerThan(16)
							? "dust_color_transition".equalsIgnoreCase(name)
									? Ref.newInstance(Particle.paramDust, Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()),
											Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize())
									: Ref.newInstance(Particle.paramRed, Ref.newInstance(Particle.vector, d.getValueX(), d.getValueY(), d.getValueZ()), d.getSize())
							: Ref.newInstance(Particle.paramRed, d.getValueX(), d.getValueY(), d.getValueZ(), d.getSize());
				} else if (data instanceof BlockOptions) {
					BlockOptions a = (BlockOptions) data;
					jValue = Ref.newInstance(Particle.paramBlock, particle, a.getType().getIBlockData());
				} else if (data instanceof ItemOptions) {
					ItemOptions a = (ItemOptions) data;
					jValue = Ref.newInstance(Particle.paramItem, particle, BukkitLoader.getNmsProvider().asNMSItem(a.getItem()));
				}
			}
			Ref.set(packet, Particle.particleOptions, jValue);
		}
		return packet;
	}

	@Override
	public int hashCode() {
		int hash = 22;
		hash = hash * 22 + particle.hashCode();
		if (data != null) {
			hash = hash * 22 + data.hashCode();
		}
		return hash * 22 + name.hashCode();
	}
}
