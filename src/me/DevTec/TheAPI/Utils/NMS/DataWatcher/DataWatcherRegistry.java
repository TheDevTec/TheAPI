package me.DevTec.TheAPI.Utils.NMS.DataWatcher;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

/**
 * @apiNote This utility is only for 1.9+
 */
public class DataWatcherRegistry {
	private static Class<?> a = Ref.nms("DataWatcherRegistry");

	/**
	 * @return DataWatcherObject<Byte>
	 */
	public static DataWatcherObject getByte(int data) {
		return new DataWatcherObject(c("a", data));
	}

	/**
	 * @return DataWatcherObject<Integer>
	 */
	public static DataWatcherObject getInt(int data) {
		return new DataWatcherObject(c("b", data));
	}

	/**
	 * @return DataWatcherObject<Float>
	 */
	public static DataWatcherObject getFloat(int data) {
		return new DataWatcherObject(c("c", data));
	}

	/**
	 * @return DataWatcherObject<Boolean>
	 */
	public static DataWatcherObject getBoolean(int data) {
		return new DataWatcherObject(c("i", data));
	}

	/**
	 * @return DataWatcherObject<String>
	 */
	public static DataWatcherObject getString(int data) {
		return new DataWatcherObject(c("d", data));
	}

	/**
	 * @return DataWatcherObject<IChatBaseComponent>
	 */
	public static DataWatcherObject getIChatBaseComponent(int data) {
		return new DataWatcherObject(c("e", data));
	}

	/**
	 * @return DataWatcherObject<Optional<IChatBaseComponent>>
	 */
	public static DataWatcherObject getOptionalIChatBaseComponent(int data) {
		return new DataWatcherObject(c("f", data));
	}

	/**
	 * @return DataWatcherObject<ItemStack>
	 */
	public static DataWatcherObject getItemStackt(int data) {
		return new DataWatcherObject(c("g", data));
	}

	/**
	 * @return DataWatcherObject<Optional<IBlockData>>
	 */
	public static DataWatcherObject getOptionalIBlockData(int data) {
		return new DataWatcherObject(c("h", data));
	}

	/**
	 * @return DataWatcherObject<ItemStack>
	 */
	public static DataWatcherObject getParticleParam(int data) {
		return new DataWatcherObject(c("j", data));
	}

	/**
	 * @return DataWatcherObject<Vector3f>
	 */
	public static DataWatcherObject getVector3f(int data) {
		return new DataWatcherObject(c("k", data));
	}

	/**
	 * @return DataWatcherObject<BlockPosition>
	 */
	public static DataWatcherObject getBlockPosition(int data) {
		return new DataWatcherObject(c("l", data));
	}

	/**
	 * @return DataWatcherObject<Optional<BlockPosition>>
	 */
	public static DataWatcherObject getOptionalBlockPosition(int data) {
		return new DataWatcherObject(c("m", data));
	}

	/**
	 * @return DataWatcherObject<EnumDirection>
	 */
	public static DataWatcherObject getEnumDirection(int data) {
		return new DataWatcherObject(c("n", data));
	}

	/**
	 * @return DataWatcherObject<Optional<UUID>>
	 */
	public static DataWatcherObject getOptionalUUID(int data) {
		return new DataWatcherObject(c("o", data));
	}

	/**
	 * @return DataWatcherObject<NBTTagCompound>
	 */
	public static DataWatcherObject getNBTTagCompound(int data) {
		return new DataWatcherObject(c("p", data));
	}

	/**
	 * @return DataWatcherObject<VillagerData>
	 */
	public static DataWatcherObject getVillagerData(int data) {
		return new DataWatcherObject(c("q", data));
	}

	/**
	 * @return DataWatcherObject<OptionalInt>
	 */
	public static DataWatcherObject getOptionalInt(int data) {
		return new DataWatcherObject(c("r", data));
	}

	/**
	 * @return DataWatcherObject<EnumDiEntityPoserection>
	 */
	public static DataWatcherObject getEntityPose(int data) {
		return new DataWatcherObject(c("s", data));
	}

	/**
	 * @return DataWatcherObject<?>
	 */
	public static DataWatcherObject get(int id, int data) {
		Object o = Ref.invokeNulled(Ref.method(a, "a", int.class), id);
		return new DataWatcherObject(Ref.invoke(o, Ref.method(o.getClass(), "a", int.class), data));
	}

	private static final UnsortedMap<String, Integer> ids = new UnsortedMap<>();
	static {
		if (TheAPI.isNewVersion()) {
			ids.put("a", 0);
			ids.put("b", 1);
			ids.put("c", 2);
			ids.put("d", 3);
			ids.put("e", 4);
			ids.put("f", 5);
			ids.put("g", 6);
			ids.put("i", 7);
			ids.put("k", 8);
			ids.put("l", 9);
			ids.put("m", 10);
			ids.put("n", 11);
			ids.put("o", 12);
			ids.put("h", 13);
			ids.put("p", 14);
			ids.put("j", 15);
			ids.put("q", 16);
			ids.put("r", 17);
			ids.put("s", 18);
		}
		ids.put("a", 0);
		ids.put("b", 1);
		ids.put("c", 2);
		ids.put("d", 3);
		ids.put("e", 4);
		ids.put("f", 5);
		ids.put("h", 6);
		ids.put("i", 7);
		ids.put("j", 8);
		ids.put("k", 9);
		ids.put("l", 10);
		ids.put("m", 11);
		ids.put("g", 12);
		ids.put("n", 13);
	}

	private static Object c(String d, int data) {
		return get(ids.get(d), data);
	}
}
