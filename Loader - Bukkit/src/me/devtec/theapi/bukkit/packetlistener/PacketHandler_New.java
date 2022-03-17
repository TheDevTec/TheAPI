package me.devtec.theapi.bukkit.packetlistener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import me.devtec.shared.Ref;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.theapi.bukkit.BukkitLoader;

@SuppressWarnings("unchecked")
public class PacketHandler_New implements PacketHandler<Channel> {
	private static final Class<?> login = Ref.nmsOrOld("network.protocol.login.PacketLoginInStart","PacketLoginInStart");
	private static final Class<?> postlogin = Ref.nmsOrOld("network.protocol.login.PacketLoginOutSuccess","PacketLoginOutSuccess");
	static final Field f = Ref.field(login, "a");
	static final Field fPost = Ref.field(postlogin, "a");
	private final Map<String, Channel> channelLookup = new HashMap<>();
	private List<ChannelFuture> networkManagers;
	private final List<Channel> serverChannels = new ArrayList<>();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private Object serverConnection;
	private ChannelInitializer<Channel> beginInitProtocol, endInitProtocol;
	protected volatile boolean closed;
	
	public PacketHandler_New(boolean lateBind) {
		serverConnection = Ref.invoke(BukkitLoader.getNmsProvider().getMinecraftServer(),"getServerConnection");
		if(serverConnection==null) //modded server
		for(Field f : Ref.getAllFields(BukkitLoader.getNmsProvider().getMinecraftServer().getClass()))
			if(f.getType()==Ref.nmsOrOld("server.network.ServerConnection","ServerConnection")) {
				serverConnection=Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), f);
				break;
			}
		if(serverConnection==null)return;
		if(lateBind) {
			while(!(boolean)Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), Ref.isOlderThan(9)?"Q":(Ref.isOlderThan(11)?"P":Ref.isOlderThan(13)?"Q":Ref.isOlderThan(14)?"P":Ref.isOlderThan(17)?"hasTicked":"ah")))
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
		}
		new Tasker() {
			public void run() {
				registerChannelHandler();
				registerPlayers();
			}
		}.runLater(1);
	}

	private void createServerChannelHandler() {
		endInitProtocol = new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) {
				try {
					synchronized (networkManagers) {
						if (!closed) {
							PacketInterceptor interceptor = new PacketInterceptor(null);
							channel.eventLoop().submit(() -> {
								if(channel.pipeline().names().contains("InjectorTA"))
									channel.pipeline().remove("InjectorTA");
								channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
								return interceptor;
							});
						}
					}
				} catch (Exception e) {
				}
			}

		};
		beginInitProtocol = new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) {
				channel.pipeline().addLast(endInitProtocol);
			}

		};
		serverChannelHandler = new ChannelInHandler();
	}
	
	@Sharable
	public class ChannelInHandler extends ChannelInboundHandlerAdapter {
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			Channel channel = (Channel) msg;
			channel.pipeline().addFirst(beginInitProtocol);
			ctx.fireChannelRead(channel);
		}
	}

	private void registerChannelHandler() {
		if(Ref.isNewerThan(16))
			networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "f"));
		else
		networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "listeningChannels")!=null?Ref.get(serverConnection, "listeningChannels"):Ref.get(serverConnection, "g"));
		if(networkManagers==null) { //modded server
			for(Field f : Ref.getAllFields(Ref.nmsOrOld("server.network.ServerConnection","ServerConnection")))
				if(java.util.List.class==f.getType()){
					networkManagers=(java.util.List<ChannelFuture>) Ref.get(serverConnection, f);
					break;
				}
			}
		if(networkManagers==null)return;
		if(networkManagers.isEmpty()) {
			networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "f")!=null?Ref.get(serverConnection, "f"):Ref.get(serverConnection, "listeningChannels"));
			if(networkManagers==null) { //modded server
				for(Field f : Ref.getAllFields(Ref.nmsOrOld("server.network.ServerConnection","ServerConnection")))
					if(java.util.List.class==f.getType()){
						networkManagers=(java.util.List<ChannelFuture>) Ref.get(serverConnection, f);
						break;
					}
				}
		}
		if(networkManagers==null)return;
		createServerChannelHandler();
		for (Object item : networkManagers) {
			if (!ChannelFuture.class.isInstance(item))continue;
			Channel serverChannel = ((ChannelFuture) item).channel();
			serverChannels.add(serverChannel);
			serverChannel.pipeline().addFirst(serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (serverChannelHandler == null)return;
		for (Channel serverChannel : serverChannels) 
			serverChannel.eventLoop().execute(() -> {
					try {
					serverChannel.pipeline().remove(serverChannelHandler);
					}catch(Exception err) {}
				});
		serverChannels.clear();
	}

	private void registerPlayers() {
		for (Player player : Bukkit.getOnlinePlayers())
			add(player);
	}

	public void add(Player player) {
		injectChannelInternal(player, get(player));
	}

	private PacketInterceptor injectChannelInternal(Player a, Channel channel) {
		if (channel == null)return null;
		try {
			PacketInterceptor interceptor = new PacketInterceptor(a.getName());
			channel.eventLoop().submit(() -> {
				if(channel.pipeline().names().contains("InjectorTA"))
					channel.pipeline().remove("InjectorTA");
				channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
				return interceptor;
			});
			return interceptor;
		} catch (Exception e) {
			return null;
		}
	}

	public Channel get(Player player) {
		Channel channel = channelLookup.get(player.getName());
		if (channel == null) {
			Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider().getConnectionNetwork(BukkitLoader.getNmsProvider().getPlayerConnection(player)));
			if(get==null)
				return null;
			channelLookup.put(player.getName(), channel = (Channel) get);
		}
		return channel;
	}

	public void remove(Channel channel) {
		if (channel == null)return;
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				String owner = null;
				for(Entry<String, Channel> s : channelLookup.entrySet())
					if(s.getValue().equals(channel)) {
						owner=s.getKey();
						break;
					}
				channelLookup.remove(owner);
				if(channel.pipeline().names().contains("InjectorTA"))
				channel.pipeline().remove("InjectorTA");
			}
		});
	}

	public boolean has(Channel channel) {
		if (channel == null)return false;
		try {
			return channel.pipeline().get("InjectorTA") != null;
		} catch (Exception e) {
			return false;
		}
	}

	public final void close() {
		if (!closed) {
			closed = true;
			for (Channel channel : channelLookup.values()){
				channel.eventLoop().execute(new Runnable() {
					public void run() {
						if(channel.pipeline().names().contains("InjectorTA"))
							channel.pipeline().remove("InjectorTA");
					}
				});
			}
			channelLookup.clear();
			unregisterChannelHandler();
		}
	}

	public final class PacketInterceptor extends ChannelDuplexHandler {
		String player;

		public PacketInterceptor(String player) {
			this.player=player;
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			final Channel channel = ctx.channel();
			synchronized (msg) {
				if (msg.getClass()==login) {
					player=((GameProfile) Ref.get(msg, f)).getName();
					channelLookup.put(player, channel);
				}
				try {
					msg = PacketManager.call(player, msg, channel, PacketType.PLAY_IN);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msg != null)
					super.channelRead(ctx, msg);
			}
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			final Channel channel = ctx.channel();
			synchronized (msg) {
				if (player==null && msg.getClass()==postlogin) { //ProtocolLib cancelled packets
					player=((GameProfile) Ref.get(msg, fPost)).getName();
					channelLookup.put(player, channel);
				}
				try {
					msg = PacketManager.call(player, msg, channel, PacketType.PLAY_OUT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (msg != null)
					super.write(ctx, msg, promise);
			}
		}
	}
	
	@Override
	public void send(Channel channel, Object packet) {
		if(channel==null||packet==null)return;
		channel.writeAndFlush(packet);
	}
}