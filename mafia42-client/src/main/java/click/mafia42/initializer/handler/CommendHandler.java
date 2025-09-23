package click.mafia42.initializer.handler;

import click.mafia42.dto.client.*;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.service.GameRoomService;
import click.mafia42.initializer.service.TokenService;
import click.mafia42.initializer.service.UserService;
import click.mafia42.payload.Payload;
import click.mafia42.initializer.service.OutputService;
import click.mafia42.util.ValidationUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Sharable
public class CommendHandler extends SimpleChannelInboundHandler<Payload> {
    private static final Logger log = LoggerFactory.getLogger(CommendHandler.class);
    private final OutputService outputService = new OutputService();
    private final TokenService tokenService = new TokenService();
    private final GameRoomService gameRoomService = new GameRoomService();
    private final UserService userService = new UserService();
    private CompletableFuture<Payload> payloadFuture = new CompletableFuture<>();

    public void setPayloadFuture(CompletableFuture<Payload> future) {
        this.payloadFuture = future;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
        switch (payload.getCommend()) {
            case NOTHING -> {
                // do nothing
            }
            case CONSOLE_OUTPUT ->
                    outputService.output(ValidationUtil.validationAndGet(payload.getBody(), ConsoleOutputReq.class));
            case SAVE_TOKEN ->
                tokenService.saveToken(ValidationUtil.validationAndGet(payload.getBody(), SaveTokenReq.class));
            case SAVE_GAME_ROOM ->
                gameRoomService.saveGameRoom(ValidationUtil.validationAndGet(payload.getBody(), SaveDetailGameRoomReq.class));
            case SAVE_GAME_ROOM_LIST ->
                    gameRoomService.saveGameRoomList(ValidationUtil.validationAndGet(payload.getBody(), SaveGameRoomListReq.class));
            case SAVE_USER_INFO_MYSELF ->
                userService.saveUserInfoMyself(ValidationUtil.validationAndGet(payload.getBody(), SaveUserInfoMyselfReq.class));
            case REMOVE_GAME_ROOM ->
                gameRoomService.removeGameRoom(ValidationUtil.validationAndGet(payload.getBody(), RemoveGameRoomReq.class));
            case SAVE_GAME_ROOM_LOBBY_MESSAGE ->
                gameRoomService.saveGameRoomLobbyMessage(ValidationUtil.validationAndGet(payload.getBody(), SaveGameRoomLobbyMessageReq.class));

            default -> throw new GlobalException(GlobalExceptionCode.UNSUPPORTED_COMMAND);
        }

        if (payloadFuture != null) {
            payloadFuture.complete(payload);
        }
    }
}
