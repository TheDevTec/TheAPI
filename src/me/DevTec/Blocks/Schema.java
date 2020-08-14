package me.DevTec.Blocks;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spigotmc.AsyncCatcher;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.DevTec.ConfigAPI;
import me.DevTec.TheAPI;
import me.DevTec.Blocks.Schemate.SimpleSave;
import me.DevTec.Other.Decompression;
import me.DevTec.Other.Position;
import me.DevTec.Other.Ref;
import me.DevTec.Other.StringUtils;
import me.DevTec.Other.TheMaterial;
import me.DevTec.Scheduler.Tasker;

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
				ConfigAPI ca = null;
				for(int iaa = 0; iaa > -1; ++iaa) {
					if(!new File("TheAPI/ChunkTask/"+iaa).exists()) {
						ca = new ConfigAPI("TheAPI/ChunkTask", ""+iaa);
						ca.create();
						break;
					}
				}
				StringUtils u = TheAPI.getStringUtils();
				for(String fs : schem.getFile().getSection("c").getKeys()) {
				ByteArrayDataInput in = ByteStreams.newDataInput(Decompression.decompress(Base64Coder.decodeLines(schem.getFile().getString("c."+fs))));
				int x=Integer.MIN_VALUE, z=Integer.MIN_VALUE;
				while(true) {
					String sd=null;
					try {
					sd= in.readUTF();
					}catch(Exception e) {
						break;
					}
					String[] s = sd.split("/!_!/");
					String[] pp = s[0].split("/");
					Position pos = new Position(position.getWorld(),u.getInt(pp[0]),u.getInt(pp[1]),u.getInt(pp[2]));
					if(schem.isSetStandingPosition())
						pos.add(position.getBlockX(),position.getBlockY(),position.getBlockZ());
					SimpleSave save = Schemate.SimpleSave.fromString(s[1]);
					TheMaterial type = new TheMaterial(Material.getMaterial(fs.replaceAll("[0-9]+", "")), u.getInt(fs));
					if(task!=null)
						if(task.set(Schema.this, pos, type, save))
					save.load(pos, type);
						else if(c.set(Schema.this, pos, type, save))
						save.load(pos, type);
					x=pos.getBlockX();
					z=pos.getBlockZ();
				}
				if(x!=Integer.MIN_VALUE)
				ca.set(fs+"", x+":"+z);
				}
				ca.save();
				for(String o : ca.getKeys(false)) {
					String[] cw = ca.getString(o).split(":");
					Position pos = new Position(position.getWorld(), TheAPI.getStringUtils().getInt(cw[0]),0,TheAPI.getStringUtils().getInt(cw[1]));
					pos.getWorld().refreshChunk(pos.getBlockX()>>4, pos.getBlockZ()>>4);
					Object a=Ref.newInstanceNms("PacketPlayOutMapChunk", pos.getNMSChunk(), 65535);
					if(a==null)a=Ref.newInstanceNms("PacketPlayOutMapChunk", pos.getNMSChunk(), true, 20);
					for(Player p : Bukkit.getOnlinePlayers())
						Ref.sendPacket(p, a);
				}
				ca.delete();
				if(onFinish!=null)
					onFinish.run();
			}
		}.runAsync();
	}
}
