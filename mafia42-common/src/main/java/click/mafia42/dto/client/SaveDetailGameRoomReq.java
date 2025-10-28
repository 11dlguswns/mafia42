package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record SaveDetailGameRoomReq(
        long id,
        String name,
        int maxPlayers,
        List<SaveGameRoomUserReq> users,
        SaveGameRoomUserReq manager,
        GameType gameType,
        boolean isStarted,
        GameStatus gameStatus,
        long endTimeSecond,
        int day,
        SaveGameRoomUserReq mostVotedUser
) {
    public static SaveDetailGameRoomReq from(GameRoom gameRoom, UUID currentUserId) {
        GameRoomUser mostVotedUser;
        if (gameRoom.getStatus() == GameStatus.JUDGEMENT) {
            mostVotedUser = gameRoom.getMostVotedUser().orElse(null);
        } else {
            mostVotedUser = null;
        }

        return new SaveDetailGameRoomReq(
                gameRoom.getId(),
                gameRoom.getName(),
                gameRoom.getMaxPlayers(),
                gameRoom.getPlayers()
                        .stream()
                        .map(gameRoomUser -> SaveGameRoomUserReq.from(
                                gameRoomUser,
                                currentUserId,
                                gameRoom.getUserVoteCount(gameRoomUser)))
                        .toList(),
                SaveGameRoomUserReq.from(
                        gameRoom.getManager(),
                        currentUserId,
                        gameRoom.getUserVoteCount(gameRoom.getManager())),
                gameRoom.getGameType(),
                gameRoom.isStarted(),
                gameRoom.getStatus(),
                gameRoom.getEndTimeSecond(),
                gameRoom.getDay(),
                SaveGameRoomUserReq.from(mostVotedUser, currentUserId, gameRoom.getUserVoteCount(mostVotedUser))
        );
    }

    public Optional<SaveGameRoomUserReq> getGameRoomUser(UUID userId) {
        return users.stream().filter(user -> user.id().equals(userId)).findFirst();
    }
}
