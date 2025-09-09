package click.mafia42.initializer.handler;

import click.mafia42.dto.ReissueTokenReq;
import click.mafia42.dto.SignInReq;
import click.mafia42.dto.SignUpReq;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.service.AuthService;
import click.mafia42.initializer.service.ConnectionService;
import click.mafia42.dto.ConsoleOutputReq;
import click.mafia42.payload.Payload;
import click.mafia42.database.ChannelManager;
import click.mafia42.util.ValidationUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static click.mafia42.payload.Commend.*;

@Sharable
public class CommendHandler extends SimpleChannelInboundHandler<Payload> {
    private static final Logger log = LoggerFactory.getLogger(CommendHandler.class);
    private final ConnectionService connectionService = new ConnectionService();
    private final AuthService authService = new AuthService();
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

        Payload response = getResponseByPayload(payload);
        ctx.channel().writeAndFlush(response);
    }

    private Payload getResponseByPayload(Payload payload) {
        if (payload.getCommend() == null) {
            ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.NOT_FOUND_COMMAND.getMessage());
            return new Payload(null, CONSOLE_OUTPUT, body);
        }

        return switch (payload.getCommend()) {
            case SIGN_UP -> authService.signUp(ValidationUtil.validationAndGet(payload.getBody(), SignUpReq.class));
            case SIGN_IN -> authService.signIn(ValidationUtil.validationAndGet(payload.getBody(), SignInReq.class));
            case REISSUE_TOKEN -> authService.reissueToken(ValidationUtil.validationAndGet(payload.getBody(), ReissueTokenReq.class));
            default -> {
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.UNSUPPORTED_COMMAND.getMessage());
                yield new Payload(null, CONSOLE_OUTPUT, body);
            }
        };
    }
}
