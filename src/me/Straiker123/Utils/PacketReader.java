package me.Straiker123.Utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.Straiker123.LoaderClass;
import me.Straiker123.TheAPI;
import me.Straiker123.Events.PlayerReadPacketEvent;
import me.Straiker123.Events.PlayerReceivePacketEvent;

public class PacketReader implements Listener {
    @EventHandler
    public synchronized void onjoin(PlayerJoinEvent e){
    	new BukkitRunnable() {
			public void run() {
    	        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
    	            @Override
    	            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
    	            	PlayerReadPacketEvent es = new PlayerReadPacketEvent(e.getPlayer(),packet);
    	                if(es.isCancelled())return;
    	                super.channelRead(channelHandlerContext, es.getPacket());
    	            }

    	            @Override
    	            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
    	                PlayerReceivePacketEvent es = new PlayerReceivePacketEvent(e.getPlayer(),packet);
    	                if(es.isCancelled())return;
    	                super.write(channelHandlerContext, es.getPacket(), channelPromise);
    	            }


    	        };
    	        ChannelPipeline pipeline =TheAPI.getNMSAPI().getNMSPlayerAPI(e.getPlayer()).getPlayerConnection().getNetworkManager().getChannel().pipeline();
    	        pipeline.addBefore("packet_handler", e.getPlayer().getName(), channelDuplexHandler);
			}
		}.runTaskAsynchronously(LoaderClass.plugin);
    		}

    @EventHandler
    public synchronized void onleave(PlayerQuitEvent e){
    	new BukkitRunnable() {
			public void run() {
    	    	Channel channel =TheAPI.getNMSAPI().getNMSPlayerAPI(e.getPlayer()).getPlayerConnection().getNetworkManager().getChannel();
    	        channel.eventLoop().submit(() -> {
    	            channel.pipeline().remove(e.getPlayer().getName());
    	            return null;
    	        });
			}}.runTaskAsynchronously(LoaderClass.plugin);
    }
}
