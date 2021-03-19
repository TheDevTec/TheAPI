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
		while(paused);
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		while(paused);
		data.remove(key);
	}

	public void reset() {
		while(paused);
		data.clear();
	}
	
	public boolean isReady() {
		return !paused;
	}
	
	private boolean paused = false;
	public void setReady(boolean val) {
		paused=val;
	}

	@Override
	public void load(String input) {
		data.clear();
		try {
			byte[] bb = Base64.getDecoder().decode(input);
			ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
			bos.readInt();
			while (true)
				try {
					String key = bos.readUTF();
					String value = bos.readUTF().substring(1);
					String next;
					while (true)
						try {
							next=bos.readUTF();
							if(next==null)value+=next;
							else {
								if(next.equals("0"))break;
								next=next.substring(1);
								value+=next;
							}
					}catch(Exception not) {
						break;
					}
					data.put(key, new Object[] {Reader.read(value), null});
				} catch (Exception e) {
					break;
				}
			if (!data.isEmpty())
				l = true;
		} catch (Exception er) {
			String inputF =input.substring(0, input.length()-2);
			try {
				byte[] bb = Base64.getDecoder().decode(inputF);
				ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
				bos.readInt();
				while (true)
					try {
						String key = bos.readUTF();
						String value = bos.readUTF().substring(1);
						String next;
						while (true)
							try {
								next=bos.readUTF();
								if(next==null)value+=next;
								else {
									if(next.equals("0"))break;
									next=next.substring(1);
									value+=next;
								}
						}catch(Exception not) {
							break;
						}
						data.put(key, new Object[] {Reader.read(value), null});
					} catch (Exception e) {
						break;
					}
				if (!data.isEmpty())
					l = true;
			}catch(Exception era) {
				l=false;
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
}
