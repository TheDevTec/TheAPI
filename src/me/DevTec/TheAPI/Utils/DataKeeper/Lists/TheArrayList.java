package me.DevTec.TheAPI.Utils.DataKeeper.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheCollection;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheList;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

@SuppressWarnings("unchecked")
public class TheArrayList<T> implements TheList<T> {
	private static final long serialVersionUID = -1511439452126949560L;
	private static final int DEFAULT_CAPACITY = 10;
    
	private transient T[] data;
	private int size=0;
	public TheArrayList() {
		this(DEFAULT_CAPACITY);
	}

	public TheArrayList(int initialSize) {
		Validator.validate(initialSize <= -1, "Initial size must be greater than -1");
		this.data =  (T[]) new Object[initialSize];
	}
	
	public TheArrayList(TheCollection<T> data) {
		this((int) (data.size() * 1.1f));
	    addAll(data);
	}

	public TheArrayList(TheCollection<T> data, int initialSize) {
		this((int) (initialSize * 1.1f));
		addAll(data);
	}
	
	public TheArrayList(Collection<T> data) {
		this((int) (data.size() * 1.1f));
	    addAll(data);
	}
	
	public TheArrayList(Collection<T> collection, int initialSize) {
		this((int) (initialSize * 1.1f));
	    addAll(collection);
	}
	
	public TheArrayList(T... array) {
		data=array;
		size=array.length;
	}
	
	public boolean add(T item) {
		if (size >= data.length)
			data=Arrays.copyOf(data, size+3);
        data[size++] = item;
        return true;
	}
	
	public T get(int i) {
		if(data.length<=i || i < 0)return null;
		return data[i];
	}
	
	public T set(int index, T item) {
		if(index<0 || index >= size)return null;
		T old = get(index);
		if(item==null)remove(index);
		else
		data[index]=item;
		return old;
	}
	
	public void remove(int index) {
		if(index<0 || index >= size)return;
		int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(data, index+1, data, index, numMoved);
        data[--size] = null;
	}
	
	public boolean remove(T item) {
		 if (item == null) {
	            for (int index = 0; index < size; index++)
	                if (data[index] == null) {
	                	int numMoved = size - index - 1;
	                    if (numMoved > 0)
	                    System.arraycopy(data, index+1, data, index,numMoved);
	                    data[--size] = null;
	                    return true;
	                }
	        } else {
	            for (int index = 0; index < size; index++)
	                if (item.equals(data[index])) {
	                	int numMoved = size - index - 1;
	                    if (numMoved > 0)
	                    System.arraycopy(data, index+1, data, index,numMoved);
	                    data[--size] = null;
	                    return true;
	                }
	        }
	        return false;
	}
	
	public boolean contains(Object item) {
		if(item==null)return false;
		int i = 0;
		for(T o : data)
			if(o!=null && o.equals(item)) {
				i=1;
				break;
			}
		return i==1;
	}
	
	public T[] toArray() {
		return (T[]) Arrays.copyOf(data, size);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size==0;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int c=0;
			@Override
			public boolean hasNext() {
				return size > c;
			}
			@Override
			public T next() {
				return data[c++];
			}
		};
	}

	@Override
	public void clear() {
		size=0;
        for (int i = 0; i < size; i++)
            data[i] = null;
	}

	@Override
	public boolean addAll(TheCollection<? extends T> collection) {
		T[] a = collection.toArray();
	    int numNew = a.length;
        data = Arrays.copyOf(data, size+numNew);
	    System.arraycopy(a, 0, data, size, numNew);
	    size += numNew;
	    return numNew != 0;
	}

	@Override
	public boolean removeAll(TheCollection<? extends T> collection) {
        int modified = 0;
        for(T o : this) {
        	if(collection.contains(o)) {
        	remove(o);
        	modified = 1;
        	}
        }
        return modified==1;
	}

	public boolean addAll(Collection<? extends T> collection) {
	    int numNew = collection.toArray().length;
		data=Arrays.copyOf(data, size + numNew);
	    System.arraycopy(collection.toArray(), 0, data, size, numNew);
	    size += numNew;
	    return numNew != 0;
	}

	public boolean removeAll(Collection<? extends T> collection) {
        int modified = 0;
        for(T o : this) {
        	if(collection.contains(o)) {
        	remove(o);
        	modified = 1;
        	}
        }
        return modified==1;
	}
	
	public String toString() {
		return "["+StringUtils.join(data, ", ")+"]";
	}

	@Override
	public String getDataName() {
		return "TheArrayList("+toString()+")";
	}
}