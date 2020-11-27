package me.DevTec.TheAPI.Utils.DataKeeper.Collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UnsortedList<V> implements List<V> {
	class Bucket {
		V val;
		int assigned;
		Bucket next, prev;

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
	
	public UnsortedList() {
		this(3);
	}
	
	public UnsortedList(int size) {
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
	
	public UnsortedList(Collection<? extends V> e) {
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
		while(c!=null && c.assigned==1) {
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
		b.prev=c;
		last=c.next=b;
		++size;
		return true;
	}

	@Override
	public boolean remove(Object value) {
		Bucket c = bucket;
		int found = 0;
		while(c!=null && c.assigned==1) {
			if(c.val.equals(value)) {
				if(c==bucket) {
					bucket=c.next;
					c.next.prev=null;
					found=1;
					--size;
				}else {
					c.next.prev=c.prev;
					c.prev.next=c.next; //remove bucket & trim to size
					found=1;
					--size;
					c=c.next;
					continue;
				}
			}
			last=c;
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
		StringBuffer f = new StringBuffer(size());
		f.append("[");
		for(V e : this) {
			if(!f.toString().equals("["))
				f.append(", ");
			f.append(e+"");
		}
		return f.append("]").toString();
	}

	@Override
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			Bucket c = bucket;
			@Override
			public boolean hasNext() {
				return c!=null && c.assigned==1;
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
		while(c!=null && c.assigned==1) {
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
		while(c!=null && c.assigned==1) {
			array[d++]=(T) c.val;
			c=c.next;
		}
		return array;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean d = true;
		for(Object a : c) {
			if(!contains(a)) {
				d=false;
				break;
			}
		}
		return d;
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
			b.prev=c;
			last=c=c.next=b;
			++size;
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> cc) {
		if(cc==null || cc.isEmpty())return false;
		UnsortedSet<Object> remove = new UnsortedSet<>();
		Bucket ca = bucket;
		while(ca!=null && ca.assigned==1) {
			if(!cc.contains(ca.val))
				remove.add(ca.val);
			ca=ca.next;
		}
		return removeAll(remove);
	}

	@Override
	public boolean removeAll(Collection<?> values) {
		if(values==null||values.isEmpty())return false;
		Bucket c = bucket;
		int found = 0;
		while(c!=null && c.assigned==1) {
			if(values.contains(c.val)) {
				if(c==bucket) {
					bucket=c.next;
					c.next.prev=null;
					found=1;
					--size;
				}else {
					c.next.prev=c.prev;
					c.prev.next=c.next; //remove bucket & trim to size
					found=1;
					--size;
					c=c.next;
					continue;
				}
			}
			last=c;
			c=c.next;
		}
		return found==1;
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> cc) {
		if(index > cc.size() || index < 0 || cc==null||cc.isEmpty())return false;
		Bucket c = last;
		int run = 0;
		for(V value : cc) {
			if(run++<index)continue;
			if(c.assigned==0 && c == bucket) {
				c.assigned=1;
				c.val=value;
				++size;
				continue;
			}
			Bucket b = c.next==null?new Bucket():c.next;
			b.val=value;
			b.assigned=1;
			b.prev=c;
			last=c=c.next=b;
			++size;
		}
		return true;
	}

	@Override
	public V get(int index) {
		if(index < 0 || index > size)return null;
		return getBucketAt(index).val;
	}

	@Override
	public V set(int index, V element) {
		if(index < 0 || index > size)return null;
		V item = null;
		int a = 0;
		Bucket c = bucket;
		while(c!=null && c.assigned==1) {
			if(a++==index) {
				item=c.val;
				c.val=element;
				break;
			}
			c=c.next;
		}
		return item;
	}

	@Override
	public void add(int index, V element) {
		if(index < 0 || index > size)return;
		int a = 0;
		Bucket c = bucket;
		while(c!=null && c.assigned==1) {
			if(++a==index) {
				Bucket next = c.next;
				c.next=new Bucket();
				c.next.val=element;
				c.next.next=next;
				break;
			}
			c=c.next;
		}
	}

	@Override
	public V remove(int index) {
		if(index < 0 || index > size)return null;
		V item = null;
		int a = 0;
		Bucket c = bucket, before = c;
		while(c!=null && c.assigned==1) {
			if(a++==index) {
				item=c.val;
				before.next=c.next;
				break;
			}
			before=c;
			c=c.next;
		}
		return item;
	}

	@Override
	public int indexOf(Object o) {
		int a = 0;
		int f = 0;
		Bucket c = bucket;
		while(c!=null && c.assigned==1) {
			if(c.val==null && o ==null || c.val!=null && c.val.equals(o)) {
				f=a;
				break;
			}
			a++;
			c=c.next;
		}
		return f;
	}

	@Override
	public int lastIndexOf(Object o) {
		int a = 0;
		int f = 0;
		Bucket c = bucket;
		while(c!=null && c.assigned==1) {
			if(c.val==null && o ==null || c.val!=null && c.val.equals(o))f=a;
			a++;
			c=c.next;
		}
		return f;
	}

	@Override
	public ListIterator<V> listIterator() {
		return listIterator(0);
	}

	public Bucket getBucketAt(int index) {
		if(index > size || index < 0)return null;
		Bucket b = bucket;
		int c = 0;
		while(b!=null && b.assigned==1) {
			if(c++==index)break;
			b=b.next;
		}
		return b;
	}
	
	@Override
	public ListIterator<V> listIterator(int index) {
		if(index < 0)return null;
		return new ListIterator<V>() {
			int c = index;
			
			@Override
			public boolean hasNext() {
				return c < size;
			}

			@Override
			public V next() {
				++c;
				return getBucketAt(c).val;
			}

			@Override
			public boolean hasPrevious() {
				return c > 0;
			}

			@Override
			public V previous() {
				--c;
				return getBucketAt(c).val;
			}

			@Override
			public int nextIndex() {
				return c+1;
			}

			@Override
			public int previousIndex() {
				return c-1;
			}

			@Override
			public void remove() {
				UnsortedList.this.remove(c);
			}

			@Override
			public void set(V e) {
				UnsortedList.this.set(c, e);
				
			}

			@Override
			public void add(V e) {
				UnsortedList.this.add(c, e);
			}
		};
	}

	@Override
	public List<V> subList(int fromIndex, int toIndex) {
		List<V> subList = new UnsortedList<>();
		if(fromIndex > size || fromIndex < 0 || toIndex > size || toIndex < 0 || toIndex < fromIndex)return subList;
		ListIterator<V> listIterator = listIterator(fromIndex);
		int c = fromIndex;
		while(listIterator.hasNext()) {
			if(c++==toIndex)break;
			subList.add(listIterator.next());
		}
		return subList;
	}
}
