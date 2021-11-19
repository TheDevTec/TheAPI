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
import me.devtec.theapi.utils.json.Json;

public class SchematicData extends Data {
	
	public SchematicData() {
		loader = new EmptyLoader();
		keys = new LinkedList<>();
	}
	
	
	public SchematicData(String filePath) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), true);
	}
	
	public SchematicData(String filePath, boolean load) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), load);
	}
	
	public SchematicData(File file) {
		this(file, true);
	}
	
	public SchematicData(File file, boolean load) {
		this.file = file;
		keys = new LinkedList<>();
		if (load)
			reload(file);
	}
	
	// CLONE
	public SchematicData(SchematicData data) {
		file = data.file;
		keys = data.keys;
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
		keys.clear();
		loader = new SchematicLoader();
		loader.load(input);
		for (String k : loader.getKeys())
			if (!keys.contains(k.split("\\.")[0]))
				keys.add(k.split("\\.")[0]);
		return this;
	}
	
	
	@Override
	public void save() {
		if(isSaving)return;
		isSaving=true;
		if(!requireSave)return;
		requireSave=false;
		if (file == null) {
			isSaving=false;
			return;
		}
		try {
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
		ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
		bos.writeInt(2);
		bos.writeUTF("1");
		for (Entry<String, Object[]> key : loader.get().entrySet())
			try {
				bos.writeUTF(key.getKey());
				if(key.getValue()[0]==null) {
					bos.writeUTF("null");
					bos.writeUTF("0");
				}else {
					if(key.getValue().length >2)
						if(key.getValue()[2]!=null && key.getValue()[2] instanceof String) {
							String write = (String)key.getValue()[2];
							if(write==null) {
								bos.writeUTF("null");
								bos.writeUTF("0");
								continue;
							}
							while(write.length()>40000) {
								String wr = write.substring(0, 39999);
								bos.writeUTF("0"+wr);
								write=write.substring(39999);
							}
							bos.writeUTF("0"+write);
							bos.writeUTF("0");
							continue;
						}
					Object val = key.getValue()[0];
					String write = val instanceof String ? (String)val:Json.writer().write(val);
					if(write==null) {
						bos.writeUTF("null");
						bos.writeUTF("0");
						continue;
					}
					while(write.length()>40000) {
						String wr = write.substring(0, 39999);
						bos.writeUTF("0"+wr);
						write=write.substring(39999);
					}
					bos.writeUTF("0"+write);
					bos.writeUTF("0");
					continue;
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
			w.write(Base64.getEncoder().encodeToString(Compressors.compress(bos.toByteArray())));
			w.close();
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
			bos.writeInt(2);
			bos.writeUTF("1");
			for (Entry<String, Object[]> key : loader.get().entrySet())
				try {
					bos.writeUTF(key.getKey());
					if(key.getValue()[0]==null) {
						bos.writeUTF("null");
						bos.writeUTF("0");
					}else {
						if(key.getValue().length >2)
							if(key.getValue()[2]!=null && key.getValue()[2] instanceof String) {
								String write = (String)key.getValue()[2];
								if(write==null) {
									bos.writeUTF("null");
									bos.writeUTF("0");
									continue;
								}
								while(write.length()>40000) {
									String wr = write.substring(0, 39999);
									bos.writeUTF("0"+wr);
									write=write.substring(39999);
								}
								bos.writeUTF("0"+write);
								bos.writeUTF("0");
								continue;
							}
						Object val = key.getValue()[0];
						String write = val instanceof String ? (String)val:Json.writer().write(val);
						if(write==null) {
							bos.writeUTF("null");
							bos.writeUTF("0");
							continue;
						}
						while(write.length()>40000) {
							String wr = write.substring(0, 39999);
							bos.writeUTF("0"+wr);
							write=write.substring(39999);
						}
						bos.writeUTF("0"+write);
						bos.writeUTF("0");
						continue;
					}
				} catch (Exception er) {
					er.printStackTrace();
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