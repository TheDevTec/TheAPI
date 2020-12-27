package me.devtec.theapi.blocksapi.schematic;

import java.util.Map;

import org.bukkit.block.Biome;

import me.devtec.theapi.blocksapi.schematic.construct.SerializedBlock;
import me.devtec.theapi.utils.TheMaterial;
import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;

public class AsyncSerializedBlock implements SerializedBlock {
	private static final long serialVersionUID = 98117975197494498L;
	
	protected TheMaterial material;
	protected Biome biome;
	protected byte data;
	
	@Override
	public SerializedBlock serialize(TheMaterial material, Biome biome, byte data) {
		this.material=material;
		this.biome=biome;
		this.data=data;
		return this;
	}

	@Override
	public String getAsString() {
		Map<String, Object> writer = new UnsortedMap<>();
		writer.put("a", material);
		writer.put("b", biome);
		writer.put("c", data);
		return Writer.write(writer);
	}

	@Override
	public SerializedBlock fromString(String string) {
		@SuppressWarnings("unchecked")
		Map<String, Object> deserialized = (Map<String, Object>) Reader.read(string);
		material=(TheMaterial)deserialized.get("a");
		biome=(Biome)deserialized.get("b");
		data=(byte)(double)deserialized.get("c");
		return this;
	}
	
}
