package me.devtec.theapi.blocksapi.schematic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Axis;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.FaceAttachable.AttachedFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.material.Colorable;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.reflections.Ref;

public class InitialSerializedBlock implements SerializedBlock {
	private static final long serialVersionUID = 98117975197494498L;
	
	protected TheMaterial material;
	protected Map<String, Object> extra;
	
	@Override
	public SerializedBlock serialize(TheMaterial material, Map<String, Object> extra) {
		this.material=material;
		this.extra=extra;
		return this;
	}

	@Override
	public String getAsString() {
		Map<String, Object> map = new HashMap<>();
		map.put("material", material.toString());
		if(!extra.isEmpty())
		map.put("state", extra);
		return Writer.write(map).replace(System.lineSeparator(), "");
	}
	
	public TheMaterial getType() {
		return material;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SerializedBlock fromString(String string) {
		values.clear();
		Map<String, Object> map = (Map<String, Object>)Reader.read(string);
		extra=(Map<String, Object>)map.get("state");
		material=TheMaterial.fromString((String)map.get("material"));
		return this;
	}
	
	@Override
	public SerializedBlock apply(Position pos) {
		pos.setType(material);
		if(extra!=null && !extra.isEmpty()) {
			Block b = pos.getBlock();
			if(TheAPI.isNewVersion()) {
				BlockData d = b.getBlockData();
					if(d instanceof Orientable && extra.containsKey("orient")) {
						Orientable dir = (Orientable)d;
						dir.setAxis(Axis.valueOf((String)extra.get("orient")));
					}
					if(d instanceof Rotatable && extra.containsKey("face")) {
						Rotatable dir = (Rotatable)d;
						dir.setRotation(BlockFace.valueOf((String)extra.get("face")));
					}
					if(d instanceof Directional && extra.containsKey("face")) {
						Directional dir = (Directional)d;
						dir.setFacing(BlockFace.valueOf((String)extra.get("face")));
					}
					if(d instanceof Ageable && extra.containsKey("age")) {
						Ageable dir = (Ageable)d;
						dir.setAge((int)(double)extra.get("age"));
					}
					if(d instanceof Stairs && extra.containsKey("sshape")) {
						Stairs dir = (Stairs)d;
						dir.setShape(org.bukkit.block.data.type.Stairs.Shape.valueOf((String)extra.get("sshape")));
					}
					if(d instanceof Rail && extra.containsKey("rshape")) {
						Rail dir = (Rail)d;
						dir.setShape(Shape.valueOf((String)extra.get("rshape")));
					}
					if(d instanceof AnaloguePowerable && extra.containsKey("power")) {
						AnaloguePowerable dir = (AnaloguePowerable)d;
						dir.setPower((int)(double)extra.get("power"));
					}
					if(d instanceof Attachable && extra.containsKey("attach")) {
						Attachable dir = (Attachable)d;
						dir.setAttached((boolean)extra.get("attach"));
					}
					if(d instanceof Bisected && extra.containsKey("half")) {
						Bisected dir = (Bisected)d;
						dir.setHalf(Half.valueOf((String)extra.get("half")));
					}
					if(TheAPI.isNewerThan(15)) {
					if(d instanceof FaceAttachable && extra.containsKey("fattach")) {
						FaceAttachable dir = (FaceAttachable)d;
						dir.setAttachedFace(AttachedFace.valueOf((String)extra.get("fattach")));
					}}
					if(d instanceof Levelled && extra.containsKey("level")) {
						Levelled dir = (Levelled)d;
						dir.setLevel((int)(double)extra.get("level"));
					}
					if(d instanceof Lightable && extra.containsKey("lit")) {
						Lightable dir = (Lightable)d;
						dir.setLit((boolean)extra.get("lit"));
					}
					if(d instanceof MultipleFacing && extra.containsKey("mface")) {
						MultipleFacing dir = (MultipleFacing)d;
						@SuppressWarnings("unchecked")
						Map<String, Boolean> map = (Map<String, Boolean>)Reader.read((String)extra.get("mface"));
						for(Entry<String, Boolean> face : map.entrySet())
							dir.setFace(BlockFace.valueOf(face.getKey()), face.getValue());
					}
					if(d instanceof Openable && extra.containsKey("open")) {
						Openable dir = (Openable)d;
						dir.setOpen((boolean)extra.get("open"));
					}
					if(d instanceof Powerable && extra.containsKey("power")) {
						Powerable dir = (Powerable)d;
						dir.setPowered((boolean)extra.get("power"));
					}
					if(d instanceof Rail && extra.containsKey("rail")) {
						Rail dir = (Rail)d;
						dir.setShape(Shape.valueOf((String)extra.get("rail")));
					}
					if(d instanceof Snowable && extra.containsKey("snow")) {
						Snowable dir = (Snowable)d;
						dir.setSnowy((boolean)extra.get("snow"));
					}
					if(d instanceof Waterlogged && extra.containsKey("water")) {
						Waterlogged dir = (Waterlogged)d;
						dir.setWaterlogged((boolean)extra.get("water"));
					}
					if(d instanceof Leaves) {
						Leaves dir = (Leaves)d;
						dir.setPersistent((boolean)extra.get("leaves"));
					}
					pos.setBlockData(d);
			}else {
				BlockState state = b.getState();
				if(state.getData() instanceof org.bukkit.material.Colorable)
					((Colorable)state.getData()).setColor(DyeColor.valueOf((String)extra.get("color")));
				if(state.getData() instanceof org.bukkit.material.Directional)
					((Directional)state.getData()).setFacing(BlockFace.valueOf((String)extra.get("face")));
				if(state.getData() instanceof org.bukkit.material.Attachable)
					((Attachable)state.getData()).setAttached((boolean)extra.get("attach"));
				if(state.getData() instanceof org.bukkit.material.Openable)
					((Openable)state.getData()).setOpen((boolean)extra.get("open"));
				if(state.getData() instanceof org.bukkit.material.Leaves)
					((org.bukkit.material.Leaves)state.getData()).setDecayable((boolean)extra.get("leaves"));
				pos.setState(state);
			}
			String n = (String)extra.get("nbt");
			if(n!=null) {
				Object nbt = Ref.invokeNulled(parse, n);
				Ref.invoke(nbt, setInt, "x", pos.getBlockX());
				Ref.invoke(nbt, setInt, "y", pos.getBlockY());
				Ref.invoke(nbt, setInt, "z", pos.getBlockZ());
				Ref.invoke(SerializedBlock.getState(pos), load, pos.getIBlockData(), nbt);
			}
		}
		Position.updateBlockAt(pos);
		Position.updateLightAt(pos);
		return this;
	}
	private static Method load = Ref.method(Ref.nms("TileEntity"), "load", Ref.nms("IBlockData"), Ref.nms("NBTTagCompound"))!=null?
			Ref.method(Ref.nms("TileEntity"), "load", Ref.nms("IBlockData"), Ref.nms("NBTTagCompound")):
				Ref.method(Ref.nms("TileEntity"), "a", Ref.nms("NBTTagCompound"));
	private static Method setInt = Ref.method(Ref.nms("NBTTagCompound"), "setInt", String.class, int.class);
	
	private static Method parse = Ref.method(Ref.nms("MojangsonParser"), "parse", String.class);
}
