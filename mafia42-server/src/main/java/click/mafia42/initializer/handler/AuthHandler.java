package click.mafia42.initializer.handler;

import click.mafia42.database.ChannelManager;
import click.mafia42.database.GameRoomManager;
import click.mafia42.database.transaction.TransactionManager;
import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.initializer.service.GameRoomService;
import click.mafia42.payload.Payload;
import click.mafia42.security.service.JwtService;
import click.mafia42.security.util.JwtUtil;
import click.mafia42.util.StringUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

@Sharable
public class AuthHandler extends SimpleChannelInboundHandler<Payload> {
    private final ChannelManager channelManager;
    private final GameRoomManager gameRoomManager;
    private final GameRoomService gameRoomService;
    public static final AttributeKey<User> USER = AttributeKey.valueOf("USER");
    private final JwtService jwtService = new JwtService();

    public AuthHandler(ChannelManager channelManager, GameRoomManager gameRoomManager) {
        this.channelManager = channelManager;
        this.gameRoomManager = gameRoomManager;
        this.gameRoomService = new GameRoomService(gameRoomManager, channelManager);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Payload payload) throws Exception {
        TransactionManager.executeInTransaction(connection -> {
            String accessToken = JwtUtil.extractBearerToken(payload.getToken());

            if (StringUtil.hasText(accessToken) && jwtService.validateAccessToken(accessToken)) {
                setAuthenticationToAttribute(ctx, accessToken);
            }

            return null;
        });

        ctx.fireChannelRead(payload);
    }

    private void setAuthenticationToAttribute(ChannelHandlerContext ctx, String accessToken) {
        User user = jwtService.getUserByAccessToken(accessToken);
        ctx.channel().attr(USER).set(user);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelManager.addChannel(ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(USER).get();
        gameRoomManager.findGameRoomByUser(user)
                .filter(gameRoom -> !gameRoom.isStarted())
                .ifPresent(gameRoom -> gameRoomService.exitGameRoom(gameRoom, user));

        channelManager.removeChannel(ctx.channel());
        ctx.fireChannelInactive();
    }
}
