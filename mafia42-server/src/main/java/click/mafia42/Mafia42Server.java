package click.mafia42;

import click.mafia42.initializer.ServerSocketChannelInitializer;
import click.mafia42.database.ChannelManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Mafia42Server {
    private final ChannelManager channelManager;

    public Mafia42Server(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public void start() throws Exception {
        try(
                EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
                EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, NioIoHandler.newFactory())
        ) {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_LINGER, 0)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(new ServerSocketChannelInitializer(channelManager));

            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }
    }

}
