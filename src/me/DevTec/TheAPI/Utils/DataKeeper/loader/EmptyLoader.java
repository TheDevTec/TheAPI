package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;

public class EmptyLoader implements DataLoader {
	private HashMap<String, DataHolder> data = new HashMap<>();
	private List<String> header = new ArrayList<>(1), footer = new ArrayList<>(1);
	
	@Override
	public HashMap<String, DataHolder> get() {
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

	@Override
	public String getDataName() {
		return "Data(EmptyLoader:"+data.size()+")";
	}
}
