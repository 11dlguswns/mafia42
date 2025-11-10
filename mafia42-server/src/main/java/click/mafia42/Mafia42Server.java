package click.mafia42;

import click.mafia42.database.GameRoomManager;
import click.mafia42.initializer.ServerSocketChannelInitializer;
import click.mafia42.database.ChannelManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Mafia42Server {
    private final ChannelManager channelManager;
    private final GameRoomManager gameRoomManager;

    public Mafia42Server(ChannelManager channelManager, GameRoomManager gameRoomManager) {
        this.channelManager = channelManager;
        this.gameRoomManager = gameRoomManager;
    }

    public void start() throws Exception {
        try(
                EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
                EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, NioIoHandler.newFactory())
        ) {
            createHealthCheckBootstrap(bossGroup, workerGroup).bind(8081).sync();

            ServerBootstrap mafia42Bootstrap = createMafia42Bootstrap(bossGroup, workerGroup);
            ChannelFuture mafia42ChannelFuture = mafia42Bootstrap.bind(8080).sync();

            mafia42ChannelFuture.channel().closeFuture().sync();
        }
    }

    private ServerBootstrap createMafia42Bootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        ServerBootstrap mafia42Bootstrap = new ServerBootstrap();
        mafia42Bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ServerSocketChannelInitializer(channelManager, gameRoomManager));

        return mafia42Bootstrap;
    }

    private ServerBootstrap createHealthCheckBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        ServerBootstrap healthCheckBootstrap = new ServerBootstrap();
        healthCheckBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
                                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });

        return healthCheckBootstrap;
    }

}
