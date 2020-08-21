package me.DevTec.Other;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Data {
	private final Map<String, Object> map = Maps.newHashMap();
  
	public void load(String input) {
		map.clear();
		try {
			ByteArrayInputStream bos = new ByteArrayInputStream(Base64.getDecoder().decode(input));
			GZIPInputStream zos = new GZIPInputStream(bos);
			ObjectInputStream ous = new ObjectInputStream(zos);
			String key=null;
			while(true) {
				try {
					if(key==null)key=ous.readUTF();
					else {
						map.put(key, ous.readObject());
						key=null;
					}
				}catch(Exception e) {
				break;
				}
			}
		}catch(Exception e) {}
	}

	public Set<String> getKeys() {
		return map.keySet();
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
  
	public int size() {
		return map.size();
	}
  
	public void set(String key, Object value) {
		map.put(key, value);
	}
  
	public boolean hasKey(String key) {
		return map.containsKey(key);
	}
	
	public boolean exists(String key) {
		return map.containsKey(key);
	}
  
	public Object get(String key) {
		try {
			return map.get(key);
		} catch (Exception error) {
			return null;
		}
	}
  
	public short getShort(String key) {
		try {
			return (short)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public byte getByte(String key) {
		try {
			return (byte)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public float getFloat(String key) {
		try {
			return (float)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public long getLong(String key) {
		try {
			return (long)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public int getInt(String key) {
		try {
			return (int)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public double getDouble(String key) {
		try {
			return (double)map.get(key);
		} catch (Exception error) {
			return 0;
		}
	}
  
	public String getString(String key) {
		try {
			return map.get(key).toString();
		} catch (Exception error) {
			return null;
		}
	}

	public double[] getDoubleArray(String key) {
		try {
			return (double[])map.get(key);
		} catch (Exception error) {
			return new double[0];
		}
	}

	public byte[] getByteArray(String key) {
		try {
			return (byte[])map.get(key);
		} catch (Exception error) {
			return new byte[0];
		}
	}

	public int[] getIntArray(String key) {
		try {
			return (int[])map.get(key);
		} catch (Exception error) {
			return new int[0];
		}
	}
  
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) {
		try {
			return (List<T>)map.get(key);
		} catch (Exception error) {
			return Lists.newArrayList();
		}
	}
  
	public boolean getBoolean(String key) {
		try {
			return (boolean)map.get(key);
		} catch (Exception error) {
			return false;
		}
	}
  
	public void remove(String key) {
		map.remove(key);
	}
	
	public String toString() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream zos = new GZIPOutputStream(bos);
			ObjectOutputStream ous = new ObjectOutputStream(zos);
			for(Object key : map.keySet()) {
				ous.writeUTF(key.toString());
				ous.writeObject(map.get(key.toString()));
			}
			zos.finish();
			bos.flush();
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		}catch(Exception e) {}
		return Base64.getEncoder().encodeToString(new byte[0]);
	}
	
	public String save() {
		return toString();
	}
  
	public boolean isEmpty() {
		return map.isEmpty();
	}
  
	public boolean equals(Object object) {
		return (super.equals(object) && Objects.equals(map.entrySet(), ((Data)object).map.entrySet()));
	}
  
	public int hashCode() {
		return super.hashCode() ^ map.hashCode();
	}
}