package me.DevTec.TheAPI.Utils.DataKeeper.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import me.DevTec.TheAPI.Utils.DataKeeper.Collections.LinkedSet;

public class NonSortedMap<K, V> implements Map<K, V> {
	class Bucket implements Entry<K, V> {
		K key;
		V val;
		Bucket next;
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return val;
		}

		@Override
		public V setValue(V value) {
			try {
			return val;
			}finally {
				val=value;
			}
		}
		
		public String toString() {
			return key+"="+val;
		}
		
		public int hashCode() {
			int hashCode = 1;
			hashCode+=key.hashCode()+val.hashCode();
			return hashCode;
		}
	}
	
	private final Bucket bucket;
	private int size;
	
	public NonSortedMap() {
		this(5);
	}
	
	public NonSortedMap(int size) {
		if(size>0) {
			bucket=new Bucket();
			Bucket current = bucket;
			Bucket next = new Bucket();
			for(int i = 0; i < size; ++i) {
				current.next=next;
				current=next;
				next=new Bucket();
			}
		}else {
			bucket = new Bucket();
		}
	}
	
	public NonSortedMap(Map<? extends K, ? extends V> e) {
		this(e.size());
		putAll(e);
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size==0;
	}

	@Override
	public boolean containsKey(Object key) {
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(c.key.equals(key))
				return true;
			c=c.next;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(value == null && c.val==null || c.val!=null && c.val.equals(value))
				return true;
			c=c.next;
		}
		return false;
	}

	@Override
	public V get(Object key) {
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(c.key.equals(key)) {
				return c.val;
			}
			c=c.next;
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(c.key.equals(key))
				return c.val=value;
			c=c.next;
		}
		if(c.key==null) {
			c.key=key;
			c.val=value;
			++size;
			c.next=new Bucket();
		}else {
			Bucket b = new Bucket();
			b.key=key;
			b.val=value;
			c.next=b;
			++size;
		}
		return null;
	}

	@Override
	public V remove(Object key) {
		V old = null;
		Bucket c = bucket;
		Bucket before = c;
		int found = 0;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(found==0 && c.key.equals(key)) {
				old=c.val;
				c.key=null;
				c.val=null;
				found=1;
				--size;
			}
			if(found==1) {
				before.next=c;
			}
			before=c;
			c=c.next;
		}
		if(found==1) {
			c.key=null;
			c.val=null;
			c.next=null;
		}
		return old;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if(m.isEmpty() || m.equals(this))return;
		Bucket c = bucket, done = new Bucket(), next = done;
		for(int i = 0; i < size; ++i) {
			if(c.key==null)break;
			if(m.containsKey(c.key)) {
				c.val=m.get(c.key);
				next.key=c.key;
				next.next=new Bucket();
				next=next.next;
			}
			c=c.next;
		}
		for(Entry<? extends K, ? extends V> e : m.entrySet()) {
			Bucket inf = done;
			int ccc = 0;
			while(inf!=null && inf.key!=null) {
				if(inf.key.equals(e.getKey())) {
					ccc=1;
					break;
				}
				inf=inf.next;
			}
			if(ccc==1)continue;
			c.key=e.getKey();
			c.val=e.getValue();
			c.next=new Bucket();
			++size;
			c=c.next;
		}
		c.next=new Bucket();
	}

	@Override
	public void clear() {
		size=0;
		bucket.next=null;
		bucket.key=null;
		bucket.val=null;
	}

	@Override
	public Set<K> keySet() {
		LinkedSet<K> key = new LinkedSet<>(size);
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c==null)break;
			key.add(c.key);
			c=c.next;
		}
		return key;
	}

	@Override
	public Collection<V> values() {
		LinkedSet<V> key = new LinkedSet<>(size);
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c==null)break;
			key.add(c.val);
			c=c.next;
		}
		return key;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		LinkedSet<Entry<K, V>> entries = new LinkedSet<>(size);
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c==null)break;
			entries.add(c);
			c=c.next;
		}
		return entries;
	}
	
	public String toString() {
		StringBuffer f = new StringBuffer(size());
		f.append("{");
		for(Entry<K, V> e : entrySet()) {
			if(!f.toString().equals("{"))
				f.append(", ");
			f.append(e.toString());
		}
		return f.append("}").toString();
	}

	public int hashCode() {
		int hashCode = 1;
		for(Entry<K, V> d : entrySet())
		hashCode+=d.hashCode();
		return hashCode;
	}
}
