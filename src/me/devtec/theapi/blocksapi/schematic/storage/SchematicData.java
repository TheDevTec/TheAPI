package me.devtec.theapi.blocksapi.schematic.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.Compressors;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.datakeeper.loader.EmptyLoader;
import me.devtec.theapi.utils.json.Writer;

public class SchematicData extends Data {
	
	public SchematicData() {
		loader = new EmptyLoader();
		aw = new LinkedList<>();
	}
	
	
	public SchematicData(String filePath) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), true);
	}
	
	public SchematicData(String filePath, boolean load) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), load);
	}
	
	public SchematicData(File f) {
		this(f, true);
	}
	
	public SchematicData(File f, boolean load) {
		a = f;
		aw = new LinkedList<>();
		if (load)
			reload(a);
	}
	
	// CLONE
	public SchematicData(SchematicData data) {
		a = data.a;
		aw = data.aw;
		loader=data.loader;
	}

	public boolean exists(String path) {
		int a = 0;
		for (String k : loader.get().keySet()) {
			if (k.indexOf(path)==0) {
				a = 1;
				break;
			}
		}
		return a == 1;
	}

	public SchematicData reload(File f) {
		if (!f.exists()) {
			try {
				if(f.getParentFile()!=null)
					f.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		return reload(StreamUtils.fromStream(f));
	}

	public SchematicData reload(String input) {
		requireSave=true;
		aw.clear();
		loader = new SchematicLoader();
		loader.load(input);
		for (String k : loader.getKeys())
			if (!aw.contains(k.split("\\.")[0]))
				aw.add(k.split("\\.")[0]);
		return this;
	}
	
	
	@Override
	public void save() {
		if(isSaving)return;
		isSaving=true;
		if(!requireSave)return;
		requireSave=false;
		if (a == null)
			return;
		try {
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(a), StandardCharsets.UTF_8);
		try {
			ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
			for (Entry<String, Object[]> key : loader.get().entrySet())
				try {
					bos.writeUTF(key.getKey());
					if(key.getValue()[0]==null) {
						bos.writeUTF(null);
					}else {
						String write = Writer.write(key.getValue()[0]);
						while(write.length()>50000) {
							String wr = write.substring(0, 49999);
							bos.writeUTF('1'+wr);
							write=write.substring(49999);
						}
						bos.writeUTF('1'+write);
					}
					bos.writeUTF("1");
				} catch (Exception er) {
				}
			w.write(Base64.getEncoder().encodeToString(Compressors.compress(bos.toByteArray())));
			w.close();
		} catch (Exception e) {
			w.close();
		}
		}catch(Exception er) {}
		isSaving=false;
	}

	@Override
	public SchematicData save(DataType type) {
		save();
		return this;
	}
	
	public String toString() {
		try {
			ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
			for (Entry<String, Object[]> key : loader.get().entrySet())
				try {
					bos.writeUTF(key.getKey());
					if(key.getValue()[0]==null) {
						bos.writeUTF(null);
					}else {
						String write = Writer.write(key.getValue()[0]);
						while(write.length()>50000) {
							String wr = write.substring(0, 49999);
							bos.writeUTF('1'+wr);
							write=write.substring(49999);
						}
						bos.writeUTF('1'+write);
					}
					bos.writeUTF("1");
				} catch (Exception er) {
				}
			return Base64.getEncoder().encodeToString(Compressors.compress(bos.toByteArray()));
		} catch (Exception e) {
		}
		return "";
	}

	public String toString(DataType type) {
		return toString();
	}
}