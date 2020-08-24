package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class PacketHandler_New implements PacketHandler<Channel> {
	private static Class<?> login = Ref.nms("PacketLoginInStart");
	private Map<String, Channel> channelLookup = new HashMap<>();
	private List<?> networkManagers;
	private List<Channel> serverChannels = Lists.newArrayList();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private ChannelInitializer<Channel> beginInitProtocol, endInitProtocol;
	protected volatile boolean closed;

	public PacketHandler_New() {
		try {
			registerChannelHandler();
			registerPlayers();
		} catch (Exception ex) {
			new Tasker() {
				@Override
				public void run() {
					registerChannelHandler();
					registerPlayers();
				}
			}.runTask();
		}
	}

	private void createServerChannelHandler() {
		endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					synchronized (networkManagers) {
						if (!closed) {
							channel.eventLoop().submit(() -> {try {
								PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get("InjectorTheAPI");
								if (interceptor == null) {
									interceptor = new PacketInterceptor();
									channel.pipeline().addBefore("packet_handler", "InjectorTheAPI", interceptor);
								}
								return interceptor;
							} catch (Exception e) {
								return (PacketInterceptor) channel.pipeline().get("InjectorTheAPI");
							}});
						}
					}
				} catch (Exception e) {
				}
			}

		};
		beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(endInitProtocol);
			}

		};

		serverChannelHandler = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				Channel channel = (Channel) msg;
				channel.pipeline().addFirst(beginInitProtocol);
				ctx.fireChannelRead(msg);
			}

		};
	}

	private void registerChannelHandler() {
		Object serverConnection = Ref.invoke(Ref.invoke(Ref.handle(Ref.cast(Ref.craft("CraftServer"), Bukkit.getServer())),"getServer"), "getServerConnection");
		networkManagers = (List<?>) Ref.get(serverConnection, (Integer.parseInt(Ref.version().split("_")[1])>=16?"listeningChannels":"g"));
		createServerChannelHandler();
		for (Object item : networkManagers) {
			if (!ChannelFuture.class.isInstance(item))break;
			Channel serverChannel = ((ChannelFuture) item).channel();
			serverChannels.add(serverChannel);
			serverChannel.pipeline().addFirst(serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (serverChannelHandler == null)return;
		for (Channel serverChannel : serverChannels) {
			final ChannelPipeline pipeline = serverChannel.pipeline();
			serverChannel.eventLoop().execute(new Runnable() {
				@Override
				public void run() {
					try {
						pipeline.remove(serverChannelHandler);
					} catch (Exception e) {
					}
				}

			});
		}
	}

	private void registerPlayers() {
		for (Player player : Bukkit.getOnlinePlayers())
			injectPlayer(player);
	}
	
	public void sendPacket(Player player, Object packet) {
		sendPacket(getChannel(player), packet);
	}
	
	public void sendPacket(Channel channel, Object packet) {
		channel.pipeline().writeAndFlush(packet);
	}
	
	public void receivePacket(Player player, Object packet) {
		receivePacket(getChannel(player), packet);
	}

	public void receivePacket(Channel channel, Object packet) {
		channel.pipeline().context("encoder").fireChannelRead(packet);
	}

	public void injectPlayer(Player player) {
		injectChannelInternal(player,getChannel(player));
	}

	private PacketInterceptor injectChannelInternal(Player a,Channel channel) {
		try {
			PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get("InjectorTheAPI");
			if (interceptor == null) {
				interceptor = new PacketInterceptor();
				channel.pipeline().addBefore("packet_handler", "InjectorTheAPI", interceptor);
			}
			return interceptor;
		} catch (Exception e) {
			return (PacketInterceptor) channel.pipeline().get("InjectorTheAPI");
		}
	}

	public Channel getChannel(Player player) {
		Channel channel = channelLookup.get(player.getName());
		if (channel == null) {
			channelLookup.put(player.getName(), channel = (Channel) Ref.channel(Ref.network(Ref.playerCon(player))));
		}
		return channel;
	}

	public void uninjectPlayer(Player player) {
		uninjectChannel(getChannel(player));
	}

	public void uninjectChannel(final Channel channel) {
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				if(hasInjected(channel))
				channel.pipeline().remove("InjectorTheAPI");
			}
		});
	}

	public boolean hasInjected(Player player) {
		return hasInjected(getChannel(player));
	}

	public boolean hasInjected(Channel channel) {
		try {
		return channel.pipeline().get("InjectorTheAPI") != null;
		}catch(Exception e) {
			return false;
		}
	}

	public final void close() {
		if (!closed) {
			closed = true;
			for (Player player : Bukkit.getOnlinePlayers()) {
				uninjectPlayer(player);
			}
			unregisterChannelHandler();
		}
	}
	public final class PacketInterceptor extends ChannelDuplexHandler {
		private Player player;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			final Channel channel = ctx.channel();
			if (login.isInstance(msg)) {
				GameProfile profile = (GameProfile)Ref.get(msg,"a");
				channelLookup.put(profile.getName(), channel);
			}
			if(player==null) {
				if(channelLookup.containsValue(channel)) {
					for(String name : channelLookup.keySet()) {
						if(channelLookup.get(name).equals(channel)) {
					if(TheAPI.getPlayer(name)!=null && TheAPI.getPlayer(name).getName().equals(name))
						player=TheAPI.getPlayer(name);
						}
						break;
					}
				}
			}

			try {
				msg = PacketManager.call(player, msg, PacketType.PLAY_IN);
			} catch (Exception e) {
			}

			if (msg != null)
				super.channelRead(ctx, msg);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			if(player==null) {
				if(channelLookup.containsValue(ctx.channel())) {
					for(String name : channelLookup.keySet()) {
						if(channelLookup.get(name).equals(ctx.channel())) {
					if(TheAPI.getPlayer(name)!=null && TheAPI.getPlayer(name).getName().equals(name))
						player=TheAPI.getPlayer(name);
						}
						break;
					}
				}
			}
			try {
				msg = PacketManager.call(player, msg, PacketType.PLAY_OUT);
			} catch (Exception e) {
			}

			if (msg != null)
				super.write(ctx, msg, promise);
		}
	}
}