package click.mafia42.initializer;

import click.mafia42.initializer.handler.AuthHandler;
import click.mafia42.initializer.handler.CommendHandler;
import click.mafia42.initializer.handler.LoggingHandler;
import click.mafia42.payload.PayloadDecoder;
import click.mafia42.payload.PayloadEncoder;
import click.mafia42.database.ChannelManager;
import click.mafia42.util.CharsetUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class ServerSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelManager channelManager;

    public ServerSocketChannelInitializer(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new JsonObjectDecoder(65536))
                .addLast(new StringDecoder(CharsetUtil.DEFUALT_CHARSET))
                .addLast(new PayloadDecoder())
                .addLast(new AuthHandler(channelManager))
                .addLast(new LoggingHandler(channelManager))
                .addLast(new CommendHandler(channelManager))
                .addLast(new PayloadEncoder());
    }
}