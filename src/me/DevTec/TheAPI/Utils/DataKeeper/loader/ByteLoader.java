package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Json.Reader;

public class ByteLoader implements DataLoader {
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
		synchronized (this) {
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
				if(!data.isEmpty())
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
					if(!data.isEmpty())
					l = true;
				} catch (Exception err) {
					l = false;
				}
			}
		}
	}

	@Override
	public List<String> getHeader() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public List<String> getFooter() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public boolean loaded() {
		return l;
	}

	@Override
	public String getDataName() {
		return "Data(ByteLoader:" + data.size() + ")";
	}
}
