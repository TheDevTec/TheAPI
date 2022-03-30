package me.devtec.shared.dataholder.loaders;

import java.util.Base64;
import java.util.Map;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.json.Json;

public class ByteLoader extends EmptyLoader {

	public void reset() {
		super.reset();
		loaded=false;
	}

	private static void byteBuilderV3(ByteArrayDataInput bos, Map<String, Object[]> map) {
		try{
			String key=bos.readUTF();
			String value=null;
			int result;
			try {
				while((result=bos.readInt())==1) { //normal value
					if(value==null)value=bos.readUTF();
					else value+=bos.readUTF();
				}
				if(result==3) { //null pointer
					value=null;
					result=bos.readInt();
				}
			}catch(Exception err) {
				value=YamlLoader.r(value);
				map.put(key, new Object[]{Json.reader().read(value), null, value});
				return;
			}
			value=YamlLoader.r(value);
			map.put(key, new Object[]{Json.reader().read(value), null, value});
			if(result==0)
				byteBuilderV3(bos, map);
		}catch(Exception err) {}
	}

	@Override
	public void load(String input) {
		reset();
		if (input == null)
			return;
		try {
			byte[] bb = Base64.getDecoder().decode(input.replace(System.lineSeparator(), ""));
			ByteArrayDataInput bos = ByteStreams.newDataInput(bb);
			int version = bos.readInt();
			if(version == 3) {
				bos.readInt();
				byteBuilderV3(bos, data);
			}
			if (!data.isEmpty())
				loaded = true;
		} catch (Exception er) {
			loaded = false;
		}
	}
}
