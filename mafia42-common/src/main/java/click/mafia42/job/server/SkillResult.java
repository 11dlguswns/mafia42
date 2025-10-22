package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoomUser;

import java.util.Set;

public record SkillResult(
        String message,
        Set<GameRoomUser> affectedUsers
) {
}
