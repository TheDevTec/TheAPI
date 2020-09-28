package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;

public class ByteLoader implements DataLoader {
	private MultiMap<String, String, DataHolder> data = new MultiMap<>();
	private boolean l;
	@Override
	public MultiMap<String, String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		try {
			byte[] bb = Base64.getDecoder().decode(input);
			ObjectInputStream ousr = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(bb)));
			while(true)
				try {
					String key = ousr.readUTF();
					data.put(key.split("\\.")[0], key, new DataHolder(Maker.objectFromJson(ousr.readUTF())));
				}catch(Exception e) {
				break;
				}
			ousr.close();
			l=true;
		}catch(Exception er) {
			l=false;
		}
	}

	@Override
	public List<String> getHeader() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public List<String> getFooter() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public boolean loaded() {
		return l;
	}
}
