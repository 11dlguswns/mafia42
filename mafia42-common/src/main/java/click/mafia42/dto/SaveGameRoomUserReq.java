package click.mafia42.dto;

import click.mafia42.entity.user.User;

import java.util.UUID;

public record SaveGameRoomUserReq(
        UUID id,
        String name
) {
    public static SaveGameRoomUserReq from(User user) {
        return new SaveGameRoomUserReq(
                user.getId(),
                user.getNickname()
        );
    }
}
