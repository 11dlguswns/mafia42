package click.mafia42.dto.client;

import click.mafia42.entity.room.GameRoom;
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
        List<SaveGameMessageReq> chatMessages
) {
    public static SaveDetailGameRoomReq from(GameRoom gameRoom, UUID currentUserId) {
        return new SaveDetailGameRoomReq(
                gameRoom.getId(),
                gameRoom.getName(),
                gameRoom.getMaxPlayers(),
                gameRoom.getPlayers()
                        .stream()
                        .map(gameRoomUser -> SaveGameRoomUserReq.from(gameRoomUser, currentUserId))
                        .toList(),
                SaveGameRoomUserReq.from(gameRoom.getManager(), currentUserId),
                gameRoom.getGameType(),
                gameRoom.isStarted(),
                gameRoom.getStatus(),
                gameRoom.getEndTimeSecond(),
                gameRoom.getChatMessages()
        );
    }

    public Optional<SaveGameRoomUserReq> getGameRoomUser(UUID userId) {
        return users.stream().filter(user -> user.id().equals(userId)).findFirst();
    }
}
