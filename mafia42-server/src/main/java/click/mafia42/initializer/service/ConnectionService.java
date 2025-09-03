package click.mafia42.initializer.service;

import io.netty.channel.ChannelHandlerContext;

public class ConnectionService {
    public void disconnect(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
