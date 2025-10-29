package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;

import java.util.UUID;

public record SaveGameRoomUserReq(
        int number,
        UUID id,
        String name,
        JobType jobType,
        GameUserStatus gameUserStatus,
        long voteCount,
        boolean isBlackmailed,
        boolean isSeduced,
        boolean isAscended
) {
    public String fetchJobAlias() {
        return jobType != null ? jobType.getAlias() : "?";
    }

    public static SaveGameRoomUserReq from(GameRoomUser gameRoomUser, UUID currentUserId, long voteCount) {
        if (gameRoomUser == null) {
            return null;
        }

        return new SaveGameRoomUserReq(
                gameRoomUser.getNumber(),
                gameRoomUser.getUser().getId(),
                gameRoomUser.getUser().getNickname(),
                getJobType(gameRoomUser, currentUserId),
                gameRoomUser.getStatus(),
                voteCount,
                isBlackmailed(gameRoomUser, currentUserId),
                isSeduced(gameRoomUser, currentUserId),
                isAscended(gameRoomUser, currentUserId)
        );
    }

    private static boolean isBlackmailed(GameRoomUser gameRoomUser, UUID currentUserId) {
        if (gameRoomUser.getUser().getId().equals(currentUserId)) {
            return gameRoomUser.isBlackmailed();
        } else {
            return false;
        }
    }

    private static boolean isSeduced(GameRoomUser gameRoomUser, UUID currentUserId) {
        if (gameRoomUser.getUser().getId().equals(currentUserId)) {
            return gameRoomUser.isSeduced();
        } else {
            return false;
        }
    }

    private static boolean isAscended(GameRoomUser gameRoomUser, UUID currentUserId) {
        if (gameRoomUser.getUser().getId().equals(currentUserId) || gameRoomUser.getJob() != null && gameRoomUser.getJob().getJobType() == JobType.PSYCHIC) {
            return gameRoomUser.isAscended();
        } else {
            return false;
        }
    }

    private static JobType getJobType(GameRoomUser gameRoomUser, UUID currentUserId) {
        if (gameRoomUser.isVisibleToUser(currentUserId) && gameRoomUser.getJob() != null) {
            return gameRoomUser.getJob().getJobType();
        } else {
            return null;
        }
    }
}
