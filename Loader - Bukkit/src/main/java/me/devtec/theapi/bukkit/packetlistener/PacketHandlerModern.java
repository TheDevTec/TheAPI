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
	private static final Class<?> login;
	private static final Class<?> postlogin;
	private static final Field name;
	private static final Field gameprofile;
	private final Map<String, Channel> channelLookup = new ConcurrentHashMap<>();
	private List<ChannelFuture> networkManagers;
	private final List<Channel> serverChannels = new ArrayList<>();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private final Object serverConnection;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;
	protected volatile boolean closed;

	static {
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE) {
			login = Ref.nms("network.protocol.login", "ServerboundHelloPacket");
			name = Ref.field(login, "name");
			postlogin = Ref.nms("network.protocol.login", "ClientboundGameProfilePacket");
			gameprofile = Ref.field(postlogin, "gameProfile");
		} else {
			login = Ref.nms("network.protocol.login", "PacketLoginInStart");
			postlogin = Ref.nms("network.protocol.login", "PacketLoginOutSuccess");
			if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 4) {
				name = Ref.field(login, "b");
				gameprofile = Ref.field(postlogin, "b");
			} else {
				name = Ref.field(login, "a");
				gameprofile = Ref.field(postlogin, "a");
			}
		}
	}

	public PacketHandlerModern(boolean lateBind) {
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE)
			serverConnection = Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), Ref.nms("server.network", "ServerConnectionListener"));
		else
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
			case 20:
				switch (Ref.serverVersionRelease()) {
				case 1:
				case 2:
					hasTicked = "ad";
					break;
				case 3:
					hasTicked = "ah";
					break;
				case 4:
					hasTicked = "ag";
					break;
				}
				break;
			}
			if (BukkitLoader.NO_OBFUSCATED_NMS_MODE)
				hasTicked = "isReady";
			while (!(boolean) Ref.get(BukkitLoader.getNmsProvider().getMinecraftServer(), hasTicked))
				try {
					Thread.sleep(20);
				} catch (Exception ignored) {
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
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE)
			networkManagers = (List<ChannelFuture>) Ref.get(serverConnection, "channels");
		else if (Ref.isNewerThan(16))
			networkManagers = (List<ChannelFuture>) Ref.get(serverConnection, "f");
		else
			networkManagers = (List<ChannelFuture>) (Ref.get(serverConnection, "listeningChannels") != null ? Ref.get(serverConnection, "listeningChannels") : Ref.get(serverConnection, "g"));
		if (networkManagers == null)
			for (Field f : Ref.getAllFields(Ref.nms("server.network", "ServerConnection")))
				if (List.class == f.getType()) {
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
						} catch (Exception ignored) {
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
					} catch (Exception ignored) {
					}
				}
			}.runTask();
		}
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
				try {
					channel.eventLoop().execute(() -> {
						if (channel.pipeline().names().contains("InjectorTA"))
							channel.pipeline().remove("InjectorTA");
					});
				} catch (IllegalStateException ignored) {

				}
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
			if (packet.getClass() == PacketHandlerModern.login) {
				player = Ref.isNewerThan(18) ? (String) Ref.get(packet, PacketHandlerModern.name) : ((GameProfile) Ref.get(packet, PacketHandlerModern.name)).getName();
				channelLookup.put(player, channel);
			}
			Object modifiedPacket;
			try {
				modifiedPacket = PacketManager.call(player, packet, channel, PacketType.PLAY_IN);
			} catch (Exception e) {
				e.printStackTrace();
				modifiedPacket = packet;
			}
			if (modifiedPacket != null)
				super.channelRead(ctx, modifiedPacket);
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			final Channel channel = ctx.channel();
			if (player == null && packet.getClass() == PacketHandlerModern.postlogin) { // ProtocolLib cancelled
				// packets
				player = ((GameProfile) Ref.get(packet, PacketHandlerModern.gameprofile)).getName();
				channelLookup.put(player, channel);
			}
			Object modifiedPacket;
			try {
				modifiedPacket = PacketManager.call(player, packet, channel, PacketType.PLAY_OUT);
			} catch (Exception e) {
				e.printStackTrace();
				modifiedPacket = packet;
			}
			if (modifiedPacket != null)
				super.write(ctx, modifiedPacket, promise);
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
