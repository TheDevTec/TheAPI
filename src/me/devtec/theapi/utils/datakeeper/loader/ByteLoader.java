package me.devtec.theapi.utils.datakeeper.loader;

import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.json.Reader;

public class ByteLoader extends DataLoader {
	private Map<String, Object[]> data = new LinkedHashMap<>();
	private boolean l;

	@Override
	public Map<String, Object[]> get() {
		return data;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, Object[] holder) {
		if (key == null)
			return;
		if (holder == null) {
			remove(key);
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
			byte[] bb = Base64.getDecoder().decode(input.trim());
			ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
			int version = bos.readInt();
			if(version==1) {
				while (true)
					try {
						String key = bos.readUTF();
						String value = bos.readUTF();
						if(!value.equals("null")) {
							value=value.substring(1);
						}else value=null;
						String next;
						boolean run = true;
						while (run)
							try {
								next=bos.readUTF();
								if(next.equals("null")) {
									value+=null;
									continue;
								}
								if(next.equals("0")) {
									run=false;
									continue;
								}else {
									next=next.substring(1);
									value+=next;
								}
							}catch(Exception not) {
								run=false;
							}
						data.put(key, new Object[] {value==null?null:Reader.read(value), null, value,1});
					} catch (Exception e) {
						break;
					}
			}else {
				String key = bos.readUTF();
				while(!key.equals("1"))key = bos.readUTF();
				key = bos.readUTF();
				while (true)
					try {
						String value = bos.readUTF();
						if(!value.equals("null")) {
							value=value.substring(1);
						}else value=null;
						String next;
						boolean run = true;
						while (run)
							try {
								next=bos.readUTF();
								if(next.equals("null")) {
									value+=null;
									continue;
								}
								if(next.equals("0")) {
									run=false;
									continue;
								}else {
									next=next.substring(1);
									value+=next;
								}
							}catch(Exception not) {
								run=false;
							}
						data.put(key, new Object[] {value==null?null:Reader.read(value), null, value,1});
					} catch (Exception e) {
						break;
					}
			}
			if (!data.isEmpty())
				l = true;
		} catch (Exception er) {
			l=false;
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
}
