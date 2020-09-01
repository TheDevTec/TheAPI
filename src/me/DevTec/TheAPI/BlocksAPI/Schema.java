package me.DevTec.TheAPI.BlocksAPI;

import org.bukkit.entity.Player;
import org.spigotmc.AsyncCatcher;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.BlocksAPI.Schemate.SimpleSave;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheMaterial;
import me.DevTec.TheAPI.Utils.DataKeeper.Data;
import me.DevTec.TheAPI.Utils.Decompression.Decompression;
import me.DevTec.TheAPI.Utils.Decompression.Decompression.Decompressor;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Schema {
	private final Schemate schem;
	private final Runnable onFinish;
	public Schema(Runnable onFinish, Schemate schemate) {
		schem=schemate;
		this.onFinish=onFinish;
	}
	
	public Schemate getSchemate() {
		return schem;
	}
	
	public String getName() {
		return schem.getName();
	}
	
	private static final SchemBlock c = new SchemBlock() {
		
		@Override
		public boolean set(Schema schem, Position pos, TheMaterial type, SimpleSave save) {
			save.load(pos, type);
			return true;
		}
	};
	
	public void paste(Position position) {
		paste(position, null);
	}
	
	public void paste(Position position, SchemBlock task) {
		if(AsyncCatcher.enabled==true)
			AsyncCatcher.enabled=false;
	    new Tasker() {
			@SuppressWarnings("deprecation")
			public void run() {
				Data ca = new Data();
				for(String fs : schem.getData().getKeys()) {
				if(!fs.startsWith("c."))continue;
				try {
				Decompressor dec = Decompression.getDecompressor((byte[])schem.getData().get(fs));
				fs=fs.replaceFirst(fs.split("\\.")[0], "");
				while(true) {
					String sd=null;
					try {
					sd= dec.readUTF();
					}catch(Exception e) {
						break;
					}
					String[] s = sd.split("/!_!/");
					String[] pp = s[0].split("/");
					Position pos = new Position(position.getWorld(),StringUtils.getInt(pp[0]),StringUtils.getInt(pp[1]),StringUtils.getInt(pp[2]));
					if(schem.isSetStandingPosition())
						pos.add(position.getBlockX(),position.getBlockY(),position.getBlockZ());
					SimpleSave save = Schemate.SimpleSave.fromString(s[1]);
					TheMaterial type = new TheMaterial(fs.replaceAll("[0-9]+", ""), StringUtils.getInt(fs));
					if(task!=null)
						if(task.set(Schema.this, pos, type, save))
					save.load(pos, type);
						else if(c.set(Schema.this, pos, type, save))
						save.load(pos, type);
					ca.set(pos.getChunkKey()+"", pos.getBlockX()+":"+pos.getBlockZ());
				}
				dec.close();
				}catch(Exception e) {}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}}
				for(String o : ca.getKeys()) {
					String[] cw = ca.getString(o).split(":");
					Position pos = new Position(position.getWorld(), StringUtils.getInt(cw[0]),0,StringUtils.getInt(cw[1]));
					pos.getWorld().refreshChunk(pos.getBlockX()>>4, pos.getBlockZ()>>4);
					Object a=Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutMapChunk"),Ref.nms("Chunk"), int.class), pos.getNMSChunk(), 65535);
					if(a==null)a=Ref.newInstance(Ref.constructor(Ref.nms("PacketPlayOutMapChunk"),Ref.nms("Chunk"), boolean.class, int.class), pos.getNMSChunk(), true, 20);
					for(Player p : TheAPI.getOnlinePlayers())
						Ref.sendPacket(p, a);
				}
				if(onFinish!=null)
					onFinish.run();
			}
		}.runAsync();
	}
}
