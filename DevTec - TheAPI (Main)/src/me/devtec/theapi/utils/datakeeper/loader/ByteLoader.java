package me.devtec.theapi.utils.datakeeper.loader;

import java.util.Base64;
import java.util.Map;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.json.Json;

public class ByteLoader extends EmptyLoader {

	public void reset() {
		super.reset();
		loaded=false;
	}

	private static void byteBuilder(ByteArrayDataInput bos, Map<String, Object[]> map) {
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
				byteBuilder(bos, map);
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
			if (version == 1) { //V1
				while (true)
					try {
						String key = bos.readUTF();
						String value = bos.readUTF();
						if (!value.equals("null")) {
							value = value.substring(1);
						} else value = null;
						String next;
						boolean run = true;
						while (run)
							try {
								next = bos.readUTF();
								if (next.equals("null")) {
									value += null;
									continue;
								}
								if (next.equals("0")) {
									run = false;
									continue;
								} else {
									next = next.substring(1);
									value += next;
								}
							} catch (Exception not) {
								run = false;
							}
						data.put(key, new Object[]{Json.reader().read(value), null, value});
					} catch (Exception e) {
						break;
					}
			} else if (version == 2) { //V2
				String key = bos.readUTF();
				while (!key.equals("1")) key = bos.readUTF();
				while (true)
					try {
						key = bos.readUTF();
						String value = bos.readUTF();
						if (!value.equals("null")) {
							value = value.substring(1);
						} else value = null;
						String next;
						boolean run = true;
						while (run)
							try {
								next = bos.readUTF();
								if (next.equals("null")) {
									value += null;
									continue;
								}
								if (next.equals("0")) {
									run = false;
									continue;
								} else {
									next = next.substring(1);
									value += next;
								}
							} catch (Exception not) {
								run = false;
							}
						data.put(key, new Object[]{Json.reader().read(value), null, value});
					} catch (Exception e) {
						break;
					}
			}else if (version == 3){ //V3
				bos.readInt();
				byteBuilder(bos, data);
			}
			if (!data.isEmpty())
				loaded = true;
		} catch (Exception er) {
			loaded = false;
		}
	}
}
