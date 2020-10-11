package me.DevTec.TheAPI.Utils.PacketListenerAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
import me.DevTec.TheAPI.Utils.NMS.NMSPlayer;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class PacketHandler_New implements PacketHandler<Channel> {
	private static Class<?> login = Ref.nms("PacketLoginInStart");
	private Map<String, Channel> channelLookup = new HashMap<>();
	private List<?> networkManagers;
	private List<Channel> serverChannels = new ArrayList<>();
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
		for (Player player : TheAPI.getOnlinePlayers())
			injectPlayer(player);
	}

	public void injectPlayer(Player player) {
		if(getChannel(player)==null)getChannel(player);
		injectChannelInternal(player, getChannel(player));
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
			try {
			return (PacketInterceptor) channel.pipeline().get("InjectorTheAPI");
			}catch(Exception err) {
				PacketInterceptor interceptor = new PacketInterceptor();
				channel.pipeline().addBefore("packet_handler", "InjectorTheAPI", interceptor);
				return interceptor;
			}
		}
	}

	public Channel getChannel(Player player) {
		Channel channel = channelLookup.getOrDefault(player.getName(), null);
		if (channel == null) {
			channel = (Channel)new NMSPlayer(player).getPlayerConnection().getNetworkManager().getChannel();
			channelLookup.put(player.getName(), channel);
		}
		return channel;
	}

	public void uninjectPlayer(Player player) {
		uninjectChannel(getChannel(player));
	}

	public void uninjectChannel(Channel channel) {
		if(channel==null)return;
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
			if(channel==null)return false;
			return channel.pipeline().get("InjectorTheAPI") != null;
		}catch(Exception e) {
			return false;
		}
	}

	public final void close() {
		if (!closed) {
			closed = true;
			for (Player player : TheAPI.getOnlinePlayers())
				uninjectPlayer(player);
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
							if(TheAPI.getPlayerOrNull(name)!=null)
								player=TheAPI.getPlayerOrNull(name);
							break;
							}
					}
				}
			}else {
				if(!player.isOnline())
					if(TheAPI.getPlayerOrNull(player.getName())!=null)
						player=TheAPI.getPlayerOrNull(player.getName());
			}
			synchronized(msg) {
			try {
				msg = PacketManager.call(player, msg, ctx.channel(), PacketType.PLAY_IN);
			} catch (Exception e) {
				try {
					msg = PacketManager.call(player, msg, ctx.channel(), PacketType.PLAY_IN);
				} catch (Exception er) {
				}
			}
			if (msg != null)
				super.channelRead(ctx, msg);
			}
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			if(player==null) {
				if(channelLookup.containsValue(ctx.channel())) {
					for(String name : channelLookup.keySet()) {
						if(channelLookup.get(name).equals(ctx.channel())) {
					if(TheAPI.getPlayerOrNull(name)!=null)
						player=TheAPI.getPlayerOrNull(name);
						break;
						}
					}
				}
			}else {
				if(!player.isOnline())
					if(TheAPI.getPlayerOrNull(player.getName())!=null)
						player=TheAPI.getPlayerOrNull(player.getName());
			}
			synchronized(msg) {
			try {
				msg = PacketManager.call(player, msg, ctx.channel(), PacketType.PLAY_OUT);
			} catch (Exception e) {
				try {
					msg = PacketManager.call(player, msg, ctx.channel(), PacketType.PLAY_OUT);
				} catch (Exception er) {
				}
			}
			if (msg != null)
				super.write(ctx, msg, promise);
			}
		}
	}
}