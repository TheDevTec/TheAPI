package me.devtec.theapi.blocksapi;

import org.bukkit.entity.Player;
import org.spigotmc.AsyncCatcher;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.blocksapi.Schemate.SimpleSave;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Compressors.Decompressor;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.reflections.Ref;

public class Schema {
	private final Schemate schem;
	private final Runnable onFinish;

	public Schema(Runnable onFinish, Schemate schemate) {
		schem = schemate;
		this.onFinish = onFinish;
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
		if (AsyncCatcher.enabled == true)
			AsyncCatcher.enabled = false;
		new Tasker() {
			public void run() {
				Data ca = new Data();
				for (String fs : schem.getData().getKeys()) {
					if (!fs.startsWith("c."))
						continue;
					try {
						Decompressor dec = new Decompressor((byte[]) schem.getData().get(fs));
						fs = fs.replaceFirst(fs.split("\\.")[0], "");
						while (true) {
							String sd = null;
							try {
								sd = dec.readUTF();
							} catch (Exception e) {
								break;
							}
							String[] s = sd.split("/!_!/");
							String[] pp = s[0].split("/");
							Position pos = new Position(position.getWorld(), StringUtils.getInt(pp[0]),
									StringUtils.getInt(pp[1]), StringUtils.getInt(pp[2]));
							if (schem.isSetStandingPosition())
								pos.add(position.getBlockX(), position.getBlockY(), position.getBlockZ());
							SimpleSave save = Schemate.SimpleSave.fromString(s[1]);
							TheMaterial type = new TheMaterial(fs.replaceAll("[0-9]+", ""), StringUtils.getInt(fs));
							if (task != null)
								if (task.set(Schema.this, pos, type, save))
									save.load(pos, type);
								else if (c.set(Schema.this, pos, type, save))
									save.load(pos, type);
							ca.set(pos.getChunkKey() + "", pos.getNMSChunk());
							Object packet = Ref
									.newInstance(
											Ref.constructor(Ref.nms("PacketPlayOutBlockChange"),
													Ref.nms("BlockPosition"), Ref.nms("IBlockData")),
											pos.getBlockPosition(), type.getIBlockData());
							for (Player p : TheAPI.getOnlinePlayers())
								if (p.getWorld().getName().equals(pos.getWorldName()))
									Ref.sendPacket(p, packet);
						}
						dec.close();
					} catch (Exception e) {
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				if (onFinish != null)
					onFinish.run();
			}
		}.runTask();
	}
}
