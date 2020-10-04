package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.List;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;

public class EmptyLoader implements DataLoader {
	private MultiMap<String, String, DataHolder> data = new MultiMap<>();
	private List<String> header = new ArrayList<>(1), footer = new ArrayList<>(1);
	
	@Override
	public MultiMap<String, String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		header.clear();
		footer.clear();
	}

	@Override
	public List<String> getHeader() {
		return header;
	}

	@Override
	public List<String> getFooter() {
		return footer;
	}

	@Override
	public boolean loaded() {
		return true;
	}
}
