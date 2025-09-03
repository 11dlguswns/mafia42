package click.mafia42;

import click.mafia42.initializer.ClientSocketChannelInitializer;
import click.mafia42.payload.Payload;
import click.mafia42.util.MapperUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static click.mafia42.payload.Commend.DISCONNECT;

public class Mafia42Client {
    private static final Logger log = LoggerFactory.getLogger(Mafia42Client.class);
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));;

    public void start() throws Exception {
        MultiThreadIoEventLoopGroup eventLoopGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress("localhost", 8080)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.SO_LINGER, 0)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientSocketChannelInitializer());

            Channel channel = bootstrap.connect().sync().channel();
            while (true) {
                String line = br.readLine();
                Payload payload = JsonToPayload(line);

                if (payload == null) {
                    log.error("잘못된 형식의 입력 값 입니다.");
                    continue;
                }

                if (payload.getCommend() == DISCONNECT) {
                    channel.writeAndFlush(new Payload(payload.getToken(), DISCONNECT, payload.getBody()))
                            .addListener(ChannelFutureListener.CLOSE)
                            .sync();
                    break;
                }

                channel.writeAndFlush(payload);
            }
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }

    }

    private Payload JsonToPayload(String line) {
        try {
            return MapperUtil.readValueOrThrow(line, Payload.class);
        } catch (Exception e) {
            return null;
        }
    }
}
