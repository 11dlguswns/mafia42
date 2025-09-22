package click.mafia42.dto.client;

import java.util.List;

public record SaveGameRoomListReq(
    List<SaveGameRoomReq> gameRooms
) {
}
