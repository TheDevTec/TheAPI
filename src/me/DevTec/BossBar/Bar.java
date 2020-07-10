package me.DevTec.BossBar;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.DevTec.TheAPI;
import me.DevTec.NMS.Reflections;
 
public class Bar {
	private Player p;
	private Object bar;
	private String name;
	private float progress;
    private boolean set, hide;
	public Bar(Player holder) {
		p=holder;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public String getTitle() {
		return name;
	}
    
    private void spawn() {
    	if(bar!=null)
    		remove();
        Location loc = p.getLocation();
        Object world =TheAPI.getNMSAPI().getWorld(p.getWorld());
        Class<?> d = Reflections.getNMSClass("EntityEnderDragon");
        bar = Reflections.c(Reflections.getConstructor(d, Reflections.getNMSClass("World")),world);
        Reflections.invoke(bar, Reflections.getMethod(d, "setLocation", double.class, double.class, double.class, float.class, float.class)
            , loc.getX(), loc.getY() - 100, loc.getZ(), 0, 0);
        Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving"),Reflections.getNMSClass("EntityLiving")),bar);
        Object watcher = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("DataWatcher"),Reflections.getNMSClass("Entity")),bar);
        Method a = Reflections.getMethod(Reflections.getNMSClass("DataWatcher"),"a", int.class, Object.class);
        Reflections.invoke(watcher, a, 0, (byte) 0x20);
        Reflections.invoke(watcher, a, 10, "");
        Reflections.invoke(watcher, a, 2, "");
        Reflections.invoke(watcher, a, 11, (byte) 1);
        Reflections.invoke(watcher, a, 3, (byte) 1);
        float toset = 0.0F;
        if (!set) {
        	set=true;
            toset = (float)Reflections.invoke(bar, Reflections.getMethod(d,"getMaxHealth"));
        }else
            toset = progress;
        Reflections.invoke(bar, Reflections.getMethod(d,"setHealth", float.class),toset);
        Reflections.invoke(watcher, a, 6, toset);
        Reflections.setField(packet,"l", watcher);
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void setProgress(double progress) {
    	if(bar==null)spawn();
        float hp = (float)progress *(float)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getMaxHealth"));
        Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"setHealth", float.class), hp);
        this.progress=hp;
        int id = (int)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getId"));
        Object watcher = Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getDataWatcher"));
        Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityMetadata"),int.class, watcher.getClass(), boolean.class),id,watcher,true);
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void setName(String name) {
    	if(bar==null)spawn();
    	this.name=name;
    	if(Reflections.getMethod(bar.getClass(),"setCustomName", String.class)!=null)
            Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"setCustomName", String.class), name);
        Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"setCustomName", Reflections.getNMSClass("IChatBaseComponent")), TheAPI.getNMSAPI().getIChatBaseComponentText(name));
            int id = (int)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getId"));
            Object watcher = Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getDataWatcher"));
            Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityMetadata"),int.class, watcher.getClass(), boolean.class),id,watcher,true);
            TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void remove() {
    	if(bar==null)return;
        int id = (int)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getId"));
        Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityDestroy"),int[].class),new int[]{id});
        bar=null;
    	set=false;
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void hide() {
    	if(bar==null)spawn();
        int id = (int)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getId"));
        Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityDestroy"),int[].class),new int[]{id});
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void show() {
    	if(bar==null)spawn();
    	Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving"),Reflections.getNMSClass("EntityLiving")),bar);
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
 
    public void teleport() {
    	if(bar==null)spawn();
        Location loc = p.getLocation();
        Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutEntityTeleport"),int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class)
        		,(int)Reflections.invoke(bar, Reflections.getMethod(bar.getClass(),"getId")),
                (int) loc.getX() * 32, (int) (loc.getY() + 600) * 32, (int) loc.getZ() * 32,
                (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
        TheAPI.getNMSAPI().sendPacket(p, packet);
    }
    
    public boolean hidden() {
    	return hide;
    }
 
    public boolean has() {
    	if(bar==null)return false;
        if (p == null || !p.isOnline())return false;
        return true;
    }
}