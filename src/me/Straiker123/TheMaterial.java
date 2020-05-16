package me.Straiker123;

import org.bukkit.Material;

public class TheMaterial {
	
	public TheMaterial(String name, String data) {
		this(Material.matchMaterial(name),TheAPI.getStringUtils().getInt(data));
	}
	
	public TheMaterial(String name, int data) {
		this(Material.matchMaterial(name),data);
	}
	
	public TheMaterial(Material material, int data) {
		m=material;
		this.data=data;
	}
	
	public TheMaterial(Material material, String data) {
		this(material,TheAPI.getStringUtils().getInt(data));
	}
	
	public TheMaterial(String name) {
		this(Material.matchMaterial(name),0);
	}

	private Material m;
	private int data;
	
	public int getData() {
		return data;
	}
	
	public Material getType() {
		return m;
	}
	
	public void setType(Material material) {
		m=material;
	}
	
	public void setData(int data) {
		this.data=data;
	}

	public String toString() {
		return "[TheMaterial:"+m.name()+"/"+data+"]";
	}
	
	public static TheMaterial fromString(String stored) {
		if(stored.startsWith("[TheMaterial:")) {
			stored=stored.replaceFirst("[TheMaterial:", "").substring(0,stored.length()-1);
			String[] s = stored.split("/");
			return new TheMaterial(s[0],s[1]);
		}
		return null;
	}
	
	public boolean equals(Object a) {
		if(a instanceof TheMaterial) {
			TheMaterial material = (TheMaterial)a;
			return material.getData()==data && material.getType()==m;
		}
		if(a instanceof Material) {
			return m==(Material)a;
		}
		return false;
	}
}
