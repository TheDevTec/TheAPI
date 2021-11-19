package me.devtec.theapi.utils.datakeeper.abstracts;

import java.util.RandomAccess;

public interface Data extends RandomAccess, Cloneable {
	public String getDataName();
}
