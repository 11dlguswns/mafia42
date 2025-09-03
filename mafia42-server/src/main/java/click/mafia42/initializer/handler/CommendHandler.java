package click.mafia42.initializer.handler;

import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import click.mafia42.player.ChannelManager;
import click.mafia42.initializer.service.ConnectionService;
import click.mafia42.initializer.service.dto.dto.ConsoleOutputReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static click.mafia42.payload.Commend.CONSOLE_OUTPUT;
import static click.mafia42.payload.Commend.DISCONNECT;

@Sharable
public class CommendHandler extends SimpleChannelInboundHandler<Payload> {
    private static final Logger log = LoggerFactory.getLogger(CommendHandler.class);
    private final ConnectionService connectionService = new ConnectionService();
    private final ChannelManager channelManager;

    public CommendHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
        if (payload.getCommend() == DISCONNECT) {
            connectionService.disconnect(ctx);
            return;
        }

        Payload response = getResponseByCommend(payload.getCommend());
        ctx.channel().writeAndFlush(response);
    }

    private Payload getResponseByCommend(Commend commend) {
        if (commend == null) {
            return new Payload(null, CONSOLE_OUTPUT, new ConsoleOutputReq("존재하지 않는 커멘드"));
        } else {
            switch (commend) {
                default -> {
                    return new Payload(null, CONSOLE_OUTPUT, new ConsoleOutputReq("처리할 수 없는 커멘드"));
                }
            }
        }
    }
}
