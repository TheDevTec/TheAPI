package me.DevTec.TheAPI.ParticlesAPI;

import java.lang.reflect.Field;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.ParticlesAPI.ParticleData.BlockOptions;
import me.DevTec.TheAPI.ParticlesAPI.ParticleData.ItemOptions;
import me.DevTec.TheAPI.ParticlesAPI.ParticleData.NoteOptions;
import me.DevTec.TheAPI.ParticlesAPI.ParticleData.RedstoneOptions;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.TheMaterial;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Particle {
	private static Class<?> a = Ref.nms("Particles");
	private static UnsortedMap<String, Object> identifier = new UnsortedMap<>();
	static {
		if(a==null)a = Ref.nms("EnumParticle"); //1.8 - 1.12.2 
		if(a==null)a = Ref.nms("PacketPlayOutWorldParticles$Particle"); //1.7.10 and older
		if(a.isEnum()) {
		for(Object e : a.getEnumConstants())
		identifier.put((String)Ref.invoke(e,"name"), e);
		}else { //1.13+
			for(Field f : Ref.getFields(a)) {
				if(f.getName().equals("au"))continue;
				identifier.put(f.getName(), Ref.getNulled(f));
			}
		}
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
		name=particle;
		this.particle=toNMS(particle);
		this.data=data;
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
		return createPacket(x, y, z, 0, 1);
	}
	
	public Object createPacket(Position pos, float speed, int amount) {
		return createPacket(pos.getX(), pos.getY(), pos.getZ(), speed, amount);
	}
	
	public Object createPacket(double x, double y, double z, float speed, int amount) {
		Object packet = Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutWorldParticles")));
		if(TheAPI.isNewVersion()) { //1.13+
			Ref.set(packet, "i", true);
			Ref.set(packet, "a", x);
			Ref.set(packet, "b", y);
			Ref.set(packet, "c", z);
			Ref.set(packet, "g", speed);
			Ref.set(packet, "h", amount);
			if(data!=null) {
				if(data instanceof RedstoneOptions) {
					RedstoneOptions d = (RedstoneOptions)data;
					Ref.set(packet, "d", d.getValueX());
					Ref.set(packet, "e", d.getValueY());
					Ref.set(packet, "f", d.getValueZ());
					Ref.set(packet, "j", Ref.newInstance(Ref.getConstructors(Ref.nms("ParticleParamRedstone"))[0], d.getValueX(), d.getValueY(), d.getValueZ(), d.getSize()));
					return packet;
				}
				if(data instanceof NoteOptions) {
					NoteOptions d = (NoteOptions)data;
					Ref.set(packet, "d", d.getValueX());
					Ref.set(packet, "e", d.getValueY());
					Ref.set(packet, "f", d.getValueZ());
					Ref.set(packet, "j", particle);
					return packet;
				}
				if(data instanceof BlockOptions) {
					BlockOptions a = (BlockOptions)data;
					Ref.set(packet, "j", Ref.newInstance(Ref.getConstructors(Ref.nms("ParticleParamBlock"))[0], particle, a.getType().getIBlockData()));
					return packet;
				}
				ItemOptions a = (ItemOptions)data;
				Ref.set(packet, "j", Ref.newInstance(Ref.getConstructors(Ref.nms("ParticleParamItem"))[0], particle, new TheMaterial(a.getItem()).toNMSItemStack()));
				return packet;
			}
			Ref.set(packet, "j", particle);
			return packet;
		}
		if(TheAPI.isOlderThan(8)) { //1.7.10 and older
			Ref.set(packet, "a", name);
			Ref.set(packet, "b", (float)x);
			Ref.set(packet, "c", (float)y);
			Ref.set(packet, "d", (float)z);
			Ref.set(packet, "h", speed);
			Ref.set(packet, "i", amount);
			if(data!=null) {
				if(data instanceof NoteOptions || data instanceof RedstoneOptions) {
					Ref.set(packet, "e", data instanceof NoteOptions ? ((NoteOptions)data).getValueX() : ((RedstoneOptions)data).getValueX());
					Ref.set(packet, "f", data instanceof NoteOptions ? ((NoteOptions)data).getValueY() : ((RedstoneOptions)data).getValueY());
					Ref.set(packet, "g", data instanceof NoteOptions ? ((NoteOptions)data).getValueZ() : ((RedstoneOptions)data).getValueZ());
					Ref.set(packet, "i", 0);
				}else {
					int[] packetData = data instanceof BlockOptions ? ((BlockOptions)data).getPacketData() : ((ItemOptions)data).getPacketData();
					Ref.set(packet, "a", name+"_" + packetData[0] + "_" + packetData[1]);
				}
			}
			return packet;
		}
		//1.8 - 1.12.2
		Ref.set(packet, "a", particle);
		Ref.set(packet, "b", (float)x);
		Ref.set(packet, "c", (float)y);
		Ref.set(packet, "d", (float)z);
		Ref.set(packet, "h", speed);
		Ref.set(packet, "i", amount);
		Ref.set(packet, "j", true);
		Ref.set(packet, "k", new int[0]);
		if(data!=null) {
			if(data instanceof NoteOptions || data instanceof RedstoneOptions) {
				Ref.set(packet, "e", data instanceof NoteOptions ? ((NoteOptions)data).getValueX() : ((RedstoneOptions)data).getValueX());
				Ref.set(packet, "f", data instanceof NoteOptions ? ((NoteOptions)data).getValueY() : ((RedstoneOptions)data).getValueY());
				Ref.set(packet, "g", data instanceof NoteOptions ? ((NoteOptions)data).getValueZ() : ((RedstoneOptions)data).getValueZ());
				Ref.set(packet, "i", 0);
			}else {
				int[] packetData = data instanceof BlockOptions ? ((BlockOptions)data).getPacketData() : ((ItemOptions)data).getPacketData();
				Ref.set(packet, "k", name.equalsIgnoreCase("ITEM_CRACK") || name.equalsIgnoreCase("ITEM_TAKE") ? packetData : new int[] { packetData[0] | (packetData[1] << 12) });
			}
		}
		return packet;
	}
}
