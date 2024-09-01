package me.devtec.theapi.bukkit.packetlistener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.theapi.bukkit.BukkitLoader;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelHandler.Sharable;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelInitializer;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public class PacketHandlerLegacy implements PacketHandler<Channel> {
	private static final Class<?> login = Ref.nms("", "PacketLoginInStart");
	private static final Class<?> postlogin = Ref.nms("", "PacketLoginOutSuccess");
	static final Field f = Ref.field(PacketHandlerLegacy.login, "a");
	static final Field fPost = Ref.field(PacketHandlerLegacy.postlogin, "a");
	private final Map<String, Channel> channelLookup = new ConcurrentHashMap<>();
	private List<?> networkManagers;
	private final List<Channel> serverChannels = new ArrayList<>();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private final Object serverConnection;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;
	protected volatile boolean closed;

	public PacketHandlerLegacy(boolean lateBind) {
		serverConnection = Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), Ref.nms("", "ServerConnection"));
		if (serverConnection == null)
			return;
		if (lateBind)
			while (!(boolean) Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), "Q"))
				try {
					Thread.sleep(20);
				} catch (Exception ignored) {
				}
		new Tasker() {
			@Override
			public void run() {
				PacketHandlerLegacy.this.registerChannelHandler();
				PacketHandlerLegacy.this.registerPlayers();
			}
		}.runLater(1);
	}

	private void createServerChannelHandler() {
		endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				try {
					synchronized (networkManagers) {
						if (!closed) {
							PacketInterceptor interceptor = new PacketInterceptor(null);
							channel.eventLoop().submit(() -> {
								if (channel.pipeline().names().contains("InjectorTA"))
									channel.pipeline().remove("InjectorTA");
								channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
								return interceptor;
							});
						}
					}
				} catch (Exception ignored) {
				}
			}

		};
		beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) {
				channel.pipeline().addLast(endInitProtocol);
			}

		};
		serverChannelHandler = new ChannelInHandler();
	}

	@Sharable
	public class ChannelInHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			Channel channel = (Channel) msg;
			channel.pipeline().addFirst(beginInitProtocol);
			ctx.fireChannelRead(channel);
		}
	}

	private void registerChannelHandler() {
		networkManagers = (List<?>) (Ref.get(serverConnection, "e") != null ? Ref.get(serverConnection, "e") : Ref.get(serverConnection, "f"));
		if (networkManagers == null)
			for (Field f : Ref.getAllFields(Ref.nms("", "ServerConnection")))
				if (List.class == f.getType()) {
					networkManagers = (List<?>) Ref.get(serverConnection, f);
					break;
				}
		if (networkManagers == null)
			return;
		if (networkManagers.isEmpty()) {
			networkManagers = (List<?>) (Ref.get(serverConnection, "f") != null ? Ref.get(serverConnection, "f") : Ref.get(serverConnection, "e"));
			if (networkManagers == null)
				for (Field f : Ref.getAllFields(Ref.nms("", "ServerConnection")))
					if (List.class == f.getType()) {
						networkManagers = (List<?>) Ref.get(serverConnection, f);
						break;
					}
		}
		if (networkManagers == null)
			return;
		createServerChannelHandler();
		for (Object item : networkManagers) {
			if (!(item instanceof ChannelFuture))
				continue;
			Channel serverChannel = ((ChannelFuture) item).channel();
			serverChannels.add(serverChannel);
			serverChannel.pipeline().addFirst(serverChannelHandler);
		}
	}

	private void unregisterChannelHandler() {
		if (serverChannelHandler == null)
			return;
		for (Channel serverChannel : serverChannels)
			serverChannel.eventLoop().execute(() -> {
				try {
					serverChannel.pipeline().remove(serverChannelHandler);
				} catch (Exception ignored) {
				}
			});
		serverChannels.clear();
	}

	private void registerPlayers() {
		for (Player player : Bukkit.getOnlinePlayers())
			add(player);
	}

	@Override
	public void add(Player player) {
		injectChannelInternal(player, get(player));
	}

	private void injectChannelInternal(Player a, Channel channel) {
		if (channel == null)
			return;
		try {
			PacketInterceptor interceptor = new PacketInterceptor(a.getName());
			channel.eventLoop().submit(() -> {
				if (channel.pipeline().names().contains("InjectorTA"))
					channel.pipeline().remove("InjectorTA");
				channel.pipeline().addBefore("packet_handler", "InjectorTA", interceptor);
				return interceptor;
			});
		} catch (Exception ignored) {
		}
	}

	@Override
	public Future<Channel> getFuture(Player player) {
		Channel channel = channelLookup.get(player.getName());
		if (channel == null) {
			Object connection = BukkitLoader.getNmsProvider().getPlayerConnection(player); // Still connecting
			if (connection == null) {
				CompletableFuture<Channel> future = new CompletableFuture<>();
				new Tasker() {

					@Override
					public void run() {
						while (API.isEnabled()) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								future.completeExceptionally(e);
								break;
							}
							Object connection = BukkitLoader.getNmsProvider().getPlayerConnection(player);
							if (connection == null)
								continue;
							Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider().getConnectionNetwork(connection));
							if (get == null)
								continue;
							channelLookup.put(player.getName(), (Channel) get);
							future.complete((Channel) get);
							break;
						}
					}
				}.runTask();
				return future;
			}
			Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider().getConnectionNetwork(connection)); // Channel still not set
			if (get == null) {
				CompletableFuture<Channel> future = new CompletableFuture<>();
				new Tasker() {

					@Override
					public void run() {
						while (API.isEnabled()) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								future.completeExceptionally(e);
								break;
							}
							Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider().getConnectionNetwork(connection));
							if (get == null)
								continue;
							channelLookup.put(player.getName(), (Channel) get);
							future.complete((Channel) get);
							break;
						}
					}
				}.runTask();
				return future;
			}
			channelLookup.put(player.getName(), (Channel) get);
			return CompletableFuture.completedFuture((Channel) get);
		}
		return CompletableFuture.completedFuture(channel);
	}

	@Override
	public void remove(Channel channel) {
		if (channel == null)
			return;
		channel.eventLoop().execute(() -> {
			String owner = null;
			for (Entry<String, Channel> s : PacketHandlerLegacy.this.channelLookup.entrySet())
				if (s.getValue().equals(channel)) {
					owner = s.getKey();
					break;
				}
			PacketHandlerLegacy.this.channelLookup.remove(owner);
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
		if (!closed) {
			closed = true;
			for (Channel channel : channelLookup.values())
				channel.eventLoop().execute(() -> {
					if (channel.pipeline().names().contains("InjectorTA"))
						channel.pipeline().remove("InjectorTA");
				});
			channelLookup.clear();
			unregisterChannelHandler();
		}
	}

	public final class PacketInterceptor extends ChannelDuplexHandler {
		String player;

		public PacketInterceptor(String player) {
			this.player = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			final Channel channel = ctx.channel();
				if (packet.getClass() == PacketHandlerLegacy.login) {
					player = ((GameProfile) Ref.get(packet, PacketHandlerLegacy.f)).getName();
					channelLookup.put(player, channel);
				}
				try {
					packet = PacketManager.call(player, packet, channel, PacketType.PLAY_IN);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (packet != null)
					super.channelRead(ctx, packet);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			final Channel channel = ctx.channel();
				if (player == null && packet.getClass() == PacketHandlerLegacy.postlogin) { // ProtocolLib cancelled
					// packets
					player = ((GameProfile) Ref.get(packet, PacketHandlerLegacy.fPost)).getName();
					channelLookup.put(player, channel);
				}
				try {
					packet = PacketManager.call(player, packet, channel, PacketType.PLAY_OUT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (packet != null)
					super.write(ctx, packet, promise);
		}
	}

	@Override
	public void send(Channel channel, Object packet) {
		if (channel == null || packet == null)
			return;
		channel.writeAndFlush(packet);
	}
}