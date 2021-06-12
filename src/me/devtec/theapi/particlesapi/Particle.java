package me.devtec.theapi.particlesapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Effect.Type;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.particlesapi.ParticleData.BlockOptions;
import me.devtec.theapi.particlesapi.ParticleData.ItemOptions;
import me.devtec.theapi.particlesapi.ParticleData.NoteOptions;
import me.devtec.theapi.particlesapi.ParticleData.RedstoneOptions;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class Particle {
	private static Class<?> a = Ref.nmsOrOld("core.particles.Particles","Particles");
	private static Map<String, Object> identifier = new HashMap<>();
	static {
		if (a == null)
			a = Ref.nms("EnumParticle"); // 1.8 - 1.12.2
		if (a == null) {
			a = Ref.nms("PacketPlayOutWorldParticles$Particle"); // 1.7.10 and older
		}
		if(a!=null) {
			if (a==Ref.nms("EnumParticle")) { // 1.8 - 1.12.2
				for (Object e : a.getEnumConstants())
					identifier.put((String) Ref.invoke(e, "name"), e);
			} else { // 1.13+
				if(TheAPI.isNewerThan(12)&&TheAPI.isOlderThan(14)) { //1.13
					for (Field f : Ref.getFields(a)) {
						Object g = Ref.getStatic(f);
						identifier.put(((String)Ref.invoke(g, "a")).toUpperCase(), g);
					}
				}else //1.7.10 or older or 1.14+
					if(TheAPI.isNewerThan(16)) { //1.17
						Object i = Ref.getStatic(Ref.getClass("net.minecraft.core.IRegistry"),"ab");
						for(Object k : (Set<?>)Ref.invoke(i, "keySet")) {
							identifier.put(Ref.invoke(k, "getKey").toString().toUpperCase(), Ref.invoke(i, "get", k));
						}
					}else
				for (Field f : Ref.getFields(a)) {
					if (f.getName().equals("au"))
						continue;
					identifier.put(f.getName(), TheAPI.isOlderThan(8)?null:Ref.getNulled(f));
				}
			}
		}else { //modded
			for(Effect e : Effect.values()) {
				if(e.getType()==Type.VISUAL) {
					identifier.put(e.name(), null);
				}
			}
			
		}
		
		if(Ref.nmsOrOld("core.particles.ParticleParamRedstone","ParticleParamRedstone")!=null) {
		paramRed = Ref.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamRedstone","ParticleParamRedstone"))[0];
		paramBlock=Ref.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamBlock","ParticleParamBlock"))[0];
		paramItem=Ref.getConstructors(Ref.nmsOrOld("core.particles.ParticleParamItem","ParticleParamItem"))[0];
		}
		part=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutWorldParticles", "PacketPlayOutWorldParticles"));
	}

	private static Object toNMS(String particle) {
		return identifier.getOrDefault(particle, null);
	}

	private Object particle;
	private String name;
	private ParticleData data;

	public Particle(String particle) {
		this(particle, null);
	}

	public Particle(String particle, ParticleData data) {
		name = particle;
		if(!TheAPI.isOlderThan(8))
		this.particle = toNMS(particle);
		else this.particle=name;
		this.data = data;
	}
	
	public boolean isValid() {
		return particle!=null;
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
	
	private static Constructor<?> paramRed, paramBlock, paramItem, part;

	public Object createPacket(double x, double y, double z, float speed, int amount) {
		Object packet = Ref.newInstance(part);
		if (TheAPI.isNewVersion()) { // 1.13+
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
					Ref.set(packet, "j", Ref.newInstance(paramRed,
							d.getValueX(), d.getValueY(), d.getValueZ(), d.getSize()));
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
					Ref.set(packet, "j", Ref.newInstance(paramBlock,
							particle, a.getType().getIBlockData()));
					return packet;
				}
				ItemOptions a = (ItemOptions) data;
				Ref.set(packet, "j", Ref.newInstance(paramItem, particle,
						NMSAPI.asNMSItem(a.getItem())));
				return packet;
			}
			Ref.set(packet, "j", particle);
			return packet;
		}
		if (TheAPI.isOlderThan(8)) { // 1.7.10 and older
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
						name.equalsIgnoreCase("CRACK_ITEM") || name.equalsIgnoreCase("ITEM_CRACK") || name.equalsIgnoreCase("ITEM") || name.equalsIgnoreCase("ITEM_TAKE") ? packetData
								: new int[] { packetData[0] | (packetData[1] << 12) });
			}
		}
		return packet;
	}
}
