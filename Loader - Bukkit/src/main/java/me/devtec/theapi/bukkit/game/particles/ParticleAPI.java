package me.devtec.theapi.bukkit.game.particles;

import me.devtec.shared.utility.MathUtils;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.Position;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author StraikerinaCZ, M3II0
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

    public static List<Object> createCircle(Position location, Callable<Particle> particleGetter, int amount, double size, double radius, double density) {
        List<Object> frames = new ArrayList<>();
        boolean negative = false;
        for (double t = 0; t <= 2 * Math.PI * radius; t += density) {
            double cos = Math.cos(t);
            double sin = Math.sin(t);
            if (cos < 0 && sin < 0)
                negative = true;
            if (negative && cos < 1 && sin > 0)
                break;
            double x = radius * cos + location.getX();
            double z = location.getZ() + radius * sin;
            double y = location.getY();
            try {
                frames.add(particleGetter.call().createPacket(x, y, z, 1, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return frames;
    }

    public static List<Object> createCircle(Location location, Callable<Particle> particleGetter, int amount, double size, double radius, double density) {
        List<Object> frames = new ArrayList<>();
        boolean negative = false;
        for (double t = 0; t <= 2 * Math.PI * radius; t += density) {
            double cos = Math.cos(t);
            double sin = Math.sin(t);
            if (cos < 0 && sin < 0)
                negative = true;
            if (negative && cos < 1 && sin > 0)
                break;
            double x = radius * cos + location.getX();
            double z = location.getZ() + radius * sin;
            double y = location.getY();
            try {
                frames.add(particleGetter.call().createPacket(x, y, z, 1, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return frames;
    }

    public static List<Object> createPointingLine(Player player, Location location, Callable<Particle> particleGetter, int amount, double size, double length) {
        Vector vector = player.getEyeLocation().getDirection();
        List<Object> frames = new ArrayList<>();
        double multiply = 1;
        for (int i = 0; i < length * 10; i++) {
            multiply = multiply + 0.1;
            try {
                frames.add(particleGetter.call().createPacket(location.getX() + vector.getX() * multiply, location.getY() + vector.getY() * multiply, location.getZ() + vector.getZ() * multiply, 1,
                        amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return frames;
    }

    public static List<Object> createRippleLinePointingAt(Player player, Location destination, Callable<Particle> particleGetter, int amount, double size) {
        Location start = player.getEyeLocation();
        Vector eye = start.toVector();
        start = start.add(0, -0.3, 0);
        int difference_x = Math.abs(start.getBlockX() - destination.getBlockX());
        int difference_y = Math.abs(start.getBlockY() - destination.getBlockY());
        int difference_z = Math.abs(start.getBlockZ() - destination.getBlockZ());
        int difference = (difference_x + difference_y + difference_z) * 10;
        Vector direction = destination.add(0, 0.5, 0).toVector().subtract(eye).divide(new Vector(difference, difference, difference));
        int half = difference / 2;
        double random_angle = MathUtils.randomDouble(0.03);
        double[] angles = quadratic(random_angle, difference / 2);
        int angleCounter = 0;
        boolean minusX = StringUtils.random.nextBoolean();
        boolean minusY = StringUtils.random.nextBoolean();
        boolean minusZ = StringUtils.random.nextBoolean();
        List<Object> animation = new ArrayList<>();
        for (int i = 0; i < difference; i++) {
            if (angleCounter == angles.length)
                angleCounter = angles.length - 1;
            if (angleCounter < 0)
                angleCounter = 0;
            double finalAngleX = minusX ? -angles[angleCounter] : angles[angleCounter];
            double finalAngleY = minusY ? -angles[angleCounter] : angles[angleCounter];
            double finalAngleZ = minusZ ? -angles[angleCounter] : angles[angleCounter];
            start = start.add(direction);
            Vector util = start.toVector().add(direction).rotateAroundX(finalAngleX).rotateAroundY(finalAngleY).rotateAroundZ(finalAngleZ);
            try {
                animation.add(particleGetter.call().createPacket(util.getX(), util.getY(), util.getZ(), 1, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i < half)
                ++angleCounter;
            else
                --angleCounter;
        }
        return animation;
    }

    public static List<Object> createComingSpiral(Location location, Callable<Particle> particleGetter, int amount, double size, double radius, double change) {
        List<Object> frames = new ArrayList<>();
        double betterRadius = radius + 0.0;
        for (double t = 0; t <= 2 * Math.PI * radius; t += 0.05) {
            if (betterRadius < 0.0)
                break;
            double x = betterRadius * Math.cos(t) + location.getX();
            double z = location.getZ() + betterRadius * Math.sin(t);
            double y = location.getY();
            try {
                frames.add(particleGetter.call().createPacket(x, y, z, 1, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
            betterRadius = betterRadius - change;
        }
        return frames;
    }

    public static List<List<Object>> createWave(Location location, Callable<Particle> particleGetter, int amount, double size, double radius, double density) {
        List<List<Object>> result = new ArrayList<>();
        for (double i = 0; i < radius; i += 0.5)
            result.add(createCircle(location, particleGetter, amount, size, i, density));
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
