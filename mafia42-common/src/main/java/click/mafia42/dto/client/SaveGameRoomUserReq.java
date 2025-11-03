package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.citizen.special.Detective;

import java.util.UUID;

public record SaveGameRoomUserReq(
        int number,
        UUID id,
        String name,
        JobType jobType,
        UUID targetId,
        GameUserStatus gameUserStatus,
        long voteCount,
        boolean isBlackmailed,
        boolean isSeduced,
        boolean isAscended
) {
    public String fetchJobAlias() {
        return jobType != null ? jobType.getAlias() : "?";
    }

    public static SaveGameRoomUserReq from(GameRoomUser gameRoomUser, GameRoomUser currentUser, long voteCount) {
        if (gameRoomUser == null) {
            return null;
        }

        getTargetId(gameRoomUser, currentUser);

        return new SaveGameRoomUserReq(
                gameRoomUser.getNumber(),
                gameRoomUser.getUser().getId(),
                gameRoomUser.getUser().getNickname(),
                getJobType(gameRoomUser, currentUser.getUser().getId()),
                getTargetId(gameRoomUser, currentUser),
                gameRoomUser.getStatus(),
                voteCount,
                isBlackmailed(gameRoomUser, currentUser),
                isSeduced(gameRoomUser, currentUser),
                isAscended(gameRoomUser, currentUser)
        );
    }

    private static UUID getTargetId(GameRoomUser gameRoomUser, GameRoomUser currentUser) {
        if (gameRoomUser.getJob() == null) {
            return null;
        }

        if (gameRoomUser.getJob() instanceof SkillJob skillJob) {
            if (skillJob.getTarget() == null) {
                return null;
            }

            boolean isCurrentUser = gameRoomUser.equals(currentUser);
            boolean detectiveTarget = currentUser.getJob() instanceof Detective detective &&
                    gameRoomUser.equals(detective.getTarget());
            boolean isSameSharedActiveType = isSameSharedActiveType(currentUser, skillJob);

            if (isCurrentUser || detectiveTarget || isSameSharedActiveType) {
                return skillJob.getTarget().getUser().getId();
            }
        }

        return null;
    }

    private static boolean isSameSharedActiveType(GameRoomUser currentUser, SkillJob skillJob) {
        if (skillJob.getSharedActiveType() == SharedActiveType.NONE) {
            return false;
        }

        if (currentUser.getJob() instanceof SkillJob currentSkillJob) {
            return skillJob.getSharedActiveType() == currentSkillJob.getSharedActiveType();
        }

        return false;
    }

    private static boolean isBlackmailed(GameRoomUser gameRoomUser, GameRoomUser currentUser) {
        if (gameRoomUser.equals(currentUser)) {
            return gameRoomUser.isBlackmailed();
        } else {
            return false;
        }
    }

    private static boolean isSeduced(GameRoomUser gameRoomUser, GameRoomUser currentUser) {
        if (gameRoomUser.equals(currentUser)) {
            return gameRoomUser.isSeduced();
        } else {
            return false;
        }
    }

    private static boolean isAscended(GameRoomUser gameRoomUser, GameRoomUser currentUser) {
        if (gameRoomUser.equals(currentUser) || gameRoomUser.getJob() != null && gameRoomUser.getJob().getJobType() == JobType.PSYCHIC) {
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
