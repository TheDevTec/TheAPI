package me.devtec.theapi.bukkit.packetlistener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
public class PacketHandlerModern implements PacketHandler<Channel> {
	private static final Class<?> login = Ref.nmsOrOld("network.protocol.login.PacketLoginInStart",
			"PacketLoginInStart");
	private static final Class<?> postlogin = Ref.nmsOrOld("network.protocol.login.PacketLoginOutSuccess",
			"PacketLoginOutSuccess");
	static final Field f = Ref.field(PacketHandlerModern.login, "a");
	static final Field fPost = Ref.field(PacketHandlerModern.postlogin, "a");
	private final Map<String, Channel> channelLookup = new ConcurrentHashMap<>();
	private List<ChannelFuture> networkManagers;
	private final List<Channel> serverChannels = new ArrayList<>();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private Object serverConnection;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;
	protected volatile boolean closed;

	public PacketHandlerModern(boolean lateBind) {
		this.serverConnection = Ref.invoke(BukkitLoader.getNmsProvider().getMinecraftServer(), "getServerConnection");
		if (this.serverConnection == null) // modded server
			for (Field f : Ref.getAllFields(BukkitLoader.getNmsProvider().getMinecraftServer().getClass()))
				if (f.getType() == Ref.nmsOrOld("server.network.ServerConnection", "ServerConnection")) {
					this.serverConnection = Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), f);
					break;
				}
		if (this.serverConnection == null)
			return;
		if (lateBind)
			while (!(boolean) Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(),
					Ref.isOlderThan(9) ? "Q"
							: Ref.isOlderThan(11) ? "P"
									: Ref.isOlderThan(13) ? "Q"
											: Ref.isOlderThan(14) ? "P" : Ref.isOlderThan(17) ? "hasTicked" : "ah"))
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
		new Tasker() {
			@Override
			public void run() {
				PacketHandlerModern.this.registerChannelHandler();
				PacketHandlerModern.this.registerPlayers();
			}
		}.runLater(1);
	}

	private void createServerChannelHandler() {
		this.endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				try {
					synchronized (PacketHandlerModern.this.networkManagers) {
						if (!PacketHandlerModern.this.closed) {
							PacketInterceptor interceptor = new PacketInterceptor(null);
							channel.eventLoop().submit(() -> {
								if (channel.pipeline().names().contains("InjectorTA"))
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
		this.beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				channel.pipeline().addLast(PacketHandlerModern.this.endInitProtocol);
			}

		};
		this.serverChannelHandler = new ChannelInHandler();
	}

	@Sharable
	public class ChannelInHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			Channel channel = (Channel) msg;
			channel.pipeline().addFirst(PacketHandlerModern.this.beginInitProtocol);
			ctx.fireChannelRead(channel);
		}
	}

	private void registerChannelHandler() {
		if (Ref.isNewerThan(16))
			this.networkManagers = (List<ChannelFuture>) Ref.get(this.serverConnection, "f");
		else
			this.networkManagers = (List<ChannelFuture>) (Ref.get(this.serverConnection, "listeningChannels") != null
					? Ref.get(this.serverConnection, "listeningChannels")
					: Ref.get(this.serverConnection, "g"));
		if (this.networkManagers == null)
			for (Field f : Ref.getAllFields(Ref.nmsOrOld("server.network.ServerConnection", "ServerConnection")))
				if (java.util.List.class == f.getType()) {
					this.networkManagers = (List<ChannelFuture>) Ref.get(this.serverConnection, f);
					break;
				}
		if (this.networkManagers == null)
			return;
		if (this.networkManagers.isEmpty()) {
			this.networkManagers = (List<ChannelFuture>) (Ref.get(this.serverConnection, "f") != null
					? Ref.get(this.serverConnection, "f")
					: Ref.get(this.serverConnection, "listeningChannels"));
			if (this.networkManagers == null)
				for (Field f : Ref.getAllFields(Ref.nmsOrOld("server.network.ServerConnection", "ServerConnection")))
					if (List.class == f.getType()) {
						this.networkManagers = (List<ChannelFuture>) Ref.get(this.serverConnection, f);
						break;
					}
		}
		if (this.networkManagers == null)
			return;
		this.createServerChannelHandler();
		for (Object item : this.networkManagers) {
			if (!(item instanceof ChannelFuture))
				continue;
			Channel serverChannel = ((ChannelFuture) item).channel();
			this.serverChannels.add(serverChannel);
			serverChannel.pipeline().addFirst(this.serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (this.serverChannelHandler == null)
			return;
		for (Channel serverChannel : this.serverChannels)
			serverChannel.eventLoop().execute(() -> {
				try {
					serverChannel.pipeline().remove(this.serverChannelHandler);
				} catch (Exception err) {
				}
			});
		this.serverChannels.clear();
	}

	private void registerPlayers() {
		for (Player player : Bukkit.getOnlinePlayers())
			this.add(player);
	}

	@Override
	public void add(Player player) {
		this.injectChannelInternal(player, this.get(player));
	}

	private PacketInterceptor injectChannelInternal(Player a, Channel channel) {
		if (channel == null)
			return null;
		try {
			PacketInterceptor interceptor = new PacketInterceptor(a.getName());
			channel.eventLoop().submit(() -> {
				if (channel.pipeline().names().contains("InjectorTA"))
					channel.pipeline().remove("InjectorTA");
				channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
				return interceptor;
			});
			return interceptor;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Channel get(Player player) {
		Channel channel = this.channelLookup.get(player.getName());
		if (channel == null) {
			Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider()
					.getConnectionNetwork(BukkitLoader.getNmsProvider().getPlayerConnection(player)));
			if (get == null)
				return null;
			this.channelLookup.put(player.getName(), channel = (Channel) get);
		}
		return channel;
	}

	@Override
	public void remove(Channel channel) {
		if (channel == null)
			return;
		channel.eventLoop().execute(() -> {
			String owner = null;
			for (Entry<String, Channel> s : PacketHandlerModern.this.channelLookup.entrySet())
				if (s.getValue().equals(channel)) {
					owner = s.getKey();
					break;
				}
			PacketHandlerModern.this.channelLookup.remove(owner);
			if (channel.pipeline().names().contains("InjectorTA"))
				channel.pipeline().remove("InjectorTA");
		});
	}

	@Override
	public boolean has(Channel channel) {
		if (channel == null)
			return false;
		try {
			return channel.pipeline().get("InjectorTA") != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public final void close() {
		if (!this.closed) {
			this.closed = true;
			for (Channel channel : this.channelLookup.values())
				channel.eventLoop().execute(() -> {
					if (channel.pipeline().names().contains("InjectorTA"))
						channel.pipeline().remove("InjectorTA");
				});
			this.channelLookup.clear();
			this.unregisterChannelHandler();
		}
	}

	public final class PacketInterceptor extends ChannelDuplexHandler {
		String player;

		public PacketInterceptor(String player) {
			this.player = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			final Channel channel = ctx.channel();
			synchronized (msg) {
				if (msg.getClass() == PacketHandlerModern.login) {
					this.player = ((GameProfile) Ref.get(msg, PacketHandlerModern.f)).getName();
					PacketHandlerModern.this.channelLookup.put(this.player, channel);
				}
				try {
					msg = PacketManager.call(this.player, msg, channel, PacketType.PLAY_IN);
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
				if (this.player == null && msg.getClass() == PacketHandlerModern.postlogin) { // ProtocolLib cancelled
																								// packets
					this.player = ((GameProfile) Ref.get(msg, PacketHandlerModern.fPost)).getName();
					PacketHandlerModern.this.channelLookup.put(this.player, channel);
				}
				try {
					msg = PacketManager.call(this.player, msg, channel, PacketType.PLAY_OUT);
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
		if (channel == null || packet == null)
			return;
		channel.writeAndFlush(packet);
	}
}