package me.DevTec.TheAPI.Utils.DataKeeper.Collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * Created 26.11. 2020
 * 
 * @author StraikerinaCZ
 * @since 5.1
 * 
 * @apiNote
 * Plus:
 * - Faster method removeAll (About ~%)
 * - May contain duplicate & null values (Like List)
 * - No sorting of embedded objects
 * - Faster methods:
 *     * addAll
 *     * retainAll
 *     * remove
 *     * contains
 *     * add
 *     * clear
 * Minus:
 * - Slower methods
 * -   * containsAll
 *
 * @param <V> Param of values in Set
 */

public class UnsortedSet<V> implements Set<V> {
	class Bucket {
		V val;
		int assigned;
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
	private Bucket last;
	private int size;
	
	public UnsortedSet() {
		this(3);
	}
	
	public UnsortedSet(int size) {
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
		last=bucket;
	}
	
	public UnsortedSet(Collection<? extends V> e) {
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
		int con = 0;
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			if(c.val.equals(value)) {
				con=1;
				break;
			}
			c=c.next;
		}
		return con==1;
	}

	@Override
	public boolean add(V value) {
		Bucket c = last;
		if(c.assigned==0 && c == bucket) {
			c.assigned=1;
			c.val=value;
			++size;
			return true;
		}
		Bucket b = c.next==null?new Bucket():c.next;
		b.val=value;
		b.assigned=1;
		last=c.next=b;
		++size;
		return true;
	}

	@Override
	public boolean remove(Object value) {
		Bucket c = bucket, prev=c;
		int found = 0;
		for(int i = 0; i < size; ++i) {
			if(c.val.equals(value)) {
				if(c==bucket) {
					bucket=c.next==null?new Bucket():c.next;
					found=1;
					--size;
				}else {
					prev.next=c.next; //remove bucket & trim to size
					found=1;
					--size;
					c=c.next;
					continue;
				}
			}
			last=c;
			prev=last;
			c=c.next;
		}
		return found==1;
	}

	@Override
	public void clear() {
		size=0;
		bucket.next=null;
		bucket.val=null;
		bucket.assigned=0;
		last=bucket;
	}
	
	public String toString() {
		StringBuffer f = new StringBuffer("[");
		Iterator<V> iterator = iterator();
		int i = 0;
		while(iterator.hasNext()) {
			if(i!=0)f.append(", "); i=1;
			f.append(iterator.next()+"");
		}
		return f.append("]").toString();
	}

	@Override
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			Bucket c = bucket;
			int i = 0;
			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public V next() {
				++i;
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
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			array[i]=c.val;
			c=c.next;
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] array) {
		Bucket c = bucket;
		for(int i = 0; i < size; ++i) {
			array[i]=(T) c.val;
			c=c.next;
		}
		return array;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		int d = 0;
		Iterator<?> t = c.iterator();
		while(t.hasNext())
			if(!contains(t.next())) {
				d=1;
				break;
			}
		return d==1;
	}

	@Override
	public boolean addAll(Collection<? extends V> cc) {
		if(cc==null || cc.isEmpty())return false;
		Bucket c = last;
		for(V value : cc) {
			if(c.assigned==0 && c == bucket) {
				c.assigned=1;
				c.val=value;
				++size;
				continue;
			}
			Bucket b = c.next==null?new Bucket():c.next;
			b.val=value;
			b.assigned=1;
			last=c=c.next=b;
			++size;
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> cc) {
		if(cc==null || cc.isEmpty())return false;
		int found = 0;
		Bucket c = bucket, prev=c;
		for(int i = 0; i < size; ++i) {
			if(!cc.contains(c.val)) {
				if(c==bucket) {
					bucket=c.next==null?new Bucket():c.next;
					found=1;
					--size;
				}else {
					prev.next=c.next; //remove bucket & trim to size
					found=1;
					--size;
					c=c.next;
					continue;
				}
			}
			last=c;
			prev=last;
			c=c.next;
		}
		return found==1;
	}

	@Override
	public boolean removeAll(Collection<?> values) {
		if(values==null||values.isEmpty())return false;
		Bucket c = bucket, prev=c;
		int found = 0;
		while(c!=null && c.assigned==1) {
			if(values.contains(c.val)) {
				if(c==bucket) {
					bucket=c.next==null?new Bucket():c.next;
					found=1;
					--size;
				}else {
					prev.next=c.next; //remove bucket & trim to size
					found=1;
					--size;
					c=c.next;
					continue;
				}
			}
			last=c;
			prev=last;
			c=c.next;
		}
		return found==1;
	}
}
