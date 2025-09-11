package click.mafia42.dto;

import java.util.List;

public record SaveGameRoomListReq(
    List<SaveGameRoomReq> gameRooms
) {
}
