package me.devtec.shared.sorting;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.devtec.shared.sorting.SortingAPI.ComparableObject;

@Deprecated
/**
 * @apiNote Scheduler to remove in the 10.1 version. Use directly SortingAPI
 *          instead.
 */
public class RankingAPI<K, V> {
	private ComparableObject<K, V>[] s;

	public RankingAPI() {
	}

	public RankingAPI(Map<K, V> map) {
		this.load(map);
	}

	public ComparableObject<K, V> get(int position) {
		if (this.s.length <= position)
			return null;
		return this.s[position];
	}

	public boolean isEmpty() {
		return this.s == null || this.s.length == 0;
	}

	public int size() {
		return this.s == null ? 0 : this.s.length;
	}

	public void clear() {
		this.s = null;
	}

	public void load(Map<K, V> map) {
		this.s = SortingAPI.sortByValueArray(map, true);
	}

	@Deprecated
	public boolean containsKey(K o) {
		for (int i = 0; i < this.size(); ++i)
			if (this.s[i].getKey().equals(o))
				return true;
		return false;
	}

	@Deprecated
	public boolean containsValue(V o) {
		for (int i = 0; i < this.size(); ++i)
			if (o == null ? this.s[i].getValue() == null : o.equals(this.s[i].getValue()))
				return true;
		return false;
	}

	@Deprecated
	public int getPosition(K o) {
		for (int i = 0; i < this.size(); ++i)
			if (this.s[i].getKey().equals(o))
				return i;
		return 0;
	}

	public List<ComparableObject<K, V>> getTop(int end) {
		return this.getTop(0, end);
	}

	public List<ComparableObject<K, V>> getTop(int start, int end) {
		List<ComparableObject<K, V>> list = new LinkedList<>();
		for (int i = start; i < end; ++i)
			list.add(this.s[i]);
		return list;
	}

	public ComparableObject<K, V>[] all() {
		return this.s;
	}
}