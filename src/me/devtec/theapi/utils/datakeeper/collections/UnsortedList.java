package me.devtec.theapi.utils.datakeeper.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * Created 27.11. 2020
 * 
 * @author StraikerinaCZ
 * @since 5.1
 * 
 
 */

public class UnsortedList<V> implements List<V> {
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
			} finally {
				val = value;
			}
		}

		public String toString() {
			return val.toString() + "";
		}

	}

	private Bucket bucket;
	private Bucket last;
	private int size;

	public UnsortedList() {
		this(3);
	}

	public UnsortedList(int size) {
		if (size > 0) {
			bucket = new Bucket();
			Bucket current = bucket;
			Bucket next = new Bucket();
			for (int i = 0; i < size; ++i) {
				current.next = next;
				current = next;
				next = new Bucket();
			}
		} else
			bucket = new Bucket();
		last = bucket;
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
		return size == 0;
	}
	
	@Override
	public boolean contains(Object value) {
		int con = 0;
		Bucket c = bucket;
		while (c != null && c.assigned == 1) {
			if (c.val==null ? value==null : c.val.equals(value)) {
				con = 1;
				break;
			}
			c = c.next;
		}
		return con == 1;
	}
	
	@Override
	public boolean add(V value) {
		Bucket c = last;
		if (c.assigned == 0 && c == bucket) {
			c.assigned = 1;
			c.val = value;
			++size;
			return true;
		}
		Bucket b = c.next == null ? new Bucket() : c.next;
		b.val = value;
		b.assigned = 1;
		last = c.next = b;
		++size;
		return true;
	}
	
	@Override
	public boolean remove(Object value) {
		Bucket c = bucket, prev = c;
		int found = 0;
		while (c != null && c.assigned == 1) {
			if (c.val==null ? value==null : c.val.equals(value)) {
				if (c == bucket) {
					bucket = c.next == null ? new Bucket() : c.next;
					found = 1;
					--size;
				} else {
					prev.next = c.next; // remove bucket & trim to size
					found = 1;
					--size;
					c = c.next;
					continue;
				}
			}
			last = c;
			prev = last;
			c = c.next;
		}
		return found == 1;
	}
	
	@Override
	public void clear() {
		size = 0;
		bucket.next = null;
		bucket.val = null;
		bucket.assigned = 0;
		last = bucket;
	}

	public String toString() {
		StringBuffer f = new StringBuffer("[");
		Iterator<V> iterator = iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (i != 0)
				f.append(", ");
			i = 1;
			V v = iterator.next();
			f.append(v==null?"null":v.toString());
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
				} finally {
					c = c.next;
				}
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size];
		Bucket c = bucket;
		for (int i = 0; i < size; ++i) {
			array[i] = c.val;
			c = c.next;
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] array) {
		Bucket c = bucket;
		for (int i = 0; i < size; ++i) {
			array[i] = (T) c.val;
			c = c.next;
		}
		return array;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		int d = 0;
		Iterator<?> t;
		for (t = c.iterator(); t.hasNext();)
			if (!contains(t.next())) {
				d = 1;
				break;
			}
		return d == 1;
	}

	@Override
	public boolean addAll(Collection<? extends V> cc) {
		if (cc == null || cc.isEmpty())
			return false;
		Bucket c = last;
		for (V value : cc) {
			if (c.assigned == 0 && c == bucket) {
				c.assigned = 1;
				c.val = value;
				++size;
				continue;
			}
			Bucket b = c.next == null ? new Bucket() : c.next;
			b.val = value;
			b.assigned = 1;
			last = c = c.next = b;
			++size;
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> cc) {
		if (cc == null || cc.isEmpty())
			return false;
		int found = 0;
		Bucket c = bucket, prev = c;
		while (c != null && c.assigned == 1) {
			if (!cc.contains(c.val)) {
				if (c == bucket) {
					bucket = c.next == null ? new Bucket() : c.next;
					found = 1;
					--size;
				} else {
					prev.next = c.next; // remove bucket & trim to size
					found = 1;
					--size;
					c = c.next;
					continue;
				}
			}
			last = c;
			prev = last;
			c = c.next;
		}
		return found == 1;
	}

	@Override
	public boolean removeAll(Collection<?> values) {
		if (values == null || values.isEmpty())
			return false;
		Bucket c = bucket, prev = c;
		int found = 0;
		while (c != null && c.assigned == 1) {
			if (values.contains(c.val)) {
				if (c == bucket) {
					bucket = c.next == null ? new Bucket() : c.next;
					found = 1;
					--size;
				} else {
					prev.next = c.next; // remove bucket & trim to size
					found = 1;
					--size;
					c = c.next;
					continue;
				}
			}
			last = c;
			prev = last;
			c = c.next;
		}
		return found == 1;
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> cc) {
		if (index > cc.size() || index < 0 || cc == null || cc.isEmpty())
			return false;
		Bucket c = last;
		int run = 0;
		for (V value : cc) {
			if (run++ < index)
				continue;
			if (c.assigned == 0 && c == bucket) {
				c.assigned = 1;
				c.val = value;
				++size;
				continue;
			}
			Bucket b = c.next == null ? new Bucket() : c.next;
			b.val = value;
			b.assigned = 1;
			last = c = c.next = b;
			++size;
		}
		return true;
	}

	@Override
	public V get(int index) {
		if (index < 0 || index > size)
			return null;
		return getBucketAt(index).val;
	}

	@Override
	public V set(int index, V element) {
		if (index < 0 || index > size)
			return null;
		V item = null;
		int a = 0;
		Bucket c = bucket;
		while (c != null && c.assigned == 1) {
			if (a++ == index) {
				item = c.val;
				c.val = element;
				break;
			}
			c = c.next;
		}
		return item;
	}

	@Override
	public void add(int index, V element) {
		if (index < 0 || index > size)
			return;
		int a = 0;
		Bucket c = bucket;
		while (c != null && c.assigned == 1) {
			if (a++ == index) {
				Bucket next = c.next;
				Bucket d = new Bucket();
				d.val = element;
				d.assigned = 1;
				c.next = d;
				d.next = next;
				++size;
				break;
			}
			c = c.next;
		}
	}

	@Override
	public V remove(int index) {
		if (index < 0 || index > size)
			return null;
		V item = null;
		int a = 0;
		Bucket c = bucket, before = c;
		while (c != null && c.assigned == 1) {
			if (a++ == index) {
				if (c == before) {
					if (c.next == null) {
						c.assigned = 0;
						c.val = null;
					} else
						bucket = c.next;
				} else {
					item = c.val;
					before.next = c.next;
				}
				--size;
				break;
			}
			before = c;
			c = c.next;
		}
		return item;
	}

	@Override
	public int indexOf(Object o) {
		int a = 0, f = -1;
		Bucket c = bucket;
		while (c != null && c.assigned == 1) {
			if (c.val.equals(o)) {
				f = a;
				break;
			}
			a++;
			c = c.next;
		}
		return f;
	}

	@Override
	public int lastIndexOf(Object o) {
		int a = 0, f = -1;
		Bucket c = bucket;
		while (c != null && c.assigned == 1) {
			if (c.val.equals(o))
				f = a;
			a++;
			c = c.next;
		}
		return f;
	}

	@Override
	public ListIterator<V> listIterator() {
		return listIterator(0);
	}

	private Bucket getBucketAt(int index) {
		if (index > size || index < 0)
			return null;
		Bucket b = bucket;
		int c = 0;
		while (b != null && b.assigned == 1) {
			if (c++ == index)
				break;
			b = b.next;
		}
		return b;
	}

	@Override
	public ListIterator<V> listIterator(int index) {
		if (index < 0)
			return null;
		return new ListIterator<V>() {
			int c = index - 1;

			@Override
			public boolean hasNext() {
				return c < size;
			}

			@Override
			public V next() {
				return getBucketAt(++c).val;
			}

			@Override
			public boolean hasPrevious() {
				return c > 0;
			}

			@Override
			public V previous() {
				return getBucketAt(--c).val;
			}

			@Override
			public int nextIndex() {
				return c + 1;
			}

			@Override
			public int previousIndex() {
				return c - 1;
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
		if (fromIndex > size || fromIndex < 0 || toIndex > size || toIndex < 0 || toIndex < fromIndex)
			return subList;
		ListIterator<V> listIterator = listIterator(fromIndex);
		int c = fromIndex;
		while (listIterator.hasNext()) {
			if (c++ == toIndex)
				break;
			subList.add(listIterator.next());
		}
		return subList;
	}
}
