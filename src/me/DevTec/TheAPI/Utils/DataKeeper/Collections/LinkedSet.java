package me.DevTec.TheAPI.Utils.DataKeeper.Collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class LinkedSet<V> implements Set<V> {
	class Bucket {
		V val;
		Bucket next;

		public V getValue() {
			return val;
		}

		public V setValue(V value) {
			try {
			return val;
			}finally {
				val=value;
			}
		}
		
		public String toString() {
			return val+"";
		}
		
	}
	private Bucket bucket;
	private int size;
	
	public LinkedSet() {
		this(1);
	}
	
	public LinkedSet(int size) {
		if(size>0) {
			bucket=new Bucket();
			Bucket current = bucket;
			Bucket next = new Bucket();
			for(int i = 0; i < size; ++i) {
				current.next=next;
				current=next;
				next=new Bucket();
			}
		}else bucket = new Bucket();
	}
	
	public LinkedSet(Collection<? extends V> e) {
		this(e.size());
		addAll(e);
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
	public boolean contains(Object value) {
		boolean con = false;
		Bucket c = bucket;
		while(c!=null && (value == null ? true : c.val!=null)) {
			if(c.val.equals(value)) {
				con=true;
				break;
			}
			c=c.next;
		}
		return con;
	}

	@Override
	public boolean add(V value) {
		if(value==null)return false;
		Bucket c = bucket;
		while(c.next!=null && c.val!=null)
			c=c.next; //get last
		if(c.val==null) {
			c.val=value;
			++size;
		}else {
			Bucket b = new Bucket();
			b.val=value;
			c.next=b;
			++size;
		}
		return true;
	}

	@Override
	public boolean remove(Object value) {
		if(value==null)return false;
		Bucket c = bucket;
		Bucket before = c;
		int found = 0;
		while(c!=null && c.val!=null) {
			if(found==0 && c.val.equals(value)) {
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
		return found==1;
	}

	@Override
	public void clear() {
		size=0;
		bucket.next=null;
		bucket.val=null;
	}
	
	public String toString() {
		StringBuffer f = new StringBuffer(size());
		f.append("[");
		for(V e : this) {
			if(!f.toString().equals("{"))
				f.append(", ");
			f.append(e.toString());
		}
		return f.append("]").toString();
	}

	@Override
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			Bucket c = bucket;
			@Override
			public boolean hasNext() {
				return c.next!=null;
			}

			@Override
			public V next() {
				try {
				return c.val;
				}finally {
					c=c.next;
				}
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size];
		int d = 0;
		Bucket c = bucket;
		while(c!=null && c.val!=null) {
			array[d++]=c.val;
			c=c.next;
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] array) {
		int d = 0;
		Bucket c = bucket;
		while(c!=null && c.val!=null) {
			array[d++]=(T) c.val;
			c=c.next;
		}
		return array;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean d = true;
		Bucket ca = bucket;
		while(ca!=null && ca.val!=null) {
			if(!c.contains(ca.val)) {
				d=false;
				break;
			}
			ca=ca.next;
		}
		return d;
	}

	@Override
	public boolean addAll(Collection<? extends V> cc) {
		if(cc==null || cc.isEmpty())return false;
		Bucket c = bucket;
		while(c.next!=null && c.val!=null)
			c=c.next; //get last
		for(V v : cc) {
		if(c.val==null) {
				c.val=v;
				++size;
			}else {
				Bucket b = new Bucket();
				b.val=v;
				c.next=b;
				++size;
			}
			c=c.next;
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> cc) {
		if(cc==null || cc.isEmpty())return false;
		LinkedSet<Object> remove = new LinkedSet<>();
		Bucket ca = bucket;
		while(ca!=null && ca.val!=null) {
			if(!cc.contains(ca.val))
				remove.add(ca.val);
			ca=ca.next;
		}
		for(Object o : remove)
			remove(o);
		return !remove.isEmpty();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
		for(Object o : c) {
			boolean isGone = remove(o);
			if(isGone)removed=true;
		}
		return removed;
	}
}
