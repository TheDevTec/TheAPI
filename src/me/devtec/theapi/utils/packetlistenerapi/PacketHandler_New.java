package me.devtec.theapi.utils.packetlistenerapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.reflections.Ref;

@SuppressWarnings("unchecked")
public class PacketHandler_New implements PacketHandler<Channel> {
	private static Class<?> login = Ref.nmsOrOld("network.protocol.login.PacketLoginInStart","PacketLoginInStart");
	private Map<String, Channel> channelLookup = new HashMap<>();
	private List<ChannelFuture> networkManagers;
	private List<Channel> serverChannels = new ArrayList<>();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private Object serverConnection;
	private ChannelInitializer<Channel> beginInitProtocol, endInitProtocol;
	protected volatile boolean closed;

	public PacketHandler_New(boolean lateBind) {
		serverConnection = Ref.invoke(Ref.server(),"getServerConnection");
		if(serverConnection==null) //modded server
		for(Field f : Ref.getAllFields(Ref.server().getClass()))
			if(f.getType()==Ref.nmsOrOld("server.network.ServerConnection","ServerConnection")) {
				serverConnection=Ref.get(Ref.server(), f);
				break;
			}
		if(serverConnection==null)return;
		if(lateBind) {
			while(!(boolean)Ref.get(Ref.server(), TheAPI.isOlderThan(9)?"Q":(TheAPI.isOlderThan(11)?"P":TheAPI.isOlderThan(13)?"Q":TheAPI.isOlderThan(14)?"P":TheAPI.isOlderThan(17)?"hasTicked":"ah")))
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			new Tasker() {
				public void run() {
					registerChannelHandler();
					registerPlayers();
				}
			}.runLater(1);
		}else
			new Tasker() {
				public void run() {
					registerChannelHandler();
					registerPlayers();
				}
			}.runLater(1);
	}

	private void createServerChannelHandler() {
		endInitProtocol = new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) throws Exception {
				try {
					synchronized (networkManagers) {
						if (!closed) {
							channel.eventLoop().submit(() -> {
								PacketInterceptor interceptor = new PacketInterceptor(null); //add new hook
								channel.eventLoop().execute(() -> {
										if(channel.pipeline().names().contains("InjectorTA")) {
											TheAPI.bcMsg("already reg");
											channel.pipeline().remove("InjectorTA"); //remove old instance - reload of server?
										}
										if(channel.pipeline().names().contains("packet_handler"))
											channel.pipeline().addBefore("packet_handler","InjectorTA", interceptor);
										else
											channel.pipeline().addBefore("encoder","InjectorTA", interceptor);
									});
								return interceptor;
							});
						}
					}
				} catch (Exception e) {
				}
			}

		};
		beginInitProtocol = new ChannelInitializer<Channel>() {
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(endInitProtocol);
			}

		};

		serverChannelHandler = new ChannelInboundHandlerAdapter() {
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				Channel channel = (Channel) msg;
				channel.pipeline().addFirst(beginInitProtocol);
				ctx.fireChannelRead(channel);
			}

		};
	}

	private void registerChannelHandler() {
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
			serverChannel.eventLoop().execute(new Runnable() {
				public void run() {
					try {
					serverChannel.pipeline().remove(serverChannelHandler);
					}catch(Exception err) {}
				}
			});
		serverChannels.clear();
	}

	private void registerPlayers() {
		for (Player player : TheAPI.getOnlinePlayers())
			add(player);
	}

	public void add(Player player) {
		injectChannelInternal(player, get(player));
	}

	private PacketInterceptor injectChannelInternal(Player a, Channel channel) {
		if (channel == null)return null;
		try {
			if(channel.pipeline().names().contains("InjectorTA"))
				return (PacketInterceptor) channel.pipeline().get("InjectorTA");
			PacketInterceptor interceptor = new PacketInterceptor(a.getName());
			if(channel.pipeline().names().contains("packet_handler"))
			channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
			else
				channel.pipeline().addBefore("encoder","InjectorTA", interceptor);
			return interceptor;
		} catch (Exception e) {
			if(channel.pipeline().names().contains("InjectorTA")) {
				PacketInterceptor interceptor = new PacketInterceptor(a.getName()); //add new hook
				channel.eventLoop().execute(new Runnable() {
					@Override
					public void run() {
						channel.pipeline().remove("InjectorTA");
						try {
							if(channel.pipeline().names().contains("packet_handler"))
						channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
						else
							channel.pipeline().addBefore("encoder","InjectorTA", interceptor);
						}catch(Exception e) {}
					}
				});
				return interceptor;
			}
			return null;
		}
	}

	public Channel get(Player player) {
		Channel channel = channelLookup.get(player.getName());
		if (channel == null)
			channelLookup.put(player.getName(), channel = (Channel) Ref.channel(Ref.network(Ref.playerCon(player))));
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
			for (Channel c : channelLookup.values())
				remove(c);
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
					player=((GameProfile) Ref.get(msg, "a")).getName();
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