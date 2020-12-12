package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Json.Reader;

public class ByteLoader extends DataLoader {
	private Map<String, DataHolder> data = new UnsortedMap<>();
	private boolean l;

	@Override
	public Map<String, DataHolder> get() {
		return data;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, DataHolder holder) {
		if (key == null)
			return;
		if (holder == null) {
			data.remove(key);
			return;
		}
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		data.remove(key);
	}

	public void reset() {
		data.clear();
	}

	@Override
	public void load(String input) {
		data.clear();
		try {
			byte[] bb = Base64.getDecoder().decode(input);
			ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
			while (true)
				try {
					String key = bos.readUTF();
					String value = bos.readUTF();
					data.put(key, new DataHolder(Reader.read(value)));
				} catch (Exception e) {
					break;
				}
			if (!data.isEmpty())
				l = true;
		} catch (Exception er) {
			try {
				ByteArrayDataInput bos = ByteStreams.newDataInput(input.getBytes());
				while (true)
					try {
						String key = bos.readUTF();
						String value = bos.readUTF();
						data.put(key, new DataHolder(Reader.read(value)));
					} catch (Exception e) {
						break;
					}
				if (!data.isEmpty())
					l = true;
			} catch (Exception err) {
				String inputF =input.substring(0, input.length()-2);
				try {
					byte[] bb = Base64.getDecoder().decode(inputF);
					ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
					while (true)
						try {
							String key = bos.readUTF();
							String value = bos.readUTF();
							data.put(key, new DataHolder(Reader.read(value)));
						} catch (Exception e) {
							break;
						}
					if (!data.isEmpty())
						l = true;
				} catch (Exception rrr) {
					try {
						ByteArrayDataInput bos = ByteStreams.newDataInput(inputF.getBytes());
						while (true)
							try {
								String key = bos.readUTF();
								String value = bos.readUTF();
								data.put(key, new DataHolder(Reader.read(value)));
							} catch (Exception e) {
								break;
							}
						if (!data.isEmpty())
							l = true;
					} catch (Exception errrr) {
						l = false;
					}
				}
			}
		}
	}

	@Override
	public Collection<String> getHeader() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public Collection<String> getFooter() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public boolean isLoaded() {
		return l;
	}
	
	public String toString() {
		return getDataName();
	}

	@Override
	public String getDataName() {
		return "Data(ByteLoader:" + data.size() + ")";
	}
}
