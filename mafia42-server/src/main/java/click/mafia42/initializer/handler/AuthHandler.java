package click.mafia42.initializer.handler;

import click.mafia42.payload.Payload;
import click.mafia42.player.ChannelManager;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Payload> {
    private final ChannelManager channelManager;

    public AuthHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
        // TODO 로그인 구현
        ctx.fireChannelRead(payload);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelManager.addChannel(ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelManager.removeChannel(ctx.channel());
        ctx.fireChannelInactive();
    }
}
