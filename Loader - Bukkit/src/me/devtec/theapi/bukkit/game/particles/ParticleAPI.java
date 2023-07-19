package me.devtec.theapi.bukkit.game.particles;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.Position;

/**
 * 
 * @author StraikerinaCZ, M3II0
 *
 */
public class ParticleAPI {
	public static void spawnParticle(Player target, Particle particle, Position pos) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos));
	}

	public static void spawnParticle(Player target, Particle particle, Location pos) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(x, y, z));
	}

	public static void spawnParticle(Player target, Particle particle, Position pos, float speed, int amount) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos, speed, amount));
	}

	public static void spawnParticle(Player target, Particle particle, Location pos, float speed, int amount) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(pos, speed, amount));
	}

	public static void spawnParticle(Player target, Particle particle, double x, double y, double z, float speed, int amount) {
		BukkitLoader.getPacketHandler().send(target, particle.createPacket(x, y, z, speed, amount));
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, Location pos) {
		Object packet = particle.createPacket(pos);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z) {
		Object packet = particle.createPacket(x, y, z);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, Position pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, Location pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Player[] target, Particle particle, double x, double y, double z, float speed, int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos) {
		Object packet = particle.createPacket(pos);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Location pos) {
		Object packet = particle.createPacket(pos);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y, double z) {
		Object packet = particle.createPacket(x, y, z);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Position pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, Location pos, float speed, int amount) {
		Object packet = particle.createPacket(pos, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static void spawnParticle(Collection<? extends Player> target, Particle particle, double x, double y, double z, float speed, int amount) {
		Object packet = particle.createPacket(x, y, z, speed, amount);
		for (Player p : target)
			BukkitLoader.getPacketHandler().send(p, packet);
	}

	public static List<Object> createCircle(Location location, Color[] colors, int amount, double size, double radius, double density) {
		if (size == 0)
			size = 1;
		Location clone = location.clone();
		List<Object> frames = new ArrayList<>();
		int colorCounter = 0;
		boolean negative = false;
		for (double t = 0; t <= 2 * Math.PI * radius; t += density) {
			++colorCounter;
			if (colorCounter >= colors.length)
				colorCounter = 0;
			double cos = Math.cos(t);
			double sin = Math.sin(t);
			if (cos < 0 && sin < 0)
				negative = true;
			if (negative && cos < 1 && sin > 0)
				break;
			double x = radius * cos + clone.getX();
			double z = clone.getZ() + radius * sin;
			double y = clone.getY();
			Color color = colors[colorCounter];
			frames.add(new Particle(Ref.isNewerThan(12) ? "DUST" : "REDSTONE", new ParticleData.RedstoneOptions(color.getRed(), color.getGreen(), color.getBlue())).createPacket(x, y, z, 1, amount));
		}
		return frames;
	}

	public static List<Object> createPointingLine(Player player, Location location, Color[] colors, int amount, double size, double length) {
		if (size == 0)
			size = 1;
		Vector vector = player.getEyeLocation().getDirection();
		List<Object> frames = new ArrayList<>();
		double multiply = 1;
		int colorCounter = 0;
		for (int i = 0; i < length * 10; i++) {
			++colorCounter;
			if (colorCounter >= colors.length)
				colorCounter = 0;
			multiply = multiply + 0.1;
			Vector working = vector.clone().multiply(multiply);
			Location export = location.clone().add(working);
			Color color = colors[colorCounter];
			frames.add(new Particle(Ref.isNewerThan(12) ? "DUST" : "REDSTONE", new ParticleData.RedstoneOptions(color.getRed(), color.getGreen(), color.getBlue())).createPacket(export.getX(),
					export.getY(), export.getZ(), 1, amount));
		}
		return frames;
	}

	public static List<Object> createRippleLinePointingAt(Player player, Location destination, Color[] colors, int amount, double size) {
		Location start = player.getEyeLocation();
		Vector eye = start.toVector();
		start = start.add(0, -0.3, 0);
		int difference_x = Math.abs(start.getBlockX() - destination.getBlockX());
		int difference_y = Math.abs(start.getBlockY() - destination.getBlockY());
		int difference_z = Math.abs(start.getBlockZ() - destination.getBlockZ());
		int difference = (difference_x + difference_y + difference_z) * 10;
		Vector direction = destination.add(0, 0.5, 0).toVector().subtract(eye).divide(new Vector(difference, difference, difference));
		int half = difference / 2;
		double random_angle = ThreadLocalRandom.current().nextDouble(0, 0.03);
		double[] angles = quadratic(random_angle, difference / 2);
		int angleCounter = 0;
		boolean minusX = ThreadLocalRandom.current().nextBoolean();
		boolean minusY = ThreadLocalRandom.current().nextBoolean();
		boolean minusZ = ThreadLocalRandom.current().nextBoolean();
		int colorCounter = 0;
		List<Object> animation = new ArrayList<>();
		for (int i = 0; i < difference; i++) {
			if (colorCounter == colors.length)
				colorCounter = 0;
			Color color = colors[colorCounter];
			if (angleCounter == angles.length)
				angleCounter = angles.length - 1;
			if (angleCounter < 0)
				angleCounter = 0;
			double finalAngleX = minusX ? -angles[angleCounter] : angles[angleCounter];
			double finalAngleY = minusY ? -angles[angleCounter] : angles[angleCounter];
			double finalAngleZ = minusZ ? -angles[angleCounter] : angles[angleCounter];
			Location copy = start.clone();
			start = start.add(direction);
			Vector util = copy.add(direction.clone()).toVector().rotateAroundX(finalAngleX).rotateAroundY(finalAngleY).rotateAroundZ(finalAngleZ);
			Location export = util.toLocation(player.getWorld());
			animation.add(new Particle(Ref.isNewerThan(12) ? "DUST" : "REDSTONE", new ParticleData.RedstoneOptions(color.getRed(), color.getGreen(), color.getBlue())).createPacket(export.getX(),
					export.getY(), export.getZ(), 1, amount));
			++colorCounter;
			if (i < half)
				++angleCounter;
			else
				--angleCounter;
		}
		return animation;
	}

	public static List<Object> createComingSpiral(Location location, Color[] colors, int amount, double size, double radius, double change) {
		if (size == 0)
			size = 1;
		int colorCounter = 0;
		List<Object> frames = new ArrayList<>();
		double betterRadius = radius + 0.0;
		for (double t = 0; t <= 2 * Math.PI * radius; t += 0.05) {
			++colorCounter;
			if (colorCounter >= colors.length)
				colorCounter = 0;
			if (betterRadius < 0.0)
				break;
			double x = betterRadius * Math.cos(t) + location.getX();
			double z = location.getZ() + betterRadius * Math.sin(t);
			double y = location.getY();
			Color color = colors[colorCounter];
			frames.add(new Particle(Ref.isNewerThan(12) ? "DUST" : "REDSTONE", new ParticleData.RedstoneOptions(color.getRed(), color.getGreen(), color.getBlue())).createPacket(x, y, z, 1, amount));
			betterRadius = betterRadius - change;
		}
		return frames;
	}

	public static List<List<Object>> createWave(Location location, Color[] colors, int amount, double size, double radius, double density) {
		List<List<Object>> result = new ArrayList<>();
		for (double i = 0; i < radius; i += 0.5)
			result.add(createCircle(location, colors, amount, size, i, density));
		return result;
	}

	public static Color[] createGradient(Color from, Color to, int transition) {
		final double[] red = linear(from.getRed(), to.getRed(), transition);
		final double[] green = linear(from.getGreen(), to.getGreen(), transition);
		final double[] blue = linear(from.getBlue(), to.getBlue(), transition);
		Color[] value = new Color[transition * 2];
		for (int i = 0; i < transition; i++)
			value[i] = new Color((int) red[i], (int) green[i], (int) blue[i]);
		int y_counter = 0;
		for (int i = transition - 1; i > -1; i--) {
			value[transition + y_counter] = value[i];
			++y_counter;
		}
		return value;
	}

	private static double[] linear(double from, double to, int max) {
		final double[] res = new double[max];
		for (int i = 0; i < max; i++)
			res[i] = from + i * ((to - from) / (max - 1));
		return res;
	}

	private static double[] quadratic(double to, int max) {
		final double[] results = new double[max];
		double a = (0 - to) / (max * max);
		double b = -2 * a * max;
		for (int i = 0; i < results.length; i++)
			results[i] = a * i * i + b * i + 0;
		return results;
	}
}
