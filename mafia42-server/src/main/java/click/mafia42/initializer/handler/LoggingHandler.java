package click.mafia42.initializer.handler;

import click.mafia42.player.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class LoggingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(LoggingHandler.class);
    private final ChannelManager channelManager;

    public LoggingHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{}포트에서 새로운 사용자가 접속했습니다 [현재 접속자 수 : {}]",
                channel.remoteAddress(),
                channelManager.getChannelsCount());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{}포트에서 사용자가 접속을 종료했습니다 [현재 접속자 수 : {}]",
                channel.remoteAddress(),
                channelManager.getChannelsCount());
        ctx.fireChannelInactive();
    }
}
