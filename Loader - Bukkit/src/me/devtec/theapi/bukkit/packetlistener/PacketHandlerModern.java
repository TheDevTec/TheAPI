package me.devtec.theapi.bukkit.packetlistener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.theapi.bukkit.BukkitLoader;

@SuppressWarnings("unchecked")
public class PacketHandlerModern implements PacketHandler<Channel> {
	private static final Class<?> login = Ref.nms("network.protocol.login", "PacketLoginInStart");
	private static final Class<?> postlogin = Ref.nms("network.protocol.login", "PacketLoginOutSuccess");
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
		serverConnection = Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), Ref.nms("server.network", "ServerConnection"));
		if (serverConnection == null)
			return;
		if (lateBind) {
			String hasTicked = "ac";
			switch (Ref.serverVersionInt()) {
			case 8:
			case 11:
			case 12:
				hasTicked = "Q";
				break;
			case 9:
			case 10:
			case 13:
				hasTicked = "P";
				break;
			case 14:
			case 15:
			case 16:
				hasTicked = "hasTicked";
				break;
			case 17:
			case 18:
				hasTicked = "ah";
				break;
			case 19:
				hasTicked = "ac";
				break;
			}
			case 20:
				hasTicked = "ad";
				break;
			while (!(boolean) Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), hasTicked))
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
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
				} catch (Exception e) {
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
		if (Ref.isNewerThan(16))
			networkManagers = (List<ChannelFuture>) Ref.get(serverConnection, "f");
		else
			networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "listeningChannels") != null ? Ref.get(serverConnection, "listeningChannels") : Ref.get(serverConnection, "g"));
		if (networkManagers == null)
			for (Field f : Ref.getAllFields(Ref.nms("server.network", "ServerConnection")))
				if (java.util.List.class == f.getType()) {
					networkManagers = (List<ChannelFuture>) Ref.get(serverConnection, f);
					break;
				}
		if (networkManagers == null)
			return;
		if (networkManagers.isEmpty()) {
			networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "f") != null ? Ref.get(serverConnection, "f") : Ref.get(serverConnection, "listeningChannels"));
			if (networkManagers == null)
				for (Field f : Ref.getAllFields(Ref.nms("server.network", "ServerConnection")))
					if (List.class == f.getType()) {
						networkManagers = (List<ChannelFuture>) Ref.get(serverConnection, f);
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
				} catch (Exception err) {
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
		Future<Channel> future = getFuture(player);
		try {
			Channel result = future.get(3, TimeUnit.SECONDS);
			if (result != null)
				injectChannelInternal(player, result);
			else
				new Tasker() {
					@Override
					public void run() {
						try {
							Channel result = future.get();
							injectChannelInternal(player, result);
						} catch (Exception errr) {
						}
					}
				}.runTask();
		} catch (Exception err) {
			new Tasker() {
				@Override
				public void run() {
					try {
						Channel result = future.get();
						injectChannelInternal(player, result);
					} catch (Exception errr) {
					}
				}
			}.runTask();
		}
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
								Thread.sleep(25);
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
								Thread.sleep(25);
							} catch (InterruptedException e) {
								future.completeExceptionally(e);
								break;
							}
							Object get = BukkitLoader.getNmsProvider().getNetworkChannel(BukkitLoader.getNmsProvider().getConnectionNetwork(BukkitLoader.getNmsProvider().getPlayerConnection(player)));
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
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			final Channel channel = ctx.channel();
			Object packet = msg;
			synchronized (packet) {
				if (packet.getClass() == PacketHandlerModern.login) {
					player = Ref.isNewerThan(18) ? (String) Ref.get(packet, PacketHandlerModern.f) : ((GameProfile) Ref.get(packet, PacketHandlerModern.f)).getName();
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
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			final Channel channel = ctx.channel();
			Object packet = msg;
			synchronized (packet) {
				if (player == null && packet.getClass() == PacketHandlerModern.postlogin) { // ProtocolLib cancelled
					// packets
					player = Ref.isNewerThan(18) ? (String) Ref.get(packet, PacketHandlerModern.f) : ((GameProfile) Ref.get(packet, PacketHandlerModern.f)).getName();
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
	}

	@Override
	public void send(Channel channel, Object packet) {
		if (channel == null || packet == null)
			return;
		if (channel.isRegistered())
			channel.writeAndFlush(packet);
	}
}
