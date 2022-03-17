package me.devtec.theapi.game.particles;

import java.util.Collection;

import org.bukkit.entity.Player;

import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.game.Position;

public class ParticleAPI {
	public static void spawnParticle(Player target, Particle particle, Position pos) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(x, y, z));
	}

	public static void spawnParticle(Player target, Particle particle, Position pos, float speed, int amount) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos, speed, amount));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z, float speed,
			int amount) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(x, y, z, speed, amount));
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z) {
		Object packet = particle.createPacket(x, y, z);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z, float speed,
			int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y,
			double z) {
		Object packet = particle.createPacket(x, y, z);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos, float speed,
			int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y,
			double z, float speed, int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for(Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}
}
