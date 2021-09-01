package me.devtec.theapi.utils.json.instances.modern;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.devtec.theapi.Pair;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.json.instances.JReader;
import me.devtec.theapi.utils.reflections.Ref;
import sun.misc.Unsafe;

public class ModernJsonReader implements JReader {
    private static final Gson parser = new GsonBuilder().create();
    private static Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe)f.get(null);
        }catch(Exception err){}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object read(String json) {
    	if(json==null || json.trim().isEmpty())return json;
        try {
            if (json.equals("null")) return null;
            if (json.equalsIgnoreCase("true")) return true;
            if (json.equalsIgnoreCase("false")) return false;
            if (StringUtils.isNumber(json)) return StringUtils.getNumber(json);
			Map<String, Object> map = parser.fromJson(json, Map.class);
            String className = (String) map.get("c");
            Class<?> c = Class.forName(className);
            String type = (String) map.get("t");
            if (type != null) { //collection, array or map
                switch (type) {
                    case "map": {
                        Object object;
                        try {
                            object = c.newInstance();
                        } catch (Exception e) {
                            object = Unsafe.getUnsafe().allocateInstance(c);
                        }
                        Map o = (Map) object;
                        for (Object cc : (List<?>) map.get("s")) {
                            Pair pair = (Pair) read(cc);
                            o.put(pair.getKey(), pair.getValue());
                        }
                        return o;
                    }
                    case "enum": {
                        return Ref.getNulled(c, map.get("e").toString());
                    }
                    case "array": {
                        Object[] obj = (Object[]) Array.newInstance(c, ((List<?>) map.get("s")).size());
                        int i = 0;
                        for (Object cc : (List<?>) map.get("s")) obj[i++] = read(cc);
                        return obj;
                    }
                    case "collection": {
                        Object object;
                        try {
                            object = c.newInstance();
                        } catch (Exception e) {
                            object = Unsafe.getUnsafe().allocateInstance(c);
                        }
                        Collection<Object> o = (Collection<Object>) object;
                        for (Object cc : (List<?>) map.get("s")) o.add(read(cc));
                        return o;
                    }
                }
                return null;
            }
            Object object;
            try {
                object = c.newInstance();
            } catch (Exception e) {
                object = unsafe.allocateInstance(c);
            }

            Map<String, Object> fields = (Map<String, Object>) map.get("f");
            Map<String, Object> sub_fields = (Map<String, Object>) map.get("sf");

            for (Map.Entry<String, Object> e : fields.entrySet()) {
                if (e.getKey().startsWith("~")) {
                    Field f = c.getDeclaredField(e.getKey().substring(1));
                    f.setAccessible(true);
                    f.set(object, object);
                    continue;
                }
                Field f = c.getDeclaredField(e.getKey());
                f.setAccessible(true);
                f.set(object, cast(e.getValue(), f.getType()));
            }
            if (sub_fields != null)
                for (Map.Entry<String, Object> e : sub_fields.entrySet()) {
                    String field = e.getKey().split(":")[1];
                    if (field.startsWith("~")) {
                        Field f = Class.forName(e.getKey().split(":")[0]).getDeclaredField(field.substring(1));
                        f.setAccessible(true);
                        f.set(object, object);
                        continue;
                    }
                    Field f = Class.forName(e.getKey().split(":")[0]).getDeclaredField(field);
                    f.setAccessible(true);
                    f.set(object, cast(e.getValue(), f.getType()));
                }
            return object;
        }catch(Exception err){
        	try {
        		return parser.fromJson(json, Collection.class);
        	}catch(Exception er) {}
        }
        return json;
    }

    private Object cast(Object value, Class<?> type) {
        if(value==null)return null;
        if(type.isArray()){
            Collection<?> o = (Collection<?>) value;
            List<Object> c = new ArrayList<>();
            for(Object a : o)c.add(read(a));
            return c.toArray();
        }
        if(Double.TYPE==type)return ((Number)value).doubleValue();
        if(Long.TYPE==type)return ((Number)value).longValue();
        if(Integer.TYPE==type)return ((Number)value).intValue();
        if(Float.TYPE==type)return ((Number)value).floatValue();
        if(Byte.TYPE==type)return ((Number)value).byteValue();
        if(Short.TYPE==type)return ((Number)value).shortValue();
        if(Character.TYPE==type)return (value+"").toCharArray()[0];
        return read(value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object read(Object s) {
    	if(s==null)return s;
        try {
            if (s instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) s;
                String className = (String) map.get("c");

                Class<?> c = Class.forName(className);
                String type = (String) map.get("t");
                if (type != null) { //collection, array or map
                    switch (type) {
                        case "map": {
                            Object object;
                            try {
                                object = c.newInstance();
                            } catch (Exception e) {
                                object = Unsafe.getUnsafe().allocateInstance(c);
                            }
                            Map o = (Map) object;
                            for (Object cc : (List<?>) map.get("s")) {
                                System.out.println(cc);
                                System.out.println(cc.getClass());
                                Pair pair = (Pair) read(cc);
                                o.put(pair.getKey(), pair.getValue());
                            }
                            return o;
                        }
                        case "array": {
                            Object[] obj = (Object[]) Array.newInstance(c, ((List<?>) map.get("s")).size());
                            int i = 0;
                            for (Object cc : (List<?>) map.get("s")) obj[i++] = read(cc);
                            return obj;
                        }
                        case "enum": {
                            return Ref.getNulled(c, map.get("e").toString());
                        }
                        case "collection": {
                            Object object;
                            try {
                                object = c.newInstance();
                            } catch (Exception e) {
                                object = Unsafe.getUnsafe().allocateInstance(c);
                            }
                            Collection<Object> o = (Collection<Object>) object;
                            for (Object cc : (List<?>) map.get("s")) o.add(read(cc));
                            return o;
                        }
                    }
                    return null;
                }
                Object object;
                try {
                    object = c.newInstance();
                } catch (Exception e) {
                    object = unsafe.allocateInstance(c);
                }

                Map<String, Object> fields = (Map<String, Object>) map.get("f");
                Map<String, Object> sub_fields = (Map<String, Object>) map.get("sf");
                for (Map.Entry<String, Object> e : fields.entrySet()) {
                    if (e.getKey().startsWith("~")) {
                        Field f = c.getDeclaredField(e.getKey().substring(1));
                        f.setAccessible(true);
                        f.set(object, object);
                        continue;
                    }
                    Field f = c.getDeclaredField(e.getKey());
                    f.setAccessible(true);
                    f.set(object, cast(e.getValue(), f.getType()));
                }
                if (sub_fields != null)
                    for (Map.Entry<String, Object> e : sub_fields.entrySet()) {
                        String field = e.getKey().split(":")[1];
                        if (field.startsWith("~")) {
                            Field f = Class.forName(e.getKey().split(":")[0]).getDeclaredField(field.substring(1));
                            f.setAccessible(true);
                            f.set(object, object);
                            continue;
                        }
                        Field f = Class.forName(e.getKey().split(":")[0]).getDeclaredField(field);
                        f.setAccessible(true);
                        f.set(object, cast(e.getValue(), f.getType()));
                    }
                return object;
            }
        }catch(Exception err){}
        return s;
    }

	@Override
	public Object simpleRead(String json) {
    	if(json==null || json.trim().isEmpty())return json;
        if (json.equals("null")) return null;
        if (json.equalsIgnoreCase("true")) return true;
        if (json.equalsIgnoreCase("false")) return false;
        if (StringUtils.isNumber(json)) return StringUtils.getNumber(json);
        Object read = null;
        try {
        	read=parser.fromJson(json, Map.class);
        	if(read==null)
            	read=parser.fromJson(json, Collection.class);
        }catch(Exception er) {
            try {
            	read=parser.fromJson(json, Collection.class);
            }catch(Exception err) {
            	
            }
        }
		return read==null?json:read;
	}
	
	public String toString() {
		return "ModernJsonReader";
	}
}