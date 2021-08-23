package me.devtec.theapi.utils.json.instances.legacy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.devtec.theapi.Pair;
import me.devtec.theapi.utils.json.instances.JWriter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;

public class LegacyJsonWriter implements JWriter {
    private static final Gson parser = new GsonBuilder().create();
    
    public Object writeWithoutParse(Object s) {
        try {
            if (s == null)
                return null;
            if (s instanceof String || s instanceof CharSequence || s instanceof Boolean || s instanceof Number || s instanceof Character)
                return s;
            if (s instanceof Map) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName());
                object.put("t", "map");
                List<Object> vals = new ArrayList<>();
                for (Map.Entry<?, ?> o : ((Map<?, ?>) s).entrySet())
                    vals.add(writeWithoutParse(new Pair(o.getKey(), o.getValue())));
                object.put("s", vals);
                return object;
            }
            if (s instanceof Collection) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName());
                object.put("t", "collection");
                List<Object> vals = new ArrayList<>();
                for (Object o : (Collection<?>) s) vals.add(writeWithoutParse(o));
                object.put("s", vals);
                return object;
            }
            if (s.getClass().isArray()) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName().replace("[]", ""));
                object.put("t", "array");
                List<Object> vals = new ArrayList<>();
                for (Object o : (Object[]) s) vals.add(writeWithoutParse(o));
                object.put("s", vals);
                return object;
            }
            Map<String, Object> object = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            Map<String, Object> sub_fields = new HashMap<>();
            object.put("c", s.getClass().getCanonicalName());
            object.put("f", fields);
            object.put("sf", sub_fields);
            Class<?> c = s.getClass();
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static")) continue;
                f.setAccessible(true);
                Object obj = f.get(s);
                if (s.equals(obj))
                    fields.put("~" + f.getName(), "~");
                else
                    fields.put(f.getName(), writeWithoutParse(obj));
            }
            c = c.getSuperclass();
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static")) continue;
                    f.setAccessible(true);
                    Object obj = f.get(s);
                    if (s.equals(obj))
                        sub_fields.put(c.getCanonicalName() + ":~" + f.getName(), "~");
                    else
                        sub_fields.put(c.getCanonicalName() + ":" + f.getName(), writeWithoutParse(obj));
                }
                c = c.getSuperclass();
            }
            if (sub_fields.isEmpty()) object.remove("sf");
            return object;
        }catch (Exception err){}
        return null;
    }

    public String write(Object s) {
        try {
            if (s == null)
                return "null";
            if (s instanceof String || s instanceof CharSequence || s instanceof Boolean || s instanceof Number || s instanceof Character)
                return s.toString();
            if (s instanceof Collection) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName());
                object.put("t", "collection");
                List<Object> vals = new ArrayList<>();
                for (Object o : (Collection<?>) s) vals.add(writeWithoutParse(o));
                object.put("s", vals);
                return parser.toJson(object);
            }
            if (s instanceof Map) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName());
                object.put("t", "map");
                List<Object> vals = new ArrayList<>();
                for (Map.Entry<?, ?> o : ((Map<?, ?>) s).entrySet())
                    vals.add(writeWithoutParse(new Pair(o.getKey(), o.getValue())));
                object.put("s", vals);
                return parser.toJson(object);
            }
            if (s.getClass().isArray()) {
                Map<String, Object> object = new HashMap<>();
                object.put("c", s.getClass().getCanonicalName().replace("[]", ""));
                object.put("t", "array");
                List<Object> vals = new ArrayList<>();
                for (Object o : (Object[]) s) vals.add(writeWithoutParse(o));
                object.put("s", vals);
                return parser.toJson(object);
            }
            Map<String, Object> object = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            Map<String, Object> sub_fields = new HashMap<>();
            object.put("c", s.getClass().getCanonicalName());
            object.put("f", fields);
            object.put("sf", sub_fields);
            Class<?> c = s.getClass();
            for (Field f : c.getDeclaredFields()) {
                if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static")) continue;
                f.setAccessible(true);
                Object obj = f.get(s);
                if (s.equals(obj))
                    fields.put("~" + f.getName(), "~");
                else
                    fields.put(f.getName(), writeWithoutParse(obj));
            }
            c = c.getSuperclass();
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static")) continue;
                    f.setAccessible(true);
                    Object obj = f.get(s);
                    if (s.equals(obj))
                        sub_fields.put(c.getCanonicalName() + ":~" + f.getName(), "~");
                    else
                        sub_fields.put(c.getCanonicalName() + ":" + f.getName(), writeWithoutParse(obj));
                }
                c = c.getSuperclass();
            }
            if (sub_fields.isEmpty()) object.remove("sf");
            return parser.toJson(object);
        }catch (Exception err){}
        return null;
    }

	
    @Override
	public String simpleWrite(Object s) {
        if (s == null)
            return "null";
        if (s instanceof String || s instanceof CharSequence || s instanceof Boolean || s instanceof Number || s instanceof Character)
            return s.toString();
		return parser.toJson(s);
	}
	
	public String toString() {
		return "LegacyJsonWriter";
	}
}
