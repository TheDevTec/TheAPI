package me.Straiker123;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NMSAPI {
	
	private Class<?> ichat=Reflections.getNMSClass("IChatBaseComponent"),ichatser
			,iblockdata=Reflections.getNMSClass("IBlockData"),pos=Reflections.getNMSClass("BlockPosition")
			,world=Reflections.getNMSClass("World"),enumChat = Reflections.getNMSClass("ChatMessageType")
			,bWorld=Reflections.getBukkitClass("CraftWorld"),bPlayer=Reflections.getBukkitClass("entity.CraftPlayer")
			,EntityPlayer=Reflections.getNMSClass("EntityPlayer"),server=Reflections.getNMSClass("MinecraftServer"),b=Reflections.getNMSClass("Block")
			,cChunk=Reflections.getBukkitClass("CraftChunk"),cs=Reflections.getNMSClass("ChunkSection"),enumTitle,Containers=Reflections.getNMSClass("Containers")
			,particleEnum = Reflections.existNMSClass("EnumParticle") ? Reflections.getNMSClass("EnumParticle") : Reflections.getNMSClass("Particles");
	private Constructor<?> pDestroy,pTitle,pOutChat,pTab,pBlock,blockPos,pChunk,ChunkSection,chunkc
	,particle,pSpawn,pNSpawn,pLSpawn,pWindow;
	private Method getmat,getb,getc,gett,WorldHandle,PlayerHandle,sendPacket,ichatcon,getser,plist,block,IBlockData
	,worldset,Chunk,getblocks,setblock,setblockb,itemstack,entityM,livingentity,oldichatser;
	private Field pCon,tps;
	public NMSAPI() {
		try {
			particle=Reflections.getNMSClass("PacketPlayOutWorldParticles").getConstructors()[1];
		}catch(Exception e) {
			particle=Reflections.getNMSClass("Packet63WorldParticles").getConstructors()[1];
		}
		try {
		ichatser=ichat.getDeclaredClasses()[0];
		enumTitle=Reflections.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0];
		}catch(Exception oldversion) {
			oldichatser=Reflections.getMethod(Reflections.getNMSClass("ChatSerializer"),"a", String.class);
		}
		
		try {
			pDestroy=Reflections.getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		try {
			pWindow=Reflections.getNMSClass("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, ichat);
		} catch (Exception e) {
			try {
				pWindow=Reflections.getNMSClass("PacketPlayOutOpenWindow").getConstructor(int.class, Containers, ichat);
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
		try {
		pSpawn = Reflections.getNMSClass("PacketPlayOutSpawnEntity").getConstructor(Reflections.getNMSClass("Entity"),int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		pNSpawn = Reflections.getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(Reflections.getNMSClass("EntityHuman"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		pLSpawn=Reflections.getNMSClass("PacketPlayOutSpawnEntityLiving").getConstructor(Reflections.getNMSClass("EntityLiving"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		entityM=Reflections.getBukkitClass("entity.CraftEntity").getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		livingentity=Reflections.getBukkitClass("entity.CraftLivingEntity").getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			itemstack=Reflections.getBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
		getblocks=cs.getMethod("getBlocks");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		setblock=Reflections.getNMSClass("DataPaletteBlock").getMethod("setBlock", int.class,int.class,int.class,Object.class);
		} catch (Exception e) {

			try {
			setblock=Reflections.getNMSClass("DataPaletteBlock").getMethod("setBlock", int.class,int.class,int.class,iblockdata);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		try {
		setblockb=Reflections.getNMSClass("DataPaletteBlock").getMethod("b", int.class,int.class,int.class,Object.class);
		} catch (Exception e) {
		}
		try {
			chunkc=Reflections.getNMSClass("ChunkCoordIntPair").getConstructor(int.class,int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getser=Reflections.getNMSClass("MinecraftServer").getMethod("getServer");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ChunkSection=Reflections.getNMSClass("ChunkSection").getConstructor(int.class);
		} catch (Exception e) {
			try {
				ChunkSection=Reflections.getNMSClass("ChunkSection").getConstructor(int.class, boolean.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		try {
			Chunk=cChunk.getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getmat = iblockdata.getMethod("getMaterial");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getb = iblockdata.getMethod("getBlock");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getc = world.getMethod("getChunkAt", int.class,int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			gett=world.getMethod("getType", pos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pTitle=Reflections.getNMSClass("PacketPlayOutTitle").getConstructor(enumTitle,ichat,int.class,int.class,int.class);
		} catch (Exception e) {
			
		}
		try {
			pOutChat=Reflections.getNMSClass("PacketPlayOutChat").getConstructor(ichat,enumChat);
		} catch (Exception e) {
			try {
				pOutChat=Reflections.getNMSClass("PacketPlayOutChat").getConstructor(ichat,int.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		try {
			pTab=Reflections.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			WorldHandle=bWorld.getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PlayerHandle=bPlayer.getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pCon=EntityPlayer.getField("playerConnection");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			sendPacket=Reflections.getNMSClass("PlayerConnection").getMethod("sendPacket", Reflections.getNMSClass("Packet"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pBlock=Reflections.getNMSClass("PacketPlayOutBlockChange").getConstructor(Reflections.getNMSClass("IBlockAccess"),pos);
		} catch (Exception ea) {
			try {
				pBlock=Reflections.getNMSClass("PacketPlayOutBlockChange").getConstructor(Reflections.getNMSClass("World"),pos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			blockPos=Reflections.getNMSClass("BlockPosition").getConstructor(int.class,int.class,int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ichatcon=ichatser.getMethod("a", String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			tps=server.getDeclaredField("recentTps");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			plist=server.getDeclaredMethod("getPlayerList");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			block = b.getMethod("getById", int.class);
			IBlockData = b.getMethod("fromLegacyData", int.class);
		}catch(Exception e) {
			try {
				IBlockData = b.getMethod("getByCombinedId", int.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		try {
			worldset=world.getMethod("setTypeAndData", pos, iblockdata,int.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pChunk=Reflections.getNMSClass("PacketPlayOutMapChunk").getConstructor(Reflections.getNMSClass("Chunk"), int.class);
		} catch (Exception e) {
			try {
				pChunk=Reflections.getNMSClass("PacketPlayOutMapChunk").getConstructor(Reflections.getNMSClass("Chunk"), boolean.class, int.class);
			} catch (Exception e1) {
				//packet not exists
			}
		}
	}

	//return DataPaletteBlock<IBlockData>
	public Object getChunkSectionBlocks(Object ChunkSection) {
		try {
			return getblocks.invoke(ChunkSection);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setChunkSectionsBlocks(int chunksection,int x,int y,int z,Object IBlockData) {
		setChunkSectionsBlocks(getChunkSection(chunksection),x,y,z,IBlockData);
	}
	
	public void setChunkSectionsBlocks(Object chunksection,int x,int y,int z,Object IBlockData) {
		try {
			setblock.invoke(chunksection,x, y, z, IBlockData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setChunkSectionsBlocksByMethodB(Object chunksection,int x,int y,int z,Object IBlockData) {
		try {
			setblockb.invoke(chunksection,x, y, z, IBlockData);
		} catch (Exception e4) {
			//this method doesn't exist in older versions
			try {
			setblock.invoke(chunksection,x, y, z, IBlockData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object getChunkSection(int y) {
		try {
			return ChunkSection.newInstance(y);
		} catch (Exception e) {
			try {
			return ChunkSection.newInstance(y,true);
			} catch (Exception e2) {
				e2.printStackTrace();
				return null;
			}
		}
	}
	
	public me.Straiker123.Player getNMSPlayerAPI(Player p){
		return new me.Straiker123.Player(getPlayer(p));
	}
	
	public me.Straiker123.Player getNMSPlayerAPI(Object entityPlayer){
		return new me.Straiker123.Player(entityPlayer);
	}
	
	public Object getChunk(Chunk chunk) {
		return getChunk(getCraftChunk(chunk));
	}

	public Object getChunk(Object CraftChunk) {
		try {
			return Chunk.invoke(CraftChunk);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getCraftChunk(Chunk chunk) {
		return Reflections.getBukkitClass("CraftChunk").cast(chunk);
	}

	public Object getChunkCoordIntPair(int x, int z) {
		try {
		return chunkc.newInstance(x,z);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getChunkCoordIntPair(Chunk chunk) {
		try {
		return chunkc.newInstance(chunk.getX(),chunk.getZ());
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Class<?> getMinecraftServer() {
		try {
			return server;
		}catch(Exception e){
			return null;
		}
	}

	public ArrayList<String> getOnlinePlayersNames() {
		ArrayList<String> a = new ArrayList<String>();
		
		List<?> list = (List<?>)Reflections.get(getPlayerList(),"players");
		for(Object s : list) {
			try {
				a.add(EntityPlayer.getMethod("getName").invoke(s).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return a;
	}

	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> a = new ArrayList<Player>();
		List<?> list = (List<?>)Reflections.get(getPlayerList(),"players");
		for(Object s : list) {
			try {
				a.add((Player)EntityPlayer.getMethod("getBukkitEntity").invoke(s));
			} catch (Exception e) {
				e.printStackTrace();
			}}
		return a;
	}
	
	public Object getPlayerList() {
		try {
			return plist.invoke(getServer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getServer() {
		try {
			return getser.invoke(server);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[] getServerTPS() {
		try {
			
			return (double[])tps.get(getServer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static enum TitleAction {
		ACTIONBAR,
		CLEAR,
		RESET,
		SUBTITLE,
		TITLE
	}
	
	public static enum ChatType {
		CHAT,
		GAME_INFO,
		SYSTEM
	}

	public Object getMaterial(Object IBlockData) {
		try {
			return getmat.invoke(IBlockData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getBlock(Object IBlockData) {
		try {
			return getb.invoke(IBlockData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getItemStack(org.bukkit.inventory.ItemStack stack) {
		try {
			return itemstack.invoke(null, stack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc) {
		return create(effect,(float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),(float)1,1,null,0,0,0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleData data) {
		return create(effect,(float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),(float)1,1,data,0,0,0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, int amount, ParticleData data) {
		return create(effect,x,y,z,1,amount,data,0,0,0);
	}

	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleData data) {
		return create(effect,x,y,z,1,1,data,0,0,0);
	}
	
	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect,x,y,z,speed,amount,null,Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect,x,y,z,speed,amount,null,color.getValueX(), color.getValueY(), color.getValueZ());
	}
	
	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect,x,y,z,speed,1,null,Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect,x,y,z,speed,1,null,color.getValueX(), color.getValueY(), color.getValueZ());
	}
	
	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect,x,y,z,1,1,null,Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect,x,y,z,1,1,null,color.getValueX(), color.getValueY(), color.getValueZ());
	}
	
	public Object getPacketPlayOutWorldParticles(Particle effect, Location loc, ParticleColor color) {
		if (effect == Particle.REDSTONE && color.getRed() == 0) {
			return create(effect,(float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),1,1,null,Float.MIN_NORMAL, color.getValueY(), color.getValueZ());
		}
		return create(effect,(float)loc.getX(),(float)loc.getY(),(float)loc.getZ(),1,1,null,color.getValueX(), color.getValueY(), color.getValueZ());
	}
	
	public Object getPacketPlayOutWorldParticles(Particle effect, float x, float y, float z, float speed, int amount, ParticleData data) {
		if (speed < 0)
			throw new IllegalArgumentException("The speed is lower than 0");
		if (amount < 0)
			throw new IllegalArgumentException("The amount is lower than 0");
		return create(effect,x,y,z,speed,amount,data,0,0,0);
	}
	
	private Object create(Particle effect, float x, float y, float z, float speed, int amount, ParticleData data
			, float floatx, float floaty, float floatz) {
		try { 
			 Object packet = particle.newInstance(particleEnum);
			if (Integer.parseInt(TheAPI.getServerVersion().split("_")[1]) < 8) {
				String name = effect.name();
				if (data != null) {
					name += data.getPacketDataString();
				}
				Reflections.setField(packet, "a", name);
			}else{
				Reflections.setField(packet, "a", particleEnum.getField(effect.name()).get(null));
				Reflections.setField(packet, "j", false);
				if (data != null) {
					int[] packetData = data.getPacketData();
					Reflections.setField(packet, "k", effect == Particle.ITEM_CRACK ? packetData : new int[] { packetData[0] | (packetData[1] << 12) });
			}}
			Reflections.setField(packet, "b", (float) x);
			Reflections.setField(packet, "c", (float) y);
			Reflections.setField(packet, "d", (float) z);
			Reflections.setField(packet, "e", floatx);
			Reflections.setField(packet, "f", floaty);
			Reflections.setField(packet, "g", floatz);
			Reflections.setField(packet, "h", speed);
			Reflections.setField(packet, "i", amount);
			return packet;
	}catch(Exception e) {
		return null;
	}}

	public Object getChunk(Object World, int x, int z) {
		 try {
			return getc.invoke(World, x,z);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getIBlockData(Object World, Object blockPosition) {
		try {
			return gett.invoke(World, blockPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getIBlockData(Object World, int x, int y, int z) {
		return getIBlockData(World,getBlockPosition(x, y, z));
	}

	public Object getIBlockData(World world, Object blockPosition) {
		return getIBlockData(getWorld(world),blockPosition);
	}

	public Object getIBlockData(World world, int x, int y, int z) {
		return getIBlockData(getWorld(world),getBlockPosition(x, y, z));
	}
	
	public Object getChunk(World world, int x, int z) {
		return getChunk(getWorld(world),x,z);
	}

	public Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent) {
		return getPacketPlayOutTitle(action, IChatBaseComponent,10,20,10);
	}
	
	public Object getPacketPlayOutOpenWindow(int id, String container, String text) {
		return getPacketPlayOutOpenWindow(id,container,getIChatBaseComponentText(text));
	}

	public Object getPacketPlayOutOpenWindow(int id, String container, Object IChatBaseComponent) {
		
		try {
			return pWindow.newInstance(id,container,IChatBaseComponent);
		} catch (Exception es) {
			try {
				return pWindow.newInstance(id,Containers.getField(container).get(null),IChatBaseComponent);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public Object getPacketPlayOutTitle(TitleAction action, Object IChatBaseComponent, int fadeIn, int stay, int fadeOut) {
		try {
			return pTitle.newInstance(enumTitle.getField(action.name()).get(null),IChatBaseComponent,fadeIn,stay,fadeOut);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getPacketPlayOutTitle(TitleAction action, String text, int fadeIn, int stay, int fadeOut) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text),fadeIn,stay,fadeOut);
	}
	
	public Object getPacketPlayOutTitle(TitleAction action, String text) {
		return getPacketPlayOutTitle(action, getIChatBaseComponentText(text),10,20,10);
	}
	
	public Object getPacketPlayOutMapChunk(Object Chunk, int workers) {
		try {
			return pChunk.newInstance(Chunk,workers);
		} catch (Exception e) {

			try {
			return pChunk.newInstance(Chunk,true,workers);
			} catch (Exception e1) {
			e.printStackTrace();
			return null;
		}}
	}

	public Object getPacketPlayOutMapChunk(World world, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z),workers);
	}

	public Object getPacketPlayOutMapChunk(World world, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(world, x, z),value,workers);
	}

	public Object getPacketPlayOutMapChunk(Object World, int x, int z, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z),workers);
	}

	public Object getPacketPlayOutMapChunk(Object World, int x, int z, boolean value, int workers) {
		return getPacketPlayOutMapChunk(getChunk(World, x, z),value,workers);
	}
	
	public Object getPacketPlayOutMapChunk(Object Chunk, boolean value, int workers) {
		try {
			return pChunk.newInstance(Chunk,workers);
		} catch (Exception e) {

			try {
			return pChunk.newInstance(Chunk,value,workers);
			} catch (Exception e1) {
			e.printStackTrace();
			return null;
		}}
	}
	
	public void refleshBlock(World w, Object blockposition, Object oldBlock, Object newBlock) {
		Reflections.invoke(getWorld(w),Reflections.getMethod(world, "notify", pos,iblockdata,iblockdata,int.class), blockposition,oldBlock,newBlock,3);
	}
	
	public Object getPacketPlayOutChat(ChatType type, Object IChatBaseComponent) {
		try {
			return pOutChat.newInstance(IChatBaseComponent,enumChat.getField(type.name()).get(null));
		} catch (Exception e) {
			try {
				return pOutChat.newInstance(IChatBaseComponent,2); //only actionbar, 1.7.10
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
	}
	
	public Object getPacketPlayOutChat(ChatType type, String text) {
		return getPacketPlayOutChat(type,getIChatBaseComponentText(text));
	}
	
	public Object getType(World w, int x, int y, int z) {
		return Reflections.invoke(getWorld(w), Reflections.getMethod(world, "getType",pos),getBlockPosition(x, y, z));
	}

	public int[] setBlock(World world, int x, int y, int z, Material material, int data, boolean applyPsychics) {
		Object old = null;
    	try {
    		old=getType(world,x,y,z);
    		worldset.invoke(getWorld(world), getBlockPosition(x, y, z), getIBlockData(material, data),applyPsychics ?3 : 2);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	refleshBlock(world, getBlockPosition(x, y, z), old, getIBlockData(material, data));
		return new int[] {x >> 4, z >> 4};
	}
	
	public int[] setBlock(World world, int x, int y, int z, Material material, boolean applyPsychics) {
		return setBlock(world, x, y, z, material, 0, applyPsychics);
	}

	public int[] setBlock(Location loc, Material material, int data, boolean applyPsychics) {
    	return setBlock(loc.getWorld(),loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),material,data,applyPsychics);
	}
	
	public Object getIBlockData(Material material) {
		return getIBlockData(material,0);
	}
	
	@SuppressWarnings("deprecation")
	public Object getIBlockData(Material material, int data) {
		try {
			//1.13+ only
			Object o =Reflections.getNMSClass("block.data.CraftBlockData").cast(Bukkit.createBlockData(material));
			return o.getClass().getMethod("getState").invoke(o);
		}catch(Exception erera) {
		try{
			return IBlockData.invoke(block.invoke(null, material.getId()), data);
		}catch(Exception e) {
			try{
			return IBlockData.invoke(null,material.getId() + (data << 12));
		}catch(Exception e1) {
			return null;
		}}}
	}

	public Object getPacketPlayOutEntityDestroy(int... id) {
		try {
			return pDestroy.newInstance(id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Object getEntity(Entity entity) {
		try {
			Object craft = Reflections.getBukkitClass("entity.CraftEntity").cast(entity);
			return entityM.invoke(craft);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getEntityLiving(LivingEntity entity) {
		try {
			Object craft = Reflections.cast(Reflections.getBukkitClass("entity.CraftLivingEntity"),entity);
			return livingentity.invoke(craft);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Object getPacketPlayOutSpawnEntity(Object entity, int id) {
		try {
			return pSpawn.newInstance(entity,id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getPacketPlayOutNamedEntitySpawn(Object Player) {
		try {
			return pNSpawn.newInstance(Player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getPacketPlayOutSpawnEntityLiving(Object entityLiving) {
		try {
			return pLSpawn.newInstance(entityLiving);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getPacketPlayOutPlayerListHeaderFooter(Object headerIChatBaseComponent, Object footerIChatBaseComponent) {
		try {
			Object packet = pTab.newInstance(new Object[0]);
			Field aField = null;
			Field bField = null;
			try {
				aField = packet.getClass().getDeclaredField("header");
			    bField = packet.getClass().getDeclaredField("footer");
			} catch (Exception e) {
			   aField = packet.getClass().getDeclaredField("a");
			   bField = packet.getClass().getDeclaredField("b");
			}
			   aField.setAccessible(true);
			   aField.set(packet, headerIChatBaseComponent);
			bField.setAccessible(true);
			bField.set(packet, footerIChatBaseComponent);
			return packet;
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
	}
	
	public Object getPacketPlayOutPlayerListHeaderFooter(String header, String footer) {
		return getPacketPlayOutPlayerListHeaderFooter(getIChatBaseComponentText(header),getIChatBaseComponentText(footer));
	}
	
	public Object getCraftWorld(World world) {
		return bWorld.cast(world);
	}

	public Object getWorld(World world) {
		return getWorld(getCraftWorld(world));
	}
	
	public Object getWorld(Object craftWorld) {
		try {
			return WorldHandle.invoke(craftWorld);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getCraftPlayer(Player player) {
		return bPlayer.cast(player);
	}
	
	public Object getPlayer(Player player) {
		return getPlayer(getCraftPlayer(player));
	}
	
	public Object getPlayer(Object CraftPlayer) {
		try {
			return PlayerHandle.invoke(CraftPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getPlayerConnection(Object Player) {
		try {
			return pCon.get(Player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getPlayerConnection(Player player) {
		try {
			return getPlayerConnection(getPlayer(player));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendPacket(Object PlayerConnection, Object packet) {
		try {
			sendPacket.invoke(PlayerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(Player player, Object packet) {
		try {
			sendPacket(getPlayerConnection(player),packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getPacketPlayOutBlockChange(Object World, int x,int y,int z) {
		return getPacketPlayOutBlockChange(World,getBlockPosition(x,y,z));
	}

	public Object getPacketPlayOutBlockChange(World world, Object BlockPosition) {
		return getPacketPlayOutBlockChange(getWorld(world),BlockPosition);
	}
	
	public Object getPacketPlayOutBlockChange(World world, int x,int y,int z) {
		return getPacketPlayOutBlockChange(getWorld(world),getBlockPosition(x,y,z));
	}

	public Object getPacketPlayOutBlockChange(Object World, Object BlockPosition) {
		try {
			return pBlock.newInstance(World,BlockPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getBlockPosition(int x,int y,int z) {
		try {
			return blockPos.newInstance(x,y,z);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object getIChatBaseComponentText(String text) {
		return getIChatBaseComponentJson("{\"text\":\"" + text+ "\"}");
	}
	
	public Object getIChatBaseComponentJson(String json) {
		try {
			if(oldichatser!=null)
				return oldichatser.invoke(null, json);
			return ichatcon.invoke(null, json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
