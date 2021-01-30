package me.devtec.theapi.blocksapi.schematic;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.reflections.Ref;
import net.minecraft.server.v1_16_R3.EnumBlockRotation;

public class InitialSerializedBlock implements SerializedBlock {
	private static final long serialVersionUID = 98117975197494498L;
	
	protected TheMaterial material;
	protected String face;
	protected Map<String, Object> extra;
	
	@Override
	public SerializedBlock serialize(TheMaterial material, String face, Map<String, Object> extra) {
		this.material=material;
		this.face=face;
		this.extra=extra;
		return this;
	}

	@Override
	public String getAsString() {
		Map<String, Object> map = new HashMap<>();
		map.put("data", face);
		map.put("material", material.toString());
		map.put("state", extra);
		return Writer.write(map);
	}
	
	public TheMaterial getType() {
		return material;
	}
	
	public String getFace() {
		return face;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SerializedBlock fromString(String string) {
		Map<String, Object> map = (Map<String, Object>)Reader.read(string);
		face=(String) map.get("data");
		extra=(Map<String, Object>)map.get("state");
		material=TheMaterial.fromString((String)map.get("material"));
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SerializedBlock apply(Position pos) {
		BlocksAPI.set(pos, material);
		if(!extra.isEmpty()) {
			Object dir = SerializedBlock.getState(pos);
			if(dir!=null) {
				if(chest.isInstance(dir)) {
					List<Object> items = (List<Object>) Ref.get(dir, "items");
					int i = 0;
					for(ItemStack o : (Collection<ItemStack>)extra.get("items"))
						items.set(i++,Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", o));
					Ref.set(dir, "items", items);
					Ref.set(dir, "opened", extra.get("open"));
				}
				if(banner.isInstance(dir)) {
					List<Object> nbt = (List<Object>) Ref.newInstance(Ref.constructor(Ref.nms("NBTTagList")));
					for(Map<String, Double> o : (Collection<Map<String, Double>>)extra.get("pattern"))
						for(Entry<String, Double> e : o.entrySet()) {
							Object n = Ref.newInstance(Ref.constructor(Ref.nms("NBTTagCompound")));
							Ref.invoke(n, pattern_string, "Pattern", e.getKey());
							Ref.invoke(n, pattern_int, "Color", e.getValue().intValue());
							nbt.add(n);
						}
					Ref.set(dir, "patterns", nbt);
					Ref.set(dir, "color", Ref.getNulled(Ref.nms("EnumColor"), (String)extra.get("color")));
					TheAPI.bcMsg(Ref.get(dir, Ref.field(Ref.nms("TileEntity"), "c")));
					Ref.set(dir, "c", Ref.invoke(Ref.get(dir, "c"), Ref.findMethodByName(Ref.nms("IBlockDataHolder"), "set"), EnumBlockRotation.CLOCKWISE_90, 15));
				}
			}
		}
		return this;
	}
	
	private static Method set = Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "put", Object.class, Object.class),
			pattern_string=Ref.method(Ref.nms("NBTTagCompound"), "setString", String.class, String.class), pattern_int=Ref.method(Ref.nms("NBTTagCompound"), "setString", String.class, int.class);
	
	static {
		if(set==null)
			set = Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "put", Object.class, Object.class);
	}
	
}
