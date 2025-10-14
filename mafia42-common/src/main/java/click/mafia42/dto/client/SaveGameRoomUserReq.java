package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.JobType;

import java.util.UUID;

public record SaveGameRoomUserReq(
        int number,
        UUID id,
        String name,
        JobType jobType
) {
    public static SaveGameRoomUserReq from(GameRoomUser gameRoomUser) {
        return new SaveGameRoomUserReq(
                gameRoomUser.getNumber(),
                gameRoomUser.getUser().getId(),
                gameRoomUser.getUser().getNickname(),
                null
        );
    }

    public static SaveGameRoomUserReq from(GameRoomUser gameRoomUser, UUID currentUserId) {
        return new SaveGameRoomUserReq(
                gameRoomUser.getNumber(),
                gameRoomUser.getUser().getId(),
                gameRoomUser.getUser().getNickname(),
                getJobType(gameRoomUser, currentUserId)
        );
    }

    private static JobType getJobType(GameRoomUser gameRoomUser, UUID currentUserId) {
        if (gameRoomUser.isVisibleToUser(currentUserId) && gameRoomUser.getJob() != null) {
            return gameRoomUser.getJob().getJobType();
        } else {
            return null;
        }
    }
}
