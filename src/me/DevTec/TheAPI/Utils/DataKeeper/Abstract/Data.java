package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

import java.io.Serializable;
import java.util.RandomAccess;

public interface Data extends RandomAccess, Serializable {
	public String getDataName();
}
