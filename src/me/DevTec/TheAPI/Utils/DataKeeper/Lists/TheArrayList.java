package me.DevTec.TheAPI.Utils.DataKeeper.Lists;

import java.util.Iterator;

import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheList;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

@SuppressWarnings("unchecked")
public class TheArrayList<T> implements TheList<T> {
	private Object data[]={};
	private int size=0;
	public TheArrayList() {
		this(1);
	}

	public TheArrayList(int initialSize) {
		Validator.validate(initialSize <= -1, "Initial size must be greater than -1");
		if(initialSize <= -1)
		initialSize=0;
		this.data = new Object[initialSize];
	}
	
	public TheArrayList(Iterable<T> data) {
		this(data, 1);
	}

	public TheArrayList(Iterable<T> data, int initialSize) {
		this(initialSize);
		addAll(data);
	}
	
	public TheArrayList(Iterator<T> data) {
		this(data, 1);
	}

	public TheArrayList(Iterator<T> data, int initialSize) {
		this(initialSize);
		addAll(data);
	}
	
	public T add(T item) {
		if (size >= data.length)
		      updateSize(size==0?1:size*2);
		data[size++]=item;
		return item;
	}
	
	public T get(int i) {
		if(data.length<=i || i < 0)return null;
		return (T) data[i];
	}
	
	public T set(int index, T item) {
		if(index<0 || index >= size)return item;
		data[index]=item;
		return item;
	}
	
	public void remove(int index) {
		if(index<0 || index >= size)return;
	    for(int i=index;i<size - 1;i++)
	       data[i]=data[i+1];
	    size--;
	}
	
	public void remove(T item) {
		if(item==null)return;
		int index = 0;
		for(Object o : data) { //find item
			if(o!=null)
			if(o.equals(item))break;
			++index;
		}
		for(int i=index;i<size - 1;i++)
		       data[i]=data[i+1];
		size--;
	}
	
	public boolean contains(T item) {
		if(item==null)return false;
		int i = 0;
		for(Object o : data) { //find item
			if(o!=null)
			if(o.equals(item)) {
				i=1;
				break;
			}
		}
		return i==1;
	}
	
	public T[] toArray() {
		return (T[]) data;
	}
	
	public int size() {
		return size;
	}
	
	private void updateSize(int size) {
		data=TheArrays.copyOf(data, size);
	}
	
	public String toString() {
		return data.toString();
	}

	public boolean isEmpty() {
		return size==0;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int c=1;
			@Override
			public boolean hasNext() {
				return !(size <= c);
			}
			@Override
			public T next() {
				return (T) data[c++];
			}
		};
	}

	@Override
	public void clear() {
		data=new Object[0];
	}
}