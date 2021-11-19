package me.devtec.theapi.particlesapi;

import java.util.Collection;

import org.bukkit.entity.Player;

import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.reflections.Ref;

public class ParticleAPI {
	public static void spawnParticle(Player target, Particle particle, Position pos) {
		Ref.sendPacket(target, particle.createPacket(pos));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z) {
		Ref.sendPacket(target, particle.createPacket(x, y, z));
	}

	public static void spawnParticle(Player target, Particle particle, Position pos, float speed, int amount) {
		Ref.sendPacket(target, particle.createPacket(pos, speed, amount));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z, float speed,
			int amount) {
		Ref.sendPacket(target, particle.createPacket(x, y, z, speed, amount));
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z) {
		Object packet = particle.createPacket(x, y, z);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z, float speed,
			int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y,
			double z) {
		Object packet = particle.createPacket(x, y, z);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos, float speed,
			int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y,
			double z, float speed, int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for (Player a : target)
			Ref.sendPacket(a, packet);
	}
}
