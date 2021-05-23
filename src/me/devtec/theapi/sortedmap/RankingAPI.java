package me.devtec.theapi.sortedmap;


import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RankingAPI<K, V> {
	private Map<K, V> s;

	public RankingAPI(Map<K, V> map) {
		setMap(map);
	}

	public K getObject(int position) {
		if (position <= 0)
			position = 1;
		int i = 0;
		K f = null;
		for (Entry<K, V> e : s.entrySet()) {
			if (++i >= position) {
				f = e.getKey();
				break;
			}
		}
		return f;
	}

	public int size() {
		return s.size();
	}

	public void clear() {
		s.clear();
	}

	public Set<K> getKeySet() {
		return s.keySet();
	}

	public Map<K, V> getMap() {
		return s;
	}

	public void setMap(Map<K, V> map) {
		s = SortedMap.sortNonComparableByValue(map);
	}

	public Set<Entry<K, V>> entrySet() {
		return s.entrySet();
	}

	public boolean containsKey(K o) {
		return s.containsKey(o);
	}

	public V getValue(K o) {
		return s.get(o);
	}

	public int getPosition(K o) {
		int i = 0, f = 0;
		for (Entry<K, V> e : s.entrySet()) {
			if (e.getKey().equals(o)) {
				f = 1;
				break;
			}
			++i;
		}
		return f == 0 ? 0 : ++i;
	}
}