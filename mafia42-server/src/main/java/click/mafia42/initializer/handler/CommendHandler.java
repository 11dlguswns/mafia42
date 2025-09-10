package click.mafia42.initializer.handler;

import click.mafia42.database.GameRoomManager;
import click.mafia42.dto.*;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.service.AuthService;
import click.mafia42.initializer.service.ConnectionService;
import click.mafia42.initializer.service.GameRoomService;
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
    private final GameRoomService gameRoomService;
    private final AuthService authService = new AuthService();
    private final ChannelManager channelManager;

    public CommendHandler(ChannelManager channelManager, GameRoomManager gameRoomManager) {
        this.channelManager = channelManager;
        this.gameRoomService = new GameRoomService(gameRoomManager);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
        if (payload.getCommend() == DISCONNECT) {
            connectionService.disconnect(ctx);
            return;
        }

        Payload response = getResponseByPayload(payload, ctx);
        ctx.channel().writeAndFlush(response);
    }

    private Payload getResponseByPayload(Payload payload, ChannelHandlerContext ctx) {
        if (payload.getCommend() == null) {
            ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.NOT_FOUND_COMMAND.getMessage());
            return new Payload(null, CONSOLE_OUTPUT, body);
        }

        return switch (payload.getCommend()) {
            case SIGN_UP ->
                    authService.signUp(ValidationUtil.validationAndGet(payload.getBody(), SignUpReq.class));
            case SIGN_IN ->
                    authService.signIn(ValidationUtil.validationAndGet(payload.getBody(), SignInReq.class));
            case REISSUE_TOKEN ->
                    authService.reissueToken(ValidationUtil.validationAndGet(payload.getBody(), ReissueTokenReq.class));
            case CREATE_GAME_ROOM ->
                    gameRoomService.createGameRoom(ValidationUtil.validationAndGet(payload.getBody(), CreateGameRoomReq.class), ctx);
            default -> {
                ConsoleOutputReq body = new ConsoleOutputReq(GlobalExceptionCode.UNSUPPORTED_COMMAND.getMessage());
                yield new Payload(null, CONSOLE_OUTPUT, body);
            }
        };
    }
}
