package click.mafia42.initializer.service;

import click.mafia42.dto.client.SaveUserInfoMyselfReq;
import click.mafia42.dto.server.FetchUserInfoMyselfReq;
import click.mafia42.entity.user.User;
import click.mafia42.initializer.handler.AuthHandler;
import click.mafia42.payload.Commend;
import click.mafia42.payload.Payload;
import io.netty.channel.ChannelHandlerContext;

public class UserService {
    public Payload fetchUserInfoMyself(FetchUserInfoMyselfReq request, ChannelHandlerContext ctx) {
        User user = ctx.channel().attr(AuthHandler.USER).get();
        return new Payload(Commend.SAVE_USER_INFO_MYSELF, new SaveUserInfoMyselfReq(user.getId(), user.getNickname()));
    }
}
